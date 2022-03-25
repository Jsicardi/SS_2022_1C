import main.java.Particle;

public class ExtendedParticle extends Particle {
    private double v;
    private double theta;

    public ExtendedParticle(int id, double x, double y,double v, double theta) {
        super(id, 0, 0);
        this.x = x;
        this.y = y;
        this.v = v;
        this.theta = theta;
    }


    public double getTheta() {
        return theta;
    }

    public double getV() {
        return v;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public void setV(double v) {
        this.v = v;
    }
}
