/*
 * Copyright (C) 2003, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.semanticweb.owl.model.helper; 
import java.util.Set;
import java.util.HashSet;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import java.util.Iterator;
import java.io.Serializable;
import java.net.URI;
import org.semanticweb.owl.model.OWLNamedObject;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLDataType;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.model.OWLClassAxiomVisitor;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

// Generated package name


/**
 * Helper functions for things like accessing the classes and
 * properties of an ontology.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OntologyHelper.java,v 1.9 2006/03/28 16:14:45 ronwalf Exp $
 */

public class OntologyHelper implements Serializable
{

    static Logger logger = Logger.getLogger(OntologyHelper.class);
    
    /** Returns the (unordered) closure of the imports from the given
    ontology. Thus if Ontology A imports B and B imports C, then the
    closure of A is {A, B, C}, while the closure of B is {B, C}. If A
    imports B and B imports A, the closure is {A,B} */ 

    public static Set importClosure( OWLOntology ontology ) throws OWLException {
	Set result = new HashSet();
	result.add( ontology );
	addImportsTo( ontology, result );
	return result;
    }
    
    private static void addImportsTo( OWLOntology ontology,
				      Set set ) throws OWLException {
	Set imported = ontology.getIncludedOntologies();
	for (Iterator it = imported.iterator();
	     it.hasNext();) {
	    OWLOntology importee = (OWLOntology) it.next();
	    if ( !set.contains( importee ) ) {
		set.add( importee );
		addImportsTo( importee, set );
	    }
	} 
    }

    public static OWLClass getClassNamed( OWLOntology ontology,
					  URI uri ) throws OWLException {
	return ontology.getClass( uri );
// 	for ( Iterator it = ontology.getClasses().iterator();
// 	      it.hasNext(); ) {
// 	    OWLClass clazz = (OWLClass) it.next();
// 	    if ( clazz.getURI().equals( uri ) ) {
// 		return clazz;
// 	    }
// 	}
// 	return null;
    }

    /** Get the class from the ontology or any of the ontologies in
     * the imports closure. */
    public static OWLClass getClassNamedFromImportsClosure( OWLOntology ontology,
							    URI uri ) throws OWLException {
	Set ontologies = importClosure( ontology );
 	for ( Iterator it = ontologies.iterator();
 	      it.hasNext(); ) {
	    OWLClass clazz = ((OWLOntology) it.next()).getClass( uri );
	    if ( clazz != null ){
		return clazz;
	    }
	}
	return null;
    }

    public static OWLIndividual getIndividualNamed( OWLOntology ontology,
					  URI uri ) throws OWLException {
	for ( Iterator it = ontology.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLIndividual ind = (OWLIndividual) it.next();
	    if ( !ind.isAnonymous() && ind.getURI().equals( uri ) ) {
		return ind;
	    }
	}
	return null;
    }

    public static OWLObjectProperty getObjectPropertyNamed( OWLOntology ontology,
					  URI uri ) throws OWLException {
	for ( Iterator it = ontology.getObjectProperties().iterator();
	      it.hasNext(); ) {
	    OWLObjectProperty prop = (OWLObjectProperty) it.next();
	    if ( prop.getURI().equals( uri ) ) {
		return prop;
	    }
	}
	return null;
    }

    public static OWLDataProperty getDataPropertyNamed( OWLOntology ontology,
					  URI uri ) throws OWLException {
	for ( Iterator it = ontology.getDataProperties().iterator();
	      it.hasNext(); ) {
	    OWLDataProperty prop = (OWLDataProperty) it.next();
	    if ( prop.getURI().equals( uri ) ) {
		return prop;
	    }
	}
	return null;
    }
    
