import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public abstract class ZombieDynamicsHelper {

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
}
