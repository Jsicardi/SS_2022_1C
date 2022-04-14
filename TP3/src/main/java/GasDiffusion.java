import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GasDiffusion {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        if(args.length != 6){
            throw new IllegalArgumentException("Invalid parameters");
        }

        File staticFile = new File(args[0]);
        Scanner myStaticReader = new Scanner(staticFile);
        File dynamicFile = new File(args[1]);
        Scanner myDynamicReader = new Scanner(dynamicFile);
        String positionsPath = args[2];
        String fpPath = args[3];

        int N = 0;
        double width = 0;
        double height = 0;
        double openingLength = Double.parseDouble(args[4]);
        double epsilon = Double.parseDouble(args[5]);

        //parse the quantity of particles(N) and the size of the grid(width*height)
        if(myStaticReader.hasNextLine()){
            N = Integer.parseInt(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }
        if(myStaticReader.hasNextLine()){
            width = Double.parseDouble(myStaticReader.nextLine().trim().replaceAll("\\s+", ""))*2;
        }
        if(myStaticReader.hasNextLine()){
            height = Double.parseDouble(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }

        List<Particle> particles = new ArrayList<>();

        GasDiffusionHelper.getStaticProperties(myStaticReader, particles);

        myDynamicReader.nextLine();

        GasDiffusionHelper.getDynamicProperties(myDynamicReader,particles);

        GasDiffusionMethod method = new GasDiffusionMethod(height,width,openingLength,particles,epsilon);

        GasDiffusionHelper.createOutputFiles(positionsPath,fpPath);

        method.executeMethod();

        long endTime = System.currentTimeMillis();
        System.out.printf("Time: %d ms\n", endTime - startTime);

    }

}
