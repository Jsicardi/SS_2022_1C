import java.io.IOException;
import java.util.*;


public class GasDiffusionMethod {

    private final double height;
    private final double width;
    private final double openingLength;
    private final double stopEpsilon;
    private final List<Particle> particles;
    private Queue<Collision> collisions;
    private boolean firstRound = true;
    private double currentTime = 0;
    private final int LEFT_WALL = -1;
    private final int UPPER_WALL = -2;
    private final int DOWN_WALL = -3;
    private final int RIGHT_WALL = -4;
    private final int MIDDLE_WALL = -5;


    public GasDiffusionMethod(double height, double width, double openingLength, List<Particle> particles, double stopEpsilon) {
        this.height = height;
        this.width = width;
        this.openingLength = openingLength;
        this.particles = particles;
        this.stopEpsilon = stopEpsilon;
        this.collisions = new PriorityQueue<>(Comparator.comparing(Collision::getTime));
    }


    public void executeMethod() throws IOException {

        // Percentage of particles in left enclosure
        double particleFraction = 1;
        List<Particle> currentParticles = new ArrayList<>(particles);
        Collision currentCollision;
        PriorityQueue<Collision> newCollisions;
        while (particleFraction > 0.5 + stopEpsilon || particleFraction < 0.5 - stopEpsilon) {

            // Calculate time of collisions
            analyzeCollisions(currentParticles);
            currentCollision = collisions.poll();

            if (currentCollision == null || currentCollision.getTime() == Integer.MAX_VALUE){
                System.out.println("System never collides");
                return;
            }

            // Move all particles until that moment and update particleFraction
            particleFraction = moveParticles(currentCollision.getTime()-currentTime);

            // Save system state
            GasDiffusionHelper.addOutputStep(particles,currentCollision.getTime(),particleFraction);

            // Resolve collision
            resolveCollision(currentCollision);

            newCollisions = new PriorityQueue<>(Comparator.comparing(Collision::getTime));

            for(Collision collision : collisions){
                if(collision.getParticle1Index() != currentCollision.getParticle1Index() && collision.getParticle2Index() != currentCollision.getParticle1Index()){
                    if(currentCollision.getParticle2Index() >= 0 && collision.getParticle1Index() != currentCollision.getParticle2Index() && collision.getParticle2Index() != currentCollision.getParticle2Index()){
                        newCollisions.add(collision);
                    }
                    else if(currentCollision.getParticle2Index() < 0){
                        newCollisions.add(collision);
                    }
                }
            }
            collisions = newCollisions;

            currentParticles = new ArrayList<>();
            currentParticles.add(particles.get(currentCollision.getParticle1Index()));
            if(currentCollision.getParticle2Index() >= 0){
                currentParticles.add(particles.get(currentCollision.getParticle2Index()));
            }

            currentTime += (currentCollision.getTime() - currentTime);

        }

        GasDiffusionHelper.closeFiles();
    }


    private void analyzeCollisions(List<Particle> currentParticles){
        analyzeWalls(currentParticles);
        if(firstRound){
            analyzeAllParticles();
            firstRound=false;
        }
        else {
            List<Particle> comparingParticles = new ArrayList<>(particles);
            if(currentParticles.size() == 2){ //if last collision was with a wall, only one particle will be found
                int index1 = currentParticles.get(0).getId()-1;
                int index2 = currentParticles.get(1).getId()-1;
                if(index1 < index2) {
                    comparingParticles.remove(index2);
                    comparingParticles.remove(index1);
                }
                else{
                    comparingParticles.remove(index1);
                    comparingParticles.remove(index2);
                }
            }
            else{
                comparingParticles.remove(currentParticles.get(0).getId()-1);
            }
            analyzeParticles(currentParticles,comparingParticles);
        }
    }


    private void analyzeWalls(List<Particle> currentParticles){
        double auxTime;
        double auxY;
        for (Particle p : currentParticles){
            // Check horizontal collision
            if (p.getVx() > 0){
                if (p.getX() < width/2){     // Is in left enclosure going right. Check if collision would be with middle wall or right wall
                    auxTime = (width/2 - p.getRadius() - p.getX()) / p.getVx();
                    auxY = (auxTime * p.getVy()) + p.getY();
                    if(auxY >= (height/2 + openingLength/2) || auxY <= (height/2 - openingLength/2)){
                        collisions.add(new Collision(currentTime + auxTime,p.getId()-1,MIDDLE_WALL));
                    }
                    else{
                        auxTime = (width - p.getRadius() - p.getX())/p.getVx();
                        collisions.add(new Collision(currentTime + auxTime,p.getId()-1,RIGHT_WALL));
                    }
                } else {
                    auxTime = (width - p.getRadius() - p.getX())/p.getVx();
                    collisions.add(new Collision(currentTime + auxTime,p.getId()-1,RIGHT_WALL));
                }
            } else {
                if (p.getX() > width/2){     // Is in right enclosure going left. Check if collision would be with middle wall or right wall
                    auxTime = (width/2 + p.getRadius() - p.getX()) / p.getVx();
                    auxY = (auxTime * p.getVy()) + p.getY();
                    if(auxY >= (height/2 + openingLength/2) || auxY <= (height/2 - openingLength/2)){
                        collisions.add(new Collision(currentTime + auxTime,p.getId()-1,MIDDLE_WALL));
                    }
                    else{
                        auxTime = (p.getRadius() - p.getX())/p.getVx();
                        collisions.add(new Collision(currentTime + auxTime,p.getId()-1,LEFT_WALL));
                    }
                } else {
                    auxTime = (p.getRadius() - p.getX())/p.getVx();
                    collisions.add(new Collision(currentTime + auxTime,p.getId()-1,LEFT_WALL));
                }
            }

            // Check vertical collision
            if (p.getVy() > 0){
                auxTime = (height - p.getRadius() - p.getY())/p.getVy();
                collisions.add(new Collision(currentTime + auxTime,p.getId()-1,UPPER_WALL));
            } else {
                auxTime = (p.getRadius() - p.getY())/p.getVy();
                collisions.add(new Collision(currentTime + auxTime,p.getId()-1,DOWN_WALL));
            }
        }
    }

