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

import java.io.Serializable;

import org.semanticweb.owl.model.OWLException;


/**
 * <code>ChangeVisitors</code> are responsible for executing {@link OntologyChange OntologyChange} events. 
 *
 *
 * @author Sean Bechhofer.
 * @version $Id: ChangeVisitor.java,v 1.6 2006/03/28 16:14:45 ronwalf Exp $
 */

public interface ChangeVisitor extends Serializable {
    
    /* Load of visit methods for various things */

    /**
     * 
     *
     * @param event an <code>AddAnnotationInstance</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddAnnotationInstance event ) throws OWLException;

    /**
     * 
     *
     * @param event an <code>AddEntity</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddEntity event ) throws OWLException;

    /**
     * 
     *
     * @param event an <code>AddDataType</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddDataType event ) throws OWLException;

    /**
     * 
     *
     * @param event a <code>RemoveEntity</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( RemoveEntity event ) throws OWLException;

    /**
     * 
     * @param event a <code>RemoveDataType</code> value
     * @exception OWLException if an error occurs
     */
	public void visit(RemoveDataType event) throws OWLException;
	
    /**
     * 
     *
     * @param event an <code>AddImport</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddImport event ) throws OWLException;
    /**
     * 
     *
     * @param event an <code>AddIndividualAxiom</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddIndividualAxiom event ) throws OWLException;

    public void visit( AddClassAxiom event ) throws OWLException;
    /**
     * 
     *
     * @param event an <code>AddPropertyAxiom</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddPropertyAxiom event ) throws OWLException;
    public void visit( AddSuperClass event ) throws OWLException;
    public void visit( AddSuperProperty event ) throws OWLException;
    public void visit( AddIndividualClass event ) throws OWLException;
    /**
     * 
     *
     * @param event an <code>AddEquivalentClass</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddEquivalentClass event ) throws OWLException;
    /**
     * 
     *
     * @param event an <code>AddEnumeration</code> value
     * @exception OWLException if an error occurs
     */
    public void visit( AddEnumeration event ) throws OWLException;

    public void visit( AddDomain event ) throws OWLException;
    public void visit( AddDataPropertyRange event ) throws OWLException;
    public void visit( AddObjectPropertyRange event ) throws OWLException;
    public void visit( AddInverse event ) throws OWLException;
    public void visit( SetFunctional event ) throws OWLException;
    public void visit( SetTransitive event ) throws OWLException;
    public void visit( SetSymmetric event ) throws OWLException;
    public void visit( SetInverseFunctional event ) throws OWLException;
    public void visit( SetOneToOne event ) throws OWLException;
    public void visit( SetDeprecated event ) throws OWLException;
    public void visit( AddObjectPropertyInstance event ) throws OWLException;
    public void visit( AddDataPropertyInstance event ) throws OWLException; 

    public void visit( RemoveClassAxiom event ) throws OWLException;
    public void visit( RemoveSuperClass event ) throws OWLException;
    public void visit( RemoveEquivalentClass event ) throws OWLException;
    public void visit( RemoveEnumeration event ) throws OWLException;

    public void visit( SetLogicalURI event ) throws OWLException;

    public void visit(RemoveDomain event) throws OWLException;
	public void visit(RemoveDataPropertyRange event) throws OWLException;
	public void visit(RemoveObjectPropertyRange event) throws OWLException;
	public void visit(RemovePropertyAxiom event) throws OWLException;
	public void visit(RemoveIndividualAxiom event) throws OWLException;
	public void visit(RemoveDataPropertyInstance event) throws OWLException;
	public void visit(RemoveObjectPropertyInstance event) throws OWLException;
	public void visit(RemoveSuperProperty event) throws OWLException;
	public void visit(RemoveAnnotationInstance event) throws OWLException;
	public void visit(RemoveImport event) throws OWLException;
	public void visit(RemoveIndividualClass event) throws OWLException;
	public void visit(RemoveInverse event) throws OWLException;
    
}// ChangeVisitor

/*
 * ChangeLog
 * $Log: ChangeVisitor.java,v $
 * Revision 1.6  2006/03/28 16:14:45  ronwalf
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
 * Revision 1.5  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.4  2004/10/25 18:01:44  aditkal
 * Bringing code up to date
 *
 * 09-14-04 aditya_kalyanpur
 * Addition of visit()->RemoveInverse
 * 
 * 09-02-04 aditya_kalyanpur
 * Addition of visit()->RemoveAnnotationInstance, RemovePropertyAxiom (handling only equivalent properties),
 * RemoveIndividualClass and RemoveImport
 * 
 * 09-01-04 aditya_kalyanpur
 * Addition of visit()->RemoveDomain, RemoveDataPropertyRange
 * RemoveObjectPropertyRange, RemovePropertyAxiom, RemoveSuperProperty, 
 * RemoveIndividualAxiom, RemoveDataPropertyInstance, RemoveObjectPropertyInstance
 * 
 * Revision 1.2  2003/12/11 12:59:45  sean_bechhofer
 * Addition of setLogicalURI change event for setting the URI of an
 * Ontology.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.7  2003/06/25 16:04:57  bechhofers
 * Added removal events
 *
 * Revision 1.6  2003/05/19 12:48:35  seanb
 * Individual -> Object
 *
 * Revision 1.5  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.4  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.3  2003/02/11 17:24:22  seanb
 * Adding new change event for individual type.
 *
 * Revision 1.2  2003/02/10 09:23:06  seanb
 * Changes to cardinality, addition of property instances.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
