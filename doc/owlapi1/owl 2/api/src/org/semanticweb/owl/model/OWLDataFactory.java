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

package org.semanticweb.owl.model; // Generated package name

import java.util.Set;
import java.io.Serializable;
import java.net.URI;

import org.semanticweb.owl.util.OWLConnection;

/**
 * OWLDataFactory provides a single access point for the creation
 * of objects within an ontology. Use of the data factory insulates
 * clients from the particular implementation that is being used. 
 * Each {@link OWLObject OWLObject} maintains a link to 
 * the OWLDataFactory that was used to create it. 


 * Note that requesting a
 * {class | individual | objectproperty | dataproperty } with the same uri as
 * an existing { class | individual | objectproperty | dataproperty } will
 * return a reference to the existing object.
 * <br/>
 *
 * @author Sean Bechhofer
 * @version $Id: OWLDataFactory.java,v 1.4 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public interface OWLDataFactory extends Serializable
{
    public OWLAnd getOWLAnd( Set set ) throws OWLException;

    public OWLAnnotationProperty getOWLAnnotationProperty(URI uri) throws OWLException;

    public OWLAnnotationInstance getOWLAnnotationInstance( OWLObject subj,
							   OWLAnnotationProperty property,
							   Object content ) throws OWLException;

    public OWLDataValue getOWLConcreteData( URI uri, String lang, Object value ) throws OWLException;

    public OWLDataType getOWLConcreteDataType( URI uri) throws OWLException;

    public OWLDataEnumeration getOWLDataEnumeration( Set values ) throws OWLException;

    public OWLDataAllRestriction 
	getOWLDataAllRestriction( OWLDataProperty prop,
				  OWLDataRange type ) throws OWLException;

    public OWLDataCardinalityRestriction 
	getOWLDataCardinalityRestriction( OWLDataProperty prop,
					  int atLeast,
					  int atMost ) throws OWLException;
    public OWLDataCardinalityRestriction 
	getOWLDataCardinalityAtLeastRestriction( OWLDataProperty prop,
					  int atLeast ) throws OWLException;
    public OWLDataCardinalityRestriction 
	getOWLDataCardinalityAtMostRestriction( OWLDataProperty prop,
					  int atMost ) throws OWLException;

    public OWLDataProperty getOWLDataProperty(URI uri) throws OWLException;

    public OWLDataSomeRestriction 
	getOWLDataSomeRestriction( OWLDataProperty prop,
				   OWLDataRange type ) throws OWLException;

    public OWLDataValueRestriction 
	getOWLDataValueRestriction( OWLDataProperty prop,
				   OWLDataValue value ) throws OWLException;

    public OWLDifferentIndividualsAxiom getOWLDifferentIndividualsAxiom( Set set ) throws OWLException;

    public OWLDisjointClassesAxiom getOWLDisjointClassesAxiom( Set set ) throws OWLException;

    public OWLEquivalentClassesAxiom getOWLEquivalentClassesAxiom( Set set ) throws OWLException;

    public OWLEquivalentPropertiesAxiom getOWLEquivalentPropertiesAxiom( Set set ) throws OWLException;

    public OWLFrame getOWLFrame( Set superClasses,
				 Set restrictions ) throws OWLException;

    public OWLIndividual getOWLIndividual( URI uri ) throws OWLException;

    // ANON: create an anoymous individual using the genid URI
    public OWLIndividual getAnonOWLIndividual( URI uri ) throws OWLException;
    // ===
    
    /*    public OWLAnonymousIndividual getOWLAnonymousIndividual() throws OWLException; */

    public OWLObjectAllRestriction 
	getOWLObjectAllRestriction( OWLObjectProperty prop,
					OWLDescription filler ) throws OWLException;

    public OWLObjectCardinalityRestriction 
	getOWLObjectCardinalityRestriction( OWLObjectProperty prop,
						int atLeast,
						int atMost ) throws OWLException;
    public OWLObjectCardinalityRestriction 
	getOWLObjectCardinalityAtLeastRestriction( OWLObjectProperty prop,
						int atLeast) throws OWLException;
    public OWLObjectCardinalityRestriction 
	getOWLObjectCardinalityAtMostRestriction( OWLObjectProperty prop,
						int atMost) throws OWLException;
    
    public OWLObjectProperty getOWLObjectProperty( URI uri ) throws OWLException;
    
    public OWLObjectSomeRestriction 
	getOWLObjectSomeRestriction( OWLObjectProperty prop,
					 OWLDescription filler ) throws OWLException;

    public OWLObjectValueRestriction 
	getOWLObjectValueRestriction( OWLObjectProperty prop,
					  OWLIndividual filler ) throws OWLException;

    public OWLNot getOWLNot( OWLDescription operand ) throws OWLException;

    public OWLClass getOWLNothing() throws OWLException;

    public OWLConnection getOWLConnection() throws OWLException;
    public OWLOr getOWLOr( Set set ) throws OWLException;

    public OWLClass getOWLClass( URI uri ) throws OWLException;

    public OWLEnumeration getOWLEnumeration( Set individuals ) throws OWLException;

    public OWLSameIndividualsAxiom getOWLSameIndividualsAxiom( Set set ) throws OWLException;

    public OWLSubClassAxiom 
	getOWLSubClassAxiom( OWLDescription subClass,
			     OWLDescription superClass ) throws OWLException;

    public OWLSubPropertyAxiom 
	getOWLSubPropertyAxiom( OWLProperty subProperty,
				OWLProperty superProperty ) throws OWLException;

    public OWLClass getOWLThing() throws OWLException;

	/**
	 * Method getOWLOntology.
	 * @param logicalURIObject
	 * @param physicalURIObject
	 * @return OWLOntology
	 */
    
    public OWLObjectPropertyInstance 
	getOWLObjectPropertyInstance( OWLIndividual subject,
					  OWLObjectProperty property,
					  OWLIndividual object ) throws OWLException;

    public OWLDataPropertyInstance 
	getOWLDataPropertyInstance( OWLIndividual subject,
					  OWLDataProperty property,
					  OWLDataValue object ) throws OWLException;

    public OWLDataPropertyRangeAxiom getOWLDataPropertyRangeAxiom( OWLDataProperty property, OWLDataRange range) throws OWLException;
    
    public OWLFunctionalPropertyAxiom getOWLFunctionalPropertyAxiom( OWLProperty property) throws OWLException;
    
    public OWLInverseFunctionalPropertyAxiom getOWLInverseFunctionalPropertyAxiom( OWLObjectProperty property) throws OWLException;
    
    public OWLInversePropertyAxiom getOWLInversePropertyAxiom( OWLObjectProperty property, OWLObjectProperty inverse) throws OWLException;

    public OWLObjectPropertyRangeAxiom getOWLObjectPropertyRangeAxiom( OWLObjectProperty property, OWLDescription range) throws OWLException;
    
    public OWLPropertyDomainAxiom getOWLPropertyDomainAxiom( OWLProperty property, OWLDescription domain) throws OWLException;
    
    public OWLSymmetricPropertyAxiom getOWLSymmetricPropertyAxiom( OWLObjectProperty property) throws OWLException;

    public OWLTransitivePropertyAxiom getOWLTransitivePropertyAxiom( OWLObjectProperty property) throws OWLException;


    public OWLOntology getOWLOntology(URI logicalURIObject, 
				   URI physicalURIObject) throws OWLException;

}// OWLDataFactory


/*
 * ChangeLog
 * $Log: OWLDataFactory.java,v $
 * Revision 1.4  2006/03/28 16:14:45  ronwalf
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
 * Revision 1.3  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2003/11/20 12:58:07  sean_bechhofer
 * Addition of language handling in OWLDataValues.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.11  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.10  2003/05/16 11:34:48  seanb
 * Further renaming of individual to object in the implementations
 * of restrictions.
 *
 * Revision 1.9  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.8  2003/04/10 12:08:50  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.7  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.6  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.5  2003/02/10 09:23:06  seanb
 * Changes to cardinality, addition of property instances.
 *
 * Revision 1.4  2003/02/06 10:27:33  seanb
 * Replacing OWLThing and OWLNothing with specific instances of OWLClass.
 *
 * Revision 1.3  2003/02/05 14:29:37  rvolz
 * Parser Stuff, Connection
 *
 * Revision 1.2  2003/01/29 16:10:51  seanb
 * Changes to support Anonymous Individuals.
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 * 
 */

