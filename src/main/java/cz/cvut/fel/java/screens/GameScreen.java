package cz.cvut.fel.java.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.cvut.fel.java.DungeonGame;
import cz.cvut.fel.java.Rooms.Room;
import cz.cvut.fel.java.characters.*;
import cz.cvut.fel.java.dto.RoomMovementDto;
import cz.cvut.fel.java.levels.Level;
import cz.cvut.fel.java.uicomponents.texts.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * GameScreen manages the main gameplay screen, including rendering the level,
 * handling user input, saving/loading game state, and pausing the game.
 */
public class GameScreen implements Screen {
    /**
     * Reference to the main game instance
     */
    private DungeonGame game;

    /**
     * The current level being played
     */
    private Level level;

    /**
     * Background texture of the level
     */
    private Texture backgroundTexture;

    /**
     * Flag indicating whether the game is paused or not
     */
    private boolean gamePaused = false;

    /**
     * List of texts displayed when the game is paused
     */
    private ArrayList<Text> gamePausedTexts;

    /**
     * Logger for logging game events
     */
    private static final Logger logger = Logger.getLogger(GameScreen.class.getName());

    /**
     * The player character instance.
     */
    private Player player;
    // TEMPORARY -> enemies are hardcoded for now

    private Enemies activeEnemies;

    /**
     * Stage object used for managing UI elements and input.
     */
    private Stage stage;

    private Trader trader;
    private Dragon dragon;

    private ShapeRenderer shapeRenderer;

    private Room currentRoom;

    private Room portalRoom;
    private Room traderRoom;

    /**
     * Constructor for the GameScreen class
     *
     * @param game the main game instance
     * @param newGame true to start a new game, false to load an existing game
     */
    public GameScreen(DungeonGame game, boolean newGame) {
        this.game = game;

        // Check if the game is new or needs to be loaded from a save
        JsonNode gameData = null;

        if (newGame) {
            // Create new save file
            gameData = createNewLevelSave(1);
            logger.info("New game -> create new save file");
        } else {
            // Load existing save file
            gameData = loadLevelSave();
            logger.info("Load existing save file");
        }

        // Create level based on save data
        this.level = Level.createLevel(gameData);

        // Create player based on save data
        this.player = Player.createPlayer(gameData);

        // Change the player position to the spawn room
        Room spawnRoom = level.rooms.getSpawnRoom();
        player.setPosition(
                spawnRoom.spawnPoint[0],
                spawnRoom.spawnPoint[1]
        );

        this.currentRoom = spawnRoom;
        currentRoom.visited = true;
        currentRoom.active = false;
        currentRoom.prepared = false;

        this.portalRoom = level.rooms.getPortalRoom();
        if (this.portalRoom != null) {
            level.hasPortalRoom = true;
        }

        this.traderRoom = level.rooms.getTraderRoom();
        if (this.traderRoom != null) {
            level.hasTraderRoom = true;
        }

        // TEMPORARY
        // Create enemies based on save data
        this.activeEnemies = new Enemies();
        this.activeEnemies.addEnemy(new Enemy("orc"));
        this.activeEnemies.addEnemy(new Enemy("goblin"));

        this.trader = new Trader();
        this.dragon = new Dragon();

        // Load the background texture for the level
        this.backgroundTexture = new Texture(Gdx.files.internal("textures/" + level.getMap()));

        // Define the game paused texts
        this.gamePausedTexts = new ArrayList<>();
        this.gamePausedTexts.add(new Text("Game paused",Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 300));
    }


    /**
     * Renders the game screen
     *
     * @param delta time since the last frame
     */
    @Override
    public void render(float delta) {
        // Check if the game is paused
        if (gamePaused) {
            handleGamePause();
            return;
        }
        // handle room active state
        if (!currentRoom.active && currentRoom.prepared) {
            setRoomActive(currentRoom, player);
            System.out.println("Room " + currentRoom.id + " is now active: " + currentRoom.active);
        }

        if (currentRoom.type.equals("fight")) {
            currentRoom.checkFightRoomStatus();
        }


        // Handle user input
        handleInput();

        handlePortal();

        // update players stamina
        player.updateStamina();


        if (currentRoom.type.equals("fight") && currentRoom.active) {
            System.out.println("suifhg");
            // Update enemies movement
            currentRoom.enemies.moveEnemies(player.getPosition());

            // Update enemies attack
            currentRoom.enemies.enemiesAttack(player);
        } else if (currentRoom.type.equals("boss")) {
            currentRoom.boss.attack(player);
        }

        // Check the game status
        if (checkStatus()) {
            return;
        }

        // Render the game screen
        renderScreen();
    }

    /**
     * Checks the status of the game, such as player death or all enemies defeated.
     *
     * @return true if the game is over or won; false otherwise
     */
    private boolean checkStatus() {
        // Check if the player is alive
        if (!player.isAlive()) {
            logger.info("Player is dead -> changing to game over screen");
            game.setScreen(new GameStateScreen(game, true));
            return true;
        }

/*        // Check if all enemies are dead
        if (currentRoom.enemies.isEmpty()) {
            logger.info("All enemies are dead -> changing to game won screen");
            game.setScreen(new GameStateScreen(game, false));
            return true;
        }*/

        return false;
    }

