import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public abstract class ZombieDynamicsHelper {
    private static FileWriter fileWriter;

    private ZombieDynamicsHelper(){

    }

    public static List<Double> parseStaticProperties(Scanner reader){
        List<Double> rads = new ArrayList<>();
        while(reader.hasNextLine()){
            String data = reader.nextLine().trim().replaceAll("\n", "");
            StringTokenizer tokenizer = new StringTokenizer(data, "\t");
            tokenizer.nextToken(); //skip id
            rads.add(Double.parseDouble(tokenizer.nextToken()));
        }
        return rads;
    }

    public static List<Particle> parseDynamicProperties(Scanner reader, List<Double> rads){
        List<Particle> humans = new ArrayList<>();
        double humanX;
        double humanY;
        double humanVx;
        double humanVy;
        int i = 0;
        while(reader.hasNextLine()){
            String data = reader.nextLine().trim().replaceAll("\n", "");
            StringTokenizer tokenizer = new StringTokenizer(data, "\t");
            tokenizer.nextToken(); //skip id
            humanX = Double.parseDouble(tokenizer.nextToken());
            humanY = Double.parseDouble(tokenizer.nextToken());
            humanVx = Double.parseDouble(tokenizer.nextToken());
            humanVy = Double.parseDouble(tokenizer.nextToken());
            humans.add(new Particle(humanX,humanY,humanVx,humanVy,rads.get(i)));
            i++;
        }
        return humans;
    }

    public static void createOutputFile(String path) throws IOException {
        File output = new File(path);
        output.createNewFile();
        fileWriter = new FileWriter(output);
    }

    public static void addOutputStep(double t, List<Particle> humans, List<Particle> zombies, Queue<TransformingAction> transformingActions) throws IOException {
        StringBuilder builder;

        fileWriter.write(String.format(Locale.US,"%g\n", t));

        for(Particle human : humans){
            fileWriter.write(String.format(Locale.US,"%g\t%g\t%g\t%g\t%g\t%s\n", human.getX(), human.getY(), human.getVx(), human.getVy(),human.getR(), "0"));
        }
        for(Particle zombie : zombies){
            fileWriter.write(String.format(Locale.US,"%g\t%g\t%g\t%g\t%g\t%s\n", zombie.getX(), zombie.getY(), zombie.getVx(), zombie.getVy(),zombie.getR(),"2"));
        }
        for(TransformingAction transformingAction : transformingActions){
            fileWriter.write(String.format(Locale.US,"%g\t%g\t%g\t%g\t%g\t%s\n", transformingAction.getHuman().getX(), transformingAction.getHuman().getY(), transformingAction.getHuman().getVx(), transformingAction.getHuman().getVy(),transformingAction.getHuman().getR(), "1"));
            fileWriter.write(String.format(Locale.US,"%g\t%g\t%g\t%g\t%g\t%s\n", transformingAction.getZombie().getX(), transformingAction.getZombie().getY(), transformingAction.getZombie().getVx(), transformingAction.getZombie().getVy(),transformingAction.getZombie().getR(),"1"));
        }

    }

    public static void closeFiles() throws IOException {
        fileWriter.close();
    }
}
