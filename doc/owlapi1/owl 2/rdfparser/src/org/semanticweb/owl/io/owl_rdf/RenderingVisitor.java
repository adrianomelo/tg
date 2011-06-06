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
 * Last modified on   $Date: 2006/03/28 16:14:46 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package org.semanticweb.owl.io.owl_rdf;
import org.semanticweb.owl.model.helper.OWLObjectVisitorAdapter;
import java.io.PrintWriter;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentPropertiesAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;
import java.util.Iterator;
import java.util.Set;

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
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;

import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDataEnumeration;

//import org.semanticweb.owl.io.abstract_syntax.*;
// Generated package name

/**
 * RenderingVisitor.java
 *
 *
 * Created: Fri Feb 21 10:57:24 2003
 *
 * @author Sean Bechhofer
 * @version $Id: RenderingVisitor.java,v 1.6 2006/03/28 16:14:46 ronwalf Exp $
 */

public class RenderingVisitor extends OWLObjectVisitorAdapter implements RenderingConstants {
    
    //    ShortFormProvider shortForms;
    StringWriter sw;
    PrintWriter pw;
    int level;
    boolean indenting = true;
    
    /* These are used when rendering axioms. */
    String property;
    OWLObject propertyObject;

    public RenderingVisitor(org.semanticweb.owl.io.Renderer renderer) {
	//        this.shortForms = null;
        reset();
	level = 0;
    }

    /** Set the level that this renderer should start indenting at. */
    public void setLevel( int i ) {
	level = i;
    }

    private void setProperty( String p ) {
	property = p;
    }

    private void setPropertyObject( OWLObject d ) {
	propertyObject = d;
    }

    public RenderingVisitor() {
	//        this.shortForms = null;
        reset();
	property = null;
	propertyObject = null;
    }

    public String result() {
        return sw.toString();
    }

    public void reset() {
        sw = new StringWriter();
        pw = new PrintWriter(sw);
    }

    public void visit(OWLClass clazz) throws OWLException {
        pw.println( indent() + "<owl:Class rdf:about=\"" + clazz.getURI().toString() + "\">");
	level++;
	addPropertyObject();
	level--;
	pw.println( indent() + "</owl:Class>");
    }

    public void visit(OWLIndividual ind) throws OWLException {
        pw.println( indent() + "<owl:Thing rdf:about=\"" + ind.getURI().toString() + "\">");
	level++;
	addPropertyObject();
	level--;
	pw.println( indent() + "</owl:Thing>");
    }
    
    public void visit(OWLEquivalentPropertiesAxiom axiom) throws OWLException {
        Set properties = axiom.getProperties();
        if (properties.size() == 2) {
            Object[] props =  properties.toArray();
            OWLProperty p1 = (OWLProperty) props[0];
            OWLProperty p2 = (OWLProperty) props[1];

	    setProperty( "owl:equivalentProperty" );
	    setPropertyObject( p1 );
	    p2.accept( this );
        } else {
	    throw new OWLException("Multiple Property Equivalence not yet supported");
        }
    }

    public void visit(OWLObjectProperty prop) throws OWLException {
        pw.println( indent() + "<owl:ObjectProperty rdf:about=\"" + prop.getURI().toString() + "\">");
	level++;
	addPropertyObject();
	level--;
	pw.println( indent() + "</owl:ObjectProperty>");
    }

    public void visit(OWLDataProperty prop) throws OWLException {
        pw.println( indent() + "<owl:DatatypeProperty rdf:about=\"" + prop.getURI().toString() + "\">");
	level++;
	addPropertyObject();
	level--;
	pw.println( indent() + "</owl:DatatypeProperty>");
    }

    public void visit(OWLAnd and) throws OWLException {
	pw.println( indent() + "<owl:Class>");
	level++;
	addPropertyObject();
        pw.println( indent() + "<owl:intersectionOf rdf:parseType=\"Collection\">");
        for (Iterator it = and.getOperands().iterator(); it.hasNext();) {
            OWLDescription desc = (OWLDescription) it.next();
	    level++;
            desc.accept(this);
	    level--;
        }
        pw.println( indent() + "</owl:intersectionOf>");
	level--;
	pw.println( indent() + "</owl:Class>");
    }

