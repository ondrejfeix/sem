package cz.cvut.fel.java.dto;

import com.badlogic.gdx.math.Vector2;
import cz.cvut.fel.java.characters.Enemy;

public class AttackDto {
     public Vector2 direction;

     public Enemy enemyToAttack;
     public Enemy attacker;

     public AttackDto(Vector2 direction, Enemy enemyToAttack, boolean isAttacker) {
          this.direction = direction;

          if (isAttacker) {
               this.attacker = enemyToAttack;
          } else {
               this.enemyToAttack = enemyToAttack;
          }
     }

}
