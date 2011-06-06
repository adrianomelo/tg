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
 * Revision           $Revision: 1.5 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:46 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.html;
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
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.model.OWLAnnotationProperty;

import java.net.URI;
// Generated package name


/**
 * RenderingVisitor.java
 *
 *
 * Created: Fri Feb 21 10:57:24 2003
 *
 * @author Sean Bechhofer
 * @version $Id: RenderingVisitor.java,v 1.5 2006/03/28 16:14:46 ronwalf Exp $
 */

public class RenderingVisitor extends OWLObjectVisitorAdapter 
{

    StringWriter sw;
    PrintWriter pw;
    int level;
    boolean indenting = true;
    Linker linker;
    OWLOntology ontology;

    public RenderingVisitor( Linker l, OWLOntology o )
    {
	this.linker = l; 
	this.ontology = o;
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
	pw.print( linker.linkFor( clazz ) );
    }
    public void visit( OWLIndividual ind ) throws OWLException {
	RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
	if ( ind.isAnonymous() ) {
	    /* We need to print out the entire description... */
	    pw.print("Individual(_" );
	    if (ind.getTypes( ontology ).isEmpty() && 
		ind.getObjectPropertyValues( ontology ).keySet().isEmpty() &&
		ind.getDataPropertyValues( ontology ).keySet().isEmpty()) {
		pw.print( ")");
	    } else {
		for ( Iterator it = ind.getTypes( ontology ).iterator();
		      it.hasNext(); ) {
		    OWLDescription eq = (OWLDescription) it.next();
		    visitor.reset();
		    eq.accept( visitor );
		    pw.print( " type(" + visitor.result() + ")" );
		}
		Map propertyValues = ind.getObjectPropertyValues( ontology );
		
		for ( Iterator it = propertyValues.keySet().iterator();
		      it.hasNext(); ) {
		    OWLObjectProperty prop = (OWLObjectProperty) it.next();
		    Set vals = (Set) propertyValues.get(prop);
		    for (Iterator valIt = vals.iterator(); valIt.hasNext();
			 ) {
			OWLIndividual oi = (OWLIndividual) valIt.next();
			visitor.reset();
			oi.accept( visitor );
			pw.print( " value(" + 
				  linker.linkFor( prop ) + " " + 
				  visitor.result() + ")" );
		    }
		}
		Map dataValues = ind.getDataPropertyValues( ontology );
		
		for ( Iterator it = dataValues.keySet().iterator();
		      it.hasNext(); ) {
		    OWLDataProperty prop = (OWLDataProperty) it.next();
		    Set vals = (Set) dataValues.get(prop);
		    for (Iterator valIt = vals.iterator(); valIt.hasNext();
			 ) {
			OWLDataValue dtv = (OWLDataValue) valIt.next();
			visitor.reset();
			dtv.accept( visitor );
			pw.print( "value(" + 
				  linker.linkFor( prop ) + " " + 
				  visitor.result() + ")" );
			if (it.hasNext()) {
			    pw.print(" ");
			}
		    }
		}
		pw.print(")");
	    }
	} else {
	    pw.print( linker.linkFor( ind ) );
	}
    }

    public void visit( OWLObjectProperty prop ) throws OWLException {
	pw.print( linker.linkFor( prop ) );
    }
    public void visit( OWLDataProperty prop ) throws OWLException {
	pw.print( linker.linkFor( prop ) );
    }
    public void visit( OWLDataValue cd ) throws OWLException {

	pw.print( "\"" + cd.getValue() + "\"");

	URI dvdt = cd.getURI();
	String dvlang = cd.getLang();
	if ( dvdt!=null) {
	    pw.print( "^^" + dvdt );
	} else {
	    if (dvlang!=null) {
		pw.print( "@" + dvlang );
	    }
	}
    }

    public void visit( OWLAnd and ) throws OWLException {
	pw.print("<span class='keyword'>intersectionOf</span>(");
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
	pw.print("<span class='keyword'>unionOf</span>(");
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
	pw.print("<span class='keyword'>complementOf</span>(");
	OWLDescription desc = (OWLDescription) not.getOperand();
	desc.accept( this );
	pw.print(")");
    }

    public void visit( OWLEnumeration enumeration ) throws OWLException {
	pw.print("<span class='keyword'>oneOf</span>(");
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
	pw.print("<span class='keyword'>restriction</span>(");
	restriction.getObjectProperty().accept( this );
	pw.print(" <span class='keyword'>someValuesFrom</span> ");
	restriction.getDescription().accept( this );
	pw.print(")");
    }

    public void visit( OWLObjectAllRestriction restriction ) throws OWLException {
	pw.print("<span class='keyword'>restriction</span>(");
	restriction.getObjectProperty().accept( this );
	pw.print(" <span class='keyword'>allValuesFrom</span> ");
	restriction.getDescription().accept( this );
	pw.print(")");
    }

    public void visit( OWLObjectValueRestriction restriction ) throws OWLException {
	pw.print("<span class='keyword'>restriction</span>(");
	restriction.getObjectProperty().accept( this );
	pw.print(" <span class='keyword'>hasValue</span> ");
	restriction.getIndividual().accept( this );
	pw.print(")");
    }


