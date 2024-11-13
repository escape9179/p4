package com.example.p4;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private Color assignedColor;
    private boolean isAnchored;
    private Rectangle shape;
    private Circle anchor;

    public Room(Color assignedColor, Color currentColor, double y, boolean isAnchored) {
        this.assignedColor = assignedColor;
        this.isAnchored = isAnchored;

        shape = new Rectangle(0, y, Game.TILE_WIDTH, Game.TILE_HEIGHT);
        shape.setFill(currentColor);
        shape.setOnMouseClicked((event) -> {
            Game.getCurrentPlayer().move(event.getX(), event.getY());
            shape.requestFocus();
        });

        shape.setOnKeyPressed(ke -> {
            ParallelTransition pt = new ParallelTransition();
            pt.getChildren().add(new FillTransition(Duration.seconds(0.2), Game.getCurrentPlayer().getShape(), Game.getCurrentPlayer().getColor(), Room.this.getColor()));
            pt.getChildren().add(new FillTransition(Duration.seconds(0.2), shape, Room.this.getColor(), Game.getCurrentPlayer().getColor()));
            pt.setOnFinished(ae -> {
                if (Room.this.getColor() != Room.this.assignedColor) { // check if the room is the correct color
                    // TODO
                }

                boolean boardInOrder = true;
                for (Room room : Game.getRooms()) {
                    if (room.getColor() != room.assignedColor) {
                        boardInOrder = false;
                        break;
                    }
                }

                if (boardInOrder) {
                    new Alert(Alert.AlertType.INFORMATION, "Player " + Game.getCurrentPlayer().getNumber() + " is the winner!").show();
                }

                if (Game.getCurrentPlayer() == Game.getPlayerOne()) Game.setCurrentPlayer(Game.getPlayerTwo());
                else Game.setCurrentPlayer(Game.getPlayerOne());

                System.out.println("It is now player " + Game.getCurrentPlayer().getNumber() + "'s turn.");

                ScaleTransition st = new ScaleTransition(Duration.seconds(.25), Game.getCurrentPlayer().getShape());
                st.setCycleCount(2);
                st.setByX(1);
                st.setByY(1);
                st.setAutoReverse(true);
                st.play();

                shape.getParent().requestFocus();
            });
            pt.play();
        });

        anchor = new Circle(shape.getWidth() / 2, shape.getHeight() / 2, 2.5, Color.BLACK);
    }

    public Color getColor() {
        return (Color) shape.getFill();
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }

    public List<Node> getNodes() {
        List<Node> nodes = new ArrayList();
        nodes.add(shape);
        if (isAnchored) nodes.add(anchor);
        return nodes;
    }
}
