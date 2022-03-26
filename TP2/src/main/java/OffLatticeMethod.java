import main.java.CIMMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OffLatticeMethod {

    private final int l;
    private final double r;
    private final double n;
    private Map<Integer, ExtendedParticle> particles;
    private final int steps;

    public OffLatticeMethod(int l, double r, double n, Map<Integer, ExtendedParticle> particles, int steps) {
        this.l = l;
        this.r = r;
        this.n = n;
        this.particles = particles;
        this.steps = steps;
    }

    public void offLattice() throws IOException {

        CIMMatrix cimMatrix;
        for (int i = 0; i <= steps; i++) {
            //Save positions and theta for output (maybe write to file here)
            OffLatticeHelper.addOutputStep(new ArrayList<>(particles.values()),i);

            //Move particles
            for (ExtendedParticle p : particles.values()) {
                moveParticle(p);
            }

            //Ask for neighbours of every particle to CIM
            cimMatrix = new CIMMatrix(l,0,r,new ArrayList<>(particles.values()),0);
            HashMap<Integer, List<Integer>> neighbours = cimMatrix.getNeighbours(true);



            //Calculate new directions (add new particle to new list because we need data from all neighbouring particles for every direction update)
            particles = updateDirection(particles, neighbours);
        }
    }

    private void moveParticle(ExtendedParticle p){
        //Calculation to update particle position based on v and theta
        double newX = p.getX() + p.getV() * Math.cos(p.getTheta());
        double newY = p.getY() + p.getV() * Math.sin(p.getTheta());

        //Check if particle should move to other side of board (periodic contour condition)
        if (newX > l)
            newX = newX - l;
        else if (newX < 0)
            newX = newX + l;
        if (newY > l)
            newY = newY - l;
        else if (newY < 0)
            newY = newY + l;

        //Update
        p.setX(newX);
        p.setY(newY);
    }

    private Map<Integer, ExtendedParticle> updateDirection(Map<Integer, ExtendedParticle> particles, HashMap<Integer, List<Integer>> neighboursMap){
        Map<Integer, ExtendedParticle> newMap = new HashMap<>();

        for (ExtendedParticle p: particles.values()){
            //Get random noise for angle between -n/2 and n/2
            double noise = ((Math.random() * (-n - n)) + -n)/2;

            //New particles with updated theta to avoid losing current data for other particle's direction update calculation
            double promSin = Math.sin(p.getTheta());
            double promCos = Math.cos(p.getTheta());
            List<Integer> neighboursIds = neighboursMap.get(p.getId());
            List<ExtendedParticle> neighbours = new ArrayList<>();
            for (Integer id:neighboursIds){
                neighbours.add(particles.get(id));
            }
            for (ExtendedParticle n:neighbours){
                promSin += Math.sin(n.getTheta());
                promCos += Math.cos(n.getTheta());
            }

            double newTheta = Math.atan2(p.getV() * promCos,p.getV() * promSin);

            newMap.put(p.getId(), new ExtendedParticle(p.getId(), p.getX(), p.getY(), p.getV(), (newTheta + noise) % (2*Math.PI)));
        }

        return newMap;
    }

}
