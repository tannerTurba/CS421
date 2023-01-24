package predicates;

import java.util.Collection;
import java.util.Iterator;

public class AcceptsSomeOf<T> implements Predicate<Predicate<T>> { 
    private Collection<T> reference;

    public AcceptsSomeOf(Collection<T> reference) {
        this.reference = reference;
    }

    @Override
    public boolean accepts(Predicate<T> predicate) {
        Iterator<T> iterator = reference.iterator();
        while(iterator.hasNext()) {
            T t = iterator.next();
            if(predicate.accepts(t)) {
                return true;
            }
        }
        return false;
    }
}
