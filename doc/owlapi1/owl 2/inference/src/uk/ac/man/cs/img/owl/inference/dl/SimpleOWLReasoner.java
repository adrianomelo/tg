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
 * Filename           $RCSfile: SimpleOWLReasoner.java,v $
 * Revision           $Revision: 1.5 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/10/19 17:18:32 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.inference.dl; 
import java.util.Set;
import java.util.HashSet;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.inference.OWLClassReasoner;
import org.semanticweb.owl.model.change.OntologyChangeListener;

import uk.ac.man.cs.img.dig.reasoner.Reasoner;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.OntologyChangeSource;
import java.util.Map;
import java.util.HashMap;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.Iterator;
import uk.ac.man.cs.img.owl.io.dig1_0.RenderingVisitor;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import org.semanticweb.owl.validation.SpeciesValidator;
import org.semanticweb.owl.io.Renderer;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.List;
import org.semanticweb.owl.inference.OWLTaxonomyReasoner;
import org.semanticweb.owl.inference.OWLIndividualReasoner;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.model.helper.OntologyHelper;
// Generated package name


/**
 * An OWL reasoner that uses a DIG reasoner to do the work.
 *
 * @author Sean Bechhofer
 * @version $Id: SimpleOWLReasoner.java,v 1.5 2004/10/19 17:18:32 sean_bechhofer Exp $
 */

public class SimpleOWLReasoner implements OWLClassReasoner, OWLIndividualReasoner, OntologyChangeListener, ReasonerProxy
{

    /* TODO: Adjust this to fit in with the OWLDLOntology. */


    /* A simple (and inefficient) OWL Reasoner. This particular
       implementation will not track ontology updates in a sensible
       manner. Instead, it simply translates the entire ontology to
       the reasoner in one go. If any changes occur, it transmits it
       all over again. This is sufficient for the implementation
       of a simple checker though. */

    /* The ontology being reasoned over */
    private OWLOntology ontology;

    /* The reasoner doing the reasoning */
    private Reasoner digReasoner;
    
    /* For rendering DIG concepts */
    private RenderingVisitor renderer;

    /* For checking the species */
    private SpeciesValidator validator;

    /* Flags that indicate the current status of the ontology w.r.t
     * the statements that have been transmitted */

    private static int OK = 0;
    private static int NOTOK = 1;
    private static int UNKNOWN = 2;

    private int status; 

    /**
     * Create a new reasoning object. Expects a DIG reasoner that will
     * do the actual work of reasoning about the class structure.
     *
     * @param digReasoner a <code>Reasoner</code> value
     */
    public SimpleOWLReasoner( Reasoner digReasoner ) throws OWLException {
	this.digReasoner = digReasoner;
	this.renderer = new RenderingVisitor();
	this.validator = new uk.ac.man.cs.img.owl.validation.SpeciesValidator();
	/* Quiet validator */
	validator.setReporter(new SpeciesValidatorReporter() {
		public void ontology( OWLOntology onto ) {
		}
		
		public void done( String str ) {
		}
		
		public void message( String str ) {
		}
		
		public void explain( int l, String str ) {
		}

		public void explain( int l, int code, String str ) {
		}
	    });
	this.status = UNKNOWN;
    }

    /** Set the ontology that the reasoner knows about. The reasoner
     * will then transmit the converted ontology to the DIG
     * reasoner. */
    public void setOntology( OWLOntology onto ) throws OWLException {
	/* Stop listening to any ontologies. */
	if (ontology!=null && ontology instanceof OntologyChangeSource) {
	    ((OntologyChangeSource) ontology).removeOntologyChangeListener( this );
	    ontology = null;
	}
	if ( onto!=null ) {
	    /* Check the species. */ 
	    this.ontology = onto;
	    if ( ontology instanceof OntologyChangeSource ) {
		((OntologyChangeSource) ontology).addOntologyChangeListener( this );
	    }
	    status = UNKNOWN;
	    checkStatus();
	}
    }
    
