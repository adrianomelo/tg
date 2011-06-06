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
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLProperty;
import java.net.URI;
import org.semanticweb.owl.model.OWLException;
import java.util.Map;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLDescription;
import java.util.Iterator;

/**
 * An {@link OWLProperty OWLProperty} whose range is a class (a
 * collection of domain objects).
 *
 * @author Sean Bechhofer
 * @version $Id: OWLObjectPropertyImpl.java,v 1.5 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public class OWLObjectPropertyImpl extends OWLPropertyImpl implements OWLObjectProperty
{
    private Map inverses;
    /* Sets that determine properties of the property. If an ontology
     * occurs in this set, then the ontology asserts that the property
     * holds. */

    private Set symmetric;
    private Set inverseFunctional;
    private Set oneToOne;
    private Set transitive;


    public OWLObjectPropertyImpl( OWLDataFactoryImpl factory, URI uri ) {
	super( factory );
	this.uri = uri;
	this.inverses = ListFactory.getMap();
	this.symmetric = ListFactory.getSet();
	this.inverseFunctional = ListFactory.getSet();
	this.oneToOne = ListFactory.getSet();
	this.transitive = ListFactory.getSet();
    }

    /** Add a Range. */
    boolean addRange( OWLOntology ontology, 
		      OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( ranges, ontology ).add( description ); 
    }
    
    
    /** Remove a Range. */
    boolean removeRange( OWLOntology ontology, 
		      OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( ranges, ontology ).remove( description ); 
    }
    /** */
    
    public Set getInverses() {
	return OWLImplHelper.unionValues( inverses );
    }

    public Set getInverses( Set ontos ) {
	return OWLImplHelper.selectValues( inverses, ontos );
    }

    public Set getInverses( OWLOntology o ) {
	return OWLImplHelper.getAppropriateSet( inverses, o );
    }

    /** Add a inverse. */
    boolean addInverse( OWLOntology ontology, 
			OWLObjectProperty prop ) {
	OWLImplHelper.getAppropriateSet( inverses, ontology ).add( prop );
	return true;
    }

    /** Remove an Inverse Property. */
    boolean removeInverse( OWLOntology ontology, 
			OWLObjectProperty prop ) {
	OWLImplHelper.getAppropriateSet( inverses, ontology ).remove( prop ); 
    return true;
    }
    /***/
    
    public boolean isSymmetric( OWLOntology o ) {
	return symmetric.contains( o );
    }

    public boolean isSymmetric( Set ontos ) {
	for (Iterator it = ontos.iterator();
	     it.hasNext(); ) {
	    if (symmetric.contains( it.next() ) ) {
		return true;
	    }
	} 
	return false;
    }

    public boolean isSymmetric() {
	return !symmetric.isEmpty();
    }

    void setSymmetric( OWLOntology o, boolean b ) {
	if ( b ) {
	    symmetric.add( o );
	} else {
	    if ( symmetric.contains( o ) ) {
		symmetric.remove ( o );
	    }
	}
    }

    public boolean isInverseFunctional( OWLOntology o ) {
	return inverseFunctional.contains( o );
    }

    public boolean isInverseFunctional( Set ontos ) {
	for (Iterator it = ontos.iterator();
	     it.hasNext(); ) {
	    if (inverseFunctional.contains( it.next() ) ) {
		return true;
	    }
	} 
	return false;
    }

    public boolean isInverseFunctional() {
	return !inverseFunctional.isEmpty();
    }

    void setInverseFunctional( OWLOntology o, boolean b ) {
	if ( b ) {
	    inverseFunctional.add( o );
	} else {
	    if ( inverseFunctional.contains( o ) ) {
		inverseFunctional.remove ( o );
	    }
	}
    }

    public boolean isOneToOne( OWLOntology o ) {
	return oneToOne.contains( o );
    }

    public boolean isOneToOne( Set ontos ) {
	for (Iterator it = ontos.iterator();
	     it.hasNext(); ) {
	    if (oneToOne.contains( it.next() ) ) {
		return true;
	    }
	} 
	return false;
    }

    public boolean isOneToOne() {
	return !oneToOne.isEmpty();
    }

    void setOneToOne( OWLOntology o, boolean b ) {
	if ( b ) {
	    oneToOne.add( o );
	} else {
	    if ( oneToOne.contains( o ) ) {
		oneToOne.remove ( o );
	    }
	}
    }

    public boolean isTransitive( OWLOntology o ) {
	return transitive.contains( o );
    }

    public boolean isTransitive( Set ontos ) {
	for (Iterator it = ontos.iterator();
	     it.hasNext(); ) {
	    if (transitive.contains( it.next() ) ) {
		return true;
	    }
	} 
	return false;
    }

    public boolean isTransitive() {
	return !transitive.isEmpty();
    }

    void setTransitive( OWLOntology o, boolean b ) {
	if ( b ) {
	    transitive.add( o );
	} else {
	    if ( transitive.contains( o ) ) {
		transitive.remove ( o );
	    }
	}
    }

    public void accept( OWLObjectVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    public void accept( OWLEntityVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }


}// OWLObjectProperty


/*
 * ChangeLog
 * $Log: OWLObjectPropertyImpl.java,v $
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
 * Revision 1.4  2005/06/10 12:20:32  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/10/25 18:01:10  aditkal
 * Bringing code up to date
 * 
 * 09-14-04 aditya_kalyanpur
 * Added removeInverse(..)
 * 
 * 09-01-04 aditya_kalyanpur
 * Added removeRange(..)
 * 
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.5  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.4  2003/03/31 16:55:12  seanb
 * Various updates and fixes to parser.
 * Adding inverses, domain and range.
 *
 * Revision 1.3  2003/03/21 13:55:41  seanb
 * Added domain/range handling.
 *
 * Revision 1.2  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */

