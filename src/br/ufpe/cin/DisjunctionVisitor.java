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

	public DisjunctionVisitor(OWLOntology o) {
		super(o);
	}

	public DisjunctionVisitor(OWLOntology ontology, OWLClassExpression left,
			OWLClassExpression right) {
		// TODO Auto-generated constructor stub
	}

	// disjunctions
	@Override
	public void visit(OWLObjectUnionOf union) {
		Set<OWLClassExpression> operands = union.getOperands();
		for (OWLClassExpression exp : operands){
			exp.accept(this);
		}
	}

	@Override
	public void visit(OWLObjectAllValuesFrom all) {
		Set<OWLClassExpression> expressions = all.getNestedClassExpressions();
		for (OWLClassExpression exp : expressions)
			exp.accept(this);
	}

	// conjunctions
	@Override
	public void visit(OWLObjectIntersectionOf intersection) {
		System.out.println("Extracting intersection: " + intersection);
		extractOWLClassExpression(intersection);
		removed_axioms.add(intersection);
	}

	@Override
	public void visit(OWLObjectSomeValuesFrom some) {
		System.out.println("Extracting some values from: " + some);
		extractOWLClassExpression(some);
		removed_axioms.add(some);
	}

	@Override
	public void visit(OWLObjectComplementOf complement) {
		OWLClassExpression cls = complement.getObjectComplementOf();
		cls.accept(this);
	}

	public Vector<OWLSubClassOfAxiom> getConjunctions() {
		// TODO Auto-generated method stub
		return null;
	}

}
