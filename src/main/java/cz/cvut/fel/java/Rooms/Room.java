package cz.cvut.fel.java.Rooms;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.fasterxml.jackson.databind.JsonNode;
import cz.cvut.fel.java.characters.Dragon;
import cz.cvut.fel.java.characters.Enemies;
import cz.cvut.fel.java.characters.Enemy;
import cz.cvut.fel.java.characters.Trader;
import cz.cvut.fel.java.dto.DimensionsDto;
import cz.cvut.fel.java.structures.Portal;

import java.util.ArrayList;

public class Room {
    public Rectangle bounds;
    public DimensionsDto dimensions;
    public int id;
    public String type;

    public ArrayList<Integer> neighborsIds;
    public Rooms neighbors = new Rooms(); // List of neighboring rooms, if applicable

    public Rectangle curtain;

    public boolean active = false;
    public boolean visited = false; // Indicates if the room has been visited
    public boolean prepared = true; // Indicates if the room is prepared for interaction

    public float[] spawnPoint; // Coordinates for spawn point, if applicable
    public Enemies enemies;
    public Portal portal;
    public Trader trader;
    public Dragon boss;

    public Room(JsonNode roomData) {
        this.id = roomData.get("id").asInt();
        this.type = roomData.get("type").asText();

        handleType(roomData);

        defineDimensions(roomData.get("dimensions"));
        this.bounds = defineRectangle();
        this.curtain = defineRectangle();

        extractNeighborsIds(roomData.get("neighbors"));
    }

    private void handleType(JsonNode roomData) {
        if (type.equals("spawn")) {
            defineSpawnRoom(roomData.get("type_specific"));
        } else if (type.equals("fight")) {
            defineFightRoom(roomData.get("type_specific"));
        } else if (type.equals("portal")) {
            definePortalRoom(roomData.get("type_specific"));
        } else if (type.equals("trader")) {
            defineTraderRoom(roomData.get("type_specific"));
        } else if (type.equals("boss")) {
            defineBossRoom(roomData.get("type_specific"));
        }
    }

    private void defineDimensions(JsonNode roomData) {
        this.dimensions = new DimensionsDto(
            (float) roomData.get("width").asDouble(),
            (float) roomData.get("height").asDouble(),
            (float) roomData.get("posX").asDouble(),
            (float)roomData.get("posY").asDouble()
        );
    }

    private Rectangle defineRectangle() {
        return new Rectangle(
            dimensions.x,
            dimensions.y,
            dimensions.width,
            dimensions.height
        );
    }

    private void extractNeighborsIds(JsonNode neighborsData) {
        this.neighborsIds = new ArrayList<>();
        for (JsonNode neighbor : neighborsData) {
            this.neighborsIds.add(neighbor.asInt());
        }
    }

    private void defineSpawnRoom(JsonNode roomData) {

        JsonNode spawnPointData = roomData.get("spawnPoint");
        this.spawnPoint = new float[]{
                (float) spawnPointData.get("posX").asDouble(),
                (float) spawnPointData.get("posY").asDouble()
        };

        this.visited = true;
        this.prepared = false; // Spawn room is not prepared for interaction
        this.active = false;
    }

    private void defineFightRoom(JsonNode roomData) {
        // Define specific properties for fight rooms if needed
        this.visited = false; // Fight rooms are not visited initially
        this.prepared = true; // Fight rooms are prepared for interaction
        this.active = false; // Fight rooms are not active initially

        enemies = new Enemies();
        System.out.println(roomData);
        JsonNode enemiesData = roomData.get("enemies");
        if (enemiesData.isArray()) {
            for (JsonNode enemyData : enemiesData) {
                float enemyPosX = (float) enemyData.get("posX").asDouble();
                float enemyPosY = (float) enemyData.get("posY").asDouble();

                Enemy newEnemy = new Enemy(enemyData.get("type").asText());
                newEnemy.setPosition(enemyPosX, enemyPosY);
                enemies.addEnemy(newEnemy);
            }
        } else if (!enemiesData.isMissingNode()) {
            float enemyPosX = (float) enemiesData.get("posX").asDouble();
            float enemyPosY = (float) enemiesData.get("posY").asDouble();

            Enemy newEnemy = new Enemy(enemiesData.get("type").asText());
            newEnemy.setPosition(enemyPosX, enemyPosY);
            enemies.addEnemy(newEnemy);
        }
    }

    private void definePortalRoom(JsonNode roomData) {
        // Define specific properties for portal rooms if needed
        this.visited = false; // Portal rooms are not visited initially
        this.prepared = false; // Portal rooms are prepared for interaction
        this.active = false; // Portal rooms are not active initially

        // Additional portal-specific logic can be added here
        JsonNode portalData = roomData.get("portal");
        this.portal = new Portal(
                (float) portalData.get("posX").asDouble(),
                (float) portalData.get("posY").asDouble()
        );
    }

    private void defineTraderRoom(JsonNode roomData) {
        // Define specific properties for trader rooms if needed
        this.visited = false; // Trader rooms are not visited initially
        this.prepared = false; // Trader rooms are prepared for interaction
        this.active = false; // Trader rooms are not active initially

        // Additional trader-specific logic can be added here
        JsonNode traderData = roomData.get("trader");
        this.trader = new Trader();
        trader.setPosition(
            (float) traderData.get("posX").asDouble(),
            (float) traderData.get("posY").asDouble()
        );
    }

    private void defineBossRoom(JsonNode roomData) {
        // Define specific properties for boss rooms if needed
        this.visited = false; // Boss rooms are not visited initially
        this.prepared = true; // Boss rooms are prepared for interaction
        this.active = false; // Boss rooms are not active initially

        // Additional boss-specific logic can be added here
        JsonNode bossData = roomData.get("boss");
        this.boss = new Dragon();
        this.boss.setPosition(
            (float) bossData.get("posX").asDouble(),
            (float) bossData.get("posY").asDouble()
        );
    }


    public void renderCurtain(ShapeRenderer shapeRenderer) {
        // Check if the room is prepared but
        if (!visited) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(curtain.x, curtain.y, curtain.width, curtain.height);
            shapeRenderer.end();
        }
    }


    public void checkFightRoomStatus() {
        if (enemies.currentEnemies.isEmpty()){
            this.prepared = false;
            this.active = false;
        }
    }



    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", dimensions=" + dimensions +
                ", bounds=" + bounds +
                ", neighborsIds=" + neighborsIds +
                '}';
    }
}
