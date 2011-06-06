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

/**
 * A universally quantified restriction over a datatype property. The
 * semantics of this expression is the collection of objects, all of
 * whose fillers for the given role ({@link OWLDataRestriction#getDataProperty getDataProperty})
 * are elements of the given datatype ({@link OWLDataQuantifiedRestriction#getDataType getDataType}).
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLDataAllRestriction.java,v 1.2 2005/06/10 12:20:28 sean_bechhofer Exp $
 */

public interface OWLDataAllRestriction extends OWLDataQuantifiedRestriction
{

}// OWLDataAllRestriction

/*
 * ChangeLog
 * $Log: OWLDataAllRestriction.java,v $
 * Revision 1.2  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/02/06 10:51:54  rvolz
 * Parser Work
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 * 
 */

