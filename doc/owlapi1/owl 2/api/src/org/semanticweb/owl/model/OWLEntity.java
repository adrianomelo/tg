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

package org.semanticweb.owl.model; // Generated package name
import java.io.Serializable;
import java.util.Set;

/**
 * A top level interface for entities -- the primitive kinds of things
that are found in Ontologies. OWLEntities accept visits from {@link OWLEntityVisitor OWLEntityVisitor}.
The use of the Visitor pattern (see <i>Design Patterns</i>, Gamma
et. al. p.331) allows us to extend the functionality of the data
structures without having to alter the underlying implementation.
 * 
<br/>

 * In general, information about subclasses of OWLEntity is held
 * w.r.t. a particular {@link OWLOntology OWLOntology}, and requests
 * for information about an OWLEntity should be made in the context
 * of an ontology or set of ontologies.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLEntity.java,v 1.4 2005/06/10 12:20:28 sean_bechhofer Exp $ 
 */

public interface OWLEntity extends OWLNamedObject, OWLOntologyObject
{
    /** Accept a visit from a visitor */
    public void accept( OWLEntityVisitor visitor ) throws OWLException;

    /** Returns a {@link Set Set} of those all {@link
     * OWLOntologyObject OWLOntologyObject} instances that use this
     * OWLEntity in some way. For example, for {@link OWLClass OWLClass} this will include all classes that use the class in a
     * superclass, or equivalent class expression. */
    public Set getUsage( OWLOntology ontology ) throws OWLException;

    /** Returns a {@link Set Set} of those all {@link OWLEntity OWLEntity} instances that are used by this entity in the
     * ontology. For example, for {@link OWLClass OWLClass} this will
     * include all classes that are directly used in superclass or
     * equivalent class expressions. Note that if, for example a
     * disjoint class axiom stating that A and B are disjoint is added
     * to the ontology, asking A for objectsUsed will not return
     * B. Similarly for equivalences. */
    public Set objectsUsed( OWLOntology ontology ) throws OWLException;

}// OWLEntity


/*
 * ChangeLog
 * $Log: OWLEntity.java,v $
 * Revision 1.4  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/07/09 14:04:57  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
 *
 * Revision 1.2  2004/07/09 12:07:45  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 * 
 */

