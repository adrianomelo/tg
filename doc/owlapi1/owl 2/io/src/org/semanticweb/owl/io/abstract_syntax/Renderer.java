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
 * Filename           $RCSfile: Renderer.java,v $
 * Revision           $Revision: 1.14 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package org.semanticweb.owl.io.abstract_syntax; // Generated package name


/**
 * Produces abstract syntax from OWL ontologies. 
 *
 *
 * Created: Fri Feb 21 09:12:03 2003
 *
 * @author Sean Bechhofer
 * @version $Id: Renderer.java,v 1.14 2006/03/28 16:14:45 ronwalf Exp $
 */
import java.io.PrintWriter;
import java.util.Set;
import java.util.List;
import java.util.Map;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFSVocabularyAdapter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.URI;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
//import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import java.io.StringWriter;
import java.io.Writer;
import org.semanticweb.owl.model.helper.OntologyHelper;
import java.net.URISyntaxException;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.SortedSet;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.apache.log4j.BasicConfigurator;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFSVocabularyAdapter;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import java.util.HashSet;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.io.ShortFormProvider;

public class Renderer implements org.semanticweb.owl.io.Renderer, ShortFormProvider
{

    /* NOTE: This renderer isn't very careful about where assertions
     * come from, so *all* information about classes, properties
     * etc. is rendered. */

    private PrintWriter pw;
    private Set allURIs;
    private List shortNames;
    private Map known;
    private int reservedNames;
    private RenderingVisitor visitor;
    private String[] names = {"a", "b", "c", "d", "e", "f", "g", "h", "i", 
		      "j", "k", "l", "m", "n", "o", "p", "q", "r", 
		      "s", "t", "u", "v", "w", "x", "y", "z", };
    
    public Renderer()
    {
    }

    public void setOptions( Map map ) {
    }

    public Map getOptions() {
	return null;
    }

