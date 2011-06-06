package br.ufpe.cin;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owl.model.OWLException;

public class Main {
	public static void main(String[] args) {
		System.out.println("The Connection Method - Matrix Transformation");
		
		String file = "doc/owl/teste1.owl";
		// String file = "doc/owl/hierarquia.owl";
		// String file = "doc/owl/blank.owl";

		try {
			Ontology ontology = new Ontology();
			ontology.loadFromFile(file);
		
			Normalization n = new Normalization(ontology);
			n.normalizeOntology();
		} catch (OWLException e){
			e.printStackTrace();
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
