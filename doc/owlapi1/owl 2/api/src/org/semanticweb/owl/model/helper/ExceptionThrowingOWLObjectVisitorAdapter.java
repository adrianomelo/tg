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
 * Provides an implementation of the {@link OWLObjectVisitor OWLObjectVisitor} interface that simply throws an exception for any visit. This can be then be used in situations where the implementation expects to be dealing with a particular subset of the OWLObject hierarchy but wants to make sure that anything odd throws an exception. 
 *
 * @author Sean Bechhofer
 * @version $Id: ExceptionThrowingOWLObjectVisitorAdapter.java,v 1.3 2006/03/28 16:14:45 ronwalf Exp $
 */

public class ExceptionThrowingOWLObjectVisitorAdapter implements OWLObjectVisitor
{

    static Logger logger = Logger.getLogger(OWLObjectVisitorAdapter.class);

    private OWLException exception;

    public ExceptionThrowingOWLObjectVisitorAdapter() {
	this.exception = new OWLException( "ExceptionThrowingOWLObjectVisitorAdapter: No Implementation Provided" );
    }
    
    /**
     * Creates a new <code>ExceptionThrowingOWLObjectVisitorAdapter</code> instance.
     *
     * @param message The message to use in the thrown execption
     */
    public ExceptionThrowingOWLObjectVisitorAdapter( String message ) {
	this.exception = new OWLException( message );
    }
    
    /**
     * Creates a new <code>ExceptionThrowingOWLObjectVisitorAdapter</code> instance.
     *
     * @param exception an <code>OWLException</code> that should be thrown when required.
     */
    public ExceptionThrowingOWLObjectVisitorAdapter( OWLException exception ) {
	this.exception = exception;
    }

    public void visit( OWLAnd node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLAnnotationProperty node ) throws OWLException
    {
	throw exception;
    }

    public void visit( OWLAnnotationInstance node ) throws OWLException
    {
	throw exception;
    }

    public void visit( OWLClassAxiom node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDataValue node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDataType node ) throws OWLException
    {
	throw exception;
    }

    public void visit( OWLDataEnumeration node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDataAllRestriction node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDataCardinalityRestriction node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDataProperty node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDataSomeRestriction node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDataValueRestriction node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDifferentIndividualsAxiom node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLDisjointClassesAxiom node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLEquivalentClassesAxiom node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLEquivalentPropertiesAxiom node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLFrame node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLIndividual node ) throws OWLException
    {
	throw exception;
    }

    public void visit( OWLObjectAllRestriction node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLObjectCardinalityRestriction node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLObjectProperty node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLObjectSomeRestriction node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLObjectValueRestriction node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLNot node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLOntology node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLOr node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLClass node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLEnumeration node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLSameIndividualsAxiom node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLSubClassAxiom node ) throws OWLException
    {
	throw exception;
    }
    
    public void visit( OWLSubPropertyAxiom node ) throws OWLException
    {
	throw exception;
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
    
} // OWLObjectVisitorAdapter



/*
 * ChangeLog
 * $Log: ExceptionThrowingOWLObjectVisitorAdapter.java,v $
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
 * Revision 1.1  2004/03/30 17:46:37  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
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
 * Revision 1.6  2003/04/10 12:10:56  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.5  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.4  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.3  2003/02/06 10:27:33  seanb
 * Replacing OWLThing and OWLNothing with specific instances of OWLClass.
 *
 * Revision 1.2  2003/01/29 16:10:52  seanb
 * Changes to support Anonymous Individuals.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 *
 */