    /** Returns all the uris of all the entities in the ontology. */
    public static Set allURIs( OWLOntology ontology ) throws OWLException {
	Set allURIs = new HashSet();
	for ( Iterator cit = ontology.getClasses().iterator();
	      cit.hasNext(); ) {
	    OWLNamedObject entity = (OWLNamedObject) cit.next();
	    allURIs.add(entity.getURI());
	}
	for ( Iterator cit = ontology.getIndividuals().iterator();
	      cit.hasNext(); ) {
	    OWLIndividual entity = (OWLIndividual) cit.next();
	    if ( !entity.isAnonymous() ) {
		allURIs.add(entity.getURI());
	    }
	}
	for ( Iterator cit = ontology.getObjectProperties().iterator();
	      cit.hasNext(); ) {
	    OWLNamedObject entity = (OWLNamedObject) cit.next();
	    allURIs.add(entity.getURI());
	}
	for ( Iterator cit = ontology.getDataProperties().iterator();
	      cit.hasNext(); ) {
	    OWLNamedObject entity = (OWLNamedObject) cit.next();
	    allURIs.add(entity.getURI());
	}
	for ( Iterator cit = ontology.getAnnotationProperties().iterator();
	   cit.hasNext(); ) {
	  OWLNamedObject entity = (OWLNamedObject) cit.next();
	  allURIs.add(entity.getURI());
	}
	for ( Iterator cit = ontology.getDatatypes().iterator();
	      cit.hasNext(); ) {
	    OWLDataType entity = (OWLDataType) cit.next();
	    allURIs.add(entity.getURI());
	}
	return allURIs;
    }

    /** Returns a collection of {@link OWLClassAxiom OWLClassAxiom}s that
    represent all the assertions made about classes in the
    ontology. This includes all the axioms that are in the ontology,
    along with additional axioms representing any of the class
    definitions that are in the ontology. Provides a "uniform" view of
    the assertions that can be useful for rendering, or in the case
    where a simple representation of the asserted information is
    required. */

    public static Set allDefinitionsAsAxioms( OWLOntology ontology ) throws OWLException {
	Set result = new HashSet();
	OWLDataFactory factory = ontology.getOWLDataFactory();
	result.addAll( ontology.getClassAxioms() );
	for ( Iterator it = ontology.getClasses().iterator();
	      it.hasNext(); ) {
	    OWLClass clazz = (OWLClass) it.next();
	    
	    for ( Iterator sit = clazz.getSuperClasses( ontology ).iterator();
		  sit.hasNext(); ) {
		OWLClassAxiom ca = 
		    factory.getOWLSubClassAxiom( clazz, 
						 (OWLDescription) sit.next() );
		result.add( ca );
	    }
	    /* Look for equivalences */
	    Set eqs = new HashSet();
	    eqs.add( clazz );
	    eqs.addAll( clazz.getEquivalentClasses( ontology ) );
	    /* Look for enumerations */
	    eqs.addAll( clazz.getEnumerations( ontology ) );
	    /* If there are any equivalences or enumerations, create
	     * an axiom to represent this */
	    if ( eqs.size() > 1 ) {
		OWLClassAxiom ca = 
		    factory.getOWLEquivalentClassesAxiom( eqs ); 
		result.add( ca );
	    }
	}
	return result;
    }

    /** Returns a collection of {@link OWLPropertyAxiom OWLPropertyAxiom}s that
    represent all the assertions made about classes in the
    ontology. This includes all the axioms that are in the ontology,
    along with additional axioms representing any of the class
    definitions that are in the ontology. Provides a "uniform" view of
    the assertions that can be useful for rendering, or in the case
    where a simple representation of the asserted information is
    required. */

    public static Set allPropertyDefinitionsAsAxioms( OWLOntology ontology ) throws OWLException {
	Set result = new HashSet();
	OWLDataFactory factory = ontology.getOWLDataFactory();
	result.addAll( ontology.getPropertyAxioms() );
	Set props = ontology.getObjectProperties();
	props.addAll( ontology.getDataProperties() );
	for ( Iterator it = props.iterator();
	      it.hasNext(); ) {
	    OWLProperty prop = (OWLProperty) it.next();
	    
	    for ( Iterator sit = prop.getSuperProperties( ontology ).iterator();
		  sit.hasNext(); ) {
		OWLPropertyAxiom ca = 
		    factory.getOWLSubPropertyAxiom( prop, 
						    (OWLProperty) sit.next() );
		result.add( ca );
	    }
	}
	return result;
    }

    /* Returns the collection of all the super properties that are
     * asserted in any of the given ontologies */
    public static Set allSuperProperties( OWLProperty prop, Set ontologies ) throws OWLException {
	Set result = new HashSet();
	addAllSupers( prop, result, ontologies );
	return result;
    }

