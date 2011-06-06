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
* A Visitor for OWL data structure objects. See <i>Design
 Patterns</i>, Gamma et. al. p.331 for a detailed description of the
 Visitor pattern. If you wish to define operations over the data
 structure (e.g. providing some kind of 3rd party representation of an
 OWL ontology), then you should implement this interface
 accordingly. For each concrete class <CODE>Concrete</CODE> in the
 OWLObject hierarchy, a function <CODE>visit(Concrete)</CODE>
 must be provided.

 <BR><BR> The expression can then be visited using the {@link OWLObject#accept(OWLObjectVisitor) accept} method.

 <BR><BR> Use of the Visitor architecture allows us to add
 application-specific functionality without "tainting" the data
 structure. Be aware, though, that if the concrete subclasses of
 {@link OWLObject OWLObject} change, then the implementors of this
 interface may need to change accordingly.

 *
 * @author Sean Bechhofer 
 * @version $Id: OWLObjectVisitor.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $ 
 */

public interface OWLObjectVisitor extends Serializable
{

    public void visit( OWLAnd node ) throws OWLException;
    public void visit( OWLAnnotationProperty node ) throws OWLException;
    public void visit( OWLAnnotationInstance node ) throws OWLException;
   //    public void visit( OWLClassAxiom node ) throws OWLException;
    public void visit( OWLDataValue node ) throws OWLException;
    public void visit( OWLDataType node ) throws OWLException;
    public void visit( OWLDataEnumeration node ) throws OWLException;
    public void visit( OWLDataAllRestriction node ) throws OWLException;
    public void visit( OWLDataCardinalityRestriction node ) throws OWLException;
    public void visit( OWLDataProperty node ) throws OWLException;
    public void visit( OWLDataSomeRestriction node ) throws OWLException;
    public void visit( OWLDataValueRestriction node ) throws OWLException;
    public void visit( OWLDifferentIndividualsAxiom node ) throws OWLException;
    public void visit( OWLDisjointClassesAxiom node ) throws OWLException;
    public void visit( OWLEquivalentClassesAxiom node ) throws OWLException;
    public void visit( OWLEquivalentPropertiesAxiom node ) throws OWLException;
    public void visit( OWLFrame node ) throws OWLException;
    public void visit( OWLIndividual node ) throws OWLException;    
    /*    public void visit( OWLAnonymousIndividual node ) throws OWLException;     */
    public void visit( OWLObjectAllRestriction node ) throws OWLException;
    public void visit( OWLObjectCardinalityRestriction node ) throws OWLException;
    public void visit( OWLObjectProperty node ) throws OWLException;
    public void visit( OWLObjectSomeRestriction node ) throws OWLException;
    public void visit( OWLObjectValueRestriction node ) throws OWLException;
    public void visit( OWLNot node ) throws OWLException;
    //    public void visit( OWLNothing node ) throws OWLException;
    public void visit( OWLOntology node ) throws OWLException;
    public void visit( OWLOr node ) throws OWLException;
    public void visit( OWLClass node ) throws OWLException;
    public void visit( OWLEnumeration node ) throws OWLException;
    public void visit( OWLSameIndividualsAxiom node ) throws OWLException;
    public void visit( OWLSubClassAxiom node ) throws OWLException;
    public void visit( OWLSubPropertyAxiom node ) throws OWLException;
    //    public void visit( OWLThing node ) throws OWLException;
    
    public void visit( OWLFunctionalPropertyAxiom node ) throws OWLException;
    public void visit( OWLInverseFunctionalPropertyAxiom node ) throws OWLException;
    public void visit( OWLTransitivePropertyAxiom node ) throws OWLException;
    public void visit( OWLSymmetricPropertyAxiom node ) throws OWLException;
    public void visit( OWLInversePropertyAxiom node ) throws OWLException;
    public void visit( OWLPropertyDomainAxiom node ) throws OWLException;
    public void visit( OWLObjectPropertyRangeAxiom node ) throws OWLException;
    public void visit( OWLDataPropertyRangeAxiom node ) throws OWLException;
    public void visit( OWLObjectPropertyInstance node ) throws OWLException;
    public void visit( OWLDataPropertyInstance node ) throws OWLException;
    public void visit( OWLIndividualTypeAssertion node ) throws OWLException;
    
}// OWLObjectVisitor

/*
 * ChangeLog
 * $Log: OWLObjectVisitor.java,v $
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
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.8  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.7  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.6  2003/04/10 12:08:50  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.5  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.4  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 * Revision 1.3  2003/02/06 10:27:33  seanb
 * Replacing OWLThing and OWLNothing with specific instances of OWLClass.
 *
 * Revision 1.2  2003/01/29 16:10:51  seanb
 * Changes to support Anonymous Individuals.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 *
 */
