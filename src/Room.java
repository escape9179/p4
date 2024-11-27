/**
 *Room.java
 * 
 * This class represents a room in the game. Each room has a name, description of its activities, and a player tracker.
 * It allows adding and removing players from each room, while it tracks who is in it.
 * 
 *@author Bruna, Tre, Gabi
 */

import java.util.*; //ArrayList, and List needed to be used

public class Room {
    private String roomName; //name given to the room
    private String roomDescription; //its description
    private List<Player> playersInRoom; //allows for checking the players in each room

    public Room(String roomName, String roomDescription) {
        this.roomName = roomName;
        this.roomDescription = roomDescription;
        this.playersInRoom = new ArrayList<>();
    }

    public String getRoomName() { //returns room name
        return roomName;
    }

    //returns a list of players currently in the room
    public List<Player> getPlayersInRoom() {
        return playersInRoom;
    }

    /**
     *adds a new player to the room, making sure the same name doesn't already exists 
     *to avoid moving and/or deleting errors
     *@param player to add
     */
    public void addPlayer(Player player) {
        if (!playersInRoom.contains(player)) {
            playersInRoom.add(player);
        }
    }

    /**
     *removes a player from the room
     *@param player to remove
     */
    public void removePlayer(Player player) {
        playersInRoom.remove(player);
    }
}
