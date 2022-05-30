import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

        double humanX = 0;
        double humanY = 0;
        double distance = 0;
        for(int i = 1; i <= Nh; i++){
            humanX = (Math.random() * 2 - 1) * R;
            humanY = (Math.random() * 2 - 1) * R;
            distance = Math.pow(humanX+rmin, 2) + Math.pow(humanY+rmin, 2);
            while(distance > Math.pow(R-rmin,2) || distance < Math.pow(1+rmin,2)){
                humanX = (Math.random() * 2 - 1) * R;
                humanY = (Math.random() * 2 - 1) * R;
                distance = Math.pow(humanX+rmin, 2) + Math.pow(humanY+rmin, 2);
            }
            dynamicWriter.write(String.format(Locale.US,"%d\t%g\t%g\t0\t0\n",i,humanX,humanY));
        }
        dynamicWriter.close();
    }
}
