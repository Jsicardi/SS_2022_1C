import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

public abstract class OffLatticeHelper {

    private static FileWriter positionsFileWriter;
    private static FileWriter vaFileWriter;
    private static int steps;

    private OffLatticeHelper() {
    }

    // parses particle static properties and returns the maximum radius
    public static void getDynamicProperties(Scanner reader, Map<Integer,ExtendedParticle> particles){
        List<Double> particleProps = new ArrayList<>();
        int id_counter = 1;

        String aux_token;
        while (reader.hasNextLine()) {
            particleProps.clear();
            String data = reader.nextLine().trim().replaceAll("\\s+", " ");;
            StringTokenizer tokenizer = new StringTokenizer(data);

            while(tokenizer.hasMoreElements()){
                particleProps.add(Double.parseDouble(tokenizer.nextToken()));
            }

            if(particleProps.size() == 4){
                particles.put(id_counter,new ExtendedParticle(id_counter,particleProps.get(0),particleProps.get(1),particleProps.get(2),particleProps.get(3)));
                id_counter++;
            }
            else{
                System.out.printf("Invalid dynamic properties for particle %d\n", id_counter);
                exit(-1);
            }
        }
    }

    public static void createOutputFiles(String positionsFile, String vaFile, int totalSteps) throws IOException {
        File positionsOutput = new File(positionsFile);
        positionsOutput.createNewFile();
        positionsFileWriter = new FileWriter(positionsFile);

        File vaOutput= new File(vaFile);
        vaOutput.createNewFile();
        vaFileWriter = new FileWriter(vaFile);

        steps = totalSteps;

    }

    public static void addOutputStep(List<ExtendedParticle> particles,int step, int N) throws IOException {

        StringBuilder builder;

        double sinSum= 0,cosSum=0;

        positionsFileWriter.write(String.format("%d\n\n", step));
        for(ExtendedParticle particle: particles){
            builder = new StringBuilder();
            builder.append(particle.getId()).append("\t").append(particle.getX()).append("\t").append(particle.getY()).append("\t").append(particle.getV()).append("\t").append(particle.getTheta()).append("\t");
            builder.replace(builder.length(), builder.length(), "\n");
            positionsFileWriter.write(builder.toString());
            sinSum+=(Math.sin(particle.getTheta()));
            cosSum+=(Math.cos(particle.getTheta()));
        }

        double va = Math.sqrt(Math.pow(sinSum,2) + Math.pow(cosSum,2)) / N;

        vaFileWriter.write(String.format("%g\n", va));

        if(step == steps) {
            positionsFileWriter.close();
            vaFileWriter.close();
        }
    }
}