    private void initialiseReasoner() throws OWLException {
	/* Initialise the kb and make sure that top and bottom are in there. */
	tell( "<clearKB/>");
	tell( "<defconcept name=\"" + OWLVocabularyAdapter.INSTANCE.getThing() + "\"/>");
	tell( "<equalc><catom name=\"" + OWLVocabularyAdapter.INSTANCE.getThing() + "\"/><top/></equalc>");
	tell( "<defconcept name=\"" + OWLVocabularyAdapter.INSTANCE.getNothing() + "\"/>");
	tell( "<equalc><catom name=\"" + OWLVocabularyAdapter.INSTANCE.getNothing() + "\"/><bottom/></equalc>");
    }
    
    public void tell( String str ) throws OWLException {
	StringWriter sw = new StringWriter();
	sw.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" );
	sw.write( "<tells xmlns=\"http://dl.kr.org/dig/lang\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dl.kr.org/dig/lang dig.xsd\">" );
	sw.write( str );
	sw.write( "</tells>" );
	StringWriter response = new StringWriter();
	try {
	    digReasoner.request( new StringReader( sw.toString() ), response );
	} catch (Exception e) {
	    throw new OWLException( e.getMessage() );
	}
    }
    
    public OWLOntology getOntology() {
	return ontology;
    }

    /* Send the ontology off to the DIG reasoner */
    private void transmitOntology() throws OWLException {
	initialiseReasoner();
	try {
	    /* Now tell the ontology to the reasoner. */
	    Renderer renderer =
		new uk.ac.man.cs.img.owl.io.dig1_0.Renderer();
	    StringWriter sw = new StringWriter();
	    renderer.renderOntology( ontology, sw );
	    
	    StringWriter response = new StringWriter();
	    digReasoner.request( new StringReader( sw.toString() ), response );
	} catch (Exception e) {
	    throw new OWLException( e.getMessage() );
	}
    }


    /* Basic reasoning tasks */

    /** Returns true if d1 is a subclass of d2. */
    public boolean isSubClassOf( OWLDescription d1,
				 OWLDescription d2 ) throws OWLException {
	checkStatus();
	StringWriter sw = new StringWriter();
	sw.write("<asks xmlns=\"http://dl.kr.org/dig/lang\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dl.kr.org/dig/lang dig.xsd\">");
	
	sw.write("<subsumes id=\"q\">");
	/* Add the rendered description here */
	renderer.reset();
	d2.accept( renderer );
	sw.write(renderer.result());

	renderer.reset();
	d1.accept( renderer );
	sw.write(renderer.result());
	sw.write("</subsumes>");
	sw.write("</asks>");
	
	StringWriter response = new StringWriter();
	try {
	    digReasoner.request(new StringReader(sw.toString()), response);
	} catch (Exception e) {
	    throw new OWLException( e.getMessage() );
	}

	
	uk.ac.man.cs.img.dig.helper.Response serverResponse =
	    new uk.ac.man.cs.img.dig.helper.Response(response.toString());
	
	org.w3c.dom.Element r =
	    serverResponse.extractResponse("q");
	return (r.getTagName().equals("true")); 
    }
    
    /** Returns true if d1 is equivalent to d2. */
    public boolean isEquivalentClass( OWLDescription d1,
				      OWLDescription d2 ) throws OWLException {
	return ( isSubClassOf( d1, d2 ) & 
		 isSubClassOf( d2, d1 ) );
    }

    /** Returns true if the description is consistent (i.e. if it is *
	possible for there to exist models in which the extension of
	the class is non-empty. */
    public boolean isConsistent( OWLDescription d1 ) throws OWLException {
	checkStatus();
	StringWriter sw = new StringWriter();
	sw.write("<asks xmlns=\"http://dl.kr.org/dig/lang\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dl.kr.org/dig/lang dig.xsd\">");
	
	sw.write("<satisfiable id=\"q\">");
	/* Add the rendered description here */
	renderer.reset();
	d1.accept( renderer );
	sw.write(renderer.result());
	sw.write("</satisfiable>");
	sw.write("</asks>");
	
	StringWriter response = new StringWriter();
	try {
	    digReasoner.request(new StringReader(sw.toString()), response);
	} catch (Exception e) {
	    throw new OWLException( e.getMessage() );
	}

	
	uk.ac.man.cs.img.dig.helper.Response serverResponse =
	    new uk.ac.man.cs.img.dig.helper.Response(response.toString());
	
	org.w3c.dom.Element r =
	    serverResponse.extractResponse("q");
	return (r.getTagName().equals("true")); 
    }

