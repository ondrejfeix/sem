package cz.cvut.fel.java.Rooms;

import java.util.ArrayList;

public class Rooms extends ArrayList<Room> {

    public Room getSpawnRoom() {
        for (Room room : this) {
            if (room.type.equals("spawn")) {
                return room;
            }
        }
        return null; // or throw an exception if no spawn room is found
    }
    public Room getPortalRoom() {
        for (Room room : this) {
            if (room.type.equals("portal")) {
                return room;
            }
        }
        return null; // or throw an exception if no fight room is found
    }
    public Room getTraderRoom() {
        for (Room room : this) {
            if (room.type.equals("trader")) {
                return room;
            }
        }
        return null; // or throw an exception if no trader room is found
    }

    public Room getRoomById(int id) {
        for (Room room : this) {
            if (room.id == id) {
                return room;
            }
        }
        return null; // or throw an exception if no room with the given ID is found
    }
}
