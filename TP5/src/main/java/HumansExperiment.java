import java.io.IOException;

public class HumansExperiment {

    public static void main(String[] args) throws IOException {
        int humans[] = {2,5,10,20,40,80,140,200,260,320};
        double rmin = 0.1;
        double rmax = 0.3;
        double vzi = 0.3;
        double vdz = 3;
        double vdh = 4;
        double Ap = 2000;
        double Bp = 0.08;
        double savingT = 1;
        double transformationT = 7;
        double tf = 300;
        double R = 11;
        double beta = 1;

        for(int Nh : humans){
            for(int i = 1; i <= 10; i++){
                ParticleGenerator.main(new String[]{Integer.toString(Nh),Double.toString(rmin), Double.toString(rmax), Double.toString(vzi), Double.toString(R)});
                ZombieDynamics.main(new String[]{"static_rand.txt", "dynamic_rand.txt", String.format("result_%d_%d.txt",Nh,i),Double.toString(vdh), Double.toString(vdz), Double.toString(Ap), Double.toString(Bp), Double.toString(savingT), Double.toString(transformationT), Double.toString(tf), Double.toString(beta)});
                System.out.printf("%s generated\n", String.format("result_%d_%d.txt",Nh,i));
            }
        }
    }
}
