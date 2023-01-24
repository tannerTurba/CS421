package predicates;

public class StartsWith<T extends CharSequence> implements Predicate<T> {
    private T prefix;

    public StartsWith(T prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean accepts(T t) {
        if(t.length() >= prefix.length()) {
            String tPrefix = t.subSequence(0, prefix.length()).toString().toLowerCase();
            return prefix.toString().toLowerCase().equals(tPrefix);
        }
        return false;
    }
    
}
