/**
 * GameGUI.java
 * 
 * Provides the GUI for the game. (visuals/graphics)
 * Players can be added, moved, and removed, and room-player mappings are displayed.
 * Includes a display of game rules and an optional map of room connections for reference.
 * 
 * ps.: GUI is not my strongest skill, I took many references to reach a valid enough outcome.
 * 
 * @author Bruna, Tre, Gabi
 */

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GameGUI {
    private static Game game = new Game(); //game logic
    private static JTextArea playerRoomDisplay; //displays list of players in each room
    private static JComboBox<String> roomComboBox; //dropdown for room selection (many rooms and it wouldn't fit screen properly)
    private static JTextField playerNameField; //text field for player names input

    public static void main(String[] args) {
        JFrame frame = new JFrame("Treasure Hunt"); //main window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 900); //initial size to fit every element

        JPanel mainPanel = new JPanel(new BorderLayout());

        //rules, map, and reference image panel
        JPanel rulesAndImagePanel = new JPanel(new BorderLayout());
        rulesAndImagePanel.setBorder(BorderFactory.createTitledBorder("Game Rules"));

        //rules
        JTextArea rulesArea = new JTextArea(
            "Welcome to the Treasure Hunt! Here are the rules:\n\n" +
            "1. All players must start in the Main Hall. (May only be added to Main Hall)\n" +
            "2. Players can only move to connected rooms. (You can find these connections through trial and error)\n" +
            "3. If a room exceeds 5 players, players will be redistributed to connected rooms. (rehashing)\n" +
            "4. The game ends when a player finds the treasure.\n" +
            "5. Don't die!\n\n"+
            "ps.: If you need help. Click 'Room Connections' below for path reference.\n"
        );
        rulesArea.setEditable(false);
        rulesArea.setLineWrap(true); //fits within panel
        rulesArea.setWrapStyleWord(true);
        rulesArea.setBackground(new Color(240, 240, 240)); //gray background
        rulesArea.setFont(new Font("Arial", Font.PLAIN, 14)); //text font
        rulesAndImagePanel.add(rulesArea, BorderLayout.CENTER);

        //image panel for best fit
        JPanel imagePanel = new JPanel();
        imagePanel.setBorder(BorderFactory.createTitledBorder("Room Connections Reference"));
        
        //load and display the image (initially invisible for choice of user)
        ImageIcon imageIcon = new ImageIcon("RoomConnections.jpg");
        Image scaledImage = imageIcon.getImage().getScaledInstance(220, 180, Image.SCALE_SMOOTH); //image size
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imageLabel.setVisible(false); //invisible at fist
        imagePanel.add(imageLabel);
        rulesAndImagePanel.add(imagePanel, BorderLayout.EAST);

        //"Room Connections" button
        JButton showConnectionsButton = new JButton("Room Connections");
        showConnectionsButton.addActionListener(e -> imageLabel.setVisible(true)); //unlocks image when clicked
        rulesAndImagePanel.add(showConnectionsButton, BorderLayout.SOUTH);

        mainPanel.add(rulesAndImagePanel, BorderLayout.NORTH);

        //input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Player Actions"));

        //player name input
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.add(new JLabel("Player Name:  "), BorderLayout.WEST);
        playerNameField = new JTextField();
        namePanel.add(playerNameField, BorderLayout.CENTER);
        inputPanel.add(namePanel);

        //room selector using the scroolable function mentioned
        JPanel roomPanel = new JPanel(new BorderLayout());
        roomPanel.add(new JLabel("Room Name:   "), BorderLayout.WEST);

        roomComboBox = new JComboBox<>(new String[] { //list of all possible rooms
            "Main Hall", "Kitchen", "Garden", "Bedroom", "Dining Room", 
            "Basement", "Attic", "Balcony", "Home Office", "Stairway", "Guest Room" 
        });
        roomComboBox.setPrototypeDisplayValue("Select Room Here");
        roomComboBox.setMaximumRowCount(5); //makes it scrollable if more than 5 items
        roomPanel.add(roomComboBox, BorderLayout.CENTER);
        inputPanel.add(roomPanel);

        //add, move remove buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JButton addButton = new JButton("Add Player");
        addButton.addActionListener(e -> handleAddPlayer());
        buttonPanel.add(addButton);

        JButton moveButton = new JButton("Move Player");
        moveButton.addActionListener(e -> handleMovePlayer());
        buttonPanel.add(moveButton);

        JButton deleteButton = new JButton("Delete Player");
        deleteButton.addActionListener(e -> handleDeletePlayer());
        buttonPanel.add(deleteButton);

        inputPanel.add(buttonPanel);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        //room's players list display area
        playerRoomDisplay = new JTextArea(22, 40);
        playerRoomDisplay.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(playerRoomDisplay);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        frame.add(mainPanel);

        //for game end
        game.setOnGameEndCallback(() -> {
            frame.dispose(); //close the GUI window
        });

        frame.setVisible(true);
    }

    /**
     * Adds a player to the game through the GUI
     * Displays messages if successfull
     */
    private static void handleAddPlayer() {
        String playerName = playerNameField.getText();
        String roomName = (String) roomComboBox.getSelectedItem();

        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Player name cannot be empty.");
            return;
        }

        Player player = new Player(playerName);
        game.addPlayerToRoom(player, roomName); //adds player to selected room
        updateDisplay(); //updates display list
    }

    /**
     * Moves a player from one room to another through the GUI
     * Displays messages if successfull
     */
    private static void handleMovePlayer() {
        String playerName = playerNameField.getText();
        String newRoomName = (String) roomComboBox.getSelectedItem();

        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Player name cannot be empty.");
            return;
        }

        game.movePlayer(playerName, newRoomName);
        updateDisplay();
    }

    /**
     * Removes a player from the game through the GUI
     * Displays messages if successfull
     */
    private static void handleDeletePlayer() {
        String playerName = playerNameField.getText();

        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Player name cannot be empty.");
            return;
        }

        boolean success = game.removePlayer(playerName);
        if (success) {
            JOptionPane.showMessageDialog(null, playerName + " has been removed from the game.");
            updateDisplay();
        } else {
            JOptionPane.showMessageDialog(null, "Player " + playerName + " not found.");
        }
    }

    /**
     * Updates the display area to show the current players in each room
     */
    private static void updateDisplay() {
        playerRoomDisplay.setText("");
        for (Map.Entry<String, Room> entry : game.getAllRooms().entrySet()) {
            Room room = entry.getValue();
            playerRoomDisplay.append("Players in " + room.getRoomName() + ":\n");
            for (Player player : room.getPlayersInRoom()) {
                playerRoomDisplay.append("- " + player + "\n");
            }
            playerRoomDisplay.append("\n");
        }
    }
}