    public void renderOntology( OWLOntology ontology,
				Writer writer ) throws RendererException {
	try {
	    this.pw = new PrintWriter( writer );
	    this.allURIs = OntologyHelper.allURIs( ontology );
	    this.visitor = new RenderingVisitor( this, ontology );
	    generateShortNames();
	    writeShortNames();
	    pw.println();
	    /* Print the ontology's logical URI */
	    pw.println("Ontology( " + shortForm( ontology.getURI() ) );
	    //	    pw.println("/* Classes */");
	    pw.println();
	    boolean done = false;

	    for ( Iterator it = ontology.getIncludedOntologies().iterator();
		  it.hasNext(); ) {
		/* Use the physical URI here.... */
		pw.println( " Annotation( owl:imports <" + ((OWLOntology) it.next()).getPhysicalURI() + ">)" );
		done = true;
	    }
	    if (done) {
		pw.println();
	    }
	    
	    if ( !ontology.getAnnotations( ontology ).isEmpty() ) {
		for ( Iterator it = ontology.getAnnotations( ontology ).iterator();
		      it.hasNext(); ) {
		    OWLAnnotationInstance oai = (OWLAnnotationInstance) it.next();
		    pw.print( " Annotation(" + shortForm( oai.getProperty().getURI() ) + " " );
		    /* Just whack out the content */
		    renderAnnotationContent( oai.getContent() );
		    //		    pw.print( "\"" + oai.getContent() + "\"" );
		    pw.println( ")" );
		    visitor.reset();
		    oai.accept( visitor );
		}
		done = true;
	    }
	    if (done) {
		pw.println();
	    }
	    
	    /* Moved properties to the top. Makes parsing easier. */

	    done = false;
	    for ( Iterator it = 
		      orderedEntities( ontology.getObjectProperties() ).iterator();
		  it.hasNext(); ) {
		done = true;
		renderObjectProperty( ontology, ( OWLObjectProperty ) it.next() );
	    }
	    //	    pw.println("/* Data Properties */");
	    if (done) {
		pw.println();
	    }
	    done = false;
	    for ( Iterator it = 
		      orderedEntities( ontology.getDataProperties() ).iterator();
		  it.hasNext(); ) {
		done = true;
		renderDataProperty( ontology, ( OWLDataProperty ) it.next() );
	    }
	    if (done) {
		pw.println();
	    }

	    done = false;
	    for ( Iterator it = 
		      orderedEntities( ontology.getClasses() ).iterator();
		  it.hasNext(); ) {
		done = true;
		renderClass( ontology, ( OWLClass ) it.next() );
	    }
	    //	    pw.println("/* Annotation Properties */");
	    if (done) {
		pw.println();
	    }
	    done = false;
	    for ( Iterator it = 
		      orderedEntities( ontology.getAnnotationProperties() ).iterator();
		  it.hasNext(); ) {
		OWLAnnotationProperty prop = ( OWLAnnotationProperty ) it.next();
		/* Write them out even if they're built in. */
		done = true;
		renderAnnotationProperty( ontology, prop );

// 		if ( !OWLVocabularyAdapter.INSTANCE.getAnnotationProperties().contains( prop.getURI().toString() ) ) {
// 		    done = true;
// 		    renderAnnotationProperty( ontology, prop );
// 		}
	    }
	    //	    pw.println("/* Object Properties */");
	    if (done) {
		pw.println();
	    }
	    done = false;
	    for ( Iterator it = 
		      orderedEntities( ontology.getIndividuals() ).iterator();
		  it.hasNext(); ) {
		done = true;
		renderIndividual( ontology, ( OWLIndividual ) it.next() );
	    }
	    //	    pw.println("/* Class Axioms */");
	    if (done) {
		pw.println();
	    }
	    done = false;

	    for ( Iterator it = 
		      orderedEntities( ontology.getDatatypes() ).iterator();
		  it.hasNext(); ) {
		done = true;
		renderDataType( ontology, ( OWLDataType ) it.next() );
	    }
	    //	    pw.println("/* Object Properties */");
	    if (done) {
		pw.println();
	    }
	    done = false;

	    for ( Iterator it = 
		      orderedEntities( ontology.getClassAxioms() ).iterator();
		  it.hasNext(); ) {
		done = true;
		renderClassAxiom( ( OWLClassAxiom ) it.next() );
	    } 
	    //	    pw.println("/* Individual Axioms */");
	    if (done) {
		pw.println();
	    }
	    done = false;
	    for ( Iterator it = 
		      orderedEntities( ontology.getPropertyAxioms() ).iterator();
		  it.hasNext(); ) {
		done = true;
		renderPropertyAxiom( ( OWLPropertyAxiom ) it.next() );
	    } 
	    if (done) {
		pw.println();
	    }
	    done = false;
	    for ( Iterator it = 
		      orderedEntities( ontology.getIndividualAxioms() ).iterator();
		  it.hasNext(); ) {
		done = true;
		renderIndividualAxiom( ( OWLIndividualAxiom ) it.next() );
	    } 
	    if (done) {
		pw.println();
	    }
	    pw.println(")");

	} catch (OWLException ex) {
	    throw new RendererException( ex.getMessage() );
	}
    }

    private void renderClass( OWLOntology ontology,
			      OWLClass clazz ) throws OWLException {
	boolean done = false;
	for ( Iterator it = clazz.getEquivalentClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println(" Class(" + shortForm( clazz.getURI() ) + 
		       (clazz.isDeprecated( ontology )?" Deprecated":"") +
		       " complete ");
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "  " + visitor.result() + ")" );
	    done = true;
	}

	if (!clazz.getSuperClasses( ontology ).isEmpty()) {
	    pw.println( " Class(" + shortForm( clazz.getURI() ) + 
			(clazz.isDeprecated( ontology )?" Deprecated":"") +
			" partial ");
	    
	    for ( Iterator it = clazz.getSuperClasses( ontology ).iterator();
		  it.hasNext(); ) {
		OWLDescription eq = (OWLDescription) it.next();
		visitor.reset();
		eq.accept( visitor );
		pw.print( "  " + visitor.result());
		if (it.hasNext()) {
		    pw.println();
		}
		done = true;
	    }
	    pw.println(")");
	}
	/* This has changed -- used to be simply a oneof in the class
	 * definition. We now get a special keyword in the
	 * vocabulary */
	for ( Iterator it = clazz.getEnumerations( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.print( " EnumeratedClass(" + shortForm( clazz.getURI() ) + 
			(clazz.isDeprecated( ontology )?" Deprecated":"") );
	    /* We know that the description has to be a oneof */
	    try {
		OWLEnumeration enumeration = (OWLEnumeration) eq;
		
		for ( Iterator iit = enumeration.getIndividuals().iterator();
		      iit.hasNext(); ) {
		    OWLIndividual desc = (OWLIndividual) iit.next();
		    visitor.reset();
		    desc.accept( visitor );
		    pw.print( " " + visitor.result() );
// 		    if (iit.hasNext()) {
// 			pw.print(" ");
// 		    }
		}
		pw.println( ")");
		done = true;
	    } catch (ClassCastException ex) {
		throw new RendererException( ex.getMessage() );
	    }
	}
	/* Bit nasty this -- annotations result in a new axiom */
	if ( !clazz.getAnnotations( ontology ).isEmpty() ) {
	    pw.println( " Class(" + shortForm( clazz.getURI() ) 
			+ " partial " );
	    for ( Iterator it = clazz.getAnnotations( ontology ).iterator();
		  it.hasNext(); ) {
		OWLAnnotationInstance oai = (OWLAnnotationInstance) it.next();
		pw.print( "  annotation(" + shortForm( oai.getProperty().getURI() ) + " " );
		/* Just whack out the content. This isn't quite right... */
		renderAnnotationContent( oai.getContent() );
		//		pw.print( "\"" + oai.getContent() + "\"" );
		pw.println( ")" );
		/* Do we need to do this??? */
		visitor.reset();
		oai.accept( visitor );
		// 		if (it.hasNext()) {
		// 		    pw.println();
		// 		}
	    }
	    pw.println(")");
	    done = true;
	}
	if (!done) {
	    /* We need to give at least an empty definition */ 
	    pw.println(" Class(" + shortForm( clazz.getURI() ) +
 		       (clazz.isDeprecated( ontology )?" Deprecated":"") + " partial" + 
		       ")");
	}
    }

