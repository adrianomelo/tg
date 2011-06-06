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
 * Revision           $Revision: 1.6 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.dig1_0; 
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;
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

// Generated package name


/**
 * RenderingVisitor.java
 *
 *
 * Created: Fri Feb 21 10:57:24 2003
 *
 * @author Sean Bechhofer
 * @version $Id: RenderingVisitor.java,v 1.6 2006/03/28 16:14:45 ronwalf Exp $
 */

public class RenderingVisitor extends OWLObjectVisitorAdapter 
{

    StringWriter sw;
    PrintWriter pw;
    int level;

    public RenderingVisitor( )
    {
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
	pw.print( "<catom name=\"" + clazz.getURI() + "\"/>");
    }

    public void visit( OWLIndividual ind ) throws OWLException {
	/* Ignoring anonymous ones.... */
	if (ind.isAnonymous()) {
	    /* Almighty hack...*/

	    /* Used defindividual. Changed to individual. SKB 18/11/03 */
	    pw.println("<individual name=\"anon-" + ind.hashCode() + "\"/>" );
	} else {
	    pw.println("<individual name=\"" + ind.getURI() + "\"/>" );
	}
    }

    public void visit( OWLObjectProperty prop ) throws OWLException {
	pw.print( "<ratom name=\"" + prop.getURI() + "\"/>");
    }
    public void visit( OWLDataProperty prop ) throws OWLException {
	pw.print( "<attribute name=\"" + prop.getURI() + "\"/>");
    }
    public void visit( OWLDataValue cd ) throws OWLException {
	/* This isn't ideal, but the assumption is that this only gets
	 * called at the "right" time */
	//	pw.print( "[" + cd.getValue()+ "^^" + cd.getURI() + "]" );
	pw.print( cd.getValue() );
    }

    public void visit( OWLAnd and ) throws OWLException {
	pw.print("<and>");
	for ( Iterator it = and.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	}
	pw.print("</and>");
    }

    public void visit( OWLOr or ) throws OWLException {
	pw.print("<or>");
	for ( Iterator it = or.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	}
	pw.print("</or>");
    }

    public void visit( OWLNot not ) throws OWLException {
	pw.print("<not>");
	OWLDescription desc = (OWLDescription) not.getOperand();
	desc.accept( this );
	pw.print("</not>");
    }

    public void visit( OWLEnumeration enumeration ) throws OWLException {
	pw.print("<iset>");
	for ( Iterator it = enumeration.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLIndividual desc = (OWLIndividual) it.next();
	    desc.accept( this );
	}
	pw.print("</iset>");
    }

    public void visit( OWLObjectSomeRestriction restriction ) throws OWLException {
	pw.print("<some>");
	restriction.getObjectProperty().accept( this );
	restriction.getDescription().accept( this );
	pw.print("</some>");
    }

    public void visit( OWLObjectAllRestriction restriction ) throws OWLException {
	pw.print("<all>");
	restriction.getObjectProperty().accept( this );
	restriction.getDescription().accept( this );
	pw.print("</all>");
    }

    public void visit( OWLObjectValueRestriction restriction ) throws OWLException {
	pw.print("<some>");
	restriction.getObjectProperty().accept( this );
	pw.print("<iset>");
	restriction.getIndividual().accept( this );
	pw.print("</iset>");
	pw.print("</some>");
    }


    public void visit( OWLObjectCardinalityRestriction restriction ) throws OWLException {
	if ( restriction.isExactly() ) {
	    pw.print("<and>");
	    pw.print("<atmost num=\"" + restriction.getAtLeast() + "\">");
	    restriction.getObjectProperty().accept( this );
	    pw.print("<top/>");
	    pw.println("</atmost>");
	    pw.print("<atleast num=\"" + restriction.getAtLeast() + "\">");
	    restriction.getObjectProperty().accept( this );
	    pw.print("<top/>");
	    pw.println("</atleast>");
	    pw.print("</and>");
	} else if ( restriction.isAtMost() ) {
	    pw.print("<atmost num=\"" + restriction.getAtMost() + "\">");
	    restriction.getObjectProperty().accept( this );
	    pw.print("<top/>");
	    pw.println("</atmost>");
	} else 	if ( restriction.isAtLeast() ) {
	    pw.print("<atleast num=\"" + restriction.getAtLeast() + "\">");
	    restriction.getObjectProperty().accept( this );
	    pw.print("<top/>");
	    pw.println("</atleast>");
	} 
    }

