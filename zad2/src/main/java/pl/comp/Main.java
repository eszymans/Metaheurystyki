package pl.comp;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== INTERFEJS TESTOWY ALGORYTMU SYMULOWANEGO WYŻARZANIA ===\n");

        // --- Wybór trybu działania ---
        System.out.println("Wybierz tryb działania programu:");
        System.out.println("1. Tryb interaktywny (ręczne wprowadzenie parametrów)");
        System.out.println("2. Tryb eksperymentalny (automatyczne testy i analiza parametrów)");
        System.out.print("Twój wybór: ");
        int mode = sc.nextInt();

        if (mode == 2) {
            // --- Uruchomienie eksperymentu ---
            System.out.println("\n=== URUCHAMIANIE TRYBU EKSPERYMENTALNEGO ===\n");
            Eksperyment.eksperyment(args);
            System.out.println("\n=== KONIEC EKSPERYMENTU ===");
            return; // zakończ po eksperymentach
        }

        // --- Tryb interaktywny ---
        System.out.println("\nWybierz funkcję testową:");
        System.out.println("1. Funkcja 1D");
        System.out.println("2. Funkcja 2D");
        System.out.print("Twój wybór: ");
        int choice = sc.nextInt();

        // --- Wprowadzenie parametrów ---
        System.out.println("\nPodaj parametry algorytmu:");
        System.out.print("Temperatura początkowa T0: ");
        double T0 = sc.nextDouble();
        System.out.print("Współczynnik chłodzenia alpha (np. 0.99): ");
        double alpha = sc.nextDouble();
        System.out.print("Liczba iteracji M: ");
        int M = sc.nextInt();
        System.out.print("Współczynnik kroku k: ");
        double k = sc.nextDouble();

        algorSA sa = new algorSA(T0, alpha, M, k);
        long start = System.nanoTime();

        if (choice == 1) {
            // --- Zakres dla funkcji 1D ---
            double x1 = -1, x2 = 2;
            double[] result = sa.algorithm(x1, x2);
            double bestX = result[0];
            double iterFound = result[1];
            double bestVal = sa.func(bestX);

            long end = System.nanoTime();
            double timeMs = (end - start) / 1e6;

            System.out.println("\n=== WYNIKI (1D) ===");
            System.out.printf("Najlepsze x: %.6f%n", bestX);
            System.out.printf("f(x) = %.6f%n", bestVal);
            System.out.printf("Iteracja znalezienia: %.0f%n", iterFound);
            System.out.printf("Czas wykonania: %.2f ms%n", timeMs);

        } else if (choice == 2) {
            // --- Zakres dla funkcji 2D ---
            double x1 = -15, x2 = 15;
            double y1 = -15, y2 = 15;

            double[] result = sa.algorithm(x1, x2, y1, y2);
            double bestX = result[0];
            double bestY = result[1];
            double iterFound = result[2];
            double bestVal = sa.func(bestX, bestY);

            long end = System.nanoTime();
            double timeMs = (end - start) / 1e6;

            System.out.println("\n=== WYNIKI (2D) ===");
            System.out.printf("Najlepsze x: %.6f%n", bestX);
            System.out.printf("Najlepsze y: %.6f%n", bestY);
            System.out.printf("f(x, y) = %.6f%n", bestVal);
            System.out.printf("Iteracja znalezienia: %.0f%n", iterFound);
            System.out.printf("Czas wykonania: %.2f ms%n", timeMs);

        } else {
            System.out.println("Niepoprawny wybór funkcji!");
        }

        System.out.println("\n=== KONIEC TESTU ===");
    }
}
