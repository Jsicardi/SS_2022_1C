public class TransformingAction {
    private final double timestamp;
    private final Particle zombie;
    private final Particle human;
    private TransformationType type;


    public TransformingAction(double timestamp, Particle zombie, Particle human, TransformationType type) {
        this.timestamp = timestamp;
        this.zombie = zombie;
        this.human = human;
        this.type = type;
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

    public TransformationType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TransformingAction{" +
                "timestamp=" + timestamp +
                ", zombie=" + zombie +
                ", human=" + human +
                '}';
    }
}
