package org.example;

import org.example.ga.GeneticAlgorithm;
import org.example.ga.crossover.CrossoverOperator;
import org.example.ga.crossover.OnePointCrossover;
import org.example.ga.crossover.TwoPointCrossover;
import org.example.ga.selection.RouletteSelection;
import org.example.ga.selection.SelectionStrategy;
import org.example.ga.selection.TournamentSelection;
import org.example.model.Individual;
import org.example.model.Item;
import org.example.model.Backpack;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Locale;

public class Main {

    // Usunięto pole avgFitness
    private static class Result {
        final int popSize;
        final int iterations;
        final double crossProb;
        final double mutProb;
        final String selection;
        final String crossover;
        final double bestFitness;
        // final double avgFitness; // USUNIĘTE
        final long executionTime;
        final double worstFitness;

        public Result(int popSize, int iterations, double crossProb, double mutProb,
                      String selection, String crossover, double bestFitness,
                      long executionTime, double worstFitness) { // Usunięto avgFitness z konstruktora
            this.popSize = popSize;
            this.iterations = iterations;
            this.crossProb = crossProb;
            this.mutProb = mutProb;
            this.selection = selection;
            this.crossover = crossover;
            this.bestFitness = bestFitness;
            // this.avgFitness = avgFitness; // USUNIĘTE
            this.executionTime = executionTime;
            this.worstFitness = worstFitness;
        }

        @Override
        public String toString() {
            // Usunięto jedną wartość z formatowania
            return String.format(Locale.US, "%d;%d;%.2f;%.3f;%s;%s;%.0f;%d;%.0f",
                    popSize, iterations, crossProb, mutProb, selection, crossover,
                    bestFitness, executionTime, worstFitness);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String path = "problem plecakowy dane CSV tabulatory.csv";
        int weight = 6404180;

        // Tworzenie folderu na wykresy
        new File("wykresy").mkdirs();

        List<Item> items = CsvLoader.loadItems(path);
        Backpack backpack = new Backpack(weight, items);

        System.out.println("1. Pełny eksperyment (zapisuje historię dla N=200, T=1000)");
        System.out.println("2. Własna konfiguracja");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1:
                runFullExperiment(backpack);
                break;
            case 2:
                runCustomConfiguration(backpack, scanner);
                break;
            default:
                System.out.println("Nieprawidłowy wybór");
        }
    }

    private static void runFullExperiment(Backpack backpack) throws IOException {
        int[] populationSizes = {50, 100, 200};
        int[] iterations = {200, 500, 1000};
        double[] crossoverProbs = {0.6, 0.8, 1.0};
        double[] mutationProbs = {0.01, 0.05, 0.1};

        List<Result> results = new ArrayList<>();
        // Usunięto AvgFitness z nagłówka
        String header = "N;T;Pc;Pm;Selection;Crossover;bestFitness;Time(ms);worstFitness";
        System.out.println(header);

        long baseSeed = 12345L;

        for (int N : populationSizes) {
            for (int T : iterations) {
                for (double pc : crossoverProbs) {
                    for (double pm : mutationProbs) {
                        runExperiment(backpack, N, T, pc, pm,
                                new TournamentSelection(3), new OnePointCrossover(),
                                "Tournament", "OnePoint", baseSeed++, results);

                        runExperiment(backpack, N, T, pc, pm,
                                new RouletteSelection(), new OnePointCrossover(),
                                "Roulette", "OnePoint", baseSeed++, results);

                        runExperiment(backpack, N, T, pc, pm,
                                new TournamentSelection(3), new TwoPointCrossover(),
                                "Tournament", "TwoPoint", baseSeed++, results);

                        runExperiment(backpack, N, T, pc, pm,
                                new RouletteSelection(), new TwoPointCrossover(),
                                "Roulette", "TwoPoint", baseSeed++, results);
                    }
                }
            }
        }

        saveResults(results, "results.csv", header);
        System.out.println("Zakończono! Wyniki w pliku results.csv");
        System.out.println("Szczegółowe przebiegi dla wariantu N=200 T=1000 są w folderze 'wykresy/'");
    }

    private static void runCustomConfiguration(Backpack backpack, Scanner scanner) {
        System.out.print("N: "); int N = scanner.nextInt();
        System.out.print("T: "); int T = scanner.nextInt();
        System.out.print("Pc: "); double pc = scanner.nextDouble();
        System.out.print("Pm: "); double pm = scanner.nextDouble();

        System.out.println("1. Tournament, 2. Roulette");
        int selType = scanner.nextInt();
        SelectionStrategy selection = (selType == 1) ? new TournamentSelection(3) : new RouletteSelection();
        String selName = (selType == 1) ? "Tournament" : "Roulette";

        System.out.println("1. OnePoint, 2. TwoPoint");
        int crossType = scanner.nextInt();
        CrossoverOperator crossover = (crossType == 1) ? new OnePointCrossover() : new TwoPointCrossover();
        String crossName = (crossType == 1) ? "OnePoint" : "TwoPoint";

        GeneticAlgorithm ga = new GeneticAlgorithm(backpack, N, pc, pm, selection, crossover, 12345L);
        long startTime = System.currentTimeMillis();
        Individual best = ga.run(T);
        long executionTime = System.currentTimeMillis() - startTime;

        System.out.println("Best: " + best.getFitness());
        System.out.println("Time: " + executionTime + "ms");

        saveEvolutionHistory(ga, "przebieg_manualny.csv");
    }

    private static void runExperiment(
            Backpack backpack, int N, int T, double pc, double pm,
            SelectionStrategy selection, CrossoverOperator crossover,
            String selectionName, String crossoverName,
            long seed, List<Result> results) {

        long startTime = System.currentTimeMillis();

        double bestFitness = Double.NEGATIVE_INFINITY;
        double worstSolution = 0;

        GeneticAlgorithm bestGA = null;

        // Pętla 5 razy nadal jest, żeby znaleźć NAJLEPSZY wynik (ale nie liczymy średniej)
        for (int i = 0; i < 5; i++) {
            GeneticAlgorithm ga = new GeneticAlgorithm(
                    backpack, N, pc, pm, selection, crossover, seed + i);
            Individual best = ga.run(T);

            if (best.getFitness() > bestFitness) {
                bestFitness = best.getFitness();
                bestGA = ga;
            }
            // Zapisujemy najgorszy wynik z ostatniej iteracji (lub można szukać najgorszego globalnie)
            worstSolution = ga.getWorstSolution().getFitness();
        }

        long executionTime = System.currentTimeMillis() - startTime;

        // Usunięto obliczanie avgFitness
        Result result = new Result(N, T, pc, pm, selectionName, crossoverName,
                bestFitness, executionTime, worstSolution);
        results.add(result);

        System.out.println(result);

        if (N == 200 && T == 1000) {
            String filename = String.format(Locale.US, "wykresy/hist_%s_%s_Pc%.1f_Pm%.2f.csv",
                    selectionName, crossoverName, pc, pm);

            if (bestGA != null) {
                saveEvolutionHistory(bestGA, filename);
            }
        }
    }

    private static void saveResults(List<Result> results, String filename, String header) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(header);
            for (Result result : results) {
                writer.println(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveEvolutionHistory(GeneticAlgorithm ga, String filename) {
        List<Double> avgHistory = ga.getAvgHistory();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Iteracja;Srednia;Najlepsza");

            for (int i = 0; i < avgHistory.size(); i++) {
                writer.printf(Locale.US, "%d;%.2f;%.2f%n",
                        i,
                        avgHistory.get(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}