    private static void addAllSupers( OWLProperty prop, Set collectedSupers, Set ontologies ) throws OWLException {
	if (collectedSupers.contains( prop ) ) {
	    return;
	}
	collectedSupers.add( prop );
	Set newSupers = prop.getSuperProperties( ontologies );
	for (Iterator it = newSupers.iterator();
	     it.hasNext(); ) {
	    OWLProperty superP = (OWLProperty) it.next();
	    addAllSupers( superP, collectedSupers, ontologies );
	}
    }


    /** Return the objects which are the fillers in the annotations
     * applied to the given object using the given annotation
     * property. */
    public static List getAnnotations( OWLOntology ontology,
				       OWLObject oo,
				       OWLAnnotationProperty oap ) throws OWLException {
	List result = new ArrayList();
	Set annos = oo.getAnnotations( ontology );
	for (Iterator annoIt = annos.iterator();
	     annoIt.hasNext(); ) {
	    
	    OWLAnnotationInstance annInst =
		(OWLAnnotationInstance) annoIt.next();
	    if ( annInst.getProperty().equals( oap ) && 
		 ( annInst.getContent() instanceof OWLDataValue )) {
		result.add( ((OWLDataValue) annInst.getContent()).getValue() );
	    }
	}
	return result;
    }

    /** Returns an ontology from the given URI. This will attempt to
     * use the default implementation (as provided by the
     * <code>org.semanticweb.owl.util.OWLConnection</code> system
     * property. This method is provided primarily for testing
     * purposes &mdash; for example it allows one to write
     * <code>main</code> methods that access ontologies without
     * introducing compile time dependencies on particular
     * implementations. <br/> This perhaps shouldn't be here, but it
     * is useful for the time being. */
    public static OWLOntology getOntology( URI uri ) throws OWLException {
	OWLConnection connection = null;
	connection = OWLManager.getOWLConnection();
	return connection.getOntologyPhysical( uri );
    }

    /**
     * Returns all annotations on the object using the rdfs:label
     * annotation property using the given language.
     * @param ontology an <code>Ontology</code> value
     * @param object an <code>OWLObject</code> value
     * @param language a <code>String</code> value. If null, returns
     * all labels.
     * @return a <code>Set</code> of Strings.
     */
    public static Set getRDFLabels( OWLOntology ontology,
					 OWLObject oo,
					 String language ) throws OWLException {
	/* Get the rdfs:label annotation property. */
	OWLAnnotationProperty rdfLabel = null;
	try {
	    rdfLabel = 
		ontology.getOWLDataFactory().getOWLAnnotationProperty( new URI(OWLVocabularyAdapter.INSTANCE.getLabel()));
	} catch (java.net.URISyntaxException ex) {
	    throw new OWLException( "Can't create rdfs:label " + ex.getMessage() );
	}
	/* Bit naughty -- should perhaps not use an explicit Set
	 * implementation */
	Set result = new HashSet();
	/* Get any annotations on the object */
	Set annos = oo.getAnnotations( ontology );
	for (Iterator annoIt = annos.iterator();
	     annoIt.hasNext(); ) {
	    
	    OWLAnnotationInstance annInst =
		(OWLAnnotationInstance) annoIt.next();
	    if ( annInst.getProperty().equals( rdfLabel ) ) {
		/* We've got an annotation using the right property */
		if ( annInst.getContent() instanceof OWLDataValue ) {
		    OWLDataValue odv = 
			(OWLDataValue) annInst.getContent();
		    /* The language of the literal. */
		    String odvLang = odv.getLang();
		    /* If the requested language is null, return the
		     * content. Otherwise, check to see if the
		     * language of the annotation matches the
		     * requested language. If it does, return it.*/
		    if ( language==null ||
			 (odvLang!=null && language.equals( odvLang ) ) ) {
			result.add( odv.getValue() );
		    }
		}
	    }
	}
	return result;
    }

    /**
     * Returns all annotations on the object using the rdfs:label
     * annotation property using any language.
     * @param ontology an <code>Ontology</code> value
     * @param object an <code>OWLObject</code> value
     * all labels.
     * @return a <code>Set</code> of Strings.
     */
    public static Set getRDFLabels( OWLOntology ontology,
					 OWLObject oo ) throws OWLException {
	return getRDFLabels( ontology, oo, null );
    }



    /** Returns all the asserted superclasses of the class in the
     * given ontology. This includes:
     * <ul>
     * <li>All superclasses associated with the class
     * <li>Any RHS of an axiom where the LHS is the class
     * </ul>
     * Note that this is <b>not</b> expected to do any reasoning. This is all simply asserted information. 
     */