    /**
     * Renders the pause menu overlay and handles input when the game is paused.
     */
    private void handleGamePause() {
        handleInputPause();

        game.getBatch().begin();

        // Draw the pause screen
        for (Text text : gamePausedTexts) {
            text.renderText(game.getBatch());
        }
        game.getBatch().end();
    }

    /**
     * Renders the game scene including player, enemies, and background.
     */
    public void renderScreen() {
        game.getBatch().begin();

        // Clear the screen with solid black color for new frame
        ScreenUtils.clear(Color.BLACK);

        // Draw the backgroundTexture
        game.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Render the rooms
        game.getBatch().end();
        renderRooms();
        game.getBatch().begin();

        // Draw the player
        player.getSprite().draw(game.getBatch());

        game.getBatch().end();
        shapeRenderer.setProjectionMatrix(game.getBatch().getProjectionMatrix());
        player.renderBar(shapeRenderer);
        game.getBatch().begin();


        if (currentRoom.type.equals("fight")) {
            // Draw the enemies
            currentRoom.enemies.renderEnemies(game.getBatch(), shapeRenderer);
        } else if (currentRoom.type.equals("boss") && currentRoom.boss.isAlive()) {
            // Draw the dragon
            currentRoom.boss.getSprite().draw(game.getBatch());

            game.getBatch().end();

            currentRoom.boss.renderAttackSquare(shapeRenderer);
            currentRoom.boss.renderBar(shapeRenderer);

            game.getBatch().begin();
        }

        if (level.hasTraderRoom) {
            if (traderRoom.visited) {
                // Draw the trader
                traderRoom.trader.getTraderSprite().draw(game.getBatch());

                renderTraderDialogue();
            }
        }
        if (level.hasPortalRoom) {
            if (portalRoom.visited) {
                portalRoom.portal.renderPortal(shapeRenderer);
            }
        }


        game.getBatch().end();

    }

    private void renderRooms() {
        for (Room room : level.rooms) {
            // Draw the room bounds
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(room.bounds.x, room.bounds.y, room.bounds.width, room.bounds.height);
            shapeRenderer.end();
            room.renderCurtain(shapeRenderer);
        }
    }

    private void renderTraderDialogue() {
        if (traderRoom.trader.tradeMenuOpened) {
            traderRoom.trader.initialDialogueText.renderText(game.getBatch());
        }

        if (traderRoom.trader.tradeResultText != null) {
            traderRoom.trader.tradeResultText.renderText(game.getBatch());
            traderRoom.trader.resultDisplayTime -= Gdx.graphics.getDeltaTime();
            if (traderRoom.trader.resultDisplayTime <= 0) {
                traderRoom.trader.tradeResultText = null;
            }

        }
    }

    /**
     * Handles all user inputs for player movement, pausing, and screen transitions.
     */
    private void handleInput() {
/*        // TEMPORARY -> when window key is clicked, change to the next level
        if (Gdx.input.justTouched()) {
            // Check if this is the max level
            if (this.level.getLevelNumber() != 3) {
                logger.info("Touched -> changing to the next level");
                // Create new save file for the next level
                createNewLevelSave(this.level.getLevelNumber() + 1);
                game.setScreen(new GameScreen(this.game, false));
            } else {
                logger.info("Touched -> not changing to the next level -> max level reached");
            }
        }*/

        if (Gdx.input.justTouched()) {
            currentRoom.active = false;
            currentRoom.prepared = false;
        }

        // TEMPORARY -> when user presses n change the screen to game over screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            logger.info("N pressed -> changing to game over screen");

            game.setScreen(new GameStateScreen(game, true));
        }

