public class TransformingAction {
    private final double timestamp;
    private final Particle zombie;
    private final Particle human;

    public TransformingAction(double timestamp, Particle zombie, Particle human) {
        this.timestamp = timestamp;
        this.zombie = zombie;
        this.human = human;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public Particle getZombie() {
        return zombie;
    }

    public Particle getHuman() {
        return human;
    }

}
