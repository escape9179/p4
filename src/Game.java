/**
 *Game.java
 * 
 * This class handles the game logic. Working with players and rooms to achieve the game purpose.
 * It manages room connections (to know where the player is allowed to move to).
 * Implements game rules: health adjustments, key possession, and exceptions.
 * 
 * *****************************************************
 * It uses HashMap to manage mapping players to rooms  *
 * and to define connections between rooms.            *
 * Making lookups for data more efficient.             *
 * *****************************************************
 * 
 *@author Bruna, Tre, Gabi
 */

import java.util.*; //using specifically ArrayList, List, HashMap, Map, and HashSet
import javax.swing.JOptionPane;

public class Game {
    private Map<String, Room> rooms; //map of room names to Room objects
    private Map<Player, Room> playerRoomMap; //map of players to their current room
    private Map<String, List<String>> roomConnections; //map for room connections
    private HashSet<String> playerNames; //for unique player names
    private static final int MAX_ROOM_CAPACITY = 5; //max # of players allowed in a room, for rehashing
    private Runnable onGameEndCallback; //notifies GUI when game ends

    //Game constructor
    public Game() {
        rooms = new HashMap<>();
        playerRoomMap = new HashMap<>();
        roomConnections = new HashMap<>();
        playerNames = new HashSet<>();

        //initializes rooms (roomName, roomDescription) - for code understanding
        rooms.put("Main Hall", new Room("Main Hall", "Leads to multiple rooms, start of the game"));
        rooms.put("Garden", new Room("Garden", "Finds key to basement"));
        rooms.put("Bedroom", new Room("Bedroom", "Sleeps, regenerates health"));
        rooms.put("Kitchen", new Room("Kitchen", "Leads to multiple rooms, dangerous"));
        rooms.put("Dining Room", new Room("Dining Room", "Eats, regenerates health"));
        rooms.put("Basement", new Room("Basement", "Where the treasure lies"));
        rooms.put("Attic", new Room("Attic", "'Leaf' room, dangerous"));
        rooms.put("Balcony", new Room("Balcony", "Sees the view, regenerates health"));
        rooms.put("Home Office", new Room("Home Office", "Finds HINT, regenerates health"));
        rooms.put("Stairway", new Room("Stairway", "Leads to multiple rooms, dangerous"));

        //defines room connections, and where a player can move to depending on which room they stand
        roomConnections.put("Main Hall", Arrays.asList("Kitchen", "Dining Room", "Stairway"));
        roomConnections.put("Kitchen", Arrays.asList("Garden", "Main Hall", "Dining Room"));
        roomConnections.put("Dining Room", Arrays.asList("Kitchen", "Main Hall"));
        roomConnections.put("Stairway", Arrays.asList("Main Hall", "Bedroom", "Home Office", "Basement", "Attic"));
        roomConnections.put("Bedroom", Arrays.asList("Home Office", "Balcony", "Stairway"));
        roomConnections.put("Garden", Arrays.asList("Kitchen"));
        roomConnections.put("Basement", Arrays.asList("Stairway"));
        roomConnections.put("Attic", Arrays.asList("Stairway"));
        roomConnections.put("Balcony", Arrays.asList("Bedroom"));
        roomConnections.put("Home Office", Arrays.asList("Bedroom", "Stairway"));
    }

