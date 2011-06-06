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

import java.net.URI;

import org.semanticweb.owl.model.OWLNamedObject;

/**
 * A superclass for all named things (anything that has a
 * {@link URI URI} associated with it).
 * 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLNamedObjectImpl.java,v 1.4 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public abstract class OWLNamedObjectImpl extends OWLObjectImpl implements OWLNamedObject
{
    protected URI uri;
    protected Double random; // random no. created at startup used for hashCode
    
    public OWLNamedObjectImpl( OWLDataFactoryImpl factory ) {
	super( factory );
	random = new Double(Math.random());
    }

    public URI getURI() {
	return uri;
    }

    public String toString() {
	String[] bits = getClass().getName().split("\\.");
	return "[" +  bits[bits.length-1] + "] " + getURI();
    }

    public int hashCode() {
		if (uri == null) {
			if (random == null)
				random = new Double(Math.random());
			return random.hashCode();
		} else
			return uri.hashCode();
	}

	public boolean equals(Object other) {
		if (super.equals(other)) {
			OWLNamedObjectImpl that = (OWLNamedObjectImpl) other;

			URI thisURI = this.getURI();
			URI thatURI = that.getURI();
			if (thisURI != null && thatURI != null)
				return thisURI.equals(thatURI);
			else
				return false;
		}

		return false;
	}
     

  
}// OWLNamedObject


/*
 * ChangeLog $Log: OWLNamedObjectImpl.java,v $
 * ChangeLog Revision 1.4  2006/03/28 16:14:45  ronwalf
 * ChangeLog Merging mindswap changes to OWLApi.
 * ChangeLog Rough summary:
 * ChangeLog * 1.5 compatibility (rename enum variables)
 * ChangeLog * An option to turn on and off importing in OWLConsumer
 * ChangeLog * Bug fix to allow DataRange in more areas
 * ChangeLog * Giving Anonymous individuals an identifier
 * ChangeLog   * New factory method - getAnonOWLIndividual
 * ChangeLog   * getOWLIndividual no longer accepts 'null'
 * ChangeLog   * added getAnonId() and isAnon() to OWLIndividual
 * ChangeLog * Some work on the RDF serializer, but we have a complete rewrite in
 * ChangeLog   Swoop that I think is better (more flexible, results easier to read)
 * ChangeLog * Added Transitive, Functional, InverseFunctional, Inverse, and
 * ChangeLog   Symmetric PropertyAxioms (not sure why, will check)
 * ChangeLog * Added .equals and .hashcode for all OWLObjects
 * ChangeLog * Added a RemoveDataType change
 * ChangeLog * Patches to OntologyImpl for Entity removal
 * ChangeLog * Added OWLIndividualTypeAssertion
 * ChangeLog * Added OWL(Object|Data)Property(Domain|Range)Axiom
 * ChangeLog * Added OWL(Object|Data)PropertyInstance
 * ChangeLog * Added subclass index to OWLClassImpl (and getSubClasses(...) for
 * ChangeLog   OWLClass)
 * ChangeLog * Changes for Entity renaming
 * ChangeLog Revision 1.3 2005/06/10 12:20:31
 * sean_bechhofer Housekeeping license information to consistent LGPL.
 * 
 * Revision 1.2 2004/07/09 12:07:48 sean_bechhofer Addition of functionality to
 * access usage, e.g. where classes, properties etc are used within the
 * ontology.
 * 
 * Revision 1.1.1.1 2003/10/14 17:10:14 sean_bechhofer Initial Import
 * 
 * Revision 1.1 2003/09/25 11:21:48 volzr model.impl -> impl.model rename
 * 
 * Revision 1.2 2003/03/27 19:51:54 seanb Various changes.
 * 
 * Revision 1.1 2003/01/29 14:30:19 seanb Initial Checkin
 * 
 * 
 */

