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
 * An {@link OWLProperty OWLProperty} whose range is a class (a
 * collection of domain objects).
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLObjectProperty.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $ 
 */

public interface OWLObjectProperty extends OWLProperty
{
    
    /**
     * Return all inverses asserted in the given ontology.
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>Set</code> value
     */
    public Set getInverses( OWLOntology o ) throws OWLException;

    /**
     * Return all inverses asserted in the given ontologies.
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>Set</code> value
     */
    public Set getInverses( Set ontologies) throws OWLException;
  
    /**
     * Returns <code>true</code> if the property is asserted to be
     * symmetric (in the given ontology).
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isSymmetric( OWLOntology o ) throws OWLException;

    /**
     * Returns <code>true</code> if the property is asserted to be
     * symmetric (in the given ontologies).
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isSymmetric( Set ontologies ) throws OWLException;

    /**
     * 
     * Returns <code>true</code> if the property is asserted to be
     * inverse functional (in the given ontology).
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isInverseFunctional( OWLOntology o ) throws OWLException;

    /**
     * 
     * Returns <code>true</code> if the property is asserted to be
     * inverse functional (in the given ontologies).
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isInverseFunctional( Set ontologies ) throws OWLException;

    /**
     * 
     * Returns <code>true</code> if the property is asserted to be
     * oneToOne (in the given ontology).
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isOneToOne( OWLOntology o ) throws OWLException;

    /**
     * 
     * Returns <code>true</code> if the property is asserted to be
     * oneToOne (in the given ontologies).
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isOneToOne( Set ontologies ) throws OWLException;

    /**
     * Returns <code>true</code> if the property is asserted to be
     * transitive (in the given ontology).
     * 
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isTransitive( OWLOntology o ) throws OWLException;

    /**
     * Returns <code>true</code> if the property is asserted to be
     * transitive (in the given ontology).
     * 
     *
     * @param o an <code>OWLOntology</code> value
     * @return a <code>boolean</code> value
     */
    public boolean isTransitive( Set ontologies ) throws OWLException;

}// OWLObjectProperty


/*
 * ChangeLog
 * $Log: OWLObjectProperty.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.3  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.2  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 * 
 */