    public boolean isConsistent( ) throws OWLException {
	notImplemented("isConsistent");
	return false;
    }

    /* Reasoning tasks relating to classification */
    
    /** Returns the collection of (named) most specific superclasses
	of the given description. The result of this will be a set of
	sets, where each set in the collection represents an
	equivalence class. */

    public Set superClassesOf( OWLDescription d ) throws OWLException {
	checkStatus();
	return getHierarchy( "parents", d );
    }

    /** Returns the collection of (named) most general subclasses
	of the given description. The result of this will be a set of
	sets, where each set in the collection represents an
	equivalence class. */

    public Set subClassesOf( OWLDescription d ) throws OWLException {
	checkStatus();
	return getHierarchy( "children", d );
    }

    public Set ancestorClassesOf( OWLDescription d ) throws OWLException {
	checkStatus();
	return getHierarchy( "ancestors", d );
    }

    public Set descendantClassesOf( OWLDescription d ) throws OWLException {
	checkStatus();
	return getHierarchy( "descendants", d );
    }

    /* Returns the collection of (named) classes which are equivalent
     * to the given description. */

    public Set equivalentClassesOf( OWLDescription d) throws OWLException {
	checkStatus();
	return getHierarchy( "equivalents", d );
    }


    private Set getHierarchy( String queryTag, OWLDescription d ) throws OWLException {
	StringWriter sw = new StringWriter();
	Set result = new HashSet();
	sw.write("<asks xmlns=\"http://dl.kr.org/dig/lang\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dl.kr.org/dig/lang dig.xsd\">");
	
	sw.write("<" + queryTag + " id=\"q\">");
	/* Add the rendered description here */
	renderer.reset();
	d.accept( renderer );
	sw.write(renderer.result());
	sw.write("</" + queryTag + ">");
	sw.write("</asks>");
	
	StringWriter response = new StringWriter();
	try {
	    digReasoner.request(new StringReader(sw.toString()), response);
	} catch (Exception e) {
	    throw new OWLException( e.getMessage() );
	}

	
	uk.ac.man.cs.img.dig.helper.Response serverResponse =
	    new uk.ac.man.cs.img.dig.helper.Response(response.toString());
	
	org.w3c.dom.Element r =
	    serverResponse.extractResponse("q");
	
	List syns = serverResponse.extractSynonymSets( r );
	for (Iterator it = syns.iterator(); it.hasNext(); ) {
	    Set innerSet = new HashSet();
	    List names = (List) it.next() ;
	    for (Iterator nameIt = names.iterator();
		 nameIt.hasNext();) {
		uk.ac.man.cs.img.dig.helper.Response.Concept c =
		    ((uk.ac.man.cs.img.dig.helper.Response.Concept) nameIt.next());
		OWLClass clazz = conceptToClass( c );
		if (clazz!=null) {
		    innerSet.add( clazz );
		}
	    }
	    result.add( innerSet );
	}
	return result;
    }

    private OWLClass conceptToClass( uk.ac.man.cs.img.dig.helper.Response.Concept c ) throws OWLException{
	try {
	    if (c.isBottom()) {
		OWLClass clazz = ontology.getClass( new URI( OWLVocabularyAdapter.INSTANCE.getNothing() ) );
		//		System.out.println( clazz );
		return clazz;
	    } else if (c.isTop()) {
		return ontology.getClass( new URI( OWLVocabularyAdapter.INSTANCE.getThing() ) );
	    } else {
		uk.ac.man.cs.img.dig.helper.Response.ConceptName cn = 
		    (uk.ac.man.cs.img.dig.helper.Response.ConceptName) c;
		//		System.out.println( "xx " + cn.toString() );
		URI classURI  = 
		    new URI( cn.toString() );
		/* This is wrong! The problem is that the URI may not
		 * be of a class *in* this ontology (due to the way
		 * that we handle imports). So rather than getting the
		 * class from the ontology, we need to get the class
		 * from the data factory.  */
		//		OWLClass clazz = ontology.getClass( classURI );
		OWLClass clazz = ontology.getOWLDataFactory().getOWLClass( classURI );
		//		System.out.println( clazz );
		return clazz;
	    }
	} catch (URISyntaxException ex) {
	    throw new OWLException( ex.getMessage() );
	}
    }

