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
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Normalization {
	Ontology ontology = null;
	Ontology normalized_ontology = null;
	
	public Normalization(Ontology o)
	{
		this.ontology = o;
	}

	public void normalizeOntology() throws OWLOntologyCreationException
	{
		Set<OWLClass> classes = this.ontology.getClasses();
		for (OWLClass cls : classes){
			if (!this.isInNormalForm(cls)){
				System.out.println(cls + " n‹o est‡ na forma normal");
				//this.normalizeAxiom(cls);
			}
		}
	}
	
	public void normalizeAxiom(OWLClass cls)
	{
		Set<OWLClassExpression> sc = ontology.getSuperClasses(cls);
		for (OWLClassExpression super_class : sc){
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

	private void normalizeLHS(OWLClassExpression super_class, OWLClass cls) {
		// TODO Auto-generated method stub
		
	}

	private void normalizeRHS(OWLClassExpression sub_class, OWLClassExpression super_class) {
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
			System.out.println("?: " + super_class.getClassExpressionType());
		}
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
