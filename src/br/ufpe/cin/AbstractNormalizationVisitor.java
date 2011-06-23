package br.ufpe.cin;

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectComplementOf;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;
import org.semanticweb.owlapi.util.OWLEntityRemover;

public abstract class AbstractNormalizationVisitor {
	
	protected OWLOntology ontology;
	protected OWLOntologyManager manager;
	protected OWLDataFactory factory;
	protected Vector<OWLSubClassOfAxiom> extrated_expressions;

	public AbstractNormalizationVisitor(OWLOntology o)
	{
		this.ontology = o;
		this.manager  = o.getOWLOntologyManager();
		this.factory  = manager.getOWLDataFactory();
		this.extrated_expressions = new Vector<OWLSubClassOfAxiom>();
	}
	
	protected void extractOWLClassExpression(OWLClassExpression axiom) {
		OWLEntityRemover visitor = new OWLEntityRemover(manager, Collections.singleton(ontology));
		Set<OWLClass> cls = axiom.getClassesInSignature();
		for (OWLClass classz : cls){
			System.out.println("Removed: " + classz);
			classz.accept(visitor);
		}
		manager.applyChanges(visitor.getChanges());
		
		manager.removeAxiom(ontology, (OWLAxiom) axiom);
		
		/*OWLClass N = factory.getOWLClass(IRI.create("Extracted" + (int) (Math.random() * 1000)));
		OWLAxiom inclusion = factory.getOWLSubClassOfAxiom(N, axiom);
		AddAxiom addAxiom  = new AddAxiom(ontology, inclusion);
		manager.applyChange(addAxiom);*/
	}
	
	abstract public OWLClassExpression visit(OWLObjectUnionOf union);
	abstract public OWLClassExpression visit(OWLObjectAllValuesFrom all);
	abstract public OWLClassExpression visit(OWLObjectIntersectionOf intersection);
	abstract public OWLClassExpression visit(OWLObjectSomeValuesFrom some);
	abstract public OWLClassExpression visit(OWLObjectComplementOf complement);
	
	public Vector<OWLClassExpression> getRemovedAxioms ()
	{
		return extrated_expressions;
	}
}
