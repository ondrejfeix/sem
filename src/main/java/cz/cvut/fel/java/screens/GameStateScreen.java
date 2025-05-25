package cz.cvut.fel.java.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import cz.cvut.fel.java.DungeonGame;
import cz.cvut.fel.java.uicomponents.buttons.GameButton;
import cz.cvut.fel.java.uicomponents.texts.Text;

/**
 * Screen that represents the end of the game state, either game over or game won.
 * It displays the game state message and provides options to start a new game or exit the game.
 *
 */
public class GameStateScreen implements Screen {
    /**
     * Reference to the main game class
     */
    private final DungeonGame game;

    /**
     * Text displayed on the screen indicating the game state (game over or won)
     */
    private final Text stateText;

    /**
     * Stage for handling UI components
     */
    private Stage stage;


    // Buttons
    /**
     * Buttons for starting a new game and exiting the game
     */
    private GameButton newGameButton;

    /**
     * Button for exiting the game
     */
    private GameButton exitButton;

    /**
     * Constructs the GameStateScreen with specified message depending on the game state.
     *
     * @param game the main game instance
     * @param gameOver true if the game is over, false if the player has won
     */
    public GameStateScreen(DungeonGame game, boolean gameOver) {
        this.game = game;

        // Initialize the text based on the game state
        this.stateText = new Text(
                gameOver ? "Game Over" : "You won" ,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
        );
    }

    /**
     * Renders the game state screen including the game state message and UI buttons.
     *
     * @param delta time since last frame
     */
    @Override
    public void render(float delta) {
        game.getBatch().begin();

        // Draw the game state text
        stateText.renderText(game.getBatch());

        game.getBatch().end();

        // Draw the stage (UI components)
        stage.act(delta);
        stage.draw();
    }

    /**
     * Called when this screen becomes the current screen for the game.
     * Initializes UI components and input handling for buttons.
     */
    @Override
    public void show() {
        // Set up the stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load the skin for UI components
        Skin skin = new Skin(Gdx.files.internal("skins/plain-james/plain-james-ui.json"));

        // Get the positions of the middle of the screen
        float middleX = Gdx.graphics.getWidth() / 2f;
        float middleY = Gdx.graphics.getHeight() / 2f;

        // Set up and configure the buttons
        newGameButton = new GameButton(
                "New Game", skin, middleX, middleY,
                () -> game.setScreen(new GameScreen(game, true))
        );
        newGameButton.addToStage(stage);

        exitButton = new GameButton(
                "Exit", skin, middleX, middleY - 120,
                () -> Gdx.app.exit()
        );
        exitButton.addToStage(stage);
    }

    /**
     * Called when the screen is resized.
     *
     * @param width new width of the screen
     * @param height new height of the screen
     */
    @Override
    public void resize(int width, int height) {}

    /**
     * Called when the application is paused.
     */
    @Override
    public void pause() {}

    /**
     * Called when the application is resumed.
     */
    @Override
    public void resume() {}

    /**
     * Called when this screen is no longer the current screen.
     */
    @Override
    public void hide() {}

    /**
     * Called when this screen is disposed.
     * Frees memory used by stage and text rendering.
     */
    @Override
    public void dispose() {
        stage.dispose();
        stateText.dispose();
    }


}
