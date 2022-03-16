package main.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

public abstract class CIMHelper {

    private CIMHelper() {
    }

    // parses particle static properties and returns the maximum radius
    public static double getStaticProperties(Scanner reader, List<Particle> particles){
        List<Double> particleProps = new ArrayList<>();
        int id_counter = 1;
        double max_radius = 0;

        while (reader.hasNextLine()) {
            particleProps.clear();
            String data = reader.nextLine().trim().replaceAll("\\s+", " ");
            StringTokenizer tokenizer = new StringTokenizer(data);

            while(tokenizer.hasMoreElements()){
                particleProps.add(Double.parseDouble(tokenizer.nextToken()));
            }

            if(particleProps.size() == 2){
                particles.add(new Particle(id_counter, particleProps.get(0), particleProps.get(1)));
                if(particleProps.get(0) > max_radius){
                    max_radius = particleProps.get(0);
                }
            }
            else{
                System.out.printf("Invalid static properties for particle %d\n", id_counter);
                exit(-1);
            }
            id_counter++;
        }
        return max_radius;
    }

    // parses particle static properties and returns the maximum radius
    public static void getDynamicProperties(Scanner reader, List<Particle> particles){
        List<Double> particlePos = new ArrayList<>();
        int id_counter = 1;

        String aux_token;
        while (reader.hasNextLine()) {
            particlePos.clear();
            String data = reader.nextLine().trim().replaceAll("\\s+", " ");;
            StringTokenizer tokenizer = new StringTokenizer(data);

            while(tokenizer.hasMoreElements()){
                particlePos.add(Double.parseDouble(tokenizer.nextToken()));
            }

            if(particlePos.size() == 2){
                particles.get(id_counter - 1).setX(particlePos.get(0));
                particles.get(id_counter - 1).setY(particlePos.get(1));
                id_counter++;
            }
            else{
                System.out.printf("Invalid dynamic properties for particle %d\n", id_counter);
                exit(-1);
            }
        }
    }

    public static void generateOutput(HashMap<Integer, List<Integer>> neighbours) throws IOException {
        File output = new File("neighbours.txt");
        output.createNewFile();
        StringBuilder builder;

        FileWriter writer = new FileWriter("neighbours.txt");

        for(int particleId : neighbours.keySet()){
            List<Integer> neighboursIds = neighbours.get(particleId);
            builder = new StringBuilder();
            builder.append(particleId).append("\t");

            for(int neighbourId : neighboursIds){
                builder.append(neighbourId).append("\t");
            }

            builder.replace(builder.length(), builder.length(), "\n");
            writer.write(builder.toString());
        }

        writer.close();
    }
}
