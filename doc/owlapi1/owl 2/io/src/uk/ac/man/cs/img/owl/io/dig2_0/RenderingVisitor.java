/*
 * Copyright (C) 2006 The University of Karlsruhe
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
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/20 13:09:27 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.dig2_0; 
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
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLNot;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
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
 * @version $Id: RenderingVisitor.java,v 1.1 2006/03/20 13:09:27 sean_bechhofer Exp $
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
	pw.print( "<class URI=\"" + Renderer.mapName( clazz.getURI() ) + "\"/>");
    }

    public void visit( OWLIndividual ind ) throws OWLException {
	/* Ignoring anonymous ones.... */
	if (ind.isAnonymous()) {
	    /* Almighty hack...*/

	    /* Used defindividual. Changed to individual. SKB 18/11/03 */
	    pw.print("<individual URI=\"anon-" + ind.hashCode() + "\"/>" );
	} else {
	    pw.print("<individual URI=\"" + Renderer.mapName( ind.getURI() ) + "\"/>" );
	}
    }

    public void visit( OWLObjectProperty prop ) throws OWLException {
	pw.print( "<objectProperty URI=\"" + Renderer.mapName( prop.getURI() ) + "\"/>");
    }
    public void visit( OWLDataProperty prop ) throws OWLException {
	pw.print( "<dataProperty URI=\"" + Renderer.mapName( prop.getURI() ) + "\"/>");
    }
    public void visit( OWLDataValue cd ) throws OWLException {
	/* This isn't ideal, but the assumption is that this only gets
	 * called at the "right" time */
	//	pw.print( "[" + cd.getValue()+ "^^" + cd.getURI() + "]" );
	pw.print( "<dataLiteral>" + cd.getValue() + "</dataLiteral>" );
    }

    public void visit( OWLAnd and ) throws OWLException {
	pw.println("<intersectionOf>");
	for ( Iterator it = and.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	    pw.println();
	}
	pw.print("</intersectionOf>");
    }

    public void visit( OWLOr or ) throws OWLException {
	pw.println("<unionOf>");
	for ( Iterator it = or.getOperands().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	    pw.println();
	}
	pw.print("</unionOf>");
    }

    public void visit( OWLNot not ) throws OWLException {
	pw.println("<complementOf>");
	OWLDescription desc = (OWLDescription) not.getOperand();
	desc.accept( this );
	pw.println();
	pw.print("</complementOf>");
    }

    public void visit( OWLEnumeration enumeration ) throws OWLException {
	pw.println("<oneOf>");
	for ( Iterator it = enumeration.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLIndividual desc = (OWLIndividual) it.next();
	    desc.accept( this );
	    pw.println();
	}
	pw.print("</oneOf>");
    }

    public void visit( OWLObjectSomeRestriction restriction ) throws OWLException {
	pw.println("<someValuesFrom>");
	restriction.getObjectProperty().accept( this );
	pw.println();
	restriction.getDescription().accept( this );
	pw.println();
	pw.print("</someValuesFrom>");
    }

    public void visit( OWLObjectAllRestriction restriction ) throws OWLException {
	pw.println("<allValuesFrom>");
	restriction.getObjectProperty().accept( this );
	pw.println();
	restriction.getDescription().accept( this );
	pw.println();
	pw.print("</allValuesFrom>");
    }

    public void visit( OWLObjectValueRestriction restriction ) throws OWLException {
	pw.println("<hasValue>");
	restriction.getObjectProperty().accept( this );
	restriction.getIndividual().accept( this );
	pw.println();
	pw.print("</hasValue>");
    }


    public void visit( OWLObjectCardinalityRestriction restriction ) throws OWLException {
	if ( restriction.isExactly() ) {
	    pw.println("<cardinality num=\"" + restriction.getAtLeast() + "\">");
	    restriction.getObjectProperty().accept( this );
	    pw.println();
	    pw.print("</cardinality>");
	} else if ( restriction.isAtMost() ) {
	    pw.println("<maxCardinality num=\"" + restriction.getAtMost() + "\">");
	    restriction.getObjectProperty().accept( this );
	    pw.println();
	    pw.print("</maxCardinality>");
	} else 	if ( restriction.isAtLeast() ) {
	    pw.println("<minCardinality num=\"" + restriction.getAtLeast() + "\">");
	    restriction.getObjectProperty().accept( this );
	    pw.println();
	    pw.print("</minCardinality>");
	} 
    }

    public void visit( OWLDataCardinalityRestriction restriction ) throws OWLException {
	if ( restriction.isExactly() ) {
	    pw.println("<cardinality num=\"" + restriction.getAtLeast() + "\">");
	    restriction.getDataProperty().accept( this );
	    pw.println();
	    pw.print("</cardinality>");
	} else if ( restriction.isAtMost() ) {
	    pw.println("<maxCardinality num=\"" + restriction.getAtMost() + "\">");
	    restriction.getDataProperty().accept( this );
	    pw.println();
	    pw.print("</maxCardinality>");
	} else 	if ( restriction.isAtLeast() ) {
	    pw.println("<minCardinality num=\"" + restriction.getAtLeast() + "\">");
	    restriction.getDataProperty().accept( this );
	    pw.println();
	    pw.print("</minCardinality>");
	} 
    }

    public void visit( OWLDataSomeRestriction restriction ) throws OWLException {
	pw.println("<someValuesFrom>");
	restriction.getDataProperty().accept( this );
	pw.println();
	restriction.getDataType().accept( this );
	pw.println();
	pw.print("</someValuesFrom>");
    }

    public void visit( OWLDataAllRestriction restriction ) throws OWLException {
	pw.println("<allValuesFrom>");
	restriction.getDataProperty().accept( this );
	pw.println();
	restriction.getDataType().accept( this );
	pw.println();
	pw.print("</allValuesFrom>");
    }

    public void visit( OWLDataValueRestriction restriction ) throws OWLException {
	pw.println("<hasValue");
	restriction.getDataProperty().accept( this );
	pw.println();
	restriction.getValue().accept( this );
	pw.println();
	pw.print("</hasValue>");
    }

    public void visit( OWLEquivalentClassesAxiom axiom ) throws OWLException {
	Object [] eqs = axiom.getEquivalentClasses().toArray();
	for (int i=0; i< eqs.length; i++) {
	    for (int j=i+1; j< eqs.length; j++) {
		OWLDescription desc1 = (OWLDescription) eqs[i];
		OWLDescription desc2 = (OWLDescription) eqs[j];
		pw.println("<equivalentClass>");
		desc1.accept( this );
		pw.println();
		desc2.accept( this );
		pw.println();
		pw.println("</equivalentClass>");
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
	pw.println("<subClass>");
	axiom.getSubClass().accept( this );
	pw.println();
	axiom.getSuperClass().accept( this );
	pw.println();
	pw.print("</subClass>");
    }

    public void visit( OWLEquivalentPropertiesAxiom axiom ) throws OWLException {
	Object [] eqs = axiom.getProperties().toArray();
	for (int i=0; i< eqs.length; i++) {
	    for (int j=i+1; j< eqs.length; j++) {
		OWLProperty prop1 = (OWLProperty) eqs[i];
		OWLProperty prop2 = (OWLProperty) eqs[j];
		pw.println("<equivalenProperty>");
		prop1.accept( this );
		pw.println();
		prop2.accept( this );
		pw.println();
		pw.println("</equivalentProperty>");
	    }
	}
    }

    public void visit( OWLSubPropertyAxiom axiom ) throws OWLException {
	pw.println("<subProperty>");
	axiom.getSubProperty().accept( this );
	pw.println();
	axiom.getSuperProperty().accept( this );
	pw.println();
	pw.print("</subProperty>");
    }

    public void visit( OWLDifferentIndividualsAxiom ax) throws OWLException {
	pw.println("<different>");
	for ( Iterator it = ax.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLIndividual desc = (OWLIndividual) it.next();
	    desc.accept( this );
	    pw.println();
	}
	pw.print("</different>");
    }

    public void visit( OWLSameIndividualsAxiom ax) throws OWLException {
	pw.println("<same>");
	for ( Iterator it = ax.getIndividuals().iterator();
	      it.hasNext(); ) {
	    OWLIndividual desc = (OWLIndividual) it.next();
	    desc.accept( this );
	    pw.println();
	}
	pw.print("</same>");
    }

    public void visit( OWLDataType ocdt ) throws OWLException {
	pw.print("<datatypeExpression/>");
    }

} // RenderingVisitor



/*
 * ChangeLog
 * $Log: RenderingVisitor.java,v $
 * Revision 1.1  2006/03/20 13:09:27  sean_bechhofer
 * Draft renderer for DIG 2.0
 *
 *
 *
 */