    public void ontologyChanged( OntologyChange event ) throws OWLException {
	/* A change has occurred. For this simple implementation, we
	 * simple flag that it's changed and then worry about that
	 * later. */
	/* The ontology has changed so we're no longer sure of its status. */
	status = UNKNOWN;
    }

    /* Check the status of the ontology. If it's been changed, we need
     * to check its species. If that's ok, then we need to send it off
     * to the reasoner. */
    private void checkStatus() throws OWLException { 
	if (status==UNKNOWN) {
	    if (!validator.isOWLDL( ontology ) ) {
		status = NOTOK;
	    } else {
		/* The status was unknown (which means a change
		 * occurred), so we need to send it to the reasoner
		 * again. */
		status = OK;
		transmitOntology();
	    }
	}
	if (status==NOTOK) {
	    throw new ExpressivenessOutOfScopeException( "Ontology: " + ontology.getURI() + " is not OWL DL!\nThis reasoner can only reason about DL ontologies.");
	} 
    }

    public boolean isInstanceOf(OWLIndividual i, OWLDescription d) throws OWLException {
	notImplemented("isInstanceOf");
	return false;
    }

    /** Returns all the instances of the given class. */
    public Set instancesOf( OWLDescription d1 ) throws OWLException {
	Set result = new HashSet();

	try {
	    checkStatus();
	    StringWriter sw = new StringWriter();
	    sw.write("<asks xmlns=\"http://dl.kr.org/dig/lang\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://dl.kr.org/dig/lang dig.xsd\">");
	
	    sw.write("<instances id=\"q\">");
	    /* Add the rendered description here */
	    renderer.reset();
	    d1.accept( renderer );
	    sw.write(renderer.result());
	    sw.write("</instances>");
	    sw.write("</asks>");
	
	    StringWriter response = new StringWriter();
	    try {
		digReasoner.request(new StringReader(sw.toString()), response);
	    } catch (Exception e) {
		throw new OWLException( e.getMessage() );
	    }

	
	    uk.ac.man.cs.img.dig.helper.Response serverResponse =
		new uk.ac.man.cs.img.dig.helper.Response(response.toString());
	
	    org.w3c.dom.Element r =
		serverResponse.extractResponse("q");
	
	    List l = serverResponse.extractIndividuals( r );
	    /* For some reason, RACER is returning duplicates */
	    java.util.Set seen = new java.util.HashSet();
	    for (int j=0; j<l.size(); j++) {
		String name = (String) l.get(j);
		if ( !seen.contains(name) ) {
		    /* As with the class, this is a little tricky. The
		     * reasoner may return individuals that aren't
		     * *in* the ontology it's working with, but are
		     * defined elsewhere.*/
		    OWLIndividual i = 
			ontology.getOWLDataFactory().getOWLIndividual( new URI( name ) );
		    //OWLIndividual i = ontology.getIndividual( new URI( name ) );
		    result.add( i );
		    seen.add( name );
		}
	    }
	} catch ( ExpressivenessOutOfScopeException ex ) {
	    throw new OWLException( ex.getMessage() );
	} catch ( Exception ex ) {
	    //System.out.println( ex.getMessage() );
	    throw new OWLException( ex.getMessage() );
	}
	return result;

    }




    private void notImplemented(String message) throws OWLException {
	throw new OWLException( message + ": Not yet implemented" );
    }

    public class ExpressivenessOutOfScopeException extends OWLException {
	ExpressivenessOutOfScopeException( String m ) {
	    super( m );
	}
    }



