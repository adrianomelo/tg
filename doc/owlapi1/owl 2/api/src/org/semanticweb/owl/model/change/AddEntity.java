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
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import java.net.URI;

// Generated package name


/**
 * Add a new Entity.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: AddEntity.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class AddEntity extends OntologyChange
{

    /**
     * The entity to be added
     *
     */
    private OWLEntity entity;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param entity an <code>OWLEntity</code> value
     * @param cause a <code>OntologyChange</code> value
     */
    public AddEntity( OWLOntology ontology,
		      OWLEntity entity,
		      OntologyChange cause) {
	super( ontology, cause );
	this.entity = entity;
    }

    /**
     * The entity that should be added.
     *
     * @return an <code>OWLEntity</code> value
     */
    public OWLEntity getEntity() {
	return entity;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    public String toString() {
	try {
	    return "Add: [" + entity.getClass() + "] " + 
		entity.getURI() + " to " + ontology.getURI();
	} catch (OWLException ex) {
	    return super.toString();
	}
    }
} // AddEntity



/*
 * ChangeLog
 * $Log: AddEntity.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.3  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.2  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
