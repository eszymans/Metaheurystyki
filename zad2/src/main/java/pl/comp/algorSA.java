package pl.comp;

import java.util.Random;
import static java.lang.Math.exp;

public class algorSA {

    private double T0;  //temperatura początkowa, maksymalna
    private double alpha;  // sposób chłodzenia
    private int M;      // liczba interacji
    private double k;      // wspolczynnik k

    private static final Random rand = new Random();

    public algorSA(double t0, double alfa, int m, double k) {
        T0 = t0;
        this.alpha = alfa;
        M = m;
        this.k = k;
    }

    // ---------- Przykładowe funkcje testowe ----------
    // 1D: f(x) = x * sin(10*pi*x) + 1  (przykład z rozdziału)
    public double func(double x) {
        return x * Math.sin(10 * Math.PI * x) + 1;
    }

    // 2D: przykładowa funkcja (przykład z Twojego oryginalnego kodu) - zwraca wartość (maxymalizujemy)
    public double func(double x, double y) {
        return 8 * Math.exp(-Math.pow(x + 12, 2) - Math.pow(y + 12, 2))
                + 9 / (1 + Math.pow(x + 12, 2) + Math.pow(y - 12, 2))
                + 20 / (Math.pow(Math.cosh(x - 12), 2) + Math.pow(Math.cosh(y + 12), 2))
                + 176 / ((Math.pow(x - 12, 2) + 2 + Math.exp(-x + 12)) * (Math.exp(y - 12) + 2 + Math.exp(-y + 12)));
    }

    private double losowySasiad(double s, double s1, double s2, double T) {
        double krok = (s2 - s1) * 0.1 * (T / T0);
        return s + (rand.nextDouble() * 2 - 1) * krok;
    }

    // zakres rozwiazania s: [x1;x2],
    public double algorithm(double x1, double x2){
        double x = x1 + (x2 - x1)*rand.nextDouble();  // wybierac x (rozwiazanie) losowe
        double T = T0;
        double bestX = x;
        double bestVal = func(x);

        for (int i = 0; i <= M; i++) {
            double xLosowySasiad = Math.max(x1, Math.min(x2, losowySasiad(x, x1, x2, T)));
            double roznica = func(xLosowySasiad) - func(x);

            if (roznica > 0) {
                x = xLosowySasiad;
            } else {
                double p = exp(roznica / (k * T));
                if (rand.nextDouble() < p) {
                    x = xLosowySasiad;
                }
            }
            if (func(x) > bestVal) {
                bestX = x;
                bestVal = func(x);
            }

            T = T * alpha;
        }
        return bestX;
    }

    // zakres rozwiazania s : [x1;x2]
    // zakres rozwiazania y : [y1;y2]
    public double[] algorithm(double x1, double x2, double y1, double y2){
        double x = x1 + (x2 - x1)*rand.nextDouble();
        double y = y1 + (y2 - y1)*rand.nextDouble();
        double bestX = x, bestY = y;
        double bestVal = func(x, y);

        double T = T0;

        for(int i = 0; i <= M; i++) {
            double xLosowySasiad = Math.max(x1, Math.min(x2, losowySasiad(x, x1, x2, T)));
            double yLosowySasiad = Math.max(y1, Math.min(y2, losowySasiad(y, y1, y2, T)));
            double roznica= func(xLosowySasiad, yLosowySasiad) -  func(x, y);

            if (roznica > 0) {
                x = xLosowySasiad;
                y = yLosowySasiad;
            } else {
                double p = exp(roznica / (k * T));
                if (rand.nextDouble() < p) {
                    x = xLosowySasiad;
                    y = yLosowySasiad;
                }
            }
            T = T * alpha;

            if (func(x, y) > bestVal) {
                bestX = x;
                bestY = y;
                bestVal = func(x, y);
            }

        }
        return new double[]{bestX, bestY};
    }
}