    private void renderIndividual( OWLOntology ontology,
				   OWLIndividual ind ) throws OWLException {
	/* If the individual is anonymous and has any incoming
	 * properties, then we do not wish to show it here -- it will
	 * be rendered during the rendering of the thing that points
	 * to it. */
	if (ind.isAnonymous()) {
	    Map m = ind.getIncomingObjectPropertyValues( ontology ); 
	    if ( !m.isEmpty() ) {
		return;
	    }
	} 
	// 	if ( ind.isAnonymous() ) {
	// 	    pw.print(" Individual(" );
	// 	} else {
	pw.print(" Individual(" + shortForm( ind.getURI() ) );
	//	}
	if (ind.getAnnotations( ontology ).isEmpty() &&
	    ind.getTypes( ontology ).isEmpty() && 
	    ind.getObjectPropertyValues( ontology ).keySet().isEmpty() &&
	    ind.getDataPropertyValues( ontology ).keySet().isEmpty()) {
	    pw.println( ")");
	} else {
	    for ( Iterator it = ind.getAnnotations( ontology ).iterator();
		  it.hasNext(); ) {
		pw.println();
		OWLAnnotationInstance oai = (OWLAnnotationInstance) it.next();
		pw.print( "  annotation(" + shortForm( oai.getProperty().getURI() ) + " " );
		/* Just whack out the content */
		renderAnnotationContent( oai.getContent() );
		//		pw.print( oai.getContent() );
		pw.println( ")" );
		visitor.reset();
		oai.accept( visitor );
// 		if (it.hasNext()) {
// 		    pw.println();
// 		}
	    }

	    //	    pw.println();
	    for ( Iterator it = ind.getTypes( ontology ).iterator();
		  it.hasNext(); ) {
		pw.println();
		OWLDescription eq = (OWLDescription) it.next();
		visitor.reset();
		eq.accept( visitor );
		pw.print( "  type(" + visitor.result() + ")" );
// 		if (it.hasNext()) {
// 		    pw.println();
// 		}
	    }
	    Map propertyValues = ind.getObjectPropertyValues( ontology );
	    //	    System.out.println("ZZ: " + ind.getURI());
	    for ( Iterator it = propertyValues.keySet().iterator();
		  it.hasNext(); ) {
		pw.println();
		OWLObjectProperty prop = (OWLObjectProperty) it.next();
		Set vals = (Set) propertyValues.get(prop);
		for (Iterator valIt = vals.iterator(); valIt.hasNext();
		     ) {
		    //		    System.out.println("QQ: " + ((OWLIndividual) valIt.next()).getURI());
 		    OWLIndividual oi = (OWLIndividual) valIt.next();
		    visitor.reset();
		    oi.accept( visitor );
 		    pw.print( "  value(" + 
			      shortForm( prop.getURI() ) + " " + 
			      visitor.result() + ")" );
		    if (valIt.hasNext()) {
			pw.println();
		    }
		}
// 		if (it.hasNext()) {
// 		    pw.println();
// 		}
	    }
	    Map dataValues = ind.getDataPropertyValues( ontology );
	    //	    System.out.println("ZZ: " + ind.getURI());
	    for ( Iterator it = dataValues.keySet().iterator();
		  it.hasNext(); ) {
		pw.println();
		OWLDataProperty prop = (OWLDataProperty) it.next();
		Set vals = (Set) dataValues.get(prop);
		for (Iterator valIt = vals.iterator(); valIt.hasNext();
		     ) {
		    //		    System.out.println("QQ: " + ((OWLIndividual) valIt.next()).getURI());
 		    OWLDataValue dtv = (OWLDataValue) valIt.next();
		    visitor.reset();
		    dtv.accept( visitor );
 		    pw.print( "  value(" + 
			      shortForm( prop.getURI() ) + " " + 
			      visitor.result() + ")" );
		    if (valIt.hasNext()) {
			pw.println();
		    }
		}
// 		if (it.hasNext()) {
// 		    pw.println();
// 		}
	    }
	    pw.println(")");
	}
	

    }

