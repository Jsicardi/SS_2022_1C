package main.java;

public class Particle {
    private final int id;
    private final double radius;
    private final double mass;
    private double x;
    private double y;

    public Particle(int id, double radius, double mass) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "id=" + id +
                ", radius=" + radius +
                ", mass=" + mass +
                ", x=" + x +
                ", y=" + y +
                '}';
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

    public double getMass() {
        return mass;
    }

    public int getId() {
        return id;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }

    public double getDistance(Particle particle, int L,boolean withContour){
        double deltaX = Math.abs(x-particle.getX());
        double deltaY = Math.abs(y-particle.getY());

        if(withContour) {
            deltaX -= deltaX > ((L*1.0f) / 2 ) ? L : 0;
            deltaY -= deltaY > ((L*1.0f) / 2) ? L : 0;
        }

        return Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY,2)) - radius - particle.getRadius();
    }

}
