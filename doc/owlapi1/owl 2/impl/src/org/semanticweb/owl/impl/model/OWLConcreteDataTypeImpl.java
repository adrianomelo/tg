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
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import java.net.URI;
import java.util.Set;

// Generated package name

/**
 * A place holder for concrete data types. This will ultimately be
 * replace by something like XML Schema data types.
 *
 *
 * @author Phillip Lord
 * @version $Id: OWLConcreteDataTypeImpl.java,v 1.4 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public class OWLConcreteDataTypeImpl extends OWLObjectImpl implements OWLDataType, OWLDeprecatableObjectImpl
{
    private URI uri;

    private Set deprecated;


    public OWLConcreteDataTypeImpl( OWLDataFactoryImpl factory, URI uri ) {
	super( factory );
	this.uri = uri;
	this.deprecated = ListFactory.getSet();
    }

    public URI getURI() {
	return uri;
    }

    public void accept( OWLObjectVisitor visitor ) throws OWLException {
	visitor.visit( this );
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

    public boolean equals(Object o) {
    		if (super.equals(o)) {
    			if (((OWLConcreteDataTypeImpl) o).getURI().equals(uri))
				return true;
    		}
    		return false;
    }
    
    public int hashCode() {
		return super.hashCode() + hashCode(uri) + hashCode(deprecated);
    }	
}// OWLConcreteDataType


/*
 * ChangeLog
 * $Log: OWLConcreteDataTypeImpl.java,v $
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
 * Revision 1.3  2005/06/10 12:20:31  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2003/12/02 10:03:29  sean_bechhofer
 * Datatypes now deprecatable.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
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

