package predicates;

@SuppressWarnings("unchecked")
public class And<T> implements Predicate<T> {
    private Predicate<T>[] referencePredicates;

    public And(Predicate<T>... referencePredicates) {
        this.referencePredicates = referencePredicates;
    }

    @Override
    public boolean accepts(T t) {
        for(Predicate<T> predicate : referencePredicates) {
            if(!predicate.accepts(t)) {
                return false;
            }
        }
        return true;
    }
}