    /**
     * Adds a player to the 'Main Hall'. No other room to start the game from.
     * 
     * @param player   the player to add
     * @param roomName the room where the player is being added
     */
    public void addPlayerToRoom(Player player, String roomName) {
        if (!roomName.equals("Main Hall")) { //cant add to a room that is not Main Hall
            JOptionPane.showMessageDialog(null, "That's not the entrance", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (playerNames.contains(player.getName())) { //to avoid errors with the commands move or delete, no same names are allowed
            JOptionPane.showMessageDialog(null, "A player with this name already exists.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Room room = rooms.get(roomName);
        if (room != null) {
            room.addPlayer(player);
            playerRoomMap.put(player, room); //player is added to playerRoomMap
            playerNames.add(player.getName()); //ensure uniqueness
            JOptionPane.showMessageDialog(null, player.getName() + " has been added to the Main Hall.");
        }
    }

    /**
     * Moves a player from one room to another.
     * Adjusts health based on the destination room. Exceptions are implemented for a longer play game.
     * 
     * @param playerName  name of the player to move
     * @param newRoomName room to move the player to
     */
    public void movePlayer(String playerName, String newRoomName) {
        Player playerToMove = null; //null until given

        //finds the player by name in playerRoomMap
        for (Player player : playerRoomMap.keySet()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                playerToMove = player;
                break;
            }
        }

        if (playerToMove == null) { //if not found
            JOptionPane.showMessageDialog(null, "Error: " + playerName + " is not in any of the rooms.");
            return;
        }

        Room currentRoom = playerRoomMap.get(playerToMove);
        List<String> connectedRooms = roomConnections.get(currentRoom.getRoomName());

        //checks if the destination room is connected
        if (connectedRooms == null || !connectedRooms.contains(newRoomName)) {
            JOptionPane.showMessageDialog(null, "You can't move to " + newRoomName + " from " + currentRoom.getRoomName() + ".");
            return;
        }

        //prevent access to the 'Basement' without a 'key', which is the end of the game
        if (newRoomName.equals("Basement") && !playerToMove.hasKey()) {
            JOptionPane.showMessageDialog(null, playerToMove.getName() + " needs the key to enter the Basement!");
            return;
        }

        Room newRoom = rooms.get(newRoomName);
        if (newRoom != null) {
            //move the player to the new room
            currentRoom.removePlayer(playerToMove);
            newRoom.addPlayer(playerToMove);
            playerRoomMap.put(playerToMove, newRoom);

            //checks health adjustments and messages based on the new room to let user know what's happening
            switch (newRoomName) {
                case "Main Hall":
                    JOptionPane.showMessageDialog(null, "Here we are! Let's find that treasure! (Health - 0)");
                    break;
                case "Kitchen":
                    playerToMove.decreaseHealth(15);
                    JOptionPane.showMessageDialog(null, "Ouch! I burned myself while trying to make ramen. (Health - 15)");
                    break;
                case "Garden":
                    playerToMove.increaseHealth(5);
                    if (!playerToMove.hasKey()) { //if they visit the garden for a second time, there's no need for another 'key'
                        playerToMove.obtainKey();
                        JOptionPane.showMessageDialog(null, "I am so happy I have found the key! (Health + 5)");
                    } else {
                        JOptionPane.showMessageDialog(null, "What a peaceful place. (Health + 5)");
                    }
                    break;
                case "Balcony":
                    playerToMove.increaseHealth(5);
                    JOptionPane.showMessageDialog(null, "What a beautiful view! (Health + 5)");
                    break;
                case "Bedroom":
                    playerToMove.increaseHealth(10);
                    JOptionPane.showMessageDialog(null, "What a great nap! (Health + 10)");
                    break;
                case "Attic":
                    playerToMove.decreaseHealth(10);
                    JOptionPane.showMessageDialog(null, "This place is scary. (Health - 10)");
                    break;
                case "Dining Room":
                    playerToMove.increaseHealth(5);
                    JOptionPane.showMessageDialog(null, "The ramen was delicious! (Health + 5)");
                    break;
                case "Stairway":
                    playerToMove.decreaseHealth(10);
                    JOptionPane.showMessageDialog(null, "I tripped, almost fell down. (Health - 10)");
                    break;
                case "Home Office":
                    playerToMove.increaseHealth(5);
                    JOptionPane.showMessageDialog(null, "HINT: the path to your treasure is found around the flowers. (Health + 5)");
                    break;
                case "Basement":
                    JOptionPane.showMessageDialog(null, "Congratulations! " + playerToMove.getName() + " found the treasure in the Basement. Game over!");
                    endGame();
                    return;
            }

            //check if player's health is zero or below to remove them
            if (playerToMove.getHealth() <= 0) {
                removePlayer(playerToMove.getName()); //calls removePlayer on this player
                JOptionPane.showMessageDialog(null, playerToMove.getName() + " has lost died and is now removed from the game.");
                return; //exits
            }

            //after all conditions checks, player is succesfully moved
            JOptionPane.showMessageDialog(null, playerToMove.getName() + " has been moved to " + newRoomName + " with Health: " + playerToMove.getHealth());
        }
    }

    //rehashes players in a room if more than capacity by moving them to connected rooms
    private void rehashRoom(Room room) {
        List<Player> playersToMove = room.getPlayersInRoom().subList(MAX_ROOM_CAPACITY, room.getPlayersInRoom().size());
        List<String> connectedRooms = roomConnections.get(room.getRoomName());

        if (connectedRooms == null || connectedRooms.isEmpty()) { //far condition on where the whole map of rooms is filled to the max
            System.out.println("No connected rooms available for rehashing.");
            return;
        }

        for (Player player : new ArrayList<>(playersToMove)) {
            for (String connectedRoomName : connectedRooms) {
                Room connectedRoom = rooms.get(connectedRoomName);
                if (connectedRoom.getPlayersInRoom().size() < MAX_ROOM_CAPACITY) {
                    room.removePlayer(player); //removes from that room
                    connectedRoom.addPlayer(player); //adds to a connected room
                    playerRoomMap.put(player, connectedRoom); //updates new location for that player in playerRoomMap
                    System.out.println("Player " + player.getName() + " moved from " + room.getRoomName() + " to " + connectedRoom.getRoomName());
                    break; //only one player
                }
            }
        }
    }

    /**
     * Removes a player from the game
     * 
     * @param playerName name of the player to remove
     * @return if the player was successfully removed
     */
    public boolean removePlayer(String playerName) {
        Player playerToRemove = null;

        //finds the player object by name in the playerRoomMap
        for (Player player : playerRoomMap.keySet()) {
            if (player.getName().equalsIgnoreCase(playerName)) {
                playerToRemove = player;
                break;
            }
        }

        if (playerToRemove != null) { //removes from map
            Room room = playerRoomMap.remove(playerToRemove);
            if (room != null) {
                room.removePlayer(playerToRemove);
                return true;
            }
        }

        return false; //if name of player not found
    }

    public Room getRoom(String roomName) { //returns room
        return rooms.get(roomName);
    }

    public Map<String, Room> getAllRooms() { //returns all rooms map
        return rooms;
    }

    public boolean isPlayerInGame(Player player) { //checks if the player exists in the game
        return playerRoomMap.containsKey(player);
    }

    //ends the game by clearing all players and displaying a message (thought of it and found online how to implement to my code)
    public void setOnGameEndCallback(Runnable callback) {
        this.onGameEndCallback = callback;
    }

    private void endGame() {
        playerRoomMap.clear(); //clears all players
        JOptionPane.showMessageDialog(null, "All players have been removed from the game. The game is over!");
        if (onGameEndCallback != null) {
            onGameEndCallback.run(); //tells GUI to close
        }
    }
}
