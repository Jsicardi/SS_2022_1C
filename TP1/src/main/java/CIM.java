package main.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

public class CIM {

    public static void main(String[] args) throws IOException {

        if(args.length != 5 && args.length != 6){
            throw new IllegalArgumentException("Invalid parameters");
        }
        File staticFile = new File(args[0]);
        Scanner myStaticReader = new Scanner(staticFile);
        File dynamicFile = new File(args[1]);
        Scanner myDynamicReader = new Scanner(dynamicFile);
        String path = args[2];

        double rMax = 0;
        int N = 0;
        int L = 0;
        int M = 0;

        double rc = Double.parseDouble(args[3]);
        boolean contourProp = Boolean.parseBoolean(args[4]);


        //check is parameter M exists
        if(args.length == 6){
            M = Integer.parseInt(args[4]);
        }

        //parse the quantity of particles(N) and the size of the square grid(L)
        if(myStaticReader.hasNextLine()){
            N = Integer.parseInt(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }
        if(myStaticReader.hasNextLine()){
            L = Integer.parseInt(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }

        List<Particle> particles = new ArrayList<>();

        rMax = CIMHelper.getStaticProperties(myStaticReader, particles);

        if(particles.size() != N){
            System.out.println("Invalid quantity of particles");
            exit(-1);
        }
        myStaticReader.close();

        if(myDynamicReader.hasNextLine()){
            myDynamicReader.nextLine(); //skipping first line as CIM use an unique snapshot of particle's position
        }

        CIMHelper.getDynamicProperties(myDynamicReader,particles);

        CIMMatrix matrix = new CIMMatrix(L,rMax,rc,particles,M);

        System.out.println(particles);

        HashMap<Integer,List<Integer>> neighbours = matrix.getNeighbours(contourProp);

        CIMHelper.generateOutputFile(neighbours,path);

    }

}
