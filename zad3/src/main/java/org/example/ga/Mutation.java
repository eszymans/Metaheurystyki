package org.example.ga;

import org.example.model.Individual;

import java.util.Random;

public class Mutation {
    private final double pm;

    public Mutation(double pm) { this.pm = pm; }

    public void mutate(Individual ind, Random rnd) {
        boolean[] g = ind.getGenes();
        for (int i = 0; i < g.length; i++) {
            if (rnd.nextDouble() < pm) g[i] = !g[i];
        }
    }
}
