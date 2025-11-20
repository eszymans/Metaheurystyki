package org.example.ga.selection;

import org.example.ga.Population;
import org.example.model.Individual;

import java.util.Random;

public class TournamentSelection implements SelectionStrategy {

    private final int tournamentSize;


    public TournamentSelection(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    @Override
    public Individual select(Population population, Random rnd) {
        Individual best = null;
        for (int i = 0; i < tournamentSize; i++) {
            Individual challanger = population.getPopulation().get(rnd.nextInt(population.size()));
            if (best == null || best.getFitness() < challanger.getFitness()) {
                best = challanger;
            }
        }
        return best;
    }
}
