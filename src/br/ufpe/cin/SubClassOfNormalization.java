package br.ufpe.cin;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class SubClassOfNormalization {
	private OWLOntology ontology = null;

	public void setOntology(OWLOntology o) {
		this.ontology = o;
	}
	
	private boolean isInNormalForm(OWLSubClassOfAxiom axiom) {
		OWLClassExpression exp_left = axiom.getSubClass();
		OWLClassExpression exp_right = axiom.getSuperClass();
		
		if (Normalization.isInNormalForm(exp_left, exp_right))
			return true;
		
		return false;
	}

	/* 
	 * Normalize-Axiom (Axiom A, Ontology O);
	 *  If A not in normal form then
	 * 	 If LHS(A) ∉ NC U SC {not a concept or pure conjunction} 
	 *    Normalize-LHS (A, O);
	 *   If RHS(A) ∉ NC U SD {not a concept or pure disjunction} 
	 *    Normalize-RHS (A, O);
	 */
	void normalizeAxiom(OWLSubClassOfAxiom axiom) {
		if (isInNormalForm(axiom))
			return;
		
		OWLClassExpression left = axiom.getSubClass();
		if (!Normalization.isConcept(left) && !Normalization.isPureConjunction(left))
			normalizeLHS(axiom);
		
		OWLClassExpression right = axiom.getSuperClass();
		if (!Normalization.isConcept(right) && !Normalization.isPureDisjunction(right))
			normalizeRHS(axiom);
	}

	private void normalizeRHS(OWLSubClassOfAxiom axiom) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Normalize-LHS (Axiom A, ontology O);
	 *  If LHS(A) ∈ SC U SnpC (a conjunction) then 
	 *  	For each D ⊆ LHS(A) │ D ∈ SD U SnpD (disjunction) 
	 *   		LHS(A) ← (LHS(A) – D) ⊓ N, N ∈ O 
	 *   
	 *   		If D ∈ SnpD (non-pure) then
	 *   			O ← O U Normalize-LHS({D ⊑ N}, O);
	 *   		Else 
	 *   			O ← O U {D ⊑ N};
	 *   
	 *   	For each nC ⊆ LHS(A) │ nC ∈ SnpC (non-pure conjunction) 
	 *   		Find the first impurity I ⊆ nC │ I ∈ SI
	 *   		nc∘ ← nC{I/N}, N ∈ O
	 *   		LHS(A) ← (LHS(A) – nC) ⊓ nc∘
	 *   		O ← O U Normalize-LHS({I ⊑ N}, O} 
	 *   
	 *   Else {LHS(A) ∈ SD U SnpD (disjunction)}
	 *   	O ← O – A; 
	 *   	For each X ⊆ LHS(A) | X ∈ NC U SC (atomic concept or pure conjunction)
	 *   		If RHS(A) ∈ NC U SC (not a concept or pure conjunction) then
	 *   			O ← O U Normalize-RHS({X ⊑ RHS(A)}, O}; 
	 * 			Else
	 * 				O ← O U {X ⊑ RHS(A)};
	 * 
	 * 		For each expression E ⊆ LHS(A) | E ∉ SC (not a pure conjunction) ???????
	 * 			If E ⊑ RHS(A) is not in normal form then 
	 * 				O ← O U Normalize-Axiom({E ⊑ RHS(A)}, O};
	 * 			Else
	 * 				O ← O U E ⊑ RHS(A);
	 */
	private void normalizeLHS(OWLSubClassOfAxiom axiom) 
	{
		OWLClassExpression left  = axiom.getSubClass();
		OWLClassExpression right = axiom.getSuperClass();
		
		// If LHS(A) ∈ SC U SnpC (a conjunction) then 
		if (Normalization.isConjunction(left) || Normalization.isPureConjunction(left)){
			// For each D ⊆ LHS(A) │ D ∈ SD U SnpD (disjunction) 
			// LHS(A) ← (LHS(A) – D) ⊓ N, N ∈ O
			
			/* modifica left para deixa-lo apenas com conjunções. 
			 * cria disjunction ⊑ Atomic, para cada disjunção */
			ConjunctionVisitor new_left = new ConjunctionVisitor(ontology, left);
			
			/* retorna as disjunções que foram removidas da conjunção */
			Vector<OWLSubClassOfAxiom> disjunctions = new_left.getDisjunctions();
			
			//	If D ∈ SnpD (non-pure) then
			// 		O ← O U Normalize-LHS({D ⊑ N}, O);
			//	Else 
			//		O ← O U {D ⊑ N};
			
			for (OWLSubClassOfAxiom subaxiom : disjunctions){
				if (!Normalization.isPureDisjunction(subaxiom.getSubClass())){
					normalizeLHS(subaxiom);
				}
			}
			
			//	For each nC ⊆ LHS(A) │ nC ∈ SnpC (non-pure conjunction) 
			//		Find the first impurity I ⊆ nC │ I ∈ SI
			//			nc∘ ← nC{I/N}, N ∈ O
			//			LHS(A) ← (LHS(A) – nC) ⊓ nc∘
			//			O ← O U Normalize-LHS({I ⊑ N}, O}
			
			/* precisa implementar mais alguma coisa? */
		}
		// Else {LHS(A) ∈ SD U SnpD (disjunction)}
		else if (Normalization.isDisjunction(left) || Normalization.isPureDisjunction(left)){
			//	For each X ⊆ LHS(A) | X ∈ NC U SC (atomic concept or ??pure?? conjunction)
			//		If RHS(A) ∈ NC U SC (not a concept or pure conjunction) then
			//			O ← O U Normalize-RHS({X ⊑ RHS(A)}, O}; 
			//		Else
			//			O ← O U {X ⊑ RHS(A)};
			
			/* modifica left para deixa-lo apenas com disjunções. 
			 * cria conjunction ⊑ RHS, para cada conjunção */
			DisjunctionVisitor new_left = new DisjunctionVisitor(ontology, left, right);
			
			/* retorna as conjunções que foram removidas da disjunção */
			Vector<OWLSubClassOfAxiom> conjunctions = new_left.getConjunctions();
			
			for (OWLSubClassOfAxiom subaxiom : conjunctions){
				OWLClassExpression X = subaxiom.getSubClass();
				if (!Normalization.isConcept(X) || Normalization.isPureConjunction(X)){
					normalizeRHS(subaxiom);		
				}
			}
			
			// 	For each expression E ⊆ LHS(A) | E ∉ SC (not a pure conjunction) ???????
			//		If E ⊑ RHS(A) is not in normal form then 
			//			O ← O U Normalize-Axiom({E ⊑ RHS(A)}, O};
			//		Else
			//			O ← O U E ⊑ RHS(A);
			
			/* precisa implementar? */
		}
	}
}
