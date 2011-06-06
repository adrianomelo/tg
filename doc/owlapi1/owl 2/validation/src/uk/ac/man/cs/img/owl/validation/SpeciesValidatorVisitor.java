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

package uk.ac.man.cs.img.owl.validation; 
import org.semanticweb.owl.model.helper.OWLObjectVisitorAdapter;
import org.semanticweb.owl.model.OWLDataPropertyInstance;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividualTypeAssertion;
import org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;

import java.util.Iterator;
import org.semanticweb.owl.model.OWLClass;
import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLException;
import java.io.PrintWriter;
import org.semanticweb.owl.io.abstract_syntax.ObjectRenderer;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.validation.OWLValidationConstants;
// Generated package name


/**
 * A visitor that calculates which language species a particular
 * object is in, depending on the expressivity that has been
 * used. 
 *
 *
 * @author Sean Bechhofer
 * @version $Id: SpeciesValidatorVisitor.java,v 1.4 2006/03/28 16:14:46 ronwalf Exp $
 */

class SpeciesValidatorVisitor extends OWLObjectVisitorAdapter implements OWLValidationConstants
{

    private int level;

    static Logger logger = Logger.getLogger(SpeciesValidatorVisitor.class);
    
    private SpeciesValidator parent;

    private ObjectRenderer objectRenderer;

    public int getLevel() {
	return level;
    }

    public SpeciesValidatorVisitor( SpeciesValidator parent, 
				    ObjectRenderer renderer ) {
	this.level = SpeciesValidator.LITE;
	this.parent = parent;
	this.objectRenderer = renderer;
    }
    
    public void visit( OWLDisjointClassesAxiom node ) {
	/* If a Disjoint Classes Axiom is used, we must be in at
	 * least DL. */
	logger.debug("Visiting DisjointClassesAxiom");
	parent.explain( SpeciesValidator.DL,
			DISJOINT, 
			"Disjoint Classes axiom found: " + renderNode( node ) );
	level = level | SpeciesValidator.DL;
    }
    
    public void visit( OWLEquivalentClassesAxiom node ) {
	/* Depends on the format of the expressions. */
	logger.debug("Visiting EquivalentClassesAxiom");
	try {
	    for ( Iterator it = node.getEquivalentClasses().iterator();
		  it.hasNext(); ) {
		OWLObject oo = (OWLObject) it.next();
		logger.debug( "Object is: " + oo );
		if ( !isClassOrSimpleRestriction( oo ) ) {
		    parent.explain( SpeciesValidator.DL,
				    EXPRESSIONINAXIOM,
				    "Equivalent Classes axiom using expressions found: " + renderNode( node ));
		    
		    level = level | SpeciesValidator.DL;
		}
	    }
	} catch ( OWLException ex ) {
	    level = level | SpeciesValidator.OTHER;
	}
    }
    
    public void visit( OWLSubClassAxiom node ) {
	/* Depends on the format of the expressions. */
	try {
	    OWLDescription subClass = node.getSubClass();
	    
	    if ( !isClass( subClass ) ) {
		parent.explain( SpeciesValidator.DL,
				EXPRESSIONINAXIOM,
				"SubClass axiom using expressions found: " + renderNode( node ));
		
		level = level | SpeciesValidator.DL;
	    }
	    OWLDescription superClass = node.getSuperClass();
	    if ( !isClassOrSimpleRestriction( superClass ) ) {
		parent.explain( SpeciesValidator.DL,
				EXPRESSIONINAXIOM,
				"SubClass axiom using expressions found: " + renderNode( node ));
		
		level = level | SpeciesValidator.DL;
	    }
	} catch ( OWLException ex ) {
	    level = level | SpeciesValidator.OTHER;
	}
    }
    
    public void visit( OWLClass node ) {
	/* Checks all the various things that have been said about the
	 * class. */
	
    }
    
    /* Check if the description is a Class */
    private boolean isClass( OWLObject oo ) throws OWLException {
	ClassOrRestrictionIdentifier civ = 
	    new ClassOrRestrictionIdentifier();
	oo.accept( civ );
	return civ.isClass();
    }

    private boolean isClassOrSimpleRestriction( OWLObject oo ) throws OWLException {
	ClassOrRestrictionIdentifier civ = 
	    new ClassOrRestrictionIdentifier();
	oo.accept( civ );
	return civ.isClassOrSimpleRestriction();
    }

    private String renderNode( OWLObject node ) {
	try {
	    return objectRenderer.renderObject( node );
	} catch (RendererException ex) {
	    return node.toString();
	}
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
    
} // SpeciesValidatorVisitor



/*
 * ChangeLog
 * $Log: SpeciesValidatorVisitor.java,v $
 * Revision 1.4  2006/03/28 16:14:46  ronwalf
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
 * Revision 1.3  2005/06/10 12:20:34  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.4  2003/02/07 13:50:24  seanb
 * Fixing RDF Parser Test and OWLOntologyImpl accessors.
 *
 * Revision 1.3  2003/02/06 16:41:42  seanb
 * More enhancements to validation
 *
 * Revision 1.2  2003/02/06 14:29:26  seanb
 * Enhancements to validation
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 */
