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
 * Filename           $RCSfile: OWLEntityCollector.java,v $
 * Revision           $Revision: 1.3 $
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
 * Collects all the OWLEntities contained in this object. For example
 * will return all the individuals that are referred to in a
 * particular enumeration. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLEntityCollector.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $
 */

public class OWLEntityCollector extends OWLObjectVisitorAdapter
{
    static Logger logger = Logger.getLogger(OWLEntityCollector.class);

    private Set entities;
    
    public OWLEntityCollector()
    {
	reset();
    }

    /** Reset the collector */
    public void reset() {
	/* Requires an implementation class :-((( */
	entities = new HashSet();
    }

    /** Returns the entities collected */
    public Set entities() {
	return new HashSet( entities );
    }
    
    public void visit( OWLAnd node ) throws OWLException
    {
	for ( Iterator it = node.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLDataAllRestriction node ) throws OWLException
    {
	entities.add( node.getDataProperty() );
    }
    
    public void visit( OWLDataCardinalityRestriction node ) throws OWLException
    {
	entities.add( node.getDataProperty() );
    }
    
    public void visit( OWLDataProperty node ) throws OWLException
    {
	entities.add( node );
    }
    
    public void visit( OWLDataSomeRestriction node ) throws OWLException
    {
	entities.add( node.getDataProperty() );
    }
    
    public void visit( OWLDataValueRestriction node ) throws OWLException
    {
	entities.add( node.getDataProperty() );
    }
    
    public void visit( OWLDifferentIndividualsAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLDisjointClassesAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getDisjointClasses().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLEquivalentClassesAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getEquivalentClasses().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLEquivalentPropertiesAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getProperties().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLFrame node ) throws OWLException
    {
	for ( Iterator it = node.getRestrictions().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
	for ( Iterator it = node.getSuperclasses().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLIndividual node ) throws OWLException
    {
	entities.add( node );
    }

//     public void visit( OWLAnonymousIndividual node ) throws OWLException
//     {
//     }
    
    public void visit( OWLObjectAllRestriction node ) throws OWLException
    {
	entities.add( node.getObjectProperty() );
	node.getDescription().accept( this );
    }
    
    public void visit( OWLObjectCardinalityRestriction node ) throws OWLException
    {
	entities.add( node.getObjectProperty() );
    }
    
    public void visit( OWLObjectProperty node ) throws OWLException
    {
	entities.add( node );
    }
    
    public void visit( OWLObjectSomeRestriction node ) throws OWLException
    {
	entities.add( node.getObjectProperty() );	
	node.getDescription().accept( this );
    }
    
    public void visit( OWLObjectValueRestriction node ) throws OWLException
    {
	entities.add( node.getObjectProperty() );
	entities.add( node.getIndividual() );
    }
    
    public void visit( OWLNot node ) throws OWLException
    {
	node.getOperand().accept( this );
    }
    
    public void visit( OWLOntology node ) throws OWLException
    {
	Set allOntologies = OntologyHelper.importClosure( node );
	logger.debug( node.getURI() );
	for (Iterator it = allOntologies.iterator();
	     it.hasNext(); ) {
	    OWLOntology onto = (OWLOntology) it.next();
	    for ( Iterator cit = onto.getClasses().iterator();
		  cit.hasNext(); ) {
		Object o = cit.next();
		entities.add( o );
		logger.debug( o );
	    }
	    for ( Iterator cit = onto.getIndividuals().iterator();
		  cit.hasNext(); ) {
		Object o = cit.next();
		entities.add( o );
		logger.debug( o );
	    }
	    for ( Iterator cit = onto.getObjectProperties().iterator();
		  cit.hasNext(); ) {
		Object o = cit.next();
		entities.add( o );
		logger.debug( o );
	    }
	    for ( Iterator cit = onto.getDataProperties().iterator();
		  cit.hasNext(); ) {
		Object o = cit.next();
		entities.add( o );
		logger.debug( o );
	    }
	}
    }
    
    public void visit( OWLOr node ) throws OWLException
    {
	for ( Iterator it = node.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLClass node ) throws OWLException
    {
	entities.add( node );
    }
    
    public void visit( OWLEnumeration node ) throws OWLException
    {
	for ( Iterator it = node.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLSameIndividualsAxiom node ) throws OWLException
    {
	for ( Iterator it = node.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLObject oo = (OWLObject) it.next();
	    oo.accept( this );
	}
    }
    
    public void visit( OWLSubClassAxiom node ) throws OWLException
    {
	node.getSubClass().accept( this );
	node.getSuperClass().accept( this );
    }
    
    public void visit( OWLSubPropertyAxiom node ) throws OWLException
    {
	node.getSubProperty().accept( this );
	node.getSuperProperty().accept( this );
    }

    public void visit( OWLAnnotationInstance node ) throws OWLException 
    {
	node.getProperty().accept( this );
	/* TODO: Probably also need to grab an individual? */
    }

    public void visit( OWLAnnotationProperty node ) throws OWLException
    {
	entities.add( node );
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
    

    
} // OWLEntityCollector



/*
 * ChangeLog
 * $Log: OWLEntityCollector.java,v $
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
 * Revision 1.2  2004/07/09 14:04:58  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/04/10 12:10:56  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.4  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.3  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.2  2003/02/17 18:23:54  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.1  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 */
