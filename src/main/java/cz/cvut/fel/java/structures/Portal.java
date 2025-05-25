package cz.cvut.fel.java.structures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Portal {
    public float posX;
    public float posY;

    public Rectangle rect;

    public Portal(float x, float y) {
        this.posX = x;
        this.posY = y;
        rect = new Rectangle(x, y, 64, 64); // Assuming a fixed size for the portal
    }

    public void renderPortal(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN); // Green color for the portal
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        shapeRenderer.end();

    }

}
