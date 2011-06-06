/*
 * Copyright (C) 2003 The University of Manchester 
 * Copyright (C) 2003 The University of Karlsruhe
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 ****************************************************************
 * Source code information
 * -----------------------
 * Filename           $RCSfile: InferenceHelper.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/10/19 17:18:05 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.inference; 
import java.util.Set;
import java.util.HashSet;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.inference.OWLClassReasoner;
import org.semanticweb.owl.model.change.OntologyChangeListener;
import org.semanticweb.owl.model.helper.OntologyHelper;

import uk.ac.man.cs.img.dig.reasoner.Reasoner;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.OntologyChangeSource;
import java.util.Map;
import java.util.HashMap;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.PrintWriter;
import java.util.Iterator;
import uk.ac.man.cs.img.owl.io.dig1_0.RenderingVisitor;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import org.semanticweb.owl.validation.SpeciesValidator;
import org.semanticweb.owl.io.Renderer;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.List;
import org.semanticweb.owl.inference.OWLTaxonomyReasoner;
import org.semanticweb.owl.inference.OWLIndividualReasoner;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.AddSuperClass;
import org.semanticweb.owl.model.change.AddEquivalentClass;
import org.apache.log4j.Logger;
// Generated package name


/**
 * A helper class that does some simple reasoning tasks.
 *
 * @author Sean Bechhofer
 * @version $Id: InferenceHelper.java,v 1.2 2004/10/19 17:18:05 sean_bechhofer Exp $
 */

public class InferenceHelper {

    static Logger logger = Logger.getLogger(InferenceHelper.class);

    /* This class illustrates the kinds of things we can do with the
     * inference/reasoning components. Basically amount to running up
     * and down hiearchies, spitting things out as we go. There's very
     * little decent error handling in here -- it's simply intended as
     * a demonstration rather than something we'd really use....*/
    
    private static void dumpHierarchy( OWLTaxonomyReasoner reasoner, 
				      PrintWriter pw, 
				      OWLClass clazz, 
				      int level ) throws OWLException {
	for (int i=0; i<level; i++) {
	    pw.print(" ");
	}
	pw.println( clazz.getURI() );

	Set subs = reasoner.subClassesOf( clazz );

	for (Iterator sit = subs.iterator(); sit.hasNext(); ) {
	    for (Iterator innerIt = ((Set) sit.next()).iterator();
		 innerIt.hasNext(); ) { 
		OWLClass cl = (OWLClass) innerIt.next();
		dumpHierarchy( reasoner, pw, cl, level+1 );
	    }			
	}
    }