    public static Set allAssertedSuperClasses( OWLOntology ontology,
					       final OWLClass cl ) throws OWLException {
	final Set result = new HashSet();
	/* First off, grab any superclasses */
	result.addAll( cl.getSuperClasses( ontology ) );
	/* Now grab any axioms that have this class as the LHS and add
	 * the RHS */

	/* This is done through the following visitor that examines
	 * axioms and does the appropriate thing. */
	OWLClassAxiomVisitor acav = 
	    new OWLClassAxiomVisitorAdapter() {
		public void visit( OWLSubClassAxiom ax ) throws OWLException {
		    if ( ax.getSubClass() == cl ) {
			result.add( ax.getSuperClass() );
		    }
		}
	    };
	    
	for (Iterator it = ontology.getClassAxioms().iterator();
	     it.hasNext(); ) {
	    OWLClassAxiom cax = (OWLClassAxiom) it.next();
	    cax.accept( acav );
	}
	/* We're done! */
	return result;
    }

    /** Returns all the asserted equivalent classes of the class in the
     * given ontology. This includes:
     * <ul>
     * <li>All equivalent classes associated with the class
     * <li>Any equivalent of an axiom where the class is one of the equivalents.
     * </ul>
     * Note that this is <b>not</b> expected to do any reasoning. This is all simply asserted information. 
     */

    public static Set allAssertedEquivalentClasses( OWLOntology ontology,
					       final OWLClass cl ) throws OWLException {
	final Set result = new HashSet();
	/* First off, grab any equivalent classes */
	result.addAll( cl.getEquivalentClasses( ontology ) );
	/* Now grab any axioms that involve this class. */

	/* This is done through the following visitor that examines
	 * axioms and does the appropriate thing. */
	OWLClassAxiomVisitor acav = 
	    new OWLClassAxiomVisitorAdapter() {
		public void visit( OWLEquivalentClassesAxiom ax ) throws OWLException {
		    boolean found = false;
		    /* Should probably really use a while for
		     * efficiency... */
		    for ( Iterator it = ax.getEquivalentClasses().iterator();
			  it.hasNext(); ) {
			if ( it.next() == cl ) {
			    found = true;
			}
		    }
		    if ( found ) {
			/* Add the equivalences */
			for ( Iterator it = ax.getEquivalentClasses().iterator();
			      it.hasNext(); ) {
			    Object eq = it.next();
			    if ( eq != cl ) {
				result.add( eq );
			    }
			}
		    }
		}
	    };
	
	for (Iterator it = ontology.getClassAxioms().iterator();
	     it.hasNext(); ) {
	    OWLClassAxiom cax = (OWLClassAxiom) it.next();
	    cax.accept( acav );
	}
	/* We're done! */
	return result;
    }
    
    /* Returns any instance of {@link
     * org.semanticweb.owl.model.OWLOntologyObject OWLOntologyObject}
     * in the Ontology that use the given entity in some way. 
     * <br/>
     * <strong>WARNING</strong>: This is still largely untested code..
     */
    
