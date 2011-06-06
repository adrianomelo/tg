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

package org.semanticweb.owl.model.change; 
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLDataValue;

// Generated package name


/**
 * Add a superclass assertion to a class.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: AddDataPropertyInstance.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class AddDataPropertyInstance extends OntologyChange 
{
    /**
     * The subject
     *
     */
    private OWLIndividual subject;

    /**
     * The object
     *
     */
    private OWLDataValue object;

    /**
     * The property 
     *
     */
    private OWLDataProperty property;

    /**
     * 
     *
     */
    public AddDataPropertyInstance( OWLOntology ontology,
				      OWLIndividual subject,
				      OWLDataProperty property,
				      OWLDataValue object,
				      OntologyChange cause) {
	super( ontology, cause );
	this.subject = subject;
	this.property = property;
	this.object = object;
    }

    public OWLIndividual getSubject() {
	return subject;
    }
    public OWLDataValue getObject() {
	return object;
    }

    public OWLDataProperty getProperty() {
	return property;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
} // AddDataPropertyInstance



/*
 * ChangeLog
 * $Log: AddDataPropertyInstance.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/05/19 12:48:35  seanb
 * Individual -> Object
 *
 * Revision 1.3  2003/04/10 12:09:47  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.2  2003/02/10 17:09:01  seanb
 * Further tests. Adding logging to ChangeVisitorAdapter.
 *
 * Revision 1.1  2003/02/10 09:25:52  seanb
 * Addition of new change events.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
