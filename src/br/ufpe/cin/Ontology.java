package br.ufpe.cin;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.helper.OWLBuilder;
import org.semanticweb.owl.util.OWLManager;

public class Ontology {
	OWLDataFactory factory;
	OWLOntology ontology;
	OWLRDFParser parser;
	
	public Ontology()
	{
	}
	
	public void loadFromFile(String filePath) throws OWLException, URISyntaxException
	{
		parser = new OWLRDFParser();
		parser.setConnection( OWLManager.getOWLConnection() );
		ontology = parser.parseOntology(new URI("file:/Users/adrianomelo/git/tg/doc/owlapi1/examples/pizza/pizza.owl"));
		factory = ontology.getOWLDataFactory();
	}
	
	public Set<OWLClass> getClasses() throws OWLException 
	{
		if (this.ontology != null){
			return this.ontology.getClasses();
		}
		
		throw new OWLException("[getClasses] Ontology not loaded.");
	}
	
	public Set<OWLDescription> getSuperClasses(OWLClass classe) throws OWLException
	{
		Set<OWLDescription> superClasses = classe.getSuperClasses(ontology);	
		return superClasses;
	}
	
	public Set<OWLDescription> getEquivalentClasses (OWLClass classe) throws OWLException
	{
		Set<OWLDescription> eqClasses = classe.getEquivalentClasses(ontology);
		return eqClasses;
	}

	public boolean isInNormalForm()
	{
		return false;
	}
}
