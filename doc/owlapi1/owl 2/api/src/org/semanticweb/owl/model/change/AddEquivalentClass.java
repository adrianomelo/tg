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
import java.util.HashSet;

import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;

// Generated package name


/**
 * Add an equivalence to a class description.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: AddEquivalentClass.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class AddEquivalentClass extends OntologyChange 
{
    /**
     * The class to have the superclass added
     *
     */
    private OWLClass owlClass;

    /**
     * The superclass being added
     *
     */
    private OWLDescription description;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param owlClass the sub class
     * @param description the super concept
     * @param cause a <code>OntologyChange</code> value
     */
    public AddEquivalentClass( OWLOntology ontology,
			       OWLClass owlClass,
			       OWLDescription description,
			       OntologyChange cause) {
	super( ontology, cause );
	this.owlClass = owlClass;
	this.description = description;
    }

    /**
     * The entity that should be added.
     *
     * @return an <code>OWLEntity</code> value
     */
    public OWLClass getOWLClass() {
	return owlClass;
    }

    public OWLDescription getDescription() {
	return description;
    }

    public void accept( ChangeVisitor visitor ) throws OWLException {
	visitor.visit( this );
    }

    /* I suspect this is no longer necessary. SKB */
//     public OWLEquivalentClassesAxiom getAxiom() {
// 	OWLDataFactoryImpl f = (OWLDataFactoryImpl) owlClass.getOWLDataFactory();
// 	HashSet set = new HashSet();
// 	set.add(owlClass);
// 	set.add(description);
// 	return new OWLEquivalentClassesAxiomImpl(f, set);
//     }
} // AddEquivalentClass



/*
 * ChangeLog
 * $Log: AddEquivalentClass.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.6  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.5  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.4  2003/05/08 07:54:34  seanb
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
