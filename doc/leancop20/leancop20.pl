%% File: leancop20.pl  -  Version: 2.01  -  Date: 06 Nov 2006
%%
%%         "Make everything as simple as possible, but not simpler."
%%                                                 [Albert Einstein]
%%
%% Purpose: leanCoP: A Lean Connection Prover for Classical Logic
%%
%% Author:  Jens Otten
%% Web:     www.leancop.de
%%
%% Usage:   prove(M).    % where M is a set of clauses
%%                       %  e.g. M=[[q(a)],[-p],[p, -q(X)]]
%%          prove(F).    % where F is a first-order formula
%%                       %  e.g. F=((p,all X:(p=>q(X)))=>all Y:q(Y))
%%          prove2(F,S). % where F is a formula and S is a subset of
%%                       %  [nodef,def,conj,reo(I),scut,cut,comp(J)]
%%                       %  (with numbers I,J) defining attributes
%%
%% Copyright: (c) 1999-2007 by Jens Otten
%% License:   GNU General Public License


:- [def_mm].  % load program for clausal form translation
:- lib(iso).  % load library for ISO compatibility
:- set_flag(occur_check,on).  % global occur check on
:- dynamic(pathlim/0), dynamic(lit/3).


%%% prove matrix M / formula F

prove(F) :- prove2(F,[cut,comp(7)]).

prove2(F,Set) :-
    Time1 is cputime, (F=[_|_] -> M=F ; make_matrix(F,M,Set)),
    retract_all(lit(_,_,_)), (member([-(#)],M) -> S=conj ; S=pos),
    assert_clauses(M,S), prove(1,Set),
    Time2 is cputime, Time is Time2-Time1, print(Time).

prove(PathLim,Set) :-
    \+member(scut,Set) -> prove([-(#)],[],PathLim,[],Set) ;
    lit(#,C,_) -> prove(C,[-(#)],PathLim,[],Set).
prove(PathLim,Set) :-
    member(comp(Limit),Set), PathLim=Limit -> prove(1,[]) ;
    (member(comp(_),Set);retract(pathlim)) ->
    PathLim1 is PathLim+1, prove(PathLim1,Set).

%%% leanCoP core prover

prove([],_,_,_,_).

prove([Lit|Cla],Path,PathLim,Lem,Set) :-
    \+ (member(LitC,[Lit|Cla]), member(LitP,Path), LitC==LitP),
    (-NegLit=Lit;-Lit=NegLit) ->
       ( member(LitL,Lem), Lit==LitL
         ;
         member(NegL,Path), unify_with_occurs_check(NegL,NegLit)
         ;
         lit(NegLit,Cla1,Grnd1),
         ( Grnd1=g -> true ; length(Path,K), K<PathLim -> true ;
           \+ pathlim -> assert(pathlim), fail ),
         prove(Cla1,[Lit|Path],PathLim,Lem,Set)
       ),
       ( member(cut,Set) -> ! ; true ),
       prove(Cla,Path,PathLim,[Lit|Lem],Set).

%%% write clauses into Prolog's database

assert_clauses([],_).
assert_clauses([C|M],Set) :-
    (Set\=conj, \+member(-_,C) -> C1=[#|C] ; C1=C),
    (ground(C) -> G=g ; G=n), assert_clauses2(C1,[],G),
    assert_clauses(M,Set).

assert_clauses2([],_,_).
assert_clauses2([L|C],C1,G) :-
    append(C1,C,C2), assert(lit(L,C2,G)), append(C1,[L],C3),
    assert_clauses2(C,C3,G).
