package cz.cvut.fel.java.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import cz.cvut.fel.java.Rooms.Room;
import cz.cvut.fel.java.dto.AttackDto;
import cz.cvut.fel.java.dto.BarVisualDto;
import cz.cvut.fel.java.dto.MovementDto;
import cz.cvut.fel.java.dto.RoomMovementDto;
import cz.cvut.fel.java.objects.weapons.Weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static cz.cvut.fel.java.textureconstants.PlayerTextures.*;
/**
 * The Player class represents a playable character with health, stamina,
 * balance, and equipped weapon. It supports movement, attacks, stamina regeneration,
 * and saving/loading player state.
 */
public class Player extends Character {

    private static final Logger logger = Logger.getLogger(Player.class.getName());

    /**
     * The player's current balance, which could be used for purchasing items.
     */
    private int balance;

    /**
     * The maximum stamina the player can have.
     */
    private int maxStamina;

    /**
     * The current stamina of the player.
     */
    private float stamina;

    /**
     * The maximum armor the player can have
     */
    private int maxArmor;

    /**
     * The current armor of the player
     */
    private int armor;



    /**
     * Constructs a Player object with specified health, stamina, and balance.
     * The player's max health and stamina are set to 100, and they are equipped with a sword.
     *
     * @param health the initial health of the player
     * @param stamina the initial stamina of the player
     * @param balance the initial balance of the player
     */
    public Player(int health, float stamina, int balance, int armor) {
        this.maxHealth = 100;
        this.maxStamina = 100;
        this.speed = 90f;

        this.health = health;
        this.stamina = stamina;
        this.balance = balance;

        this.maxArmor = 100;
        this.armor = armor;

        // Equip the player with a sword by default
        this.equippedWeapon = new Weapon("sword");

        // Set the default texture for the player
        this.texture = new Texture(PLAYER_STAND_DOWN);
        this.sprite = new Sprite(this.texture);
    }

    /**
     * Returns a default player instance with preset values for health, stamina, and balance.
     * Used for creating new game saves
     *
     * @return a new default player
     */
    public static Player getDefaultPlayer() {
        return new Player(100, 100, 0, 100);
    }

    /**
     * Saves the player's current state (health, stamina, and balance) to a game save.
     *
     * @param gameSave the JSON node representing the game save data
     */
    public void savePlayer(JsonNode gameSave) {
        ObjectNode playerSave = gameSave.with("player");

        playerSave.put("health", this.health);
        playerSave.put("stamina", this.stamina);
        playerSave.put("balance", this.balance);
        playerSave.put("armor", this.armor);
    }

    /**
     * Creates a new player instance from the provided game save data.
     *
     * @param gameSave the JSON node containing saved game data
     * @return a new player instance based on the saved data
     */
    public static Player createPlayer(JsonNode gameSave) {
        int health = gameSave.get("player").get("health").asInt();
        int stamina = gameSave.get("player").get("stamina").asInt();
        int balance = gameSave.get("player").get("balance").asInt();
        int armor = gameSave.get("player").get("armor").asInt();

        return new Player(health, stamina, balance, armor);
    }

    /**
     * Updates the player's stamina, regenerating it over time up to the maximum stamina.
     */
    public void updateStamina() {
        float delta = Gdx.graphics.getDeltaTime();

        // The amount of stamina to regenerate per second
        float staminaRegen = 10f;

        this.stamina += staminaRegen * delta;

        if (this.stamina > this.maxStamina) {
            this.stamina = this.maxStamina;
        }
    }

    private void decreaseStamina(float amount) {
        this.stamina -= amount;
        if (this.stamina < 0) {
            this.stamina = 0;
        }
    }

    /**
     * Handles the player's attack action. The player uses stamina to attack enemies within range,
     * and rewards are granted for defeating enemies.
     *
     * @param enemies the list of enemies currently in the game
     */
    public void handleAttack(ArrayList<Enemy> enemies) {
        // Check if the player has enough stamina to attack
        if (this.stamina < this.equippedWeapon.getStaminaCost()) {
            return;
        }

        // TODO temporary only attack down
        this.sprite.setTexture(new Texture(PLAYER_ATTACK_DOWN));

/*        // Change to attack texture
        Vector2 attackerPosition = enemiesInRange.getFirst().enemyToAttack.getPosition();
        Vector2 attackDirection = attackerPosition.cpy().sub(getPosition());
        updateAttackTexture(attackDirection);*/

        // Decrease stamina
        decreaseStamina(this.equippedWeapon.getStaminaCost());

        // Get the enemies in range
        ArrayList<AttackDto> enemiesInRange = getEnemiesInRange(enemies);

        // If no enemies are in range, return early
        if (enemiesInRange.isEmpty()) {
            return;
        }

        // Choose the first enemy in range
        boolean enemyDead = attackEnemy(enemiesInRange.getFirst());

        if (enemyDead) {
            // Get the reward for killing the enemy
            this.balance += enemiesInRange.getFirst().enemyToAttack.reward;

            // Remove the enemy from the game
            enemiesInRange.getFirst().enemyToAttack.dispose();
            enemies.remove(enemiesInRange.getFirst().enemyToAttack);
        }


    }

