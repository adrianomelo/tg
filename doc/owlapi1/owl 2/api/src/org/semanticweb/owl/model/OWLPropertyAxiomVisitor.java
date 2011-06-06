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

package org.semanticweb.owl.model;

import java.io.Serializable;

/**
 *
* A Visitor for OWL entities objects. See <i>Design
 Patterns</i>, Gamma et. al. p.331 for a detailed description of the
 Visitor pattern. If you wish to define operations over the data
 structure (e.g. providing some kind of 3rd party representation of an
 OWL ontology), then you should implement this interface
 accordingly. For each concrete class <CODE>Concrete</CODE> in the
 OWLPropertyAxiom hierarchy, a function <CODE>visitConcrete(Concrete)</CODE>
 must be provided.

 <BR><BR> The expression can then be visited using the {@link OWLPropertyAxiom#accept(OWLPropertyAxiomVisitor) accept} method.

 <BR><BR> Use of the Visitor architecture allows us to add
 application-specific functionality without "tainting" the data
 structure. Be aware, though, that if the concrete subclasses of
 {@link OWLPropertyAxiom OWLPropertyAxiom} change, then the implementors of this
 interface may need to change accordingly.

 *
 * @author Sean Bechhofer
 * @version $Id: OWLPropertyAxiomVisitor.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $
 */

public interface OWLPropertyAxiomVisitor extends Serializable
{
    public void visit( OWLDataPropertyRangeAxiom axiom) throws OWLException;
    public void visit( OWLEquivalentPropertiesAxiom axiom ) throws OWLException;
    public void visit( OWLFunctionalPropertyAxiom axiom) throws OWLException;
    public void visit( OWLInverseFunctionalPropertyAxiom axiom) throws OWLException;
    public void visit( OWLInversePropertyAxiom axiom) throws OWLException;
    public void visit( OWLObjectPropertyRangeAxiom axiom) throws OWLException;
    public void visit( OWLPropertyDomainAxiom axiom) throws OWLException;
    public void visit( OWLSubPropertyAxiom axiom ) throws OWLException;
    public void visit( OWLSymmetricPropertyAxiom axiom) throws OWLException;
    public void visit( OWLTransitivePropertyAxiom axiom) throws OWLException;

}// OWLPropertyAxiomVisitor

/*
 * ChangeLog
 * $Log: OWLPropertyAxiomVisitor.java,v $
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
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 * 
 */
