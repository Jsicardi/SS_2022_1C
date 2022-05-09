import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RadiationHelper {
    // Constant parameters
    private final double k = Math.pow(10, 10);
    private final double q = Math.pow(10, -19);
    private final double m = Math.pow(10, -27);
    private final double d = Math.pow(10, -8);
    private final double n = Math.pow(16, 2);
    private final static double EPSILON = 0.0000000001;

    // Specific parameters for exercise
    private double t = 0;
    private final double l;
    private final double v0;     // In range [5 * 10^3, 5 * 10^4]
    private final double deltaT;
    private final double savingT;
    private final Particle particle;      // The one that moves
    private List<Particle> particles;   // The particles that make the structure
    private final FileWriter writer;

    // Parameters for integration methods
    private final double[][]derivativesX = new double[2][6];   // [] = ld,new ; [] = derivative order
    private final double[][]derivativesY = new double[2][6];   // [] = ld,new ; [] = derivative order
    private final double[] alphas = {3.0/20, 251.0/360, 1, 11.0/18, 1.0/6, 1.0/60};
    private double deltaAX, deltaR2X;
    private double deltaAY, deltaR2Y;

    public RadiationHelper(double v0, double y0, double deltaT, double savingT,String path, boolean randomY0) throws IOException {
        this.v0 = v0;
        this.deltaT = deltaT;
        this.savingT = savingT;

        this.l = Math.sqrt(n) * d - d;

        if (randomY0) {
            Random r = new Random();
            y0 = -d + (d - -d) * r.nextDouble();
        }
        this.particle = new Particle(0,y0 + l/2, v0, 0, m, q);

        generateMatrix();

        // Initialize variables for Gear
        derivativesX[0][0] = particle.getX();
        derivativesY[0][0] = particle.getY();
        derivativesX[0][1] = particle.getVx();
        derivativesY[0][1] = particle.getVy();
        double[] forces = getAllEleForce();
        derivativesX[0][2] = getA(forces[0]);
        derivativesY[0][2] = getA(forces[1]);

        for (int i = 3; i < 6; i++) {
            derivativesX[0][i] = 0;
            derivativesY[0][i] = 0;
        }

        // Initialize file writer
        File file = new File(path);
        file.createNewFile();
        this.writer = new FileWriter(path);

        execute();
        writer.close();
    }


    private void execute() throws IOException {
        double[] forces;
        double auxT;
        while (!cutCondition()) {
            if(t % savingT < EPSILON || t % savingT > savingT - EPSILON) {
                auxT = round(t,6);
                generateOutput(particle, auxT);
            }

            //1.predict derivatives
            Algorithms.gearPredictorPredictDerivatives(derivativesX[0], derivativesX[1],deltaT);
            Algorithms.gearPredictorPredictDerivatives(derivativesY[0], derivativesY[1],deltaT);

            //2.evaluate
            forces = getAllEleForce();
            System.out.println("Forces: " + forces[0] + " " + forces[1]);
            deltaAX = getA(forces[0]) - derivativesX[1][2];
            deltaR2X = deltaAX * Math.pow(deltaT,2) / 2;
            deltaAY = getA(forces[1]) - derivativesY[1][2];
            deltaR2Y = deltaAY * Math.pow(deltaT,2) / 2;

            //3. correct variable
            Algorithms.gearPredictorCorrectDerivatives(derivativesX[0], derivativesX[1], alphas, deltaR2X, deltaT);
            Algorithms.gearPredictorCorrectDerivatives(derivativesY[0], derivativesY[1], alphas, deltaR2Y, deltaT);

            //4. update particle
            System.out.println("Old x: " + particle.getX() + " Old y: " + particle.getY());
            System.out.println("Old vx: " + particle.getVx() + " Old vy: " + particle.getVy());
            particle.setVx(derivativesX[0][1]);
            particle.setX(derivativesX[0][0]);
            particle.setVy(derivativesY[0][1]);
            particle.setY(derivativesY[0][0]);
            System.out.println("New x: " + particle.getX() + " New y: " + particle.getY());
            System.out.println("New vx: " + particle.getVx() + " New vy: " + particle.getVy());

            t += deltaT;
        }
    }


    private void generateMatrix() {
        particles = new ArrayList<>();
        int x, y;
        int positive = 1;
        for (int i = 0; i < n; i++) {
            y = i / (int) Math.sqrt(n);
            x = (i % (int) Math.sqrt(n)) + 1;
            positive = y%2 == 0 ?  (i%2)*2-1 :  ((i+1)%2)*2-1;
            particles.add(new Particle(x * d, y * d, 0, 0, m, positive * q));
        }
    }


    private double getDistBetweenPoints(double x1, double y1, double x2, double y2) {
        double ac = Math.abs(y2 - y1);
        double cb = Math.abs(x2 - x1);

        return Math.hypot(ac, cb);
    }


    private double getA(double f){
        return f / m;
    }


    private void addEleForce(double[] forces, Particle p1, Particle p2) {
        double angle = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
        double dist = getDistBetweenPoints(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        double force =  k * p1.getCharge() * p2.getCharge() / Math.pow(dist, 2);
        double forceX = force * Math.cos(angle);
        double forceY = force * Math.sin(angle);
        forces[0] += forceX;
        forces[1] += forceY;
    }


    private double[] getAllEleForce() {
        double[] forces = {0, 0};
        for (Particle p2 : particles) {
            addEleForce(forces, particle, p2);
        }
        return forces;
    }


    /*
    private double getPotEnergy(Particle p1, Particle p2) {
        double dist = getDistBetweenPoints(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        return k * p1.getCharge() * p2.getCharge() / dist;
    }


    private double getAllPotEnergy() {
        double sum = 0;
        for (Particle p2 : particles) {
            sum += getPotEnergy(particle, p2);
        }
        return sum;
    }
    */

    private boolean cutCondition() {
        double x = particle.getX();
        double y = particle.getY();
        double dist = l + 2 * d;
        if (x < 0 || x > dist || y < 0-d || y > dist) {
            System.out.println("x: " + x + " y: " + y);
            System.out.println("x < 0:" + (x < 0) + " x > dist:" + (x > dist) + " y < 0:" + (y < 0) + " y > dist:" + (y > dist));
            System.out.println("Se va del recinto");
            return true;
        }
        for (Particle otherParticle: particles) {
           if (getDistBetweenPoints(otherParticle.getX(), otherParticle.getY(), particle.getX(),  particle.getY()) < 0.01 * d) {
               System.out.println("Absorbida por partícula");
               return true;
           }
        }
        return false;
    }

    private void generateOutput(Particle p, double t) throws IOException {
        String toWrite = t + "\n" +
                p.getX() + "\t" +
                p.getY() + "\t" +
                p.getVx() + "\t" +
                p.getVy() + "\t\n";
        writer.write(toWrite);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
