diminishes([]).
diminishes([_]).
diminishes([X,Y|Z]) :- 
    X > Y, 
    diminishes([Y|Z]).

combine([],N,N).
combine(N,[],N).
combine([S|T],[X|Y],[S|N]) :- 
    S < X, 
    combine(T,[X|Y],N).
combine([S|T],[X|Y],[X|N]) :- 
    S >= X, 
    combine([S|T],Y,N).

counts(L1, L2) :-
    counts_helper(L1, L2, []).

counts_helper([], L, L).
counts_helper([H|T], X, L) :-
    (   member([H,C], L) 	%if the pair is in the list L,
    ->  C1 is C + 1,		%increment the count, and remove from L.
        delete(L, [H,C], NewL),
        counts_helper(T, X, [[H,C1]|NewL]) %recurse with the new pair.
    ;   counts_helper(T, X, [[H,1]|L]) 	%else recurse with a newly created pair.
    ).

