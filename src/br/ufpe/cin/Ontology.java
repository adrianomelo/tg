package br.ufpe.cin;

import java.io.File;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class Ontology {
	OWLOntologyManager manager;
	OWLOntology ontology;
	OWLDataFactory factory;
	
	public void loadFromFile(String filePath) throws OWLOntologyCreationException
	{
		File file = new File(filePath);
		manager  = OWLManager.createOWLOntologyManager();
		ontology = manager.loadOntologyFromOntologyDocument(file);
		factory  = manager.getOWLDataFactory();
	}
	
	public Set<OWLClass> getClasses() throws OWLOntologyCreationException
	{
		if (this.ontology != null){
			return this.ontology.getClassesInSignature();
		}
		
		throw new OWLOntologyCreationException();
	}
	
	public Set<OWLObjectProperty> getObjectProperties (OWLClass classe)
	{
		Set<OWLObjectProperty> properties = classe.getObjectPropertiesInSignature();
		return properties;
	}
	
	public Set<OWLDataProperty> getDataProperties(OWLClass classe) 
	{
		Set<OWLDataProperty> properties = classe.getDataPropertiesInSignature();
		return properties;
	}
	
	public Set<OWLClassExpression> getSuperClasses(OWLClass classe)
	{
		Set<OWLClassExpression> superClasses = classe.getSuperClasses(ontology);	
		return superClasses;
	}
	
	public Set<OWLClassExpression> getEquivalentClasses (OWLClass classe)
	{
		Set<OWLClassExpression> eqClasses = classe.getEquivalentClasses(ontology);
		return eqClasses;
	}

	public boolean isInNormalForm()
	{
		return false;
	}

	public void save() throws OWLOntologyStorageException {
		manager.saveOntology(ontology);
	}
}
