import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ParticleGenerator {
    public static void main(String[] args) throws IOException {

        if(args.length != 3){
            throw new IllegalArgumentException("Invalid parameters");
        }
        int n, l;
        double speed;
        n = Integer.parseInt(args[0]);
        l = Integer.parseInt(args[1]);
        speed = Double.parseDouble(args[2]);
        File staticAns = new File("static_rand.txt");
        staticAns.createNewFile();
        File dynamicAns = new File("dynamic_rand.txt");
        dynamicAns.createNewFile();

        FileWriter staticWriter = new FileWriter("static_rand.txt");
        FileWriter dynamicWriter = new FileWriter("dynamic_rand.txt");

        staticWriter.write(n + "\n" + l);
        staticWriter.close();
        dynamicWriter.write("   " + 0);

        double x,y, angle;
        for (int i = 0; i<n; i++) {
            x = l * Math.random();
            y = l * Math.random();
            angle = 2 * Math.PI * Math.random();
            dynamicWriter.write("\n   " + x + "   " + y + "   " + speed + "   " + angle);
        }
        dynamicWriter.close();
    }
}