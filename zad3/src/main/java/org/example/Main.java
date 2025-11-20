package org.example;

import org.example.ga.GeneticAlgorithm;
import org.example.ga.crossover.OnePointCrossover;
import org.example.ga.crossover.TwoPointCrossover;
import org.example.ga.selection.RouletteSelection;
import org.example.ga.selection.TournamentSelection;
import org.example.model.Individual;
import org.example.model.Item;
import org.example.model.Backpack;

import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {
        String path = "problem plecakowy dane CSV tabulatory.csv";

        int weight = 6404180;
        List<Item> items = CsvLoader.loadItems(path);
        Backpack backpack = new Backpack(weight,items);

        int N = 50;
        int T = 200;
        double pc = 0.6;
        double pm = 0.01;
        long seed = 12345L;

        GeneticAlgorithm ga1 = new GeneticAlgorithm(backpack, N, pc, pm,
                new TournamentSelection(3),
                new OnePointCrossover(), seed);
        Individual best1 = ga1.run(T);
        System.out.println("Tournament + OnePoint best: " + best1);

        // --- Przykład 2: Roulette + Two-point
        GeneticAlgorithm ga2 = new GeneticAlgorithm(backpack, N, pc, pm,
                new RouletteSelection(),
                new TwoPointCrossover(), seed + 1);
        Individual best2 = ga2.run(T);
        System.out.println("Roulette + TwoPoint best: " + best2);
    }
}