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
import java.util.Map;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLDescription;
import java.net.URI;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataValue;

/**
 * An individual object. Each individual knows which Classes it has
 * been asserted as being an instance of. In addition, each individual
 * also knows the individuals it is related to.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLIndividualImpl.java,v 1.5 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public class OWLIndividualImpl extends OWLEntityImpl implements OWLIndividual
{
    private Map types;
    private Map objectPropertyValues;
    private Map incomingObjectPropertyValues;
    private Map dataPropertyValues;

    private URI anonId;
    
    public OWLIndividualImpl( OWLDataFactoryImpl factory, URI uri) {
	this( factory, uri, false );
    }
    
    // ANON: another constructor to disntinguish if URI is genid 
    public OWLIndividualImpl( OWLDataFactoryImpl factory, URI uri, boolean isAnon) {
	super( factory );
	if(uri == null) throw new NullPointerException();
	if(isAnon)
	    this.anonId = uri;
	else
	    this.uri = uri;
	this.types = ListFactory.getMap();
	this.objectPropertyValues = ListFactory.getMap();
	this.incomingObjectPropertyValues = ListFactory.getMap();
	this.dataPropertyValues = ListFactory.getMap();
    }

    public boolean isAnonymous() {
	return uri==null;
    }

    public URI getAnonId() {
	return anonId;
    }

    public Set getTypes( OWLOntology o ) {
	return OWLImplHelper.getAppropriateSet( types, o );
    }

    public Set getTypes( Set ontologies ) {
	return OWLImplHelper.selectValues( types, ontologies );
    }

    /** Returns a list of the {@link OWLDescription OWLDescription}s that
     * this individual is asserted as being an instance of. */ 
    public Set getTypes() {
	return OWLImplHelper.unionValues( types );
    }

    /** Add an explicit type. */
    boolean addType( OWLOntology ontology, 
		      OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( types, ontology ).add( description ); 
    }
  
    /** Remove an explicit type. */
    boolean removeType( OWLOntology ontology, 
		      OWLDescription description ) {
	return OWLImplHelper.getAppropriateSet( types, ontology ).remove( description ); 
    }
    
    /** Returns a {@link Map Map} which encapsulate the relationships
     * between this individual and other individuals. */
    public Map getObjectPropertyValues( OWLOntology o ) {
	return OWLImplHelper.getAppropriateMap( objectPropertyValues, o );
    }

    /** Returns a {@link Map Map} which encapsulate the relationships
     * between this individual and other individuals. */
    public Map getObjectPropertyValues( Set ontologies ) {
	return OWLImplHelper.selectMaps( objectPropertyValues, ontologies );
    }

    /** Returns a {@link Map Map} which encapsulate the relationships
     * between this individual and other individuals. */
    public Map getObjectPropertyValues() {
	return OWLImplHelper.unionMaps( objectPropertyValues );
    }

    boolean addObjectPropertyValue( OWLOntology o, 
					OWLObjectProperty prop, 
					OWLIndividual ind ) {
	//	System.out.println(this.getURI() + " - " + prop.getURI() + " -> " + ind.getURI()); 
	Map m = OWLImplHelper.getAppropriateMap( objectPropertyValues, o );
	/* Find the values for this property */
	Set s = (Set) m.get( prop );
	if ( s == null ) {
	    /* Not there, so we put a new one in */
	    s = ListFactory.getSet();
	    m.put( prop, s );
	}
	boolean added = s.add( ind );

	/* Now also inform the other individual of the relationship */
	return ((OWLIndividualImpl) ind).addIncomingObjectPropertyValue( o, prop, this ) && added;
    }
	
    boolean removeObjectPropertyValue( OWLOntology o, 
			OWLObjectProperty prop, 
			OWLIndividual ind ) {
    	//	System.out.println(this.getURI() + " - " + prop.getURI() + " -> " + ind.getURI()); 
    	Map m = OWLImplHelper.getAppropriateMap( objectPropertyValues, o );
    	/* Find the values for this property */
    	Set s = (Set) m.get( prop );
//    	if ( s == null ) {
//    		/* Not there, so we put a new one in */
//    		s = ListFactory.getSet();
//    		m.put( prop, s );
//    	}	
    	boolean removed = s.remove( ind );

    	/* Now also inform the other individual of the relationship */
    	return ((OWLIndividualImpl) ind).removeIncomingObjectPropertyValue( o, prop, this ) && removed;
    }
    
    /** Returns a {@link Map Map} which encapsulate the relationships
     * between other individuals and this individual. */
    public Map getIncomingObjectPropertyValues( OWLOntology o ) {
	return OWLImplHelper.getAppropriateMap( incomingObjectPropertyValues, o );
    }

    /** Returns a {@link Map Map} which encapsulate the relationships
     * between other individuals and this individual. */
    public Map getIncomingObjectPropertyValues( Set ontologies) {
	return OWLImplHelper.selectMaps( incomingObjectPropertyValues, ontologies );
    }

    /** Returns a {@link Map Map} which encapsulate the relationships
     * between other individuals and this individual. */
    public Map getIncomingObjectPropertyValues() {
	return OWLImplHelper.unionMaps( incomingObjectPropertyValues );
    }

    boolean addIncomingObjectPropertyValue( OWLOntology o, 
					OWLObjectProperty prop, 
					OWLIndividual ind ) {
	Map m = OWLImplHelper.getAppropriateMap( incomingObjectPropertyValues, o );
	/* Find the values for this property */
	Set s = (Set) m.get( prop );
	if ( s == null ) {
	    /* Not there, so we put a new one in */
	    s = ListFactory.getSet();
	    m.put( prop, s );
	}
	return s.add( ind );
    }

    boolean removeIncomingObjectPropertyValue( OWLOntology o, 
			OWLObjectProperty prop, 
			OWLIndividual ind ) {
    	Map m = OWLImplHelper.getAppropriateMap( incomingObjectPropertyValues, o );
    	/* Find the values for this property */
    	Set s = (Set) m.get( prop );
//    	if ( s == null ) {
//    		/* Not there, so we put a new one in */
//    		s = ListFactory.getSet();
//    		m.put( prop, s );
//    	}	
    	return s.remove( ind );
    }
    
    /** Returns a {@link Map Map} which encapsulates the relationship
	between this individual and data values. */
    public Map getDataPropertyValues( OWLOntology o ) {
	return OWLImplHelper.getAppropriateMap( dataPropertyValues, o );
    }

    /** Returns a {@link Map Map} which encapsulates the relationship
	between this individual and data values. */
    public Map getDataPropertyValues( Set ontologies ) {
	return OWLImplHelper.selectMaps( dataPropertyValues, ontologies );
    }
    
    /** Returns a {@link Map Map} which encapsulates the relationship
    between this individual and data values. */
    public Map getDataPropertyValues() {
	return OWLImplHelper.unionMaps( dataPropertyValues );
    }

    boolean addDataPropertyValue( OWLOntology o, 
				  OWLDataProperty prop, 
				  OWLDataValue val ) {
	//	System.out.println(this.getURI() + " - " + prop.getURI() + " -> " + val.getURI()); 
	Map m = OWLImplHelper.getAppropriateMap( dataPropertyValues, o );
	/* Find the values for this property */
	Set s = (Set) m.get( prop );
	if ( s == null ) {
	    /* Not there, so we put a new one in */
	    s = ListFactory.getSet();
	    m.put( prop, s );
	}
 	return s.add( val );
    }

    boolean removeDataPropertyValue( OWLOntology o, 
			  OWLDataProperty prop, 
			  OWLDataValue val ) {
    	//	System.out.println(this.getURI() + " - " + prop.getURI() + " -> " + val.getURI()); 
    	Map m = OWLImplHelper.getAppropriateMap( dataPropertyValues, o );
    	/* Find the values for this property */
    	Set s = (Set) m.get( prop );
//    	if ( s == null ) {
//    		/* Not there, so we put a new one in */
//    		s = ListFactory.getSet();
//    		m.put( prop, s );
//    	}
    	return s.remove( val );
}
    

    public void accept( OWLObjectVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    public void accept( OWLEntityVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    
    
    public boolean equals(Object other) {
        if( this == other )
            return true;
        
        if( other instanceof OWLIndividualImpl ) {
            OWLIndividualImpl that = (OWLIndividualImpl) other;
            
            URI thisURI = this.getURI();
            URI thatURI = that.getURI();            
    		if( thisURI != null ) {
    		    if( thatURI != null )
    		        return thisURI.equals( thatURI );  
    		    else
    		        return false;
    		}
    		else {
    		    if( thatURI == null ) {
    	            thisURI = this.getAnonId();
    	            thatURI = that.getAnonId();            
    		        return (thisURI != null) && (thatURI != null) && thisURI.equals(thatURI);
    		    }
    		    else
    		        return false;
    		}  		        		
        }
        
		return false;
	}
}// OWLIndividual


/*
 * ChangeLog
 * $Log: OWLIndividualImpl.java,v $
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
 * Revision 1.3  2004/10/25 18:01:10  aditkal
 * Bringing code up to date
 *
 * 09-01-04 aditya_kalyanpur
 * Added removeDataPropertyValue, removeObjectPropertyValue, 
 * removeIncomingObjectPropertyValue, removeType
 * 
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.9  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.8  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.7  2003/04/10 12:13:06  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.6  2003/03/31 16:55:12  seanb
 * Various updates and fixes to parser.
 * Adding inverses, domain and range.
 *
 * Revision 1.5  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.4  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.3  2003/02/17 18:23:54  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.2  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */

