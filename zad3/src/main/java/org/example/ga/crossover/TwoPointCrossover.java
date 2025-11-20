package org.example.ga.crossover;

import org.example.model.Individual;

import java.util.Random;

public class TwoPointCrossover implements CrossoverOperator{
    @Override
    public Individual[] crossover(Individual p1, Individual p2, Random rnd) {
        int n = p1.getGenes().length;
        int cp1 = rnd.nextInt(n-1);
        int cp2 = cp1 + 1 +rnd.nextInt(n-cp1-1);

        boolean[] c1 = new boolean[n];
        boolean[] c2 = new boolean[n];

        for (int i = 0; i < n; i++) {
            if(i < cp1 || i > cp2){
                c1[i] = p1.getGenes()[i];
                c2[i] = p2.getGenes()[i];
            } else {
                c1[i] = p2.getGenes()[i];
                c2[i] = p1.getGenes()[i];
            }
        }
        return new Individual[]{new Individual(c1), new Individual(c2)};
    }
}
