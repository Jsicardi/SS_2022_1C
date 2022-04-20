public class Particle {
    private final int id;
    private final double radius;
    private final double mass;
    private double x;
    private double y;
    private double vx;
    private double vy;

    public Particle(int id, double radius, double mass, double x, double y, double vx, double vy) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

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
                ", vx=" + vx +
                ", vy=" + vy +
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

    public double getVy() {
        return vy;
    }

    public double getVx() {
        return vx;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

}
