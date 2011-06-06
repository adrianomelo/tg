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

package org.semanticweb.owl.model;


/**
 * An axiom asserting a subclass relationship between a pair of
 * Descriptions. 
 *
 *
 * @author Sean Bechhofer 
 * @version $Id: OWLSubClassAxiom.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public interface OWLSubClassAxiom extends OWLClassAxiom 
{
    public OWLDescription getSuperClass() throws OWLException;

    public OWLDescription getSubClass() throws OWLException;
} // OWLSubClassAxiom



/*
 * ChangeLog
 * $Log: OWLSubClassAxiom.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 *
 */
