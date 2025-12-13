package org.example;

public class Point {
    private int id;
    private double x;
    private double y;

    public Point(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public double distanceTo(Point p) {
        double dx = this.x - p.x;
        double dy = this.y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
