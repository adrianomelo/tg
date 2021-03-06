package br.ufpe.cin;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.OWLCompositeOntologyChange;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataAllValuesFrom;
import org.semanticweb.owlapi.model.OWLDataComplementOf;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

public class Normalization {
	Ontology ontology = null;
	SubClassOfNormalization subclass_normalization = null;
	EquivalentClassesNormalization equivalent_normalization = null;
	
	public Normalization(Ontology o)
	{
		this.ontology = o;
		subclass_normalization = new SubClassOfNormalization(o);
		equivalent_normalization = new EquivalentClassesNormalization(o);
	}

	public void normalizeOntology() throws OWLOntologyCreationException
	{
		OWLOntology onto = ontology.getOntology();
		Set<OWLClass> classes = onto.getClassesInSignature();
		for (OWLClass cls : classes){
			Set<OWLEquivalentClassesAxiom> eq = onto.getEquivalentClassesAxioms(cls);
			Set<OWLSubClassOfAxiom> sub       = onto.getSubClassAxiomsForSubClass(cls);
			
			/*System.out.println(cls);
			for (OWLEquivalentClassesAxiom c : eq){
				System.out.println("__________________");
				//for (OWLClass c2 : c.getNamedClasses())
				//	System.out.println("	classes: " + c2);
				
				for (OWLClassExpression e : c.getClassExpressions())
					System.out.println("	expressions: " + e);
			}*/
			
			/*System.out.println(cls);
			for (OWLSubClassOfAxiom s : sub){
				System.out.println("	sub: " + s.getSubClass());
				System.out.println("	super: " + s.getSuperClass());
			}*/
			
			for (OWLEquivalentClassesAxiom axiom : eq)
				equivalent_normalization.normalizeAxiom(axiom);
			
			for (OWLSubClassOfAxiom axiom : sub)
				subclass_normalization.normalizeAxiom(axiom);
			
		}
	}
	
	// i)    C [ ^C
	// ii)  vD [  C
	// iii) ^C [ vD
	public static boolean isInNormalForm (OWLClassExpression cls_left, OWLClassExpression cls_right)
	{
		if (isConcept(cls_left) && isPureConjunction(cls_right))
			return true;
		
		if (isPureDisjunction(cls_left) && isConcept(cls_right))
			return true;
		
		if (isPureConjunction(cls_left) && isPureDisjunction(cls_right))
			return true;
			
		return false;
	}
	
	public static boolean isConcept(OWLClassExpression cls)
	{
		return cls instanceof OWLClass;
	}

	public static boolean isDisjunction(OWLClassExpression cls)
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
	public static boolean isPureDisjunction(OWLClassExpression cls){
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
	
	
	public static boolean isConjunction(OWLClassExpression cls)
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
	
	public static boolean isPureConjunction(OWLClassExpression cls){
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