package cz.cvut.fel.java.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.databind.JsonNode;
import cz.cvut.fel.java.dto.MovementDto;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents a collection of enemies in the game. This class manages movement, attack, and rendering of the enemies.
 */
public class Enemies {
    /**
     * List holding all current enemies in the game.
     */
    public ArrayList<Enemy> currentEnemies;

    /**
     * Logger for logging messages related to enemy actions.
     */
    private static final Logger logger = Logger.getLogger(Enemies.class.getName());

    /**
     * Moves all enemies towards the player's position.
     *
     * @param playerPosition The current position of the player to which enemies will move.
     */
    public void moveEnemies(Vector2 playerPosition) {
        for (Enemy enemy: currentEnemies) {
            // Create a virtual thread for each enemy to calculate its movement
            Thread.ofVirtual().start(() -> {
                // Calculate the movement direction towards the player
                MovementDto movementDetail = enemy.calculateMove(playerPosition);
                // Move the enemy using the calculated movement details
                enemy.move(movementDetail);
            });
        }
    }

    /**
     * Makes enemies attack the player if they are in range and the attack cooldown has passed.
     *
     * @param player The player who might be attacked by the enemies.
     */
    public void enemiesAttack(Player player) {
        // Get the player's position for the enemies to calculate their attack range.
        Vector2 playerPosition = player.getPosition();
        // List to hold all threads for the attacks.
        List<Thread> threads = new ArrayList<>();

        for (Enemy enemy: currentEnemies) {
            Thread thread = Thread.ofVirtual().unstarted(() -> {
                // Create a new virtual thread to handle each enemy's attack.
                if (enemy.enemyCanAttack(playerPosition)) {
                    logger.info("Enemy " + enemy.type + " is attacking the player!");
                    // Get the enemy's position to calculate the direction of attack.
                    Vector2 selfPosition = enemy.getPosition();

                    // Calculate the direction of the attack from the enemy to the player.
                    Vector2 direction = selfPosition.cpy().sub(playerPosition).nor();

                    // Use synchronized block to ensure thread safety when modifying player health.
                    synchronized(player) {
                        // Player takes damage from the enemy's attack.
                        player.takeDamage(enemy.equippedWeapon.getDamage());
                        logger.info("Player took damage from enemy " + enemy.type + "\nplayer stats: " + player +"\n");
                    }

                    // Update the enemy's attack texture based on the attack direction.
                    Gdx.app.postRunnable(() -> {
                        enemy.updateAttackTexture(direction);
                    });

                } else {
                    // Update the attack cooldown for the enemy if they cannot attack yet.
                    enemy.updateAttackCooldown();
            }
        });
        threads.add(thread);
        thread.start();
    }

    // wait for all threads to finish before proceeding
        for (Thread thread: threads) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

    /**
     * Adds a new enemy to the list of current enemies.
     *
     * @param enemy The enemy to add to the list of current enemies.
     */
    public void addEnemy(Enemy enemy) {
        if (currentEnemies == null) {
            currentEnemies = new ArrayList<>();
        }
        currentEnemies.add(enemy);
    }

    /**
     * Renders all enemies on the screen using the provided SpriteBatch.
     *
     * @param batch The SpriteBatch used to draw the enemies.
     */
    public void renderEnemies(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        for (Enemy enemy: currentEnemies) {
            enemy.getSprite().draw(batch);
            batch.end();
            enemy.renderBar(shapeRenderer);
            batch.begin();
        }
    }

    /**
     * Disposes of all enemies' resources (such as textures).
     */
    public void dispose() {
        for (Enemy enemy: currentEnemies) {
            enemy.dispose();
        }
    }


    public static Enemies loadEnemies(JsonNode enemiesData) {
        Enemies enemies = new Enemies();
        for (JsonNode enemyData : enemiesData) {
            String type = enemyData.get("type").asText();
            Enemy enemy = new Enemy(type);
            enemies.addEnemy(enemy);
        }
        return enemies;
    }
}
