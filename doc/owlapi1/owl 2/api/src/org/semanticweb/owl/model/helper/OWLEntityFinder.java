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
 * Filename           $RCSfile: OWLEntityFinder.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package org.semanticweb.owl.model.helper; 
import org.semanticweb.owl.model.*;

import java.util.Set;
// Dubious import....
import java.util.Iterator;
import java.util.HashSet;

import org.apache.log4j.Logger;

// Generated package name


/**
 * Looks for the occurrence of a particular OWLEntity.
 *
 * @author Sean Bechhofer
 * @version $Id: OWLEntityFinder.java,v 1.2 2006/03/28 16:14:45 ronwalf Exp $
 */

public class OWLEntityFinder extends OWLObjectVisitorAdapter
{

    /* Simply trawls through expressions and stops when it can. */
    static Logger logger = Logger.getLogger(OWLEntityFinder.class);

    private OWLEntity entity;
    private boolean found;
    
    public OWLEntityFinder( OWLEntity _entity )
    {
	entity = _entity;
	reset();
    }

    /** Reset the collector */
    public void reset() {
	found = false;
    }

    /** Returns the entities collected */
    public boolean found() {
	return found;
    }
    
    public void visit( OWLAnd node ) throws OWLException
    {
	for ( Iterator it = node.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		/* We've found it and we're done */
		return;
	    }
	    /* Otherwise, we carry on looking. Oooh Matron! */
	}
    }
    
    public void visit( OWLDataAllRestriction node ) throws OWLException
    {
	node.getDataProperty().accept( this );	
    }
    
    public void visit( OWLDataCardinalityRestriction node ) throws OWLException
    {
	node.getDataProperty().accept( this );
    }
    
    public void visit( OWLDataProperty node ) throws OWLException
    {
	found = ( node == entity );
    }
    
    public void visit( OWLDataSomeRestriction node ) throws OWLException
    {
	node.getDataProperty().accept( this );
    }
    
    public void visit( OWLDataValueRestriction node ) throws OWLException
    {
	node.getDataProperty().accept( this );
    }
    
    public void visit( OWLDifferentIndividualsAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept ( this );
	    if (found) {
		return;
	    }
	}
    }
    
    public void visit( OWLDisjointClassesAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getDisjointClasses().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		/* We've found it and we're done */
		return;
	    }
	}
    }
    
    public void visit( OWLEquivalentClassesAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getEquivalentClasses().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		/* We've found it and we're done */
		return;
	    }
	}
    }
    
    public void visit( OWLEquivalentPropertiesAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getProperties().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		return;
	    }
	}
    }
    
    public void visit( OWLFrame node ) throws OWLException
    {
	for ( Iterator it = node.getRestrictions().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		/* We've found it and we're done */
		return;
	    }	    
	}
	for ( Iterator it = node.getSuperclasses().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		/* We've found it and we're done */
		return;
	    }	    
	}
    }
    
    public void visit( OWLIndividual node ) throws OWLException
    {
	found = ( node == entity );
    }

