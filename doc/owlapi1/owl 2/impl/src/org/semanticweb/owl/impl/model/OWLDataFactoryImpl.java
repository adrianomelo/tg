/*
 * Copyright (C) 2005, University of Manchester
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

package org.semanticweb.owl.impl.model; // Generated package name

import java.util.Set;
import java.io.Serializable;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentPropertiesAxiom;
import org.semanticweb.owl.model.OWLFrame;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;
//import org.semanticweb.owl.model.OWLAnonymousIndividual;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLNot;
//import org.semanticweb.owl.model.OWLNothing;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLDataPropertyInstance;
//import org.semanticweb.owl.model.OWLThing;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;

import java.net.URI;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLProperty;
import java.util.Map;
import java.net.URISyntaxException;
import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLDataEnumeration;
import org.semanticweb.owl.model.OWLDataRange;

/**
 * Provides a single access point for the creation
 * of objects within an ontology. 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLDataFactoryImpl.java,v 1.5 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public class OWLDataFactoryImpl implements OWLDataFactory, Serializable {

    static Logger logger = Logger.getLogger(OWLDataFactoryImpl.class);

    /* Collections to keep track of entities that have been created. */
    private Map classes;
    private Map objectProperties;
    private Map annotationProperties;
    private Map dataProperties;
    private Map individuals;
    /* Map keyed on logical URIs */
    private Map ontologies;
    private Map concreteDataTypes;
    
    private OWLConnection con;

    /* Instance variables for thing/nothing */
    private OWLClass thing;
    private OWLClass nothing;

    /**
     * Constructor should only be available internally. 
     * This should be a singleton.
     * [REVIEW singleton only wrt. connection]
     */
    private OWLDataFactoryImpl(OWLConnection con) {
	this.con = con;
	this.classes = ListFactory.getMap();
	this.objectProperties = ListFactory.getMap();
	this.annotationProperties = ListFactory.getMap();
	this.dataProperties = ListFactory.getMap();
	this.individuals = ListFactory.getMap();
	this.concreteDataTypes = ListFactory.getMap();
	this.ontologies = ListFactory.getMap();
	try {
	    URI uri = new URI( OWLVocabularyAdapter.INSTANCE.getThing() );
	    this.thing = getOWLClass( uri );
	    
	    uri = new URI( OWLVocabularyAdapter.INSTANCE.getNothing() );
	    this.nothing = getOWLClass( uri );
	} catch (URISyntaxException ex) {
	    /* Better do something here! Can we throw an exception
	     * from the data factory creation????*/
	}
    }

    public static OWLDataFactoryImpl getInstance(OWLConnection con) {
	return new OWLDataFactoryImpl(con);
    }

    public OWLAnd getOWLAnd(Set set) {
	return new OWLAndImpl(this, set);
    }

    public OWLDataValue getOWLConcreteData( URI uri, String lang, Object value ) {
	return new OWLConcreteDataImpl(this, uri, lang, value );
    }

    public OWLDataType getOWLConcreteDataType( URI uri ) {
	if (concreteDataTypes.get(uri) == null) {
	    OWLDataType newType = new OWLConcreteDataTypeImpl(this, uri);
	    concreteDataTypes.put(uri, newType);
	    return newType;
	} else {
	    return (OWLDataType) concreteDataTypes.get(uri);
	}
    }

    public OWLDataEnumeration getOWLDataEnumeration(Set set) {
	return new OWLDataEnumerationImpl(this, set);
    }


    public OWLDataAllRestriction getOWLDataAllRestriction(
							  OWLDataProperty property,
							  OWLDataRange dataType) {
	return new OWLDataAllRestrictionImpl(this, property, dataType);
    }

    public OWLDataCardinalityRestriction 
	getOWLDataCardinalityRestriction(
					 OWLDataProperty property,
					 int atLeast,
					 int atMost) {
	return new OWLDataCardinalityRestrictionImpl(
						     this,
						     property,
						     atLeast,
						     atMost);
    }
    public OWLDataCardinalityRestriction 
	getOWLDataCardinalityAtLeastRestriction(
					 OWLDataProperty property,
					 int atLeast ) {
	return new OWLDataCardinalityRestrictionImpl(
						     this,
						     property,
						     atLeast,
						     OWLDataCardinalityRestrictionImpl.UNSET);
    }
    public OWLDataCardinalityRestriction 
	getOWLDataCardinalityAtMostRestriction(
					 OWLDataProperty property,
					 int atMost) {
	return new OWLDataCardinalityRestrictionImpl(
						     this,
						     property,
						     OWLDataCardinalityRestrictionImpl.UNSET,
						     atMost);
    }
    
    public OWLDataProperty getOWLDataProperty(URI uri) {
// 	if (uri.toString().startsWith("http://www.w3.org")) {
// 	    Thread.currentThread().dumpStack();
// 	}
	if (dataProperties.get(uri) == null) {
	    OWLDataProperty newDataProperty =
		new OWLDataPropertyImpl(this, uri);
	    dataProperties.put(uri, newDataProperty);
	    return newDataProperty;
	} else {
	    return (OWLDataProperty) dataProperties.get(uri);
	}
    }

    public OWLDataSomeRestriction getOWLDataSomeRestriction(
							    OWLDataProperty property,
							    OWLDataRange dataType) {
	return new OWLDataSomeRestrictionImpl(this, property, dataType);
    }

    public OWLDataValueRestriction getOWLDataValueRestriction(
							      OWLDataProperty property,
							      OWLDataValue value) {
	return new OWLDataValueRestrictionImpl(this, property, value);
    }

    public OWLDifferentIndividualsAxiom getOWLDifferentIndividualsAxiom(Set set) {
	return new OWLDifferentIndividualsAxiomImpl(this, set);
    }

    public OWLDisjointClassesAxiom getOWLDisjointClassesAxiom(Set set) {
	return new OWLDisjointClassesAxiomImpl(this, set);
    }

    public OWLEquivalentClassesAxiom getOWLEquivalentClassesAxiom(Set set) {
	return new OWLEquivalentClassesAxiomImpl(this, set);
    }

    public OWLEquivalentPropertiesAxiom getOWLEquivalentPropertiesAxiom(Set set) {
	return new OWLEquivalentPropertiesAxiomImpl(this, set);
    }

    public OWLFrame getOWLFrame(Set superClasses, Set restrictions) {
	return new OWLFrameImpl(this, superClasses, restrictions);
    }

    public OWLIndividual getOWLIndividual(URI uri) {
	if (uri==null) throw new NullPointerException();
	if (individuals.get(uri) == null) {
	    OWLIndividual newIndividual = new OWLIndividualImpl(this, uri);
	    individuals.put(uri, newIndividual);
	    return newIndividual;
	} else {
	    return (OWLIndividual) individuals.get(uri);
	}
    }
    
    // ANON: create anon individual
    public OWLIndividual getAnonOWLIndividual(URI uri) {
    if (uri==null) throw new NullPointerException();
	if (individuals.get(uri) == null) {
	    OWLIndividual newIndividual = new OWLIndividualImpl(this, uri, true);
	    individuals.put(uri, newIndividual);
	    return newIndividual;
	} else {
	    return (OWLIndividual) individuals.get(uri);
	}
    }
    // ===
    
