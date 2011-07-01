package br.ufpe.cin;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;

public class EquivalentClassesNormalization {
	private Ontology ontology = null;
	private OWLOntologyManager manager = null;
	private SubClassOfNormalization subclass_normalization = null;

	public EquivalentClassesNormalization(Ontology o) {
		this.ontology = o;
		manager = o.getManager();
		subclass_normalization = new SubClassOfNormalization(o);
	}
	
	void normalizeAxiom(OWLEquivalentClassesAxiom axiom) {
		//System.out.println("Equivalent Axiom visited: " + axiom);
		
		Set<OWLSubClassOfAxiom> axioms = transformToSubClassOfAxiom(axiom);
		
		RemoveAxiom remove = new RemoveAxiom(ontology.getOntology(), axiom);
		manager.applyChange(remove);
		
		for (OWLSubClassOfAxiom subclass : axioms){
			AddAxiom add = new AddAxiom(ontology.getOntology(), subclass);
			manager.applyChange(add);
			subclass_normalization.normalizeAxiom(subclass);
		}
	}
	
	Set<OWLSubClassOfAxiom> transformToSubClassOfAxiom (OWLEquivalentClassesAxiom eq)
	{
		OWLDataFactory factory = ontology.getFactory();
		
		Set<OWLSubClassOfAxiom> set = new HashSet<OWLSubClassOfAxiom>();
		
		Set<OWLClassExpression> exp1 = eq.getClassExpressions();
		Set<OWLClassExpression> exp2 = eq.getClassExpressions();
		
		for (OWLClassExpression e1 : exp1){
			for (OWLClassExpression e2 : exp2){
				if (e1 == e2)
					continue;
				
				OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom(e1, e2);
				OWLSubClassOfAxiom axiom2 = factory.getOWLSubClassOfAxiom(e2, e1);
				
				set.add(axiom);
				set.add(axiom2);
			}
		}
		
		return set;
	}
}
