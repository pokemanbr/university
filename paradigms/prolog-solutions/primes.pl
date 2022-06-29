fill(I, N) :- 
	I =< N,
	prime(I),
	J is I * I,
	fill_composite(I, J, N).
fill(I, N) :-
	I =< N,
	I1 is I + 1,
	fill(I1, N).

fill_composite(I, J, N) :-
	J =< N,
	assert(composite(J)),
	J1 is J + I,
	fill_composite(I, J1, N).

init(MAX_N) :- fill(2, MAX_N).

prime(N) :- N \= 1, \+ composite(N).

find_divisors(N, I, [N]) :- I * I > N, !. 
find_divisors(N, I, [I | T]) :-
	I =< N,
	0 is mod(N, I),
	N1 is div(N, I),
	find_divisors(N1, I, T).
find_divisors(N, I, [H | T]) :-
	I =< N,
	\+ (0 is mod(N, I)),
	I1 is I + 1,
	find_divisors(N, I1, [H | T]).

find_number(1, H, []).
find_number(N, L, [H | T]) :-
	prime(H),
	L =< H,
	find_number(N1, H, T),
	N is N1 * H.

prime_divisors(1, []) :- !.
prime_divisors(N, Divisors) :- number(N), find_divisors(N, 2, Divisors).
prime_divisors(N, Divisors) :- \+ number(N), find_number(N, 1, Divisors).

convert_to_k(0, K, []) :- !.
convert_to_k(N, K, [H | T]) :-
	H is mod(N, K),
	N1 is div(N, K),
	convert_to_k(N1, K, T).

prime_palindrome(N, K) :-
	number(N),
	prime(N),
	convert_to_k(N, K, NK),
	reverse(NK, NK1),
	NK = NK1.