    private void renderAnnotationProperty( OWLOntology ontology,
					   OWLAnnotationProperty prop ) throws OWLException {
	pw.println(" AnnotationProperty(" + shortForm( prop.getURI() ) + ")");
    }
    
    private void renderObjectProperty( OWLOntology ontology,
				       OWLObjectProperty prop ) throws OWLException {
	pw.print(" ObjectProperty(" + shortForm( prop.getURI() ));
	if ( prop.isTransitive( ontology ) ) {
	    pw.print(" Transitive");
	}
	if ( prop.isFunctional( ontology ) ) {
	    pw.print(" Functional");
	}
	if ( prop.isInverseFunctional( ontology ) ) {
	    pw.print(" InverseFunctional");
	}
	if ( prop.isSymmetric( ontology ) ) {
	    pw.print(" Symmetric");
	}
	for ( Iterator it = prop.getInverses( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    OWLObjectProperty inv = (OWLObjectProperty) it.next();
	    visitor.reset();
	    inv.accept( visitor );
	    pw.print( "  inverseOf(" + visitor.result() + ")");
	}	    
	for ( Iterator it = prop.getSuperProperties( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    OWLObjectProperty sup = (OWLObjectProperty) it.next();
	    visitor.reset();
	    sup.accept( visitor );
	    pw.print( "  super(" + visitor.result() + ")");
	}	    
	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    OWLDescription dom = (OWLDescription) it.next();
	    visitor.reset();
	    dom.accept( visitor );
	    pw.print( "  domain(" + visitor.result() + ")");
// 	    if (it.hasNext()) {
// 		pw.println();
// 	    }
	}
	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    OWLDescription ran = (OWLDescription) it.next();
	    visitor.reset();
	    ran.accept( visitor );
	    pw.print( "  range(" + visitor.result() + ")");
	}
	
	pw.println(")");
    }
    
