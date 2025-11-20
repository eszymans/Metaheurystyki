package org.example.ga.selection;

import org.example.ga.Population;
import org.example.model.Individual;

import java.util.Random;

public interface SelectionStrategy {
    Individual select(Population population, Random rnd);
}
