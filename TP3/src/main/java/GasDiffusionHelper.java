import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static java.lang.System.exit;

public abstract class GasDiffusionHelper {
    private static FileWriter positionsFileWriter;
    private static FileWriter fpFileWriter;

    private GasDiffusionHelper() {
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
        List<Double> particleProperties = new ArrayList<>();
        int id_counter = 1;

        String aux_token;
        while (reader.hasNextLine()) {
            particleProperties.clear();
            String data = reader.nextLine().trim().replaceAll("\\s+", " ");;
            StringTokenizer tokenizer = new StringTokenizer(data);

            while(tokenizer.hasMoreElements()){
                particleProperties.add(Double.parseDouble(tokenizer.nextToken()));
            }

            if(particleProperties.size() == 4){
                particles.get(id_counter - 1).setX(particleProperties.get(0));
                particles.get(id_counter - 1).setY(particleProperties.get(1));
                particles.get(id_counter - 1).setVx(particleProperties.get(2));
                particles.get(id_counter - 1).setVy(particleProperties.get(3));
                id_counter++;
            }
            else{
                System.out.printf("Invalid dynamic properties for particle %d\n", id_counter);
                exit(-1);
            }
        }
    }


    public static void createOutputFiles(String positionsFile, String fpFile) throws IOException {
        File positionsOutput = new File(positionsFile);
        positionsOutput.createNewFile();
        positionsFileWriter = new FileWriter(positionsFile);

        File fpOutput= new File(fpFile);
        fpOutput.createNewFile();
        fpFileWriter = new FileWriter(fpFile);
        fpFileWriter.write("Time\tFpA\tFpB\n");

    }

    public static void addOutputStep(List<Particle> particles,double time, double fp) throws IOException {

        StringBuilder builder;

        positionsFileWriter.write(String.format("%.2f\n\n", time));
        for(Particle particle: particles){
            builder = new StringBuilder();
            builder.append(particle.getId()).append("\t").append(particle.getX()).append("\t").append(particle.getY()).append("\t").append(particle.getVx()).append("\t").append(particle.getVy()).append("\t");
            builder.replace(builder.length(), builder.length(), "\n");
            positionsFileWriter.write(builder.toString());
        }

        fpFileWriter.write(String.format("%.2f\t%g\t%g\n",time,fp, 1-fp));

    }

    public static void closeFiles() throws IOException {
        positionsFileWriter.close();
        fpFileWriter.close();
    }
}
