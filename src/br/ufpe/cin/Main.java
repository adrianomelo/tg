package br.ufpe.cin;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.util.OWLClassExpressionVisitorAdapter;

public class Main {
	public static void main(String[] args) {
		try {
			
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			
			File file = new File("doc/owl/hierarquia.owl");
			
			OWLOntology ont = manager.loadOntologyFromOntologyDocument(file);
			System.out.println("Loaded ontology: " + ont);
			
			IRI documentIRI = manager.getOntologyDocumentIRI(ont);
			System.out.println("    from: " + documentIRI);
			
			Set<OWLOntology> set = Collections.singleton(ont);
			OntologyVisitor vist = new OntologyVisitor(set);
			
			manager.removeOntology(ont);
			
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
	
	private static class OntologyVisitor extends OWLClassExpressionVisitorAdapter {

        public OntologyVisitor(Set<OWLOntology> onts) {
        	for (OWLOntology o : onts){
        		
        	}
        }
        public void visit(OWLClass desc) {
        	System.out.println(desc);
        }
    }
}
