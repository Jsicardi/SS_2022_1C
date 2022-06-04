import java.io.IOException;
import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CPM {

    private static final double TAU = 0.5;
    private static final double EPSILON = 1e-10;
    private static final double ZOMBIE_VISION_RADIUS = 4;
    private static final double HUMAN_VISION_RADIUS = 4;
    private static final double ZOMBIE_ESCAPE_WEIGHT = 2.5;
    private final double rMin;
    private final double rMax;
    private final double R;
    private final double deltaT;
    private final double savingT;
    private final double tf;
    private final double vzi;
    private final double vdz;
    private final double vdh;
    private final double veh;
    private final double Ap;
    private final double Bp;
    private final double Apz;
    private final double Bpz;
    private final double transformationTime;
    private final double beta;
    private final List<Particle> zombies;
    private final List<Particle> humans;
    private final Queue<TransformingAction> transformingActions;
    private double t = 0;
    private double Nh;
    private final double cureProbability;

    public CPM(double rMax,double R, double vdh, double vdz, double Ap, double Bp, double savingT, double transformationTime, double tf,double beta,double cureProbability, List<Particle> humans, List<Particle> zombies){
        this.rMin = zombies.get(0).getR();
        this.rMax = rMax;
        this.R = R;
        this.vdh = vdh;
        this.veh = vdh;
        this.vdz = vdz;
        this.vzi = Math.sqrt(Math.pow(zombies.get(0).getVx(), 2) + Math.pow(zombies.get(0).getVy(), 2));
        this.savingT = savingT;
        this.transformationTime = transformationTime;
        this.tf = tf;
        this.Ap = Ap;
        this.Bp = Bp;
        this.Apz = Ap*ZOMBIE_ESCAPE_WEIGHT;
        this.Bpz = Bp*ZOMBIE_ESCAPE_WEIGHT;
        this.zombies = zombies;
        this.humans = humans;
        this.Nh = humans.size();
        this.deltaT = this.rMin / (2*(this.vdh));
        this.transformingActions = new LinkedList<>();
        this.beta = beta;
        this.cureProbability = cureProbability;
    }

    public void execute() throws IOException {

        // Initialize aux variables
        double auxT;
        double fz = 0.0001;

        // Iterations
        while(fz > EPSILON && fz < 1 && t < tf) {
            // Check save state
            if ((t % savingT) < EPSILON || (t % savingT) > savingT - EPSILON) {
                auxT = round(t, 15);
                ZombieDynamicsHelper.addOutputStep(auxT, humans, zombies, transformingActions);
            }

            // Check if human transformation ended (and turns into zombie) peeking at transformation queue
            checkTransformationsEnd();

            // Check if zombie is in contact to human and start human transformation, put in queue and remove zombie and human from lists
            checkZombieBite();

            // Iterate: find contacts, adjust radii and calculate new targets and velocities
            humansCalculations();
            zombiesCalculations();

            // Iterate: move particles
            moveParticles();

            t += deltaT;
            fz = (zombies.size() + transformingActions.size()) / (Nh+1);
        }
    }

    private void humansCalculations(){
        // Check contacts with other humans and walls
        Map<Particle, Particle> humanContacts = getHumansContacts();               // Return  particle -> closest human that is in contact
        Map<Particle, List<Double>> wallContacts = getWallContacts(humans);       // Return  particle -> (x, y) of wall position that's in contact
        for (Particle human : humans) {
            // Update radii based on if human is in contact with something
            if (humanContacts.containsKey(human) || wallContacts.containsKey(human))
                human.setR(rMin);
            else if (human.getR() < rMax - EPSILON) {
                human.setR(human.getR() + (rMax / (TAU / deltaT)));
                if(human.getR() > rMax){
                    human.setR(rMax);
                }
            }

            // If in contact, escape
            if (wallContacts.containsKey(human)){
                List<Double> wallPos = wallContacts.get(human);
                escapeFromContact(human, wallPos.get(0), wallPos.get(1), false);
            } else if (humanContacts.containsKey(human)){
                Particle otherHuman = humanContacts.get(human);
                escapeFromContact(human, otherHuman.getX(), otherHuman.getY(), false);
            } else {
                //Else, avoid collisions
                // Give new target and speed based on contacts and closest zombie position
                giveHumanTarget(human, findClosestZombie(human, false), findClosestHuman(human, false), findClosestWall(human, false));
            }
        }
    }

    private void zombiesCalculations(){
        // Check contacts with other zombies and walls
        Map<Particle, Particle> zombiesContacts = getZombiesContacts();         // Return  particle -> closest zombie that is in contact
        Map<Particle, List<Double>> wallContacts = getWallContacts(zombies);             // Return  particle -> (x, y) of wall position that's in contact
        for (Particle zombie : zombies) {
            // Update radii based on if human is in contact with something
            if (zombiesContacts.containsKey(zombie) || wallContacts.containsKey(zombie))
                zombie.setR(rMin);
            else if (zombie.getR() < rMax - EPSILON){
                zombie.setR(zombie.getR() + (rMax / (TAU / deltaT)));
                if(zombie.getR() > rMax){
                    zombie.setR(rMax);
                }
            }

            // If in contact, escape
            if (wallContacts.containsKey(zombie)){
                List<Double> wallPos = wallContacts.get(zombie);
                escapeFromContact(zombie, wallPos.get(0), wallPos.get(1), true);
            } else if (zombiesContacts.containsKey(zombie)){
                Particle otherZombie = zombiesContacts.get(zombie);
                escapeFromContact(zombie, otherZombie.getX(), otherZombie.getY(), true);
            } else {
                // Else, avoid collisions
                // Give new target and speed based on contacts and closest human position
                giveZombieTarget(zombie, findClosestHuman(zombie, true));
            }

        }
    }

    private void escapeFromContact(Particle particle, double x, double y, boolean isZombie){  // Particle to move and point to run from
        double[] escapeDir = {particle.getX() - x, particle.getY() - y};
        double length = Math.sqrt(Math.pow(escapeDir[0], 2) + Math.pow(escapeDir[1], 2));
        escapeDir[0] = escapeDir[0] / length;
        escapeDir[1] = escapeDir[1] / length;
        if (isZombie){
            particle.setVx(escapeDir[0] * vdz);
            particle.setVy(escapeDir[1] * vdz);
        } else {
            particle.setVx(escapeDir[0] * vdh);
            particle.setVy(escapeDir[1] * vdh);
        }
    }

    private Particle findClosestZombie(Particle particle, boolean callingZombie){
        double hx = particle.getX();
        double hy = particle.getY();
        Particle closestZombie = null;
        double closestDistance = Integer.MAX_VALUE;

        List<Particle> allZombies = Stream.concat(zombies.stream(), transformingActions.stream().map(TransformingAction::getZombie)).collect(Collectors.toList());

        double dist;
        for (Particle zombie : allZombies){
            dist = getDistance(hx,hy,zombie.getX(),zombie.getY(),particle.getR(),zombie.getR());
            if(callingZombie){
                if (dist <= ZOMBIE_VISION_RADIUS && dist < closestDistance && !zombie.equals(particle)){
                    closestDistance = dist;
                    closestZombie = zombie;
                }
            } else {
                if (dist <= HUMAN_VISION_RADIUS && dist < closestDistance && !zombie.equals(particle)){
                    closestDistance = dist;
                    closestZombie = zombie;
                }
            }
        }

        return closestZombie;
    }

    private Particle findClosestHuman(Particle particle, boolean callingZombie){
        double zx = particle.getX();
        double zy = particle.getY();
        Particle closestHuman = null;
        double closestDistance = Integer.MAX_VALUE;

        double dist;
        for (Particle human : humans){
            dist = getDistance(zx,zy,human.getX(),human.getY(),particle.getR(),human.getR());
            if (callingZombie){
                if (dist <= ZOMBIE_VISION_RADIUS && dist < closestDistance && !human.equals(particle)){
                    closestDistance = dist;
                    closestHuman = human;
                }
            } else {
                if (dist < closestDistance && !human.equals(particle)) {
                    closestDistance = dist;
                    closestHuman = human;
                }
            }
        }

        return closestHuman;
    }

    private List<Double> findClosestWall(Particle particle, boolean callingZombie){
        double x = particle.getX();
        double y = particle.getY();
        double closestWallX;
        double closestWallY;

        // Circle center is in (0 ; 0)
        double magV = Math.sqrt(x*x + y*y);
        if (magV == 0) {
            closestWallX = 0;
            closestWallY = R;
        } else {
            closestWallX = x / magV * R;
            closestWallY = y / magV * R;
        }

        if (callingZombie && magV > ZOMBIE_VISION_RADIUS)
            return null;

        return Arrays.asList(closestWallX,closestWallY);
    }

    private Map<Particle, Particle> getHumansContacts(){
        Map<Particle,Particle> contacts = new HashMap<>();
        double distance;
        Particle human1;
        Particle human2;
        List<Particle> allHumans = Stream.concat(Stream.concat(humans.stream(), transformingActions.stream().map(TransformingAction::getHuman)),transformingActions.stream().map(TransformingAction::getZombie)).collect(Collectors.toList());
        for(int i=0; i < allHumans.size(); i++){
            human1 = allHumans.get(i);
            for(int j=i+1; j < allHumans.size(); j++){
                human2 = allHumans.get(j);
                distance = getDistance(human1.getX(), human1.getY(), human2.getX(), human2.getY(), human1.getR(), human2.getR());
                if (distance <= EPSILON){
                    contacts.put(human1,human2);
                    break;
                }
            }
        }
        return contacts;
    }

    private Map<Particle, Particle> getZombiesContacts(){
        Map<Particle,Particle> contacts = new HashMap<>();
        double distance;
        Particle zombie1;
        Particle zombie2;
        List<Particle> allZombies = Stream.concat(Stream.concat(zombies.stream(), transformingActions.stream().map(TransformingAction::getHuman)),transformingActions.stream().map(TransformingAction::getZombie)).collect(Collectors.toList());
        for(int i=0; i < allZombies.size(); i++){
            zombie1 = allZombies.get(i);
            for(int j=i+1; j < allZombies.size(); j++){
                zombie2 = allZombies.get(j);
                distance = getDistance(zombie1.getX(), zombie1.getY(), zombie2.getX(), zombie2.getY(), zombie1.getR(), zombie2.getR());
                if (distance <= EPSILON){
                    contacts.put(zombie1,zombie2);
                    break;
                }
            }
        }
        return contacts;
    }

    private Map<Particle, List<Double>> getWallContacts(List<Particle> particles){
        Map<Particle,List<Double>> contacts = new HashMap<>();
        double distance;
        double closestWallX;
        double closestWallY;
        double x,y;

        for(Particle p : particles){
            x = p.getX();
            y = p.getY();

            // Circle center is in (0 ; 0)
            double magV = Math.sqrt(x*x + y*y);
            closestWallX = x / magV * R;
            closestWallY = y / magV * R;
            distance = getDistance(p.getX(),p.getY(),closestWallX,closestWallY,p.getR(),0);

            if(distance <= EPSILON){
                contacts.put(p,Arrays.asList(closestWallX,closestWallY));
            }
        }
        return contacts;
    }

    private double[] getEscapeValues(double x1, double y1, double x2, double y2, double r1, double r2, boolean isZombie){
        double escapeX = x1-x2;
        double escapeY = y1-y2;
        double dist = getDistance(x1,y1,x2,y2,r1,r2);
        double auxAp = this.Ap;
        double auxBp = this.Bp;
        if(isZombie){
            auxAp = this.Apz;
            auxBp = this.Bpz;
        }
        double weight = auxAp * Math.exp(-dist / auxBp);
        return new double[]{(escapeX / dist) * weight, escapeY/dist*weight};

    }

    private void giveHumanTarget(Particle human, Particle closestZombie, Particle closestHuman, List<Double> closestWall){
        // Check all the possible things to run from and choose using heuristic
        double hx = human.getX();
        double hy = human.getY();

        if(closestZombie == null){
            human.setVx(0);
            human.setVy(0);
            return;
        }
        // Avoid human
        double[] awayFromHumanDir = {0, 0};
        if (closestHuman != null){
            // Calculate direction of escape from human
            awayFromHumanDir = getEscapeValues(hx,hy,closestHuman.getX(),closestHuman.getY(), human.getR(), closestHuman.getR(),false);
        }

        // Avoid wall
        double[] awayFromWallDir = {0, 0};
        if (closestWall != null){
            // Calculate direction of escape from wall
            double x = human.getX();
            double y = human.getY();
            awayFromWallDir = getEscapeValues(hx,hy,closestWall.get(0),closestWall.get(1),human.getR(),0,false);
        }

        // Escape from zombie
        double[] zombieEscape = getEscapeValues(hx,hy,closestZombie.getX(),closestZombie.getY(), human.getR(), closestZombie.getR(),true);
        // Sum and decide on final direction and speed
        double[] finalDirection = {zombieEscape[0] + awayFromHumanDir[0] + awayFromWallDir[0], zombieEscape[1] + awayFromHumanDir[1] + awayFromWallDir[1]};
        double distFinal = Math.sqrt(Math.pow(finalDirection[0], 2) + Math.pow(finalDirection[1], 2));
        finalDirection[0] = finalDirection[0] / distFinal;
        finalDirection[1] = finalDirection[1] / distFinal;
        human.setVx((finalDirection[0] * vdh * Math.pow((human.getR() - rMin) / (rMax - rMin), beta)));
        human.setVy((finalDirection[1] * vdh * Math.pow((human.getR() - rMin) / (rMax - rMin), beta)));
    }

    private void giveZombieTarget(Particle zombie, Particle closestHuman) {
        // Check all the possible things to run from and choose using heuristic
        double zx = zombie.getX();
        double zy = zombie.getY();

        // Run towards human
        double[] towardsHumanDir = {0, 0};
        if(closestHuman == null){
            double angle = Math.atan2(zombie.getVy(), zombie.getVx());
            zombie.setVx(vzi * Math.cos(angle) * Math.pow((zombie.getR() - rMin) / (rMax - rMin), beta));
            zombie.setVy(vzi * Math.sin(angle) * Math.pow((zombie.getR() - rMin) / (rMax - rMin), beta));
        }
        else {
            towardsHumanDir[0] = closestHuman.getX() - zx;
            towardsHumanDir[1] = closestHuman.getY() - zy;    // Direction of pursuit towards human
            double distFromZombie = Math.sqrt(Math.pow(towardsHumanDir[0], 2) + Math.pow(towardsHumanDir[1], 2));
            towardsHumanDir[0] = towardsHumanDir[0] / distFromZombie;
            towardsHumanDir[1] = towardsHumanDir[1] / distFromZombie;
            
            zombie.setVx((towardsHumanDir[0] * vdz * Math.pow((zombie.getR() - rMin) / (rMax - rMin), beta)));
            zombie.setVy((towardsHumanDir[1] * vdz * Math.pow((zombie.getR() - rMin) / (rMax - rMin), beta)));
        }
    }

    private void checkTransformationsEnd(){
        boolean stop = false;
        double humanAngle;
        double zombieAngle;

        while (!stop){
            TransformingAction closest = transformingActions.peek();
            if (closest != null && (t - EPSILON) > closest.getTimestamp()){

                if(closest.getType() == TransformationType.CONSUME) {
                    // Transformation done
                    Particle human = closest.getHuman();
                    humanAngle = Math.toRadians(Math.random() * 360);
                    human.setR(rMin);
                    human.setVx(0.0000001 * humanAngle);
                    human.setVy(0.0000001 * humanAngle);
                    zombies.add(human);

                    zombieAngle = Math.toRadians(Math.random() * 360);
                    closest.getZombie().setR(rMin);
                    closest.getZombie().setVx(0.0000001 * zombieAngle);
                    closest.getZombie().setVy(0.0000001 * zombieAngle);
                    zombies.add(closest.getZombie());
                }
                else{
                    Particle human = closest.getHuman();
                    human.setR(rMin);
                    humans.add(human);
                    closest.getZombie().setR(rMin);
                    humans.add(closest.getZombie());
                }

                transformingActions.remove();
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
        double distance;
        double randomProb;
        TransformationType type;
        while (itr.hasNext()){
            Particle zombie = itr.next();
            for(int i=0; i< humans.size(); i++) {
                distance = getDistance(humans.get(i).getX(), humans.get(i).getY(), zombie.getX(), zombie.getY(), humans.get(i).getR(), zombie.getR());
                if (distance <= ZOMBIE_VISION_RADIUS) {
                    if (distance <= EPSILON) {
                        type = TransformationType.CONSUME;
                        itr.remove();
                        zombie.setVx(0);
                        zombie.setVy(0);
                        humans.get(i).setVx(0);
                        humans.get(i).setVy(0);
                        randomProb = Math.random();
                        if(randomProb < cureProbability){
                            type = TransformationType.CURE;
                        }
                        transformingActions.add(new TransformingAction(t + transformationTime, zombie, humans.get(i), type));
                        humans.remove(i);
                        break;
                    }
                }
            }
        }
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private double getDistance(double x1, double y1, double x2, double y2, double r1, double r2){
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2,2)) - (r1+r2);
    }

}
