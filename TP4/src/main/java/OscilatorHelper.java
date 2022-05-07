import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class OscilatorHelper {

    private final static double EPSILON = 0.00001;

    private final double k = 10000;
    private double m = 70;
    private final double A = 1;
    private final double gamma = 100;
    private final double finalT = 5;
    private double deltaT;
    private double savingT;
    private Particle p;
    private double[][] derivatives = new double[2][6];
    private double aPrev;   // For correcting v in beeman
    private FileWriter writer;


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

    public void executeBeeman() throws IOException {
        double t = 0;
        double x = 0;
        double vPredicted = 0;
        double vCorrected = 0;
        double aNext = 0;

        // Initialize aPrev
        double prevX = Algorithms.eulerX(derivatives[0][0], derivatives[0][1], deltaT);
        double prevV = Algorithms.eulerV(derivatives[0][1], getForce(derivatives[0][0], derivatives[0][1]), m, deltaT);
        aPrev = getA(getForce(prevX, prevV));

        while(t <= finalT){
            if(t % savingT < EPSILON || t % savingT > savingT - EPSILON){
                generateOutput(p,t);
            }

            //calculate x
            x = Algorithms.beemanX(p.getX(), p.getVx(), getA(getForce(p.getX(), p.getVx())),aPrev,t,deltaT);

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
        int step = 0;
        double t = 0;
        derivatives[0][2] = getA(getForce(derivatives[0][0],derivatives[0][1]));
        derivatives[0][3] = getR3(derivatives[0][1],derivatives[0][2]);
        derivatives[0][4] = getR4(derivatives[0][2],derivatives[0][3]);
        derivatives[0][5] = getR5(derivatives[0][3],derivatives[0][4]);

        while(t <= finalT){
            if(t % savingT == 0){
                generateOutput(p,t);
            }
            //1.predict derivatives
            Algorithms.gearPredictorPredictDerivatives(derivatives[step%2], derivatives[(step+1)%2],deltaT);
            //2.evaluate

            //3. correct variables
            step ++;
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
        StringBuilder builder = new StringBuilder();
        builder.append(t).append("\n");
        builder.append(p.getX()).append("\t");
        builder.append(p.getVx()).append("\t\n");
        writer.write(builder.toString());
    }


}
