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
import org.semanticweb.owl.model.OWLDataRange;

// Generated package name


/**
 * A Restriction over an {@link OWLDataProperty OWLDataProperty} that is
 * quantified. Subinterfaces specify the quantifier.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLDataQuantifiedRestriction.java,v 1.2 2005/06/10 12:20:28 sean_bechhofer Exp $
 */

public interface OWLDataQuantifiedRestriction extends OWLDataRestriction 
{

    /** Returns the data type that the restriction quantifies over. */
    public OWLDataRange getDataType() throws OWLException;
 
}// DataQuantifiedRestriction


/*
 * ChangeLog
 * $Log $
 * 
 */

