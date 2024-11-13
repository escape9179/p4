package com.example.p4;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Game extends Application {

    public static final double WIDTH = 800;
    public static final double HEIGHT = 600;
    public static final int NUM_TILES = 8;
    public static final double TILE_WIDTH = WIDTH;
    public static final double TILE_HEIGHT = HEIGHT / NUM_TILES;

    public static final Color TOP_COLOR = Color.RED;
    public static final Color BOTTOM_COLOR = Color.GREEN;

    private static Player playerOne;
    private static Player playerTwo;
    private static Player currentPlayer;
    private static List<Color> gradient;
    private static Room[] rooms;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        initialize();
        Group group = new Group();
        Scene scene = new Scene(group, WIDTH, HEIGHT, Color.BLACK);
        group.getChildren().addAll(getRooms().stream().flatMap(room -> room.getNodes().stream()).toList());
        group.getChildren().add(playerOne.getShape());
        group.getChildren().add(playerTwo.getShape());
        primaryStage.setScene(scene);
        primaryStage.show();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Welcome!\n\nThe goal is to arrange the tiles on the board to form a smooth gradient from top to bottom.\n\n" +
                "Each player has a distinct color.\n\n" +
                "Players take turns swapping colors until the gradient is complete.\n\n" +
                "Whoever puts the most tiles in the correct order wins.");
        alert.setHeaderText("Gradient (Gabi, Bruna, Tre)");
        alert.setTitle("Game");
        alert.show();
    }

    public static void initialize() {
        playerOne = new Player(0.0, 0.0, 1);
        playerTwo = new Player(0.0, 0.0, 2);
        currentPlayer = playerOne;
        gradient = createGradient(TOP_COLOR, BOTTOM_COLOR);
        rooms = createRooms();
    }

    private static Color pickRandomColor(List<Color> colors, List<Color> except) {
        Color color;
        do {
            color = colors.get((int) (Math.random() * colors.size()));
        } while (except.contains(color));
        except.add(color);
        return color;
    }

    private static Room[] createRooms() {
        Room[] rooms = new Room[gradient.size()];
        List<Color> usedColors = new ArrayList<>(Arrays.asList(gradient.get(0), gradient.get(gradient.size() - 1)));
        rooms[0] = new Room(gradient.get(0), gradient.get(0), 0, true);
        rooms[gradient.size() - 1] = new Room(gradient.get(gradient.size() - 1), gradient.get(gradient.size() - 1), (gradient.size() - 1) * TILE_HEIGHT, true);
        for (int i = 1; i < gradient.size() - 1; ++i) {
            rooms[i] = new Room(gradient.get(i), pickRandomColor(gradient, usedColors), i * TILE_HEIGHT, false);
        }
        return rooms;
    }

    private static List<Color> createGradient(Color from, Color to) {
        List<Color> colors = new ArrayList<>();
        double step = 1.0 / NUM_TILES;
        double n = 0;
        for (int i = 0; i < NUM_TILES; ++i) {
            colors.add(from.interpolate(to, n));
            n += step;
        }
        return colors;
    }

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    public static Player getPlayerOne() {
        return playerOne;
    }

    public static Player getPlayerTwo() {
        return playerTwo;
    }

    public static List<Room> getRooms() {
        return Arrays.asList(rooms);
    }
}
