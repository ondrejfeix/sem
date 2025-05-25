package cz.cvut.fel.java.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import cz.cvut.fel.java.dto.BarVisualDto;

import java.util.logging.Logger;

import static cz.cvut.fel.java.textureconstants.DragonTexture.*;

public class Dragon extends Character {
    private float timeBetweenAttacks = 0f;
    private final float betweenAttacksCooldown = 3f;

    // Time that the attack square is shown before the attack
    private float timeToAttack = 0;
    private final float timeToAttackCooldown = 1.5f;

    private boolean attackPrepared = false;

    private final int attackDamage = 30;
    private Rectangle attackSquare;

    private static final Logger logger = Logger.getLogger(Dragon.class.getName());


    public Dragon() {
        // TODO - health
        this.health = 150;
        this.maxHealth = 500;

        this.texture = new Texture(DRAGON_STAND_DOWN);
        this.sprite = new Sprite(texture);
    }

    public void attack(Player player) {
        if ((timeBetweenAttacks > betweenAttacksCooldown) && !attackPrepared) {
            prepareAttack(player.getPosition());
        } else {
            timeBetweenAttacks += Gdx.graphics.getDeltaTime();
        }

        if (attackPrepared) {
            if (timeToAttack > timeToAttackCooldown) {
                logger.info("Dragon is attacking the player");
                attackPlayer(player);
            } else {
                timeToAttack += Gdx.graphics.getDeltaTime();
            }
        }
    }


    private void prepareAttack(Vector2 playerPosition) {
        logger.info("Dragon is preparing to attack");
        defineAttackSquare(playerPosition);
        timeToAttack = 0;
        attackPrepared = true;
    }

    private void attackPlayer(Player player) {
        if (playerInAttackSquare(player)) {
            logger.info("Dragon has attacked the player");
            player.takeDamage(attackDamage);
            attackSquare = null;
        }
        timeBetweenAttacks = 0f;
        attackPrepared = false;
    }

    private void defineAttackSquare(Vector2 playerPosition) {
        logger.info("Defining attack square");

        float squareSize = 64;
        float x = playerPosition.x - squareSize / 2f;
        float y = playerPosition.y - squareSize / 2f;
        attackSquare = new Rectangle(x, y, squareSize, squareSize);
    }

    // 🔽 Optional helper method
    private boolean playerInAttackSquare(Player player) {
        return attackSquare != null && attackSquare.contains(player.getPosition());
    }

    public void renderAttackSquare(ShapeRenderer shapeRenderer) {
        if (attackPrepared) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);

            shapeRenderer.rect(attackSquare.x, attackSquare.y, attackSquare.width, attackSquare.height);
            shapeRenderer.end();
        }
    }

    @Override
    protected BarVisualDto[] getBarDto() {
        return new BarVisualDto[] {
                new BarVisualDto("health", this.health / (float) this.maxHealth),
        };
    }

}
