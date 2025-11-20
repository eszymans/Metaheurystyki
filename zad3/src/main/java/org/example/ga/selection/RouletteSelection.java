package org.example.ga.selection;

import org.example.ga.Population;
import org.example.model.Individual;

import java.util.List;
import java.util.Random;

public class RouletteSelection implements SelectionStrategy{
    @Override
    public Individual select(Population population, Random rnd) {
        List<Individual> inds = population.getPopulation();
        double minFitness = Double.POSITIVE_INFINITY;
        double total = 0.0;
        for (Individual ind : inds) {
            if (ind.getFitness() < minFitness) {
                minFitness = ind.getFitness();
            }
        }
        double shift;
        if (minFitness <= 0) {
            shift = -minFitness + 1;
        } else {
            shift = 0;
        }
        for(Individual ind : inds) {
            total += ind.getFitness() + shift;
        }
        double r = rnd.nextDouble() * total;
        double acc = 0.0;
        for(Individual ind : inds) {
            acc += ind.getFitness() + shift;
            if(acc >= r) {
                return ind.copy();
            }
        }
        return inds.get(rnd.nextInt(inds.size())).copy();
    }
}
