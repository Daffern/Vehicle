package no.daffern.vehicle.network;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import no.daffern.vehicle.common.SystemInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Daffern on 09.12.2016.
 */
public class MyClient extends Client implements SystemInterface {

    private List<Listener> listeners = new ArrayList<>(20);
    private Object listenerLock = new Object();

    private Queue<Connection> connectedQueue = new ConcurrentLinkedQueue<>();
    private Queue<Connection> disconnectQueue = new ConcurrentLinkedQueue<>();
    private Queue<ReceivedHolder> receiveQueue = new ConcurrentLinkedQueue<>();
    private Queue<Connection> idleQueue = new ConcurrentLinkedQueue<>();

	private int totalBytesSent = 0;
	private int bytesOutCounter = 0;
	private int bpsOutLastFrame = 0;


    private Listener listener = new Listener() {
        public void connected(Connection connection) {
            connectedQueue.add(connection);
        }

        /** Called when the remote end is no longer connected. There is no guarantee as to what thread will invoke this method. */
        public void disconnected(Connection connection) {
            disconnectQueue.add(connection);
        }

        /** Called when an object has been received from the remote end of the connection. This will be invoked on the same thread as
         * {@link Client#update(int)} and {@link Server#update(int)}. This method should not block for long periods as other network
         * activity will not be processed until it returns. */
        public void received(Connection connection, Object object) {
            receiveQueue.add(new ReceivedHolder(connection, object));

        }

        /** Called when the connection is below the {@link Connection#setIdleThreshold(float) idle threshold}. */
        public void idle(Connection connection) {
            idleQueue.add(connection);
        }
    };

    public MyClient(){
        super();
	    super.addListener(listener);
    }

    @Override
    public int sendTCP(Object o){
		int sent = super.sendTCP(o);
		bytesOutCounter += sent;
		totalBytesSent += sent;
		return sent;
    }

    @Override
    public int sendUDP(Object o){
	    int sent = super.sendUDP(o);
	    bytesOutCounter += sent;
	    totalBytesSent += sent;
	    return sent;
    }

    public void register(Class... classes){
        Kryo kryo = getKryo();
        for (Class c : classes){
            kryo.register(c);
        }
    }

	@Override
	public void addListener(Listener listener) {
		synchronized (listenerLock) {
			listeners.add(listener);
		}
	}

	public void dispatchQueues() {
		synchronized (listenerLock) {

			while (!connectedQueue.isEmpty()) {
				Connection connection = connectedQueue.poll();
				for (Listener listener : listeners) {
					listener.connected(connection);
				}
			}

			while (!disconnectQueue.isEmpty()) {
				Connection connection = disconnectQueue.poll();
				for (Listener listener : listeners) {
					listener.disconnected(connection);
				}
			}

			while (!receiveQueue.isEmpty()) {
				ReceivedHolder receivedHolder = receiveQueue.poll();
				for (Listener listener : listeners) {
					listener.received(receivedHolder.connection, receivedHolder.object);
				}
			}

			while (!idleQueue.isEmpty()) {
				Connection connection = idleQueue.poll();
				for (Listener listener : listeners) {
					listener.idle(connection);
				}

			}
		}
	}

    @Override
    public void preStep() {

    }

    @Override
    public void postStep() {
		bpsOutLastFrame = bytesOutCounter;
		bytesOutCounter = 0;
    }

    @Override
    public void render(Batch batch, float delta) {

    }

    public int getBpsOutLastFrame(){
    	return bpsOutLastFrame;
    }

    private class ReceivedHolder {
        Connection connection;
        Object object;

        ReceivedHolder(Connection connection, Object object) {
            this.connection = connection;
            this.object = object;
        }
    }
}