    public void visit(OWLOr or) throws OWLException {
	pw.println( indent() + "<owl:Class>");
	level++;
	addPropertyObject();
        pw.println( indent() +"<owl:unionOf rdf:parseType=\"Collection\">");
        for (Iterator it = or.getOperands().iterator(); it.hasNext();) {
            OWLDescription desc = (OWLDescription) it.next();
	    level++;
            desc.accept(this);
	    level--;
        }
        pw.println( indent() +"</owl:unionOf>");
	level--;
	pw.println( indent() +"</owl:Class>");
    }

    public void visit(OWLNot not) throws OWLException {
	pw.println( indent() +"<owl:Class>");
	level++;
	addPropertyObject();
        pw.println( indent() +"<owl:complementOf>");
        OWLDescription desc = (OWLDescription) not.getOperand();
	level++;
	desc.accept(this);
	level--;
        pw.println( indent() +"</owl:complementOf>");
	level--;
	pw.println( indent() +"</owl:Class>");
    }

    public void visit(OWLEnumeration enumeration) throws OWLException {
	pw.println( indent() +"<owl:Class>");
	level++;
	addPropertyObject();
        pw.println( indent() +"<owl:oneOf rdf:parseType=\"Collection\">");
        for (Iterator it = enumeration.getIndividuals().iterator();
            it.hasNext();
            ) {
            OWLIndividual desc = (OWLIndividual) it.next();
            pw.print("");
	    level++;
            desc.accept(this);
	    level--;
        }
        pw.println( indent() +"</owl:oneOf>");
	level--;
	pw.println( indent() +"</owl:Class>");
    }

    public void visit(OWLObjectSomeRestriction restriction)
        throws OWLException {
        pw.println( indent() +"<owl:Restriction>");
	level++;
	addPropertyObject();
        pw.println( indent() +
            "<owl:onProperty rdf:resource=\""
                + restriction.getObjectProperty().getURI().toString()
                + "\" />");
        pw.println( indent() +"<owl:someValuesFrom>");
	level++;
        restriction.getDescription().accept(this);
	level--;
        pw.println( indent() +"</owl:someValuesFrom>");
	level--;
        pw.println( indent() +"</owl:Restriction>");
    }

    public void visit(OWLObjectAllRestriction restriction)
        throws OWLException {
        pw.println( indent() +"<owl:Restriction>");
	level++;
	addPropertyObject();
        pw.println( indent() +
            "<owl:onProperty rdf:resource=\""
                + restriction.getObjectProperty().getURI().toString()
                + "\" />");
        pw.println( indent() +"<owl:allValuesFrom>");
	level++;
        restriction.getDescription().accept(this);
	level--;
        pw.println( indent() +"</owl:allValuesFrom>");
	level--;
        pw.println( indent() +"</owl:Restriction>");
    }

    public void visit(OWLObjectValueRestriction restriction)
        throws OWLException {
        pw.println( indent() +"<owl:Restriction>");
	level++;
	addPropertyObject();
        pw.println( indent() +
            "<owl:onProperty rdf:resource=\""
                + restriction.getObjectProperty().getURI().toString()
                + "\" />");
        pw.println( indent() +
            "<owl:hasValue rdf.resource=\""
                + restriction.getIndividual().getURI()
                + "\" />");
	level--;
        pw.println( indent() +"</owl:Restriction>");
    }

    public void visit(OWLObjectCardinalityRestriction restriction)
        throws OWLException {
        pw.println( indent() +"<owl:Restriction>");
	level++;
	addPropertyObject();
        pw.println( indent() +
            "<owl:onProperty rdf:resource=\""
                + restriction.getObjectProperty().getURI().toString()
                + "\" />");
        if (restriction.isExactly()) {
            pw.println( indent() +
                "<owl:cardinality rdf:datatype=\"&xsd;nonNegativeInteger\">"
                    + restriction.getAtMost()
                    + "</owl:cardinality>");
        } else if (restriction.isAtMost()) {
            pw.println( indent() +
                "<owl:maxCardinality rdf:datatype=\"&xsd;nonNegativeInteger\">"
                    + restriction.getAtMost()
                    + "</owl:maxCardinality>");
        } else if (restriction.isAtLeast()) {
            pw.println( indent() +
                "<owl:minCardinality rdf:datatype=\"&xsd;nonNegativeInteger\">"
                    + restriction.getAtLeast()
                    + "</owl:minCardinality>");

        }
	level--;
        pw.println( indent() +"</owl:Restriction>");
    }

