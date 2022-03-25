import java.io.File;
import java.io.IOException;
import java.util.*;

public class OffLattice {

    public static void main(String[] args) throws IOException {

        //args = [static,dynamic,final_arch,steps]

        if(args.length != 4){
            throw new IllegalArgumentException("Invalid parameters");
        }

        File staticFile = new File(args[0]);
        Scanner myStaticReader = new Scanner(staticFile);
        File dynamicFile = new File(args[1]);
        Scanner myDynamicReader = new Scanner(dynamicFile);
        String path = args[2];
        int steps = Integer.parseInt(args[3]);

        int N = 0;
        int L = 0;
        double r = 0;
        double n = 0;

        //parse the quantity of particles(N) and the size of the square grid(L)
        if(myStaticReader.hasNextLine()){
            N = Integer.parseInt(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }
        if(myStaticReader.hasNextLine()){
            L = Integer.parseInt(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }

        //parse the interaction radius(r) and the noise factor (n)
        if(myStaticReader.hasNextLine()){
            r = Double.parseDouble(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }
        if(myStaticReader.hasNextLine()){
            n = Double.parseDouble(myStaticReader.nextLine().trim().replaceAll("\\s+", ""));
        }

        Map<Integer,ExtendedParticle> particles = new HashMap<>();

        myDynamicReader.nextLine();

        OffLatticeHelper.getDynamicProperties(myDynamicReader,particles);

        OffLatticeMethod method = new OffLatticeMethod(L,r,n,particles,steps);

        OffLatticeHelper.createOutputFile(path,steps);

        method.offLattice();
    }
}
