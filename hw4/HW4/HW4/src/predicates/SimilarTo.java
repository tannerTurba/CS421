package predicates;

public class SimilarTo<T> implements Predicate<T> {
    private T reference;
    private Metric<T> metric;
    private double threshold;
    
    public SimilarTo(T reference, Metric<T> metric, double threshold) {
        this.reference = reference;
        this.metric = metric;
        this.threshold = threshold;
    }

    @Override
    public boolean accepts(T t) {
        return metric.distance(reference, t) <= threshold;
    }
    
}