    public void visit( OWLDataCardinalityRestriction restriction ) throws OWLException {
	/* Don't really know what to do here -- ideally we want DIG to
	 * allow unqualified cardinality restrictions. */
	throw new OWLException(" Data cardinality restriction. ");
	
// 	if ( restriction.isExactly() ) {
// 	    pw.print("<and>");
// 	    pw.print("<atmost num=\"" + restriction.getAtLeast() + "\">");
// 	    restriction.getDataProperty().accept( this );
// 	    pw.print("<top/>");
// 	    pw.println("</atmost>");
// 	    pw.print("<atleast num=\"" + restriction.getAtLeast() + "\">");
// 	    restriction.getDataProperty().accept( this );
// 	    pw.print("<top/>");
// 	    pw.println("</atleast>");
// 	    pw.print("</and>");
// 	} else if ( restriction.isAtMost() ) {
// 	    pw.print("<atmost num=\"" + restriction.getAtMost() + "\">");
// 	    restriction.getDataProperty().accept( this );
// 	    pw.print("<top/>");
// 	    pw.println("</atmost>");
// 	} else 	if ( restriction.isAtLeast() ) {
// 	    pw.print("<atleast num=\"" + restriction.getAtLeast() + "\">");
// 	    restriction.getDataProperty().accept( this );
// 	    pw.print("<top/>");
// 	    pw.println("</atleast>");
// 	} 
    }


    public void visit( OWLDataAllRestriction node ) throws OWLException
    {
	/* The problem here is that there isn't really an expression
	 * in DIG that corresponds to a data all restriction. In some
	 * cases we could translate to a local range but it would be
	 * rather unpleasant bespoke code. Instead, we choose to throw
	 * an exception here. */
	throw new OWLException(" Can't handle DataAll restriction\n\t" + node.getDataProperty().getURI() + " " + node.getDataType() );
    }

    public void visit( OWLDataSomeRestriction node ) throws OWLException
    {
	/* The problem here is that there isn't really an expression
	 * in DIG that corresponds to a data some restriction. In some
	 * cases we could translate to a local range but it would be
	 * rather unpleasant bespoke code. Instead, we choose to throw
	 * an exception here. */
	throw new OWLException(" Can't handle DataSome restriction\n\t" + node.getDataProperty().getURI() + " " + node.getDataType() );
    }
    
    public void visit( OWLDataValueRestriction node ) throws OWLException
    {
	/* If the datavalue is a string or int, we can handle
	 * it. Otherwise, throw an exception. */
	OWLDataValue dv = node.getValue();
	java.net.URI type = dv.getURI();
	if (type==null) {
	    /* Assume string */
	    OWLDataValue val = node.getValue();
	    pw.print("<stringequals val=\"");
	    val.accept( this );
	    pw.print("\">");
	    node.getDataProperty().accept( this );
	    pw.print("</stringequals>");
	} else if (type.toString().equals(org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getString()) ) {
	    OWLDataValue val = node.getValue();
	    pw.print("<stringequals val=\"");
	    val.accept( this );
	    pw.print("\">");
	    node.getDataProperty().accept( this );
	    pw.print("</stringequals>");
	} else if (type.toString().equals(org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInteger()) || 
               type.toString().equals(XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInt())) {
	    OWLDataValue val = node.getValue();
	    pw.print("<intequals val=\"");
	    val.accept( this );
	    pw.print("\">");
	    node.getDataProperty().accept( this );
	    pw.print("</intequals>");
	} else {
	    throw new OWLException(" Can't handle DataValue restriction\n\t" + node.getDataProperty().getURI() + " " + node.getValue() );
	}
	return;
    }

