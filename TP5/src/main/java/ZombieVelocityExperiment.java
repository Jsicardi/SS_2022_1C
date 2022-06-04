import java.io.IOException;
import java.util.Locale;

public class ZombieVelocityExperiment {
    public static void main(String[] args) throws IOException {
        double velocities[] = {1,1.5,2,2.5,3,3.5,4,4.5,5};
        int Nh = 200;
        double rmin = 0.1;
        double rmax = 0.3;
        double vzi = 0.3;
        double vdh = 4;
        double Ap = 2000;
        double Bp = 0.08;
        double savingT = 1;
        double transformationT = 7;
        double tf = 300;
        double R = 11;
        double beta = 1;
        double cureProbability = 0;
        boolean withEnergy = false;

        for(double vdz : velocities){
            for(int i = 1; i <= 10; i++){
                ParticleGenerator.main(new String[]{Integer.toString(Nh),Double.toString(rmin), Double.toString(rmax), Double.toString(vzi), Double.toString(R)});
                ZombieDynamics.main(new String[]{"static_rand.txt", "dynamic_rand.txt", String.format(Locale.US,"result_%.2g_%d.txt",vdz,i),Double.toString(vdh), Double.toString(vdz), Double.toString(Ap), Double.toString(Bp), Double.toString(savingT), Double.toString(transformationT), Double.toString(tf), Double.toString(beta), Double.toString(cureProbability), Boolean.toString(withEnergy)});
                System.out.printf("%s generated\n", String.format(Locale.US,"result_%.2g_%d.txt",vdz,i));
            }
        }
    }
}