        // TEMPORARY -> when user presses m change the screen to game won screen
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            logger.info("M pressed -> changing to game won screen");
            game.setScreen(new GameStateScreen(game, false));
        }

        // Handle pause / unpause input
        handleInputPause();

        // Handle player movement input
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            checkRoomSwicth(player.moveUp(currentRoom));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            checkRoomSwicth(player.moveDown(currentRoom));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            checkRoomSwicth(player.moveLeft(currentRoom));
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            checkRoomSwicth(player.moveRight(currentRoom));
        }

        // Handle player attack input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (currentRoom.enemies != null && (!currentRoom.enemies.currentEnemies.isEmpty())) {
                player.handleAttack(currentRoom.enemies.currentEnemies);
                logger.info("Player attacking" + player);
            }
        }

        if (level.hasTraderRoom) {
            // Handle trader input
            if (currentRoom.type.equals("trader")) {
                handleTraderInput();
            } else {
                traderRoom.trader.tradeMenuOpened = false; // Close the trader menu if not in trader room
            }
        }

    }

    private void checkRoomSwicth(RoomMovementDto playerMoveDto) {
        // Check if the player can move to the next room
        if (playerMoveDto.switchRoom) {
            currentRoom = playerMoveDto.nextRoom;

            if (!currentRoom.visited) {
                currentRoom.visited = true;
            }
        }
    }

    private void setRoomActive(Room currentRoom, Player player) {
        if (currentRoom.type.equals("corridor")) {
            return;
        }

        Vector2 playerPosition = player.getPosition();
        float playerWidth = player.getSprite().getWidth();
        float playerHeight = player.getSprite().getHeight();

        boolean isInsideLeft = (playerPosition.x + playerWidth) < (currentRoom.bounds.x + currentRoom.bounds.width);
        boolean isInsideRight = (playerPosition.x) > currentRoom.bounds.x;
        boolean isInsideTop = playerPosition.y > (currentRoom.bounds.y);
        boolean isInsideBottom = (playerPosition.y + playerHeight) < (currentRoom.bounds.y + currentRoom.bounds.height);

        if ((isInsideLeft && isInsideRight) && (isInsideTop && isInsideBottom)) {
            currentRoom.active = true;
        }

        System.out.println("left: " + isInsideLeft + ", right: " + isInsideRight +
                ", top: " + isInsideTop + ", bottom: " + isInsideBottom);


    }

    public void handlePortal() {
        // Check if the player is in the portal room
        if (currentRoom.type.equals("portal")) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                if (this.level.getLevelNumber() != 3) {
                    logger.info("Touched -> changing to the next level");
                    // Create new save file for the next level
                    createNewLevelSave(this.level.getLevelNumber() + 1);
                    game.setScreen(new GameScreen(this.game, false));
                } else {
                    logger.info("Touched -> not changing to the next level -> max level reached");
                }
            }
        }
    }

    /**
     * Handles user input for pausing the game
     * Is called inside the handle input method and render method when the game is paused
     */
    private void handleInputPause() {
        // TEMPORARY -> when user presses ESC pause the game / unpause the game
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            logger.info("Escape pressed -> changing game state to unpause / pause");
            this.gamePaused = !this.gamePaused;
        }
    }


    private void handleTraderInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            traderRoom.trader.tradeMenuOpened = !trader.tradeMenuOpened;
        }

        if (traderRoom.trader.tradeMenuOpened) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                traderRoom.trader.healPlayer(player);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                traderRoom.trader.repairArmor(player);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                traderRoom.trader.upgradeWeapon(player);
            }
            logger.info("player: " + player);
        }
    }
    /**
     * Called when this screen becomes the current screen.
     */
    public void show() {
        // Set the input processor to the current screen
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // DRAGON -> needed to render the attackSquare
        shapeRenderer = new ShapeRenderer();
    }

    /**
     * Called when the application is resized.
     */
    public void resize(int width, int height) {}

    /**
     * Called when the application is paused.
     */
    public void pause() {}

    /**
     * Called when the application is resumed.
     */
    public void resume() {}

    /**
     * Called when this screen is no longer the current screen.
     */
    public void hide() {}

    /**
     * Called when this screen is disposed.
     * Disposes of all assets and resources associated with this screen.
     */
    public void dispose() {
        logger.info("Disposing game screen");
        // Dispose of all the enemies if they are not null
        if (currentRoom.enemies != null) {
            currentRoom.enemies.dispose();
        }
        // Dispose of the player if it is not null
        if (player != null) {
            player.dispose();
        }

        // dispose of the pause screen texts
        for (Text text : gamePausedTexts) {
            text.dispose();
        }
    }

    /**
     * Creates a new save file for the current level.
     * This method generates a JSON object with the current level number,
     * writes it to a save file, and returns the JSON node.
     *
     * @param levelNumber the level number to save
     * @return the new save file as a JsonNode
     */
    private JsonNode createNewLevelSave(int levelNumber) {
        // Create an instance of ObjectMapper to work with JSON
        ObjectMapper mapper = new ObjectMapper();

        // Create a new JSON object node to store game data
        ObjectNode gameData = mapper.createObjectNode();

        // Add the current level number to the game data
        gameData.put("currentLevel", levelNumber);

        // Add the player data to the game data
        if (player == null) {
            player = Player.getDefaultPlayer();
        }
        player.savePlayer(gameData);

        try {
            // Write the JSON object to a file
            File saveFile = new File("src/main/resources/saves/gameSave.json");
            mapper.writeValue(saveFile, gameData);
        } catch (IOException e) {
            logger.severe("Failed to write save file: " + e.getMessage());
        }

        // Return the created JSON game data
        return gameData;
    }

    /**
     * Loads the existing save file containing level data.
     * This method reads the save file and returns its contents as a JsonNode.
     *
     * @return the loaded save file as a JsonNode
     */
    private JsonNode loadLevelSave() {
        // Create an instance of ObjectMapper to work with JSON
        ObjectMapper mapper = new ObjectMapper();
        JsonNode gameData = null;

        try {
            // Read the JSON object from the save file
            File saveFile = new File("src/main/resources/saves/gameSave.json");
            gameData = mapper.readTree(saveFile);
        } catch (IOException e) {
            logger.severe("Failed to load save file: " + e.getMessage());
        }

        // Return the loaded JSON game data
        return gameData;
    }



}
