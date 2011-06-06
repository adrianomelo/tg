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
 * Filename           $RCSfile: SetFunctional.java,v $
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
 * Set the functional flag on a Property.
 *
 *
 * Created: Fri Feb 07 17:20:09 2003
 *
 * @author Sean Bechhofer
 * @version $Id: SetFunctional.java,v 1.1.1.1 2003/10/14 17:10:09 sean_bechhofer Exp $
 */

public class SetFunctional extends OntologyChange 
{
    /**
     * The property to be changed.
     *
     */
    private OWLProperty property;

    /**
     * The value 
     *
     */
    private boolean functional;

    /**
     * 
     *
     */
    public SetFunctional( OWLOntology ontology,
		      OWLProperty property,
		      boolean functional,
		      OntologyChange cause) {
	super( ontology, cause );
	this.property = property;
	this.functional = functional;
    }
    
    /**
     * The entity that should be added.
     *
     * @return an <code>OWLEntity</code> value
     */
    public OWLProperty getProperty() {
	return property;
    }

    public boolean isFunctional() {
	return functional;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
} // SetFunctional



/*
 * ChangeLog
 * $Log: SetFunctional.java,v $
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