    /********************************************************/

    public void visit(OWLDataSomeRestriction restriction)
        throws OWLException {
        pw.println( indent() +"<owl:Restriction>");
	level++;
	addPropertyObject();
        pw.println( indent() +
		    "<owl:onProperty rdf:resource=\""
		    + restriction.getDataProperty().getURI().toString()
		    + "\" />");
        pw.println( indent() +"<owl:someValuesFrom>");
	level++;
        restriction.getDataType().accept(this);
	level--;
        pw.println( indent() +"</owl:someValuesFrom>");
	level--;
        pw.println( indent() +"</owl:Restriction>");
    }

    public void visit(OWLDataAllRestriction restriction)
        throws OWLException {
        pw.println( indent() +"<owl:Restriction>");
	level++;
	addPropertyObject();
        pw.println( indent() +
		    "<owl:onProperty rdf:resource=\""
		    + restriction.getDataProperty().getURI().toString()
		    + "\" />");
        pw.println( indent() +"<owl:allValuesFrom>");
	level++;
        restriction.getDataType().accept(this);
	level--;
        pw.println( indent() +"</owl:allValuesFrom>");
	level--;
        pw.println( indent() +"</owl:Restriction>");
    }

    public void visit(OWLDataValueRestriction restriction)
        throws OWLException {
        pw.println( indent() +"<owl:Restriction>");
	level++;
	addPropertyObject();
        pw.println( indent() +
		    "<owl:onProperty rdf:resource=\""
		    + restriction.getDataProperty().getURI().toString()
		    + "\" />");
        pw.print( indent() +
		  "<owl:hasValue" );
	
	OWLDataValue odv = restriction.getValue();
	if (odv.getURI()!=null) {
	    pw.print(" rdf:datatype=\""
		     + odv.getURI() + "\"" );
	}
	if (odv.getLang()!=null) {
	    pw.print(" xml:lang=\""
		     + odv.getLang() + "\"" );
	}
	pw.print(">");
	pw.print(odv.getValue().toString());
	pw.println( "</owl:hasValue>");
	level--;
        pw.println( indent() + "</owl:Restriction>");
    }

    public void visit(OWLDataCardinalityRestriction restriction)
        throws OWLException {
        pw.println( indent() +"<owl:Restriction>");
	level++;
	addPropertyObject();
        pw.println( indent() +
            "<owl:onProperty rdf:resource=\""
                + restriction.getDataProperty().getURI().toString()
                + "\" />");
        if (restriction.isExactly()) {
            pw.println( indent() +
                "<owl:cardinality rdf:datatype=\"&xsd;nonNegativeInteger\">"
                    + restriction.getAtMost()
                    + "</owl:cardinality>");
        } else if (restriction.isAtMost()) {
            pw.println( indent() +
                "<owl:maxCardinality rdf:datatype=\"&xsd;nonNegativeInteger\">"
                    + restriction.getAtMost()
                    + "</owl:maxCardinality>");
        } else if (restriction.isAtLeast()) {
            pw.println( indent() +
                "<owl:minCardinality rdf:datatype=\"&xsd;nonNegativeInteger\">"
                    + restriction.getAtLeast()
                    + "</owl:minCardinality>");

        }
	level--;
        pw.println( indent() +"</owl:Restriction>");
    }


    public void visit(OWLDataType datatype) throws OWLException {
        pw.println( indent() + "<rdfs:Datatype rdf:about=\"" + datatype.getURI().toString() + "\"/>");
    }


    /* Aaargh. This one's horrible as it requires us to use an
     * rdf:List (see
     * http://www.w3.org/TR/owl-ref/#EnumeratedDatatype). Oh dear. */

    public void visit(OWLDataEnumeration enumeration) throws OWLException {
	throw new OWLException("Data Enumeration Rendering not yet supported");
    }

    /********************************************************/


    /* The renderings of equivalent class and disjoint class axioms
     * aren't quite optimal. For example, the disjoint axioms one will
     * introduce a load of bilateral disjoints rather than some big
     * one. */