//     public OWLAnonymousIndividual getOWLAnonymousIndividual() {
// 	return new OWLAnonymousIndividualImpl(this);
//     }

    public OWLObjectAllRestriction getOWLObjectAllRestriction(
								      OWLObjectProperty property,
								      OWLDescription description) {
	return new OWLObjectAllRestrictionImpl(this, property, description);
    }

    public OWLObjectCardinalityRestriction 
	getOWLObjectCardinalityRestriction(
					       OWLObjectProperty property,
					       int atLeast,
					       int atMost) {
	return new OWLObjectCardinalityRestrictionImpl(
							   this,
							   property,
							   atLeast,
							   atMost);
    }
    public OWLObjectCardinalityRestriction 
	getOWLObjectCardinalityAtLeastRestriction(
						      OWLObjectProperty property,
						      int atLeast ) {
	return new OWLObjectCardinalityRestrictionImpl(
							   this,
							   property,
							   atLeast,
							   OWLObjectCardinalityRestrictionImpl.UNSET);
    }
    public OWLObjectCardinalityRestriction 
	getOWLObjectCardinalityAtMostRestriction(
						     OWLObjectProperty property,
						     int atMost) {
	return new OWLObjectCardinalityRestrictionImpl(
							   this,
							   property,
							   OWLObjectCardinalityRestrictionImpl.UNSET,
							   atMost);
    }
    
    public OWLAnnotationProperty getOWLAnnotationProperty(URI uri) {
	if (annotationProperties.get(uri) == null) {
	    OWLAnnotationProperty newAnnotationProperty =
		new OWLAnnotationPropertyImpl(this, uri);
	    annotationProperties.put(uri, newAnnotationProperty);
	    return newAnnotationProperty;
	} else {
	    return (OWLAnnotationProperty) annotationProperties.get(uri);
	}
    }

    public OWLObjectProperty getOWLObjectProperty(URI uri) {
	if (objectProperties.get(uri) == null) {
	    OWLObjectProperty newObjectProperty =
		new OWLObjectPropertyImpl(this, uri);
	    objectProperties.put(uri, newObjectProperty);
	    return newObjectProperty;
	} else {
	    return (OWLObjectProperty) objectProperties.get(uri);
	}
    }

    public OWLObjectSomeRestriction getOWLObjectSomeRestriction(
									OWLObjectProperty property,
									OWLDescription description) {
	return new OWLObjectSomeRestrictionImpl(
						    this,
						    property,
						    description);
    }

    public OWLObjectValueRestriction getOWLObjectValueRestriction(
									  OWLObjectProperty property,
									  OWLIndividual individual) {
	return new OWLObjectValueRestrictionImpl(
						     this,
						     property,
						     individual);
    }

    public OWLNot getOWLNot(OWLDescription operand) {
	return new OWLNotImpl(this, operand);
    }

    public OWLClass getOWLNothing() {
	return nothing;
    }

    public OWLOntology getOWLOntology(URI logicalURI, URI physicalURI) throws OWLException {
	if ( ontologies.get(logicalURI) == null ) {
	    OWLOntology newOntology = 
		new OWLOntologyImpl( this, logicalURI, physicalURI );
	    /* What's going on here? This should be ontologies!!! */
	    //	    classes.put(logicalURI, newOntology);
	    ontologies.put(logicalURI, newOntology);
	    return newOntology;
	} else {
	    /* If we've seen the logicalURI before, check that the
	     * physical one matches up. */
	    OWLOntology existing = (OWLOntology) ontologies.get(logicalURI);
	    if (!existing.getPhysicalURI().equals( physicalURI )) {
		throw new OWLException("Ontology with logical URI: \n\t" +
				       logicalURI + 
				       "\nAlready present using physical URI\n\t" +
				       existing.getPhysicalURI());
	    }
	    return existing;
	}
    }

    public OWLOr getOWLOr(Set set) {
	return new OWLOrImpl(this, set);
    }

    public OWLClass getOWLClass(URI uri) {
	logger.debug( "Creating: " + uri );
	if (logger.isDebugEnabled() && 
	    uri == null) {
	    Thread.currentThread().dumpStack();
	}

	if (classes.get(uri) == null) {
	    OWLClass newClass = new OWLClassImpl(this, uri);
	    classes.put(uri, newClass);
	    return newClass;
	} else {
	    return (OWLClass) classes.get(uri);
	}
    }

    public OWLEnumeration getOWLEnumeration(Set set) {
	return new OWLEnumerationImpl(this, set);
    }

    public OWLSameIndividualsAxiom getOWLSameIndividualsAxiom(Set set) {
	return new OWLSameIndividualsAxiomImpl(this, set);
    }

    public OWLSubClassAxiom getOWLSubClassAxiom(
						OWLDescription subClass,
						OWLDescription superClass) {
	return new OWLSubClassAxiomImpl(this, subClass, superClass);
    }

    public OWLSubPropertyAxiom getOWLSubPropertyAxiom(
						      OWLProperty subProperty,
						      OWLProperty superProperty) {
	return new OWLSubPropertyAxiomImpl(this, subProperty, superProperty);
    }

    public OWLClass getOWLThing() {
	return thing;
    }


    public OWLObjectPropertyInstance 
	getOWLObjectPropertyInstance( OWLIndividual subject,
					  OWLObjectProperty property,
					  OWLIndividual object ) throws OWLException {
	return null;
    }

    public OWLAnnotationInstance 
	getOWLAnnotationInstance( OWLObject subject,
				  OWLAnnotationProperty property,
				  Object content ) throws OWLException {
	return new OWLAnnotationInstanceImpl(this, subject, property, content);
    }

    public OWLDataPropertyInstance 
	getOWLDataPropertyInstance( OWLIndividual subject,
					  OWLDataProperty property,
					  OWLDataValue object ) throws OWLException {
	return null;
    }

    public OWLConnection getOWLConnection() {
	return con;
    }

    /* This is poor. We're having problems with ontologies lying
     * around and not being thrown away. As a result when we try and
     * reopen things, stuff goes wrong. This is a stop gap -- we
     * really need to refactor all the connection/manager/datafactory
     * stuff. */
    public void forgetOWLOntology( OWLOntology o ) throws OWLException {
	ontologies.remove( o.getURI() );
    }

    public OWLDataPropertyRangeAxiom getOWLDataPropertyRangeAxiom(OWLDataProperty property, OWLDataRange range) throws OWLException {
	return new OWLDataPropertyRangeAxiomImpl(this, property, range);
    }

    public OWLFunctionalPropertyAxiom getOWLFunctionalPropertyAxiom(OWLProperty property) throws OWLException {
	return new OWLFunctionalPropertyAxiomImpl(this, property);
    }

    public OWLInverseFunctionalPropertyAxiom getOWLInverseFunctionalPropertyAxiom(OWLObjectProperty property) throws OWLException {
	return new OWLInverseFunctionalPropertyAxiomImpl(this, property);
    }

    public OWLInversePropertyAxiom getOWLInversePropertyAxiom(OWLObjectProperty property, OWLObjectProperty inverse) throws OWLException {
	return new OWLInversePropertyAxiomImpl(this, property, inverse);
    }

    public OWLObjectPropertyRangeAxiom getOWLObjectPropertyRangeAxiom(OWLObjectProperty property, OWLDescription range) throws OWLException {
	return new OWLObjectPropertyRangeAxiomImpl(this, property, range);
    }

    public OWLPropertyDomainAxiom getOWLPropertyDomainAxiom(OWLProperty property, OWLDescription domain) throws OWLException {
	return new OWLPropertyDomainAxiomImpl(this, property, domain);
    }

    public OWLSymmetricPropertyAxiom getOWLSymmetricPropertyAxiom(OWLObjectProperty property) throws OWLException {
	return new OWLSymmetricPropertyAxiomImpl(this, property);
    }

    public OWLTransitivePropertyAxiom getOWLTransitivePropertyAxiom(OWLObjectProperty property) throws OWLException {
	return new OWLTransitivePropertyAxiomImpl(this, property);
    }

} // OWLDataFactory

