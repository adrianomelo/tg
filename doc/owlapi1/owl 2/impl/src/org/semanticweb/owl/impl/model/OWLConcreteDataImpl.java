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

package org.semanticweb.owl.impl.model; 
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLException;
import java.net.URI;
// Generated package name

/**
 * A place holder for concrete data. This will ultimately be replaces
 * by something like XML Schema data values.
 *
 *
 * @author Phillip Lord
 * @version $Id: OWLConcreteDataImpl.java,v 1.4 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public class OWLConcreteDataImpl extends OWLObjectImpl implements OWLDataValue 
{
    /* All needs a little more thought */


    private URI uri;
    private Object value;
    private String lang;

    public OWLConcreteDataImpl( OWLDataFactoryImpl factory, 
				URI uri, 
				String lang, 
				Object value ) {
	super( factory );
	/* By default xsd:string */

	/* One of uri or lang should really be null here... */
	this.uri = uri;
	this.lang = lang;
	this.value = value;
    }
    
    public URI getURI() {
	return uri;
    }

    public String getLang() {
	return lang;
    }

    public Object getValue() {
	/* This should maybe return a copy??*/
	return value;
    }

    public void accept( OWLObjectVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    public String toString() {
	if (uri!=null) {
	    return value.toString() + "^^" + uri.toString();
	} else if (lang!=null) {
	    return value.toString() + "@" + lang;
	} else {
	    return value.toString();
	}
    }
    
    public boolean equals(Object o) {
		if (super.equals(o)) {
    			if (((OWLConcreteDataImpl) o).toString().equals(this.toString())) {
				return true;
			}
    		}
    		return false;
    }
    
    public int hashCode() {
    	return super.hashCode() + hashCode(uri) + hashCode(value) + hashCode(lang);
    }

}// OWLConcreteData


/*
 * ChangeLog
 * $Log: OWLConcreteDataImpl.java,v $
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
 * Revision 1.2  2003/11/20 12:58:09  sean_bechhofer
 * Addition of language handling in OWLDataValues.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.4  2003/05/15 13:51:43  seanb
 * Addition of new non-streaming parser.
 *
 * Revision 1.3  2003/04/10 12:13:06  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.2  2003/02/07 18:43:11  seanb
 * New change events
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */

