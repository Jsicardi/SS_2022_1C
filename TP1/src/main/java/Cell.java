package main.java;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    private List<Particle> particles;

    public Cell() {
        particles = new ArrayList<>();
    }

    public void add(Particle particle){
        particles.add(particle);
    }

    public List<Particle> getParticles() {
        return new ArrayList<>(particles);
    }
}