    /**
     * Returns a list of enemies within the player's attack range.
     *
     * @param enemies the list of all enemies in the game
     * @return a list of AttackDto containing the enemies in range
     */
    private ArrayList<AttackDto> getEnemiesInRange(ArrayList<Enemy> enemies) {
        ArrayList<AttackDto> enemiesInRange = new ArrayList<>();

        // Get the position of the player
        Vector2 playerPosition = getPosition();

        // Default attack texture
        this.attackTextureName = PLAYER_ATTACK_DOWN;

        List<Thread> threads = new ArrayList<>();

        for (Enemy enemy : enemies) {
            // Start a virtual thread for every enemy to check if they are in range
            Thread thread = Thread.ofVirtual().unstarted(() -> {
                // Get the position of the enemy
                Vector2 selfPosition = enemy.getPosition();

                // Calculate the distance between the player and the enemy
                float distance = playerPosition.dst(selfPosition);

                // Check if the enemy is within range
                if (distance < this.equippedWeapon.getRange()) {
                    // Get the direction of the attack
                    Vector2 attackDirection = playerPosition.cpy().sub(selfPosition).nor();

                    enemiesInRange.add(new AttackDto(attackDirection, enemy, false));
                }
            });
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return enemiesInRange;
    }

    /**
     * Performs an attack on the specified enemy.
     *
     * @param attackDetail contains information about the attack direction and the target enemy
     * @return true if the enemy is dead, false if the enemy is still alive
     */
    private boolean attackEnemy(AttackDto attackDetail) {
        attackDetail.enemyToAttack.takeDamage(this.equippedWeapon.getDamage());

        // update the attack texture
        // TODO - temporary solution
        // this.sprite.setTexture(new Texture(PLAYER_ATTACK_DOWN));
        /*updateAttackTexture(attackDetail.direction);*/

        return !attackDetail.enemyToAttack.isAlive();
    }

    /**
     * Updates the player's attack texture based on the direction of the attack.
     *
     * @param direction the direction of the attack
     */
    private void updateAttackTexture(Vector2 direction) {
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            this.attackTextureName = (direction.x > 0) ? PLAYER_ATTACK_RIGHT : PLAYER_ATTACK_LEFT;
        } else {
            this.attackTextureName = (direction.y > 0) ? PLAYER_ATTACK_UP : PLAYER_ATTACK_DOWN;
        }

        this.sprite.setTexture(new Texture(this.attackTextureName));
    }


    private RoomMovementDto canMoveX(float deltaX, Room currentRoom) {
        float delta = Gdx.graphics.getDeltaTime();
        float newX = this.sprite.getX() + deltaX * delta;
        float spriteWidth = this.sprite.getWidth();

        float left = newX;
        float right = newX + spriteWidth;

        float roomLeft = currentRoom.bounds.x;
        float roomRight = roomLeft + currentRoom.bounds.width;

        // Normal movement inside current room
        if (left >= roomLeft && right <= roomRight) {
            return new RoomMovementDto(true, false, null);
        }

        if (currentRoom.active == true) {
            return new RoomMovementDto(false, false, null);
        }

        float playerBottom = this.sprite.getY();
        float playerTop = playerBottom + this.sprite.getHeight();

        for (Room neighbor : currentRoom.neighbors) {
            if (left < roomLeft && isLeftNeighbor(currentRoom, neighbor)) {
                if (overlapsVertically(playerBottom, playerTop, neighbor.bounds.y, neighbor.bounds.y + neighbor.bounds.height)) {

                    // ======= CLAMP PLAYER Y POSITION TO NEIGHBOR ROOM VERTICAL BOUNDS =======
                    float clampedY = Math.min(Math.max(playerBottom, neighbor.bounds.y),
                            neighbor.bounds.y + neighbor.bounds.height - this.sprite.getHeight());
                    this.sprite.setY(clampedY);

                    return new RoomMovementDto(true, true, neighbor);
                }
            }

            if (right > roomRight && isRightNeighbor(currentRoom, neighbor)) {
                if (overlapsVertically(playerBottom, playerTop, neighbor.bounds.y, neighbor.bounds.y + neighbor.bounds.height)) {

                    // ======= CLAMP PLAYER Y POSITION TO NEIGHBOR ROOM VERTICAL BOUNDS =======
                    float clampedY = Math.min(Math.max(playerBottom, neighbor.bounds.y),
                            neighbor.bounds.y + neighbor.bounds.height - this.sprite.getHeight());
                    this.sprite.setY(clampedY);

                    return new RoomMovementDto(true, true, neighbor);
                }
            }
        }

        return new RoomMovementDto(false, false, null);
    }

