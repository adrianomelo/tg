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
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

public class ConjunctionVisitor extends OWLClassExpressionVisitorAdapter {
	private OWLOntology ontology;
	private Vector<OWLClassExpression> removed_axioms;

	public ConjunctionVisitor(OWLOntology o)
	{
		this.ontology = o;
		this.removed_axioms = new Vector<OWLClassExpression>();
	}
	
	@SuppressWarnings("unchecked")
	public Vector<OWLClassExpression> getRemovedAxioms()
	{
		return removed_axioms;
	}
	
	// DISJUNCTIONS
	public void visit(OWLObjectUnionOf union)
	{
		System.out.println("visitando union" + union);
		
		Set<OWLClassExpression> operands = union.getOperands();
		for (OWLClassExpression exp : operands){
			// TODO remover a uniao
			exp.accept(this);
		}
	}
	
	public void visit(OWLObjectAllValuesFrom all)
	{
		System.out.println("visitando objectall " + all);
		
		OWLClassExpression exp = all.getFiller();
		// TODO remover da superclasse
		exp.accept(this);
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
		for (OWLClassExpression exp : expressions){
			exp.accept(this);
		}
	}
	
	public void visit(OWLObjectComplementOf complement)
	{	
		OWLClassExpression cls = complement.getObjectComplementOf();
		cls.accept(this);
	}
	
}
