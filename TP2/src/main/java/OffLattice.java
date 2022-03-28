import java.io.File;
import java.io.IOException;
import java.util.*;

public class OffLattice {

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();

        if(args.length != 6){
            throw new IllegalArgumentException("Invalid parameters");
        }

        File staticFile = new File(args[0]);
        Scanner myStaticReader = new Scanner(staticFile);
        File dynamicFile = new File(args[1]);
        Scanner myDynamicReader = new Scanner(dynamicFile);
        String path = args[2];

        int N = 0;
        int L = 0;
        double r = Double.parseDouble(args[3]);
        double n = Double.parseDouble(args[4]);
        int steps = Integer.parseInt(args[5]);

        //parse the quantity of particles(N) and the size of the square grid(L)
        if(myStaticReader.hasNextLine()){
            N = Integer.parseInt(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }
        if(myStaticReader.hasNextLine()){
            L = Integer.parseInt(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }

        Map<Integer,ExtendedParticle> particles = new HashMap<>();

        myDynamicReader.nextLine();

        OffLatticeHelper.getDynamicProperties(myDynamicReader,particles);

        OffLatticeMethod method = new OffLatticeMethod(L,r,n,particles,steps);

        OffLatticeHelper.createOutputFile(path,steps);

        method.offLattice();

        long endTime = System.currentTimeMillis();
        System.out.printf("Time: %d ms\n", endTime - startTime);

    }
}