/*
 * Copyright (C) 2003 The University of Manchester 
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 *
 ****************************************************************
 * Source code information
 * -----------------------
 * Filename           $RCSfile: SomeUndefinedOWLPropertyLaxHandler.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/12/01 17:06:10 $
 *               by   $Author: dturi $
 * Created: Mon Nov 17 11:55:12 2003
 ****************************************************************/
package org.semanticweb.owl.io.abstract_syntax;

/**
 * Lax handler for {@link SomeUndefinedOWLPropertyException}s.
 *
 * @author <a href="mailto:dturi at cs.man.ac.uk">Daniele Turi</a>
 * @version $Id: SomeUndefinedOWLPropertyLaxHandler.java,v 1.1 2003/12/01 17:06:10 dturi Exp $
 */
public class SomeUndefinedOWLPropertyLaxHandler
    implements SomeUndefinedOWLPropertyHandler 
{

    /**
     * Handles the {@link SomeUndefinedOWLPropertyException} <code>e</code> by
     * returning the value of the property type stored in <code>e</code>.
     *
     * @param e a <code>SomeUndefinedOWLPropertyException</code> value
     * @return the property type stored in <code>e</code>.
     */
    public int handle(SomeUndefinedOWLPropertyException e) 
	throws SomeUndefinedOWLPropertyException 
    {
	    return e.getPropertyType();
    }
    
}
