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
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * Remove a class axiom.
 *
 * @author Sean Bechhofer
 * @version $Id: RemoveClassAxiom.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class RemoveClassAxiom extends OntologyChange
{

    /**
     * The axiom to be removed.
     *
     */
    private OWLClassAxiom axiom;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param importOntology an <code>OWLEntity</code> value
     * @param cause a <code>OntologyChange</code> value
     */
    public RemoveClassAxiom( OWLOntology ontology,
		      OWLClassAxiom axiom,
		      OntologyChange cause) {
	super( ontology, cause );
	this.axiom = axiom;
    }

    /**
     * The axiom to be removed.
     *
     * @return an <code>OWLEntity</code> value
     */
    public OWLClassAxiom getAxiom() {
	return axiom;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
} // RemoveClassAxiom



/*
 * ChangeLog
 * $Log: RemoveClassAxiom.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/06/25 16:04:57  bechhofers
 * Added removal events
 *
 *
 */
