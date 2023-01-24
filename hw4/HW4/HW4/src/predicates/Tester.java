package predicates;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Tester {
	public static <T> void compare( String msg, Collection<? extends T> test, Collection<? extends T> oracle ) {
		boolean ok = test.containsAll( oracle ) && oracle.containsAll( test );
		System.out.print( pad( msg ) );
		if( ok ) {
			System.out.println(" SUCCESS " );
		} else {
			System.out.println( " FAILS " );
		}
	}
	
	public static <T> List<Boolean> results( Predicate<T> predicate, List<T> tests ) {
		return tests
				.stream()
				.map( test -> predicate.accepts(test) )
		 		.collect( Collectors.toList() );
	}
	
	private static String pad( String txt ) {
		StringBuilder sb = new StringBuilder(txt);
		while( sb.length() < 32 ) {
			sb.append(" ");
		}
		
		sb.append(": ");		
		return sb.toString();
	}
	
	public static <T> void test( String msg, Predicate<T> predicate, List<T> tests, List<Boolean> expected ) {
		List<Boolean> actual = results( predicate, tests );
		
		System.out.print( pad(msg) );
		if( !actual.equals(expected) ) {
			System.out.println( " FAILURE" );
			for(int i=0; i<actual.size(); i++ ) {
				System.out.println( "\t\t" + tests.get(i) + "=>" + actual.get(i) + ":" + expected.get(i));
			}
		} else {
			System.out.println( " SUCCESS" );
		}
	}

	public static void similarTo1() {
		Metric<Rectangle> metric = new Metric<Rectangle>() {
			@Override
			public double distance(Rectangle t1, Rectangle t2) {
				double dx = t1.getCenterX() - t2.getCenterX();
				double dy = t1.getCenterY() - t2.getCenterY();
				return Math.sqrt( dx * dx + dy * dy );
			}
		};
		
		Rectangle reference = new Rectangle( 50, 50, 50, 50 );
		SimilarTo<Rectangle> predicate = new SimilarTo<Rectangle>( reference, metric, 20 );
		List<Rectangle> tests = Arrays.asList( 
				new Rectangle( 70, 70, 10, 10 ),
				new Rectangle( 25, 25, 25, 25 ),
				new Rectangle( 75, 75, 100, 100 ) );
		List<Boolean> oracle = Arrays.asList( true, false, false );
		
		test( "SimilarTo<Rectangle>", predicate, tests, oracle );
		
	}
	
	public static void similarTo2() {
		Metric<String> metric = new Metric<String>() {
			@Override
			public double distance(String t1, String t2) {
				return Math.abs( t1.length() - t2.length() );
			}			
		};
		
		SimilarTo<String> predicate = new SimilarTo<String>( "packer", metric, 1 );
		List<String> tests = Arrays.asList( "a", "ab", "abc", "abcd", "abcde", "abcdef", "abcdefg", "abcdefgh" );
		List<Boolean> oracle = Arrays.asList( false, false, false, false, true, true, true, false );
		test( "SimilarTo<String>", predicate, tests, oracle );
	}

	
	public static void similarColor() {
		SimilarColor predicate = new SimilarColor( new Color(100,100,100), 20 );
		List<Color> tests = Arrays.asList( Color.red, Color.green, new Color(95, 90, 100 ) );
		List<Boolean> oracle = Arrays.asList( false, false, true );
		
		test( "SimilarColor", predicate, tests, oracle );
	}

	public static void startsWith1() {
		StartsWith<String> predicate = new StartsWith<String>("aB");
		List<String> tests = Arrays.asList( "aBc", "a", "aB", "AB", "", " abcsdfasdf" );
		List<Boolean> oracle = Arrays.asList( true, false, true, true, false, false );

		test( "StartsWith<String>(aB)", predicate, tests, oracle );
	}
	
	public static void startsWith2() {
		final Function<String, StringBuilder> f = (str) -> {
			StringBuilder sb = new StringBuilder();
			return sb.append( str );
		};
		

		StartsWith<StringBuilder> predicate = new StartsWith<>( f.apply("aB") );
		List<StringBuilder> tests = Arrays
				.asList( "aBc", "a", "aB", "AB", "", " abcsdfasdf" )
				.stream()
				.map( f )
				.collect( Collectors.toList() );
		List<Boolean> oracle = Arrays.asList( true, false, true, true, false, false );

		test( "StartsWith<StringBuilder>(aB)", predicate, tests, oracle );
	}

	public static void greaterThan1() {
		GreaterThan<Integer> predicate = new GreaterThan<Integer>( 50 );
		List<Integer> tests = Arrays.asList( 10, 20, 50, 75, 100 );
		List<Boolean> oracle = Arrays.asList( false, false, false, true, true );
		
		test( "GreaterThan<Integer>(50)", predicate, tests, oracle );
	}

	public static void greaterThan2() {
		GreaterThan<String> predicate = new GreaterThan<String>( "mad" );
		List<String> tests = Arrays.asList( "ark", "low", "mad", "men", "xylo" );
		List<Boolean> oracle = Arrays.asList( false, false, false, true, true );

		test( "GreaterThan<String>(mad)", predicate, tests, oracle );
	}

	public static void subsetOf() {
		SubsetOf<String> predicate = new SubsetOf<String>( Arrays.asList("mad", "men") );
		List<Collection<? extends String>> tests = 
				Arrays.asList( 
						Arrays.asList("mad", "men"),
						Arrays.asList("men", "mad"),
						Arrays.asList("men", "women", "rams", "mad"),
						Arrays.asList("mad", "cowboy"),
						Arrays.asList() );
		List<Boolean> oracle = Arrays.asList( true, true, false, false, true );
		
		test( "SubsetOf<String>(mad, men)", predicate, tests, oracle );				
	}

	public static void negation() {
		StartsWith<String> p1 = new StartsWith<String>("ab");
		Negation<String> predicate = new Negation<String>( p1 );		
		List<String> tests = Arrays.asList( "abc", "ab", "a", "ba", "", "asfabcsdfasdf" );
		List<Boolean> oracle = Arrays.asList( false, false, true, true, true, true );
		
		test( "Negation<String>", predicate, tests, oracle );				
	}

	public static void acceptsAllOf() {
		List<String> data =	Arrays.asList( "abc", "ab", "a", "a", "asfabcsdfasdf" );		
		AcceptsAllOf<String> predicate = new AcceptsAllOf<>( data );
		List<Predicate<String>> tests = Arrays.asList(
				new StartsWith<String>("a"),
				new GreaterThan<String>("0"),
				new Negation<String>( new StartsWith<String>("a") ) );
		
		List<Boolean> oracle = Arrays.asList( true, true, false );		
		test( "AcceptsAllOf<String>", predicate, tests, oracle ); 
	}

	public static void acceptsSomeOf() {
		List<String> data = Arrays.asList( "abc", "", "a",	"a", "asfabcsdfasdf" );
		AcceptsSomeOf<String> predicate = new AcceptsSomeOf<String>(data);
		
		List<Predicate<String>> tests = Arrays.asList(
				new StartsWith<String>("a"),
				new GreaterThan<String>("z"),
				new Negation<String>( new StartsWith<String>("a") ) );

		List<Boolean> oracle = Arrays.asList( true, false, true );		
		test( "AcceptsSomeOf<String>", predicate, tests, oracle );		
	}
	
	public static void acceptsMostOf() {
		List<String> data = Arrays.asList( "abc", "", "a",	"a", "asfabcsdfasdf" );
		AcceptsMostOf<String> predicate = new AcceptsMostOf<String>(data);
		
		List<Predicate<String>> tests = Arrays.asList(
				new StartsWith<String>("a"),
				new GreaterThan<String>("z"),
				new Negation<String>( new StartsWith<String>("a") ) );

		List<Boolean> oracle = Arrays.asList( true, false, false );		
		test( "AcceptsMostOf<String>", predicate, tests, oracle );		
	}

	@SuppressWarnings("unchecked")
	public static void and() {
		And<String> predicate = new And<String>(
				new StartsWith<String>("a"),
				new GreaterThan<String>("ab") ) ;						
		List<String> tests = Arrays.asList( "abc", "aaa", "", "a", "asfabcsdfasdf" );
		
		List<Boolean> oracle = Arrays.asList( true, false, false, false, true );		
		test( "And<String>", predicate, tests, oracle );	
	}
	
	@SuppressWarnings("unchecked")
	public static void or() {
		Or<String> predicate = new Or<String>(
				new Negation<String>( new GreaterThan<String>("e" ) ),
				new GreaterThan<String>("m") ) ;						
		List<String> tests = Arrays.asList( "a", "f", "p" );
		
		List<Boolean> oracle = Arrays.asList( true, false, true );  
		test( "Or<String>", predicate, tests, oracle );	
	}
	
	public static void filter() {
		Metric<String> metric = new Metric<String>() {
			@Override
			public double distance(String t1, String t2) {
				return Math.abs( t1.length() - t2.length() );
			}			
		};
		
		SimilarTo<String> predicate = new SimilarTo<String>( "packer", metric, 1 );
		Set<String> data = new HashSet<String>( Arrays.asList("rat", "bee", "horse", "snake", "kangaroo") );		
		Collection<String> c = PredicateUtilities.filter( data, predicate );
		
		compare( "filter", c, new HashSet<>(Arrays.asList("horse", "snake" ) ) );
	}

	public static void main(String[] args) {
		startsWith1();
		startsWith2();
		greaterThan1();
		greaterThan2();
		subsetOf();
		negation();
		acceptsAllOf();
		acceptsSomeOf();
		acceptsMostOf();
		//mostAcceptsMostOf
		and();
		or();
		similarTo1();
		similarTo2();
		similarColor();
		filter();	
	}

}