    public static void main( String[] args ) {
	try {
	    org.apache.log4j.BasicConfigurator.configure();

	    java.net.URI uri = new java.net.URI( args[1] );
	    
	    /* Get hold of a reasoner */

	    Reasoner digReasoner = 
		new uk.ac.man.cs.img.dig.reasoner.impl.HTTPReasoner(args[0]);
	    
	    SimpleOWLReasoner reasoner = new SimpleOWLReasoner( digReasoner );

 	    OWLOntology onto = OntologyHelper.getOntology( uri );

	    reasoner.setOntology( onto );

	    try { 
		if ( !reasoner.isConsistent() ) {
		    System.out.println( "!!! Inconsistent Ontology !!!"  );
		}
	    } catch ( Exception ex ) {
		//System.out.println( ex.getMessage() );
		ex.printStackTrace();
	    }
	    for (Iterator it = onto.getClasses().iterator(); 
		 it.hasNext();) {
		OWLClass clazz = (OWLClass) it.next();

		if ( !reasoner.isConsistent( clazz ) ) {
		    System.out.println( "Unsatisfiable Class: " + clazz.getURI() );
		    //		System.out.println( (reasoner.isConsistent( clazz )?"GOOD: ":"BAD:  ") + clazz.getURI() ) ;
		}
		System.out.println( clazz.getURI() );
		Set subs = reasoner.subClassesOf( clazz );
		for (Iterator sit = subs.iterator(); sit.hasNext(); ) {
		    for (Iterator innerIt = ((Set) sit.next()).iterator();
			 innerIt.hasNext(); ) { 
			OWLClass cl = (OWLClass) innerIt.next();
			System.out.println("> " + cl.getURI() );
			System.out.println( reasoner.isSubClassOf( cl, clazz ));
		    }			
		}
		Set supers = reasoner.superClassesOf( clazz );
		for (Iterator sit = supers.iterator(); sit.hasNext(); ) {
		    for (Iterator innerIt = ((Set) sit.next()).iterator();
			 innerIt.hasNext(); ) { 
			OWLClass cl = (OWLClass) innerIt.next();
			System.out.println("< " + cl.getURI() );
			System.out.println( reasoner.isSubClassOf( clazz, cl ));
		    }			
		}
		Set equivs = reasoner.equivalentClassesOf( clazz );
		for (Iterator eit = equivs.iterator(); eit.hasNext(); ) {
		    for (Iterator innerIt = ((Set) eit.next()).iterator();
			 innerIt.hasNext(); ) { 
			OWLClass cl = (OWLClass) innerIt.next();
			System.out.println("= " + cl.getURI() );
			System.out.println( reasoner.isEquivalentClass( clazz, cl ));
		    }			
		}
	    }
	} catch ( ExpressivenessOutOfScopeException ex ) {
	    System.out.println( ex.getMessage() );
	} catch ( Exception ex ) {
	    //System.out.println( ex.getMessage() );
	    ex.printStackTrace();
	}
    }

    
} // SimpleOWLReasoner



/*
 * ChangeLog
 * $Log: SimpleOWLReasoner.java,v $
 * Revision 1.5  2004/10/19 17:18:32  sean_bechhofer
 * Changes to handle classes from the import closure.
 *
 * Revision 1.4  2004/03/05 17:34:48  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.3  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.2  2003/11/20 12:58:12  sean_bechhofer
 * Addition of language handling in OWLDataValues.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.14  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.13  2003/10/03 10:00:04  bechhofers
 * Refactoring of RDFErrorHandler:
 *  o Addition of error codes for OWL Full situations
 *
 * Revision 1.12  2003/10/01 16:51:09  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.11  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.10  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.9  2003/09/02 14:12:25  bechhofers
 * Fixing parse problem re annotations on annotation properties
 *
 * Revision 1.8  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.7  2003/07/09 13:58:30  bechhofers
 * docs update
 *
 * Revision 1.6  2003/07/08 18:01:07  bechhofers
 * Presentation Servlet and additional documentation regarding inference.
 *
 * Revision 1.5  2003/07/08 13:23:50  bechhofers
 * Further work on reasoning -- addition of taxonomy reasoners for classes.
 *
 * Revision 1.4  2003/06/25 16:04:57  bechhofers
 * Added removal events
 *
 * Revision 1.3  2003/06/19 13:33:32  seanb
 * Addition of construct checking.
 *
 * Revision 1.2  2003/06/16 12:50:41  seanb
 * Small change to reasoning
 *
 * Revision 1.1  2003/06/03 17:01:53  seanb
 * Additional inference
 *
 * Revision 1.3  2003/05/29 09:07:28  seanb
 * Moving RDF error handler
 *
 * Revision 1.2  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.1  2003/05/19 11:51:40  seanb
 * Implementation of reasoners
 *
 */
