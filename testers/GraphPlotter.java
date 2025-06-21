import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GraphPlotter {
    public static void plot(int[] xVals, double[] yVals, String fileName, String title) {
        if (xVals.length == 0 || yVals.length != xVals.length) {
            throw new IllegalArgumentException("Mismatched data lengths");
        }
        int width = 800;
        int height = 600;
        int margin = 60;

        double maxY = 0;
        for (double v : yVals) {
            if (v > maxY) {
                maxY = v;
            }
        }
        if (maxY == 0) {
            maxY = 1;
        }

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // axes
        g.setColor(Color.BLACK);
        g.drawLine(margin, height - margin, width - margin, height - margin); // x
        g.drawLine(margin, margin, margin, height - margin); // y

        // x labels
        for (int i = 0; i < xVals.length; i++) {
            int x = margin + (i * (width - 2 * margin) / (xVals.length - 1));
            g.drawLine(x, height - margin - 5, x, height - margin + 5);
            String label = Integer.toString(xVals[i]);
            int labelW = g.getFontMetrics().stringWidth(label);
            g.drawString(label, x - labelW / 2, height - margin + 20);
        }

        // y labels
        int ySteps = 10;
        for (int i = 0; i <= ySteps; i++) {
            int y = height - margin - i * (height - 2 * margin) / ySteps;
            g.drawLine(margin - 5, y, margin + 5, y);
            String label = String.format("%.1f", (maxY * i) / ySteps);
            int labelW = g.getFontMetrics().stringWidth(label);
            g.drawString(label, margin - labelW - 10, y + 5);
        }

        // draw title
        int titleW = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (width - titleW) / 2, margin / 2);

        // plot points and lines
        g.setColor(Color.RED);
        for (int i = 0; i < xVals.length - 1; i++) {
            int x1 = margin + (i * (width - 2 * margin) / (xVals.length - 1));
            int x2 = margin + ((i + 1) * (width - 2 * margin) / (xVals.length - 1));
            int y1 = height - margin - (int) (yVals[i] / maxY * (height - 2 * margin));
            int y2 = height - margin - (int) (yVals[i + 1] / maxY * (height - 2 * margin));
            g.drawLine(x1, y1, x2, y2);
            g.fillOval(x1 - 3, y1 - 3, 6, 6);
        }
        int xLast = margin + ((xVals.length - 1) * (width - 2 * margin) / (xVals.length - 1));
        int yLast = height - margin - (int) (yVals[yVals.length - 1] / maxY * (height - 2 * margin));
        g.fillOval(xLast - 3, yLast - 3, 6, 6);

        g.dispose();
        try {
            ImageIO.write(img, "png", new File(fileName));
        } catch (IOException e) {
            throw new RuntimeException("Failed to save graph", e);
        }
    }
}
