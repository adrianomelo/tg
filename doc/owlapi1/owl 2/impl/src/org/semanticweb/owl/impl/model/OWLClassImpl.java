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

package org.semanticweb.owl.impl.model; // Generated package name

import java.util.Set;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLFrame;
import org.semanticweb.owl.model.OWLOntology;
import java.net.URI;
import org.semanticweb.owl.model.OWLException;
import java.util.Map;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLDescriptionVisitor;

/**
 * An OWL Class represents a named class within an ontology. If
 * "anonymous" classes are used within descriptions, then the
 * appropriate description should be used. A Class may have a number
 * of definitions associated with it. 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLClassImpl.java,v 1.4 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public class OWLClassImpl extends OWLEntityImpl implements OWLClass, OWLDeprecatableObjectImpl 
{
    /* The following maps map ontologies to the assertions that they
     * make about the particular class. In this way, every assertion
     * is made in a particular context. */
    private Map superClasses;
    private Map subClasses;
    private Map equivalentClasses;
    private Map enumerations;
    private Set deprecated;

    public OWLClassImpl( OWLDataFactoryImpl factory, URI uri) {
	super( factory );
	this.uri = uri;
	this.superClasses = ListFactory.getMap();
	this.equivalentClasses = ListFactory.getMap();
	this.enumerations = ListFactory.getMap();
	this.deprecated = ListFactory.getSet();
	this.subClasses = ListFactory.getMap();
    }

    
    /** Returns the explicit superclasses of this class in the given
     * ontology. 
     * Returns a collection of {@link OWLFrame OWLFrame}s. Each frame
     * in this list provides necessary conditions for class membership. */
    public Set getSuperClasses( OWLOntology o ) {
	return ListFactory.getSet( OWLImplHelper.getAppropriateSet( superClasses, o ) );
    }

    /** Returns the explicit superclasses of this class in the given
     * ontologies. 
     * Returns a collection of {@link OWLFrame OWLFrame}s. Each frame
     * in this list provides necessary conditions for class membership. */
    public Set getSuperClasses( Set ontos ) {
	return ListFactory.getSet( OWLImplHelper.selectValues( superClasses, ontos ) );
    }

    /** Returns the explicit superclasses of this class. Returns a
     * collection of {@link OWLFrame OWLFrame}s. Each frame in this list
     * provides necessary conditions for class membership. */
    public Set getSuperClasses() {
	return ListFactory.getSet( OWLImplHelper.unionValues( superClasses ) );
    }

    /** Add an explicit superclass. */
    boolean addSuperClass( OWLOntology ontology, 
			   OWLDescription description ) {    	
	return OWLImplHelper.getAppropriateSet( superClasses, ontology ).add( description ); 
    }

    /** Add an explicit subclass. */
    boolean addSubClass( OWLOntology ontology, 
			   OWLDescription description ) {    	
	return OWLImplHelper.getAppropriateSet( subClasses, ontology ).add( description ); 
    }
    
    /** Remove an explicit subclass. */
    boolean removeSubClass( OWLOntology ontology, 
			   OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( subClasses, ontology ).remove( description ); 
    }
    
    /** Remove an explicit superclass. */
    boolean removeSuperClass( OWLOntology ontology, 
			   OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( superClasses, ontology ).remove( description ); 
    }
    
    /** Returns the equivalent classes in the given ontology. 
     * Returns a collection of {@link OWLFrame OWLFrame}s. Each frame
     * in this list provides necessary conditions for class membership. */
    public Set getEquivalentClasses( OWLOntology o ) {
	return ListFactory.getSet( OWLImplHelper.getAppropriateSet( equivalentClasses, o ) );
    }

    /** Returns the equivalent classes in the given ontology. 
     * Returns a collection of {@link OWLFrame OWLFrame}s. Each frame
     * in this list provides necessary conditions for class membership. */
    public Set getEquivalentClasses( Set ontos ) {
	return ListFactory.getSet( OWLImplHelper.selectValues( equivalentClasses, ontos ) );
    }
  
    /** Returns equivalent classes to this class. Returns a collection
     * of {@link OWLFrame OWLFrame}s. Each frame in the list provides
     * necessary and sufficient conditions for the class. There may be
     * several equivalences in which case all are deemed to be
     * equivalent. */
    public Set getEquivalentClasses() {
	return OWLImplHelper.unionValues( equivalentClasses );
    }
  
    /** Add an equivalence. */
    boolean addEquivalentClass( OWLOntology ontology, 
				OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( equivalentClasses, ontology ).add( description ); 
    }

    /** Remove an equivalence. */
    boolean removeEquivalentClass( OWLOntology ontology, 
				OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( equivalentClasses, ontology ).remove( description ); 
    }

    /** Returns the enumerations that have been asserted as being
     * equivalent to this class in the given ontology. Each
     * Enumeration in the collection provides an enumeration of all
     * the individuals in the class. Note that if this collection
     * contains more than one enumeration, then implicit equivalances
     * between individuals may be being asserted. */ 
    public Set getEnumerations( OWLOntology o ) {
	return ListFactory.getSet( OWLImplHelper.getAppropriateSet( enumerations, o ) );
    }

    /** Returns the enumerations that have been asserted as being
     * equivalent to this class in the given ontologies. Each
     * Enumeration in the collection provides an enumeration of all
     * the individuals in the class. Note that if this collection
     * contains more than one enumeration, then implicit equivalances
     * between individuals may be being asserted. */ 
    public Set getEnumerations( Set ontos ) {
	return ListFactory.getSet( OWLImplHelper.selectValues( enumerations, ontos ) );
    }

    /** Returns the enumerations that have been asserted as being
     * equivalent to this class. Each Enumeration in the collection
     * provides an enumeration of all the individuals in the
     * class. Note that if this collection contains more than one
     * enumeration, then implicit equivalances between individuals may
     * be being asserted. */ 
    public Set getEnumerations() {
	return OWLImplHelper.unionValues( enumerations );
    }

    /** Add an enumeration. */
    boolean addEnumeration( OWLOntology ontology, 
			    OWLEnumeration enumeration ) {
	return OWLImplHelper.getAppropriateSet( enumerations, ontology ).add( enumeration ); 
    }

    /** Remove an enumeration. */
    boolean removeEnumeration( OWLOntology ontology, 
			    OWLEnumeration enumeration ) {
	return OWLImplHelper.getAppropriateSet( enumerations, ontology ).remove( enumeration ); 
    }

    public boolean isDeprecated( OWLOntology o ) {
	return deprecated.contains( o );
    }

    public void setDeprecated( OWLOntology o, boolean b ) {
	if ( b ) {
	    deprecated.add( o );
	} else {
	    if ( deprecated.contains( o ) ) {
		deprecated.remove ( o );
	    }
	}
    }

    public void accept( OWLObjectVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    public void accept( OWLEntityVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    public void accept( OWLDescriptionVisitor visitor) throws OWLException {
	visitor.visit( this );
    }

	public Set getSubClasses(OWLOntology o) throws OWLException {
		return ListFactory.getSet( OWLImplHelper.getAppropriateSet( subClasses, o ) );		
	}

	public Set getSubClasses(Set ontos) throws OWLException {
		return ListFactory.getSet( OWLImplHelper.selectValues( subClasses, ontos ) );
	}


//     /** Returns a {@link Set Set} of those all {@link
//      * OWLOntologyObject OWLOntologyObject} instances that use this
//      * OWLClass, for example, this will include all classes that use
//      * this class in a superclass, or equivalent class expression. */
//     public Set getUsage( OWLOntology ontology ) throws OWLException {
// 	/* This is horribly inefficient as it requires us to run
//     around and collect stuff from all over. Would be much better to
//     keep track of this explicitly. */
//     }

}// OWLClass


/*
 * ChangeLog
 * $Log: OWLClassImpl.java,v $
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
 * Revision 1.3  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/07/09 12:07:48  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.7  2003/06/25 16:04:57  bechhofers
 * Added removal events
 *
 * Revision 1.6  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.5  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.4  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.3  2003/02/07 18:43:11  seanb
 * New change events
 *
 * Revision 1.2  2003/02/06 16:42:20  seanb
 * Tweaking of class implementation.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */

