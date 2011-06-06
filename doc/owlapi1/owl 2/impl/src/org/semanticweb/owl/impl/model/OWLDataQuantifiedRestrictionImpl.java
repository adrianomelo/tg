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
import org.semanticweb.owl.model.OWLDataQuantifiedRestriction;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLObjectVisitor;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLDataRange;

// Generated package name


/**
 * A Restriction over an {@link OWLDataProperty OWLDataProperty} that is
 * quantified. Subinterfaces specify the quantifier.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLDataQuantifiedRestrictionImpl.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $
 */

public abstract class OWLDataQuantifiedRestrictionImpl extends OWLDataRestrictionImpl implements OWLDataQuantifiedRestriction
{

    private OWLDataRange dataType;

    public OWLDataQuantifiedRestrictionImpl( OWLDataFactoryImpl factory,
					     OWLDataProperty property,
					     OWLDataRange dataType ) {
	super( factory, property );
	this.dataType = dataType;
    }

    /** Returns the data type that the restriction quantifies over. */
    public OWLDataRange getDataType() {
	return dataType;
    }
    
    public boolean equals(Object obj) {
		if (super.equals(obj)) {
			if (((OWLDataQuantifiedRestrictionImpl) obj).dataType.equals(dataType)) {
				return true;
			}
		}
		return false;
    }
    
    public int hashCode() {
		return super.hashCode() + hashCode(dataType);
    }

}// DataQuantifiedRestriction


/*
 * ChangeLog
 * $Log $
 * 
 */

