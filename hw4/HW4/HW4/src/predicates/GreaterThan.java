package predicates;

public class GreaterThan<T extends Comparable<T>> implements Predicate<T> {
    private T reference;

    public GreaterThan(T reference) {
        this.reference = reference;
    }

    @Override
    public boolean accepts(T t) {
        return t.compareTo(reference) > 0;
    }
}
