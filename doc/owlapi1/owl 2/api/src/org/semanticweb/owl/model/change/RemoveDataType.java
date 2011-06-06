/*
 * Copyright (C) 2002 by the University of Manchester
 *
 * This software was written by Sean Bechhofer (Seanb@cs.man.ac.uk)
 * whilst at the University of Manchester
 *
 * The initial code base is copyright by the University
 * of Manchester. Modifications to the initial code base are copyright
 * of their respective authors, or their employers as appropriate. 
 * Authorship of the modifications may be determined from the ChangeLog
 * placed at the end of this file
 *
 * This software was developed as part of the OilEd editor. Further 
 * information, and the latest version can be found at 
 * http://oiled.man.ac.uk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA
 */

package org.semanticweb.owl.model.change; 
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import java.net.URI;
import org.semanticweb.owl.model.OWLDataType;

// Generated package name


/**
 * Remove a data type.
 *
 *
 * @author Ron Alford <ronwalf@volus.net>
 */

public class RemoveDataType extends OntologyChange
{

    /**
     * The entity to be removed
     *
     */
    private OWLDataType datatype;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param datatype an <code>OWLDataType</code> value
     * @param cause a <code>OntologyChange</code> value
     */
    public RemoveDataType( OWLOntology ontology,
		      OWLDataType datatype,
		      OntologyChange cause) {
	super( ontology, cause );
	this.datatype = datatype;
    }

    /**
     * The datatype that should be added.
     *
     * @return an <code>OWLDataType</code> value
     */
    public OWLDataType getDatatype() {
	return datatype;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
    
    public String toString() {
	try {
	    return "Remove: " + datatype.getURI() + " to " + ontology.getURI();
	} catch (OWLException ex) {
	    return super.toString();
	}
    }
} // RemoveDataType




