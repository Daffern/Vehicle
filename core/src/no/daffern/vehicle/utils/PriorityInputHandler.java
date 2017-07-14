package no.daffern.vehicle.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.OrderedMap;

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

    private OrderedMap<Integer, InputProcessor> inputProcessors;

    public PriorityInputHandler() {
        inputProcessors = new OrderedMap<Integer, InputProcessor>();
    }

    public void addInputProcessor(InputProcessor inputProcessor, int priority) {
        if (inputProcessors.get(priority) != null){
            Tools.log(this, "Priority value already allocated");
        }
        inputProcessors.put(priority, inputProcessor);
    }
    public void removeInputProcessor(int priority) {
        inputProcessors.remove(priority);
    }


    @Override
    public boolean keyDown(int keycode) {
        for (OrderedMap.Entry<Integer, InputProcessor> inputProcessor : inputProcessors.entries()) {
            if (inputProcessor.value.keyDown(keycode))
                return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (OrderedMap.Entry<Integer, InputProcessor> inputProcessor : inputProcessors.entries()) {
            if (inputProcessor.value.keyUp(keycode))
                return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        for (OrderedMap.Entry<Integer, InputProcessor> inputProcessor : inputProcessors.entries()) {
            if (inputProcessor.value.keyTyped(character))
                return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (OrderedMap.Entry<Integer, InputProcessor> inputProcessor : inputProcessors.entries()) {
            if (inputProcessor.value.touchDown(screenX, screenY, pointer, button))
                return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (OrderedMap.Entry<Integer, InputProcessor> inputProcessor : inputProcessors.entries()) {
            if (inputProcessor.value.touchUp(screenX, screenY, pointer, button))
                return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (OrderedMap.Entry<Integer, InputProcessor> inputProcessor : inputProcessors.entries()) {
            if (inputProcessor.value.touchDragged(screenX, screenY, pointer))
                return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        for (OrderedMap.Entry<Integer, InputProcessor> inputProcessor : inputProcessors.entries()) {
            if (inputProcessor.value.mouseMoved(screenX, screenY))
                return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        for (OrderedMap.Entry<Integer, InputProcessor> inputProcessor : inputProcessors.entries()) {
            if (inputProcessor.value.scrolled(amount))
                return true;
        }
        return false;
    }

}
