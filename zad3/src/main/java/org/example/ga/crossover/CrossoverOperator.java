package org.example.ga.crossover;

import org.example.model.Individual;

import java.util.Random;

public interface CrossoverOperator {
    Individual[] crossover(Individual parent1, Individual parent2, Random random);

}
