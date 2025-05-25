package cz.cvut.fel.java.dto;

public class MovementDto {
    public float deltaX;
    public float deltaY;

    public String textureName;

    public MovementDto(float deltaX, float deltaY, String textureName) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.textureName = textureName;
    }
}
