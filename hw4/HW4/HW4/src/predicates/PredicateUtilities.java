package predicates;

import java.util.ArrayList;
import java.util.Collection;

public class PredicateUtilities<T> {

    public static <T> Collection<T> filter(Collection<? extends T> collection, Predicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for(T t : collection) {
            if(predicate.accepts(t)) {
                result.add(t);
            }
        }
        return result;
    }
}
