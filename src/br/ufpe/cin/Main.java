package br.ufpe.cin;

import java.util.Iterator;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class Main {
	public static void main(String[] args) {
		System.out.println("The Conexions Method - Matriz Transformation");
		
		String file = "doc/owl/pizza.owl";
		// String file = "doc/owl/hierarquia.owl";
		// String file = "doc/owl/blank.owl";

		try {
			Ontology ontology = new Ontology();
			ontology.loadFromFile(file);
		
			Set<OWLClass> classes = ontology.getClasses();
			
			Iterator<OWLClass> it1 = classes.iterator();
			while (it1.hasNext()){
				OWLClass c1 = it1.next();
				
				Iterator<OWLClass> it2 = classes.iterator();
				while (it2.hasNext()){
					OWLClass c2 = it2.next();
					
					if (!c1.equals(c2)){
						System.out.println(c1 + " - " + c2);
					}
				}
			}
		
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
