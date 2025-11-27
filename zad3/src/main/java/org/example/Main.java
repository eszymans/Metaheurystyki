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

    private static class Result {
        final int popSize; final int iterations; final double crossProb; final double mutProb;
        final String selection; final String crossover; final double bestFitness;
        final long executionTime; final double worstFitness;

        public Result(int popSize, int iterations, double crossProb, double mutProb,
                      String selection, String crossover, double bestFitness,
                      long executionTime, double worstFitness) {
            this.popSize = popSize; this.iterations = iterations; this.crossProb = crossProb;
            this.mutProb = mutProb; this.selection = selection; this.crossover = crossover;
            this.bestFitness = bestFitness; this.executionTime = executionTime; this.worstFitness = worstFitness;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "%d;%d;%.3f;%.3f;%s;%s;%.0f;%d;%.0f",
                    popSize, iterations, crossProb, mutProb, selection, crossover, bestFitness, executionTime, worstFitness);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String path = "problem plecakowy dane CSV tabulatory.csv";
        int weight = 6404180;
        new File("wykresy").mkdirs();
        List<Item> items = CsvLoader.loadItems(path);
        Backpack backpack = new Backpack(weight, items);

        System.out.println("1. Pełny eksperyment");
        System.out.println("2. Własna konfiguracja");
        int choice = scanner.nextInt();

        if (choice == 1) runFullExperiment(backpack);
        if (choice == 2) runCustomConfiguration(backpack, scanner);
    }

    private static void runFullExperiment(Backpack backpack) throws IOException {
        int[] populationSizes = {50, 100, 200};
        int[] iterations = {200, 500, 1000};
        // Gęste parametry dla ładnych wykresów
        double[] crossoverProbs = {0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
        double[] mutationProbs = {0.001, 0.01, 0.02, 0.05, 0.1};

        List<Result> results = new ArrayList<>();
        String header = "N;T;Pc;Pm;Selection;Crossover;bestFitness;Time(ms);worstFitness";
        System.out.println(header);
        long baseSeed = 12345L;

        for (int N : populationSizes) {
            for (int T : iterations) {
                for (double pc : crossoverProbs) {
                    for (double pm : mutationProbs) {
                        runExperiment(backpack, N, T, pc, pm, new TournamentSelection(3), new OnePointCrossover(), "Tournament", "OnePoint", baseSeed++, results);
                        runExperiment(backpack, N, T, pc, pm, new RouletteSelection(), new OnePointCrossover(), "Roulette", "OnePoint", baseSeed++, results);
                        runExperiment(backpack, N, T, pc, pm, new TournamentSelection(3), new TwoPointCrossover(), "Tournament", "TwoPoint", baseSeed++, results);
                        runExperiment(backpack, N, T, pc, pm, new RouletteSelection(), new TwoPointCrossover(), "Roulette", "TwoPoint", baseSeed++, results);
                    }
                }
            }
        }
        saveResults(results, "results.csv", header);
        System.out.println("\nZakończono! Wyniki w pliku results.csv");
    }

    private static void runExperiment(Backpack backpack, int N, int T, double pc, double pm,
                                      SelectionStrategy selection, CrossoverOperator crossover, String selectionName, String crossoverName,
                                      long seed, List<Result> results) {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            GeneticAlgorithm ga = new GeneticAlgorithm(backpack, N, pc, pm, selection, crossover, seed + i);
            Individual best = ga.run(T);

            results.add(new Result(N, T, pc, pm, selectionName, crossoverName, best.getFitness(), (System.currentTimeMillis()-startTime)/(i+1), ga.getWorstSolution().getFitness()));

            if (N == 200 && T == 1000 && Math.abs(pc - 0.8) < 0.001 && Math.abs(pm - 0.05) < 0.001) {
                String filename = String.format(Locale.US, "wykresy/hist_%s_%s_Pc%.1f_Pm%.2f_run%d.csv", selectionName, crossoverName, pc, pm, i);
                saveEvolutionHistory(ga, filename);
            }
        }
        System.out.print(".");
    }

    private static void saveResults(List<Result> results, String filename, String header) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(header);
            for (Result result : results) writer.println(result);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void saveEvolutionHistory(GeneticAlgorithm ga, String filename) {
        List<Double> avgHistory = ga.getAvgHistory();
        List<Double> bestHistory = ga.getBestHistory();

        if (avgHistory == null || bestHistory == null) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Iteracja;Srednia;Najlepsza");

            int size = Math.min(avgHistory.size(), bestHistory.size());
            for (int i = 0; i < size; i++) {
                if (i % 20 == 0 || i == size - 1) {
                    writer.printf(Locale.US, "%d;%.2f;%.2f%n",
                            i,
                            avgHistory.get(i),
                            bestHistory.get(i));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static void runCustomConfiguration(Backpack backpack, Scanner scanner) {
        System.out.println("\n--- KONFIGURACJA WŁASNA ---");

        // 1. Pobieranie parametrów liczbowych
        System.out.print("Podaj rozmiar populacji (N): ");
        int N = scanner.nextInt();

        System.out.print("Podaj liczbę iteracji (T): ");
        int T = scanner.nextInt();

        // Uwaga: Jeśli masz polski system, w konsoli wpisuj liczby z przecinkiem (np. 0,8),
        // chyba że Scanner jest ustawiony na Locale.US.
        System.out.print("Podaj prawdopodobieństwo krzyżowania (Pc, np. 0.8): ");
        double pc = scanner.nextDouble();

        System.out.print("Podaj prawdopodobieństwo mutacji (Pm, np. 0.01): ");
        double pm = scanner.nextDouble();

        // 2. Wybór operatorów
        System.out.println("\nWybierz metodę selekcji:");
        System.out.println("1. Turniej (Tournament)");
        System.out.println("2. Ruletka (Roulette)");
        int selChoice = scanner.nextInt();

        SelectionStrategy selection;
        String selName;

        if (selChoice == 1) {
            System.out.print("Podaj rozmiar turnieju (np. 3): ");
            int tourSize = scanner.nextInt();
            selection = new TournamentSelection(tourSize);
            selName = "Tournament";
        } else {
            selection = new RouletteSelection();
            selName = "Roulette";
        }

        System.out.println("\nWybierz metodę krzyżowania:");
        System.out.println("1. Jednopunktowe (OnePoint)");
        System.out.println("2. Dwupunktowe (TwoPoint)");
        int crossChoice = scanner.nextInt();

        CrossoverOperator crossover;
        String crossName;

        if (crossChoice == 1) {
            crossover = new OnePointCrossover();
            crossName = "OnePoint";
        } else {
            crossover = new TwoPointCrossover();
            crossName = "TwoPoint";
        }

        // 3. Uruchomienie algorytmu
        System.out.println("\n--> Uruchamiam algorytm...");
        long startTime = System.currentTimeMillis();

        // Używamy stałego ziarna (12345L) dla powtarzalności lub System.currentTimeMillis() dla losowości
        GeneticAlgorithm ga = new GeneticAlgorithm(backpack, N, pc, pm, selection, crossover, 12345L);
        Individual best = ga.run(T);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // 4. Wyświetlenie wyników
        System.out.println("--------------------------------------------------");
        System.out.println("ZAKOŃCZONO!");
        System.out.println("Najlepsza wartość (Fitness): " + best.getFitness());
        System.out.println("Waga plecaka: " + best.getWeight());
        System.out.println("Najgorszy osobnik: " + ga.getWorstSolution().getFitness());
        System.out.println("Czas wykonania: " + duration + " ms");
        System.out.println("--------------------------------------------------");

        // 5. Zapis historii do pliku (żebyś mógł zrobić z tego wykres)
        String filename = String.format(Locale.US, "wykresy/custom_%s_%s_Pc%.2f_Pm%.3f.csv",
                selName, crossName, pc, pm);

        saveEvolutionHistory(ga, filename);
        System.out.println("Zapisano historię przebiegu do pliku: " + filename);
    }
}