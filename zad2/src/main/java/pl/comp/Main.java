package pl.comp;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // === Parametry z artykułu ===
        double T0_1D = 5;
        double alpha_1D = 0.997;
        int M_1D = 1200;
        double k_1D = 0.1;

        double T0_2D = 90;
        double alpha_2D = 0.999;
        int M_2D = 200;
        double k_2D = 0.5;

        // Zakresy funkcji
        double x1_1D = -1, x2_1D = 2;
        double x1_2D = -15, x2_2D = 15;
        double y1_2D = -15, y2_2D = 15;

        // Oczekiwane maksimum
        double expectedMax1D = 2.85027376656965;
        double expectedX1D = 1.850547;

        double expectedMax2D = 11.015598;
        double expectedX2D = 12.0;
        double expectedY2D = 12.0;

        // === CZĘŚĆ 1: Odtworzenie eksperymentów z artykułu ===
        System.out.println("=== Eksperyment 1D: Przykład 4 ===");
        runMultiple1D(T0_1D, alpha_1D, M_1D, k_1D, x1_1D, x2_1D, expectedMax1D, expectedX1D);

        System.out.println("\n=== Eksperyment 2D: Przykład 2 ===");
        runMultiple2D(T0_2D, alpha_2D, M_2D, k_2D, x1_2D, x2_2D, y1_2D, y2_2D, expectedMax2D, expectedX2D, expectedY2D);

        // === CZĘŚĆ 2: Analiza wpływu parametrów ===
        System.out.println("\n\n=== Analiza wpływu parametrów (1D) ===");
        analyzeParameters1D(x1_1D, x2_1D, expectedMax1D, k_1D);

        System.out.println("\n\n=== Analiza wpływu parametrów (2D) ===");
        // POPRAWKA: Użyto parametrów 2D (w oryginale były błędnie 1D)
        analyzeParameters2D(x1_2D, x2_2D, y1_2D, y2_2D, expectedMax2D, k_2D);
    }

    // --- eksperymenty ---
    private static void runMultiple1D(double T0, double alpha, int M, double k,
                                      double x1, double x2, double expectedMax, double expectedX) {
        for (int i = 1; i <= 5; i++) {
            long start = System.nanoTime();
            algorSA sa = new algorSA(T0, alpha, M, k);

            // ZMIANA: algorithm() zwraca teraz tablicę [bestX, iterFound]
            double[] result = sa.algorithm(x1, x2);
            double resultX = result[0];
            double iterFound = result[1];

            double resultVal = sa.func(resultX);
            long end = System.nanoTime();

            double error = Math.abs(resultVal - expectedMax);
            double xError = Math.abs(resultX - expectedX);
            double timeMs = (end - start) / 1e6;

            // ZMIANA: Dodano wydruk iteracji, w której znaleziono wynik
            System.out.printf("Run %d: x = %.6f, f(x) = %.6f, Δf = %.8f, Δx = %.8f, time = %.2f ms, iter = %.0f%n",
                    i, resultX, resultVal, error, xError, timeMs, iterFound);
        }
    }

    private static void runMultiple2D(double T0, double alpha, int M, double k,
                                      double x1, double x2, double y1, double y2,
                                      double expectedMax, double expectedX, double expectedY) {
        for (int i = 1; i <= 5; i++) {
            long start = System.nanoTime();
            algorSA sa = new algorSA(T0, alpha, M, k);

            // ZMIANA: algorithm() zwraca teraz tablicę [bestX, bestY, iterFound]
            double[] result = sa.algorithm(x1, x2, y1, y2);
            double iterFound = result[2]; // Odczyt iteracji

            double resultVal = sa.func(result[0], result[1]);
            long end = System.nanoTime();

            double error = Math.abs(resultVal - expectedMax);
            double xError = Math.abs(result[0] - expectedX);
            double yError = Math.abs(result[1] - expectedY);
            double timeMs = (end - start) / 1e6;

            // ZMIANA: Dodano wydruk iteracji, w której znaleziono wynik
            System.out.printf("Run %d: x = %.6f, y = %.6f, f(x,y) = %.6f, Δf = %.8f, Δx = %.8f, Δy = %.8f, time = %.2f ms, iter = %.0f%n",
                    i, result[0], result[1], resultVal, error, xError, yError, timeMs, iterFound);
        }
    }

    // --- ANALIZA WPŁYWU PARAMETRÓW ---
    private static void analyzeParameters1D(double x1, double x2, double expectedMax, double k) {
        double[] T0_values = {1, 5, 50, 100};
        double[] alpha_values = {0.9, 0.95, 0.99, 0.999};
        int[] M_values = {100, 500, 1000};

        // ZMIANA: Dodano kolumnę "Śr. iter."
        System.out.printf("%8s %8s %8s %15s %15s %15s %15s %15s%n",
                "T0", "alpha", "M", "Śr. czas [ms]", "Śr. f(x)", "Śr. |Δf|", "Odchylenie", "Śr. iter.");

        for (double T0 : T0_values) {
            for (double alpha : alpha_values) {
                for (int M : M_values) {
                    List<Double> wyniki = new ArrayList<>();
                    List<Double> iteracje = new ArrayList<>(); // ZMIANA: Lista na iteracje
                    long totalTime = 0;

                    // ZMIANA: Przekazanie listy iteracji do metody pomocniczej
                    totalTime = getTotalTime1D(x1, x2, k, T0, alpha, M, wyniki, iteracje, totalTime);

                    double avgF = srednia(wyniki);
                    double stdF = odchylenie(wyniki, avgF);
                    double avgTime = (totalTime / 5.0) / 1_000_000.0; // w ms
                    double diff = Math.abs(expectedMax - avgF);
                    double avgIter = srednia(iteracje); // ZMIANA: Obliczenie średniej iteracji

                    // ZMIANA: Dodano avgIter do wydruku
                    System.out.printf("%8.1f %8.3f %8d %15.2f %15.5f %15.5f %15.5f %15.0f%n",
                            T0, alpha, M, avgTime, avgF, diff, stdF, avgIter);
                }
            }
        }
    }

    // ZMIANA: Sygnatura metody przyjmuje listę iteracji
    private static long getTotalTime1D(double x1, double x2, double k, double T0, double alpha, int M, List<Double> wyniki, List<Double> iteracje, long totalTime) {
        for (int run = 0; run < 5; run++) {
            long start = System.nanoTime();
            algorSA sa = new algorSA(T0, alpha, M, k);

            // ZMIANA: Odczyt tablicy [bestX, iterFound]
            double[] result = sa.algorithm(x1, x2);
            double resultX = result[0];
            double iterFound = result[1];

            double resultVal = sa.func(resultX);
            long end = System.nanoTime();

            wyniki.add(resultVal);
            iteracje.add(iterFound); // ZMIANA: Zapisanie liczby iteracji
            totalTime += (end - start);
        }
        return totalTime;
    }

    private static void analyzeParameters2D(double x1, double x2, double y1, double y2, double expectedMax, double k) {
        double[] T0_values = {1, 5, 50, 100};
        double[] alpha_values = {0.9, 0.95, 0.99, 0.999};
        int[] M_values = {100, 500, 1000};

        // ZMIANA: Dodano kolumnę "Śr. iter."
        System.out.printf("%8s %8s %8s %15s %15s %15s %15s %15s%n",
                "T0", "alpha", "M", "Śr. czas [ms]", "Śr. f(x,y)", "Śr. |Δf|", "Odchylenie", "Śr. iter.");

        for (double T0 : T0_values) {
            for (double alpha : alpha_values) {
                for (int M : M_values) {
                    List<Double> wyniki = new ArrayList<>();
                    List<Double> iteracje = new ArrayList<>(); // ZMIANA: Lista na iteracje
                    long totalTime = 0;

                    // ZMIANA: Przekazanie listy iteracji
                    totalTime = getTotalTime2D(x1, x2, y1, y2, k, T0, alpha, M, wyniki, iteracje, totalTime);

                    double avgF = srednia(wyniki);
                    double stdF = odchylenie(wyniki, avgF);
                    double avgTime = (totalTime / 5.0) / 1_000_000.0; // w ms
                    double diff = Math.abs(expectedMax - avgF);
                    double avgIter = srednia(iteracje); // ZMIANA: Obliczenie średniej iteracji

                    // ZMIANA: Dodano avgIter do wydruku
                    System.out.printf("%8.1f %8.3f %8d %15.2f %15.5f %15.5f %15.5f %15.0f%n",
                            T0, alpha, M, avgTime, avgF, diff, stdF, avgIter);
                }
            }
        }
    }

    // ZMIANA: Sygnatura metody przyjmuje listę iteracji
    private static long getTotalTime2D(double x1, double x2, double y1, double y2, double k, double T0, double alpha, int M, List<Double> wyniki, List<Double> iteracje, long totalTime) {
        for (int run = 0; run < 5; run++) {
            long start = System.nanoTime();
            algorSA sa = new algorSA(T0, alpha, M, k);

            // ZMIANA: Odczyt tablicy [bestX, bestY, iterFound]
            double[] result = sa.algorithm(x1, x2, y1, y2);
            double iterFound = result[2];

            double resultVal = sa.func(result[0], result[1]);
            long end = System.nanoTime();

            wyniki.add(resultVal);
            iteracje.add(iterFound); // ZMIANA: Zapisanie liczby iteracji
            totalTime += (end - start);
        }
        return totalTime;
    }


    private static double srednia(List<Double> arr) {
        if (arr == null || arr.isEmpty()) return 0;
        double s = 0;
        for (double v : arr) s += v;
        return s / arr.size();
    }

    private static double odchylenie(List<Double> arr, double mean) {
        if (arr == null || arr.isEmpty()) return 0;
        double s = 0;
        for (double v : arr) s += Math.pow(v - mean, 2);
        return Math.sqrt(s / arr.size());
    }
}