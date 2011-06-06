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
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObject;
import java.util.Iterator;
import org.semanticweb.owl.model.OWLClass;
import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLException;
import java.io.PrintWriter;

import org.semanticweb.owl.model.OWLDataPropertyInstance;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividualTypeAssertion;
import org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLNot;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;
import org.semanticweb.owl.io.abstract_syntax.ObjectRenderer;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.OWLObjectProperty;
import java.util.Set;
import java.util.HashSet;
import org.semanticweb.owl.validation.OWLValidationConstants;
import org.semanticweb.owl.model.OWLDataEnumeration;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import uk.ac.man.cs.img.owl.validation.ClassIdentifierVisitor;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataValueRestriction;

// Generated package name


/**
 * A visitor that calculates which language species an expression is
 * in. As a side effect, the visitor will note any usage of a property
 * in a cardinality constraint.
 *
 * @author Sean Bechhofer
 * @version $Id: ExpressionValidatorVisitor.java,v 1.4 2006/03/28 16:14:46 ronwalf Exp $
 */

class ExpressionValidatorVisitor extends OWLObjectVisitorAdapter implements OWLValidationConstants
{

    private int level;
    /* A flag that indicates whether we're at the top level of an
    equivalence description. In this particular case, it *is* ok to
    have an intersection */ 
    private boolean topLevelDescription = false;

    private Set complexProperties;
    
    static Logger logger = Logger.getLogger(ExpressionValidatorVisitor.class);
    
    private SpeciesValidator parent;

    private ObjectRenderer objectRenderer;

    public int getLevel() {
	return level;
    }

    public Set getComplexProperties() {
	return complexProperties;
    }

    public void setTopLevelDescription( boolean b ) {
	topLevelDescription = b;
    }
    
    public ExpressionValidatorVisitor( SpeciesValidator parent, 
				       ObjectRenderer renderer) {
	this.level = LITE;
	this.parent = parent;
	this.objectRenderer = renderer;
	this.complexProperties = new HashSet();
    }
    
    public void reset() {
	this.level = LITE;
	this.topLevelDescription = false;
    }
    
    /* If it's one of: 
     * or
     * not
     * oneof
     * individualValueRestriction
     * cardinality with anything other than 0, 1 
     * then we're in DL. */
    
    public void visit( OWLOr node ) {
	explain( DL,
		 UNION,
		 "Or: " + renderNode( node ) );
	level = level | DL;
    }
    public void visit( OWLNot node ) {
	explain( DL,
		 COMPLEMENT,
		 "Not: " + renderNode( node ) );
	level = level | DL;
    }
    public void visit( OWLEnumeration node ) {
	explain( DL,
		 ONEOF,
		 "Enumeration: " + renderNode( node ) );
	level = level | DL;
    }
    public void visit( OWLDataEnumeration node ) {
	explain( DL,
		 ONEOF, /* DATARANGE?? */
		 "Data Enumeration: " + renderNode( node ) );
	level = level | DL;
    }
    public void visit( OWLObjectValueRestriction node ) {
	explain( DL,
		 ONEOF,
		 "Individual Value: " + renderNode( node ) );
	level = level | DL;
    }
    public void visit( OWLObjectCardinalityRestriction node ) throws OWLException {
	/* The property is complex. */
	complexProperties.add( node.getProperty() );

	if ( (node.isAtLeast() && node.getAtLeast() > 1) ||
	     (node.isAtMost() && node.getAtMost() > 1) ) {
	    explain( DL,
		     CARDINALITY,
		     "Cardinality with > 1: " + renderNode( node ) );
	    level = level | DL;
	}
    }
    public void visit( OWLDataCardinalityRestriction node ) throws OWLException {
	if ( (node.isAtLeast() && node.getAtLeast() > 1) ||
	     (node.isAtMost() && node.getAtMost() > 1) ) {
	    explain( DL,
		     CARDINALITY,
		     "Cardinality with > 1: " + renderNode( node ) );
	    level = level | DL;
	}
    }
    public void visit( OWLDataValueRestriction node ) {
	explain( DL,
		 ONEOF,
		 "Data Value: " + renderNode( node ) );
	level = level | DL;
    }

    
    /* It it's an and, and we're at a top level equivalence, we need
     * to check that the operands are all either classes or
     * restrictions.  If they are restrictions, we then need to check
     * that the restrictions are themselves ok. */

