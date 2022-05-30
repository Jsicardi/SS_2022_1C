import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

public class ZombieDynamics {
    public static void main(String[] args) throws IOException {

        if(args.length != 10){
            System.out.println("Invalid arguments quantity");
            exit(-1);
        }

        File staticFile = new File(args[0]);
        Scanner myStaticReader = new Scanner(staticFile);
        File dynamicFile = new File(args[1]);
        Scanner myDynamicReader = new Scanner(dynamicFile);
        String outputPath = args[2];
        double vdh = Double.parseDouble(args[3]);
        double vdz = Double.parseDouble(args[4]);
        double Ap = Double.parseDouble(args[5]);
        double Bp = Double.parseDouble(args[6]);
        double savingT = Double.parseDouble(args[7]);
        double transformationT = Double.parseDouble(args[8]);
        double tf = Double.parseDouble(args[9]);

        int Nh = Integer.parseInt(myStaticReader.nextLine());
        double rmax = Double.parseDouble(myStaticReader.nextLine());
        double R = Double.parseDouble(myStaticReader.nextLine());
        List<Double> rads = ZombieDynamicsHelper.parseStaticProperties(myStaticReader);

        List<Particle> zombies = new ArrayList<>();
        myDynamicReader.nextLine();
        String data = myDynamicReader.nextLine().trim().replaceAll("\n", "");
        StringTokenizer tokenizer = new StringTokenizer(data, "\t");
        tokenizer.nextToken(); //skip id
        double zombieX = Double.parseDouble(tokenizer.nextToken());
        double zombieY = Double.parseDouble(tokenizer.nextToken());
        double zombieVx = Double.parseDouble(tokenizer.nextToken());
        double zombieVy = Double.parseDouble(tokenizer.nextToken());
        zombies.add(new Particle(zombieX,zombieY,zombieVx,zombieVy,rads.get(0)));

        List<Particle> humans = ZombieDynamicsHelper.parseDynamicProperties(myDynamicReader,rads);

        ZombieDynamicsHelper.createOutputFile(outputPath);

        CPM cpmMethod = new CPM(rmax,R,vdh,vdz,Ap,Bp,savingT,transformationT,tf,humans,zombies);

        cpmMethod.execute();

        ZombieDynamicsHelper.closeFiles();

    }
}
