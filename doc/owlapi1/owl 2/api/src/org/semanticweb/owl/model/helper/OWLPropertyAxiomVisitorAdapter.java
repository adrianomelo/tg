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

package org.semanticweb.owl.model.helper; // Generated package name

import org.semanticweb.owl.model.*;
import org.apache.log4j.Logger;

/**
 * Provides an empty implementation of the {@link OWLPropertyAxiomVisitor OWLPropertyAxiomVisitor} interface. Those wishing to implement a visitor over subsets of the data structure can extend this class and provide the methods they need. 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLPropertyAxiomVisitorAdapter.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $
 */

public class OWLPropertyAxiomVisitorAdapter implements OWLPropertyAxiomVisitor
{

    static Logger logger = Logger.getLogger(OWLPropertyAxiomVisitorAdapter.class);
    
    public void visit(OWLDataPropertyRangeAxiom node) throws OWLException {
	logger.debug("Default DataPropertyRange");
	return;
    }

    public void visit( OWLEquivalentPropertiesAxiom node ) throws OWLException
    {
	logger.debug("Default EquivalentProperties");
	return;
    }
    
    public void visit(OWLFunctionalPropertyAxiom node) throws OWLException {
	logger.debug("Default FunctionalProperty");
	return;
    }

    public void visit(OWLInverseFunctionalPropertyAxiom node) throws OWLException {
	logger.debug("Default InverseFunctionalProperty");
	return;
    }

    public void visit(OWLInversePropertyAxiom node) throws OWLException {
	logger.debug("Default InverseProperty");
	return;
    }

    public void visit(OWLObjectPropertyRangeAxiom node) throws OWLException {
	logger.debug("Default ObjectPropertyRange");
	return;
    }

    public void visit(OWLPropertyDomainAxiom node) throws OWLException {
	logger.debug("Default PropertyDomain");
	return;
    }

    public void visit( OWLSubPropertyAxiom node ) throws OWLException
    {
	logger.debug("Default SubProperty");
	return;
    }

    public void visit(OWLSymmetricPropertyAxiom node) throws OWLException {
	logger.debug("Default SymmetricProperty");
	return;
    }

    public void visit(OWLTransitivePropertyAxiom node) throws OWLException {
	logger.debug("Default TransitiveProperty");
	return;
    }
    
    
} // OWLPropertyAxiomVisitorAdapter



/*
 * ChangeLog
 * $Log: OWLPropertyAxiomVisitorAdapter.java,v $
 * Revision 1.3  2006/03/28 16:14:45  ronwalf
 * Merging mindswap changes to OWLApi.
 * Rough summary:
 * * 1.5 compatibility (rename enum variables)
 * * An option to turn on and off importing in OWLConsumer
 * * Bug fix to allow DataRange in more areas
 * * Giving Anonymous individuals an identifier
 *   * New factory method - getAnonOWLIndividual
 *   * getOWLIndividual no longer accepts 'null'
 *   * added getAnonId() and isAnon() to OWLIndividual
 * * Some work on the RDF serializer, but we have a complete rewrite in
 *   Swoop that I think is better (more flexible, results easier to read)
 * * Added Transitive, Functional, InverseFunctional, Inverse, and
 *   Symmetric PropertyAxioms (not sure why, will check)
 * * Added .equals and .hashcode for all OWLObjects
 * * Added a RemoveDataType change
 * * Patches to OntologyImpl for Entity removal
 * * Added OWLIndividualTypeAssertion
 * * Added OWL(Object|Data)Property(Domain|Range)Axiom
 * * Added OWL(Object|Data)PropertyInstance
 * * Added subclass index to OWLClassImpl (and getSubClasses(...) for
 *   OWLClass)
 * * Changes for Entity renaming
 *
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 *
 *
 */
