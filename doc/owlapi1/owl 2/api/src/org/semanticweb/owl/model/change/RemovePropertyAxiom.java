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
 * Filename           $RCSfile: RemovePropertyAxiom.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 09-01-2004
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model.change;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLPropertyAxiom;

public class RemovePropertyAxiom extends OntologyChange
{

    /**
     * The entity to be removed
     *
     */
    private OWLPropertyAxiom axiom;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param importOntology an <code>OWLEntity</code> value
     * @param cause a <code>OntologyChange</code> value
     */
    public RemovePropertyAxiom( OWLOntology ontology,
		      OWLPropertyAxiom axiom,
		      OntologyChange cause) {
	super( ontology, cause );
	this.axiom = axiom;
    }

    /**
     * The entity that should be removed.
     *
     * @return an <code>OWLEntity</code> value
     */
    public OWLPropertyAxiom getAxiom() {
	return axiom;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
} // RemovePropertyAxiom

/*
 * ChangeLog
 * $Log: RemovePropertyAxiom.java,v $
 * Revision 1.3  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/10/25 18:01:45  aditkal
 * Bringing code up to date
 *
 * Date: 09/01/04 aditya_kalyanpur
 * Added New Class to API
 *
 */
