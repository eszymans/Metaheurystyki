package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoutePlotter extends JFrame {
    public RoutePlotter(List<Point> points, int[] bestRoute, double length) {
        setTitle("Mapa Wesołego Miasteczka - Trasa ACO (Długość: " + String.format("%.2f", length) + ")");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Skalowanie do okna (automatyczne dopasowanie)
                double minX = Double.MAX_VALUE, maxX = Double.MIN_VALUE;
                double minY = Double.MAX_VALUE, maxY = Double.MIN_VALUE;
                for (Point p : points) {
                    if (p.getX() < minX) minX = p.getX(); if (p.getX() > maxX) maxX = p.getX();
                    if (p.getY() < minY) minY = p.getY(); if (p.getY() > maxY) maxY = p.getY();
                }

                int w = getWidth();
                int h = getHeight();
                int padding = 50;

                double scaleX = (w - 2 * padding) / (maxX - minX);
                double scaleY = (h - 2 * padding) / (maxY - minY);

                // Rysowanie ścieżek
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(2));

                for (int i = 0; i < bestRoute.length - 1; i++) {
                    Point p1 = points.get(bestRoute[i]);
                    Point p2 = points.get(bestRoute[i+1]);
                    drawLink(g2, p1, p2, minX, minY, scaleX, scaleY, padding, h);
                }
                // Powrót
                drawLink(g2, points.get(bestRoute[bestRoute.length-1]), points.get(bestRoute[0]), minX, minY, scaleX, scaleY, padding, h);

                // Rysowanie punktów
                g2.setColor(Color.RED);
                for (Point p : points) {
                    int px = (int) ((p.getX() - minX) * scaleX) + padding;
                    int py = h - ((int) ((p.getY() - minY) * scaleY) + padding); // Odwrócenie osi Y
                    g2.fillOval(px - 5, py - 5, 10, 10);
                    g2.drawString(String.valueOf(p.getId()), px + 8, py);
                }
            }

            private void drawLink(Graphics2D g2, Point p1, Point p2, double minX, double minY, double scaleX, double scaleY, int padding, int h) {
                int x1 = (int) ((p1.getX() - minX) * scaleX) + padding;
                int y1 = h - ((int) ((p1.getY() - minY) * scaleY) + padding);
                int x2 = (int) ((p2.getX() - minX) * scaleX) + padding;
                int y2 = h - ((int) ((p2.getY() - minY) * scaleY) + padding);
                g2.drawLine(x1, y1, x2, y2);
            }
        };

        add(panel);
        setVisible(true);
    }
}
