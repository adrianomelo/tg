package br.ufpe.cin;

import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
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
			System.out.println("disjuction: " + super_class);
		} else if (this.isConjunction(super_class)){
			System.out.println("conjunction: " + super_class);
		} else {			
			System.out.println("?: " + super_class.getClassExpressionType());
		}
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
}
