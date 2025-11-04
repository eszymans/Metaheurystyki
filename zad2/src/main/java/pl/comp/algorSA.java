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
        this.T0 = t0;
        this.alpha = alfa;
        this.M = m;
        this.k = k;
    }

    // ---------- Przykładowe funkcje testowe ----------
    // 1D: f(x) = x * sin(10*pi*x) + 1  (przykład z rozdziału)
    public double func(double x) {
        return x * Math.sin(10 * Math.PI * x) + 1;
    }

    // 2D: przykładowa funkcja (przykład z Twojego oryginalnego kodu) - zwraca wartość (maxymalizujemy)
    public double func(double x, double y) {
        return 8 * Math.exp(-getPlusPow(x) - getPlusPow(y))
                + 9 / (1 + getPlusPow(x) + getMinusPow(y))
                + 20 / (getXCosh(x) + getYCosh(y))
                + 176 / ((getExp(x,12) + 2 + getExp(12, x)) * (getExp(y, 12) + 2 + getExp(12, y)));
    }

    private double losowySasiad(double s, double s1, double s2, double T) {
        double krok = (s2 - s1) * 0.1 * (T / T0);
        return s + (rand.nextDouble() * 2 - 1) * krok;
    }

    // zakres rozwiazania s: [x1;x2],
    public double algorithm(double x1, double x2){
        double x = getaDouble(x1, x2);  // wybierac x (rozwiazanie) losowe
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

            T *= alpha;
        }
        return bestX;
    }

    // zakres rozwiazania s : [x1;x2]
    // zakres rozwiazania y : [y1;y2]
    public double[] algorithm(double x1, double x2, double y1, double y2){
        double x = getaDouble(x1, x2);
        double y = getaDouble(y1, y2);
        double bestX = x, bestY = y;
        double bestVal = func(x, y);

        double T = T0;

        for(int i = 0; i <= M; i++) {
            double xLosowySasiad = Math.max(x1, Math.min(x2, losowySasiad(x, x1, x2, T)));
            double yLosowySasiad = Math.max(y1, Math.min(y2, losowySasiad(y, y1, y2, T)));
            double roznica = func(xLosowySasiad, yLosowySasiad) -  func(x, y);

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
            T *= alpha;

            if (func(x, y) > bestVal) {
                bestX = x;
                bestY = y;
                bestVal = func(x, y);
            }

        }
        return new double[]{bestX, bestY};
    }

    private static double getaDouble(double n1, double n2) {
        return n1 + (n2 - n1) * rand.nextDouble();
    }

    private static double getMinusPow(double n) {
        return Math.pow(n - 12, 2);
    }

    private static double getPlusPow(double n) {
        return Math.pow(n + 12, 2);
    }

    private double getYCosh(double y) {
        return Math.pow(Math.cosh(y + 12), 2);
    }

    private static double getXCosh(double x) {
        return Math.pow(Math.cosh(x - 12), 2);
    }

    private double getExp(double x, double y) {
        return Math.exp(x - y);
    }
}