    public void visit( OWLObjectCardinalityRestriction restriction ) throws OWLException {
	pw.print("<span class='keyword'>restriction</span>(");
	restriction.getObjectProperty().accept( this );
	if ( restriction.isExactly() ) {
	    pw.print(" <span class='keyword'>cardinality</span>(" + restriction.getAtLeast() + "))");
	} else if ( restriction.isAtMost() ) {
	    pw.print(" <span class='keyword'>maxCardinality</span>(" + restriction.getAtMost() + "))");
	} else 	if ( restriction.isAtLeast() ) {
	    pw.print(" <span class='keyword'>minCardinality</span>(" + restriction.getAtLeast() + "))");
	} 
    }

    public void visit( OWLDataCardinalityRestriction restriction ) throws OWLException {
	pw.print("<span class='keyword'>restriction</span>(");
	restriction.getDataProperty().accept( this );
	if ( restriction.isExactly() ) {
	    pw.print(" <span class='keyword'>cardinality</span>(" + restriction.getAtLeast() + "))");
	} else if ( restriction.isAtMost() ) {
	    pw.print(" <span class='keyword'>maxCardinality</span>(" + restriction.getAtMost() + "))");
	} else 	if ( restriction.isAtLeast() ) {
	    pw.print(" <span class='keyword'>minCardinality</span>(" + restriction.getAtLeast() + "))");
	} 
    }

    public void visit( OWLDataValueRestriction restriction ) throws OWLException {
	pw.print("<span class='keyword'>restriction</span>(");
	restriction.getDataProperty().accept( this );
	pw.print(" <span class='keyword'>hasValue</span> ");
	restriction.getValue().accept( this );
	pw.print(")");
    }

    public void visit( OWLEquivalentClassesAxiom axiom ) throws OWLException {
	pw.print("<span class='keyword'>EquivalentClasses</span>(");
	for ( Iterator it = axiom.getEquivalentClasses().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	    if (it.hasNext()) {
		pw.print(" ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLDisjointClassesAxiom axiom ) throws OWLException {
	pw.print("<span class='keyword'>DisjointClasses</span>(");
	for ( Iterator it = axiom.getDisjointClasses().iterator();
	      it.hasNext(); ) {
	    OWLDescription desc = (OWLDescription) it.next();
	    desc.accept( this );
	    if (it.hasNext()) {
		pw.print(" ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLSubClassAxiom axiom ) throws OWLException {
	pw.print("<span class='keyword'>SubClassOf</span>(");
	axiom.getSubClass().accept( this );
	pw.print(" ");
	axiom.getSuperClass().accept( this );
	pw.print(")");
    }

    public void visit( OWLEquivalentPropertiesAxiom axiom ) throws OWLException {
	pw.print("<span class='keyword'>EquivalentProperties</span>(");
	for ( Iterator it = axiom.getProperties().iterator();
	      it.hasNext(); ) {
	    OWLProperty prop = (OWLProperty) it.next();
	    prop.accept( this );
	    if (it.hasNext()) {
		pw.print(" ");
	    }
	}
	pw.print(")");
    }

    public void visit( OWLSubPropertyAxiom axiom ) throws OWLException {
	pw.print("<span class='keyword'>SubPropertyOf</span>(");
	axiom.getSubProperty().accept( this );
	pw.print(" ");
	axiom.getSuperProperty().accept( this );
	pw.print(")");
    }

    public void visit( OWLDifferentIndividualsAxiom ax) throws OWLException {
	pw.print("<span class='keyword'>DifferentIndividuals</span>(");
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

    public void visit( OWLSameIndividualsAxiom ax) throws OWLException {
	pw.print("<span class='keyword'>SameIndividual</span>(");
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
	pw.print( ocdt.getURI() );
    }

    public void visit( OWLAnnotationInstance oai ) throws OWLException {
	pw.print( linker.linkFor( oai.getProperty() ) + " " );
	/* Just whack out the content */
	Object o = oai.getContent();
	if ( o instanceof URI ) {
	    pw.print( o.toString() );
	} else if ( o instanceof OWLIndividual ) {
	    pw.print( ( (OWLIndividual) o ).getURI().toString() );
	} else if ( o instanceof OWLDataValue ) {
	    OWLDataValue dv = (OWLDataValue) o;
	    pw.print( "\"" + dv.getValue() + "\"" );
	    /* Only show it if it's not string */
	    URI dvdt = dv.getURI();
	    String dvlang = dv.getLang();
	    if ( dvdt!=null) {
		pw.print( "^^" + dvdt );
	    } else {
		if (dvlang!=null) {
		    pw.print( "@" + dvlang );
		}
	    }
	} else {
	    pw.print( o.toString() );
	}
    }

    public void visit( OWLAnnotationProperty prop ) throws OWLException {
	pw.print( linker.linkFor( prop ) );
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
 * Revision 1.5  2006/03/28 16:14:46  ronwalf
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
 * Revision 1.4  2004/10/19 17:32:36  sean_bechhofer
 * Cosmetic changes
 *
 * Revision 1.3  2004/07/09 14:04:59  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
 *
 * Revision 1.2  2003/11/28 11:04:36  sean_bechhofer
 * Minor rendering fixes
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/10/02 14:33:06  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.1  2003/07/05 16:58:14  bechhofers
 * Adding an HTML servlet-based renderer and some changes to the
 * inferencing classes.
 *
 * Revision 1.12  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.11  2003/05/16 11:34:47  seanb
 * Further renaming of individual to object in the implementations
 * of restrictions.
 *
 * Revision 1.10  2003/05/15 13:51:42  seanb
 * Addition of new non-streaming parser.
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