    private RoomMovementDto canMoveY(float deltaY, Room currentRoom) {
        float delta = Gdx.graphics.getDeltaTime();
        float newY = this.sprite.getY() + deltaY * delta;
        float spriteHeight = this.sprite.getHeight();

        float bottom = newY;
        float top = newY + spriteHeight;

        float roomBottom = currentRoom.bounds.y;
        float roomTop = roomBottom + currentRoom.bounds.height;

        // Normal movement inside current room
        if (bottom >= roomBottom && top <= roomTop) {
            return new RoomMovementDto(true, false, null);
        }

        if (currentRoom.active == true) {
            return new RoomMovementDto(false, false, null);
        }

        float playerLeft = this.sprite.getX();
        float playerRight = playerLeft + this.sprite.getWidth();

        for (Room neighbor : currentRoom.neighbors) {
            if (bottom < roomBottom && isBottomNeighbor(currentRoom, neighbor)) {
                if (overlapsHorizontally(playerLeft, playerRight, neighbor.bounds.x, neighbor.bounds.x + neighbor.bounds.width)) {

                    // ======= CLAMP PLAYER X POSITION TO NEIGHBOR ROOM HORIZONTAL BOUNDS =======
                    float clampedX = Math.min(Math.max(playerLeft, neighbor.bounds.x),
                            neighbor.bounds.x + neighbor.bounds.width - this.sprite.getWidth());
                    this.sprite.setX(clampedX);

                    return new RoomMovementDto(true, true, neighbor);
                }
            }

            if (top > roomTop && isTopNeighbor(currentRoom, neighbor)) {
                if (overlapsHorizontally(playerLeft, playerRight, neighbor.bounds.x, neighbor.bounds.x + neighbor.bounds.width)) {

                    // ======= CLAMP PLAYER X POSITION TO NEIGHBOR ROOM HORIZONTAL BOUNDS =======
                    float clampedX = Math.min(Math.max(playerLeft, neighbor.bounds.x),
                            neighbor.bounds.x + neighbor.bounds.width - this.sprite.getWidth());
                    this.sprite.setX(clampedX);

                    return new RoomMovementDto(true, true, neighbor);
                }
            }
        }

        return  new RoomMovementDto(false, false, null);
    }

// Helper methods to check adjacency

    // Changed overlap check methods with minimum overlap threshold
    private boolean overlapsVertically(float start1, float end1, float start2, float end2) {
        float overlap = Math.min(end1, end2) - Math.max(start1, start2);
        float minOverlap = 0.3f * (end1 - start1);  // minimum 30% overlap of player height
        return overlap > minOverlap;
    }

    private boolean overlapsHorizontally(float start1, float end1, float start2, float end2) {
        float overlap = Math.min(end1, end2) - Math.max(start1, start2);
        float minOverlap = 0.3f * (end1 - start1);  // minimum 30% overlap of player width
        return overlap > minOverlap;
    }

    // =======================
    // Improved adjacency check with overlap verification

    private boolean isLeftNeighbor(Room current, Room neighbor) {
        // Check left adjacency and vertical overlap of room edges
        boolean adjacentOnX = Math.abs(neighbor.bounds.x + neighbor.bounds.width - current.bounds.x) < 0.001;
        boolean verticalOverlap = overlapsVertically(current.bounds.y, current.bounds.y + current.bounds.height,
                neighbor.bounds.y, neighbor.bounds.y + neighbor.bounds.height);
        return adjacentOnX && verticalOverlap;
    }

    private boolean isRightNeighbor(Room current, Room neighbor) {
        boolean adjacentOnX = Math.abs(neighbor.bounds.x - (current.bounds.x + current.bounds.width)) < 0.001;
        boolean verticalOverlap = overlapsVertically(current.bounds.y, current.bounds.y + current.bounds.height,
                neighbor.bounds.y, neighbor.bounds.y + neighbor.bounds.height);
        return adjacentOnX && verticalOverlap;
    }

