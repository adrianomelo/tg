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
 * Filename           $RCSfile: AddDomain.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:09 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model.change; 
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;

// Generated package name


/**
 * Add a domain constraint to a property.
 *
 *
 * Created: Fri Feb 07 17:20:09 2003
 *
 * @author Sean Bechhofer
 * @version $Id: AddDomain.java,v 1.1.1.1 2003/10/14 17:10:09 sean_bechhofer Exp $
 */

public class AddDomain extends OntologyChange 
{
    /**
     * The property to have the domain added
     *
     */
    private OWLProperty property;

    /**
     * The domain being added
     *
     */
    private OWLDescription domain;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param owlClass the OWL class
     * @param description the equivalent concept
     * @param cause a <code>OntologyChange</code> value
     */
    public AddDomain( OWLOntology ontology,
		      OWLProperty property,
		      OWLDescription domain,
		      OntologyChange cause) {
	super( ontology, cause );
	this.property = property;
	this.domain = domain;
    }
    
    /**
     * The entity that should be added.
     *
     * @return an <code>OWLEntity</code> value
     */
    public OWLProperty getProperty() {
	return property;
    }

    public OWLDescription getDomain() {
	return domain;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
} // AddDomain



/*
 * ChangeLog
 * $Log: AddDomain.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/02/10 15:09:52  seanb
 * Tidying up and extra event testing.
 *
 * Revision 1.1  2003/02/10 09:25:52  seanb
 * Addition of new change events.
 *
 */
