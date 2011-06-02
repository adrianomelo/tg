package br.ufpe.cin;

import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
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
			this.normalizeAxiom(cls);
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
	
	// i)    C [ ^C
	// ii)  vD [  C
	// iii) ^C [ vD
	private boolean isInPositiveNormalForm (OWLClass cls){
		Set<OWLClassExpression> axioms = cls.getSubClasses(ontology.ontology);
		
		boolean is_pureconjunction = true, is_puredisjunction = true;
		for (OWLClassExpression axiom : axioms){
			is_pureconjunction = is_pureconjunction && isPureConjunction(axiom);
			is_puredisjunction = is_puredisjunction && isPureDisjunction(axiom);
		}
		
		// left side is a concept and right side is pure
		if (is_pureconjunction || is_puredisjunction) 
			return true;
		
	}
	
	private boolean isDisjunction(OWLClassExpression cls)
	{
		if (cls.getClassExpressionType() == ClassExpressionType.OWL_CLASS)
			return true;
		
		if (cls.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF)
			return true;
		
		if (cls.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM)
			return true; 
		
		if (cls.getClassExpressionType() == ClassExpressionType.DATA_ALL_VALUES_FROM)
			return true;
		
		return false;
	}
	private boolean isPureDisjunction(OWLClassExpression cls){
		if (!isDisjunction(cls))
			return false;
		
		Set<OWLClassExpression> sub_axioms = null;
		
		if (cls instanceof OWLClass){
			OWLClass sub_class = (OWLClass) cls;
			sub_axioms = sub_class.getEquivalentClasses(ontology.ontology);
			sub_axioms.addAll(sub_class.getSubClasses(ontology.ontology));
		} 
		else if (cls instanceof OWLObjectUnionOf){
			OWLObjectUnionOf union = (OWLObjectUnionOf) cls;
			sub_axioms = union.getOperands();
		}
		else if (cls instanceof OWLObjectAllValuesFrom){
			OWLObjectAllValuesFrom all_values = (OWLObjectAllValuesFrom) cls;
			sub_axioms = all_values.getNestedClassExpressions();
		}
		else if (cls instanceof OWLDataAllValuesFrom){
			OWLDataAllValuesFrom data_values = (OWLDataAllValuesFrom) cls;
			sub_axioms = data_values.getNestedClassExpressions();
		}
		
		for (OWLClassExpression axiom : sub_axioms){
			if (!isDisjunction(axiom)){
				return false;
			}
		}
		
		return true;
	}
	
	
	private boolean isConjunction(OWLClassExpression cls)
	{
		if (cls.getClassExpressionType() == ClassExpressionType.OWL_CLASS)
			return true;
		
		if (cls.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF)
			return true;
		
		if (cls.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM)
			return true; 
		
		if (cls.getClassExpressionType() == ClassExpressionType.DATA_SOME_VALUES_FROM)
			return true;
		
		return false;
	}
	
	private boolean isPureConjunction(OWLClassExpression cls){
		if (!isConjunction(cls))
			return false;
		
		Set<OWLClassExpression> sub_axioms = null;
		
		if (cls instanceof OWLClass){
			OWLClass sub_class = (OWLClass) cls;
			sub_axioms = sub_class.getEquivalentClasses(ontology.ontology);
			sub_axioms.addAll(sub_class.getSubClasses(ontology.ontology));
		} 
		else if (cls instanceof OWLObjectIntersectionOf){
			OWLObjectIntersectionOf intersection = (OWLObjectIntersectionOf) cls;
			sub_axioms = intersection.getOperands();
		}
		else if (cls instanceof OWLObjectSomeValuesFrom){
			OWLObjectSomeValuesFrom some_values = (OWLObjectSomeValuesFrom) cls;
			sub_axioms = some_values.getNestedClassExpressions();
		}
		else if (cls instanceof OWLDataSomeValuesFrom){
			OWLDataSomeValuesFrom data_values = (OWLDataSomeValuesFrom) cls;
			sub_axioms = data_values.getNestedClassExpressions();
		}
		
		for (OWLClassExpression axiom : sub_axioms){
			if (!isConjunction(axiom)){
				return false;
			}
		}
		
		return true;
	}
}