    public static Set entityUsage( OWLOntology ontology,
				   OWLEntity entity ) throws OWLException {
	
	/* This is horribly inefficien and should be done in a better
	 * way. It is general, in that it only uses the API classes,
	 * but there should be much better implementations. */

	logger.debug( "Looking for " + entity );
	/* A finder to do some finding */
	OWLEntityFinder finder = new OWLEntityFinder( entity );
	
	Set users = new HashSet();

	/* Gather everything up. */

	/* Stuff associated with classes */
	for (Iterator it = ontology.getClasses().iterator();
	     it.hasNext();) {
	    OWLClass clazz = 
		(OWLClass) it.next();
	    logger.debug( "Checking " + clazz );
	    Set thingsToLookIn = new HashSet();
	    /* Grab any superclasses */
	    thingsToLookIn.addAll( clazz.getSuperClasses( ontology ) );
	    /* Grab any equivalentclasses */
	    thingsToLookIn.addAll( clazz.getEquivalentClasses( ontology ) );
	    /* Grab any enumerations */
	    thingsToLookIn.addAll( clazz.getEnumerations( ontology ) );

	    /* Grab any annotations */
	    thingsToLookIn.addAll( clazz.getAnnotations( ontology ) );
	    /* Now set the finder off to look for these things. */
	    for (Iterator thingIt = thingsToLookIn.iterator();
		 thingIt.hasNext();) {
		finder.reset();
		OWLObject oo =
		    (OWLObject) thingIt.next();
		oo.accept( finder );
		if ( finder.found() ) {
		    logger.debug( "Found in " + clazz );
		    /* The class uses the entity */
		    users.add( clazz );
		}
	    }
	}

	/* Stuff associated with object properties */
	for (Iterator it = ontology.getObjectProperties().iterator();
	     it.hasNext();) {
	    OWLObjectProperty prop = 
		(OWLObjectProperty) it.next();
	    /* Grab any domains */
	    Set thingsToLookIn = new HashSet();

	    thingsToLookIn.addAll( prop.getDomains( ontology ) );
	    /* Grab any ranges */
	    thingsToLookIn.addAll( prop.getRanges( ontology ) );

	    /* Grab any annotations */
	    thingsToLookIn.addAll( prop.getAnnotations( ontology ) );
	    /* Now set the finder off to look for these things. */
	    for (Iterator thingIt = thingsToLookIn.iterator();
		 thingIt.hasNext();) {
		finder.reset();
		OWLObject oo =
		    (OWLObject) thingIt.next();
		oo.accept( finder );
		if ( finder.found() ) {
		    /* The property uses the entity */
		    users.add( prop );
		}
	    }
	    for (Iterator invIt = prop.getInverses( ontology ).iterator();
		 invIt.hasNext();) {
		/* Check if the property is the inverse */
		if ( invIt.next() == entity )  {
		    users.add( prop );
		}
	    }
	}

	/* Stuff associated with data properties */
	for (Iterator it = ontology.getDataProperties().iterator();
	     it.hasNext();) {
	    OWLDataProperty prop = 
		(OWLDataProperty) it.next();
	    Set thingsToLookIn = new HashSet();
	    /* Grab any domains */
	    thingsToLookIn.addAll( prop.getDomains( ontology ) );
	    /* Grab any annotations */
	    thingsToLookIn.addAll( prop.getAnnotations( ontology ) );

	    /* Now set the finder off to look for these things. */
	    for (Iterator thingIt = thingsToLookIn.iterator();
		 thingIt.hasNext();) {
		finder.reset();
		OWLObject oo =
		    (OWLObject) thingIt.next();
		oo.accept( finder );
		if ( finder.found() ) {
		    /* The property uses the entity */
		    users.add( prop );
		}
	    }
	}

	/* Stuff associated with individuals */
	for (Iterator it = ontology.getIndividuals().iterator();
	     it.hasNext();) {
	    OWLIndividual ind = 
		(OWLIndividual) it.next();
	    logger.debug( "Checking " + ind );
	    Set thingsToLookIn = new HashSet();
	    /* Grab any superclasses */
	    thingsToLookIn.addAll( ind.getTypes( ontology ) );

	    /* Grab any annotations */
	    thingsToLookIn.addAll( ind.getAnnotations( ontology ) );

	    Map map = ind.getObjectPropertyValues( ontology );
	    /* Add all the properties and property values */
	    for (Iterator mit = map.keySet().iterator();
		 mit.hasNext(); ) {
		Object k = mit.next();
		thingsToLookIn.add( k );
		thingsToLookIn.addAll( (Set) map.get( k ) );
	    }
	    /* Add all object properties used */
	    thingsToLookIn.addAll( ind.getDataPropertyValues( ontology ).keySet() );
	    /* Now set the finder off to look for these things. */
	    for (Iterator thingIt = thingsToLookIn.iterator();
		 thingIt.hasNext();) {
		finder.reset();
		OWLObject oo =
		    (OWLObject) thingIt.next();
		oo.accept( finder );
		if ( finder.found() ) {
		    logger.debug( "Found in " + ind );
		    /* The class uses the entity */
		    users.add( ind );
		}
	    }
	}

	/* Class Axioms */
	for (Iterator it = ontology.getClassAxioms().iterator();
	     it.hasNext();) {
	    OWLClassAxiom axiom = (OWLClassAxiom) it.next();
	    finder.reset();
	    axiom.accept( finder );
	    if ( finder.found() ) {
		users.add( axiom );
	    }
	}

	/* Property Axioms */
	for (Iterator it = ontology.getPropertyAxioms().iterator();
	     it.hasNext();) {
	    OWLPropertyAxiom axiom = (OWLPropertyAxiom) it.next();
	    finder.reset();
	    axiom.accept( finder );
	    if ( finder.found() ) {
		users.add( axiom );
	    }
	}
	
	/* !!!! TODO: Annotation Properties?? !!!!*/
	
	return users;
    }

