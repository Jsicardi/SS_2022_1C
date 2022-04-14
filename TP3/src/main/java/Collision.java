public class Collision{
    private final double time;
    private final int particle1Index;
    private final int particle2Index;

    public Collision(double time, int particle1Index, int particle2Index) {
        this.time = time;
        this.particle1Index = particle1Index;
        this.particle2Index = particle2Index;
    }

    public double getTime() {
        return time;
    }

    public int getParticle1Index() {
        return particle1Index;
    }

    public int getParticle2Index() {
        return particle2Index;
    }

    @Override
    public String toString() {
        return "Collision{" +
                "time=" + time +
                ", particle1Index=" + particle1Index +
                ", particle2Index=" + particle2Index +
                '}';
    }
}