    /** Dumps a textual rendering of the class hierarchy to the given
     * writer */
    public static void dumpHierarchy( OWLTaxonomyReasoner reasoner, 
				      PrintWriter pw ) throws OWLException {
	try {
	    OWLClass thing = reasoner.getOntology().getClass( new URI( OWLVocabularyAdapter.INSTANCE.getThing() ) );
	    dumpHierarchy( reasoner, pw, thing, 0 );
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    public static void dumpInstances( OWLIndividualReasoner reasoner,
				      PrintWriter pw ) throws OWLException {
	Set allOntologies = OntologyHelper.importClosure( reasoner.getOntology() );
	Set allClasses = new HashSet();
	/* Iterate over all the ontologies in the import closure,
	 * grabbing all the classes */
	for ( Iterator allIt = allOntologies.iterator();
	      allIt.hasNext(); ) {
	    OWLOntology ontologyToProcess = (OWLOntology) allIt.next();
	    logger.debug( "Ontology: " + ontologyToProcess.getURI() ) ;
	    allClasses.addAll( ontologyToProcess.getClasses() );
	}
	
	/* Now iterate over the classes, adding appropriate information. */
	// 	for ( Iterator it = reasoner.getOntology().getClasses().iterator();
	// 	      it.hasNext(); ) {
	for ( Iterator it = allClasses.iterator();
	      it.hasNext(); ) {
	    OWLClass clazz = (OWLClass) it.next();
	    Set individuals = reasoner.instancesOf( clazz );
	    pw.println( clazz.getURI() ) ;
	    for ( Iterator indIt = individuals.iterator();
		  indIt.hasNext(); ) {
		OWLIndividual ind = 
		    (OWLIndividual) indIt.next();
		pw.println( "... " + ind.getURI() );
	    }
	}
    }
    
    /** Dumps the class hierarchy as RDFS */
    public static void dumpHierarchyRDFS( OWLTaxonomyReasoner reasoner, 
					  PrintWriter pw ) throws OWLException {
	try {
	    pw.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
	    pw.println("<rdf:RDF xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
	    pw.println("    xmlns:owl=\"http://www.w3.org/2002/07/owl#\"");
	    pw.println("    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
	    pw.println("    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"");
	    pw.println("    xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\">");

	    for ( Iterator it = reasoner.getOntology().getClasses().iterator();
		  it.hasNext(); ) {
		OWLClass clazz = (OWLClass) it.next();
		Set subs = reasoner.subClassesOf( clazz );
		
		for (Iterator sit = subs.iterator(); sit.hasNext(); ) {
		    for (Iterator innerIt = ((Set) sit.next()).iterator();
			 innerIt.hasNext(); ) { 
			OWLClass cl = (OWLClass) innerIt.next();
			
			pw.println("<owl:Class rdf:about=\"" + cl.getURI() + "\">");
			pw.println("  <rdfs:subClassOf>");
			pw.println("    <owl:Class rdf:about=\"" + clazz.getURI() + 
				   "\"/>" );
			pw.println("  </rdfs:subClassOf>");
			pw.println("</owl:Class>");
		    }			
		}
	    }
	    pw.println("</rdf:RDF>");
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

    /** Returns a new ontology which contains the classes from the
     * original ontology along with any subclass
     * information. Equivalences will be represented here through two
     * superclass statements. There is some information loss in this
     * translation (obviously as definitions involving restrictions
     * are not produced) but the hierarchy is preserved....*/
    public static OWLOntology hierarchyAsNewOntology( OWLTaxonomyReasoner reasoner ) throws OWLException {
	OWLOntology ontology = reasoner.getOntology();
	URI uri = null;
	try {
	    uri = new URI( ontology.getURI().toString() + "-hierarchy");
	} catch (URISyntaxException ex) {
	    /* Shouldn't happen.... */
	    ex.printStackTrace();
	}
	OWLOntology newOntology = 
	    ontology.getOWLDataFactory().getOWLOntology( uri, uri );
	/* Ugly! This bit relies on knowledge about the
	 * implementation, i.e. that the OWLOntology is in fact a
	 * ChangeVisitor..... */
	ChangeVisitor visitor = (ChangeVisitor) newOntology;

	/* Produces hierarchy of *all* classes, including the imported ones. */

	OWLOntology theOntology = reasoner.getOntology();
	Set allOntologies = OntologyHelper.importClosure( theOntology );
	Set allClasses = new HashSet();
	/* Iterate over all the ontologies in the import closure,
	 * grabbing all the classes */
	for ( Iterator allIt = allOntologies.iterator();
	      allIt.hasNext(); ) {
	    OWLOntology ontologyToProcess = (OWLOntology) allIt.next();
	    logger.debug( "Ontology: " + ontologyToProcess.getURI() ) ;
	    allClasses.addAll( ontologyToProcess.getClasses() );
	}
	
	/* Now iterate over the classes, adding appropriate information. */
	// 	for ( Iterator it = reasoner.getOntology().getClasses().iterator();
	// 	      it.hasNext(); ) {
	for ( Iterator it = allClasses.iterator();
	      it.hasNext(); ) {
	    OWLClass clazz = (OWLClass) it.next();
	    logger.debug( "Adding Class: " + clazz.getURI() ) ;

	    /* For each class in the original ontology, make sure
	     * that it occurs in the new one. */
	    OntologyChange oc = new AddEntity( newOntology, clazz, null );
	    oc.accept( visitor ); 
	} 

	for ( Iterator it = allClasses.iterator();
	      it.hasNext(); ) {
	    OWLClass clazz = (OWLClass) it.next();
	    logger.debug( "Class: " + clazz.getURI() ) ;
	    
	    /* Now for each superclass of the class (according to
	     * the reasoner), add the subclass to the ontology and
	     * add the relationship. */
	    Set sups = reasoner.superClassesOf( clazz );
	    logger.debug( "Sups: " + sups.size() );
	    for (Iterator sit = sups.iterator(); sit.hasNext(); ) {
		Set ss = (Set) sit.next();
		for (Iterator innerIt = ss.iterator();
		     innerIt.hasNext(); ) { 
		    OWLClass cl = (OWLClass) innerIt.next();
		    logger.debug( "Adding super: " + cl.getURI() ) ;

		    // 		    oc = new AddEntity( newOntology, 
		    // 					cl, 
		    // 					null );
		    // 		    oc.accept( visitor );
		    OntologyChange oc = new AddSuperClass( newOntology, 
							   clazz, 
							   cl, 
							   null );
		    oc.accept( visitor );
		}			
	    }
	    
	    /* Now add two implications for equivalences. */
	    Set equivs = reasoner.equivalentClassesOf( clazz );
	    
	    for (Iterator sit = equivs.iterator(); sit.hasNext(); ) {
		for (Iterator innerIt = ((Set) sit.next()).iterator();
		     innerIt.hasNext(); ) { 
		    OWLClass cl = (OWLClass) innerIt.next();
		    if ( cl != clazz ) {
			OntologyChange oc = new AddEntity( newOntology, 
					    cl, 
					    null );
			oc.accept( visitor );
			oc = new AddSuperClass( newOntology, 
						clazz, 
						cl, 
						null );
			oc.accept( visitor );
			oc = new AddSuperClass( newOntology, 
						cl, 
						clazz, 
						null );
			oc.accept( visitor );
		    }
		}			
	    }
	}
	return newOntology;
    }
} 
