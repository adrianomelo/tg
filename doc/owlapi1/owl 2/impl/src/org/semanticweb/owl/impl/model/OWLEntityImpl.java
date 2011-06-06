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
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.helper.OntologyHelper;
import java.util.Set;

// Generated package name


/**
 *
 * @author Sean Bechhofer
 * @version $Id: OWLEntityImpl.java,v 1.4 2005/06/10 12:20:31 sean_bechhofer Exp $
 */

public abstract class OWLEntityImpl extends OWLNamedObjectImpl implements OWLEntity
{

    private Set ontologies;
    
    public OWLEntityImpl( OWLDataFactoryImpl factory ) {
	super( factory );
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
     * Remove the given ontology from the collection that this class knows
     * it appears in.
     *
     * @param o an <code>OWLOntologyImpl</code> value
     */
    protected boolean removeOntology( OWLOntologyImpl o ) {
	return ontologies.remove( o );
    }

    /** Returns a {@link Set Set} of those all {@link
     * org.semanticweb.owl.model.OWLOntologyObject OWLOntologyObject}
     * instances that use this OWLEntity in some way. For example, for
     * {@link org.semanticweb.owl.model.OWLClass OWLClass} this will
     * include all classes that use the class in a superclass, or
     * equivalent class expression.  
     * <br/> 
     * <strong>WARNING</strong>: This is largely untested code and is
     * rather inefficient.
     */
    public Set getUsage( OWLOntology ontology ) throws OWLException {
	/* Very inefficient -- uses a general mechanism to do the
	 * running around. */
	return OntologyHelper.entityUsage( ontology, this );
    }

    /** Returns a {@link Set Set} of those all {@link OWLEntity OWLEntity} instances that are used by this entity in the
     * ontology. For example, for {@link OWLClass OWLClass} this will
     * include all classes that are directly used in superclass or
     * equivalent class expressions. Note that if, for example a
     * disjoint class axiom stating that A and B are disjoint is added
     * to the ontology, asking A for objectsUsed will not return
     * B. Similarly for equivalences. */
    public Set objectsUsed( OWLOntology ontology ) throws OWLException {
	return OntologyHelper.objectsUsed( ontology, this );
    }
    
} // OWLEntityImpl



/*
 * ChangeLog
 * $Log: OWLEntityImpl.java,v $
 * Revision 1.4  2005/06/10 12:20:31  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/07/09 14:04:58  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
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
 * Revision 1.2  2003/02/12 16:14:22  seanb
 * no message
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
