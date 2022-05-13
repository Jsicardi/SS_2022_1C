public class Particle {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private final double mass;
    private double charge;

    public Particle(double x,double y, double vx, double vy, double mass){
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
    }

    public Particle(double x,double y, double vx, double vy, double mass, double charge){
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.charge = charge;
    }

    public double getMass() {
        return mass;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getCharge() {
        return charge;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
