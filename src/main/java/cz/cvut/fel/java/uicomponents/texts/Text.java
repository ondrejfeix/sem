package cz.cvut.fel.java.uicomponents.texts;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Text {
    private BitmapFont font;
    private String label;
    private float x;
    private float y;
    private GlyphLayout layout = new GlyphLayout();


    public Text(String text, int gameWidth, int gameHeight) {
        this.label = text;
        this.font = new BitmapFont();
        this.font.getData().setScale(3);
        this.font.setColor(1f, 0.157f, 0.078f, 1f);

        // Update the position of the text
        updateTextPosition(gameWidth, gameHeight);
    }

    public void renderText(SpriteBatch batch) {
        this.font.draw(batch, this.label, this.x, this.y);
    }

    public void updateTextPosition(int gameWidth, int gameHeight) {
        // Use GlyphLayout to measure the text size
        layout.setText(this.font, this.label);

        float textWidth = layout.width;

        this.x = (gameWidth - textWidth) /2f;
        this.y = gameHeight - (gameHeight /4f);
    }

    public void dispose() {
        this.font.dispose();
    }
}
