package cz.cvut.fel.java.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import cz.cvut.fel.java.objects.weapons.Weapon;
import cz.cvut.fel.java.uicomponents.texts.Text;
import lombok.Getter;

import static cz.cvut.fel.java.textureconstants.TraderTextures.*;

public class Trader {
    private final int healCost = 10;
    private final int repairCost = 10;
    private final int upgradeCost = 30;

    private final int healAmount = 10;
    private final int repairAmount = 10;

    public float resultDisplayTime;
    private final float resultDisplayTimeConstant = 2f;

    public boolean tradeMenuOpened = false;

    public Texture traderTexture = new Texture(TRADER_STAND_DOWN);
    @Getter public Sprite traderSprite = new Sprite(traderTexture);


    public final Text initialDialogueText = new Text(
            "initial dialogue",
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
    );
    public final Text tradeUnsuccessfulText = new Text(
            "Not enough money",
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight() - 300
    );

    public Text tradeResultText;

    public final Text healSucessfulText = new Text(
            "Player healed",
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 300
    );
    public final Text repairSuccessfulText = new Text(
            "Armor repaired",
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 300
    );
    public final Text upgradeSuccessfulText = new Text(
            "Weapon upgraded",
            Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + 300
    );


    public void healPlayer(Player player) {
        // Check if player can play for the healing
        if (player.canPay(healCost)) {
            player.heal(healAmount);
            player.pay(healCost);
            this.tradeResultText = healSucessfulText;
        } else {
            this.tradeResultText = tradeUnsuccessfulText;
        }
        this.resultDisplayTime = this.resultDisplayTimeConstant;
    }

    public void repairArmor(Player player) {
        if (player.canPay(repairCost)) {
            player.repairArmor(repairAmount);
            player.pay(repairCost);
            tradeResultText = repairSuccessfulText;
        } else {
            this.tradeResultText = tradeUnsuccessfulText;
        }
        this.resultDisplayTime = this.resultDisplayTimeConstant;
    }

    public void upgradeWeapon(Player player) {
        if (player.canPay(upgradeCost)) {
            Weapon upgradedWeapon = new Weapon("sword2");
            player.changeWeapon(upgradedWeapon);
            player.pay(upgradeCost);
            tradeResultText = upgradeSuccessfulText;
        } else {
            this.tradeResultText = tradeUnsuccessfulText;
        }
        this.resultDisplayTime = this.resultDisplayTimeConstant;
    }

    public void setPosition(float x, float y) {
        this.traderSprite.setPosition(x, y);
    }


}
