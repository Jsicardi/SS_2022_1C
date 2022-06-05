import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.System.exit;

public class ParticleGenerator {
    public static void main(String[] args) throws IOException {
        if(args.length != 5){
            System.out.println("Invalid arguments quantity");
            exit(-1);
        }

        int Nh = Integer.parseInt(args[0]);
        double rmin = Double.parseDouble(args[1]);
        double rmax = Double.parseDouble(args[2]);
        double vzi = Double.parseDouble(args[3]);
        double R = Double.parseDouble(args[4]);

        File staticAns = new File("static_rand.txt");
        staticAns.createNewFile();
        File dynamicAns = new File("dynamic_rand.txt");
        dynamicAns.createNewFile();

        FileWriter staticWriter = new FileWriter("static_rand.txt");
        FileWriter dynamicWriter = new FileWriter("dynamic_rand.txt");

        staticWriter.write(String.format(Locale.US,"%d\n%g\n%g\n", Nh+1,rmax,R));
        for(int i = 0; i <= Nh; i++){
            staticWriter.write(String.format(Locale.US,"%d\t%g\n", i, rmin));
        }
        staticWriter.close();

        dynamicWriter.write("0\n");
        double zombieAngle = Math.toRadians(Math.random() * 360);
        double zombieVx = vzi * Math.cos(zombieAngle);
        double zombieVy = vzi * Math.sin(zombieAngle);
        dynamicWriter.write(String.format(Locale.US,"0\t0\t0\t%g\t%g\n",zombieVx,zombieVy));


        List<Particle> particles = new ArrayList<Particle>();

        for(int i = 1; i <= Nh; i++){
            double distance = 0;
            double humanAngle = 0;
            double humanX = 0, humanY = 0;
            while(superpositions(humanX, humanY, particles, rmax)) {
                humanAngle = Math.toRadians(Math.random() * 360);
                distance = Math.random() * (R - rmax - 1) + 1;
                humanX = Math.cos(humanAngle) * distance;
                humanY = Math.sin(humanAngle) * distance;
            }
            particles.add(new Particle(humanX, humanY));
            dynamicWriter.write(String.format(Locale.US,"%d\t%g\t%g\t0\t0\n",i, humanX, humanY));
        }
        dynamicWriter.close();
    }

    private static class Particle {
        private double x, y;
        Particle(double x, double y) {
            this.x = x;
            this.y = y;
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

    private static boolean superpositions(double x, double y, List<Particle> particles, double rmax) {
        for (Particle particle : particles) {
            if(Math.sqrt(Math.pow((particle.getY() - y), 2) + Math.pow((particle.getX() - x), 2)) <= rmax) {
                return true;
            }
        }
        return false;
    }
}
