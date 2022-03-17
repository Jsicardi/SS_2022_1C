package main.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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

        File staticAns = new File("staticAns.txt");
        staticAns.createNewFile();
        File dynamicAns = new File("dynamicAns.txt");
        dynamicAns.createNewFile();

        FileWriter staticWriter = new FileWriter("staticAns.txt");
        FileWriter dynamicWriter = new FileWriter("dynamicAns.txt");

        staticWriter.write("    " + n + "\n    " + l);
        dynamicWriter.write("   " + 0);

        double x,y, r = rMax;
        for (int i = 0; i<n; i++) {
            x = Math.random() * l;
            y = Math.random() * l;
            if (!sameR){
                r = Math.random() * rMax;
            }
            staticWriter.write("\n    " + r + "    " + 1.0000); //el weight sigue en 1
            dynamicWriter.write("\n   " + x + "   " + y);
        }
        staticWriter.close();
        dynamicWriter.close();
    }
}
