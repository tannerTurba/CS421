package predicates;

import java.util.Collection;
import java.util.Iterator;

public class AcceptsMostOf<T> implements Predicate<Predicate<T>> {
    Collection<T> reference;

    public AcceptsMostOf(Collection<T> reference) {
        this.reference = reference;
    }

    @Override
    public boolean accepts(Predicate<T> predicate) {
        int target = (int) Math.floor(reference.size() / 2); 
        int counter = 0;
        Iterator<T> iterator = reference.iterator();

        while(iterator.hasNext()) {
            T t = iterator.next();
            if(predicate.accepts(t)) {
                counter++;
            }
        }
        return counter >= target;
    }
}