    private void renderDataProperty( OWLOntology ontology,
				     OWLDataProperty prop ) throws OWLException {
	pw.print(" DatatypeProperty(" + shortForm( prop.getURI() ) );
	if ( prop.isFunctional( ontology ) ) {
	    pw.print(" Functional");
	}

	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    OWLDescription dom = (OWLDescription) it.next();
	    visitor.reset();
	    dom.accept( visitor );
	    pw.print( "  domain(" + visitor.result() + ")");
// 	    if (it.hasNext()) {
// 		pw.println();
// 	    }
	}
	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    OWLDataRange ran = (OWLDataRange) it.next();
	    visitor.reset();
	    ran.accept( visitor );
	    pw.print( "  range(" + visitor.result() + ")");
	}
	


	pw.println(")");

    }

    private void renderDataType( OWLOntology ontology,
				 OWLDataType datatype ) throws OWLException {
	pw.println(" Datatype(" + shortForm( datatype.getURI() ) + ")" );
    }

    private void renderClassAxiom( OWLClassAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(" " + visitor.result() );
    }

    private void renderPropertyAxiom( OWLPropertyAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(" " + visitor.result() );
    }

    private void renderIndividualAxiom( OWLIndividualAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(" " + visitor.result() );
    }

    /* Well dodgy coding */
    private void renderAnnotationContent( Object o ) throws OWLException {
	if ( o instanceof URI ) {
	    pw.print( o.toString() );
	} else if ( o instanceof OWLIndividual ) {
	    pw.print( ( (OWLIndividual) o ).getURI().toString() );
	} else if ( o instanceof OWLDataValue ) {
	    OWLDataValue dv = (OWLDataValue) o;
	    pw.print( "\"" + escape( dv.getValue() ) + "\"" );
	    /* Only show it if it's not string */
	    URI dvdt = dv.getURI();
	    String dvlang = dv.getLang();
	    if ( dvdt!=null) {
		pw.print( "^^" + dvdt );
// 		if (!dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getString())) {
// 		    pw.print( "^^" + dv.getURI() );
// 		}
	    } else {
		if (dvlang!=null) {
		    pw.print( "@" + dvlang );
		}
	    }
	} else {
	    pw.print( o.toString() );
	}
    }

    /* Replace " with \" and \ with \\ */
    public static String escape( Object o ) {
	/* Should probably use regular expressions */
	StringBuffer sw = new StringBuffer();
	String str = o.toString();
	for (int i = 0; i< str.length(); i++ ) {
	    char c = str.charAt( i );
	    if (c!='"' &&
		c!='\\') {
		sw.append( c );
	    } else {
		sw.append( '\\' );
		sw.append( c );
	    }
	}
	return sw.toString();
    }

    private void generateShortNames() {
	/* Generates a list of namespaces. */
	shortNames = new ArrayList();
	known = new HashMap();
	known.put( OWLVocabularyAdapter.INSTANCE.OWL, "owl" );
	known.put( RDFVocabularyAdapter.INSTANCE.RDF, "rdf" );
	known.put( RDFSVocabularyAdapter.INSTANCE.RDFS, "rdfs" );
	known.put( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.XS, "xsd" );

	for ( Iterator it = allURIs.iterator();
	      it.hasNext(); ) {
	    try {
		URI uri = (URI) it.next();
		if (uri.getFragment()!=null) {
		    String ssp = new URI( uri.getScheme(),
					  uri.getSchemeSpecificPart(),
					  null ).toString();
		    /* Trim off the fragment bit if necessary */
		    if ( !ssp.endsWith("#") ) {
			ssp = ssp + "#";
		    }
		    if ( !known.keySet().contains( ssp ) && 
			 !shortNames.contains( ssp ) ) {
			shortNames.add( ssp );
		    }
		}
	    } catch ( URISyntaxException ex ) {
	    }
	}
    }
    
    private void writeShortNames() {
	/* Changed to fit with "concrete abstract syntax" */
	//	pw.println("[Namespaces: ");
	for ( Iterator it=known.keySet().iterator();
	      it.hasNext(); ) {
	    String ns = (String) it.next();
	    String shrt = (String) known.get( ns );
	    pw.println( "Namespace(" + shrt + "\t= <" + ns + ">)" );
	}
	for ( int i=0; i<shortNames.size(); i++ ) {
	    if (i < names.length) {
		String ns = (String) shortNames.get( i );
		pw.println( "Namespace(" + names[i] + "\t= <" + ns + ">)" );
	    }
	}
	//	pw.println("]");

// 	pw.println("[Namespaces: ");
// 	for ( Iterator it=known.keySet().iterator();
// 	      it.hasNext(); ) {
// 	    String ns = (String) it.next();
// 	    String shrt = (String) known.get( ns );
// 	    pw.println("  " + shrt + "\t= " + ns);
// 	}
// 	for ( int i=0; i<shortNames.size(); i++ ) {
// 	    if (i < names.length) {
// 		String ns = (String) shortNames.get( i );
// 		pw.println("  " + names[i] + "\t= " + ns);
// 	    }
// 	}
// 	pw.println("]");
    }

    public String shortForm( URI uri ) {
	if (uri==null) {
	    return "_";
	}
	try {
	    if ( uri.getFragment()==null ||
		 uri.getFragment().equals("") ) {
		/* It's not of the form http://xyz/path#frag */
		return "<" + uri.toString() + ">";
	    }
	    /* It's of the form http://xyz/path#frag */
	    String ssp = new URI( uri.getScheme(),
				  uri.getSchemeSpecificPart(),
				  null ).toString();
	    if ( !ssp.endsWith("#") ) {
		ssp = ssp + "#";
	    }
	    if ( known.keySet().contains( ssp ) ) {
		return (String) known.get( ssp ) + ":" + 
		    uri.getFragment();
	    }
	    if ( shortNames.contains( ssp ) ) {
		/* Check whether the fragment is ok, e.g. just
		 * contains letters. */
		String frag = uri.getFragment();
		boolean fragOk = true;
		/* This is actually quite severe -- URIs allow other
		 * stuff in here, but for our concrete syntax we
		 * don't, only letters, numbers and _ */
		for (int i=0; i<frag.length(); i++) {
 		    fragOk = fragOk && 
 			( Character.isLetter(frag.charAt( i ) ) ||
 			  Character.isDigit(frag.charAt( i ) )  ||
			  frag.charAt( i ) == '_' );
		}
		if ( fragOk &&
		     (shortNames.indexOf( ssp )) < names.length) {
		    return (names[ shortNames.indexOf( ssp ) ]) + 
			":" + 
			frag;
		}
		/* We can't shorten it -- there are too many...*/
		return "<" + uri.toString() + ">";	
	    }
 	} catch ( URISyntaxException ex ) {
	}
	return "<" + uri.toString() + ">";	
    }

    /* Return a collection, ordered by the URIs. */
    private SortedSet orderedEntities( Set entities ) { 
	SortedSet ss = new TreeSet(new Comparator() {
		public int compare( Object o1, Object o2) {
		    try {
			return ((OWLEntity) o1).getURI().toString().compareTo( ((OWLEntity) o2).getURI().toString());
		    } catch ( Exception ex ) {
			return o1.toString().compareTo( o2.toString() );
		    }
		}});
	ss.addAll( entities );
	return ss;
    }

    public static void main( String[] args ) {
	try {
	    BasicConfigurator.configure();

	    URI uri = new URI( args[0] );

	    OWLOntology onto =
		OntologyHelper.getOntology(uri);
	    Renderer renderer = new Renderer();
	    Writer writer = new StringWriter();
	    renderer.renderOntology( onto, writer );
	    System.out.println( writer.toString() );
	} catch ( Exception ex ) {
	    ex.printStackTrace();
	}
    }
    
    
} // Renderer



