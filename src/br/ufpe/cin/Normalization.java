package br.ufpe.cin;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.OWLCompositeOntologyChange;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

public class Normalization {
	Ontology ontology = null;
	Ontology normalized_ontology = null;
	
	public Normalization(Ontology o)
	{
		this.ontology = o;
	}

	public void normalizeOntology() throws OWLOntologyCreationException
	{
		/* 
		 * For each axiom A ∈ O Normalize-Axiom (A,O);
		 * This axioms A are the NamedClasses in ontology
		 */
		
		Set<OWLClass> classes = this.ontology.getClasses();
		for (OWLClass cls : classes){
			this.normalizeAxiom(cls);
		}
	}
	
	/* 
	 * Normalize-Axiom (Axiom A, Ontology O);
	 *  If A not in normal form then
	 * 	 If LHS(A) ∉ NC U SC {not a concept or pure conjunction} 
	 *    Normalize-LHS (A, O);
	 *   If RHS(A) ∉ NC U SD {not a concept or pure disjunction} 
	 *    Normalize-RHS (A, O);
	 */
	public void normalizeAxiom(OWLClass cls)
	{
		// TODO LHS está levando em conta equivalencia?
		
		if (!this.isInNormalForm(cls)) {
			Vector<OWLClassExpression> lhs = new Vector<OWLClassExpression>();
			Vector<OWLClassExpression> rhs = new Vector<OWLClassExpression>();
			
			lhs.addAll(cls.getEquivalentClasses(ontology.ontology));
			rhs.addAll(cls.getSuperClasses(ontology.ontology));
			rhs.addAll(cls.getEquivalentClasses(ontology.ontology));
			
			// If RHS(A) ∉ NC U SD {not a concept or pure disjunction} 
			for (OWLClassExpression classz : rhs){
				if (!isConcept(classz) && !isPureDisjunction(classz))
					normalizeRHS(cls, classz);
			}
			
			// If LHS(A) ∉ NC U SC {not a concept or pure conjunction} 
			for (OWLClassExpression classz : lhs){
				if (!isConcept(classz) && !isPureConjunction(classz))
					normalizeLHS(classz, cls);
			}
			
		}
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
	 * 		For each expression E ⊆ LHS(A) | E ∉ SC (not a pure conjunction)
	 * 			If E ⊑ RHS(A) is not in normal form then 
	 * 				O ← O U Normalize-Axiom({E ⊑ RHS(A)}, O};
	 * 			Else
	 * 				O ← O U E ⊑ RHS(A);
	 */
	private void normalizeLHS(OWLClassExpression left_hs, OWLClassExpression right_hs) {
		if (isConjunction(left_hs)) 
		{
			ConjunctionVisitor visitor = new ConjunctionVisitor(ontology.ontology);
			left_hs.accept(visitor);
		} 
		else if (isDisjunction(left_hs))
		{
			
		} 
		else {
			System.err.println(left_hs + " not suported by the normalization yet.");
		}
	}

	private void normalizeRHS(OWLClassExpression classz, OWLClassExpression classz2) {
		// TODO Auto-generated method stub
		
	}

	private boolean isInNormalForm (OWLClass cls)
	{		
		Set<OWLClassExpression> inclusion_axioms = cls.getSuperClasses(ontology.ontology);
		for (OWLClassExpression axiom : inclusion_axioms){
			
			if (!isInNormalForm(cls, axiom))
				return false;
		}
		
		Set<OWLClassExpression> equivalence_axioms = cls.getEquivalentClasses(ontology.ontology);
		for (OWLClassExpression axiom : equivalence_axioms){
			
			if (!isInNormalForm(cls, axiom) || !isInNormalForm(axiom, cls))
				return false;
		}
		
		return true;
	}
	
	// i)    C [ ^C
	// ii)  vD [  C
	// iii) ^C [ vD
	private boolean isInNormalForm (OWLClassExpression cls_left, OWLClassExpression cls_right)
	{
		if (isConcept(cls_left) && isPureConjunction(cls_right))
			return true;
		
		if (isPureDisjunction(cls_left) && isConcept(cls_right))
			return true;
		
		if (isPureConjunction(cls_left) && isPureDisjunction(cls_right))
			return true;
			
		return false;
	}
	
	private boolean isConcept(OWLClassExpression cls)
	{
		return cls instanceof OWLClass;
	}

	private boolean isDisjunction(OWLClassExpression cls)
	{
		if (cls instanceof OWLClass)
			return true;
		
		if (cls instanceof OWLObjectUnionOf)
			return true;
		
		if (cls instanceof OWLObjectAllValuesFrom)
			return true; 
		
		if (cls instanceof OWLDataAllValuesFrom)
			return true;
		
		if (cls instanceof OWLObjectComplementOf)
			return true;
		
		if (cls instanceof OWLDataComplementOf)
			return true;
		
		return false;
	}
	private boolean isPureDisjunction(OWLClassExpression cls){
		if (!isDisjunction(cls))
			return false;
		
		Vector<OWLClassExpression> sub_axioms = new Vector<OWLClassExpression>();
		
		if (cls instanceof OWLClass){
			/*OWLClass sub_class = (OWLClass) cls;
			sub_axioms.addAll(sub_class.getEquivalentClasses(ontology.ontology));
			sub_axioms.addAll(sub_class.getSubClasses(ontology.ontology));*/
		} 
		else if (cls instanceof OWLObjectUnionOf){
			OWLObjectUnionOf union = (OWLObjectUnionOf) cls;
			sub_axioms.addAll(union.getOperands());
		}
		else if (cls instanceof OWLObjectAllValuesFrom){
			OWLObjectAllValuesFrom all_values = (OWLObjectAllValuesFrom) cls;
			sub_axioms.addAll(all_values.getNestedClassExpressions());
		}
		else if (cls instanceof OWLDataAllValuesFrom){
			OWLDataAllValuesFrom data_values = (OWLDataAllValuesFrom) cls;
			sub_axioms.addAll(data_values.getNestedClassExpressions());
		}
		else if (cls instanceof OWLObjectComplementOf){
			OWLObjectComplementOf compl = (OWLObjectComplementOf) cls;
			
			if (compl.getObjectComplementOf() instanceof OWLClass == false)
				System.out.println("Complement of a non concept" + compl);
				return false;
		}
		else if (cls instanceof OWLDataComplementOf){}
		
		for (OWLClassExpression axiom : sub_axioms){
			if (!isDisjunction(axiom)){
				return false;
			}
		}
		
		return true;
	}
	
	
	private boolean isConjunction(OWLClassExpression cls)
	{
		if (cls instanceof OWLClass)
			return true;
		
		if (cls instanceof OWLObjectIntersectionOf)
			return true;
		
		if (cls instanceof OWLObjectSomeValuesFrom)
			return true; 
		
		if (cls instanceof OWLDataSomeValuesFrom)
			return true;
		
		if (cls instanceof OWLObjectComplementOf)
			return true;
			
		if (cls instanceof OWLDataComplementOf)
			return true;
			
		return false;
	}
	
	private boolean isPureConjunction(OWLClassExpression cls){
		if (!isConjunction(cls))
			return false;
		
		Vector<OWLClassExpression> sub_axioms = new Vector<OWLClassExpression>();
		
		if (cls instanceof OWLClass){
			/*OWLClass sub_class = (OWLClass) cls;
			sub_axioms.addAll(sub_class.getEquivalentClasses(ontology.ontology));
			sub_axioms.addAll(sub_class.getSubClasses(ontology.ontology));*/
		} 
		else if (cls instanceof OWLObjectIntersectionOf){
			OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) cls;
			sub_axioms.addAll(intersection.getOperands());
		}
		else if (cls instanceof OWLObjectSomeValuesFrom){
			OWLObjectSomeValuesFrom some_values = (OWLObjectSomeValuesFrom) cls;
			sub_axioms.addAll(some_values.getNestedClassExpressions());
		}
		else if (cls instanceof OWLDataSomeValuesFrom){
			OWLDataSomeValuesFrom data_values = (OWLDataSomeValuesFrom) cls;
			sub_axioms.addAll(data_values.getNestedClassExpressions());
		}
		else if (cls instanceof OWLObjectComplementOf){
			OWLObjectComplementOf compl = (OWLObjectComplementOf) cls;
			
			if (compl.getObjectComplementOf() instanceof OWLClass == false)
				System.out.println("Complement of a non concept" + compl);
				return false;
		}
		else if (cls instanceof OWLDataComplementOf){}
		
		for (OWLClassExpression axiom : sub_axioms){
			if (!isConjunction(axiom)){
				return false;
			}
		}
		
		return true;
	}
}