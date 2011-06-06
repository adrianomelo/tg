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

package org.semanticweb.owl.impl.model; 
import org.semanticweb.owl.model.OWLObjectRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLException;
// Generated package name

/**
 * A Restriction that particularly applies to an {@link OWLObjectProperty OWLObjectProperty}.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLObjectRestrictionImpl.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public abstract class OWLObjectRestrictionImpl extends OWLObjectImpl implements OWLObjectRestriction
{
    private OWLObjectProperty property;

    public OWLObjectRestrictionImpl( OWLDataFactoryImpl factory,
					 OWLObjectProperty property ) {
	super( factory );
	this.property = property;
    }

    /** Returns the {@link OWLObjectProperty OWLObjectProperty} that the
     * restriction applies to. */
    public OWLObjectProperty getObjectProperty() {
	return property;
    }

    public OWLProperty getProperty() {
	return getObjectProperty();
    }
    
    public boolean equals(Object obj) {
    		if (super.equals(obj)) {
    			if (((OWLObjectRestrictionImpl)obj).property.equals(property)) {
    				return true;
    			}
    		}
		return false;
    }
    public int hashCode() {
		int hash = super.hashCode() + hashCode(property);
		return hash;
    }

}// OWLObjectRestriction


/*
 * ChangeLog
 * $Log: OWLObjectRestrictionImpl.java,v $
 * Revision 1.3  2006/03/28 16:14:45  ronwalf
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
 * Revision 1.2  2005/06/10 12:20:32  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.1  2003/05/16 11:34:48  seanb
 * Further renaming of individual to object in the implementations
 * of restrictions.
 *
 *
 * 
 */

