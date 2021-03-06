package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CIMMatrix {

    private final Cell[][] matrix;
    private final double l;
    private int m;
    private final double rc;

    public CIMMatrix(double l, double rMax, double rc, List<Particle> particles,int M) {
        this.l = l;
        this.m = M > 0 && M < l/(rc+2*rMax) ? M : (int) Math.floor(l/(rc+2*rMax));
        if(m == l/(rc+2*rMax)){ //respect the < strict in case m was already an int before casting
            m -=1;
        }
        if (m == 0) {
            m = 1;
        }
        this.rc = rc;
        matrix = new Cell[m][m];
        fillMatrix(particles);
    }

    private void fillMatrix(List<Particle> particles) {
        int rowIndex;
        int colIndex;

        for(int i = 0; i < m; i++){
            for(int j = 0; j < m; j++){
                matrix[i][j] = new Cell();
            }
        }

        for (Particle particle : particles) {
            rowIndex = ((int) (particle.getX() / ((float) l / m))) % m;
            colIndex = ((int) (particle.getY() / ((float) l / m))) % m;
            matrix[rowIndex][colIndex].add(particle);
        }
    }

    public HashMap<Integer, List<Integer>> getNeighbours(boolean withContour){
        HashMap<Integer,List<Integer>> neighbours = new HashMap<>();

        for (int i = 0; i < m; i++){
            for (int j = 0; j < m; j++) {
                for (Particle particle : matrix[i][j].getParticles()) {

                    if(!neighbours.containsKey(particle.getId())) {
                        neighbours.put(particle.getId(), new ArrayList<>());
                    }

                    if (withContour){ //with periodic contour
                        checkSelf(neighbours.get(particle.getId()), particle, matrix[i][j].getParticles());
                        checkNeighbours(particle,neighbours, matrix[(i+m-1)%m][(j+1)%m].getParticles(), true);
                        checkNeighbours(particle,neighbours, matrix[i][(j+1)%m].getParticles(), true);
                        checkNeighbours(particle,neighbours, matrix[(i+1)%m][(j+1)%m].getParticles(), true);
                        checkNeighbours(particle,neighbours, matrix[(i+1)%m][j].getParticles(), true);

                    }else {
                        checkSelf(neighbours.get(particle.getId()), particle, matrix[i][j].getParticles());
                        if(i<m-1){
                            checkNeighbours(particle,neighbours, matrix[i+1][j].getParticles(), false);
                        }
                        if(j<m-1) {
                            checkNeighbours(particle,neighbours, matrix[i][j+1].getParticles(), false);
                            if(i<m-1){
                                checkNeighbours(particle,neighbours, matrix[i+1][j+1].getParticles(), false);
                            }
                            if (i > 0){
                                checkNeighbours(particle,neighbours, matrix[i-1][j+1].getParticles(), false);
                            }
                        }
                    }
                }
            }
        }
        return neighbours;
    }

    private void checkSelf(List<Integer> neighbours, Particle particle, List<Particle> neighbourCell){
        for(Particle neighbour : neighbourCell){
            if(neighbour.getId() != particle.getId()){ //avoids adding neighbours in the same cell twice
                if(particle.getDistance(neighbour,l,false) <= rc){
                    neighbours.add(neighbour.getId());
                }
            }
        }
    }

    private void checkNeighbours(Particle particle, HashMap<Integer, List<Integer>> neighbours, List<Particle> neighbourCell, boolean withContour){
        double distance;
        for (Particle neighbour : neighbourCell) {
            distance = particle.getDistance(neighbour,l,withContour);
            if(distance <= rc){
                neighbours.get(particle.getId()).add(neighbour.getId());
                if(!neighbours.containsKey(neighbour.getId())){
                    neighbours.put(neighbour.getId(), new ArrayList<>());
                }
                neighbours.get(neighbour.getId()).add(particle.getId());
            }
        }
    }
}
