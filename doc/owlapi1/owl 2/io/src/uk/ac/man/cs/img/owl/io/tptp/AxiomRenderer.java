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
 * Filename           $RCSfile: AxiomRenderer.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:46 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.tptp; 
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
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import java.util.HashSet;

// Generated package name


/**
 * AxiomRenderer.java
 *
 *
 * Created: Fri Feb 21 10:57:24 2003
 *
 * @author Sean Bechhofer
 * @version $Id: AxiomRenderer.java,v 1.3 2006/03/28 16:14:46 ronwalf Exp $
 */

public class AxiomRenderer extends OWLObjectVisitorAdapter 
{

    Renderer renderer; 
    StringWriter sw;
    PrintWriter pw;
    int level;
    boolean indenting = true;

    /* The variable we're working on */
    String var;
    /* Variables already bound */
    Set boundVariables;

    private String[] variables = {"X", "Y", "Z", "W", "A", "B", "C", "D", "E", "F", "G", "H", "I", 
		      "J", "K", "L", "M", "N", "O", "P", "Q", "R", 
		      "S", "T", "U", "V" };

    public AxiomRenderer( Renderer renderer )
    {
	this.renderer = renderer;
	sw = new StringWriter();
	pw = new PrintWriter( sw );
    }
    
    public String result() {
	return sw.toString();
    }

    public void visit( OWLEquivalentClassesAxiom axiom ) throws OWLException {
	if ( axiom.getEquivalentClasses().size() > 1 ) {
	    pw.print("![X]: (");
	    
	    /* This is a bit over-zealous. We should be able to get away with:
	       
	    C1 <=> C2, C2 <=> C3, .... Cn-1 <=> Cn
	    
	    */
	    
	    Object[] eqs = axiom.getEquivalentClasses().toArray();
	    for (int i=0; i< eqs.length; i++) {
		for (int j=i+1; j<eqs.length; j++) {
		    RenderingVisitor rv1 = new RenderingVisitor( renderer, "X", new HashSet() );
		    OWLDescription desc1 = (OWLDescription) eqs[i];
		    desc1.accept( rv1 );
		    RenderingVisitor rv2 = new RenderingVisitor( renderer, "X", new HashSet() );
		    OWLDescription desc2 = (OWLDescription) eqs[j];
		    desc2.accept( rv2 );
		    if ( !( (i==0) && (j==1) ) ) {
			pw.print( " & " );
		    }
		    pw.print("(" + rv1.result() + " <=> " + rv2.result() + ")");
		}
	    }
	    pw.println( ")" );
	}
    }

    public void visit( OWLDisjointClassesAxiom axiom ) throws OWLException {
	pw.print("![X]: (");
	
	Object[] eqs = axiom.getDisjointClasses().toArray();
	for (int i=0; i< eqs.length; i++) {
	    for (int j=i+1; j<eqs.length; j++) {
		RenderingVisitor rv1 = new RenderingVisitor( renderer, "X", new HashSet() );
		OWLDescription desc1 = (OWLDescription) eqs[i];
		desc1.accept( rv1 );
		RenderingVisitor rv2 = new RenderingVisitor( renderer, "X", new HashSet() );
		OWLDescription desc2 = (OWLDescription) eqs[j];
		desc2.accept( rv2 );
		/* Originally translated as d1 <=> ~d2. This is too
		 * strong, and has been replaced with ~(d1 & d2). */
		//pw.print("(" + rv1.result() + " <=> ~(" + rv2.result() + "))");

		/* This will produce bogus stuff for multi
		 * disjoints.... NEEDS FIXING!*/
		if ( !( (i==0) && (j==1) ) ) {
		    pw.print( " & " );
		}
		pw.print("~(" + rv1.result() + " & " + rv2.result() + ")");
	    }
	}
	pw.println( ")" );
    }

    public void visit( OWLSubClassAxiom axiom ) throws OWLException {
	pw.print("![X]: ");
	
	RenderingVisitor rv1 = new RenderingVisitor( renderer, "X", new HashSet() );
	axiom.getSubClass().accept( rv1 );
	pw.print( "(" + rv1.result() );
	pw.print( " => ");
	RenderingVisitor rv2 = new RenderingVisitor( renderer, "X", new HashSet() );
	axiom.getSuperClass().accept( rv2 );
	pw.print( rv2.result() + ")" );
    }

