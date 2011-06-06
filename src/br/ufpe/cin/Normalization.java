package br.ufpe.cin;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOr;


public class Normalization {
	Ontology ontology = null;
	Ontology normalized_ontology = null;
	
	public Normalization(Ontology o)
	{
		this.ontology = o;
	}

	public void normalizeOntology() throws OWLException
	{
		Set<OWLClass> classes = this.ontology.getClasses();
		for (OWLClass cls : classes){
			if (!this.isInNormalForm(cls))
				System.out.println(cls);
		}
	}
	
	public void normalizeAxiom(OWLClass cls) throws OWLException
	{
		Set<OWLDescription> sc = ontology.getSuperClasses(cls);
		for (OWLDescription super_class : sc){
			// left is not a pure conjuction
			this.normalizeRHS(cls, super_class);
		}
		
		/*Set<OWLClassExpression> ec = ontology.getEquivalentClasses(cls);
		for (OWLClassExpression equivalent_class : ec){
			// left is not a pure conjunction
			this.normalizeLHS(equivalent_class, cls);
			
			// right is not a pure disjunction
			this.normalizeRHS(equivalent_class, cls);
		}*/
	}

	private void normalizeLHS(OWLDescription super_class, OWLClass cls) {
		// TODO Auto-generated method stub
		
	}

	private void normalizeRHS(OWLDescription sub_class, OWLDescription super_class) throws OWLException {
		if (this.isDisjunction(super_class)) {
			
			if (isPureDisjunction(super_class))
				System.out.println("pure disjuction: " + super_class);
			else
				System.out.println("disjuction: " + super_class);
			
		} else if (this.isConjunction(super_class)){
			
			if(isPureConjunction(super_class))
				System.out.println("pure conjunction: " + super_class);
			else 
				System.out.println("conjunction: " + super_class);
			
		} else {			
			System.out.println("?: " + super_class);
		}
	}
	
	private boolean isInNormalForm (OWLClass cls) throws OWLException
	{		
		Set<OWLDescription> inclusion_axioms = cls.getSuperClasses(ontology.ontology);
		for (OWLDescription axiom : inclusion_axioms){
			
			if (!isInNormalForm(cls, axiom))
				return false;
		}
		
		Set<OWLDescription> equivalence_axioms = cls.getEquivalentClasses(ontology.ontology);
		for (OWLDescription axiom : equivalence_axioms){
			
			if (!isInNormalForm(cls, axiom) || !isInNormalForm(axiom, cls))
				return false;
		}
		
		return true;
	}
	
	// i)    C [ ^C
	// ii)  vD [  C
	// iii) ^C [ vD
	private boolean isInNormalForm (OWLDescription cls_left, OWLDescription cls_right) throws OWLException
	{
		if (isConjunction(cls_left) && isPureConjunction(cls_right))
			return true;
		
		if (isPureDisjunction(cls_left) && isConjunction(cls_right))
			return true;
		
		if (isPureConjunction(cls_left) && isPureDisjunction(cls_right))
			return true;
			
		return false;
	}
	
	private boolean isDisjunction(OWLDescription cls)
	{
		if (cls instanceof OWLClass)
			return true;
		
		if (cls instanceof OWLOr)
			return true;
		
		if (cls instanceof OWLObjectAllRestriction)
			return true; 

		if (cls instanceof OWLDataAllRestriction)
			return true; 
		
		return false;
	}
	private boolean isPureDisjunction(OWLDescription cls) throws OWLException{
		if (!isDisjunction(cls))
			return false;
		
		Vector<OWLDescription> sub_axioms = new Vector<OWLDescription>();
		
		/*
		 * loop infinito caso adicione os axiomas abaixo
		 */
		if (cls instanceof OWLClass){
			/*OWLClass sub_class = (OWLClass) cls;
			sub_axioms.addAll(sub_class.getEquivalentClasses(ontology.ontology));
			sub_axioms.addAll(sub_class.getSuperClasses(ontology.ontology));*/
		} 
		else if (cls instanceof OWLOr){
			OWLOr union = (OWLOr) cls;
			sub_axioms.addAll(union.getOperands());
		}
		else if (cls instanceof OWLObjectAllRestriction){
			OWLObjectAllRestriction all_values = (OWLObjectAllRestriction) cls;
			sub_axioms.add(all_values.getDescription());
		}
		/*else if (cls instanceof OWLDataAllRestriction){
			OWLDataAllRestriction data_values = (OWLDataAllRestriction) cls;
			// TODO sub_axioms.add(data_values.
		}*/
		
		for (OWLDescription axiom : sub_axioms){
			if (!isPureDisjunction(axiom)){
				return false;
			}
		}
		
		return true;
	}
	
	
	private boolean isConjunction(OWLDescription cls)
	{
		if (cls instanceof OWLClass)
			return true;
		
		if (cls instanceof OWLAnd)
			return true;
		
		if (cls instanceof OWLObjectSomeRestriction)
			return true; 

		if (cls instanceof OWLDataSomeRestriction)
			return true; 
		
		return false;
	}
	
	private boolean isPureConjunction(OWLDescription cls) throws OWLException{
		if (!isConjunction(cls))
			return false;
		
		Vector<OWLDescription> sub_axioms = new Vector<OWLDescription>();
		
		if (cls instanceof OWLClass){
			/*OWLClass sub_class = (OWLClass) cls;
			sub_axioms.addAll(sub_class.getEquivalentClasses(ontology.ontology));
			sub_axioms.addAll(sub_class.getSuperClasses(ontology.ontology));*/
		} 
		else if (cls instanceof OWLAnd){
			OWLAnd intersection = (OWLAnd) cls;
			sub_axioms.addAll(intersection.getOperands());
		}
		else if (cls instanceof OWLObjectSomeRestriction){
			OWLObjectSomeRestriction some_values = (OWLObjectSomeRestriction) cls;
			sub_axioms.add(some_values.getDescription());
		}
		/*else if (cls instanceof OWLDataSomeRestriction){
			OWLDataSomeRestriction data_values = (OWLDataSomeRestriction) cls;
			// TODO sub_axioms.add(data_values.
		}*/
		
		for (OWLDescription axiom : sub_axioms){
			if (!isPureConjunction(axiom)){
				return false;
			}
		}
		
		return true;
	}
}
