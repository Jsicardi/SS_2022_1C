import java.io.IOException;
import java.util.List;

public class GasDiffusionMethod {

    private final double height;
    private final double width;
    private final double openingLength;
    private final double stopEpsilon;
    private final List<Particle> particles;
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
    }


    public void executeMethod() throws IOException {
        int[] collisionParticles = new int[2];

        // Percentage of particles in left enclosure
        double particleFraction = 1;
        double timeToCollision;

        while (particleFraction > 0.5 + stopEpsilon || particleFraction < 0.5 - stopEpsilon) {
            // Calculate time to first collision

            timeToCollision = getTimeToCollision(collisionParticles);
            if (timeToCollision == Integer.MAX_VALUE){
                System.out.println("System never collides");
                return;
            }

            // Move all particles until that moment and update particleFraction
            particleFraction = moveParticles(timeToCollision);

            // Save system state
            GasDiffusionHelper.addOutputStep(particles,timeToCollision,particleFraction);

            // Resolve collision
            resolveCollission(collisionParticles);

        }

        GasDiffusionHelper.closeFiles();
    }


    private double getTimeToCollision(int[] collisionParticles){
        double timeToCollisionWalls = timeAgainstWalls(collisionParticles);
        double timeToCollisionParticles = timeAgainstParticles(collisionParticles);

        return Math.min(timeToCollisionWalls, timeToCollisionParticles);
    }


    private double timeAgainstWalls(int[] collissionParticles){
        double timeToCollision = Integer.MAX_VALUE;
        double auxTime = 0;
        double auxY = 0;
        int wallIndex = 0;
        int index = 0;
        for (Particle p : particles){
            // Check horizontal collision
            if (p.getVx() > 0){
                if (p.getX() < width/2){     // Is in left enclosure going right. Check if collision would be with middle wall or right wall
                    auxTime = (width/2 - p.getRadius() - p.getX()) / p.getVx();
                    auxY = (auxTime * p.getVy()) + p.getY();
                    if(auxY >= (height/2 + openingLength/2) || auxY <= (height/2 - openingLength/2)){
                        auxTime = (width/2 - p.getRadius() - p.getX()) / p.getVx();
                        wallIndex = MIDDLE_WALL;
                    }
                    else{
                        auxTime = (width - p.getRadius() - p.getX())/p.getVx();
                        wallIndex = RIGHT_WALL;
                    }
                } else {
                    auxTime = (width - p.getRadius() - p.getX())/p.getVx();
                    wallIndex = RIGHT_WALL;
                }
            } else {
                if (p.getX() > width/2){     // Is in right enclosure going left. Check if collision would be with middle wall or right wall
                    auxTime = (width/2 + p.getRadius() - p.getX()) / p.getVx();
                    auxY = (auxTime * p.getVy()) + p.getY();
                    if(auxY >= (height/2 + openingLength/2) || auxY <= (height/2 - openingLength/2)){
                        auxTime = (width/2 + p.getRadius() - p.getX()) / p.getVx();
                        wallIndex = MIDDLE_WALL;
                    }
                    else{
                        auxTime = (p.getRadius() - p.getX())/p.getVx();
                        wallIndex = LEFT_WALL;
                    }
                } else {
                    auxTime = (p.getRadius() - p.getX())/p.getVx();
                    wallIndex = LEFT_WALL;
                }
            }

            if (auxTime < timeToCollision) {
                timeToCollision = auxTime;
                collissionParticles[0] = index;
                collissionParticles[1] = wallIndex;
            }

            // Check vertical collision
            if (p.getVy() > 0){
                auxTime = (height - p.getRadius() - p.getY())/p.getVy();
                wallIndex = UPPER_WALL;
            } else {
                auxTime = (p.getRadius() - p.getY())/p.getVy();
                wallIndex = DOWN_WALL;
            }

            if (auxTime < timeToCollision) {
                timeToCollision = auxTime;
                collissionParticles[1] = wallIndex;
            }

            index++;
        }

        return timeToCollision;
    }


    private double timeAgainstParticles(int[] collissionParticles){
        double timeToCollision = Integer.MAX_VALUE;
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

                if (auxTime < timeToCollision){
                    timeToCollision = auxTime;
                    collissionParticles[0] = i;
                    collissionParticles[1] = j;
                }
            }
        }

        return timeToCollision;
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


    private void resolveCollission(int[] collisionParticles){
        double vx;
        double vy;

        //resolve collision with walls
        switch (collisionParticles[1]){
            case LEFT_WALL:
            case RIGHT_WALL:
            case MIDDLE_WALL:
                vx = particles.get(collisionParticles[0]).getVx();
                particles.get(collisionParticles[0]).setVx(-vx);
                break;
            case UPPER_WALL:
            case DOWN_WALL:
                vy = particles.get(collisionParticles[0]).getVy();
                particles.get(collisionParticles[0]).setVy(-vy);
                break;
            default:
                resolveCollisionWithParticles(collisionParticles);
        }
    }


    private void resolveCollisionWithParticles(int[] collisionParticles){
        Particle p1 = particles.get(collisionParticles[0]);
        Particle p2 = particles.get(collisionParticles[1]);

        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double deltaVx = p2.getVx() - p1.getVx();
        double deltaVy = p2.getVy() - p1.getVy();
        double sigma = p1.getRadius() + p2.getRadius();
        double deltaVdeltaR =deltaVx*deltaX + deltaVy*deltaY;

        double J = (2*(p1.getMass()*p2.getMass())*deltaVdeltaR) / sigma * (p1.getMass() + p2.getMass());

        double Jx = (J *deltaX) / sigma;
        double Jy = (J *deltaY) / sigma;

        p1.setVx(p1.getVx() + (Jx/p1.getMass()));
        p1.setVy(p1.getVy() + (Jy/p1.getMass()));
        p2.setVx(p2.getVx() - (Jx/p2.getMass()));
        p2.setVy(p2.getVy() - (Jy/p2.getMass()));

    }
}
