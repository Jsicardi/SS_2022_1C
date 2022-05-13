import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParticleGenerator {
    public static void main(String[] args) throws IOException {

        if(args.length != 7){
            throw new IllegalArgumentException("Invalid parameters");
        }
        int n;
        double lx, ly, rMax, mass, speed;
        boolean sameR;
        n = Integer.parseInt(args[0]);
        lx = Double.parseDouble(args[1]);
        ly = Double.parseDouble(args[2]);
        rMax = Double.parseDouble(args[3]);
        mass = Double.parseDouble(args[4]);
        speed = Double.parseDouble(args[5]);
        sameR = Boolean.parseBoolean(args[6]);

        File staticAns = new File("static_rand.txt");
        staticAns.createNewFile();
        File dynamicAns = new File("dynamic_rand.txt");
        dynamicAns.createNewFile();

        FileWriter staticWriter = new FileWriter("static_rand.txt");
        FileWriter dynamicWriter = new FileWriter("dynamic_rand.txt");

        staticWriter.write("    " + n + "\n    " + lx + "\n    " + ly);
        dynamicWriter.write("   " + 0);

        double x,y, r = rMax;
        boolean flag;
        List<Particle> particleList = new ArrayList<>();
        for (int i = 0; i<n; i++) {
            if (!sameR) {
                r = Math.random() * rMax;
            }
            flag = false;
            while (!flag){
                flag = true;
                x = r + ((lx - 2*r) * Math.random());
                y = r + ((ly - 2*r) * Math.random());
                for (Particle particle : particleList) {
                    if (isSuperposition(particle.getX(), particle.getY(), particle.getRadius(), x, y, r)) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    double angle = 2 * Math.PI * Math.random();
                    double vx = Math.cos(angle) * speed;
                    double vy = Math.sin(angle) * speed;
                    particleList.add(new Particle(i, r, mass, x, y, vx, vy));
                    staticWriter.write("\n    " + r + "    " + mass); //aditional property default in 1
                    dynamicWriter.write("\n   " + x + "   " + y + "    " + vx + "    " + vy);
                }
            }
        }
        staticWriter.close();
        dynamicWriter.close();
    }

    private static boolean isSuperposition(double x1, double y1, double r1, double x2, double y2,  double r2) {
        double deltaX = Math.abs(x1-x2);
        double deltaY = Math.abs(y1-y2);

        return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)) - r1 - r2 < 0;
    }
}
