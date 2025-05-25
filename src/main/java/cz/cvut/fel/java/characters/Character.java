package cz.cvut.fel.java.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import cz.cvut.fel.java.dto.BarVisualDto;
import cz.cvut.fel.java.dto.MovementDto;
import cz.cvut.fel.java.objects.weapons.Weapon;
import lombok.Getter;

import java.util.logging.Logger;




/**
 * The class represents a game character with health,
 * movement, and weapon attributes. It supports healing, taking damage,
 * updating its position and texture based on movement, and disposing
 * of resources when no longer needed.
 */
public class Character {
    private static final Logger logger = Logger.getLogger(Character.class.getName());
    /**
     * Maximum health the character can have.
     */
    protected int maxHealth;

    /**
     * Current health of the character.
     */
    protected int health;

    /**
     * Movement speed of the character.
     */
    protected float speed;

    /**
     * The weapon currently equipped by the character.
     */
    protected Weapon equippedWeapon;

    /**
     * The sprite representing the character's visual representation.
     */
    @Getter protected Sprite sprite;

    /**
     * The texture used for the character's visual representation.
     */
    protected Texture texture;

    /**
     * The name of the texture used for the character's attack.
     */
    protected String attackTextureName;

    /**
     * Checks whether the character is alive.
     *
     * @return true if the character's health is greater than 0, false otherwise.
     */
    public boolean isAlive() {
        return this.health > 0;
    }

    /**
     * Reduces the character's health by the specified damage amount.
     * If the health drops below 0, it is set to 0.
     *
     * @param damage the amount of damage to be taken
     * @return true if the character is still alive, false if the character is dead
     */
    protected boolean takeDamage(int damage) {
        logger.info("taking damage: " + damage);

        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
        return this.isAlive();
    }

    /**
     * Heals the character by the specified amount.
     * If the health exceeds the maximum health, it is set to the maximum health.
     *
     * @param heal the amount of health to be restored
     */
    protected void heal(int heal) {
        this.health += heal;
        if (this.health > this.maxHealth) {
            this.health = this.maxHealth;
        }
    }

    /**
     * Updates the character's movement and sprite texture based on input.
     *
     * @param movementDetail contains delta movement and texture name
     */
    protected void updateMovementTexture(MovementDto movementDetail) {
        float delta = Gdx.graphics.getDeltaTime();

        // Move the sprite by deltaX and deltaY scaled by delta time
        this.sprite.translate(movementDetail.deltaX * delta, movementDetail.deltaY * delta);
        // Change the sprite's texture to reflect movement direction
        this.sprite.setTexture(new Texture(movementDetail.textureName));
    }

    /**
     * Frees up resources used by the character, such as the sprite's texture.
     * Should be called when the character is no longer needed.
     */
    public void dispose() {
        if (this.sprite != null && this.sprite.getTexture() != null) {
            this.sprite.getTexture().dispose();
        }
    }

    /**
     * Gets the current position of the character as a 2D vector.
     *
     * @return the character's position (x, y)
     */
    public Vector2 getPosition() {
        return new Vector2(this.sprite.getX(), this.sprite.getY());
    }

    protected BarVisualDto[] getBarDto() {
        return new BarVisualDto[] {
                new BarVisualDto("health", this.health / (float) this.maxHealth)
        };
    }

    public void renderBar(ShapeRenderer shapeRenderer) {
        BarVisualDto[] barDto = getBarDto();

        // get self position
        Vector2 selfPosition = new Vector2(this.sprite.getX(), this.sprite.getY());
        float spriteWidth = this.sprite.getWidth();
        float spriteHeight = this.sprite.getHeight();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (BarVisualDto bar : barDto) {
            // Calculate the bar position
            float x = (float) selfPosition.x + (spriteWidth / 2f) - (bar.barWidth /2f);
            float y = (float) selfPosition.y + spriteHeight + (bar.order * bar.padding);

            shapeRenderer.setColor(bar.color);
            shapeRenderer.rect(x, y, bar.barWidth * bar.ratio, bar.barHeight);
        }
        shapeRenderer.end();
    }

    public void setPosition(float x, float y) {
        sprite.setPosition(x, y);
    }
}
