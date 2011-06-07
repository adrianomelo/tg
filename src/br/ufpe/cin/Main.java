package br.ufpe.cin;

import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {
	public static void main(String[] args) {
		System.out.println("The Connection Method - Matrix Transformation");
		
		//String file = "doc/owl/teste1.owl";
		String file = "doc/owl/pizza.owl";
		// String file = "doc/owl/hierarquia.owl";
		// String file = "doc/owl/blank.owl";

		try {
			Ontology ontology = new Ontology();
			ontology.loadFromFile(file);
		
			Normalization n = new Normalization(ontology);
			n.normalizeOntology();
		} catch (OWLOntologyInputSourceException e){
			System.out.println("erro ao carregar a ontologia");
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}
