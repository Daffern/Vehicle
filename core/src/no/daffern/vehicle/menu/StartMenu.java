package no.daffern.vehicle.menu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;
import no.daffern.vehicle.utils.PriorityInputHandler;


/**
 * Created by Daffern on 06.11.2016.
 */
public class StartMenu {


	Stage stage;
	VisWindow window;

	private static float pad = 2f;
	private static float height = 30f;

	public StartMenu() {

		stage = new Stage(new ScreenViewport());


		VisUI.load(VisUI.SkinScale.X2);


		window = new VisWindow("Menu", true);

		stage.addActor(window);


		//stage.setDebugAll(true);


	}

	public Stage getStage() {
		return stage;
	}


	public void load(final StartMenuListener menuListener) {

		VisTextButton clientButton = new VisTextButton("Client");
		VisTextButton serverButton = new VisTextButton("Server");
		VisTextButton quickstartButton = new VisTextButton("Debug");

		clientButton.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					if (menuListener != null)
						menuListener.onClientClicked();
				}
			}
		});

		serverButton.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					if (menuListener != null)
						menuListener.onServerClicked();
				}
			}

		});
		quickstartButton.addListener(new ClickListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if (button == Input.Buttons.LEFT) {
					if (menuListener != null)
						menuListener.onQuickstartClicked();
				}
			}

		});

		VisTable table = new VisTable();
		table.add(clientButton);
		//table.add(serverButton);

		window.add(table);
		window.row();
		window.add(quickstartButton);
		window.pack();
		window.centerWindow();

		PriorityInputHandler.getInstance().addInputProcessor(stage, 0);

	}

	public void unload() {
		PriorityInputHandler.getInstance().removeInputProcessor(0);
		stage.clear();
		VisUI.dispose();
	}

	public void resize(float width, float height){
		stage.getViewport().setWorldSize(width,height);
		stage.getViewport().apply();
	}

	public void render(float delta) {
		stage.act(delta);
		stage.draw();
	}

	public interface StartMenuListener {
		void onClientClicked();

		void onServerClicked();

		void onQuickstartClicked();
	}

}