    private void analyzeAllParticles(){
        double auxTime = 0;

        for (int i = 0; i < particles.size()-1; i++){
            for (int j = i+1; j < particles.size(); j++){
                Particle p1 = particles.get(i);
                Particle p2 = particles.get(j);

                double deltaX = p2.getX() - p1.getX();
                double deltaY = p2.getY() - p1.getY();
                double deltaVx = p2.getVx() - p1.getVx();
                double deltaVy = p2.getVy() - p1.getVy();
                double sigma = p1.getRadius() + p2.getRadius();
                double deltaRSq = deltaX*deltaX +deltaY*deltaY;
                double deltaVSq = deltaVx*deltaVx + deltaVy*deltaVy;
                double deltaVdeltaR =deltaVx*deltaX + deltaVy*deltaY;
                double d = deltaVdeltaR*deltaVdeltaR - deltaVSq * (deltaRSq - sigma*sigma);

                if (deltaVdeltaR >= 0 || d < 0){    // Will never collide
                    continue;
                }

                auxTime = -((deltaVdeltaR + Math.sqrt(d)) / deltaVSq);

                collisions.add(new Collision(currentTime + auxTime,i,j));
            }
        }

    }

    private void analyzeParticles(List<Particle> currentParticles, List<Particle> comparingParticles){
        double auxTime = 0;

        for (int i = 0; i < currentParticles.size(); i++){
            for (int j = 0; j < comparingParticles.size(); j++){
                Particle p1 = currentParticles.get(i);
                Particle p2 = comparingParticles.get(j);

                double deltaX = p2.getX() - p1.getX();
                double deltaY = p2.getY() - p1.getY();
                double deltaVx = p2.getVx() - p1.getVx();
                double deltaVy = p2.getVy() - p1.getVy();
                double sigma = p1.getRadius() + p2.getRadius();
                double deltaRSq = deltaX*deltaX +deltaY*deltaY;
                double deltaVSq = deltaVx*deltaVx + deltaVy*deltaVy;
                double deltaVdeltaR =deltaVx*deltaX + deltaVy*deltaY;
                double d = deltaVdeltaR*deltaVdeltaR - deltaVSq * (deltaRSq - sigma*sigma);

                if (deltaVdeltaR >= 0 || d < 0){    // Will never collide
                    continue;
                }

                auxTime = -((deltaVdeltaR + Math.sqrt(d)) / deltaVSq);

                collisions.add(new Collision(currentTime+auxTime, p1.getId()-1, p2.getId()-1));
            }
        }

    }


    private double moveParticles(double timeToCollision){
        int particlesOnLeftEnclosure = 0;
        for (Particle p : particles){
            p.setX(p.getX() + p.getVx() * timeToCollision);
            p.setY(p.getY() + p.getVy() * timeToCollision);

            if (p.getX() < width/2)
                particlesOnLeftEnclosure++;
        }

        return (double) particlesOnLeftEnclosure / particles.size();
    }


    private void resolveCollision(Collision collision){
        double vx;
        double vy;

        //resolve collision with walls
        switch (collision.getParticle2Index()){
            case LEFT_WALL:
            case RIGHT_WALL:
            case MIDDLE_WALL:
                vx = particles.get(collision.getParticle1Index()).getVx();
                particles.get(collision.getParticle1Index()).setVx(-vx);
                break;
            case UPPER_WALL:
            case DOWN_WALL:
                vy = particles.get(collision.getParticle1Index()).getVy();
                particles.get(collision.getParticle1Index()).setVy(-vy);
                break;
            default:
                resolveCollisionWithParticles(collision);
        }
    }


    private void resolveCollisionWithParticles(Collision collision){
        Particle p1 = particles.get(collision.getParticle1Index());
        Particle p2 = particles.get(collision.getParticle2Index());

        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double deltaVx = p2.getVx() - p1.getVx();
        double deltaVy = p2.getVy() - p1.getVy();
        double sigma = p1.getRadius() + p2.getRadius();
        double deltaVdeltaR =deltaVx*deltaX + deltaVy*deltaY;

        double J = (2 * (p1.getMass() * p2.getMass()) * deltaVdeltaR) / (sigma * (p1.getMass() + p2.getMass()));

        double Jx = (J *deltaX) / sigma;
        double Jy = (J *deltaY) / sigma;

        particles.get(collision.getParticle1Index()).setVx(p1.getVx() + (Jx/p1.getMass()));
        particles.get(collision.getParticle1Index()).setVy(p1.getVy() + (Jy/p1.getMass()));
        particles.get(collision.getParticle2Index()).setVx(p2.getVx() - (Jx/p2.getMass()));
        particles.get(collision.getParticle2Index()).setVy(p2.getVy() - (Jy/p2.getMass()));

    }
}
