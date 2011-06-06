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
import java.io.Serializable;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.util.OWLConnection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import org.semanticweb.owl.model.OWLOntology;
import java.util.Set;
import org.semanticweb.owl.model.OWLAnnotationInstance;

/**
 * A top level class for the kinds of things that are found in Ontologies. Each OWLObject knows about the {@link OWLDataFactory OWLDataFactory} that was used to create it. OWLObjects also accept visits from {@link OWLObjectVisitor OWLObjectVisitor}.
The use of the Visitor pattern (see <i>Design
 Patterns</i>, Gamma et. al. p.331) allows us to extend the functionality of the data structures without having to alter the underlying implementation. 
 * 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLObjectImpl.java,v 1.5 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public abstract class OWLObjectImpl implements Cloneable, Serializable, OWLObject {

	private OWLDataFactoryImpl owlDataFactory;
    private Map metadata;
    private Map annotations;
    
    public OWLObjectImpl( OWLDataFactoryImpl factory ) {
	this.owlDataFactory = factory;
	this.metadata = ListFactory.getMap();
	this.annotations = ListFactory.getMap();
    }

    /**
     * 
     * Return any objects which are contained within this object. By
     * contained this means, any of the objects which can be reached
     * from any of more specific methods in the interface. The
     * ordering of the objects in the array is undefined. A null
     * return represents 
     * @return an <code>OWLObject[]</code>, or null.
     */
    public OWLObject[] getContainedObjects() {
	/* Stop gap */
	return new OWLObject[0];
    }

    /** Returns an OWLDataFactory */
    public OWLDataFactory getOWLDataFactory() {
	return owlDataFactory;
    }

    public Object clone() {
	/* Stop gap */
	try {
	    return super.clone();
	} catch (CloneNotSupportedException ex) {
	    /* Deal with this! */
	    ex.printStackTrace();
	}
	return null;
    }

    /** Accept a visit from a visitor */
    public abstract void accept( OWLObjectVisitor visitor ) throws OWLException ;

    /** Return the generic metadata structure. */
    public Map getMetadata() {
	return ListFactory.getMap( metadata );
    }
    
    public OWLConnection getOWLConnection() {
    	return owlDataFactory.getOWLConnection();
    }

    /** Returns the annotatsion in the given ontology. */
    public Set getAnnotations( OWLOntology o ) {
	return ListFactory.getSet( OWLImplHelper.getAppropriateSet( annotations, o ) );
    }

    /** Returns annotations on the object.*/
    public Set getAnnotations() {
	return ListFactory.getSet( OWLImplHelper.unionValues( annotations ) );
    }

    /** Add an explicit annotation. */
    boolean addAnnotation( OWLOntology ontology, 
			   OWLAnnotationInstance instance ) {
	return OWLImplHelper.getAppropriateSet( annotations, ontology ).add( instance ); 
    }

    /** Remove an explicit annotation. */
    boolean removeAnnotation( OWLOntology ontology, 
			   OWLAnnotationInstance instance ) {
    	Set newAnnotInstanceSet = new HashSet();
    	Iterator iter = OWLImplHelper.getAppropriateSet(annotations, ontology).iterator();
    	while (iter.hasNext()) {
    		OWLAnnotationInstance currInst = (OWLAnnotationInstance) iter.next();
    		try {
	    		if (currInst.getSubject().equals(instance.getSubject()) && 
	    			currInst.getContent().equals(instance.getContent()) &&
					currInst.getProperty().equals(instance.getProperty())) {
	    			// dont add
	    		}
	    		else {
	    			newAnnotInstanceSet.add(currInst);
	    		}
    		}
    		catch (OWLException e) {
    			e.printStackTrace();
    		}
    	}
    	OWLImplHelper.getAppropriateSet(annotations, ontology).clear();
    	OWLImplHelper.getAppropriateSet(annotations, ontology).addAll(newAnnotInstanceSet);
    	return false;
    }
    
    public boolean equals(Object obj) {
    		
    		if ((obj != null) && (this.hashCode() == obj.hashCode())
    				&& this.getClass().equals(obj.getClass())) {
    			return true;
    		}
    		return false;
    }
    
    protected static int hashCode(Object o) {
    		//System.out.println("hashCode for "+o);
    		if (o==null) {
    			return 0;
    		} else {
    			return o.hashCode();
    		}
    }
    
    public int hashCode() {
    		return getClass().hashCode();
    		//return hashCode(metadata) + hashCode(annotations);
    }

}// OWLObjectImpl


/*
 * ChangeLog
 * $Log: OWLObjectImpl.java,v $
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
 * 09-01-04, aditya_kalyanpur
 * Added removeAnnotation(..)
 * 
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.3  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.2  2003/02/05 14:29:37  rvolz
 * Parser Stuff, Connection
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */

