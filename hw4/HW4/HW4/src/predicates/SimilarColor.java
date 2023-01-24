package predicates;

import java.awt.Color;

public class SimilarColor extends SimilarTo<Color> {
    private static class ColorMetric implements Metric<Color> {

        public ColorMetric() {

        }

        @Override
        public double distance(Color t1, Color t2) {
            int redDiff = Math.abs(t1.getRed() - t2.getRed());
            int greenDiff = Math.abs(t1.getGreen() - t2.getGreen());
            int blueDiff = Math.abs(t1.getBlue() - t2.getBlue());
            return redDiff + greenDiff + blueDiff;
        }
    }

    private static Metric<Color> colorMetric = new ColorMetric();

    public SimilarColor(Color reference, double threshold) {
        super(reference, colorMetric, threshold);
    }
    
}
