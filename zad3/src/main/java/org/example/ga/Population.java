package org.example.ga;

import org.example.model.Individual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Population {
    private final List<Individual> population;

    public Population(int size) {
        population = new ArrayList<>(size);
    }

    public List<Individual> getPopulation() {
        return population;
    }

    public void add(Individual ind) {
        population.add(ind);
    }

    public int size() {
        return population.size();
    }

    public void sortByFitness() {
        Collections.sort(population);
    }

    public Individual getBest() {
        sortByFitness();
        return population.getFirst();
    }

}
