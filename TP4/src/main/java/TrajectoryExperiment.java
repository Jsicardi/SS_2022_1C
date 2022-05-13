import java.io.IOException;

public class TrajectoryExperiment {

    public static void main(String[] args) throws IOException {
        int[] velocities = {5000,16250,27500,38750,50000};
        double dt = 1e-16;
        double savingT = 1e-14;
        double y0 = -1e-8;
        double dy = 1e-10;
        String[] mainArgs;
        double epsilon = 1e-14;

        String fileFormat = "result_v";
        int count;
        double y;
        double j;
        for(int i = 0; i < velocities.length; i++){
            count = 1;
            y = y0;
            while(y <= -y0){
               mainArgs = new String[]{String.format("%s%d_%d.txt", fileFormat, i + 1, count),Double.toString(dt),Double.toString(savingT),Integer.toString(velocities[i]),Double.toString(y),Boolean.toString(false) };
               Radiation.main(mainArgs);
               count++;
               y+=dy;
               if(y < 0 + epsilon && y > 0 - epsilon){
                   y = 0;
               }
            }
        }
    }
}
