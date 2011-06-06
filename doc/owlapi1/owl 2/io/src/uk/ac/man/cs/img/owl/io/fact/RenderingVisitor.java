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
 * Filename           $RCSfile: RenderingVisitor.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:46 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.fact; 
import org.semanticweb.owl.model.helper.OWLObjectVisitorAdapter;
import java.io.PrintWriter;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import java.util.Iterator;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import java.io.StringWriter;

import org.semanticweb.owl.model.OWLDataPropertyInstance;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividualTypeAssertion;
import org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLNot;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;

import java.util.Set;
import java.util.Map;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDescriptionVisitor;
import org.semanticweb.owl.model.OWLClassAxiomVisitor;
import org.semanticweb.owl.model.OWLIndividualAxiomVisitor;
import org.semanticweb.owl.model.OWLEquivalentPropertiesAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.io.ShortFormProvider;

// Generated package name


/**
 * RenderingVisitor.java
 *
 *
 * Created: Fri Feb 21 10:57:24 2003
 *
 * @author Sean Bechhofer
 * @version $Id: RenderingVisitor.java,v 1.3 2006/03/28 16:14:46 ronwalf Exp $
 */

public class RenderingVisitor extends OWLObjectVisitorAdapter 
{

    ShortFormProvider shortForms; 
    StringWriter sw;
    PrintWriter pw;
    int level;
    boolean indenting = true;

    public RenderingVisitor( ShortFormProvider shortForms )
    {
	this.shortForms = shortForms;
	reset();
    }
    
    public String result() {
	return sw.toString();
    }

    public void reset() {
	sw = new StringWriter();
	pw = new PrintWriter( sw );
    }
	
    public void visit( OWLClass clazz ) throws OWLException {
	pw.print( "|" + shortForms.shortForm( clazz.getURI() ) + "|");
    }
    public void visit( OWLIndividual ind ) throws OWLException {
	pw.print( "|_I_" + shortForms.shortForm( ind.getURI() ) + "|");

// 	RenderingVisitor visitor = new RenderingVisitor( shortForms );
// 	if ( ind.isAnonymous() ) {
// 	    /* We need to print out the entire description... */
// 	    pw.print("Individual(_" );
// 	    if (ind.getTypes().isEmpty() && 
// 		ind.getIndividualPropertyValues().keySet().isEmpty() &&
// 		ind.getDataPropertyValues().keySet().isEmpty()) {
// 		pw.print( ")");
// 	    } else {
// 		for ( Iterator it = ind.getTypes().iterator();
// 		      it.hasNext(); ) {
// 		    OWLDescription eq = (OWLDescription) it.next();
// 		    visitor.reset();
// 		    eq.accept( visitor );
// 		    pw.print( " type(" + visitor.result() + ")" );
// 		}
// 		Map propertyValues = ind.getIndividualPropertyValues();
		
// 		for ( Iterator it = propertyValues.keySet().iterator();
// 		      it.hasNext(); ) {
// 		    OWLObjectProperty prop = (OWLObjectProperty) it.next();
// 		    Set vals = (Set) propertyValues.get(prop);
// 		    for (Iterator valIt = vals.iterator(); valIt.hasNext();
// 			 ) {
// 			OWLIndividual oi = (OWLIndividual) valIt.next();
// 			visitor.reset();
// 			oi.accept( visitor );
// 			pw.print( " value(" + 
// 				  shortForms.shortForm( prop.getURI() ) + " " + 
// 				  visitor.result() + ")" );
// 		    }
// 		}
// 		Map dataValues = ind.getDataPropertyValues();
		
// 		for ( Iterator it = dataValues.keySet().iterator();
// 		      it.hasNext(); ) {
// 		    OWLDataProperty prop = (OWLDataProperty) it.next();
// 		    Set vals = (Set) dataValues.get(prop);
// 		    for (Iterator valIt = vals.iterator(); valIt.hasNext();
// 			 ) {
// 			OWLDataValue dtv = (OWLDataValue) valIt.next();
// 			visitor.reset();
// 			dtv.accept( visitor );
// 			pw.print( "value(" + 
// 				  shortForms.shortForm( prop.getURI() ) + " " + 
// 				  visitor.result() + ")" );
// 			if (it.hasNext()) {
// 			    pw.print(" ");
// 			}
// 		    }
// 		}
// 		pw.print(")");
// 	    }
// 	} else {
// 	    pw.print( shortForms.shortForm( ind.getURI() ) );
// 	}
    }

