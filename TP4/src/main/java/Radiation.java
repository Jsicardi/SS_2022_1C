import java.io.IOException;


public class Radiation {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        if(args.length != 6){
            throw new IllegalArgumentException("Invalid parameters");
        }
        String path = args[0];

        double deltaT = Double.parseDouble(args[1]);
        double savingT = Double.parseDouble(args[2]);
        double v0 = Double.parseDouble(args[3]);
        double y0 = Double.parseDouble(args[4]);
        boolean randomY0 = Boolean.parseBoolean(args[5]);

        RadiationHelper helper = new RadiationHelper(v0, y0, deltaT,savingT,path, randomY0);

        long endTime = System.currentTimeMillis();
        System.out.printf("Time: %d ms\n", endTime - startTime);

    }


}
