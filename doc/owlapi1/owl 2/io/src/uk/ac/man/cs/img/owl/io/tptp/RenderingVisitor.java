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
 * Revision           $Revision: 1.4 $
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
import java.net.URI;
import org.semanticweb.owl.model.OWLDataEnumeration;
import org.semanticweb.owl.io.RendererException;

// Generated package name


/**
 * RenderingVisitor.java
 *
 *
 * Created: Fri Feb 21 10:57:24 2003
 *
 * @author Sean Bechhofer
 * @version $Id: RenderingVisitor.java,v 1.4 2006/03/28 16:14:46 ronwalf Exp $
 */

public class RenderingVisitor extends OWLObjectVisitorAdapter 
{
    /* A number of tweaks needed here to ensure that the data/abstract
     * domain splitting works. Basically, negation has to be within
     * the context of the abstract domain, so whenever we see: not
     * P(X) we need to also specify that X must be in the abstract
     * domain. However, we also must be careful about some other
     * predicates where negation is lurking, in particular:
     *
     * max cardinalities. In this case, the data elements fit the
     * bill because of the fact that data elements cannot have
     * successors (there are axioms relating to domain/range of
     * properties. Thus for max cardinality assertions, we need to add
     * that the thing is an abstract thing.
     *
     * universal quantification: In this case, if we end up
     * quantifying over nothing, then the datadomain elements get
     * dragged in. So we have to add that the var must be in the
     * abstract domain. It is easiest to just add this every time as
     * we can't be sure that the filler is not empty.
     * 
     */

    /** Stops the renderer producing massive files if big
     * cardinalities are involved. */
    public static int MAXCARDINALITYHANDLED=257;

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

    public RenderingVisitor( Renderer renderer, String v, Set boundVars )
    {
	this.renderer = renderer;
	sw = new StringWriter();
	pw = new PrintWriter( sw );
	var = v;
	/* Take a copy of the bound variables */
	boundVariables = new HashSet(boundVars);
	/* Add the current one to the bound set */
	boundVariables.add( v );
    }
    
    public String result() {
	return sw.toString();
    }

    public void visit( OWLClass clazz ) throws OWLException {
	pw.print( renderer.shortForm( clazz ) + "(" + var + ")" );
    }

    public void visit( OWLIndividual ind ) throws OWLException {
	pw.print( renderer.shortForm( ind ) );
    }

    public void visit( OWLObjectProperty prop ) throws OWLException {
	pw.print( renderer.shortForm( prop ) );
    }

    public void visit( OWLDataProperty prop ) throws OWLException {
	pw.print( renderer.shortForm( prop ) );
    }

    public void visit( OWLDataValue cd ) throws OWLException {
	throw new RendererException( "DataValue encountered...." );
	//	pw.print( "[" + cd.getValue()+ "^^" + cd + "]" );
    }

