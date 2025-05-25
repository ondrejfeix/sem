package cz.cvut.fel.java.objects.weapons;

import lombok.Getter;

/**
 * Class represents a weapon with specific attributes
 * such as range, stamina cost, and damage. These attributes vary depending
 * on the weapon type provided during construction.
 *
 * <p>Supported weapon types include:
 * <ul>
 *   <li>dagger</li>
 *   <li>axe</li>
 *   <li>sword</li>
 * </ul>
 */
public class Weapon {

    /**
     * The type of the weapon.
     */
    @Getter private float range;

    /**
     * The stamina cost associated with using the weapon.
     */
    @Getter private float staminaCost;

    /**
     * The damage dealt by the weapon.
     */
    @Getter private int damage;

    /**
     * Constructs a weapon with specific attributes based on the provided type.
     *
     * @param type the type of the weapon ("dagger", "axe", "sword", "sword2")
     */
    public Weapon(String type) {
        if (type.equals("dagger")) {
            damage = 10;
            range = 10f;
        } else if (type.equals("axe")) {
            damage = 25;
            range = 20f;
        } else if (type.equals("sword")) {
            damage = 1000;
            range = 15f;
            staminaCost = 10;
        } else if (type.equals("sword2")) {
            damage = 30;
            range = 20f;
            staminaCost = 15;
        } else {
            // TODO LOG
        }
    }
}
