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
 * An OWL Class represents a named class within an ontology. If
 * "anonymous" classes are used within descriptions, then the
 * appropriate description should be used. As with other examples of
 * {@link OWLEntity OWLEntity}, information about the OWLClass is
 * held w.r.t. a particular {@link OWLOntology OWLOntology}, and
 * requests for information about an OWLClass should be made in
 * the context of an ontology or set of ontologies.  <br/>
 *
 * @author Sean Bechhofer
 * @version $Id: OWLClass.java,v 1.4 2005/06/10 12:20:28 sean_bechhofer Exp $ 
 */

public interface OWLClass extends OWLDescription, OWLEntity, OWLDeprecatableObject
{

    /** Returns the explicit superclasses of this class in the given ontology. Returns a
     * collection of {@link OWLDescription OWLDescription}s. Each description in this list
     * provides necessary conditions for class membership. */
    public Set getSuperClasses( OWLOntology o ) throws OWLException;

    /** Returns the explicit superclasses of this class in any of the
     * given ontologies. Each description in this list provides necessary
     * conditions for class membership. */
    public Set getSuperClasses( Set ontologies ) throws OWLException;

    /** Returns equivalent classes to this class in the given ontology. Returns a collection
     * of {@link OWLFrame OWLFrame}s. Each frame in the list provides
     * necessary and sufficient conditions for the class. There may be
     * several equivalences in which case all are deemed to be
     * equivalent. */
    public Set getEquivalentClasses( OWLOntology o ) throws OWLException;

    /** Returns equivalent classes to this class in any of the given
     * ontologies. Each description in the list provides necessary and
     * sufficient conditions for the class. There may be several
     * equivalences in which case all are deemed to be equivalent. */
    public Set getEquivalentClasses( Set ontologies ) throws OWLException;
  
    /** Returns the enumerations that have been asserted as being
     * equivalent to this class in any of the given ontologies. Each
     * Enumeration in the collection provides an enumeration of all
     * the individuals in the class. Note that if this collection
     * contains more than one enumeration, then implicit equivalances
     * between individuals may be being asserted. */ 
    public Set getEnumerations( Set ontologies ) throws OWLException;

    /** Returns the enumerations that have been asserted as being
     * equivalent to this class in the given ontology. Each Enumeration in the collection
     * provides an enumeration of all the individuals in the
     * class. Note that if this collection contains more than one
     * enumeration, then implicit equivalances between individuals may
     * be being asserted. */ 
    public Set getEnumerations( OWLOntology o ) throws OWLException;

}// OWLClass


/*
 * ChangeLog
 * $Log: OWLClass.java,v $
 * Revision 1.4  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/07/09 12:07:45  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.2  2003/12/02 10:01:11  sean_bechhofer
 * Fixing documentation
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.4  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.3  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.2  2003/02/10 09:23:06  seanb
 * Changes to cardinality, addition of property instances.
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 * 
 */

