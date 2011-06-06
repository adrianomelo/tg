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
 * Filename           $RCSfile: RemoveImport.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 09-02-2004
 *               by   $Author: aditkal $
 ****************************************************************/

package org.semanticweb.owl.model.change;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;

/**
 * @author UMD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RemoveImport extends OntologyChange
{

    /**
     * The entity to be added
     *
     */
    private OWLOntology importOntology;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param importOntology an <code>OWLEntity</code> value
     * @param cause a <code>OntologyChange</code> value
     */
    public RemoveImport( OWLOntology ontology,
		      OWLOntology importOntology,
		      OntologyChange cause) {
	super( ontology, cause );
	this.importOntology = importOntology;
    }

    /**
     * The entity that should be removed.
     *
     * @return an <code>OWLEntity</code> value
     */
    public OWLOntology getImportOntology() {
	return importOntology;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
} // RemoveImport

/*
 * ChangeLog
 * $Log: RemoveImport.java,v $
 * Revision 1.2  2004/10/25 18:01:45  aditkal
 * Bringing code up to date
 *
 * Date: 09/02/04 aditya_kalyanpur
 * Added New Class to API
 *
 */