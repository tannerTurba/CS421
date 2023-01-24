package predicates;

import java.util.Collection;

public class SubsetOf<T> implements Predicate<Collection<? extends T>> {
    Collection<T> reference;

    public SubsetOf(Collection<T> reference) {
        this.reference = reference;
    }

    @Override
    public boolean accepts(Collection<? extends T> t) {
        return reference.containsAll(t);
    }
}
