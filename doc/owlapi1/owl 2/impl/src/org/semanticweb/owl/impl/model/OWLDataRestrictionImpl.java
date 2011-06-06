/*
 * Copyright (C) 2005, University of Manchester
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

package org.semanticweb.owl.impl.model; 
import org.semanticweb.owl.model.OWLDataRestriction;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLException;

// Generated package name

/**
 * A Restriction that particularly applies to an {@link OWLDataProperty OWLDataProperty}. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLDataRestrictionImpl.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public abstract class OWLDataRestrictionImpl extends OWLObjectImpl implements OWLDataRestriction
{
    private OWLDataProperty property;

    public OWLDataRestrictionImpl( OWLDataFactoryImpl factory,
				   OWLDataProperty property) {
	super( factory );
	this.property = property;
    }
    
    /** Returns the {@link OWLDataProperty OWLDataProperty} that the
     * restriction applies to. */
    public OWLDataProperty getDataProperty() {
	return property;
    }
    
    public OWLProperty getProperty() {
	return getDataProperty();
    }
    
    public boolean equals(Object obj) {
    		if (super.equals(obj)) {
    			if (((OWLDataRestrictionImpl) obj).property.equals(property)) {
    				return true;
    			}
    		}
    		return false;
    }
    
    public int hashCode() {
		return super.hashCode() + hashCode(property);
    }
}// OWLDataRestriction


/*
 * ChangeLog
 * $Log $
 * 
 */

