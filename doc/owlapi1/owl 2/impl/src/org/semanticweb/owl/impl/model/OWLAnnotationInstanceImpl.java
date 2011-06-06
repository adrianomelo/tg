/*
 * Copyright (C) 2003 The University of Manchester 
 * Copyright (C) 2003 The University of Karlsruhe
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 ****************************************************************
 * Source code information
 * -----------------------
 * Filename           $RCSfile: OWLAnnotationInstanceImpl.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package org.semanticweb.owl.impl.model; 
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.impl.model.OWLObjectImpl;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.model.OWLSubClassAxiom;

import java.util.Set;

// Generated package name


/**
 * OWLAnnotationInstanceImpl.java
 *
 *
 * Created: Fri May 09 09:33:05 2003
 *
 * @author Sean Bechhofer
 * @version $Id: OWLAnnotationInstanceImpl.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $
 */

public class OWLAnnotationInstanceImpl extends OWLObjectImpl implements OWLAnnotationInstance
{

    private OWLObject subject;
    private OWLAnnotationProperty property;
    private Object content;

    /* Should this really be a set or a singleton? */
    private Set ontologies;

    public OWLAnnotationInstanceImpl( OWLDataFactoryImpl factory,
				      OWLObject subj,
				      OWLAnnotationProperty prop,
				      Object cont)
    {
	super( factory );
	this.subject = subj;
	this.property = prop;
	this.content = cont;
	this.ontologies = ListFactory.getSet();
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
     * Remove the given ontology from the collection that this annotation knows
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

    public OWLObject getSubject() throws OWLException {
	return subject;
    }
    public OWLAnnotationProperty getProperty() throws OWLException {
	return property;
    }
    public Object getContent() throws OWLException {
	return content;
    }
    
    public boolean equals(Object o) {
    		if (super.equals(o)) {
	 
	    		if (((OWLAnnotationInstanceImpl) o).subject.equals(this.subject)
				&& ((OWLAnnotationInstanceImpl) o).property.equals(this.property)
				&& ((OWLAnnotationInstanceImpl) o).content.equals(this.content)) {
					return true;
				}
	    	}
    	
    		return false;
    }
    
    public int hashCode() {
    		return super.hashCode() + hashCode(subject) + hashCode(property) + hashCode(content);
    }

} // OWLAnnotationInstanceImpl



/*
 * ChangeLog
 * $Log: OWLAnnotationInstanceImpl.java,v $
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
 * Revision 1.1  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 */
