package br.ufpe.cin;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLClass;

public class Ontology {
	OWLOntologyManager manager;
	OWLOntology ontology;
	
	public Ontology()
	{
		manager = OWLManager.createOWLOntologyManager();
	}
	
	public void loadFromFile(String filePath) throws OWLOntologyCreationException
	{
		File file = new File(filePath);
		ontology = manager.loadOntologyFromOntologyDocument(file);
	}
	
	public Set<OWLClass> getClasses() throws OWLOntologyCreationException
	{
		if (this.ontology != null){
			return this.ontology.getClassesInSignature();
		}
		
		throw new OWLOntologyCreationException();
	}
	
	public Set<OWLObjectProperty> getClassProperties (OWLClass classe){
		Set<OWLObjectProperty> properties = classe.getObjectPropertiesInSignature();
		
		for (OWLObjectProperty property : properties){
			System.out.println(property);
		}
		
		return properties;
	}
	
	public Set getSuperClasses(OWLClass classe){
		System.out.println("superclasses de " + classe);
		Set<OWLClassExpression> superClasses = classe.getSuperClasses(ontology);
		
		for (OWLClassExpression cls : superClasses){
			System.out.println(cls);
		}
		
		return null;
	}
	
	public Set getEquivalentClasses (OWLClass classe){
		System.out.println("classes equivalentes a " + classe);
		
		Set<OWLClassExpression> eqClasses = classe.getEquivalentClasses(ontology);
		
		for (OWLClassExpression cls : eqClasses){
			System.out.println(cls);
		}
		
		return null;
	}
}
