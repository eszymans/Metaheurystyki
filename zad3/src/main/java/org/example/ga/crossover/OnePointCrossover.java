package org.example.ga.crossover;

import org.example.model.Individual;

import java.util.Random;

public class OnePointCrossover implements CrossoverOperator{
    @Override
    public Individual[] crossover(Individual p1, Individual p2, Random rnd) {
        int n = p1.getGenes().length;
        int cp = rnd.nextInt(1,n-1);
        boolean[] c1 = new boolean[n];
        boolean[] c2 = new boolean[n];

        for (int i = 0; i < n; i++) {
            if(cp > i){
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
