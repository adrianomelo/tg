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
 * A frame packages up a top-level description of a class, and
 * consists of a number of superclasses and a number of
 * restrictions. Although {@link OWLFrame OWLFrame} doesn't have a direct
 * counterpart in OWL (or in DAML+OIL), the notion of a frame was a
 * particularly useful one in the <a
 * href="http://oiled.man.ac.uk">OilEd</a> implementation as it
 * provides a user-friendly view on ontology descriptions. The
 * semantics of a frame is simply the conjunction of the superclass
 * descriptions and restrictions.
 *
 * <br/> This class is pretty much unused at present, however it is
 * likely to prove useful in supporting user interactions with OWL
 * ontologies. In particular, it allows us to isolate those places
 * where an intersection can actually be legally used in OWL Lite, and
 * can thus ease the process of species validation.
 * 
 * Created: Fri May 31 16:04:25 2002
 *
 * @author Sean Bechhofer
 * @version $Id: OWLFrame.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $ 
 */

public interface OWLFrame extends OWLDescription
{
    /** Returns a collection of {@link OWLDescription OWLDescription}s. */
    public Set getSuperclasses() throws OWLException;
    /** Returns a collection of {@link OWLRestriction OWLRestriction}s. */
    public Set getRestrictions() throws OWLException;

}// OWLFrame


/*
 * ChangeLog
 * $Log: OWLFrame.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/07/08 18:01:07  bechhofers
 * Presentation Servlet and additional documentation regarding inference.
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

