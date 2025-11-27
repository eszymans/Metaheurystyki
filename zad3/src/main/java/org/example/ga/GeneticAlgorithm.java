package org.example.ga;

import org.example.ga.crossover.CrossoverOperator;
import org.example.ga.repair.GreedyRepair;
import org.example.ga.selection.SelectionStrategy;
import org.example.model.Backpack;
import org.example.model.Individual;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {
    private final Backpack backpack;
    private final Population population;
    private final FitnessEvaluator evaluator;
    private final Mutation mutation;
    private final GreedyRepair repair;
    private final CrossoverOperator crossover;
    private final SelectionStrategy strategy;
    private final Random rnd;

    private final int popSize;
    private final double pc;

    private Individual worstSolution;
    private double mean;

    // Listy do przechowywania historii
    private final List<Double> avgHistory = new ArrayList<>();
    private final List<Double> bestHistory = new ArrayList<>();

    public GeneticAlgorithm(
            Backpack backpack,
            int popSize,
            double pc,
            double pm,
            SelectionStrategy strategy,
            CrossoverOperator crossover,
            long seed
    ) {
        this.backpack = backpack;
        this.popSize = popSize;
        this.pc = pc;
        this.strategy = strategy;
        this.crossover = crossover;
        this.rnd = new Random(seed);
        this.population = new Population(popSize);
        this.evaluator = new FitnessEvaluator(backpack);
        this.repair = new GreedyRepair(backpack);
        this.mutation = new Mutation(pm);
        // Inicjalizacja ewaluatora (techniczna)
        this.evaluator.evaluate(new Individual(backpack.size()));
    }

    public void initializeRandom() {
        this.population.getPopulation().clear();
        this.avgHistory.clear();
        this.bestHistory.clear();

        for (int i = 0; i < popSize; i++) {
            Individual ind = new Individual(backpack.size());
            ind.randomize(rnd);
            evaluator.evaluate(ind);
            population.add(ind);
        }
    }

    public Individual run(int generation) {
        initializeRandom();

        population.sortByFitness(); // Sortujemy, żeby getBest() był poprawny
        avgHistory.add(population.getMean());
        bestHistory.add((double) population.getBest().getFitness());

        for (int gen = 0; gen < generation; gen++) {
            Population newPop = new Population(popSize);

            while (newPop.size() < popSize) {
                Individual parent1 = strategy.select(population, rnd);
                Individual parent2 = strategy.select(population, rnd);

                Individual child1 = parent1.copy();
                Individual child2 = parent2.copy();

                if(rnd.nextDouble() < pc){
                    Individual[] offs = crossover.crossover(parent1, parent2, rnd);
                    child1 = offs[0];
                    child2 = offs[1];
                }

                mutation.mutate(child1, rnd);
                mutation.mutate(child2, rnd);

                //repair.repair(child1);
                evaluator.evaluate(child1);
                if (newPop.size() < popSize) newPop.add(child1);

                //repair.repair(child2);
                evaluator.evaluate(child2);
                if (newPop.size() < popSize) newPop.add(child2);
            }

            // --- ZMIANA KLUCZOWA ---
            // Najpierw podmieniamy populację na nową...
            this.population.getPopulation().clear();
            this.population.getPopulation().addAll(newPop.getPopulation());

            // ...potem sortujemy nową populację...
            this.population.sortByFitness();

            // ...i DOPIERO TERAZ zapisujemy statystyki TEGO NOWEGO pokolenia
            avgHistory.add(population.getMean());
            bestHistory.add((double) population.getBest().getFitness());
        }

        population.sortByFitness();
        this.worstSolution = population.getWorst();
        this.mean = population.getMean();
        return population.getBest();
    }

    public Individual getWorstSolution() {
        return worstSolution;
    }

    public List<Double> getAvgHistory() {
        return avgHistory;
    }

    public List<Double> getBestHistory() {
        return bestHistory;
    }

    public double getMean(){
        return mean;
    }
}