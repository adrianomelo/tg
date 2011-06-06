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
import org.semanticweb.owl.model.OWLEnumeration;

// Generated package name


/**
 * Remove an assertion that a class is equivalent to an enumeration.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: RemoveEnumeration.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class RemoveEnumeration extends OntologyChange 
{
    /**
     * The class 
     *
     */
    private OWLClass owlClass;

    /**
     * The enumeration being removed
     *
     */
    private OWLEnumeration enumeration;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param owlClass the OWL class
     * @param enumeration the enumeration
     * @param cause a <code>OntologyChange</code> value
     */
    public RemoveEnumeration( OWLOntology ontology,
			   OWLClass owlClass,
			   OWLEnumeration enumeration,
			   OntologyChange cause) {
	super( ontology, cause );
	this.owlClass = owlClass;
	this.enumeration = enumeration;
    }

    /**
     * The class
     *
     * @return an <code>OWLEntity</code> value
     */
    public OWLClass getOWLClass() {
	return owlClass;
    }

    /** 
     * The enumeration
     */
    public OWLEnumeration getEnumeration() {
	return enumeration;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
} // RemoveEnumeration



/*
 * ChangeLog
 * $Log: RemoveEnumeration.java,v $
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
