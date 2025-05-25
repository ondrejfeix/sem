package cz.cvut.fel.java.dto;

import com.badlogic.gdx.graphics.Color;

public class BarVisualDto {
    public final float barWidth = 60f;
    public float barHeight = 3f;

    public Color color;

    public int order;
    public float ratio;

    public String type;
    public float padding;

    public BarVisualDto(String type, float ration) {
        boolean createPadding = false;
        if (type.equals("health")) {
            this.type = "health";
            color = Color.RED;
            order = 2;
            this.ratio = ration;
            createPadding = true;
        } else if (type.equals("armor")) {
            this.type = "armor";
            color = Color.GRAY;
            order = 1;
            this.ratio = ration;
            createPadding = true;
        } else if (type.equals("stamina")) {
            this.type = "stamina";
            color = Color.BLUE;
            order = 1;
            this.ratio = ration;
        }

         if (createPadding) {
             this.padding = 4f;
         }
    }

    @Override
    public String toString() {
        return "BarVisualDto{" +
                "ratio=" + ratio +
                ", type='" + type + '\'' +
                '}';
    }
}
