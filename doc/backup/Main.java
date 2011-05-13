package br.ufpe.cin;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
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
			
			OntologyVisitor visitor = new OntologyVisitor(ont);
			
			String base = "http://www.cin.ufpe.br/~astm/teste.owl#";
            PrefixManager pm = new DefaultPrefixManager(base);
			
			OWLDataFactory factory = manager.getOWLDataFactory();
			
			Set<OWLClass> classes = ont.getClassesInSignature();
			for (OWLClass c : classes){
				System.out.println("achou " + c);
			}
			
			OWLClass thing = factory.getOWLThing();
			
			thing.accept(visitor);
			
			OntologyClasses clses = OntologyClasses.getInstance();
			Iterator<OWLClassExpression> i = clses.getIterator();
			
			while (i.hasNext())
				System.out.println(i.next());
			
			manager.removeOntology(ont);
			
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}
	}
	
	private static class OntologyVisitor extends OWLClassExpressionVisitorAdapter {
		private OWLOntology ontology;
        
		public OntologyVisitor(OWLOntology onto){
        	this.ontology = onto;
        }
        
        public void visit(OWLClass desc) {        	
        	Set<OWLClassExpression> classes = desc.getSubClasses(this.ontology);
        	
        	for (OWLClassExpression classe : classes){
        		
        		OntologyClasses clses = OntologyClasses.getInstance();
        		clses.addClass(classe);
        		
        		classe.accept(this);
        	}
        }
    }
}