//     public void visit( OWLAnonymousIndividual node ) throws OWLException
//     {
//     }
    
    public void visit( OWLObjectAllRestriction node ) throws OWLException
    {
	node.getObjectProperty().accept( this );
	if ( found ) {
	    /* We've found it and we're done */
	    return;
	}
	/* Carry on looking. */
	node.getDescription().accept( this );
    }
    
    public void visit( OWLObjectCardinalityRestriction node ) throws OWLException
    {
	node.getObjectProperty().accept( this );
    }
    
    public void visit( OWLObjectProperty node ) throws OWLException
    {
	found = ( node == entity );
    }
    
    public void visit( OWLObjectSomeRestriction node ) throws OWLException
    {
	node.getObjectProperty().accept( this );
	if ( found ) {
	    /* We've found it and we're done */
	    return;
	}
	/* Carry on looking. */
	node.getDescription().accept( this );
    }
    
    public void visit( OWLObjectValueRestriction node ) throws OWLException
    {
	node.getObjectProperty().accept( this );
	if ( found ) {
	    /* We've found it and we're done */
	    return;
	}
	/* Carry on looking. */
	node.getIndividual().accept( this );
    }
    
    public void visit( OWLNot node ) throws OWLException
    {
	node.getOperand().accept( this );
    }
    
    public void visit( OWLOntology node ) throws OWLException
    {
	/* Not so sure about this one.... */
	if ( node == entity ) {
	    found = true;
	} else {
// 	    Set allOntologies = OntologyHelper.importClosure( node );
// 	    logger.debug( node.getURI() );
// 	    for (Iterator it = allOntologies.iterator();
// 		 it.hasNext(); ) {
// 		OWLOntology onto = (OWLOntology) it.next();
// 		for ( Iterator cit = onto.getClasses().iterator();
// 		      cit.hasNext(); ) {
// 		    Object o = cit.next();
// 		    entities.add( o );
// 		    logger.debug( o );
// 		}
// 		for ( Iterator cit = onto.getIndividuals().iterator();
// 		      cit.hasNext(); ) {
// 		    Object o = cit.next();
// 		    entities.add( o );
// 		logger.debug( o );
// 		}
// 		for ( Iterator cit = onto.getObjectProperties().iterator();
// 		      cit.hasNext(); ) {
// 		    Object o = cit.next();
// 		    entities.add( o );
// 		    logger.debug( o );
// 		}
// 		for ( Iterator cit = onto.getDataProperties().iterator();
// 		      cit.hasNext(); ) {
// 		    Object o = cit.next();
// 		    entities.add( o );
// 		    logger.debug( o );
// 		}
// 	    }
 	}
    }
    
    public void visit( OWLOr node ) throws OWLException
    {
	for ( Iterator it = node.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		return;
	    }
	}
    }
    
    public void visit( OWLClass node ) throws OWLException
    {
	found = ( node == entity );
    }
    
    public void visit( OWLEnumeration node ) throws OWLException
    {
	for ( Iterator it = node.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		/* We've found it and we're done */
		return;
	    }
	}
    }
    
    public void visit( OWLSameIndividualsAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	    if (found) {
		/* We've found it and we're done */
		return;
	    }
	}
    }
    
    public void visit( OWLSubClassAxiom node ) throws OWLException
    {
	node.getSubClass().accept( this );
	if (found) {
	    return;
	}
	node.getSuperClass().accept( this );
    }
    
    public void visit( OWLSubPropertyAxiom node ) throws OWLException
    {
	node.getSubProperty().accept( this );
	if (found) {
	    return;
	}
	node.getSuperProperty().accept( this );
    }

    public void visit( OWLAnnotationProperty node ) throws OWLException
    {
	found = ( node == entity );
    }

    public void visit( OWLAnnotationInstance node ) throws OWLException 
    {
	node.getProperty().accept( this );
    }

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLFunctionalPropertyAxiom)
	 */
	public void visit(OWLFunctionalPropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom)
	 */
	public void visit(OWLInverseFunctionalPropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLTransitivePropertyAxiom)
	 */
	public void visit(OWLTransitivePropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLSymmetricPropertyAxiom)
	 */
	public void visit(OWLSymmetricPropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLInversePropertyAxiom)
	 */
	public void visit(OWLInversePropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLPropertyDomainAxiom)
	 */
	public void visit(OWLPropertyDomainAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom)
	 */
	public void visit(OWLObjectPropertyRangeAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLDataPropertyRangeAxiom)
	 */
	public void visit(OWLDataPropertyRangeAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLObjectPropertyInstance)
	 */
	public void visit(OWLObjectPropertyInstance node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLDataPropertyInstance)
	 */
	public void visit(OWLDataPropertyInstance node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLIndividualTypeAssertion)
	 */
	public void visit(OWLIndividualTypeAssertion node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

    
} // OWLEntityFinder



/*
 * ChangeLog
 * $Log: OWLEntityFinder.java,v $
 * Revision 1.2  2006/03/28 16:14:45  ronwalf
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
 * Revision 1.1  2004/07/09 12:07:47  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 *
 */
