import java.io.IOException;


public class Oscilator {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();

        if(args.length != 4){
            throw new IllegalArgumentException("Invalid parameters");
        }
        String path = args[0];

        double deltaT1 = Double.parseDouble(args[1]);
        double deltaT2 = Double.parseDouble(args[2]);
        String method = args[3];

        OscilatorHelper helper = new OscilatorHelper(new Particle(1,0,-(100.0 / 140),0,70), deltaT1,deltaT2,path);

        switch (method){
            case "Beeman":
                helper.executeBeeman();
                break;
            case "Verlet":
                helper.executeVerlet();
                break;
            case "Gear":
                helper.executeGear();
                break;
        }

        long endTime = System.currentTimeMillis();
        System.out.printf("Time: %d ms\n", endTime - startTime);

    }


}
