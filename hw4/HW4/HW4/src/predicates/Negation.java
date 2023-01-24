package predicates;

public class Negation<T> implements Predicate<T> {
    Predicate<T> reference;

    public Negation(Predicate<T> reference) {
        this.reference = reference;
    }

    @Override
    public boolean accepts(T t) {
        return !reference.accepts(t);
    }
}
