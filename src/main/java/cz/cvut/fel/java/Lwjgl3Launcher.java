package cz.cvut.fel.java;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Entry point for the Dungeon Game application using LWJGL3 backend.
 * This class sets up the configuration and launches the game.
 */
public class Lwjgl3Launcher {
    /**
     * Width of the game window
     */
    public static final int WIDTH = 1300;
    /**
     * Height of the game window
     */
    public static final int HEIGHT = 800;

    /**
     * Main method to start the game. Sets up the game window and initializes the game.
     * @param args command-line arguments (ignored)
     */
    public static void main(String[] args) {
        // Set up the application configuration
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Set the title of the window
        config.setTitle("Dungeon Game");
        // Set the dimensions of teh game
        config.setWindowedMode(WIDTH, HEIGHT);
        // Set the window to not to be resizable
        config.setResizable(false);

        // Create a new instance of the game and start it
        new Lwjgl3Application(new DungeonGame(), config);
    }
}
