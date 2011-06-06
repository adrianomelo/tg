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
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLProperty;

// Generated package name


/**
 * Add a superclass assertion to a class.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: AddSuperProperty.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class AddSuperProperty extends OntologyChange 
{
    /**
     * The class to have the superclass added
     *
     */
    private OWLProperty property;

    /**
     * The superclass being added
     *
     */
    private OWLProperty superProperty;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param owlClass the OWL class
     * @param description the equivalent concept
     * @param cause a <code>OntologyChange</code> value
     */
    public AddSuperProperty( OWLOntology ontology,
			     OWLProperty property,
			     OWLProperty superProperty,
			     OntologyChange cause) {
	super( ontology, cause );
	this.property = property;
	this.superProperty = superProperty;
    }

    /**
     * The property that's being acted on.
     *
     * @return an <code>OWLProperty</code> value
     */
    public OWLProperty getProperty() {
	return property;
    }

    public OWLProperty getSuperProperty() {
	return superProperty;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }
    
} // AddSuperProperty



/*
 * ChangeLog
 * $Log: AddSuperProperty.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.2  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.1  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.3  2003/03/31 16:55:12  seanb
 * Various updates and fixes to parser.
 * Adding inverses, domain and range.
 *
 * Revision 1.2  2003/03/26 19:04:02  rvolz
 * *** empty log message ***
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
