package no.daffern.vehicle.utils;

import com.badlogic.gdx.InputProcessor;

import java.util.Collections;
import java.util.Vector;

/**
 * Created by Daff on 13.11.2016.
 */
public class PriorityInputHandler implements InputProcessor {

    private static PriorityInputHandler singleton;

    public static PriorityInputHandler getInstance() {
        if (singleton == null)
            singleton = new PriorityInputHandler();
        return singleton;
    }

    private Vector<PriorityInputProcessor> inputProcessors = new Vector(3,1);


    public void addInputProcessor(InputProcessor inputProcessor, int priority) {

    	inputProcessors.add(new PriorityInputProcessor(inputProcessor, priority));

	    Collections.sort(inputProcessors);
    }
    public void removeInputProcessor(int priority) {
    	for (int i = 0 ; i < inputProcessors.size() ; i++){
		    if (inputProcessors.get(i).priority == priority){
		    	inputProcessors.remove(i);
		    	break;
		    }
	    }
    }


    @Override
    public boolean keyDown(int keycode) {
        for (PriorityInputProcessor inputProcessor : inputProcessors){
        	if (inputProcessor.inputProcessor.keyDown(keycode))
        		return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
	    for (PriorityInputProcessor inputProcessor : inputProcessors){
		    if (inputProcessor.inputProcessor.keyUp(keycode))
			    return true;
	    }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
	    for (PriorityInputProcessor inputProcessor : inputProcessors){
		    if (inputProcessor.inputProcessor.keyTyped(character))
			    return true;
	    }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
	    for (PriorityInputProcessor inputProcessor : inputProcessors){
		    if (inputProcessor.inputProcessor.touchDown(screenX,screenY,pointer,button))
			    return true;
	    }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
	    for (PriorityInputProcessor inputProcessor : inputProcessors){
		    if (inputProcessor.inputProcessor.touchUp(screenX,screenY,pointer,button))
			    return true;
	    }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
	    for (PriorityInputProcessor inputProcessor : inputProcessors){
		    if (inputProcessor.inputProcessor.touchDragged(screenX, screenY, pointer))
			    return true;
	    }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
	    for (PriorityInputProcessor inputProcessor : inputProcessors){
		    if (inputProcessor.inputProcessor.mouseMoved(screenX,screenY))
			    return true;
	    }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
	    for (PriorityInputProcessor inputProcessor : inputProcessors){
		    if (inputProcessor.inputProcessor.scrolled(amount))
			    return true;
	    }
        return false;
    }

	private class PriorityInputProcessor implements Comparable<PriorityInputProcessor>{
    	int priority;
    	InputProcessor inputProcessor;
    	private PriorityInputProcessor(InputProcessor inputProcessor, int priority){
    		this.inputProcessor = inputProcessor;
    		this.priority = priority;
	    }

		@Override
		public int compareTo(PriorityInputProcessor o) {
    		if (o.priority == priority)
    			throw new IllegalArgumentException("Two input processors has the same priority: " + priority);
			return o.priority - priority;
		}
	}
}