    public void visit( OWLAnd and ) throws OWLException {
	pw.print("(");
	for ( Iterator it = and.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	    if (it.hasNext()) {
		pw.print(" & ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLOr or ) throws OWLException {
	pw.print("(");
	for ( Iterator it = or.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	    if (it.hasNext()) {
		pw.print(" | ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLNot not ) throws OWLException {
	/* This is a little tricky. In order to ensure that we negate
	 * w.r.t. the abstract domain, we need to conjoin the abstract
	 * domain to the predicate. */
	pw.print( "(abstractDomain(" + var + ") & ");
	pw.print("(~(");
	OWLDescription desc = (OWLDescription) not.getOperand();
	desc.accept( this );
	pw.print("))");
	pw.print( ")");
    }

    public void visit( OWLEnumeration enumeration ) throws OWLException {
	if (enumeration.getIndividuals().isEmpty()) {
	    pw.print("(cowlNothing(" + var + "))");
	} else {
	    pw.print("(");
	    for (Iterator it = enumeration.getIndividuals().iterator(); it.hasNext(); ) {
		OWLIndividual ind = (OWLIndividual) it.next();
		pw.print( "equal(" + var + "," + renderer.shortForm( ind ) + ")" );
		if (it.hasNext()) {
		    pw.print(" | ");
		}
	    }
	    pw.print(")");
	}
    }
    
    public void visit( OWLObjectSomeRestriction restriction ) throws OWLException {
	String newVar = getNewUnboundVar();
	pw.print( "(?[" + newVar + "]: (" );
	pw.print( renderer.shortForm( restriction.getObjectProperty() ) );
	pw.print( "(" + var + "," + newVar + ") & ");
	Set s = new HashSet(boundVariables);
	RenderingVisitor rv = new RenderingVisitor( renderer, newVar, s );
	restriction.getDescription().accept( rv );
	pw.print( rv.result() + ")" );
	pw.print(")");
	/* Now make sure that any new predicates introduced due to
	 * enumerations get dealt with! */
    }

    public void visit( OWLObjectAllRestriction restriction ) throws OWLException {
	/* We have to be careful here. If the quantification is over
	 * nothing, then we end up including the data elements in
	 * here. So we throw in the abstract domain just to make
	 * sure. */
	String newVar = getNewUnboundVar();
	pw.print( "(abstractDomain(" + var + ") & ");
	pw.print( "(![" + newVar + "]: (" );
	pw.print( renderer.shortForm( restriction.getObjectProperty() ) );
	pw.print( "(" + var + "," + newVar + ") => ");
	Set s = new HashSet(boundVariables);
	RenderingVisitor rv = new RenderingVisitor( renderer, newVar, s );
	restriction.getDescription().accept( rv );
	pw.print( rv.result() + ")" );
	pw.print(")");
	pw.print( ")" );
		
	/* Now make sure that any new predicates introduced due to
	 * enumerations get dealt with! */
    }

    public void visit( OWLObjectValueRestriction restriction ) throws OWLException {
	pw.print( "(" + renderer.shortForm( restriction.getObjectProperty() ) + "(" + var + "," );
	pw.print( renderer.shortForm(restriction.getIndividual() ) );
	pw.print("))");
    }


    public void visit( OWLObjectCardinalityRestriction restriction ) throws OWLException {
	int min = -1;
	int max = -1;
	
	if ( restriction.isExactly() ) {
	    min = restriction.getAtLeast();
	    max = restriction.getAtMost();
	} else if ( restriction.isAtMost() ) {
	    max = restriction.getAtMost();
	} else 	if ( restriction.isAtLeast() ) {
	    min = restriction.getAtLeast();
	}

	doCardinality( restriction.getObjectProperty(), min, max );
    }



    public void visit( OWLDataSomeRestriction restriction ) throws OWLException {
	String newVar = getNewUnboundVar();
	pw.print( "(?[" + newVar + "]: (" );
	pw.print( renderer.shortForm( restriction.getDataProperty() ) );
	pw.print( "(" + var + "," + newVar + ") & ");
	Set s = new HashSet(boundVariables);
	RenderingVisitor rv = new RenderingVisitor( renderer, newVar, s );
	restriction.getDataType().accept( rv );
	pw.print( rv.result() + ")" );
	pw.print(")");
    }

    public void visit( OWLDataAllRestriction restriction ) throws OWLException {
	/* We have to be careful here. If the quantification is over
	 * nothing, then we end up including the data elements in
	 * here. So we throw in the abstract domain just to make
	 * sure. */
	String newVar = getNewUnboundVar();
	pw.print( "(abstractDomain(" + var + ") & ");
	pw.print( "(![" + newVar + "]: (" );
	pw.print( renderer.shortForm( restriction.getDataProperty() ) );
	pw.print( "(" + var + "," + newVar + ") => ");
	Set s = new HashSet(boundVariables);
	RenderingVisitor rv = new RenderingVisitor( renderer, newVar, s );
	restriction.getDataType().accept( rv );
	pw.print( rv.result() + ")" );
	pw.print(")");
	pw.print( ")" );
    }

    private void doCardinality( OWLProperty prop, int min, int max ) throws OWLException {
	if ( ( min > MAXCARDINALITYHANDLED ) ) {
	    renderer.cardinalityException( "Cardinality with min " + min + " encountered." );
	}
	if ( ( max > MAXCARDINALITYHANDLED ) ) {
	    renderer.cardinalityException( "Cardinality with max " + max + " encountered." );
	}
	pw.print("(");
	if (min>0) {
	    String newVar = getNewUnboundVar();
	    pw.print("(?[");
	    for (int i=0; i<min; i++) {
		if (i>0) {
		    pw.print(",");
		}
		pw.print(newVar + intToString(i) );
	    }
	    pw.print("]:(");
	    for (int i=0; i<min; i++) {
		if (i>0) {
		    pw.print(" & ");
		}
		pw.print( renderer.shortForm( prop ) + "(" + var + "," + newVar + intToString(i) + ")" );
	    }
	    for (int i=0; i<min; i++) {
		for (int j=i+1; j<min; j++) {
		    if (i<min && j<min) {
			pw.print(" & ");
		    }
		    pw.print( "~equal(" + newVar + intToString(i) + "," + newVar + intToString(j)+ ")" );
		}
	    }
	    pw.print("))");
	} 
	if (max>-1) {
	    if (min>0) {
		/* We've done a min, so need the & */
		pw.print(" & ");
	    }
	    /* We have to be careful here and be explicit that the var
	     * must be in the abstract domain. To do this we throw in
	     * an extra conjunction. This saves us from nasty
	     * situations where the data values are in fact elements
	     * of this description as we know that they never have
	     * successors. */
	    pw.print( "(abstractDomain(" + var + ") & ");

	    if (max==0) {
		String newVar = getNewUnboundVar();
		pw.print( "(~(?[" + newVar + "]:(" + renderer.shortForm( prop ) + "(" + var + "," + newVar + "))))" );
	    } else {
		String newVar = getNewUnboundVar();
		pw.print( "(![" );
		for (int i=0; i<max+1; i++) {
		    if (i>0) {
			pw.print(",");
		    }
		    pw.print(newVar + intToString(i) );
		}
		pw.print("]:((");
		for (int i=0; i<max+1; i++) {
		    if (i>0) {
			pw.print(" & ");
		    }
		    pw.print( renderer.shortForm( prop ) + "(" + var + "," + newVar + intToString(i) + ")" );
		}
		pw.print(") => (");
		/* x1 = x2 or x1 = x3 or ... or x2 = x3 or ... */
		for (int i=0; i<max+1; i++) {
		    for (int j=i+1; j<max+1; j++) {
			pw.print( "equal(" + newVar + intToString(i) + "," + newVar + intToString(j)+ ")" );
			if (!(i==(max-1) && j==max)) {
			    pw.print(" | ");
			}
		    }
		}
		pw.print(")))");
	    }
	    pw.print( ")" );
	}
	pw.print(")");
    }
    
    /* Needed because X1,X2 aren't valid variable
     * names. Sigh.... */
    /* It's now ok with the latest Wampire. */
     private String intToString( int i ) {
	String s = Integer.toString( i );
	return s;

// 	s = s.replace('1','A');
// 	s = s.replace('2','B');
// 	s = s.replace('3','C');
// 	s = s.replace('4','D');
// 	s = s.replace('5','E');
// 	s = s.replace('6','F');
// 	s = s.replace('7','G');
// 	s = s.replace('8','H');
// 	s = s.replace('9','I');
// 	s = s.replace('0','J');
// 	return s;
    }
    
    public void visit( OWLDataValueRestriction restriction ) throws OWLException {
	/* Don't know what to do here....*/
	pw.print( "(" + renderer.shortForm( restriction.getDataProperty() ) + "(" + var + "," );
	pw.print( renderer.canonicalize( restriction.getValue() ) );
	pw.print("))");
	//	throw new RendererException(" Data restriction ");
    }

    public void visit( OWLDataCardinalityRestriction restriction ) throws OWLException {
	int min = -1;
	int max = -1;
	
	if ( restriction.isExactly() ) {
	    min = restriction.getAtLeast();
	    max = restriction.getAtMost();
	} else if ( restriction.isAtMost() ) {
	    max = restriction.getAtMost();
	} else 	if ( restriction.isAtLeast() ) {
	    min = restriction.getAtLeast();
	}

	doCardinality( restriction.getDataProperty(), min, max );
    }

    public void visit( OWLEquivalentClassesAxiom axiom ) throws OWLException {
	throw new RendererException(" Axiom ");
    }

    public void visit( OWLDisjointClassesAxiom axiom ) throws OWLException {
	throw new RendererException(" Axiom ");
    }

    public void visit( OWLSubClassAxiom axiom ) throws OWLException {
	throw new RendererException(" Axiom ");
    }

    public void visit( OWLEquivalentPropertiesAxiom axiom ) throws OWLException {
	throw new RendererException(" Axiom ");
    }

    public void visit( OWLSubPropertyAxiom axiom ) throws OWLException {
	throw new RendererException(" Axiom ");
    }

    public void visit( OWLDifferentIndividualsAxiom ax) throws OWLException {
	throw new RendererException(" Axiom ");
    }

    public void visit( OWLSameIndividualsAxiom ax) throws OWLException {
	throw new RendererException(" Axiom ");
    }

    public void visit( OWLDataType ocdt ) throws OWLException {
	pw.print( renderer.typeFor( ocdt ) + "(" + var + ")" );
    }
    

    public void visit( OWLDataEnumeration enumeration ) throws OWLException {
	if (enumeration.getValues().isEmpty()) {
	    pw.print("(cowlNothing(" + var + "))");
	} else {
	    pw.print("(");
	    for (Iterator it = enumeration.getValues().iterator(); it.hasNext(); ) {
		OWLDataValue dv = (OWLDataValue) it.next();
		pw.print( "equal(" + var + "," + 
			  renderer.canonicalize( dv ) + ")" );
		if (it.hasNext()) {
		    pw.print(" | ");
		}
	    }
	    pw.print(")");
	}
    }



    /* Returns a new, unbound variable */
    private String getNewUnboundVar() throws OWLException {
	for (int i = 0; i<variables.length; 
	     i++ ) {
	    if (!boundVariables.contains(variables[i])) {
		return variables[i];
	    }
	}
	throw new RendererException( "Ran out of variables!!!" );
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
 * Revision 1.3  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.2  2003/12/12 16:22:49  sean_bechhofer
 * Changes to TPTP renderer to cope with data/abstract domain split.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
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
