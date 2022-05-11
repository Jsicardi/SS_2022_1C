import java.io.IOException;

public class EnergyExperiment {

    public static void main(String[] args) throws IOException {
        double[] deltas = {1e-14,5e-15,1e-15,5e-16,1e-16,5e-17,1e-17};
        double savingT = 1e-14;
        double[] y0 = {-1e-8,-5e-9,0,5e-9,1e-8};
        int maxVelocity = 50000;
        String[] mainArgs;

        String fileFormat = "result_d";
        int count;
        for(int i = 0; i < y0.length; i++){
            count = 1;
            for(double delta : deltas){
                mainArgs = new String[]{String.format("%s%d_%d.txt", fileFormat, i + 1, count),Double.toString(delta),Double.toString(savingT),Integer.toString(maxVelocity),Double.toString(y0[i]),Boolean.toString(false) };
                Radiation.main(mainArgs);
                count++;
            }
        }
    }
}
