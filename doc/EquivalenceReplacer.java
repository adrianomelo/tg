import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.util.OWLEntityRemover;

public class EquivalenceReplacer {

	public static void main(String[] args) {

		try {
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			OWLDataFactory df = manager.getOWLDataFactory();
			File file = new File("/home/anne/Ontologies/test/sciUnits.owl");
			
			//Load Ontology
			OWLOntology localont = manager.loadOntologyFromOntologyDocument(file);
			System.out.println("Loaded ontology: " + localont);
			IRI documentIRI = manager.getOntologyDocumentIRI(localont);
			System.out.println("    from: " + documentIRI);

			LinkedList<OWLOntologyChange> changes = new LinkedList<OWLOntologyChange>();
			OWLEntityRemover remover = new OWLEntityRemover(manager, Collections.singleton(localont));

		    OWLAxiom axiom;
			for(OWLClass cl : localont.getClassesInSignature()){
				for(OWLEquivalentClassesAxiom eqcl : localont.getEquivalentClassesAxioms(cl)){
					for(OWLClass cl2 : eqcl.getNamedClasses()){
						if(cl != cl2){
						axiom = df.getOWLSubClassOfAxiom(cl, cl2); 
						changes.add(new AddAxiom(localont, axiom));
						}
					}
				 changes.add(new RemoveAxiom(localont, eqcl));
				}
			}		
			manager.applyChanges(changes);
			remover.reset();
			manager.saveOntology(localont, IRI.create(file.toURI()));

		} catch (OWLOntologyCreationIOException e) {
			// IOExceptions during loading get wrapped in an
			// OWLOntologyCreationIOException
			IOException ioException = e.getCause();
			if (ioException instanceof FileNotFoundException) {
				System.out.println("Could not load ontology. File not found: "
						+ ioException.getMessage());
			} else if (ioException instanceof UnknownHostException) {
				System.out.println("Could not load ontology. Unknown host: "
						+ ioException.getMessage());
			} else {
				System.out.println("Could not load ontology: "
						+ ioException.getClass().getSimpleName() + " "
						+ ioException.getMessage());
			}
		} catch (UnparsableOntologyException e) {
			// If there was a problem loading an ontology because there are
			// syntax errors in the document (file) that
			// represents the ontology then an UnparsableOntologyException is
			// thrown
			System.out.println("Could not parse the ontology: "
					+ e.getMessage());
			// A map of errors can be obtained from the exception
			Map<OWLParser, OWLParserException> exceptions = e.getExceptions();
			// The map describes which parsers were tried and what the errors
			// were
			for (OWLParser parser : exceptions.keySet()) {
				System.out.println("Tried to parse the ontology with the "
						+ parser.getClass().getSimpleName() + " parser");
				System.out.println("Failed because: "
						+ exceptions.get(parser).getMessage());
			}		
		} catch (UnloadableImportException e) {
			System.out.println("Could not load import: " + e.getImportsDeclaration());
			OWLOntologyCreationException cause = e.getOntologyCreationException();
			System.out.println("Reason: " + cause.getMessage());
		} catch (OWLOntologyCreationException e) {
			System.out.println("Could not load ontology: " + e.getMessage());
		} catch (OWLOntologyStorageException e) {
			System.out.println("Could not save ontology: " + e.getMessage());
		} catch (OWLOntologyChangeException e) {
			System.out.println("Could not modify ontology: " + e.getMessage());
		}
	}
}
