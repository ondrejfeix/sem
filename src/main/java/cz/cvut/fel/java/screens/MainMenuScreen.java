package cz.cvut.fel.java.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import cz.cvut.fel.java.DungeonGame;
import cz.cvut.fel.java.uicomponents.buttons.GameButton;

/**
 * Represents the main menu screen of the Dungeon game.
 * Displays options to start a new game, load a game or exit.
 */
public class MainMenuScreen implements Screen {
    /**
     * Reference to the game instance
     */
    private final DungeonGame game;

    // UI COMPONENTS
    /**
     * Stage for handling UI components
     */
    private Stage stage;
    /**
     * Background texture for the main menu
     */
    private Texture backgroundTexture;
    // Buttons for the main menu
    /**
     * Button to start a new game
     */
    private GameButton newGameButton;
    /**
     * Button to load an existing game
     */
    private GameButton loadGameButton;
    /**
     * Button to exit the game
     */
    private GameButton exitGameButton;

    /**
     * Constructor that initializes the screen with the given game instance.
     *
     * @param game the game instance
     */
    public MainMenuScreen(DungeonGame game) {
        this.game = game;

        // Load the background texture
        backgroundTexture = new Texture("textures/main_menu_bg.png");
    }

    /**
     * Renders the screen every frame.
     * Draws the background and the UI stage.
     *
     * @param delta time since the last frame
     */
    @Override
    public void render(float delta) {
        // Clear the screen with solid black color for new frame
        ScreenUtils.clear(Color.BLACK);

        this.game.getBatch().begin();

        // Draw the background texture
        this.game.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        this.game.getBatch().end();

        // Update the stage and draw the UI components
        stage.act(delta);
        stage.draw();
    }

    /**
     * Called when this screen becomes the current screen.
     * Initializes the UI stage and buttons.
     */
    @Override
    public void show() {
        // Initializes a new stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load the UI skin
        Skin skin = new Skin(Gdx.files.internal("skins/plain-james/plain-james-ui.json"));

        // Get the positions of the middle of the screen
        float middleX = Gdx.graphics.getWidth() / 2f;
        float middleY = Gdx.graphics.getHeight() / 2f;

        // Set up and configure the buttons
        newGameButton = new GameButton(
                "New Game", skin, middleX, middleY + 100,
                () -> game.setScreen(new GameScreen(game, true))
        );
        newGameButton.addToStage(stage);

        loadGameButton = new GameButton(
                "Load Game", skin, middleX, middleY,
                () -> game.setScreen(new GameScreen(game, false))
        );
        loadGameButton.addToStage(stage);

        exitGameButton = new GameButton(
                "Exit", skin, middleX, middleY - 100,
                () -> Gdx.app.exit()
        );
        exitGameButton.addToStage(stage);
    }

    /**
     * Called when the application is resized.
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
     * Called when the screen should release all resources.
     * Disposes of the background texture and stage.
     */
    @Override
    public void dispose() {
        backgroundTexture.dispose();
        stage.dispose();
    }
}
