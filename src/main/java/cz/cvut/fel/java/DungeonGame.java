package cz.cvut.fel.java;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import cz.cvut.fel.java.screens.MainMenuScreen;
import lombok.Getter;

/**
 * Main class that manages the core game lifecycle and screen transitions.
 * Inherits from LibGDX {@link Game} class.
 */
public class DungeonGame extends Game {
    /**
     * SpriteBatch is used to draw 2D images (textures) efficiently.
     * @return the sprite batch used for rendering
     */
    @Getter private SpriteBatch batch;

    /**
     * Viewport handles the scaling and resizing of the game's screen.
     * ScreenViewport stretches the game to fill the entire screen.
     * @return the viewport used for camera projection
     */
    @Getter private Viewport viewport;

    /**
     * Called when the application is first created.
     * Initializes rendering tools and sets the initial screen to the main menu.
     */
    @Override
    public void create() {
        // Initialize the viewport and batch
        this.batch = new SpriteBatch();
        this.viewport = new ScreenViewport();

        // Set the initial screen to the main menu
        this.setScreen(new MainMenuScreen(this));
    }

    /**
     * Called every frame to update and render the current screen.
     * Delegates the rendering to the currently active screen.
     */
    @Override
    public void render() {
        // Updates and renders the active screen
        super.render();
    }
}
