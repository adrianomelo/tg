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

import java.util.Set;

import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLClassAxiomVisitor;

/**
 * An axiom asserting the equivalence of a number of classes. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLEquivalentClassesAxiomImpl.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $
 */

public class OWLEquivalentClassesAxiomImpl extends OWLClassAxiomImpl implements OWLEquivalentClassesAxiom 
{
    private Set equivalentClasses;
    private Set ontologies;
    
    public OWLEquivalentClassesAxiomImpl( OWLDataFactoryImpl factory,
					  Set set ) {
	super( factory );
	this.equivalentClasses = ListFactory.getSet( set );
	this.ontologies = ListFactory.getSet();
    }

    /** Returns a collection of {@link OWLDescription OWLDescription}s which
     * are the descriptions asserted to be equivalent. */ 
    public Set getEquivalentClasses() {
	return ListFactory.getSet( equivalentClasses );
    }

    public Set getOntologies() {
	return ListFactory.getSet( ontologies );
    }

    /**
     * Add the given ontology to the collection that this class knows
     * it appears in.
     *
     * @param o an <code>OWLOntologyImpl</code> value
     */
    protected void addOntology( OWLOntologyImpl o ) {
	ontologies.add( o );
    }

    /**
     * Remove the given ontology from the collection that this class knows
     * it appears in.
     *
     * @param o an <code>OWLOntologyImpl</code> value
     */
    protected boolean removeOntology( OWLOntologyImpl o ) {
	return ontologies.remove( o );
    }

    public void accept( OWLObjectVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    public void accept( OWLClassAxiomVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
    
    public boolean equals(Object o) {
    		if (super.equals(o)) {
			if (((OWLEquivalentClassesAxiomImpl) o).getEquivalentClasses().equals(this.getEquivalentClasses()))
				return true;			
		}
		return false;
    }
    
    public int hashCode() {
    		return super.hashCode() + hashCode(equivalentClasses);
    }

} // OWLEquivalentClassesAxiom



/*
 * ChangeLog
 * $Log: OWLEquivalentClassesAxiomImpl.java,v $
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
 * Revision 1.2  2005/06/10 12:20:31  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.2  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 *
 */
