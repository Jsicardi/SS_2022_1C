package main.java;

public class Particle {
    private int id;
    private double radius;
    private int color;
    private double x;
    private double y;

    public Particle(int id, double radius, int color, double x, double y) {
        this.id = id;
        this.radius = radius;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public double getRadius() {
        return radius;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getColor() {
        return color;
    }

    public int getId() {
        return id;
    }
}
