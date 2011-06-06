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
import java.util.Map;

import org.semanticweb.owl.util.OWLConnection;
import java.util.Set;



/**
 * A top level interface for the kinds of things that are found in Ontologies. Each OWLObject knows about the {@link OWLDataFactory OWLDataFactory} that was used to create it. OWLObjects also accept visits from {@link OWLObjectVisitor OWLObjectVisitor}.
The use of the Visitor pattern (see <i>Design
 Patterns</i>, Gamma et. al. p.331) allows us to extend the functionality of the data structures without having to alter the underlying implementation. 
 * 
 * <br/> 
 * 
 * In addition, we can store metadata about any OWLObject using a
 * {@link Map Map}. This allows us to store pretty much anything we
 * want, although any clients of the objects will, of course, need to
 * know something about the way in which that metadata is structured
 * in order to make use of it.
 * 
 * @author Sean Bechhofer
 * @version $Id: OWLObject.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $ 
 */

// (TODO) : 09-06-2002 Phillip Lord :-  Serializable? Yes? No?
public interface OWLObject extends Cloneable, Serializable
{
    /**
     * 
     * Return any objects which are contained within this object. By
     * contained this means, any of the objects which can be reached
     * from any of more specific methods in the interface. The
     * ordering of the objects in the array is undefined. A null
     * return represents an empty collection.
     * @return an <code>OWLObject[]</code>, or null.
     */
    public OWLObject[] getContainedObjects() throws OWLException;

    /** Returns an OWLDataFactory 
     * [REVIEW] the DataFactory should live with the connection object... */
    public OWLDataFactory getOWLDataFactory() throws OWLException;
    
    /** Returns the Connection of this object */
    public OWLConnection getOWLConnection() throws OWLException;

    public Object clone();

    /** Accept a visit from a visitor */
    public void accept( OWLObjectVisitor visitor ) throws OWLException;

    /** Get metadata -- this returns a {@link Map Map} which allows
     * the support of arbitrary metadata. */
    public Map getMetadata() throws OWLException;

    /** Returns the annotations on the object. */ 
    public Set getAnnotations() throws OWLException;

    /** Returns the annotations on the object in the given
     * ontology. */ 
    public Set getAnnotations( OWLOntology o ) throws OWLException;
    

}// OWLObject


/*
 * ChangeLog
 * $Log: OWLObject.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
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
 * Revision 1.2  2003/02/05 14:29:37  rvolz
 * Parser Stuff, Connection
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 * 
 */

