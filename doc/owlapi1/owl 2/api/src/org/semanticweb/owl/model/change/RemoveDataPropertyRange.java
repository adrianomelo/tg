/*
 * Copyright (C) 2003 The University of Manchester 
 * Copyright (C) 2003 The University of Karlsruhe
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
 * Filename           $RCSfile: RemoveDataPropertyRange.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/10/25 18:01:45 $
 *               by   $Author: aditkal $
 ****************************************************************/

package org.semanticweb.owl.model.change;

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;

/*
 * Extension to OWL API 1.2, date: 09-01-04, @author: Aditya Kalyanpur (MINDSWAP)
 */

public class RemoveDataPropertyRange extends OntologyChange{

	/**
     * The property to have the range removed
     *
     */
    private OWLDataProperty property;

    /**
     * The range being removed
     *
     */
    private OWLDataRange range;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param owlClass the OWL class
     * @param description the equivalent concept
     * @param cause a <code>OntologyChange</code> value
     */
    public RemoveDataPropertyRange( OWLOntology ontology,
			 OWLDataProperty property,
			 OWLDataRange range,
			 OntologyChange cause) {
	super( ontology, cause );
	this.property = property;
	this.range = range;
    }
    
    /**
     * The property.
     *
     * @return an <code>OWLProperty</code> value
     */
    public OWLDataProperty getProperty() {
	return property;
    }

    /**
     * The range.
     *
     * @return an <code>OWLConcreteDataType</code> value
     */
    public OWLDataRange getRange() {
	return range;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
	
}

/*
 * ChangeLog
 * $Log: RemoveDataPropertyRange.java,v $
 * Revision 1.2  2004/10/25 18:01:45  aditkal
 * Bringing code up to date
 *
 * Date: 09/01/04 aditya_kalyanpur
 * Added New Class to API
 *
 */