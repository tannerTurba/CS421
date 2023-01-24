package predicates;

public interface Metric<T> {
    public double distance(T t1, T t2);
}