    public void visit( OWLEquivalentClassesAxiom axiom ) throws OWLException {
	Object [] eqs = axiom.getEquivalentClasses().toArray();
	for (int i=0; i< eqs.length; i++) {
	    for (int j=i+1; j< eqs.length; j++) {
		OWLDescription desc1 = (OWLDescription) eqs[i];
		OWLDescription desc2 = (OWLDescription) eqs[j];
		pw.println("<equalc>");
		desc1.accept( this );
		pw.println();
		desc2.accept( this );
		pw.println();
		pw.println("</equalc>");
	    }
	}
    }

    public void visit( OWLDisjointClassesAxiom axiom ) throws OWLException {
	Object [] disjs = axiom.getDisjointClasses().toArray();
	for (int i=0; i< disjs.length; i++) {
	    for (int j=i+1; j< disjs.length; j++) {
		OWLDescription desc1 = (OWLDescription) disjs[i];
		OWLDescription desc2 = (OWLDescription) disjs[j];
		pw.println("<disjoint>");
		desc1.accept( this );
		pw.println();
		desc2.accept( this );
		pw.println();
		pw.println("</disjoint>");
	    }
	}
    }

    public void visit( OWLSubClassAxiom axiom ) throws OWLException {
	pw.println("<impliesc>");
	axiom.getSubClass().accept( this );
	pw.println();
	axiom.getSuperClass().accept( this );
	pw.println();
	pw.print("</impliesc>");
    }

    public void visit( OWLEquivalentPropertiesAxiom axiom ) throws OWLException {
	Object [] eqs = axiom.getProperties().toArray();
	for (int i=0; i< eqs.length; i++) {
	    for (int j=i+1; j< eqs.length; j++) {
		OWLProperty prop1 = (OWLProperty) eqs[i];
		OWLProperty prop2 = (OWLProperty) eqs[j];
		pw.println("<equalr>");
		prop1.accept( this );
		pw.println();
		prop2.accept( this );
		pw.println();
		pw.println("</equalr>");
	    }
	}
    }

    public void visit( OWLSubPropertyAxiom axiom ) throws OWLException {
	pw.print("<impliesr>");
	axiom.getSubProperty().accept( this );
	pw.println();
	axiom.getSuperProperty().accept( this );
	pw.println();
	pw.print("</impliesr>");
    }

    public void visit( OWLDifferentIndividualsAxiom ax) throws OWLException {
	/* This is here in DIG due to UNA.... */
    }

    public void visit( OWLSameIndividualsAxiom ax) throws OWLException {
	/* What do we do with this???? */
    }

    public void visit( OWLDataType ocdt ) throws OWLException {
	/* What do we do with this???? */
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
 * Revision 1.5  2004/02/10 15:22:48  dturi
 * line endings.
 *
 * Revision 1.4  2004/01/28 15:12:30  dturi
 * Refined previous update by adding the int case to the previously 
 * added string and integer.
 *
 * Revision 1.3  2004/01/28 12:53:14  sean_bechhofer
 * Minor update to DIG renderer to handle concrete data values.
 *
 * Revision 1.2  2003/11/18 17:49:01  sean_bechhofer
 * Fix to one-of rendering
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/09/15 16:09:02  bechhofers
 * Minor change to DIG renderer to add axioms concerning thing
 * and nothing and to fix anonymous individuals.
 *
 * Revision 1.3  2003/09/11 15:42:03  bechhofers
 * no message
 *
 * Revision 1.2  2003/06/03 17:01:53  seanb
 * Additional inference
 *
 * Revision 1.1  2003/05/19 11:59:12  seanb
 * DIG Renderer
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
