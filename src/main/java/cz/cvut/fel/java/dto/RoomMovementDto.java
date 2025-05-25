package cz.cvut.fel.java.dto;

import cz.cvut.fel.java.Rooms.Room;

public class RoomMovementDto {
    public boolean canMove;
    public boolean switchRoom;
    public Room nextRoom;

    public RoomMovementDto(boolean canMove, boolean switchRoom, Room nextRoom) {
        this.canMove = canMove;
        this.switchRoom = switchRoom;
        this.nextRoom = nextRoom;
    }
}
