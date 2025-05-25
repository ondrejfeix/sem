package cz.cvut.fel.java.uicomponents.buttons;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import lombok.Getter;

import java.util.logging.Logger;

public class GameButton {
    private static final Logger logger = Logger.getLogger(GameButton.class.getName());

    @Getter private TextButton button;

    public GameButton(String text, Skin skin, float posX, float posY, Runnable onClick) {
        button = new TextButton(text, skin);
        button.setSize(400, 70);
        button.setPosition(posX - button.getWidth() /2, posY - button.getHeight() / 2);

        button.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float px, float py, int pointer, int buttonCode) {
                logger.info("Button - " + text + " pressed");
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float px, float py, int pointer, int buttonCode) {
                if (button.isOver()) {
                    logger.info("Button - " + text + " released");
                    onClick.run();
                }
            }
        });
    }

    public void addToStage(Stage stage) {
        stage.addActor(button);
    }
}
