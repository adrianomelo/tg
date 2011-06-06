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
 * Remove an equivalence from a class description.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: RemoveEquivalentClass.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class RemoveEquivalentClass extends OntologyChange 
{
    /**
     * The class to have the equivalent removed.
     *
     */
    private OWLClass owlClass;

    /**
     * The description being removed.
     *
     */
    private OWLDescription description;

    /**
     * 
     *
     * @param ontology an <code>OWLOntology</code> value
     * @param owlClass the sub class
     * @param description the equivalence
     * @param cause a <code>OntologyChange</code> value
     */
    public RemoveEquivalentClass( OWLOntology ontology,
			       OWLClass owlClass,
			       OWLDescription description,
			       OntologyChange cause) {
	super( ontology, cause );
	this.owlClass = owlClass;
	this.description = description;
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
     * The equivalence
     */
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
} // RemoveEquivalentClass



/*
 * ChangeLog
 * $Log: RemoveEquivalentClass.java,v $
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
 * Revision 1.1  2003/06/25 16:04:57  bechhofers
 * Added removal events
 *
 *
 */
