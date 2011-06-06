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
 * An OWLCardinalityRestriction represents an assertion about the number
 * of role fillers a property can or must have. In a change from
 * DAML+OIL, OWL cardinality restrictions are unqualified, i.e. they
 * do not state what type the filler should have.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLCardinalityRestriction.java,v 1.2 2005/06/10 12:20:28 sean_bechhofer Exp $
 */

public interface OWLCardinalityRestriction extends OWLRestriction
{

    public int getAtLeast() throws OWLException;
  
    public int getAtMost() throws OWLException;
  
    /** Is this an atmost cardinality restriction? */ 
    public boolean isAtMost() throws OWLException;

    /** Is this an atleast cardinality restriction? */ 
    public boolean isAtLeast() throws OWLException;
    /** Is this an exact cardinality restriction, i.e. does atleast ==
     * atmost? */ 
    public boolean isExactly() throws OWLException;

}// OWLCardinalityRestriction

/*
 * ChangeLog
 * $Log: OWLCardinalityRestriction.java,v $
 * Revision 1.2  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.3  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.2  2003/02/10 09:23:06  seanb
 * Changes to cardinality, addition of property instances.
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 * 
 */
