import java.util.Objects;

public class Particle {
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double r;
    private double energy;

    public Particle(double x, double y,double vx, double vy, double r, double energy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.r = r;
        this.energy = energy;
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

    public void setR(double r) {
        this.r = r;
    }

    public double getR() {
        return r;
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy(double energy) {
        this.energy = energy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Double.compare(particle.x, x) == 0 && Double.compare(particle.y, y) == 0 && Double.compare(particle.vx, vx) == 0 && Double.compare(particle.vy, vy) == 0 && Double.compare(particle.r, r) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, vx, vy, r);
    }

    @Override
    public String toString() {
        return "Particle{" +
                "x=" + x +
                ", y=" + y +
                ", vx=" + vx +
                ", vy=" + vy +
                ", r=" + r +
                '}';
    }
}
