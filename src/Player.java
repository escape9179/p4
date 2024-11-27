/**
 *Player.java
 * 
 * This class represents a player in the game. Each player has a name, a health tracker, and the possible access to a 'key'.
 * Health (0-100) is adjusted throughout the game based on each room's effect on the player.
 * Key is given to a player once they enter the 'Garden' room which is where the key is hiding.
 * 
 *@author Bruna, Tre, Gabi
 */

public class Player {
    private String name;
    private int health = 100; //initiates health of each player at 100
    private boolean hasKey = false; //tracks if player has the key, initiates at false

    public Player(String name) {
        this.name = name;
    }

    public String getName() { //returns player's name
        return name;
    }

    public int getHealth() { //returns player's health 
        return health;
    }

    public boolean hasKey() { //checks if has key
        return hasKey;
    }

    public void obtainKey() { //calling this method sets the hasKey value to true of a specific player
        this.hasKey = true;
    }

    //adds specified amount to health, max health at 100
    public void increaseHealth(int points) {
        this.health = Math.min(this.health + points, 100);
    }

    //subtracts specified points from health, min health at 0
    public void decreaseHealth(int points) {
        this.health = Math.max(this.health - points, 0);
    }

    @Override //string representation of player, where we also list their health and wheter they have a key
    public String toString() {
        String result = name + " (Health: " + health + ")";
        if (hasKey) {
            result += " + Key";
        }
        return result;
    }
}
