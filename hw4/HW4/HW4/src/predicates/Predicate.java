package predicates;

public interface Predicate<T> {
    public boolean accepts(T t);
}
