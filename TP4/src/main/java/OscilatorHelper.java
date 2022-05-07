import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class OscilatorHelper {

    private final static double EPSILON = 0.00001;

    private final double k = 10000;
    private final double m;

    private final double A = 1;
    private final double gamma = 100;
    private final double finalT = 5;
    private final double deltaT;
    private final double savingT;
    private Particle p;
    private double[][] derivatives = new double[2][6];
    private final FileWriter writer;


    public OscilatorHelper(Particle particle,double deltaT,double savingT,String path) throws IOException {
        this.p = particle;
        this.m = p.getMass();
        derivatives[0][0] = p.getX();
        derivatives[0][1] = p.getVx();
        File file = new File(path);
        file.createNewFile();
        this.writer = new FileWriter(path);
        this.deltaT = deltaT;
        this.savingT = savingT;
    }

    public void executeVerlet() throws IOException {
        double t = 0;
        double x = 0;
        double v = 0;
        double xPrev = Algorithms.eulerX(derivatives[0][0], derivatives[0][1], deltaT);

        while(t <= finalT){
            System.out.printf("Current x: %g Prev x: %g V: %g F: %g\n",p.getX(), xPrev, p.getVx(),getForce(p.getX(),p.getVx()));
            x = Algorithms.verletX(p.getX(), xPrev,getForce(p.getX(),p.getVx()),p.getMass(),deltaT);
            System.out.println(x);

            if(t != 0){
                v = Algorithms.verletV(x, xPrev, deltaT);
                p.setVx(v);
            }

            if(t % savingT < EPSILON || t % savingT > savingT - EPSILON){
                if(t != 0) {
                    generateOutput(p, t);
                }
                else {
                    generateOutput(p, t);
                }
            }
            xPrev = p.getX();
            p.setX(x);

            t+=deltaT;
            t = round(t,2);
        }
        writer.close();
    }

    public void executeBeeman() throws IOException {
        double t = 0;
        double x;
        double vPredicted;
        double vCorrected;
        double aNext;

        // Initialize aPrev
        double prevX = Algorithms.eulerX(derivatives[0][0], derivatives[0][1], deltaT);
        double prevV = Algorithms.eulerV(derivatives[0][1], getForce(derivatives[0][0], derivatives[0][1]), m, deltaT);
        // For correcting v in beeman
        double aPrev = getA(getForce(prevX, prevV));

        while(t <= finalT){
            if(t % savingT < EPSILON || t % savingT > savingT - EPSILON){
                generateOutput(p,t);
            }

            //calculate x
            x = Algorithms.beemanX(p.getX(), p.getVx(), getA(getForce(p.getX(), p.getVx())), aPrev,t,deltaT);

            //calculate v
            vPredicted = Algorithms.beemanPredictionV(p.getVx(),getA(getForce(x,p.getVx())), aPrev,deltaT);
            aNext = getA(getForce(x,vPredicted));
            vCorrected = Algorithms.beemanV(p.getVx(), aPrev,getA(getForce(p.getX(), p.getVx())), aNext,deltaT);

            aPrev = getA(getForce(p.getX(), p.getVx()));

            //update particle
            p.setVx(vCorrected);
            p.setX(x);
            t+=deltaT;
            t = round(t,2);
        }
        writer.close();
    }

    public void executeGear() throws IOException {

        double[] alphas = {3.0/16, 251.0/360, 1, 11.0/18, 1.0/6, 1.0/60};
        double deltaA, deltaR2;
        double t = 0;
        derivatives[0][2] = getA(getForce(derivatives[0][0],derivatives[0][1]));
        derivatives[0][3] = getR3(derivatives[0][1],derivatives[0][2]);
        derivatives[0][4] = getR4(derivatives[0][2],derivatives[0][3]);
        derivatives[0][5] = getR5(derivatives[0][3],derivatives[0][4]);

        while(t <= finalT){
            if(t % savingT < EPSILON || t % savingT > savingT - EPSILON){
                generateOutput(p,t);
            }

            //1.predict derivatives
            Algorithms.gearPredictorPredictDerivatives(derivatives[0], derivatives[1],deltaT);

            //2.evaluate
            deltaA = getA(getForce(derivatives[1][0], derivatives[1][1])) - derivatives[1][2];
            deltaR2 = deltaA * Math.pow(deltaT,2) / 2;

            //3. correct variable
            Algorithms.gearPredictorCorrectDerivatives(derivatives[0], derivatives[1], alphas, deltaR2, deltaT);

            //4. update particle
            p.setVx(derivatives[0][1]);
            p.setX(derivatives[0][0]);

            t += deltaT;
            t = round(t,2);
        }
        writer.close();
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    private double getForce(double x, double v){
        return - k*x - gamma*v;
    }
    private double getR3(double r1, double r2) {
        return (-k*r1 - gamma*r2)/m;
    }
    private double getR4(double r2, double r3){
        return (-k*r2 - gamma*r3)/m;
    }
    private double getR5(double r3, double r4){
        return (-k*r3 - gamma*r4)/m;
    }


    private double getA(double f){
        return f / m;
    }

    private void generateOutput(Particle p, double t) throws IOException {
        String toWrite = t + "\n" +
                p.getX() + "\t" +
                p.getVx() + "\t\n";
        writer.write(toWrite);
    }


}
