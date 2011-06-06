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
 * Filename           $RCSfile: RemoveDataPropertyInstance.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/10/25 18:01:45 $
 *               by   $Author: aditkal $
 ****************************************************************/

package org.semanticweb.owl.model.change;

import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author UMD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveDataPropertyInstance extends OntologyChange 
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
    public RemoveDataPropertyInstance( OWLOntology ontology,
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
} // RemoveDataPropertyInstance


/*
 * ChangeLog
 * $Log: RemoveDataPropertyInstance.java,v $
 * Revision 1.2  2004/10/25 18:01:45  aditkal
 * Bringing code up to date
 *
 *
 * Date: 09/01/04 aditya_kalyanpur
 * Added New Class to API
 *
 */
