package no.daffern.vehicle.utils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.ArrayList;

/**
 * Created by Daffern on 04.01.2017.
 */
public class ContactListenerMultiplexer implements ContactListener{

    private ArrayList<ContactListener> contactListeners = new ArrayList<>();

    public void addContactListener(ContactListener contactListener){
        contactListeners.add(contactListener);
    }
    public void removeContactListener(ContactListener contactListener){
    	//search from the end (faster for simple add and remove)
    	for (int i = contactListeners.size() - 1 ; i >= 0 ; i--){
    		if (contactListeners.get(i) == contactListener){
    			contactListeners.remove(i);
		    }
	    }
    }

    @Override
    public void beginContact(Contact contact) {
        for (ContactListener contactListener : contactListeners)
            contactListener.beginContact(contact);
    }

    @Override
    public void endContact(Contact contact) {
        for (ContactListener contactListener : contactListeners)
            contactListener.endContact(contact);
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        for (ContactListener contactListener : contactListeners)
            contactListener.preSolve(contact, oldManifold);
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        for (ContactListener contactListener : contactListeners)
            contactListener.postSolve(contact, impulse);
    }
}
