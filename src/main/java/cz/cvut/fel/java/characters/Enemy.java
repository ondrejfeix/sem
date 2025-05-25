package cz.cvut.fel.java.characters;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import cz.cvut.fel.java.dto.BarVisualDto;
import cz.cvut.fel.java.dto.MovementDto;
import cz.cvut.fel.java.objects.weapons.Weapon;



import static cz.cvut.fel.java.textureconstants.OrcTextures.*;
import static cz.cvut.fel.java.textureconstants.GoblinTextures.*;
/**
 * Represents an enemy character in the game. This class defines the common attributes and behaviors for all enemies
 * such as movement towards the player, attacking, and handling cooldowns for attacks.
 *
 */
 public class Enemy extends Character {
    // Movement and attack textures for the enemy
    /**
     * The texture name for the enemy's walking animation when moving up.
     */
    private String WALK_UP;

    /**
     * The texture name for the enemy's walking animation when moving down.
     */
    private String WALK_DOWN;

    /**
     * The texture name for the enemy's walking animation when moving left.
     */
    private String WALK_LEFT;

    /**
     * The texture name for the enemy's walking animation when moving right.
     */
    private String WALK_RIGHT;

    /**
     * The texture name for the enemy's standing position (facing down).
     */
    private String STAND_DOWN;

    /**
     * The texture name for the enemy's attack animation when attacking down.
     */
    private String ATTACK_DOWN;

    /**
     * The texture name for the enemy's attack animation when attacking up.
     */
    private String ATTACK_UP;

    /**
     * The texture name for the enemy's attack animation when attacking left.
     */
    private String ATTACK_LEFT;

    /**
     * The texture name for the enemy's attack animation when attacking right.
     */
    private String ATTACK_RIGHT;

    /**
     * The time elapsed since the last attack was performed by the enemy.
     */
    private float timeSinceLastAttack = 0f;

    /**
     * The cooldown time (in seconds) that must pass between consecutive attacks by the enemy.
     */
    private float attackCooldown;

    /**
     * The type of the enemy ("orc", "goblin").
     */
    public String type;

    /**
     * The reward given to the player for defeating this enemy.
     */
    public int reward;

    /**
     * Constructor to initialize the enemy based on its type (either "orc" or "goblin").
     * This sets up the health, speed, weapon, and textures accordingly.
     *
     * @param type Type of the enemy ("orc" or "goblin").
     */
    public Enemy(String type) {
        // Initialize enemy properties based on type
        if (type.equals("orc")) {
            this.maxHealth = 110;
            this.speed = 50f;
            this.attackCooldown = 1.5f;

            this.health = maxHealth;

            this.equippedWeapon = new Weapon("axe");
            this.reward = 10;

            // Set texture constants specific to orc
            this.WALK_UP = ORC_WALKING_UP;
            this.WALK_DOWN = ORC_WALKING_DOWN;
            this.WALK_LEFT = ORC_WALKING_LEFT;
            this.WALK_RIGHT = ORC_WALKING_RIGHT;

            this.STAND_DOWN = ORC_STAND_DOWN;

            this.ATTACK_DOWN = ORC_ATTACK_DOWN;
            this.ATTACK_UP = ORC_ATTACK_UP;
            this.ATTACK_LEFT = ORC_ATTACK_LEFT;
            this.ATTACK_RIGHT = ORC_ATTACK_RIGHT;

        } else if (type.equals("goblin")) {
            this.maxHealth = 80;
            this.speed = 80f;
            this.attackCooldown = 1f;

            this.health = maxHealth;

            this.equippedWeapon = new Weapon("dagger");
            this.reward = 5;

            // Set texture constants specific to goblin
            this.WALK_UP = GOBLIN_WALKING_UP;
            this.WALK_DOWN = GOBLIN_WALKING_DOWN;
            this.WALK_LEFT = GOBLIN_WALKING_LEFT;
            this.WALK_RIGHT = GOBLIN_WALKING_RIGHT;

            this.STAND_DOWN = GOBLIN_STAND_DOWN;

            this.ATTACK_DOWN = GOBLIN_ATTACK_DOWN;
            this.ATTACK_UP = GOBLIN_ATTACK_UP;
            this.ATTACK_LEFT = GOBLIN_ATTACK_LEFT;
            this.ATTACK_RIGHT = GOBLIN_ATTACK_RIGHT;
        } else {
            // TODO LOGGER warning
        }

        // Set initial texture for the enemy
        this.texture = new Texture(STAND_DOWN);
        this.sprite = new Sprite(this.texture);

        this.type = type;
    }

    /**
     * Calculates the movement towards the player based on the player's position.
     * The enemy moves towards the player if the player is outside the enemy's attack range.
     *
     * @param playerPosition The position of the player.
     * @return A MovementDto object that contains the movement vector and the texture name for the movement.
     */
    public MovementDto calculateMove(Vector2 playerPosition) {
        // Calculate the direction and distance to the player
        Vector2 selfPosition = this.getPosition();
        Vector2 direction = playerPosition.cpy().sub(selfPosition);

        float distance = direction.len();

        // Move towards the player if they are outside the attack range
        if (distance > this.equippedWeapon.getRange()) {
            direction.nor();

            float deltaX = direction.x * this.speed;
            float deltaY = direction.y * this.speed;

            String textureName;
            // Determine the texture name based on the movement direction
            if (Math.abs(deltaX) > Math.abs(deltaY)) {
                textureName = (direction.x > 0) ? WALK_RIGHT : WALK_LEFT;
            } else {
                textureName = (direction.y > 0) ? WALK_UP : WALK_DOWN;
            }
            return new MovementDto(deltaX, deltaY, textureName);
        }
        // If the player is within attack range, return a standing position
        return new MovementDto(0, 0, STAND_DOWN);
    }

    /**
     * Moves the enemy based on the provided movement details.
     * This method updates the sprite texture and position based on the movement.
     *
     * @param movementDetail A MovementDto object that contains movement direction and texture.
     */
    public void move(MovementDto movementDetail) {
        Gdx.app.postRunnable(() -> {
            updateMovementTexture(movementDetail);
        });
    }

    /**
     * Checks whether the player is within the enemy's attack range.
     *
     * @param playerPosition The position of the player.
     * @return true if the player is within the attack range; false otherwise.
     */
    private boolean playerInRange(Vector2 playerPosition) {
        // Calculate the distance between the player and the enemy
        float distance = playerPosition.dst(this.getPosition());

        // Check if the enemy is in range
        if (distance < this.equippedWeapon.getRange()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the enemy can attack the player.
     * The enemy can attack if the player is within range and the cooldown period has passed.
     *
     * @param playerPosition The position of the player.
     * @return true if the enemy can attack the player; false otherwise.
     */
    public boolean enemyCanAttack(Vector2 playerPosition) {
        // Check if the player is in range
        if (playerInRange(playerPosition)) {
            // Check if the attack cooldown has passed
            if (timeSinceLastAttack >= attackCooldown) {
                timeSinceLastAttack = 0f;
                return true;
            }
        }
        return false;
    }

    // TODO - temporary solution
    public void updateAttackTexture(Vector2 direction) {
        this.sprite.setTexture(new Texture(ATTACK_DOWN));
    }

/*    *//**
     * Updates the attack texture based on the direction of the attack.
     *
     * @param direction The direction of the attack.
     *//*
    public void updateAttackTexture(Vector2 direction) {
        if (Math.abs(direction.x) > Math.abs(direction.y)) {
            this.attackTextureName = (direction.x > 0) ? ATTACK_RIGHT : ATTACK_LEFT;
        } else {
            this.attackTextureName = (direction.y > 0) ? ATTACK_UP : ATTACK_DOWN;
        }
        this.sprite.setTexture(new Texture(this.attackTextureName));
    }*/

    /**
     * Updates the attack cooldown by incrementing it with the time passed since the last frame.
     */
    public void updateAttackCooldown() {
        timeSinceLastAttack += Gdx.graphics.getDeltaTime();
    }

    @Override
    protected BarVisualDto[] getBarDto() {
        return new BarVisualDto[]{
                new BarVisualDto("health", this.health / (float) this.maxHealth),
        };
    }


}