    public void visit( OWLObjectProperty prop ) throws OWLException {
	pw.print( "|" + shortForms.shortForm( prop.getURI() ) + "|" );
    }
    public void visit( OWLDataProperty prop ) throws OWLException {
	pw.print( "|" + shortForms.shortForm( prop.getURI() ) + "|" );
    }
    public void visit( OWLDataValue cd ) throws OWLException {
	pw.print( "[" + cd.getValue()+ "^^" + cd.getURI() + "]" );
    }

    public void visit( OWLAnd and ) throws OWLException {
	pw.print("(and ");
	for ( Iterator it = and.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	    if (it.hasNext()) {
		pw.print(" ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLOr or ) throws OWLException {
	pw.print("(or ");
	for ( Iterator it = or.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	    if (it.hasNext()) {
		pw.print(" ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLNot not ) throws OWLException {
	pw.print("(not ");
	OWLDescription desc = (OWLDescription) not.getOperand();
	desc.accept( this );
	pw.print(")");
    }

    public void visit( OWLEnumeration enumeration ) throws OWLException {
	pw.print("(or ");
	for ( Iterator it = enumeration.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLIndividual desc = (OWLIndividual) it.next();
	    desc.accept( this );
	    if (it.hasNext()) {
		pw.print(" ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLObjectSomeRestriction restriction ) throws OWLException {
	pw.print("(some ");
	restriction.getObjectProperty().accept( this );
	pw.print(" ");
	restriction.getDescription().accept( this );
	pw.print(")");
    }

    public void visit( OWLObjectAllRestriction restriction ) throws OWLException {
	pw.print("(all ");
	restriction.getObjectProperty().accept( this );
	pw.print(" ");
	restriction.getDescription().accept( this );
	pw.print(")");
    }

    public void visit( OWLObjectValueRestriction restriction ) throws OWLException {
	pw.print("(some ");
	restriction.getObjectProperty().accept( this );
	pw.print(" ");
	restriction.getIndividual().accept( this );
	pw.print(")");
    }


    public void visit( OWLObjectCardinalityRestriction restriction ) throws OWLException {
	if ( restriction.isExactly() ) {
	    pw.print(" (and (atmost " + restriction.getAtLeast() + " ");
	    restriction.getObjectProperty().accept( this );
	    pw.print(" :TOP)");
	    pw.print(" (atleast " + restriction.getAtLeast() + " ");
	    restriction.getObjectProperty().accept( this );
	    pw.print(" :TOP))");
	} else if ( restriction.isAtMost() ) {
	    pw.print(" (atmost " + restriction.getAtMost() + " ");
	    restriction.getObjectProperty().accept( this );
	    pw.print(" :TOP)");
	} else 	if ( restriction.isAtLeast() ) {
	    pw.print(" (atleast " + restriction.getAtLeast() + " ");
	    restriction.getObjectProperty().accept( this );
	    pw.print(" :TOP)");
	} 
    }

    public void visit( OWLDataCardinalityRestriction restriction ) throws OWLException {
	if ( restriction.isExactly() ) {
	    pw.print(" (and (atmost " + restriction.getAtLeast() + " ");
	    restriction.getDataProperty().accept( this );
	    pw.print(" :TOP)");
	    pw.print(" (atleast " + restriction.getAtLeast() + " ");
	    restriction.getDataProperty().accept( this );
	    pw.print(" :TOP))");
	} else if ( restriction.isAtMost() ) {
	    pw.print(" (atmost " + restriction.getAtMost() + " ");
	    restriction.getDataProperty().accept( this );
	    pw.print(" :TOP)");
	} else 	if ( restriction.isAtLeast() ) {
	    pw.print(" (atleast " + restriction.getAtLeast() + " ");
	    restriction.getDataProperty().accept( this );
	    pw.print(" :TOP)");
	} 
    }

    public void visit( OWLEquivalentClassesAxiom axiom ) throws OWLException {
	Object[] eqs = axiom.getEquivalentClasses().toArray();
	for (int i=0; i< eqs.length; i++) {
	    for (int j=i+1; j<eqs.length; j++) {
		pw.print(" (equal_c ");
		OWLDescription desc1 = (OWLDescription) eqs[i];
		desc1.accept( this );
		OWLDescription desc2 = (OWLDescription) eqs[j];
		desc2.accept( this );
		pw.print(")");
	    }
	}
    }

    public void visit( OWLDisjointClassesAxiom axiom ) throws OWLException {
	pw.println(" ;; Disjoint");
	Object[] eqs = axiom.getDisjointClasses().toArray();
	for (int i=0; i< eqs.length; i++) {
	    for (int j=i+1; j<eqs.length; j++) {
		pw.print(" (implies_c ");
		OWLDescription desc1 = (OWLDescription) eqs[i];
		desc1.accept( this );
		OWLDescription desc2 = (OWLDescription) eqs[j];
		pw.print(" (not ");
		desc2.accept( this );
		pw.print(" ))");
	    }
	}
// 	pw.print(" (disjoint ");
// 	for ( Iterator it = axiom.getDisjointClasses().iterator();
// 	      it.hasNext(); ) {
// 	    OWLDescription desc = (OWLDescription) it.next();
// 	    desc.accept( this );
// 	    if (it.hasNext()) {
// 		pw.print(" ");
// 	    }
// 	}
// 	pw.print(")");
    }

    public void visit( OWLSubClassAxiom axiom ) throws OWLException {
	pw.print(" (implies_c ");
	axiom.getSubClass().accept( this );
	pw.print(" ");
	axiom.getSuperClass().accept( this );
	pw.print(")");
    }

    public void visit( OWLEquivalentPropertiesAxiom axiom ) throws OWLException {
	Object[] eqs = axiom.getProperties().toArray();
	for (int i=0; i< eqs.length; i++) {
	    for (int j=i+1; j<eqs.length; j++) {
		pw.print(" (equal_r ");
		OWLProperty desc1 = (OWLProperty) eqs[i];
		desc1.accept( this );
		OWLProperty desc2 = (OWLProperty) eqs[j];
		desc2.accept( this );
		pw.print(")");
	    }
	}
    }

    public void visit( OWLSubPropertyAxiom axiom ) throws OWLException {
	pw.print(" (implies_r ");
	axiom.getSubProperty().accept( this );
	pw.print(" ");
	axiom.getSuperProperty().accept( this );
	pw.print(")");
    }

    public void visit( OWLDifferentIndividualsAxiom ax) throws OWLException {
	/* This happens for free.... */
// 	pw.print("DifferentIndividuals(");
// 	for ( Iterator it = ax.getIndividuals().iterator();
// 	      it.hasNext(); ) {
// 	    OWLIndividual desc = (OWLIndividual) it.next();
// 	    desc.accept( this );
// 	    if (it.hasNext()) {
// 		pw.print(" ");
// 	    }
// 	}
// 	pw.print(")");
    }

    public void visit( OWLSameIndividualsAxiom ax) throws OWLException {
	/* This we can't really do.... */
	pw.print(";; SameIndividual(");
	for ( Iterator it = ax.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLIndividual desc = (OWLIndividual) it.next();
	    desc.accept( this );
	    if (it.hasNext()) {
		pw.print(" ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLDataType ocdt ) throws OWLException {
	pw.print( "|" + shortForms.shortForm( ocdt.getURI() ) + "|" );
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

} // RenderingVisitor



/*
 * ChangeLog
 * $Log: RenderingVisitor.java,v $
 * Revision 1.3  2006/03/28 16:14:46  ronwalf
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
 * Revision 1.2  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/05/15 13:51:43  seanb
 * Addition of new non-streaming parser.
 *
 * Revision 1.1  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.9  2003/05/06 14:26:53  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.8  2003/04/10 12:15:28  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.7  2003/04/09 14:26:48  seanb
 * no message
 *
 * Revision 1.6  2003/04/07 17:15:11  seanb
 * no message
 *
 * Revision 1.5  2003/03/31 16:55:12  seanb
 * Various updates and fixes to parser.
 * Adding inverses, domain and range.
 *
 * Revision 1.4  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.3  2003/03/26 18:46:17  seanb
 * Improved handling of anonymous nodes in the parser.
 *
 * Revision 1.2  2003/03/24 13:54:13  seanb
 * Minor changes to Data property handling. Addition of extra logging to
 * assist in tracking large ontologies.
 *
 * Revision 1.1  2003/03/20 10:26:34  seanb
 * Adding Abstract Syntax Renderer
 *
 */
