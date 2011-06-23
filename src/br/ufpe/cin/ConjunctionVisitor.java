package br.ufpe.cin;

import java.util.Set;

import java.util.Vector;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLAxiomVisitorEx;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLObjectVisitor;
import org.semanticweb.owlapi.model.OWLObjectVisitorEx;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

public class ConjunctionVisitor extends AbstractNormalizationVisitor {
	OWLClassExpression left;
	
	public ConjunctionVisitor(OWLOntology o) {
		super(o);
	}

	public ConjunctionVisitor(OWLOntology ontology, OWLClassExpression left) {
		super(ontology);
		
		this.left = left;
		
		left.accept(this);
		
		accept(left, this);
	}
	
	OWLClassExpression accept (OWLClassExpression classz, ConjunctionVisitor visitor){
		if (classz instanceof OWLObjectUnionOf)
			OWLObjectUnionOf exp = (OWLObjectUnionOf) classz;
			return visitor.visit();
	}

	// DISJUNCTIONS
	public void visit(OWLObjectUnionOf union)
	{
		System.out.println("N‹o ser‡ visitado!");
	}
	
	public void visit(OWLObjectAllValuesFrom all)
	{
		System.out.println("N‹o ser‡ visitado!");
	}

	// CONJUNCTIONS
	public void visit(OWLObjectIntersectionOf intersection)
	{
		Set<OWLClassExpression> operands = intersection.getOperands();
		Vector<OWLClassExpression> new_operands = new Vector<OWLClassExpression>();
		
		for (OWLClassExpression exp : operands){
			if (!Normalization.isConjunction(exp)) { // is a disjunction
				OWLClass N = factory.getOWLClass(IRI.create("Extracted" + (int) (Math.random() * 1000)));
				OWLSubClassOfAxiom N_axiom = factory.getOWLSubClassOfAxiom(exp, N);
				extrated_expressions.add(N_axiom);
				
				new_operands.add(N);
			}else{
				new_operands.add(exp);
			}
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

	public Vector<OWLSubClassOfAxiom> getDisjunctions() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