/*
 * ChangeLog
 * $Log: Renderer.java,v $
 * Revision 1.14  2006/03/28 16:14:45  ronwalf
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
 * Revision 1.13  2004/05/14 10:37:11  sean_bechhofer
 * no message
 *
 * Revision 1.12  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.11  2004/03/05 17:34:48  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.10  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.9  2003/12/11 12:37:03  sean_bechhofer
 * Adding Ontology logical URI
 *
 * Revision 1.8  2003/12/02 13:09:54  sean_bechhofer
 * Handling quotes and backslash in comments. Reordering of definitions.
 * Properties now come first.
 *
 * Revision 1.7  2003/12/02 10:04:41  sean_bechhofer
 * Minor rendering fixes
 *
 * Revision 1.6  2003/11/28 11:04:35  sean_bechhofer
 * Minor rendering fixes
 *
 * Revision 1.5  2003/11/20 12:58:13  sean_bechhofer
 * Addition of language handling in OWLDataValues.
 *
 * Revision 1.4  2003/11/18 17:50:05  sean_bechhofer
 * Rendering built in annotation props.
 *
 * Revision 1.3  2003/11/18 13:54:25  sean_bechhofer
 * Fix to abstract syntax rendering.
 *
 * Revision 1.2  2003/11/12 11:08:35  sean_bechhofer
 * Changes to harmonise with new Concrete Abstract Syntax proposal.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.22  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.21  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.20  2003/10/01 16:51:09  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.19  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.18  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.17  2003/09/22 13:21:44  bechhofers
 * Fixes to rendering and some extra helper functions.
 *
 * Revision 1.16  2003/09/10 16:45:08  bechhofers
 * Minor rendering changes.
 *
 * Revision 1.15  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.14  2003/08/28 10:29:04  bechhofers
 * Updating parser to improve validation. Addition of new consumer with
 * simple triple model.
 *
 * Revision 1.13  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.12  2003/06/19 13:32:37  seanb
 * Addition of construct checking. Change to parser to do imports properly.
 *
 * Revision 1.11  2003/06/11 16:47:47  seanb
 * Minor changes.
 *
 * Revision 1.10  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.9  2003/05/19 16:24:49  seanb
 * no message
 *
 * Revision 1.8  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.7  2003/05/06 14:26:53  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 *
 * Revision 1.6  2003/04/10 12:15:28  rvolz
 * Removed/Added in refactoring process
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
 * Revision 1.2  2003/03/21 13:55:40  seanb
 * Added domain/range handling.
 *
 * Revision 1.1  2003/03/20 10:26:34  seanb
 * Adding Abstract Syntax Renderer
 *
 */