/*
 * ChangeLog
 * $Log: OWLDataFactoryImpl.java,v $
 * Revision 1.5  2006/03/28 16:14:45  ronwalf
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
 * Revision 1.4  2005/06/10 12:20:31  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/11/03 17:36:42  sean_bechhofer
 * Minor addition to connection to support dropping/deletion.
 *
 * Revision 1.2  2003/11/20 12:58:10  sean_bechhofer
 * Addition of language handling in OWLDataValues.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.14  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.13  2003/05/16 11:34:48  seanb
 * Further renaming of individual to object in the implementations
 * of restrictions.
 *
 * Revision 1.12  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.11  2003/04/10 12:13:06  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.10  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.9  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 * Revision 1.8  2003/02/12 16:14:14  seanb
 * no message
 *
 * Revision 1.7  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.6  2003/02/07 18:43:11  seanb
 * New change events
 *
 * Revision 1.5  2003/02/06 11:14:51  seanb
 * Moved Vocabulary adapters into owl.io.vocabulary and renamed adaptor
 * to adapter.
 *
 * Revision 1.4  2003/02/06 10:27:33  seanb
 * Replacing OWLThing and OWLNothing with specific instances of OWLClass.
 *
 * Revision 1.3  2003/02/05 14:29:37  rvolz
 * Parser Stuff, Connection
 *
 * Revision 1.2  2003/01/29 16:10:52  seanb
 * Changes to support Anonymous Individuals.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */
