import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CPM {
    private static final double TAU = 0.5;
    private static final double EPSILON = 1e-14;
    private final double rMin;
    private final double rMax;
    private final double R;
    private final double deltaT;
    private final double savingT;
    private final double cutConditionT;
    private final double vzi;
    private final double vdz;
    private final double vdh;
    private final double veh;
    private final double Ap;
    private final double Bp;
    private final double transformationTime;
    private final List<Particle> zombies;
    private final List<Particle> humans;
    private final Queue<TransformingAction> tranformingActions;
    private double t = 0;


    public CPM(double rMin, double rMax,double R, double vdh, double vdz, double Ap, double Bp, double savingT, double cutConditionT, double transformationTime, List<Particle> humans, List<Particle> zombies){
        this.rMin = rMin;
        this.rMax = rMax;
        this.R = R;
        this.vdh = vdh;
        this.veh = vdh;
        this.vdz = vdz;
        this.vzi = Math.sqrt(Math.pow(zombies.get(0).getVx(), 2) + Math.pow(zombies.get(0).getVy(), 2));
        this.savingT = savingT;
        this.Ap = Ap;
        this.Bp = Bp;
        this.zombies = zombies;
        this.humans = humans;
        this.deltaT = this.rMin / 2*(this.vdh);
        this.tranformingActions = new LinkedList<>();
        this.transformationTime = transformationTime;
        this.cutConditionT = cutConditionT;
    }


    public void execute(){

        // Initialize aux variables and radii on particles
        double auxT;
        for (Particle human : humans)
            human.setR(rMin);
        for (Particle zombie : zombies)
            zombie.setR(rMin);


        // Iterations

        // Check save state
        if(t % savingT < EPSILON || t % savingT > savingT - EPSILON) {
            auxT = round(t,15);
            //generateOutput(auxT);
        }

        // Check if human transformation ended (and turns into zombie) peeking at transformation queue
        checkTransformationsEnd();

        // Check if zombie is in contact to human and start human transformation, put in queue and remove zombie and human from lists
        checkZombieBite();

        // Iterate: find contacts, adjust radii and calculate new targets and velocities
        checkHumansContacts();
        checkZombiesContacts();

        // Iterate: move particles
        moveParticles();

    }


    /*
    old method
    private double getVdMagnitude(Particle particle, boolean isHuman){
        double vd;
        if (isHuman)
            vd = vdh;
        else
            vd = vdz;
        return vd * Math.pow((particle.getR() - rMin) / (rMax - rMin), beta);
    }
    */

    private void giveHumansTargets(){

    }

    private void giveZombiesTargets(){

    }

    private void checkHumansContacts(){

    }

    private void checkZombiesContacts(){

    }


    private void checkTransformationsEnd(){
        boolean stop = false;
        while (!stop){
            TransformingAction closest = tranformingActions.peek();
            if (closest != null && closest.getTimestamp() > t - EPSILON){
                // Transformation done
                zombies.add(closest.getZombie());
                Particle human = closest.getHuman();
                // TODO give the human zombie conditions
                // human.speed = vzi
                // human.speedDirection = random
                zombies.add(human);
                tranformingActions.remove();
            } else
                stop = true;
        }
    }


    private void moveParticles(){
        for (Particle human : humans) {
            human.setX(human.getX() + human.getVx() * deltaT);
            human.setY(human.getY() + human.getVy() * deltaT);
        }
        for (Particle zombie : zombies) {
            zombie.setX(zombie.getX() + zombie.getVx() * deltaT);
            zombie.setY(zombie.getY() + zombie.getVy() * deltaT);
        }
    }


    private void checkZombieBite(){
        Iterator<Particle> itr = zombies.iterator();
        while (itr.hasNext()){
            Particle zombie = itr.next();
            /*
            if (zombie is in contact with human){
                tranformingActions.add(new TransformingAction(t + transformationTime , zombie, human));
                itr.remove();
                remove human from humans
            }
            */
        }
    }


    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