    /* This is very unpleasant, and would be the kind of situation
     * where OWLFrame would be useful as it would allow intersection
     * at the top level....*/
    public void visit ( OWLAnd node ) throws OWLException {
	if ( topLevelDescription ) {
	    topLevelDescription = false;
	    ClassOrRestrictionIdentifier cori = 
		new ClassOrRestrictionIdentifier();
	    for ( Iterator it = node.getOperands().iterator();
		  it.hasNext(); ) {
		OWLDescription description = (OWLDescription) it.next();
		cori.reset();
		description.accept( cori );
		if ( !cori.isClassOrRestriction() ) {
		    /* If it's not a class or restriction, we're DL */
		    explain( DL,
			     INTERSECTION,
			     "And with non-class or restriction: " + renderNode( node ) );
		    level = level | DL;
		} else {
		    /* Now need to check the expression itself too. */
		    if ( cori.isRestriction() ) {
			/* Recurse down and check the restriction */
			description.accept( this );
		    }
		}
	    }
	} else {
	    explain( DL,
		     INTERSECTION,
		     "And: " + renderNode( node ) );
	    level = level | DL;
	}
    }

    /* If it's an object restriction, then if the filler is anything
     * other than a class, we're in at least DL. */

    public void visit ( OWLObjectSomeRestriction node ) throws OWLException {
 	topLevelDescription = false;
	ClassIdentifierVisitor civ = new ClassIdentifierVisitor();
	node.getDescription().accept( civ );
	if ( !civ.isClass() ) {
	    explain( DL, EXPRESSIONINRESTRICTION, "Object restriction with non classID filler: " + renderNode( node ) );
	    level = level | DL;
	}
	node.getDescription().accept( this );
    }

    public void visit ( OWLObjectAllRestriction node ) throws OWLException {
	topLevelDescription = false;
	ClassIdentifierVisitor civ = new ClassIdentifierVisitor();
	node.getDescription().accept( civ );
	if ( !civ.isClass() ) {
	    explain( DL, EXPRESSIONINRESTRICTION, "Object restriction with non classID filler: " + renderNode( node ) );
	    level = level | DL;
	}
	node.getDescription().accept( this );
    }

    private String renderNode( OWLObject node ) {
	try {
	    if ( objectRenderer!=null ) {
		return objectRenderer.renderObject( node );
	    }
	} catch (RendererException ex) {
	}
	return node.toString();
    }

    private void explain( int level, 
			  int code, 
			  String str ) {
	if ( parent!=null ) {
	    parent.explain( level, code, str );
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
    
} // ExpressionValidatorVisitor



/*
 * ChangeLog
 * $Log: ExpressionValidatorVisitor.java,v $
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
 * Revision 1.11  2003/08/28 10:29:04  bechhofers
 * Updating parser to improve validation. Addition of new consumer with
 * simple triple model.
 *
 * Revision 1.10  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.9  2003/06/19 13:33:33  seanb
 * Addition of construct checking.
 *
 * Revision 1.8  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.7  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.6  2003/05/06 14:26:54  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.5  2003/04/10 12:14:58  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.4  2003/04/07 17:15:11  seanb
 * no message
 *
 * Revision 1.3  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.2  2003/02/07 18:42:35  seanb
 * no message
 *
 * Revision 1.1  2003/02/06 16:44:00  seanb
 * Enhancement of validation.
 *
 * Revision 1.2  2003/02/06 14:29:26  seanb
 * Enhancements to validation
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 */
