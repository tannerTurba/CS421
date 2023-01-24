package predicates;

import java.util.Collection;
import java.util.Iterator;

public class MostAcceptsMostOf<T> implements Predicate<Collection<Predicate<T>>> {
    private Collection<T> reference;

    public MostAcceptsMostOf(Collection<T> reference) {
        this.reference = reference;
    }

    @Override
    public boolean accepts(Collection<Predicate<T>> predicates) {
        int referenceTarget = (int) Math.floor(reference.size() / 2); 
        int predicateTarget = (int) Math.floor(predicates.size() / 2);
        int validReferences = 0;
        int validPredicates = 0;
        Iterator<T> refIterator = reference.iterator();
        Iterator<Predicate<T>> pIterator = predicates.iterator();

        //For each predicate
        while(pIterator.hasNext()) {
            Predicate<T> predicate = pIterator.next();

            //And for each reference
            while(refIterator.hasNext()) {
                T t = refIterator.next();
                if(predicate.accepts(t)) {
                    validReferences++;
                }
            }

            //Increment validPredicates if there are enough validReferences.
            if(validReferences >= referenceTarget) {
                validPredicates++;
            }
            validReferences = 0;
        }
        return validPredicates >= predicateTarget;
    }
    
}
