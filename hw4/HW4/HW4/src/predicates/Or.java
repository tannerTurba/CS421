package predicates;

@SuppressWarnings("unchecked")
public class Or<T> implements Predicate<T> {
    private Predicate<T>[] referencePredicates;

    public Or(Predicate<T>... referencePredicates) {
        this.referencePredicates = referencePredicates;
    }

    @Override
    public boolean accepts(T t) {
        for(Predicate<T> predicate : referencePredicates) {
            if(predicate.accepts(t)) {
                return true;
            }
        }
        return false;
    }
}