    public void visit( OWLEquivalentPropertiesAxiom axiom ) throws OWLException {
// 	pw.print( "![X,Y]: " );
// 	pw.print( "(" + renderer.shortForm( axiom.getSubProperty() ) + "(X,Y) <=> " );
// 	pw.print( renderer.shortForm( axiom.getSuperProperty() ) + "(X,Y))" );
//	throw new OWLException(" Equivalent Properties not implemented ");
	if ( axiom.getProperties().size() > 1 ) {
	    pw.print( "(" );
	    Object[] eqs = axiom.getProperties().toArray();
	    for (int i=0; i< eqs.length; i++) {
		for (int j=i+1; j<eqs.length; j++) {
		    if ( !( (i==0) && (j==1) ) ) {
			pw.print( " & " );
		    }
		    OWLProperty prop1 = (OWLProperty) eqs[i];
		    OWLProperty prop2 = (OWLProperty) eqs[j];
		    pw.print( "(![X,Y]: " );
		    pw.print( "(" + renderer.shortForm( prop1 ) + "(X,Y) <=> " );
		    pw.print( renderer.shortForm( prop2 ) + "(X,Y))" );
		    pw.print(")");
		    
		}
	    }
	    pw.print( ")" );
	}
    }

    public void visit( OWLSubPropertyAxiom axiom ) throws OWLException {
	pw.print( "![X,Y]: " );
	pw.print( "(" + renderer.shortForm( axiom.getSubProperty() ) + "(X,Y) => " );
	pw.print( renderer.shortForm( axiom.getSuperProperty() ) + "(X,Y))" );
    }

    public void visit( OWLDifferentIndividualsAxiom ax) throws OWLException {
	pw.print("(");
	
	Object[] diffs = ax.getIndividuals().toArray();
	for (int i=0; i< diffs.length; i++) {
	    for (int j=i+1; j<diffs.length; j++) {
		OWLIndividual ind1 = (OWLIndividual) diffs[i];
		OWLIndividual ind2 = (OWLIndividual) diffs[j];
		if ((i!=0) || (j!=1)) {
		    pw.print( " & " );
		}
		pw.print("~(equal(" + renderer.shortForm( ind1 ) + "," + renderer.shortForm( ind2 ) + "))" );
// 		if (!(i==diffs.length-2) & (j==diffs.length-1)) {
// 		    pw.print( " & " );
// 		}
	    }
	}
	pw.print(")");

// 	pw.print("(different ");
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
	/* We only need to say i1=i2 & i2=i3 & ... in-1=in due to
	 * transitivity of equality */
	pw.print("(");
	
	Object[] diffs = ax.getIndividuals().toArray();
	for (int i=0; i< diffs.length-1; i++) {
	    OWLIndividual ind1 = (OWLIndividual) diffs[i];
	    OWLIndividual ind2 = (OWLIndividual) diffs[i+1];
	    pw.print("(equal(" + renderer.shortForm( ind1 ) + "," + renderer.shortForm( ind2 ) + "))" );
	    if (i<(diffs.length-2)) {
		pw.print( " & " );
	    }
	}
	pw.print(")");
	
	//	throw new OWLException(" Same Individuals not implemented ");
// 	/* This we can't really do.... */
// 	pw.print("(same ");
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

} // AxiomRenderer



/*
 * ChangeLog
 * $Log: AxiomRenderer.java,v $
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
 * Revision 1.2  2003/11/21 16:31:11  sean_bechhofer
 * Changes to TPTP rendering -- separation of abstract and data domains.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.4  2003/09/22 13:21:44  bechhofers
 * Fixes to rendering and some extra helper functions.
 *
 * Revision 1.3  2003/08/20 08:39:57  bechhofers
 * Alterations to tests.
 *
 * Revision 1.2  2003/06/11 16:48:37  seanb
 * Changes to TPTP renderer.
 *
 * Revision 1.1  2003/06/06 14:30:30  seanb
 * Renderer for TPTP
 *
 * Revision 1.1  2003/06/03 17:01:53  seanb
 * Additional inference
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
