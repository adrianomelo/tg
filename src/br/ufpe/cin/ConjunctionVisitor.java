package br.ufpe.cin;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;

public class ConjunctionVisitor extends AbstractNormalizationVisitor {
	
	public ConjunctionVisitor(Ontology o) {
		super(o);
	}

	// DISJUNCTIONS
	public void visit(OWLObjectUnionOf union)
	{
		System.out.println("Extracting union: " + union);
		extractOWLClassExpression(union);
		removed_axioms.add(union);
	}
	
	public void visit(OWLObjectAllValuesFrom all)
	{
		System.out.println("Extracting all values from: " + all);
		extractOWLClassExpression(all);
		
		removed_axioms.add(all);
	}

	// CONJUNCTIONS
	public void visit(OWLObjectIntersectionOf intersection)
	{
		Set<OWLClassExpression> operands = intersection.getOperands();
		for (OWLClassExpression exp : operands){
			exp.accept(this);
		}
	}
	
	public void visit(OWLObjectSomeValuesFrom some)
	{
		Set<OWLClassExpression> expressions = some.getNestedClassExpressions();
		for (OWLClassExpression exp : expressions)
			exp.accept(this);
	}
	
	public void visit(OWLObjectComplementOf complement)
	{	
		OWLClassExpression cls = complement.getObjectComplementOf();
		cls.accept(this);
	}
	
}