    private boolean isBottomNeighbor(Room current, Room neighbor) {
        boolean adjacentOnY = Math.abs(neighbor.bounds.y + neighbor.bounds.height - current.bounds.y) < 0.001;
        boolean horizontalOverlap = overlapsHorizontally(current.bounds.x, current.bounds.x + current.bounds.width,
                neighbor.bounds.x, neighbor.bounds.x + neighbor.bounds.width);
        return adjacentOnY && horizontalOverlap;
    }

    private boolean isTopNeighbor(Room current, Room neighbor) {
        boolean adjacentOnY = Math.abs(neighbor.bounds.y - (current.bounds.y + current.bounds.height)) < 0.001;
        boolean horizontalOverlap = overlapsHorizontally(current.bounds.x, current.bounds.x + current.bounds.width,
                neighbor.bounds.x, neighbor.bounds.x + neighbor.bounds.width);
        return adjacentOnY && horizontalOverlap;
    }



// Movement methods to update the player's sprite position
    /**
     * Moves the player upwards by updating the sprite's position and texture.
     * The movement speed and texture for walking up are used.
     */
    public RoomMovementDto moveUp(Room currentRoom) {
        float deltaY = this.speed;
        float delta = Gdx.graphics.getDeltaTime();
        RoomMovementDto dto = canMoveY(deltaY, currentRoom);
        if (dto.canMove) {
            this.sprite.setY(this.sprite.getY() + deltaY * delta);
            this.updateMovementTexture(new MovementDto(0, deltaY, PLAYER_WALKING_UP));
        }

        return dto;
    }

    /**
     * Moves the player downwards by updating the sprite's position and texture.
     * The movement speed and texture for walking down are used.
     */
    public RoomMovementDto moveDown(Room currentRoom) {
        float deltaY = -this.speed;
        float delta = Gdx.graphics.getDeltaTime();
        RoomMovementDto dto = canMoveY(deltaY, currentRoom);
        if (dto.canMove) {
            this.sprite.setY(this.sprite.getY() + deltaY * delta);
            this.updateMovementTexture(new MovementDto(0, deltaY, PLAYER_WALKING_DOWN));
        }
        return dto;
    }

    /**
     * Moves the player to the left by updating the sprite's position and texture.
     * The movement speed and texture for walking left are used.
     */
    public RoomMovementDto moveLeft(Room currentRoom) {
        float deltaX = -this.speed;
        float delta = Gdx.graphics.getDeltaTime();
        RoomMovementDto dto = canMoveX(deltaX, currentRoom);
        if (dto.canMove) {
            this.sprite.setX(this.sprite.getX() + deltaX * delta);
            this.updateMovementTexture(new MovementDto(deltaX, 0, PLAYER_WALKING_LEFT));
        }
        return dto;
    }

    /**
     * Moves the player to the right by updating the sprite's position and texture.
     * The movement speed and texture for walking right are used.
     */
    public RoomMovementDto moveRight(Room currentRoom) {
        float deltaX = this.speed;
        float delta = Gdx.graphics.getDeltaTime();
        RoomMovementDto dto = canMoveX(deltaX, currentRoom);
        if (dto.canMove) {
            this.sprite.setX(this.sprite.getX() + deltaX * delta);
            this.updateMovementTexture(new MovementDto(deltaX, 0, PLAYER_WALKING_RIGHT));
        }
        return dto;
    }

    public boolean canPay(int cost) {
        return this.balance >= cost;
    }
    public void pay(int cost) {
        this.balance -= cost;
    }

    public void repairArmor(int amount) {
        this.armor += amount;
        if (this.armor > this.maxArmor) {
            this.armor = this.maxArmor;
        }
    }

    public void changeWeapon(Weapon newWeapon) {
        this.equippedWeapon = newWeapon;
    }



    @Override
    protected boolean takeDamage(int damage){
        this.armor -= damage;

        if (armor <= 0) {

            this.health -= Math.abs(this.armor);
            this.armor = 0;

            if (this.health < 0) {
                this.health = 0;
            }
        }

        logger.info("player is taking damage: " + this.toString() + "\n damage" + damage);

        return isAlive();
    }

    @Override
    protected BarVisualDto[] getBarDto() {
        return new BarVisualDto[] {
                new BarVisualDto("health", this.health / (float) this.maxHealth),
                new BarVisualDto("armor", this.armor / (float) this.maxArmor),
                new BarVisualDto("stamina", this.stamina / (float) this.maxStamina),
        };
    }


    @Override
    public String toString() {
        return "Player {" +
                "health=" + health +
                ", armor=" + armor +
                ", stamina=" + stamina +
                ", balance=" + balance +
                '}';
    }
}
