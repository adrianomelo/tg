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
 *
 ****************************************************************
 * Source code information
 * -----------------------
 * Filename           $RCSfile: AddDataPropertyRange.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2005/06/10 12:20:29 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model.change; 
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * Add a range constraint to a DataProperty.
 *
 *
 * Created: Fri Feb 07 17:23:16 2003
 *
 * @author Sean Bechhofer
 * @version $Id: AddDataPropertyRange.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class AddDataPropertyRange extends OntologyChange 
{
    /**
     * The property to have the range added
     *
     */
    private OWLDataProperty property;

    /**
     * The range being added
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
    public AddDataPropertyRange( OWLOntology ontology,
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
} // AddDataPropertyRange



/*
 * ChangeLog
 * $Log: AddDataPropertyRange.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.1  2003/05/19 12:48:35  seanb
 * Individual -> Object
 *
 * Revision 1.3  2003/04/10 12:09:47  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.2  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.1  2003/02/10 09:25:52  seanb
 * Addition of new change events.
 *
 */
