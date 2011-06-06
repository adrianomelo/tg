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
 * Filename           $RCSfile: SomeUndefinedOWLPropertyException.java,v $
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/12/01 17:06:10 $
 *               by   $Author: dturi $
 ****************************************************************/
package org.semanticweb.owl.io.abstract_syntax;

import org.semanticweb.owl.model.OWLException;



/**
 * SomeUndefinedOWLPropertyException.java.
 *
 * @author <a href="mailto:dturi at cs.man.ac.uk">Daniele Turi</a>
 * @version $Id: SomeUndefinedOWLPropertyException.java,v 1.1 2003/12/01 17:06:10 dturi Exp $
 */
public class SomeUndefinedOWLPropertyException extends OWLException {

    int propertyType;

    /**
     * Creates a new <code>SomeUndefinedOWLPropertyException</code> instance
     * and sets the <code>propertyType</code> property.
     *
     * @param propertyType one of      
     * {@link ANTLRParser#DATA_PROP_INT}, 
     * {@link ANTLRParser#OBJ_PROP_INT},
     * {@link ANTLRParser#ANN_PROP_INT}, 
     * {@link ANTLRParser#ONT_PROP_INT}.
     */
    public SomeUndefinedOWLPropertyException(int propertyType) {
	super("Warning: some, but not all, of the properties are undefined");
	this.propertyType = propertyType;
    }     

    /**
     * Gets the value of the property <code>propertyType</code>,
     * which is set by {@link #SomeUndefinedOWLPropertyException(int)}.
     */
    public int getPropertyType() { return propertyType; }

} 
