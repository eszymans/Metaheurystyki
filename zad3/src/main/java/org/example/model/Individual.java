package org.example.model;

import java.util.Arrays;
import java.util.Random;

public class Individual implements Comparable<Individual> {
    private boolean[] genes;
    private int fitness = -1;
    private int weight = -1;

    public Individual(int lenght) {
        genes = new boolean[lenght];
    }

    public Individual(boolean[] genes){
        this.genes = genes;
    }

    public int getFitness() {
        return fitness;
    }
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public int getWeight() {
        return weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean[] getGenes() {
        return genes;
    }

    public void randomize(Random rnd) {
        for (int i = 0; i < genes.length; i++) {
            this.genes[i] = rnd.nextBoolean();
        }
        fitness = -1;
        weight = -1;
    }

    public Individual copy() {
        Individual copy = new Individual(genes);
        copy.setWeight(weight);
        copy.setFitness(fitness);
        return copy;
    }

    public String toString() {
        return "Individual{fitness=" + fitness + ", weight=" + weight + ", genes=" + Arrays.toString(genes) + "}";
    }

    @Override
    public int compareTo(Individual o) {
        return Integer.compare(o.getFitness(), this.getFitness());
    }
}
