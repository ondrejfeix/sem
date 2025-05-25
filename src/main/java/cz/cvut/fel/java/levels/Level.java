package cz.cvut.fel.java.levels;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.java.Rooms.Room;
import cz.cvut.fel.java.Rooms.Rooms;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;

/**
 * Represents a game level with associated data such as level number and map layout.
 */
public class Level {

    /**
     * The level number of the game level.
     */
    @Getter @Setter private int levelNumber;

    /**
     * The name of the map file associated with the level.
     */
    @Getter @Setter private String map;

    /**
     * A collection of rooms in the level.
     */
    public Rooms rooms = new Rooms();

    public boolean hasPortalRoom = false;
    public boolean hasTraderRoom = false;

    /**
     * Creates a Level instance using data from a JSON node.
     * The method reads the corresponding level JSON file based on the level number
     * provided in the input JsonNode and deserializes it into a Level object.
     *
     * @param levelData a JsonNode containing metadata (e.g. level number) about the level
     * @return a Level object populated with data from the level's JSON file
     */
    public static Level createLevel(JsonNode levelData) {
        // Get the level number from the JSON data
        int levelNumber = levelData.get("currentLevel").asInt();

        // Build the file path to the level's JSON file
        String filePath = "src/main/resources/levels/level" + levelNumber + ".json";

        // Create an ObjectMapper to read the JSON file
        ObjectMapper mapper = new ObjectMapper();

        // Initialize the Level object
        Level level = new Level();
        try {
            JsonNode root = mapper.readTree(new File(filePath));
            level.levelNumber = root.get("levelNumber").asInt();
            level.map = root.get("map").asText();

            // Extract rooms from the JSON data
            level.extractRooms(root.path("rooms"));
            // Assert that neighbors are correctly set up
            level.assertNeighbors();

            System.out.println("rooms: " + level.rooms.size());
            for (Room room : level.rooms) {
                System.out.println(room);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return level;
    }

    private void extractRooms(JsonNode roomsData) {
        if (roomsData.isArray()) {
            for (JsonNode roomData : roomsData) {
                rooms.add(new Room(roomData));
            }
        } else if (!roomsData.isMissingNode()) {
            rooms.add(new Room(roomsData));
        }
    }

    private void assertNeighbors() {
        for (Room room : rooms) {
            for (Integer neighborId : room.neighborsIds) {
                Room neighbor = rooms.getRoomById(neighborId);
                if (neighbor != null) {
                    room.neighbors.add(neighbor);
                } else {
                    System.err.println("Warning: Neighbor with ID " + neighborId + " not found for room " + room.id);
                }
            }
        }
    }


    /**
     * Returns a string representation of the Level object,
     * including the level number and map layout.
     *
     * @return string representation of the Level object
     */
    @Override
    public String toString() {
        return "Level{" +
                "levelNumber=" + levelNumber +
                ", map='" + map + '\'' +
                '}';
    }

}