    /* This might not deal properly with singleton equivalence
     * axioms. But they have no semantic import so who cares. */

    public void visit(OWLEquivalentClassesAxiom axiom) throws OWLException {	
	/* Turn it into an array so we can do an easier iteration
	 * over pairs */
	OWLDescription[] descs = (OWLDescription[])
	    axiom.getEquivalentClasses().toArray( new OWLDescription[axiom.getEquivalentClasses().size()] );
	for (int i=0; i<descs.length-1; i++) {
	    setProperty( "owl:equivalentClass" );
	    setPropertyObject( descs[i] );
	    descs[i+1].accept( this );
	}
    }

    public void visit(OWLDisjointClassesAxiom axiom) throws OWLException {
	/* Turn it into an array so we can do an iteration over
	 * pairs */
	OWLDescription[] descs = (OWLDescription[])
	    axiom.getDisjointClasses().toArray( new OWLDescription[axiom.getDisjointClasses().size()] );
	for (int i=0; i<descs.length; i++) {
	    for (int j=i+1; j<descs.length; j++) {
		setProperty( "owl:disjointWith" );
		setPropertyObject( descs[i] );
		descs[j].accept( this );
	    }
	}
    }

    public void visit(OWLSubClassAxiom axiom) throws OWLException {
	OWLDescription sub = axiom.getSubClass();
	OWLDescription sup = axiom.getSuperClass();

	setProperty( "rdfs:subClassOf" );
	setPropertyObject( sup );
	sub.accept( this );
    }
    

    public void visit(OWLSubPropertyAxiom axiom) throws OWLException {
	OWLProperty sub = axiom.getSubProperty();
	OWLProperty sup = axiom.getSuperProperty();

	setProperty( "rdfs:subPropertyOf" );
	setPropertyObject( sup );
	sub.accept( this );
    }

    /* This is used when we're rendering axioms. It allows us to
     * insert extra stuff within a class definition. This allows us to do
     * things like:
     * 
     <pre>
     &lt;owl:Class&gt;
      &lt;rdfs:subClassOf&gt;
       &lt;owl:Class&gt;
       ...
       &lt;/owl:Class&gt;
      &lt;/rdf:subClassOf&gt;
      ...
      &lt;/owl:Class&gt;
     </pre>

     Note that because of the way that this works, we need to call it
     before doing other recursive visits, or things get screwed
     up. Leads to some ugly RDF, but hey, that's not *my*
     problem..... */

    private void addPropertyObject() throws OWLException {
	if (property!=null) {
	    /* We've got a property so off we go. */
	    String name = property;
	    pw.println( indent() +"<" + name + ">");
	    /* Null the property */
	    setProperty ( null );
	    propertyObject.accept( this );
	    pw.println( indent() +"</" + name + ">");
	}
    }	

    private String indent() {
	StringBuffer sb = new StringBuffer();
	for (int i=0;i<level; i++) {
	    sb.append(INDENT);
	}
	return sb.toString();
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
 * Revision 1.6  2006/03/28 16:14:46  ronwalf
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
 * Revision 1.5  2004/02/05 17:55:50  sean_bechhofer
 * Minor fixes.
 *
 * Revision 1.4  2004/02/05 17:36:19  sean_bechhofer
 * RDF/XML Rendering support. Still some minor holes, but almost there.
 *
 * Revision 1.3  2004/02/05 11:48:11  sean_bechhofer
 * Changes to OWLConsumer to improve efficiency in handling large
 * file validation.
 *
 * Revision 1.2  2003/12/03 16:24:13  sean_bechhofer
 * Minor updates -- this still needs work.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:18  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/10/03 16:06:41  bechhofers
 * Refactoring of source and tests to break dependencies on implementation
 * classes.
 *
 * Revision 1.3  2003/04/10 12:15:28  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.2  2003/03/26 19:04:02  rvolz
 * *** empty log message ***
 *
 * Revision 1.1  2003/03/20 19:35:11  rvolz
 * Initial Version, missing are Import, OntologyMetadata, rdf:datatype stuff. No extensive testing. Parser seems to have problems with things attached to Properties (super, domain etc.)
 *
 * Revision 1.1  2003/03/20 10:26:34  seanb
 * Adding Abstract Syntax Renderer
 *
 */
