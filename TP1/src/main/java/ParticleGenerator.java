package main.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class ParticleGenerator {
    public static void main(String[] args) throws IOException {

        if(args.length != 4){
            throw new IllegalArgumentException("Invalid parameters");
        }
        int n, l;
        double rMax;
        boolean sameR;
        n = Integer.parseInt(args[0]);
        l = Integer.parseInt(args[1]);
        rMax = Double.parseDouble(args[2]);
        sameR = Boolean.parseBoolean(args[3]);

        File staticAns = new File("static_rand.txt");
        staticAns.createNewFile();
        File dynamicAns = new File("dynamic_rand.txt");
        dynamicAns.createNewFile();

        FileWriter staticWriter = new FileWriter("static_rand.txt");
        FileWriter dynamicWriter = new FileWriter("dynamic_rand.txt");

        staticWriter.write("    " + n + "\n    " + l);
        dynamicWriter.write("   " + 0);

        double x,y, r = rMax;
        for (int i = 0; i<n; i++) {
            if (!sameR){
                r = Math.random() * rMax;
            }
            x = r + (((l-r) -r) * Math.random());
            y = r + (((l-r) -r) * Math.random());
            staticWriter.write("\n    " + r + "    " + 1.0000); //aditional property default in 1
            dynamicWriter.write("\n   " + x + "   " + y);
        }
        staticWriter.close();
        dynamicWriter.close();
    }
}