    public static Set objectsUsed( OWLOntology ontology,
	OWLEntity entity ) throws OWLException {
	OWLObjectsUsedCollector oouc =
	    new OWLObjectsUsedCollector( ontology );
	entity.accept( oouc );
	return oouc.entities();
    }

    public static void main( String[] args ) {
	BasicConfigurator.configure();

	try {
	    OWLOntology onto = 
		OntologyHelper.getOntology( new URI (args[0]) );
	    if ( onto!=null ) {
		System.out.println( onto.getClasses().size() + "\tClasses" );
		System.out.println( onto.getObjectProperties().size() + "\tObject Properties" );
		System.out.println( onto.getDataProperties().size() + "\tDatatype Properties" );
		System.out.println( onto.getIndividuals().size() + "\tIndividuals" );
	    }
	    for (Iterator it = onto.getClasses().iterator();
		 it.hasNext();) {
		OWLClass clazz = (OWLClass) it.next();
	        System.out.println( clazz.getURI() );
		System.out.println( "Usage:");
		for ( Iterator usageIt = OntologyHelper.entityUsage( onto, clazz ).iterator();
		      usageIt.hasNext(); ) {
		    System.out.println( "\t" + usageIt.next() );
		}
		System.out.println( "Uses:");
		for ( Iterator usageIt = OntologyHelper.objectsUsed( onto, clazz ).iterator();
		      usageIt.hasNext(); ) {
		    System.out.println( "\t" + usageIt.next() );
		}
	    }
	} catch ( Exception ex ) {
	    ex.printStackTrace();
	}
	
    }
} // OntologyHelper



/*
 * ChangeLog
 * $Log: OntologyHelper.java,v $
 * Revision 1.9  2006/03/28 16:14:45  ronwalf
 * Merging mindswap changes to OWLApi.
 * Rough summary:
 * * 1.5 compatibility (rename enum variables)
 * * An option to turn on and off importing in OWLConsumer
 * * Bug fix to allow DataRange in more areas
 * * Giving Anonymous individuals an identifier
 *   * New factory method - getAnonOWLIndividual
 *   * getOWLIndividual no longer accepts 'null'
 *   * added getAnonId() and isAnon() to OWLIndividual
 * * Some work on the RDF serializer, but we have a complete rewrite in
 *   Swoop that I think is better (more flexible, results easier to read)
 * * Added Transitive, Functional, InverseFunctional, Inverse, and
 *   Symmetric PropertyAxioms (not sure why, will check)
 * * Added .equals and .hashcode for all OWLObjects
 * * Added a RemoveDataType change
 * * Patches to OntologyImpl for Entity removal
 * * Added OWLIndividualTypeAssertion
 * * Added OWL(Object|Data)Property(Domain|Range)Axiom
 * * Added OWL(Object|Data)PropertyInstance
 * * Added subclass index to OWLClassImpl (and getSubClasses(...) for
 *   OWLClass)
 * * Changes for Entity renaming
 *
 * Revision 1.8  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.7  2004/10/19 17:14:12  sean_bechhofer
 * Added method to get class from imports closure
 *
 * Revision 1.6  2004/07/09 14:04:58  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
 *
 * Revision 1.5  2004/07/09 12:07:47  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.4  2004/03/08 17:27:00  sean_bechhofer
 * Removing references to deprecated methods.
 *
 * Revision 1.3  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.2  2003/11/20 12:58:08  sean_bechhofer
 * Addition of language handling in OWLDataValues.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.9  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.8  2003/09/22 13:21:44  bechhofers
 * Fixes to rendering and some extra helper functions.
 *
 * Revision 1.7  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.6  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.5  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.4  2003/03/27 19:48:56  seanb
 * Helper that gathers axioms.
 *
 * Revision 1.3  2003/02/21 16:12:20  seanb
 * Updates to parser.
 *
 * Revision 1.2  2003/02/03 16:00:29  rvolz
 * minimal changes to OWLOntology
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
