package br.ufpe.cin;

import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class DisjunctionVisitor extends AbstractNormalizationVisitor {
	
	public DisjunctionVisitor(Ontology o) {
		super(o);
	}

	// conjunctions
	public void visit(OWLObjectIntersectionOf intersection) {
		System.out.println("Impurity Detected.. extracting intersection!");
		extractOWLClassExpression(intersection);
	}

	public void visit(OWLObjectSomeValuesFrom some) {
		System.out.println("Impurity Detected.. extracting Some Values!");
		extractOWLClassExpression(some);
	}
}
