package org.example;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- ACO (TSP) ---");

        // Ścieżka do pliku z danymi
        String path = "data/input/A-n80-k10.txt";

        // Wyciągnięcie samej nazwy pliku (bez rozszerzenia), np. "A-n32-k5"
        File file = new File(path);
        String dataName = file.getName().replaceFirst("[.][^.]+$", "");

        List<Point> points = DataLoader.loadData(path);
        if (points == null || points.isEmpty()) {
            System.err.println("Nie udało się załadować punktów lub plik jest pusty: " + path);
            return;
        }

        System.out.println("Wczytano punktów: " + points.size() + " z pliku: " + dataName);

        System.out.println("Wybierz tryb:");
        System.out.println("1) Pojedyncze uruchomienie (konfigurowalne)");
        System.out.println("2) Eksperyment batch (wiele kombinacji parametrów, zapisz wyniki)");
        System.out.print("Wybór (1/2): ");
        int mode = readIntWithDefault(scanner, 1);

        if (mode == 1) {
            runSingleInteractive(points, scanner, dataName);
        } else {
            runBatchExperiments(points, dataName);
        }
    }

    private static void runSingleInteractive(List<Point> points, Scanner scanner, String dataName) {
        System.out.print("Liczba mrówek (numAnts, np. 50): ");
        int numAnts = readIntWithDefault(scanner, 50);

        System.out.print("Liczba iteracji (interations, np. 200): ");
        int iterations = readIntWithDefault(scanner, 200);

        System.out.print("Parametr alpha (np. 1.0): ");
        double alpha = readDoubleWithDefault(scanner, 1.0);

        System.out.print("Parametr beta (np. 5.0): ");
        double beta = readDoubleWithDefault(scanner, 5.0);

        System.out.print("Współczynnik parowania (evaporation, np. 0.5): ");
        double evaporation = readDoubleWithDefault(scanner, 0.5);

        System.out.print("Prawdopodobieństwo losowego wyboru (pRandom, np. 0.01): ");
        double pRandom = readDoubleWithDefault(scanner, 0.01);

        new File("wykresy_aco").mkdirs();
        new File("results").mkdirs();

        System.out.println("Uruchamiam ACO...");
        long start = System.currentTimeMillis();

        ACO aco = new ACO(points, numAnts, iterations, alpha, beta, evaporation, pRandom);
        aco.solve();

        long duration = System.currentTimeMillis() - start;

        System.out.println("-- Wynik --");
        System.out.println("Najlepsza długość trasy: " + aco.getBestTourLength());

        System.out.println("Czas wykonania (ms): " + duration);

        String params = String.format(Locale.US, "%s_A%d_I%d_al%.1f_be%.1f_ev%.2f_pr%.3f",
                dataName, numAnts, iterations, alpha, beta, evaporation, pRandom);

        String histFile = String.format(Locale.US, "results/%s_history_avg_best.csv", params);
        saveEvolutionHistory(aco, histFile);
        System.out.println("Zapisano historię do: " + histFile);

        SwingUtilities.invokeLater(() -> {
            new RoutePlotter(points, aco.getBestTourOrder(), aco.getBestTourLength());
        });
    }


    private static void runBatchExperiments(List<Point> points, String dataName) {
        int[] antsArr = {10, 20, 50, 100};
        double[] pRandomArr = {0.0, 0.01, 0.05, 0.1};
        double[] alphas = {0.5, 1.0, 2.0, 5.0};
        double[] betas = {1.0, 2.0, 5.0, 10.0};
        double[] rhos = {0.1, 0.3, 0.5, 0.7};
        int[] iterationsArr = {100, 500, 1000};

        int repeats = 5;

        new File("results").mkdirs();

        String summaryPath = "results/experiment_summary.csv";
        try (PrintWriter summary = new PrintWriter(new FileWriter(summaryPath))) {
            summary.println("id;data;ants;iterations;alpha;beta;rho;pRandom;repeats;mean_best;median_best;std_best;min_best;max_best;time_ms");

            int id = 1;
            for (int ants : antsArr) {
                for (int iterations : iterationsArr) {
                    for (double alpha : alphas) {
                        for (double beta : betas) {
                            for (double rho : rhos) {
                                for (double pRandom : pRandomArr) {

                                    System.out.printf("Exp %d: Ants=%d Al=%.1f Be=%.1f Rho=%.1f Pr=%.2f... ",
                                            id, ants, alpha, beta, rho, pRandom);

                                    String params = String.format(Locale.US, "%s_A%d_I%d_al%.1f_be%.1f_rho%.2f_pr%.3f",
                                            dataName, ants, iterations, alpha, beta, rho, pRandom);

                                    double[] bests = new double[repeats];
                                    long totalTime = 0;

                                    double[][] historiesBest = new double[repeats][iterations];
                                    double[][] historiesAvg  = new double[repeats][iterations];
                                    double[][] historiesWorst = new double[repeats][iterations];

                                    int[] bestTourOverall = null;
                                    double bestOverall = Double.MAX_VALUE;

                                    for (int r = 0; r < repeats; r++) {
                                        long t0 = System.currentTimeMillis();
                                        ACO aco = new ACO(points, ants, iterations, alpha, beta, rho, pRandom);
                                        aco.solve();
                                        long t1 = System.currentTimeMillis();

                                        double bestLen = aco.getBestTourLength();
                                        bests[r] = bestLen;

                                        List<Double> hBest = aco.getHistoryBest();
                                        List<Double> hAvg = aco.getHistoryAvg();
                                        List<Double> hWorst = aco.getHistoryWorst();

                                        for (int it = 0; it < iterations; it++) {
                                            if (it < hBest.size()) historiesBest[r][it] = hBest.get(it);
                                            if (it < hAvg.size())  historiesAvg[r][it]  = hAvg.get(it);
                                            if (it < hWorst.size()) historiesWorst[r][it] = hWorst.get(it);
                                        }

                                        String runFilename = String.format(Locale.US, "results/%s_id%03d_run%d.csv", params, id, r);
                                        try (PrintWriter runWriter = new PrintWriter(new FileWriter(runFilename))) {
                                            runWriter.println("iter;best;avg;worst");
                                            int len = Math.min(hBest.size(), Math.min(hAvg.size(), hWorst.size()));
                                            for(int i=0; i<len; i++) {
                                                runWriter.printf(Locale.US, "%d;%.6f;%.6f;%.6f%n",
                                                        i, hBest.get(i), hAvg.get(i), hWorst.get(i));
                                            }
                                        } catch (IOException e) {
                                            System.err.println("Błąd zapisu run: " + e.getMessage());
                                        }

                                        if (bestLen < bestOverall) {
                                            bestOverall = bestLen;
                                            bestTourOverall = aco.getBestTourOrder();
                                        }
                                        totalTime += (t1 - t0);
                                    }

                                    double mean = mean(bests);
                                    double med = median(bests);
                                    double std = std(bests, mean);
                                    double min = min(bests);
                                    double max = max(bests);

                                    summary.printf(Locale.US, "%d;%s;%d;%d;%.2f;%.2f;%.2f;%.3f;%d;%.4f;%.4f;%.4f;%.4f;%.4f;%d%n",
                                            id, dataName, ants, iterations, alpha, beta, rho, pRandom, repeats,
                                            mean, med, std, min, max, totalTime);

                                    String histPath = String.format(Locale.US, "results/%s_history_stats.csv", params + "_id" + String.format("%03d", id));
                                    try (PrintWriter hw = new PrintWriter(new FileWriter(histPath))) {
                                        hw.println("iter;mean_best;min_best;max_best;std_best;mean_avg;mean_worst");

                                        for (int it = 0; it < iterations; it++) {
                                            double[] colBest = new double[repeats];
                                            double[] colAvg = new double[repeats];
                                            double[] colWorst = new double[repeats];

                                            for (int r = 0; r < repeats; r++) {
                                                colBest[r] = historiesBest[r][it];
                                                colAvg[r] = historiesAvg[r][it];
                                                colWorst[r] = historiesWorst[r][it];
                                            }

                                            double meanBest = mean(colBest);
                                            double stdBest = std(colBest, meanBest);
                                            double minBest = min(colBest);
                                            double maxBest = max(colBest);

                                            double meanAvgVal = mean(colAvg);
                                            double meanWorstVal = mean(colWorst);

                                            hw.printf(Locale.US, "%d;%.6f;%.6f;%.6f;%.6f;%.6f;%.6f%n",
                                                    it, meanBest, minBest, maxBest, stdBest, meanAvgVal, meanWorstVal);
                                        }
                                    } catch (IOException e) {
                                        System.err.println("Błąd zapisu historii: " + e.getMessage());
                                    }

                                    if (bestTourOverall != null) {
                                        String tourPath = String.format(Locale.US, "results/%s_bestTour.txt", params + "_id" + String.format("%03d", id));
                                        try (PrintWriter tw = new PrintWriter(new FileWriter(tourPath))) {
                                            tw.println("bestLength=" + bestOverall);
                                            tw.print("tour=");
                                            for (int i = 0; i < bestTourOverall.length; i++) {
                                                tw.print(bestTourOverall[i]);
                                                if (i + 1 < bestTourOverall.length) tw.print(",");
                                            }
                                            tw.println();
                                        } catch (IOException e) { /* ignore */ }
                                    }

                                    System.out.println("OK");
                                    id++;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("Zakończono. Wyniki w folderze results/");
        } catch (IOException e) {
            System.err.println("Błąd pliku summary: " + e.getMessage());
        }
    }

    private static int readIntWithDefault(Scanner scanner, int defaultVal) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return defaultVal;
        try {
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private static double readDoubleWithDefault(Scanner scanner, double defaultVal) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) return defaultVal;
        try {
            return Double.parseDouble(line.replace(',', '.'));
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private static void saveEvolutionHistory(ACO aco, String filename) {
        List<Double> avg = aco.getHistoryAvg();
        List<Double> best = aco.getHistoryBest();
        if ((avg == null || best == null) || (avg.isEmpty() && best.isEmpty())) {
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Iteracja;Srednia;Najlepsza");
            int size = Math.min(avg.size(), best.size());
            for (int i = 0; i < size; i++) {
                writer.printf(Locale.US, "%d;%.4f;%.4f%n", i, avg.get(i), best.get(i));
            }
        } catch (IOException e) {
            System.err.println("Nie można zapisać pliku historii: " + e.getMessage());
        }
    }

    private static double mean(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double s = 0.0;
        for (double v : arr) s += v;
        return s / arr.length;
    }

    private static double median(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double[] copy = Arrays.copyOf(arr, arr.length);
        Arrays.sort(copy);
        int mid = copy.length / 2;
        if (copy.length % 2 == 0) return (copy[mid - 1] + copy[mid]) / 2.0;
        return copy[mid];
    }

    private static double std(double[] arr, double mean) {
        if (arr == null || arr.length == 0) return 0.0;
        double s = 0.0;
        for (double v : arr) s += (v - mean) * (v - mean);
        return Math.sqrt(s / arr.length);
    }

    private static double min(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double m = Double.MAX_VALUE;
        for (double v : arr) if (v < m) m = v;
        return m;
    }

    private static double max(double[] arr) {
        if (arr == null || arr.length == 0) return 0.0;
        double m = -Double.MAX_VALUE;
        for (double v : arr) if (v > m) m = v;
        return m;
    }
}