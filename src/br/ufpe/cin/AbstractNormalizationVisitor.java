package br.ufpe.cin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owlapi.util.OWLEntityRemover;

public abstract class AbstractNormalizationVisitor extends OWLClassExpressionVisitorAdapter{
	
	protected Ontology ontology;
	protected OWLOntologyManager manager;
	protected OWLDataFactory factory;
	protected Stack<OWLClassExpression> stack;
	private static int num = 0;

	public AbstractNormalizationVisitor(Ontology o)
	{
		this.manager  = o.getManager();
		this.factory  = o.getFactory();
		ontology = o;
		this.stack = new Stack<OWLClassExpression>();
	}
	
	protected void extractOWLClassExpression(OWLClassExpression axiom) {
		OWLClass N = factory.getOWLClass(IRI.create("Extracted" + AbstractNormalizationVisitor.nextNumber()));
		
		OWLAxiom inclusion = factory.getOWLSubClassOfAxiom(N, axiom);
		AddAxiom addAxiom  = new AddAxiom(ontology.getOntology(), inclusion);
		manager.applyChange(addAxiom);
		
		stack.add(N);
		
		System.out.println("Impurity " + axiom + " replaced by " + N);
	}
	private void removeExpression(OWLClassExpression exp)
	{
		OWLClass N = factory.getOWLClass(IRI.create("Removed" + AbstractNormalizationVisitor.nextNumber()));
		stack.add(N);

		System.out.println("This expression is not allowed in ALC: " + exp + " will be replaced by: " + N);
	}
	
	public void visit(OWLObjectUnionOf union)
	{
		Set<OWLClassExpression> expressions = union.getOperands();
		
		int expression_counter = 0;
		for (OWLClassExpression exp : expressions)
		{
			exp.accept(this);	
			expression_counter = expression_counter + 1;
		}
		
		Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
		while (expression_counter-- > 0){
			operands.add(stack.pop());
		}
		
		OWLObjectUnionOf new_union = factory.getOWLObjectUnionOf(operands);
		stack.add(new_union);
	}
	public void visit(OWLObjectIntersectionOf intersection)
	{
		Set<OWLClassExpression> expressions = intersection.getOperands();
		
		int expression_counter = 0;
		for (OWLClassExpression exp : expressions)
		{
			//OWLClassExpression exp_nnf = exp.getNNF();
			//exp_nnf.accept(this);
			exp.accept(this);
			expression_counter = expression_counter + 1;
		}
		
		Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
		while (expression_counter-- > 0){
			operands.add(stack.pop());
		}
		
		OWLObjectIntersectionOf new_intersection = factory.getOWLObjectIntersectionOf(operands);
		stack.add(new_intersection);
	}
	public void visit(OWLObjectAllValuesFrom all)
	{
		OWLClassExpression expression = all.getNNF();
		OWLObjectPropertyExpression property = all.getProperty();
		expression.accept(this);
		
		OWLClassExpression new_expression = stack.pop();
		OWLObjectAllValuesFrom new_all = factory.getOWLObjectAllValuesFrom(property, new_expression);
		
		stack.add(new_all);
	}
	public void visit(OWLObjectSomeValuesFrom some)
	{
		OWLClassExpression expression = some.getNNF();
		OWLObjectPropertyExpression property = some.getProperty();
		
		expression.accept(this);
		
		OWLClassExpression new_expression = stack.pop();
		OWLObjectSomeValuesFrom new_some = factory.getOWLObjectSomeValuesFrom(property, new_expression);
		
		stack.add(new_some);
	}
	public void visit(OWLObjectComplementOf complement)
	{
		OWLClassExpression exp = complement.getNNF();
		
		if (!(exp instanceof OWLClass))
			System.out.println("This complement of object is invalid: " + complement);
		
		stack.add(exp);
	}
	public void visit(OWLClass cls)
	{
		stack.add(cls);
	}
	public void visit(OWLObjectMinCardinality card)
	{
		removeExpression(card);
	}
	public void visit(OWLObjectHasValue value)
	{
		removeExpression(value);
	}
	public void visit(OWLObjectOneOf one){
		removeExpression(one);
	}
	public OWLClassExpression getNewExpression()
	{
		return stack.pop();
	}
	public static int nextNumber(){
		return num++;
	}
}
