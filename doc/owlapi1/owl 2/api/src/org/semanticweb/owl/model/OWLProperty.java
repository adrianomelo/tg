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

import java.util.Set;
/**
 * A Property in an OWL ontology. As with other examples of {@link
 * OWLEntity OWLEntity}, information about the OWLProperty is held
 * w.r.t. a particular {@link OWLOntology OWLOntology}, and requests
 * for information about an OWLProperty should be made in the context of
 * an ontology or set of ontologies.  <br/>
 * 
 * @author Sean Bechhofer
 * @version $Id: OWLProperty.java,v 1.3 2005/06/10 12:20:29 sean_bechhofer Exp $ 
 */

public interface OWLProperty extends OWLEntity, OWLDeprecatableObject
{
    /**
     * Return any asserted superproperties in the given ontology.
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>Set</code> value
     */
    public Set getSuperProperties( OWLOntology o ) throws OWLException;

    /**
     * Return any asserted superproperties in any of the given ontologies.
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>Set</code> value
     */
    public Set getSuperProperties( Set ontologies ) throws OWLException;

    /**
     * Return all domains (in the given ontology).
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>Set</code> value
     */
    public Set getDomains( OWLOntology o ) throws OWLException;

    /**
     * Return all domains (in the given ontologies).
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>Set</code> value
     */
    public Set getDomains( Set ontologies ) throws OWLException;

    /**
     * Return all ranges in the given ontology.
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>Set</code> value
     */
    public Set getRanges( OWLOntology o ) throws OWLException;

    /**
     * Return all ranges in the given ontologies.
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>Set</code> value
     */
    public Set getRanges( Set ontologies ) throws OWLException;

    /**
     * Returns <code>true</code> if the property is declared as being
     * functional in the given ontology
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isFunctional( OWLOntology o ) throws OWLException;

    /**
     * Returns <code>true</code> if the property is declared as being
     * functional in any of the given ontologies
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isFunctional( Set ontologies ) throws OWLException;

  
}// OWLProperty


/*
 * ChangeLog
 * $Log: OWLProperty.java,v $
 * Revision 1.3  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/07/09 12:07:45  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.8  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.7  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.6  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.5  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.4  2003/04/09 14:26:48  seanb
 * no message
 *
 * Revision 1.3  2003/03/26 19:04:02  rvolz
 * *** empty log message ***
 *
 * Revision 1.2  2003/02/10 09:23:06  seanb
 * Changes to cardinality, addition of property instances.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */

