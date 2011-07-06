package br.ufpe.cin;

import java.util.Vector;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;

public class SubClassOfNormalization {
	private Ontology ontology = null;
	private OWLOntologyManager manager;
	private OWLDataFactory factory;

	public SubClassOfNormalization(Ontology o) {
		this.ontology = o;
		manager = o.getManager();
		factory = o.getFactory();
	}

	void normalizeAxiom(OWLSubClassOfAxiom axiom) {
		//System.out.println("Axiom visited: " + axiom);
		if (isInNormalForm(axiom))
			return;
		
		System.out.println("\nAxiom to normalize: " + axiom);
		
		OWLClassExpression left = axiom.getSubClass();
		OWLClassExpression new_left = purify(left);
			
		OWLClassExpression right = axiom.getSuperClass();
		OWLClassExpression new_right = purify(right);
		
		RemoveAxiom remove = new RemoveAxiom(ontology.getOntology(), axiom);
		manager.applyChange(remove);
		
		OWLSubClassOfAxiom new_axiom = factory.getOWLSubClassOfAxiom(new_left, new_right);
		AddAxiom add = new AddAxiom(ontology.getOntology(), new_axiom);
		manager.applyChange(add);

		if (!Normalization.isInNormalForm(new_left, new_right)) {
			create_assertion (new_left, new_right);
		}
	}
	
	private boolean isInNormalForm(OWLSubClassOfAxiom axiom) {
		OWLClassExpression exp_left = axiom.getSubClass();
		OWLClassExpression exp_right = axiom.getSuperClass();
		
		if (Normalization.isInNormalForm(exp_left, exp_right))
			return true;
		
		return false;
	}

	private void create_assertion(OWLClassExpression new_left,  OWLClassExpression new_right) {
		OWLDataFactory factory = ontology.getFactory();
		
		OWLClass N = factory.getOWLClass(IRI.create("Added" + (int) (Math.random() * 1000)));
		
		OWLSubClassOfAxiom left = factory.getOWLSubClassOfAxiom(new_left, N);
		OWLSubClassOfAxiom right = factory.getOWLSubClassOfAxiom(N, new_right);
		
		AddAxiom left_axiom  = new AddAxiom(ontology.getOntology(), left);
		AddAxiom right_axiom = new AddAxiom(ontology.getOntology(), right);
		
		OWLOntologyManager manager = ontology.getManager();
		manager.applyChange(left_axiom);
		manager.applyChange(right_axiom);
	}

	private OWLClassExpression purify(OWLClassExpression expression){
		AbstractNormalizationVisitor normalization_visitor = null;
		
		if (Normalization.isConjunction(expression) && !Normalization.isPureConjunction(expression)){
			normalization_visitor = new ConjunctionVisitor(ontology);
			expression.accept(normalization_visitor);
			
		} else if (Normalization.isDisjunction(expression) && !Normalization.isPureDisjunction(expression)){
			normalization_visitor = new DisjunctionVisitor(ontology);
			expression.accept(normalization_visitor);
			
		} else if (Normalization.isConcept(expression)){
			return expression;
			
		} else {
			normalization_visitor = new DisjunctionVisitor(ontology);
			expression.accept(normalization_visitor);	
		}

		return normalization_visitor.getNewExpression();
	}

}
