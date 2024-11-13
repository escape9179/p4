package com.example.p4;

import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.Objects;

public class Player {

    private double x;
    private double y;
    private int playerNum;
    private double radius = 10.0;
    private Circle shape;

    public Player(double x, double y, int playerNum) {
        this.x = x;
        this.y = y;
        this.playerNum = playerNum;
        shape = new Circle(x, y, radius);
        shape.setEffect(new DropShadow(10.0, playerNum == 1 ? Color.WHITE : Color.BLACK));
        shape.setFill(playerNum == 1 ? Color.WHITE : Color.BLACK);
    }

    public void move(double x, double y) {
        this.x = x;
        this.y = y;
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), shape);
        tt.setToX(x);
        tt.setToY(y);
        tt.play();
    }

    public void setColor(Color color) {
        shape.setFill(color);
    }

    public Circle getShape() {
        return shape;
    }

    public Color getColor() {
        return (Color) shape.getFill();
    }

    public int getNumber() {
        return playerNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (this.getClass() == o.getClass()) {
            return ((Player) o).playerNum == playerNum; // TODO check other fields
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, playerNum, radius, shape);
    }
}
