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

package org.semanticweb.owl.io.owl_rdf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.model.change.*;
import org.semanticweb.owl.util.OWLConnection;

import org.xml.sax.SAXException;

import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;

import edu.unika.aifb.rdf.api.syntax.RDFConsumer;
import edu.unika.aifb.rdf.api.util.RDFConstants;
import java.util.List;
import java.util.ArrayList;
import org.semanticweb.owl.io.ParserException;
import org.semanticweb.owl.model.change.AddSuperClass;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.change.AddSuperProperty;
import org.semanticweb.owl.io.vocabulary.RDFSVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFVocabularyAdapter;
import java.math.BigInteger;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * <p>This is <strong>yet another</strong> refactoring and
 * reimplementation of the parser. This time, we're collecting all the
 * triples up after parsing and stuffing them into a big data
 * structure. It is likely that this is getting less and less
 * efficient, and at some point we would be better moving to using a
 * parser such as Jena that actually provides some optimised data
 * structures for these kinds of things. However, in the short term it
 * may be fruitful to have multiple implementations of parser.</p>

 * <p>This parser that doesn't even attempt to do any
 * streaming. Instead it collects the triples as required and then
 * processes them later.</p>

 * <p> This does introduce memory overhead, but makes the whole
 * process of parsing much easier as we no longer have to make guesses
 * or assumptions about the format of things. </p>

 * <p>Where it can, the parser will try and fix broken bits of
 * syntax. For example, if we have untyped individuals, these will
 * still be added to the ontology. However, the error handler will be
 * informed of the problem. Thus if the parsing client wishes to
 * strictly enforce, e.g. membership of OWL Lite, it can do so, but if
 * it's happy to have the parser try and fix the problem it can simply
 * ignore (or report) the problem.</p>

 * <p>
 * In some situations, there are multiple ways in which the
 * triples can be mapped into the data structures. The basic
 * choice boils down to whether assertions about subclasses and
 * equivalences are represented as either axioms or "class
 * definitions". Of course, in terms of formal semantics, there is
 * no difference, but it can be useful to preserve the users
 * intention (see <a
 * href="http://potato.cs.man.ac.uk/seanb/publications.php#not-enough">DAML+OIL
 * is Not Enough</a>).
 * <p>
 * In order to do this, the parser basically tries to identify
 * situations where the triples have been produced via a mapping
 * from the abstract syntax, as described in the OWL Abstract
 * Syntax and Semantics document. As an example, a situation like:
 * <pre>
 * &lt;class id="a"&gt;
 *   &lt;intersectionOf rdf:parseType="Collection"&gt;
 *     &lt;class id="b"/&gt;
 *     &lt;class id="c"/&gt;
 *   &lt;/intersectionOf&gt;
 * &lt;/class&gt;
 * </pre>
 * can <strong>only</strong> arise from a statement of the form:
 * <pre>
 * Class(a complete intersectionOf(b c))
 * </pre>
 *
 * in the abstract syntax. Thus this is dealt with by adding
 * information to the class rather than as an equivalence axiom. Note
 * that this is the <b>only</b> place where intersectionOf can appear
 * in an OWL lite ontology. The only situation where there is
 * ambiguity is in:
 *
 * <pre>
 * &lt;class id="a"&gt;
 *  &lt;subClassOf&gt;
 *   &lt;intersectionOf rdf:parseType="Collection"&gt;
 *     &lt;class id="b"/&gt;
 *     &lt;class id="c"/&gt;
 *   &lt;/intersectionOf&gt;
 *  &lt;/subClassOf&gt;
 * &lt;/class&gt;
 * </pre>
 * In this case, these triples may arise from:
 * <pre>
 * Class(a partial intersectionOf(b c))
 * </pre>
 * or
 * <pre>
 * SubClass(a intersectionOf(b c))
 * </pre>
 * The parser will choose the former wherever it can, on the basis
 * that information relating to the class is then kept in one
 * place.
 * <p>
 * <b>Limitations and known issues/bugs</b>
 * <p>
 * <ul>
 * 
 * <li>Anonymity of URIs is currently dealt with via an ad hoc
 * mechanism (relating to the RDF parser). Nodes are considered to be
 * anonymous if their fragment is a name of the form genidNNN. This is
 * obviously unsatisfactory, and should be replaced by some
 * communication/callback with the parser.</li>
 * 
 * <li>Doesn't really handle data types particularly well.</li>
 * 
 * <li>Annotation properties not added to ontologies yet.</li>
 * 
 * <li>Doesn't handle ontology metadata.</li>
 * 
 * <li>Imports. Due to the inexpressiveness of RDF, it's impossible to
 * ascertain what the URI for "this" ontology is. We can use
 * hueristics (guessing that if there's an ontology node that isn't
 * the object of a triple it's probably that), but these are not
 * guaranteed to work. This means that we can't guarantee to handle
 * imports properly because we can't determine whether not the import
 * applies to "this" ontology. The strategy employed is to simply take
 * any imports triple that we find and import it. This is clearly
 * wrong &mdash; if an ontology tries to say things about other
 * ontologies importing each other this will break, but I see no
 * obvious solution.  </li>
 * 
 * </ul>

 * <p><strong>Note</strong> also that this parser is really targeted
 * at parsing OWL-DL (and Lite) ontologies. If given an OWL-RDF which
 * is actually Full, some of the information in the triples may be
 * ignored or interpreted in a particular way. If this does occur,
 * however, warnings or errors will be communicated to the error
 * handler of the parser.</p>
 * 
 * @since 05.02.2003
 * @author <a href="mailto:volz@fzi.de">Raphael Volz</a>
 * @author Sean Bechhofer
 * 
 */
public class OWLConsumer implements RDFConsumer, OWLRDFErrorConstants {

    /* Open Issues: 

    13/10/03 Ontology typing. A bug in the RDF parser means that
       rdf:about="" references are not being resolved properly. This
       leads to problems with ontology typing. As a result, the
       consumer does *not* currently check to see whether imported
       ontologies are typed properly.

    */

    /*********************************************************************
     * The implementation of this class has changed quite a bit since
     * Raphael's first cut. In particular, the original used maps to
     * cache information like subclass and subproperty. Unfortunately,
     * this causes problems when we have multiple subclasses, e.g.
     *
     * a subClass b
     * a subClass c
     *
     * to cope with this, the caches now use Sets where appropriate,
     * with the sets consisting of pairs of URIs (implemented using
     * arrays). 
     * 
     * In addition, processing has now been delayed until the end. So
     * we are not attempting to stream at all.
     * 
     * A map translatedDescriptions stores the results of translating
     * URIs (possible anonymous) to descriptions. This map is
     * built lazily during the processing stage. 
     * 
     */


    /* TODO!!! Adding annotation properties to the ontology */


    /* Needed for callbacks. */
    OWLRDFParser myParser = null;

    public void setOWLRDFParser(OWLRDFParser p) {
        this.myParser = p;
    }

    /*************************/

    /* Some useful constants */


    final static int UNSET = -1;

    final static int ONEOF = 0;
    final static int UNION = 1;
    final static int INTERSECTION = 2;
    final static int COMPLEMENT = 3;
    final static int RESTRICTION = 4;

    final static int ALL = 10;
    final static int SOME = 11;
    final static int HASVALUE = 12;
    final static int DATAHASVALUE = 13;
    final static int MIN = 14;
    final static int MAX = 15;
    final static int CARD = 16;

    final static int CLASS = 17;
    final static int LIST = 18;

    /* constant that represents the biggest cardinality we're allowed. */

    final static BigInteger MAXCARDASBIGINTEGER = 
	new BigInteger(new Integer(Integer.MAX_VALUE).toString());

    OWLConnection connection;

    OWLOntology onto = null;
    OWLDataFactory factory = null;
    /* Basic logger */
    static Logger logger = Logger.getLogger(OWLConsumer.class);

    /* Logger that will just report triples */
    static Logger tripleLogger = Logger.getLogger(OWLConsumer.class.getName() + "Triples");

    /* Logger that will just report triples */
    static Logger parseMessageLogger = Logger.getLogger(OWLConsumer.class.getName() + "Parsing");

    /* Logger that will report progress */
    static Logger progressLogger = Logger.getLogger(OWLConsumer.class.getName() + "Progress");

    protected int tripleCount = 0;
    protected static int tripleCountChunk = 5000;

    /* Used for caching URIs. We keep a cache of strings -> URIs which
     * prevents us from recreating a new URI object each time we see a
     * new URI. We _may_ need to be careful about this as the resulting
     * objects may end up sharing things though.... */
    protected Map stringsToURIs; 


    /* Named stuff in the ontology */
    
    HashSet definitelyOntologies = new HashSet();
    HashSet owlClasses = new HashSet();
    HashSet owlIndividuals = new HashSet();

    HashSet owlOntologyProperties = new HashSet();
    
    /* Used for recovering from possible errors */

    HashSet rdfsClasses = new HashSet();
    HashSet rdfProperties = new HashSet();
    HashMap rdfTypes = new HashMap();
    
    /* Restrictions */

    HashSet owlRestrictions = new HashSet();
    HashMap onProperty = new HashMap();
    HashMap allValues = new HashMap();
    HashMap someValues = new HashMap();
    HashMap hasValue = new HashMap();
    HashMap dataHasValue = new HashMap();
    HashMap minCardinality = new HashMap();
    HashMap maxCardinality = new HashMap();
    HashMap cardinality = new HashMap();
    
    /* Lists */
    HashSet lists = new HashSet();
    HashMap listFirst = new HashMap();
    HashMap listRest = new HashMap();

    /* Other stuff */
    HashSet allDifferent = new HashSet();
    HashMap distinctMembers = new HashMap();
    HashMap subClass = new HashMap();
    HashMap equivalentClass = new HashMap();
    HashMap subProperty = new HashMap();
    HashMap equivalentProperty = new HashMap();
    HashMap disjointWith = new HashMap();
    HashMap sameAs = new HashMap();
    HashMap differentFrom = new HashMap();
    HashMap owlInverseOf = new HashMap();

    HashSet arbitraryTriples = new HashSet();
    HashSet arbitraryDataTriples = new HashSet();

    HashMap translatedDescriptions = new HashMap();
    HashMap createdIndividuals = new HashMap();
    HashSet pendingTranslations = new HashSet();

    /* Collections relating to assumptions */
    HashSet definitelyClasses = new HashSet();
    HashSet definitelyObjectProperties = new HashSet();
    HashSet definitelyAnnotationProperties = new HashSet();
    HashSet definitelyDataProperties = new HashSet();
    HashSet definitelyDatatypes = new HashSet();

    HashSet builtInClasses = new HashSet();

    HashSet assumedClasses = new HashSet();
    HashSet assumedDataProperties = new HashSet();
    HashSet assumedObjectProperties = new HashSet();

    HashSet untypedIndividuals = new HashSet();
    HashSet definitelyTypedIndividuals = new HashSet();

    HashSet assumedOntologies = new HashSet();
    
    /* If set, check that everything is defined when you've
     * finished. May be unset if the consumer is being used for an
     * import parse. After finishing an import parse, there may be
     * entities that are undefined because the definitions are in the
     * enclosing ontologies (which is fine). SKB 18/02/03*/
    boolean checkDefinitions = true;

    /* If set, try and handle imports. */
    boolean importing = true;

    URI xsdString;
    URI rdfLiteral;
    URI xmlLiteral;

    /* Check against nodeID happens later on. This makes sure we've
     * got something to check against rather than accessing the
     * vocabulary each time. Should probably go in the vocabulary
     * really. */
    String rdfNodeID;


    /** If set to true, this parser will simply ignore annotation
     * content when it's data (rather than individuals) -- the
     * annotations do not get added. Basic device to try and improve
     * performance w.r.t validation of large things. Validation
     * doesn't require us to worry about the content of annotations
     * (or even their existence). */

    boolean ignoreAnnotationContent = false;

    /** Control whether the consumer should include data
     * annotatations. If set to false, data annotations are not added
     * to the model. This can dramatically improve the performance of,
     * for example, validation, as it means that large data sttuctures
     * are not being built. The validator doesn't need to know
     * anything about the annotation content to do its work, as long
     * as it is able to work out whether things are being used as
     * annotation proeprties or not.  */
    public void setIgnoreAnnotationContent( boolean b ) {
	if ( b ) {
	    try {
		warning( "Ignoring Annotation Content...");
	    } catch (Exception ex) {
	    }
	}
	ignoreAnnotationContent = b;
    }

    /** If set to true, the parser will ignore import statements that
    * refer to the OWL, RDF and RDF(S) vocabularies. A common mistake
    * is to include these, which of course always results in OWL
    * Full. */

    boolean ignoreSchemaImports = false;

    /** Control whether the consumer should ignore inclusion of the
     * OWL, RDF and RDF(S) schemas. */
    public void setIgnoreSchemaImports( boolean b ) {
	if ( b ) {
	    try {
		warning( "Ignoring Schema Imports...");
	    } catch (Exception ex) {
	    }
	}
	ignoreSchemaImports = b;
    }

    /** If set to true, the parser will try and apply heuristics that
     * fix bad use of sameAs. Be <strong>very</strong>
     * careful about setting this flag -- the parser will then be
     * applying changes to the ontology that may <strong>not</strong>
     * be what you intend. */

    boolean fixSameAs = false;
    /** Control whether the consumer should try and fix the use of
     * sameAs for classes or properties. */
    public void setFixSameAs( boolean b ) {
	if ( b ) {
	    try {
		warning( "Fixing SameAs...");
	    } catch (Exception ex) {
	    }
	}
	fixSameAs = b;
    }

//     /** If set to true, the parser will assume that owl:Thing and
//      * owl:Nothing are definitely owlClasses. Be <strong>very</strong>
//      * careful about setting this flag -- the parser will then be
//      * applying changes to the ontology that may <strong>not</strong>
//      * be what you intend. */
    
//     boolean laxThingHandling = false;
//     /** Control whether the consumer should try and fix the use of
//      * sameAs for classes or properties. */
//     public void setLaxThingHandling( boolean b ) {
// 	if ( b ) {
// 	    try {
// 		warning( "Lax Thing Handling...");
// 	    } catch (Exception ex) {
// 	    }
// 	}
// 	laxThingHandling = b;
//     }

    /* Known datatypes from XML Schema */
    Set xsdDatatypes;
    
    HashSet parsedURIs = new HashSet();

    /* Any bnodes encountered during the parse */
    HashSet bNodes = new HashSet();

    /* A set that represents all the bnodes that have been used. This
     * is used to spot structure sharing. */

    HashSet usedBNodes = new HashSet();

    /* The logical URI of the ontology. This might not be set if
     * there's no Ontology triple... */

    URI ontologyLogicalURI;

    
    /* Basic error handling routines. These may get passed off to an error handler in the future */ 

    protected OWLRDFErrorHandler handler;

    protected void owlFullConstruct( int code, 
				   String message ) throws SAXException {
	if (handler!=null) {
	    handler.owlFullConstruct( code, message );
	}
    }

    protected void owlFullConstruct( int code, 
				     String message,
				     Object obj ) throws SAXException {
	if (handler!=null) {
	    handler.owlFullConstruct( code, message, obj );
	}
    }

    protected void error( String message ) throws SAXException {
	if (handler!=null) {
	    handler.error( message );
	}
    }

    protected void warning( String message ) throws SAXException {
	if (handler!=null) {
	    handler.warning( message );
	}
    }

    public void setOWLRDFErrorHandler( OWLRDFErrorHandler h ) {
	this.handler = h;
    }


    /** Takes a URI that should be the URI of an RDF listm and returns
    * a collection of those things that form the elements of the
    * list. These will either be URIs or pairs of Strings in the case
    * of literal values. */
    protected void rdfListToSet(URI start, Set mySet) throws SAXException {
	/* If there's a triple asserting that this is a
	 * list, mark it as used.  */
	Triple listTriple = getTriple( start.toString(), v.getInstanceOf(), RDFConstants.RDF_LIST );
	
	if ( listTriple!=null ) {
	    usedTriples.add( listTriple );
	}
	
    	/* Changed to check for nil and trap empty lists.... */
	if (logger.isDebugEnabled()) {
	    logger.debug(">in");
	}
    	if ( start.toString().equals( v.getNil() ) ) {
    	    //logger.debug("Nil reached...");
    	} else {
	    Set rests = getObjects( start.toString(), RDFConstants.RDF_REST );
	    if (rests.size() != 1) {
		error( "Malformed List (multiple rests): " + start );
	    }
	    try {
	    Triple nextTriple = (Triple) getSingletonObject( rests );
	    usedTriples.add( nextTriple );
	    URI next = nextTriple.object;
	    
	    Set firsts = getObjects( start.toString(), RDFConstants.RDF_FIRST );
	    if ( firsts.size() == 1 ) {
		/* We've got a URI first. */
		// try {
		Triple firstTriple = (Triple) getSingletonObject( firsts );
		usedTriples.add( firstTriple );
		if (logger.isDebugEnabled()) {
		    logger.debug("Adding " + firstTriple.object);
		}
		mySet.add( firstTriple.object );
		if (next != null ) {
		    rdfListToSet(next, mySet);
		} else {
		    owlFullConstruct( MALFORMED_LIST,
				      "Malformed List! " + start );
		}
	    
		
		} else {
		/* It may be that we've got some data in the list. */
		firsts = getLiterals( start.toString(), RDFConstants.RDF_FIRST );
		if ( firsts.size() == 1 ) {
		    /* We've got some data. */
		    LiteralTriple firstLiteralTriple = (LiteralTriple) getSingletonObject( firsts );
		    usedTriples.add( firstLiteralTriple );
		    
		    if (logger.isDebugEnabled()) {
			logger.debug("Adding " + firstLiteralTriple.object);
		    }
		    String[] obs = new String[3];
		    obs[0] = firstLiteralTriple.object;
		    obs[1] = firstLiteralTriple.language;
		    obs[2] = firstLiteralTriple.dataType;
		    mySet.add( obs );
		    if (next != null ) {
			rdfListToSet(next, mySet);
		    } else {
			owlFullConstruct( MALFORMED_LIST,
					  "Malformed List! " + start );
		    }
		} else {
		    owlFullConstruct( MALFORMED_LIST,
				      "Malformed List! " + start );
		}

	    }
} catch (NoSuchElementException e) {
			// A first, but no element ;-(
			owlFullConstruct( MALFORMED_LIST,
				      "Malformed List! " + start );
		
		}
    	}
	if (logger.isDebugEnabled()) {
	    logger.debug("<out");
	}
    }

    /** Takes a URI representing a list of descriptions and returns a
    collection of descriptions obtained by translating those
    descriptions. */
    protected Set uriToClassSet(Set uriSet) throws SAXException {
	HashSet result = new HashSet();

	if (logger.isDebugEnabled()) {
	    logger.debug( "---" );
	    for (Iterator iter = uriSet.iterator(); iter.hasNext();) {
		logger.debug( iter.next() );
	    }
	    logger.debug( "---" );
	}

	for (Iterator iter = uriSet.iterator(); iter.hasNext();) {
	    URI element = (URI) iter.next();
	    if (logger.isDebugEnabled()) {		
		logger.debug( "XX: " + element );
	    }
	    OWLDescription d;
    	    d = translateDescription( element );
	    checkAndConsumeBNode( element );
    	    result.add(d);
	}
	return result;
    }

    public Set uriToIndividualSet(Set uriSet) throws SAXException {
	HashSet result = new HashSet();
	for (Iterator iter = uriSet.iterator(); iter.hasNext();) {
	    URI element = (URI) iter.next();
	    OWLIndividual i = translateIndividual(element);
    	    result.add(i);
	}
	return result;
    }

    OWLVocabularyAdapter v = OWLVocabularyAdapter.INSTANCE;
    RDFSVocabularyAdapter rdfsV = RDFSVocabularyAdapter.INSTANCE;
    RDFVocabularyAdapter rdfV = RDFVocabularyAdapter.INSTANCE;

    // Declared Things
    HashSet createdClasses = new HashSet();
    HashSet createdObjectProperties = new HashSet();
    HashSet createdDatatypeProperties = new HashSet();
    HashSet createdRestrictions = new HashSet();
    HashSet createdOntologies = new HashSet();
    HashSet createdAllDifferent = new HashSet();

    HashSet deprecatedProperties = new HashSet();

    HashSet badTriples = new HashSet();
    // TO DO at the moment only warnings are written out.

    /** Create a new consumer that will parse into the given ontology */
    public OWLConsumer(OWLOntology onto) throws OWLException {

	stringsToURIs = new HashMap();
	//	super( onto );
	this.onto = onto;
	factory = onto.getOWLDataFactory();
	/* Default Error Handler */
	setOWLRDFErrorHandler(new OWLRDFErrorHandler(){
		public void owlFullConstruct( int code,
					      String message,
					      Object obj ) throws SAXException {
		    parseMessageLogger.warn( message );
		}
		public void owlFullConstruct( int code,
					      String message ) throws SAXException {
		    parseMessageLogger.warn( message );
		}
		public void error( String message ) throws SAXException {
		    parseMessageLogger.error( message );
		    //throw new SAXException( message.toString() );
		}
		public void warning( String message ) throws SAXException {
		    parseMessageLogger.warn( message );
		}
	    });
        // prepareTypeMap();
	try {
	    URI t = newURI(OWLVocabularyAdapter.INSTANCE.getThing() );
	    definitelyClasses.add( t );
	    builtInClasses.add( t );
	    URI nt = newURI(OWLVocabularyAdapter.INSTANCE.getNothing() );
	    definitelyClasses.add( nt );
	    builtInClasses.add( nt );
	} catch (URISyntaxException ex) {
	    /* This is quite a serious error. */
	    System.err.println("Error in creating OWL vocabulary!" + ex.getMessage());
	}
	/* Make sure that the annotation properties are known about */
	try {

	    for (Iterator it = OWLVocabularyAdapter.INSTANCE.getAnnotationProperties().iterator();
		 it.hasNext(); ) {
		String p = (String) it.next();
		URI ap = newURI( p );
		if (!definitelyAnnotationProperties.contains( ap ) ) {
		    definitelyAnnotationProperties.add( ap );
		}
	    }

	    for (Iterator it = OWLVocabularyAdapter.INSTANCE.getOntologyProperties().iterator();
		 it.hasNext(); ) {
		String p = (String) it.next();
		
		owlOntologyProperties.add( newURI(p) );
	    }

	    URI u = newURI (OWLVocabularyAdapter.INSTANCE.getThing());
	    translatedDescriptions.put( u, factory.getOWLThing() );

	    u = newURI (OWLVocabularyAdapter.INSTANCE.getNothing());
	    translatedDescriptions.put( u, factory.getOWLNothing() );
	    
	    /* Useful for later stuff */
	    xsdString = newURI("http://www.w3.org/2001/XMLSchema#string");
	    definitelyDatatypes.add( xsdString );

	    /* I'm confused about this. Should it be Literal or
	     * XMLLiteral???? */

	    //	    rdfLiteral =
	    
	    rdfLiteral = newURI( RDFSVocabularyAdapter.INSTANCE.getLiteral() );
	    definitelyDatatypes.add( rdfLiteral );

	    /* Oh dear. All getting a bit confused. */
	    xmlLiteral = 
		newURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");
	    definitelyDatatypes.add( xmlLiteral );
	    
	    xsdDatatypes = XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getDatatypes();
	    /* This gets used in a number of places. */
	    rdfNodeID = rdfV.RDF+ "nodeID";

 	} catch (URISyntaxException ex) {
 	    /* This is quite a serious error. */
 	    System.err.println("Error in creating Vocabulary!" + ex.getMessage());
 	} catch (OWLException ex) {
 	    System.err.println("Error in creating Thing/Nothing!" + ex.getMessage());
	}
    }

    /** Set the assumptions that this consumer should be making about
       the objects that its parsing. This allows us to deal with
       things spread across a number of ontologies. */ 

    protected void setAssumptions( HashSet[] assumptions ) {
	this.assumedClasses = assumptions[0];
	this.assumedDataProperties = assumptions[1];
	this.assumedObjectProperties = assumptions[2];
	this.definitelyClasses = assumptions[3];
	this.definitelyObjectProperties = assumptions[4];
	this.definitelyDataProperties = assumptions[5];
	this.untypedIndividuals = assumptions[6];
	this.definitelyTypedIndividuals = assumptions[7];
	this.parsedURIs = assumptions[8];
	this.rdfsClasses = assumptions[9];
	this.rdfProperties = assumptions[10];
	this.definitelyAnnotationProperties = assumptions[11];
	this.definitelyDatatypes = assumptions[12];
	this.definitelyOntologies = assumptions[13];
	this.assumedOntologies = assumptions[14];
    }

    protected void setCheckDefinitions( boolean b ) {
	this.checkDefinitions = b;
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#startModel(String)
     */
    public void startModel(String arg0) throws SAXException {
        // onto is created already, arg0 gives only the physicalURI of the onto 
        // [REVIEW] Use Connection instead to create onto object and get it from here.
        logger.info("Start model " + arg0);
	parsedURIs.add( arg0 );
    }

    /** 
     * Returns a property with the given uri if it is created in the ontology
     * null otherwise 
     */
    protected OWLProperty getProperty(URI uri) throws OWLException {
        OWLProperty ret = null;
        ret = onto.getObjectProperty(uri);
        if (ret == null)
            ret = onto.getDataProperty(uri);
        return ret;
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#endModel()
     */
    public void endModel() throws SAXException {
 	/* Dump all the triples */
	//	logger.debug( dumpTriples() );

	/* Used to record all the triples that we've made use of. */
// System.err.println(dumpTriples());
	usedTriples = new HashSet();

	Set workingTriples = null;

	/* Find any ontologies. */
	workingTriples = getSubjects( v.getInstanceOf(), v.getOntology() );
	progressLogger.info( "Ontologies: " + workingTriples.size() );
	
	boolean foundLogicalURI = false;
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    if (logger.isDebugEnabled()) {
		logger.debug("OWLOntology: " + triple.subject);
	    }
	    URI uri = triple.subject;
	    definitelyOntologies.add( uri );
	    if ( assumedOntologies.contains( uri ) ) {
		assumedOntologies.remove( uri );
	    }
	    
	    /* There should be a single node with type owl:Ontology
	     * that is not the object of any other triples. This is
	     * "this" ontology. We simply look for the first one that
	     * we find. If there are more, unpredictable results will
	     * ensue..*/

	    /* This was too strict :-(. There could be all sorts of
	     * crap in here that points to the ontology -- definedBy
	     * etc. As far as I can tell, there is simply *no* way
	     * that we can guarantee what the URI of the ontology
	     * should be.  */

	    /* The whole notion of the owl:Ontology is, IMO,
	     * completely broken. There just isn't any way of
	     * resolving this stuff, so I'm not going to bother. */

	    boolean theOne = !isObject( uri );
	    
	    // 	    /* If there's only one, pick it */
	    theOne = theOne || (workingTriples.size() == 1);
	    
	    if ( theOne ) {
		if (!foundLogicalURI) {
		    if (logger.isDebugEnabled()) {			
			logger.debug("Logical URI: " + uri);
		    }
		    ontologyLogicalURI = uri;
		    foundLogicalURI = true;
		    /* What happened to this code? It appears to have got lost
		     * somewhere between 1.8 and 1.9?? */
		    try {
			SetLogicalURI slu = 
			    new SetLogicalURI(onto, uri, null);
			applyChange( slu );
		    } catch ( OWLException ex ) {
			error( ex.getMessage() );
		    }
		} else {
		    /* We've already found a logical URI for the
		     * ontology, but there appears to be another
		     * candidate....*/
		    warning( "Cannot determine what the logical URI of this ontology should be.\nMultiple owl:Ontology nodes are present.\nChoosing: " + ontologyLogicalURI + " as the logical URI." );
		}
	    }
	}

	if ( ontologyLogicalURI==null ) {
	    try {
		warning( "No Logical URI can be determined. Using physical URI: " + onto.getPhysicalURI());
		ontologyLogicalURI = onto.getPhysicalURI();
	    } catch ( OWLException ex ) {
		error( ex.getMessage() );
	    }
	}


	/* The first thing we do is go through all the classes and add
	 * each one to the ontology. */

	workingTriples = getSubjects( v.getInstanceOf(), v.getClass_() );
	progressLogger.info( "Classes: " + workingTriples.size() );
	
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );
	    
	    if (logger.isDebugEnabled()) {
		logger.debug("OWLClass: " + triple.subject);
	    }
	    URI uri = triple.subject;
	    definitelyClasses.add( uri );

	    /* For the non-anonymous ones, add a class */
	    if (!isAnonymousNode( uri.toString() ) ) {
		OWLClass entity = createClass( onto, uri );
		/* We know that it's definitely a class now. */
		//		definitelyClasses.add( uri );
		assumedClasses.remove( uri );
		
		if ( tripleExists( uri.toString(), v.getInstanceOf(), v.getDeprecatedClass() ) ) {
		    if (logger.isDebugEnabled()) {
			logger.debug("Deprecated Class: " + uri );
		    }
		    try {
			SetDeprecated se = new SetDeprecated(onto, entity, true, null);
			applyChange( se );
		    } catch ( OWLException ex ) {
			error( ex.getMessage() );
		    }
		}
		/* Record the fact that this URI maps to the given
		 * class */
		translatedDescriptions.put( uri, entity );
	    }
	}

	workingTriples = getSubjects( v.getInstanceOf(), v.getObjectProperty() );
	progressLogger.info( "ObjectProperties: " + workingTriples.size() );
	/* Now properties */
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;

	    OWLObjectProperty entity = createObjectProperty( onto, uri );
	    definitelyObjectProperties.add( uri );
	    assumedObjectProperties.remove( uri );
	    Triple objectCharacteristic = getTriple( uri.toString(), v.getInstanceOf(), v.getDeprecatedProperty() );
	    if ( objectCharacteristic!=null ) {
		usedTriples.add( objectCharacteristic );
		try {
		    OntologyChange oc = 
			new SetDeprecated(onto, entity, true, null);
		    applyChange( oc );
		} catch ( OWLException ex ) {
		    error( ex.getMessage() );
		}
	    }
	    objectCharacteristic = getTriple( uri.toString(), v.getInstanceOf(), v.getFunctionalProperty() );
	    if ( objectCharacteristic!=null ) {
		usedTriples.add( objectCharacteristic );
		try {
		    OntologyChange oc = 
			new SetFunctional(onto, entity, true, null);
		    applyChange( oc );
		} catch ( OWLException ex ) {
		    error( ex.getMessage() );
		}
	    }
	    objectCharacteristic = getTriple( uri.toString(), v.getInstanceOf(), v.getInverseFunctionalProperty() );
	    if ( objectCharacteristic!=null ) {
		usedTriples.add( objectCharacteristic );
		try {
		    OntologyChange oc = 
			new SetInverseFunctional(onto, entity, true, null);
		    applyChange( oc );
		} catch ( OWLException ex ) {
		    error( ex.getMessage() );
		}
	    }
	    objectCharacteristic = getTriple( uri.toString(), v.getInstanceOf(), v.getTransitive() );
	    if ( objectCharacteristic!=null ) {
		usedTriples.add( objectCharacteristic );
		try {
		    OntologyChange oc = 
			new SetTransitive(onto, entity, true, null);
		    applyChange( oc );
		} catch ( OWLException ex ) {
		    error( ex.getMessage() );
		}
	    }
	    objectCharacteristic = getTriple( uri.toString(), v.getInstanceOf(), v.getSymmetricProperty() );
	    if ( objectCharacteristic!=null ) {
		usedTriples.add( objectCharacteristic );
		try {
		    OntologyChange oc = 
			new SetSymmetric(onto, entity, true, null);
		    applyChange( oc );
		} catch ( OWLException ex ) {
		    error( ex.getMessage() );
		}
	    }
	}

	workingTriples = getSubjects( v.getInstanceOf(), v.getDatatypeProperty() );
	progressLogger.info( "DataProperties: " + workingTriples.size() );
	/* Now properties */
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;

	    OWLDataProperty entity = createDataProperty( onto, uri );
	    definitelyDataProperties.add( uri );
	    assumedDataProperties.remove( uri );

	    Triple objectCharacteristic = getTriple( uri.toString(), v.getInstanceOf(), v.getDeprecatedProperty() );
	    if ( objectCharacteristic!=null ) {
		usedTriples.add( objectCharacteristic );
		try {
		    OntologyChange oc = 
			new SetDeprecated(onto, entity, true, null);
		    applyChange( oc );
		} catch ( OWLException ex ) {
		    error( ex.getMessage() );
		}
	    }
	    objectCharacteristic = getTriple( uri.toString(), v.getInstanceOf(), v.getFunctionalProperty() );
	    if ( objectCharacteristic!=null ) {
		usedTriples.add( objectCharacteristic );
		try {
		    OntologyChange oc = 
			new SetFunctional(onto, entity, true, null);
		    applyChange( oc );
		} catch ( OWLException ex ) {
		    error( ex.getMessage() );
		}
	    }
	    objectCharacteristic = getTriple( uri.toString(), v.getInstanceOf(), v.getInverseFunctionalProperty() );
	    if ( objectCharacteristic!=null ) {
		usedTriples.add( objectCharacteristic );
		owlFullConstruct( INVERSE_FUNCTIONAL_DATA_PROPERTY,
				  "Data Properties cannot be inverse functional: " + uri,
				  uri );
	    }

	}

	/* Check that all functional properties have been dealt
	 * with */
	workingTriples = getSubjects( v.getInstanceOf(), v.getFunctionalProperty() );
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;

	    try {
		OWLObjectProperty op = onto.getObjectProperty( uri );
		if ( op == null ) {
		    OWLDataProperty dp = onto.getDataProperty( uri );
		    if ( dp == null ) {
			/* We've got one that isn't properly specified! */
			owlFullConstruct( UNSPECIFIED_FUNCTIONAL_PROPERTY,
					  "Functional Property " + uri + " unspecified",
					  uri );
			/* Try and recover -- assume it's an object property */
			OWLObjectProperty entity = createObjectProperty( onto, uri );
			definitelyObjectProperties.add( uri );
			assumedObjectProperties.remove( uri );
			OntologyChange oc = 
			    new SetFunctional(onto, entity, true, null);
			applyChange( oc );
		    }
		}
	    } catch ( OWLException ex ) {
		error( ex.getMessage() );
	    }
	}

	/* The following code is certainly an area of inefficiency --
	 * we should be able to deal with this in a nicer fashion. */

	/* Check that any inverseFunctional properties have been dealt
	 * with. */

	/* This is a troublesome area highlighted by an example from
	   Natasha Noy. In OWL Full, there's no reason why we can't
	   state that a DatatypeProperty is
	   inverseFunctional. However, the API doesn't actually
	   support this as it's really expecting "sensible" DL
	   ontologies. If the property is actually a datatype one,
	   then we end up creating a new object property. */
	
	workingTriples = getSubjects( v.getInstanceOf(), v.getInverseFunctionalProperty() );
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();

	    /* If the triple has been used, then it must be either:
	       1) an ObjectProperty that has already been created and
	          set to be inverseFunctional

	       2) a DatatypeProperty, in which case a warning will
	          already have been issued that the ontology is Full.
	    
	       In either case, we can safely ignore the triple.

	       If the triple hasn't been used, then we need to use it
	       and create an appropriate object property.
	    */

	    if (usedTriples.contains( triple ) ) {
		logger.debug( "Used IFP triple: " + triple );
	    } else {
		
		/* Record the fact that we've used this one. */
		usedTriples.add( triple );
		
		URI uri = triple.subject;
		
		try {
		    OWLObjectProperty op = onto.getObjectProperty( uri );
		    if ( op == null ) {
			/* Need to add this and set the appropriate flag */
			OWLObjectProperty entity = createObjectProperty( onto, uri );
			definitelyObjectProperties.add( uri );
			assumedObjectProperties.remove( uri );
			/* Now set it as inverse functional */
			try {
			    OntologyChange oc = 
				new SetInverseFunctional(onto, entity, true, null);
			    applyChange( oc );
			} catch ( OWLException ex ) {
			    error( ex.getMessage() );
			}
		    }
		} catch ( OWLException ex ) {
		    error( ex.getMessage() );
		}
	    }
	}

	/* Check that any symmetric properties have been dealt
	 * with */
	workingTriples = getSubjects( v.getInstanceOf(), v.getSymmetricProperty() );
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;

	    try {
		OWLObjectProperty op = onto.getObjectProperty( uri );
		if ( op == null ) {
		    /* Need to add this and set the appropriate flag */
		    OWLObjectProperty entity = createObjectProperty( onto, uri );
		    definitelyObjectProperties.add( uri );
		    assumedObjectProperties.remove( uri );
		    /* Now set it as symmetric */
		    try {
			OntologyChange oc = 
			    new SetSymmetric(onto, entity, true, null);
			applyChange( oc );
		    } catch ( OWLException ex ) {
			error( ex.getMessage() );
		    }
		}
	    } catch ( OWLException ex ) {
		error( ex.getMessage() );
	    }
	}

	/* Check that any transitive properties have been dealt
	 * with */
	workingTriples = getSubjects( v.getInstanceOf(), v.getTransitive() );
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;

	    try {
		OWLObjectProperty op = onto.getObjectProperty( uri );
		if ( op == null ) {
		    /* Need to add this and set the appropriate flag */
		    OWLObjectProperty entity = createObjectProperty( onto, uri );
		    definitelyObjectProperties.add( uri );
		    assumedObjectProperties.remove( uri );
		    /* Now set it as transitive */
		    try {
			OntologyChange oc = 
			    new SetTransitive(onto, entity, true, null);
			applyChange( oc );
		    } catch ( OWLException ex ) {
			error( ex.getMessage() );
		    }
		}
	    } catch ( OWLException ex ) {
		error( ex.getMessage() );
	    }
	}

	workingTriples = getSubjects( v.getInstanceOf(), v.getAnnotationProperty() );
	progressLogger.info( "AnnotationProperties: " + workingTriples.size() );
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;
	    OWLAnnotationProperty entity = createAnnotationProperty( onto, uri );
	    definitelyAnnotationProperties.add( uri );
	    
	    definitelyTypedIndividuals.add( uri );
	    untypedIndividuals.remove( uri );
	    //	    usedTriples.add( triple );
	    /* TODO: Still need to add these to the ontology! */
	}
	progressLogger.info( definitelyAnnotationProperties.size() + " AnnotationProperties present." );
	
	workingTriples = getSubjects( v.getInstanceOf(), v.getDatatype() );
	progressLogger.info( "Datatypes: " + workingTriples.size() );
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;
	    
	    /* Add a datatype.*/
	    OWLDataType entity = createDatatype( onto, uri );
	    /* TODO: Still need to add these to the ontology! */
	}

	/* Now we need to do the imports. This should ensure that
	   everything is defined before it's used. */

	workingTriples = getByPredicate( v.getImports() );
	progressLogger.info( "Imports: " + workingTriples.size() );
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;

	    if ( importing ) {
		/* It's an import.... */
		importOntology(onto, triple.subject, triple.object);
	    }
	}

	progressLogger.info( "Defining Classes" );

	/* Now for each class we need to add any definitions that the
	 * class may have. */

	workingTriples = getSubjects( v.getInstanceOf(), v.getClass_() );
	//	progressLogger.info( "Classes: " + workingTriples.size() );


	/* This breaks down because of owl:Thing and owl:Nothing. They
	 * don't have to have a class triple, and so may bugger things
	 * up here. */

	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    
	    if (logger.isDebugEnabled()) {
		logger.debug("Defining Class: " + triple.subject);
	    }
	    URI uri = triple.subject;


// 	for (Iterator it = owlClasses.iterator();
// 	     it.hasNext();) {
// 	    URI uri = (URI) it.next();
	    if (!isAnonymousNode( uri.toString() ) ) {

		/* We can assume it's in there as we've added it */
		OWLClass clazz = null;
		try {
		    clazz = onto.getClass( uri );
		} catch (OWLException ex) {
		    error( ex.getMessage() );
		}
		Set oneofs = getObjects( uri.toString(), v.getOneOf() );
		
		// 		if (owlOneOf.containsKey( uri ) ) {
		// 		    Set oneofs = (Set) owlOneOf.get( uri );
		for (Iterator ooit = oneofs.iterator(); 
		     ooit.hasNext(); ) {
		    
		    /* It's a one of */
		    if (logger.isDebugEnabled()) {
			logger.debug("  one-of");
		    }
		    
		    Triple ooTriple = (Triple) ooit.next();
		    usedTriples.add( ooTriple );
		    URI listStart = ooTriple.object;

		    HashSet uriSet = new HashSet();
		    if (logger.isDebugEnabled()) {
			logger.debug("  listToSet");
		    }
		    
		    rdfListToSet(listStart, uriSet);
		    if (logger.isDebugEnabled()) {
			logger.debug("  uriToIndividual");
		    }
		    
		    Set individuals = uriToIndividualSet(uriSet);
		    
		    if (logger.isDebugEnabled()) {
			logger.debug("  adding " + individuals.size() + " individuals");
		    }
		    try {
			
			OWLEnumeration enum_ = factory.getOWLEnumeration(individuals);		    
			if (logger.isDebugEnabled()) {
			    logger.debug("  made enum ");
			}
			
			AddEnumeration ae = new AddEnumeration(onto, 
							       clazz, 
							       enum_,
							       null);
			if (logger.isDebugEnabled()) {
			    logger.debug("  applying change ");
			}
			
			applyChange( ae );
		    } catch (OWLException e) {
			error( e.getMessage() );
		    }
		    if (logger.isDebugEnabled()) {
			logger.debug("  done");
		    }
		} // ooit
		
		Set unionofs = getObjects( uri.toString(), v.getUnionOf() );
		
		for (Iterator uoit = unionofs.iterator();
		     uoit.hasNext(); ) {
		    
		    /* It's a union */ 
		    if (logger.isDebugEnabled()) {
			logger.debug("  union");
		    }
		    Triple uoTriple = (Triple) uoit.next();
		    usedTriples.add( uoTriple );
		    URI listStart = uoTriple.object;
		    //URI listStart = (URI) cachedUnionOf.get( uri );
		    //		    URI listStart = (URI) uoit.next();
		    
		    HashSet uriSet = new HashSet();
		    rdfListToSet(listStart, uriSet);
		    Set classes = uriToClassSet(uriSet);
		    try {
			OWLDescription desc = factory.getOWLOr(classes);
			AddEquivalentClass ae = new AddEquivalentClass(onto, 
								       clazz, 
								       desc,
								       null);
			applyChange( ae );
		    } catch (OWLException e) {
			error( e.getMessage() );
		    }
		    if (logger.isDebugEnabled()) {
			logger.debug("  done");
		    }
		} // uoit
		
		Set intersectionOfs = getObjects( uri.toString(), v.getIntersectionOf() );
		
		for (Iterator ioit = intersectionOfs.iterator();
		     ioit.hasNext(); ) {
		    /* It's an intersection */
		    if (logger.isDebugEnabled()) {
			logger.debug("  intersection");
		    }
		    Triple ioTriple = (Triple) ioit.next();
		    usedTriples.add( ioTriple );
		    URI listStart = ioTriple.object;
		    
		    HashSet uriSet = new HashSet();
		    rdfListToSet(listStart, uriSet);
		    Set classes = uriToClassSet(uriSet);
		    try {
			OWLDescription desc = factory.getOWLAnd(classes);
			AddEquivalentClass ae = new AddEquivalentClass(onto, 
								       clazz, 
								       desc,
								       null);
			applyChange( ae );
		    } catch (OWLException e) {
			error( e.getMessage() );
		    }
		    if (logger.isDebugEnabled()) {
			logger.debug("  done");
		    }
		} // ioit
		
		Set complementOfs = getObjects( uri.toString(), v.getComplementOf() );
		
		for (Iterator coit = complementOfs.iterator();
		     coit.hasNext(); ) {
		    
		    /* It's a complement */
		    if (logger.isDebugEnabled()) {
			logger.debug("  complement");
		    }
		    Triple coTriple = (Triple) coit.next();
		    usedTriples.add( coTriple );
		    URI argURI = coTriple.object;
		    checkAndConsumeBNode( argURI );
		    OWLDescription arg = translateDescription( argURI );
		    try {
			OWLDescription desc = factory.getOWLNot( arg );
			AddEquivalentClass ae = new AddEquivalentClass(onto, 
								       clazz, 
								       desc,
								       null);
			applyChange( ae );
		    } catch (OWLException e) {
			error( e.getMessage() );
		    }
		    if (logger.isDebugEnabled()) {
			logger.debug("  done");
		    }
		} // coit
	    } // isAnonymousNode 
	} // it

	/* We're done with the classes now so we can throw the set away to save space. */
	/* No we can't as we need it to check stuff later! SKB */
	//owlClasses = null;

	/* Stuff relating to properties now */

        // Inverses

	workingTriples = getByPredicate( v.getInverseOf() );
	progressLogger.info("Processing " + workingTriples.size() + " Inverses");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );
	    createInverseOf( onto, triple.subject, triple.object );
        }

        // Domains

	workingTriples = getByPredicate( v.getDomain() );
	progressLogger.info("Processing " + workingTriples.size() + " Domains");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );
	    createDomain( onto, triple.subject, triple.object );
        }

        // Range

	workingTriples = getByPredicate( v.getRange() );
	progressLogger.info("Processing " + workingTriples.size() + " Ranges");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );
	    createRange( onto, triple.subject, triple.object );
        }

	/* General Axioms now. */

	workingTriples = getByPredicate( v.getSubClassOf() );
	progressLogger.info("Processing " + workingTriples.size() + " SubClass");

	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    try {
		Triple triple = (Triple) it.next();
		/* Record the fact that we've used this one. */
		usedTriples.add( triple );

		URI subC = triple.subject;
		URI superC = triple.object;
		if (logger.isDebugEnabled()) {
		    logger.debug(subC + " -> " + superC);
		}
		
		/* If the left hand side is anonymous, remove it from
		 * the potentional orphans collection */
		//		potentialAnonymousOrphanNodes.remove( subC );
		
		OWLDescription c = translateDescription( subC );
		OWLDescription d = translateDescription( superC );
		checkAndConsumeBNode( subC );
		checkAndConsumeBNode( superC );
		if (c==null || d == null ) {
		    // It is not a Description
		    if (logger.isDebugEnabled()) {
			logger.debug( "Null Description: " + c + " " + d);
		    }
		    error( "SubClass problems: " + subC + " " + superC );
		}
		/* This is all dreadfully non-OO, but it works..... */
		if (c instanceof OWLClass) {
		    /* If the sub is actually a class, then stuff the
		     * supers into the definition. */
		    AddSuperClass asc = 
			new AddSuperClass(onto, (OWLClass) c, d, null);
		    applyChange( asc );
		    //		    }
		} else {
		    OWLSubClassAxiom ax;
		    ax = factory.getOWLSubClassAxiom(c, d);
		    AddClassAxiom ac = new AddClassAxiom(onto, ax, null);
		applyChange( ac );
		}
            } catch (OWLException e) {
                error( e.getMessage() );
            }
	} // subClass

	workingTriples = getByPredicate( v.getEquivalentClass() );
	progressLogger.info("Processing " + workingTriples.size() + " EquivalentClass");

	/* Technique here is as follows. 

	1) Partition all the nodes up into sets, where each set
           consists of connected nodes (via the equivalent class
           relationship).

	2) For each set, if the size is > 2, then treat as an axiom,
           and add an axiom containing all the things in the set. Mark
           any bnode occuring in the set as used.

	   Note that this is an improvement on earlier parsing as this
	   will attempt to grab as much into a single axiom as it can.
	*/

	/* Maps nodes to equivalence classes. */
	Map equivalenceClasses = new HashMap();
	/* Maps triples to the equivalence classes that they infer */
	Map triplesContributing = new HashMap();
	int eqClasses = 0;

	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );
	    
	    URI eq1 = triple.subject;
	    URI eq2 = triple.object;
	    
	    Integer eq1Key = (Integer) equivalenceClasses.get( eq1 );
	    Integer eq2Key = (Integer) equivalenceClasses.get( eq2 );
	    
	    if ( eq1Key == null && eq2Key == null ) {
		/* We've not seen either of them before. So pick a
		 * new index, and stick it in. */
		Integer index = new Integer( eqClasses++ );
		equivalenceClasses.put( eq1, index );
		equivalenceClasses.put( eq2, index );
		triplesContributing.put( triple, index );
	    } else if ( eq1Key == null ) {
		/* We've not seen eq1 before, but we have seen
		 * eq2, so we simply give eq1 the same index. */
		equivalenceClasses.put( eq1, eq2Key );
		triplesContributing.put( triple, eq2Key );
	    } else if ( eq2Key == null ) {
		/* We've not seen eq2 before, but we have seen
		 * eq1, so we simply give eq2 the same index. */
		equivalenceClasses.put( eq2, eq1Key );
		triplesContributing.put( triple, eq1Key );
	    } else {
		/* Tricky case. We've got to amalgamate the
		   equivalence classes. Anything with eq1's key
		   gets replaced by eq2's key.*/ 
		triplesContributing.put( triple, eq2Key );
		
		for (Iterator keys = equivalenceClasses.keySet().iterator();
		     keys.hasNext(); ) {
		    Object key = keys.next();
		    if ( equivalenceClasses.get( key ).equals( eq1Key ) ) {
			equivalenceClasses.put( key, eq2Key );
		    }
		}
		for (Iterator keys = triplesContributing.keySet().iterator();
		     keys.hasNext(); ) {
		    Object key = keys.next();
		    if ( triplesContributing.get( key ).equals( eq1Key ) ) {
			triplesContributing.put( key, eq2Key );
		    }
		}
	    }
	}
	try {
	    /* Ok. So now we've got this HashMap that encodes the
	     * equivalence classes. */
	    
// 	    for (Iterator ks = equivalenceClasses.keySet().iterator();
// 		 ks.hasNext(); ) {
// 		Object kk = ks.next();
// 		if (progressLogger.isDebugEnabled()) {
// 		    progressLogger.debug(kk + " ~~ " + equivalenceClasses.get( kk ) );
// 		}
// 	    }
	    
	    /* Get the indices of the equivalence classes. Turn it
	     * into a set to get rid of duplicates. */
	    Set indices = new HashSet(equivalenceClasses.values());
	    for (Iterator indexIt = indices.iterator();
		 indexIt.hasNext(); ) {
		Object index = indexIt.next();
		
		Set trips = new HashSet();
		for (Iterator keys = triplesContributing.keySet().iterator();
		     keys.hasNext(); ) {
		    Object key = keys.next();
		    if ( triplesContributing.get( key ).equals( index ) ) {
			trips.add( key );
		    }
		}
		
		/* If all the triples that contribute to the equivalence are of the form:
		   
		* name owl:equiv expression
		
		* then deal with them as equivalent definitions. This
		* helps to keep us in OWL Lite whenever
		* possible. Lordy! It's like pulling teeth...*/
		
		boolean defo = true;
		
		for (Iterator tripIt = trips.iterator();
		     tripIt.hasNext(); ) {
		    Triple trip = (Triple) tripIt.next();
		    defo = defo && !isAnonymousNode( trip.subject.toString() ); 
		}
		
		if (logger.isDebugEnabled()) {
		    logger.debug("DEFO: " + defo);
		}

		if ( defo ) {
		    /* Iterate round the triples and add equivalent
		     * class definitions whereever necessary. */
		    for (Iterator tripIt = trips.iterator();
			 tripIt.hasNext(); ) {
			Triple trip = (Triple) tripIt.next();
			if (logger.isDebugEnabled()) {
			    logger.debug( "Equiv: " + trip );
			}

			URI subj = (URI) trip.subject;
			URI obj = (URI) trip.object;
			
			OWLDescription lhs = translateDescription( subj );
			OWLDescription rhs = translateDescription( obj );
			checkAndConsumeBNode( obj );
			
			AddEquivalentClass aec = new AddEquivalentClass(onto, (OWLClass) lhs, rhs, null);
			applyChange( aec );
		    }
		} else {
		    Set eqs = new HashSet();
		    for (Iterator keys = equivalenceClasses.keySet().iterator();
			 keys.hasNext(); ) {
			Object key = keys.next();
			if ( equivalenceClasses.get( key ).equals( index ) ) {
			    eqs.add( key );
			}
		    }
		    
		    /* We now have a set of
		     * equivalences. Hopefully. */
		    
		    /* Iterator over the URIs, translating to
		     * descriptions. */
		    Set descriptionSet = new HashSet();
		    for (Iterator descIt = eqs.iterator();
			 descIt.hasNext(); ) {
			URI descURI = (URI) descIt.next();
			OWLDescription d = translateDescription( descURI );
			/* Consume it */
			checkAndConsumeBNode( descURI );
			/* Add to the set. */
			descriptionSet.add( d );
		    }
		    
		    /* Now do the business. */
		    
		    OWLEquivalentClassesAxiom ax;
		    ax = factory.getOWLEquivalentClassesAxiom( descriptionSet );
		    AddClassAxiom ac = new AddClassAxiom(onto, ax, null);
		    applyChange( ac );
		}
	    }
	} catch (OWLException e) {
                error( e.getMessage() );
	}
    	
	workingTriples = getByPredicate( v.getDisjointWith() );

	progressLogger.info("Processing " + workingTriples.size() + " DisjointWith");

	/* This is all ***horribly*** inefficent, but it does
	 * work. Many thanks to Peter Patel-Schneider for discussion
	 * at DL'03 leading to this implementation. Disjoints are
	 * tackled as follows:
	 
	 * Look for a bnode that's involved in a disjointWith triple. 

	 * If you find one, then get hold of all the nodes that you
	 * can reach from that node s.t. the path doesn't pass
	 * *through* a named node.

	 * In order to be in DL, these nodes must then form a connected
	 * graph. If they do, we chuck in a multiple disjoint.

	 * Once we've done that, cycle through the disjointWith
	 * triples. If you find one that isn't covered by a multiple
	 * one as introduced above, chuck it in.

	*/

	boolean stillWorkToDo = false;;
	Set nodesAlreadyDone = new HashSet();
	URI found = null;

	/* Maps URIs to sets of things they're disjoint with. */
	Map multiDisjoints = new HashMap();
	
	do {

	    found = null;
	    
	    /* First of all, try and find a bnode that we haven't
	     * dealt with */
	    for (Iterator it = workingTriples.iterator();
		 it.hasNext();) {
		
		Triple triple = (Triple) it.next();
		/* Might be wrong... */
		usedTriples.add( triple );

		URI disj1 = triple.subject;
		URI disj2 = triple.object;
		
		if ( !nodesAlreadyDone.contains( disj1 ) && 
		     isAnonymousNode( disj1.toString() ) ) {
		    found = disj1;
		} 
		if ( !nodesAlreadyDone.contains( disj2 ) &&
		     isAnonymousNode( disj2.toString() ) ) {
		    found = disj2;
		}
	    }
	    if ( found != null ) {
		if (logger.isDebugEnabled()) {
		    logger.debug( "Found " + found );
		}
		/* We've got an anonymous node. */
		nodesAlreadyDone.add( found );
		
		/* This will represent the collection of those nodes that can be reached
		 * from a particular bnode via a path that does not pass
		 * *through* a non-bnode. */
		
		Set reachableNodes = new HashSet();
		reachableNodes.add( found );
		
		boolean somethingChanged = false;
		
		do {
		    somethingChanged = false;
		    for (Iterator it = workingTriples.iterator();
			 it.hasNext();) {
			
			Triple triple = (Triple) it.next();
			URI disj1 = triple.subject;
			URI disj2 = triple.object;
			/* If disj1 is in there, disj2 isn't and disj1
			 * is anonymous, then we throw disj2 in as
			 * well/ */
			if ( reachableNodes.contains( disj1 ) &&
			     !reachableNodes.contains( disj2 ) &&
			     isAnonymousNode( disj1.toString() ) ) {
			    if (logger.isDebugEnabled()) {
				logger.debug( "Found Reachable " + disj2 );
			    }

			    reachableNodes.add( disj2 );
			    nodesAlreadyDone.add( disj2 );
			    somethingChanged = true;
			}
			/* Similar other way round */
			if ( reachableNodes.contains( disj2 ) &&
			     !reachableNodes.contains( disj1 ) &&
			     isAnonymousNode( disj2.toString() ) ) {
			    if (logger.isDebugEnabled()) {
				logger.debug( "Found Reachable " + disj1 );
			    }

			    reachableNodes.add( disj1 );
			    nodesAlreadyDone.add( disj1 );
			    somethingChanged = true;
			}
		    }
		} while ( somethingChanged );
		/* Now we have a collection of reachable
		   nodes. These *must* be connected via a path consisting of
		   the disjoints. If not, then it's a bad situation. If so,
		   then we know that we have to add a disjoint consisting of
		   all the different bits. */ 
		if (logger.isDebugEnabled()) {
		    logger.debug( "--- disj ---" );
		    for (Iterator iter = reachableNodes.iterator(); iter.hasNext();) {
			logger.debug( iter.next() );
		    }
		    logger.debug( "+++ disj +++" );
		}

		/* Now check the connectedness of the graph */
		for (Iterator iter = reachableNodes.iterator(); 
		     iter.hasNext();) {
		    URI a = (URI) iter.next();
		    
		    for (Iterator iter2 = reachableNodes.iterator(); 
			 iter2.hasNext();) {

			URI b = (URI) iter2.next();
			
			if (logger.isDebugEnabled()) {
			    logger.debug( a + " -?- " + b );
			}

			if ( a != b ) {
			    /* Record that fact that we're doing a||b here */
			    Set s = (Set) multiDisjoints.get( a );
			    if (s==null) {
				s = new HashSet();
				multiDisjoints.put( a, s );
			    } 
			    s.add( b );

			    if ( !tripleExists( a.toString(), 
						v.getDisjointWith(),
						b.toString() ) &&
				 !tripleExists( b.toString(), 
						v.getDisjointWith(),
						a.toString() ) ) {
				owlFullConstruct( STRUCTURE_SHARING,
						  "Badly constructed disjoint! Non complete subgraph involving bnodes. No edge between " + 
						  a + " and " + b );
			    }
			}
		    }
		}
		/* We now have a set of
		 * disjoints. /
		 
		 /* Iterator over the URIs, translating to
		 * descriptions. */
		Set descriptionSet = new HashSet();
		for (Iterator descIt = reachableNodes.iterator();
		     descIt.hasNext(); ) {
		    URI descURI = (URI) descIt.next();
		    OWLDescription d = translateDescription( descURI );
		    /* Consume it */
		    checkAndConsumeBNode( descURI );
		    /* Add to the set. */
		    descriptionSet.add( d );
		}
		
		/* Now do the business. */
		try {
		    OWLDisjointClassesAxiom ax;
		    ax = factory.getOWLDisjointClassesAxiom( descriptionSet );
		    AddClassAxiom ac = new AddClassAxiom(onto, ax, null);
		    applyChange( ac );
		} catch (OWLException ex) {
		    error( ex.getMessage() );
		}
	    }
	} while ( found!=null );

	/* Still need to do the rest.... */
	
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    try {
		Triple triple = (Triple) it.next();
		/* Record the fact that we've used this one. */
		//		usedTriples.add( triple );
		
		URI disj1 = triple.subject;
		URI disj2 = triple.object;
		if (logger.isDebugEnabled()) {
		    logger.debug( disj1 + " || " + disj2 );
		}
		Set multis = (Set) multiDisjoints.get( disj1 );
		if (logger.isDebugEnabled()) {
		    logger.debug( "multis: " + multis );
		}
		if ( multis == null || !multis.contains( disj2 ) ) {
		    /* We haven't already done this one....*/
		    if (logger.isDebugEnabled()) {
			logger.debug( disj1 + " != " + disj2 );
		    }
		    
		    /* If the left hand side is anonymous, remove it from
		     * the potentional orphans collection */
		    //		    potentialAnonymousOrphanNodes.remove( disj1 );
		    
		    OWLDescription c = translateDescription( disj1 );
		    checkAndConsumeBNode( disj1 );
		    OWLDescription d = translateDescription( disj2 );
		    checkAndConsumeBNode( disj2 );
		    
		    if (c==null || d == null ) {
			// It is not a Description
			error( "Disjoint problems: " + disj1 + " " + disj2 );
		    }
		    HashSet set = new HashSet(2, 1);
		    set.add(d);
		    set.add(c);
		    OWLDisjointClassesAxiom ax;
		    
		    ax = factory.getOWLDisjointClassesAxiom(set);
		    AddClassAxiom ac = new AddClassAxiom(onto, ax, null);
		    applyChange( ac );
		}
	    } catch (OWLException e) {
		error( e.getMessage() );
	    }

	} // disjointClass

	workingTriples = getByPredicate( v.getSubPropertyOf() );
	progressLogger.info("Processing " + workingTriples.size() + " SubProperty");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );
	    URI prop1 = triple.subject;
	    URI prop2 = triple.object;

	    if (logger.isDebugEnabled()) {
		logger.debug(prop1 + " -> " + prop2);
	    }
	    createSubProperty( onto, prop1, prop2 );
	} // subProperty

	workingTriples = getByPredicate( v.getEquivalentProperty() );
	progressLogger.info("Processing " + workingTriples.size() + " EquivalentProperty");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );
	    URI prop1 = triple.subject;
	    URI prop2 = triple.object;
	    
	    if (logger.isDebugEnabled()) {
		logger.debug(prop1 + " == " + prop2);
	    }
	    createEquivalentProperty( onto, prop1, prop2 );
	} // subProperty

	/* Deal with the rdf:type triples. */

	/* This is a little more complex now as we have to basically
	   ignore any type triples that use the reserved vocabulary. */

	workingTriples = getByPredicate( v.getInstanceOf() );
	progressLogger.info("Processing " + workingTriples.size() + " Types");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    try {
		Triple triple = (Triple) it.next();

		/* Check to see if the object is one of the languagey
		   things */
		if ( languageMachineryType( triple.object ) ) {
		    if (logger.isDebugEnabled()) {
			logger.debug( "Ignoring: " + triple );
		    }
		} else {
		    /* Record the fact that we've used this one. */
		    usedTriples.add( triple );

		    URI indURI = triple.subject;
		    URI typeURI = triple.object;
		    checkAndConsumeBNode( typeURI );
		    if (!definitelyOntologies.contains( indURI )) {
			/* Don't do it if it's an ontology! */
			
			if (logger.isDebugEnabled()) {
			    logger.debug(indURI + "rdf:type " + typeURI);
			}
			OWLIndividual ind = translateIndividual( indURI );
			
			/* At this point we know that the uri is not an
			 * orphan node. Well, it might be, but we're
			 * allowed to have them if they're individuals. */
			//			potentialAnonymousOrphanNodes.remove( indURI );
			
			
			OWLDescription desc = translateDescription( typeURI );
			
			AddIndividualClass aic = new AddIndividualClass(onto, 
									ind, 
									desc,
									null);
			applyChange( aic );
			definitelyTypedIndividuals.add( indURI );
			untypedIndividuals.remove( indURI );
		    } // owlOntology check 
		}
	    } catch ( OWLException ex ) {
		error( ex.getMessage() );
	    }
	} // rdfTypes

	workingTriples = getSubjects( v.getInstanceOf(), v.getAllDifferent() );
	progressLogger.info("Processing " + workingTriples.size() + " AllDifferent");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    URI uri = triple.subject;
	    
	    /* We know it's not an orphan. */
	    //	    potentialAnonymousOrphanNodes.remove( uri );
	    
	    Set distincts = getObjects( uri.toString(), v.getDistinctMembers() );
	    
	    //	    Set distincts = (Set) distinctMembers.get( uri );
	    if (distincts.size() == 0) {
		owlFullConstruct( MALFORMED_ALLDIFFERENT, "AllDifferent " + uri + 
				  " with no distinctMembers");
	    } else if (distincts.size() > 1) {
		owlFullConstruct( MALFORMED_ALLDIFFERENT, "AllDifferent " + uri + 
				  " with multiple distinctMembers");
	    } else {
		
		//		URI listStart = (URI)
		//		getSingletonObject( distincts );
		Triple disTriple = ((Triple) getSingletonObject( distincts ));
		/* Record the fact that we've used this one. XXXXX
		 * Might be wrong...*/
		usedTriples.add( disTriple );

		URI listStart = disTriple.object;
		HashSet uriSet = new HashSet();
		if (logger.isDebugEnabled()) {
		    logger.debug("  listToSet");
		}
		
		rdfListToSet(listStart, uriSet);
		if (logger.isDebugEnabled()) {
		    logger.debug("  uriToIndividual");
		}
		
		Set individuals = uriToIndividualSet(uriSet);
		if (logger.isDebugEnabled()) {
		    logger.debug("  adding " + individuals.size() + " individuals");
		}
		try {
		    OWLDifferentIndividualsAxiom ax = factory.getOWLDifferentIndividualsAxiom(individuals);		    
		    if (logger.isDebugEnabled()) {
			logger.debug("  made axiom ");
		    }
		    AddIndividualAxiom aia = new AddIndividualAxiom(onto, 
								    ax, 
								    null);
		    if (logger.isDebugEnabled()) {
			logger.debug("  applying change ");
		    }
		    applyChange( aia );
		} catch (OWLException e) {
		    error( e.getMessage() );
		}
		if (logger.isDebugEnabled()) {
		    logger.debug("  done");
		}
	    } 
	} // allDifferent

        // SameAs

	workingTriples = getByPredicate( v.getSameAs() );
	progressLogger.info("Processing " + workingTriples.size() + " SameAs");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );

	    if (logger.isDebugEnabled()) {
		logger.debug(triple.subject + " === " + triple.object);
	    }
	    createSameIndividualAs( onto, triple.subject, triple.object );
        }

	workingTriples = getByPredicate( v.getDifferentFrom() );
	progressLogger.info("Processing " + workingTriples.size() + " DifferentFrom");
	for (Iterator it = workingTriples.iterator();
	     it.hasNext();) {
	    Triple triple = (Triple) it.next();
	    /* Record the fact that we've used this one. */
	    usedTriples.add( triple );
	    
	    if (logger.isDebugEnabled()) {
		logger.debug(triple.subject + " =/= " + triple.object);
	    }
	    createDifferentFrom( onto, triple.subject, triple.object );
        }

	/* Now make a note of any rdf properties or classes. This
	 * might be an indication of something going wrong.*/

	workingTriples = getSubjects( v.getInstanceOf(), rdfsV.getProperty() );
	
        for (Iterator iter = workingTriples.iterator();
	     iter.hasNext();
	     ) {
	    Triple triple = (Triple) iter.next();
	    usedTriples.add( triple );
	    rdfProperties.add( triple.subject );
	    warning( "Possibly using wrong vocabulary (rdf:Property instead of owl:[Object|Data]Property)\n\t" + triple.subject );
	}

	workingTriples = getSubjects( v.getInstanceOf(), RDFConstants.RDFS_CLASS );
	
        for (Iterator iter = workingTriples.iterator();
	     iter.hasNext();
	     ) {
	    Triple triple = (Triple) iter.next();
	    usedTriples.add( triple );
	    rdfsClasses.add( triple.subject );
	    warning( "Possibly using wrong vocabulary (rdfs:Class instead of owl:Class)\n\t" + triple.subject );
	}

	/* YASFCC! It may be that there are anonymous class nodes that
	 * aren't used If this is the case, were allowed them, as they
	 * correspond to an equivalent class axiom that has only one
	 * class in it. Sheesh! */

	for (Iterator it = bNodes.iterator();
	     it.hasNext(); ) {
	    URI bnode = (URI) it.next();
	    if ( !usedBNodes.contains( bnode ) ) {
		if ( tripleExists( bnode.toString(), 
				   v.getInstanceOf(), v.getClass_() ) ||
		     tripleExists( bnode.toString(), 
				   v.getInstanceOf(), v.getRestriction() ) ) {
		    OWLDescription desc = translateDescription( bnode );
		    checkAndConsumeBNode( bnode );
		    try {
			HashSet set = new HashSet();
			set.add(desc);
			OWLEquivalentClassesAxiom ax;
			ax = factory.getOWLEquivalentClassesAxiom(set);
			AddClassAxiom ac = new AddClassAxiom(onto, ax, null);
			applyChange( ac );
		    } catch (OWLException e) {
			error( e.getMessage() );
		    }
		}
	    }
	}

	/* At this point, we've done everything that relates to the
	 * definition of ontology classes and the like. We now provide
	 * the opportunity for subclasses to do something special
	 * here. For example, a parser for a rules language would look
	 * for anything that uses the special rules vocabulary and
	 * handle it here. This may result in triples being "used up"
	 * that then don't get processed in the next phase. */ 

	additionalParsingHook();
	
	/* Now deal with any other stuff lying around. These will
	 * basically be the triples that we haven't done anything with
	 * so far (and that haven't been identified as using the
	 * reserved vocabulary). Each will be translated into an
	 * object property instance linking two individuals. */

	Set remainingTriples = new HashSet();
	for (Iterator it = triples.iterator();
	     it.hasNext(); ) {
	    Triple triple = (Triple) it.next();
	    if (!usedTriples.contains( triple ) &&
		!(triple.predicate.toString().equals( v.getInstanceOf() ) &&
		  languageMachineryType( triple.object ) ) ) { 
		remainingTriples.add( triple );
	    }
	}
	
	progressLogger.info( remainingTriples.size() + 
			     " Triples left");

	/* Oh dear. This must have been using up a bit of time. */
// 	progressLogger.debug("++++++++++++++++++++++++++++++++++");
// 	for (Iterator it = remainingTriples.iterator();
// 	     it.hasNext(); ) {
// 	    progressLogger.debug( it.next() );
// 	}
// 	progressLogger.debug("++++++++++++++++++++++++++++++++++");

	
	/* Basically provides an simplified graph of the edges between
	 * BNodes. Maps each bnode to the bnodes it's connected to. */
	
	HashMap bNodeConnectedness = new HashMap();

	tripleCount = 0;
	
	for (Iterator iter = remainingTriples.iterator();
	     iter.hasNext();
	     ) {

	    /* Report on the progress of triples */
	    tripleCount++;
	    if ( tripleLogger.isInfoEnabled() &&
		 (tripleCount % tripleCountChunk) == 0 ) {
		tripleLogger.info( Integer.toString( tripleCount ) );
	    }

	    Triple triple = (Triple) iter.next();
	    if (logger.isDebugEnabled()) {
		logger.debug(triple.subject + "-" + triple.predicate + "-" + triple.object);
	    }
	    //	    logger.debug(uris[0] + "-" + uris[1] + "-" + uris[2]);
	    if (definitelyAnnotationProperties.contains( triple.predicate ) ) {
		createAnnotationInstance( onto, triple.subject, triple.predicate, triple.object );
		/* It's an annotation. */
	    } else if (owlOntologyProperties.contains(triple.predicate)) {
		/* It's an ontology property */

		if (!triple.predicate.toString().equals( v.getImports() ) ) {
		    /* Need to check that the subject and object are
		     * ontology properties. We don't do anything else
		     * at the moment though. */
		    if (!definitelyOntologies.contains( triple.subject ) ) {
			URI culprit = triple.subject;
			if ( isAnonymousNode( culprit.toString() ) ) {
			    culprit = null;
			}
			owlFullConstruct( UNTYPED_ONTOLOGY, 
					  "Untyped Ontology: " + triple.subject,
					  culprit );
		    }
		    if (!definitelyOntologies.contains( triple.object ) ) {
			URI culprit = triple.subject;
			if ( isAnonymousNode( culprit.toString() ) ) {
			    culprit = null;
			}
			owlFullConstruct( UNTYPED_ONTOLOGY,
					  "Untyped Ontology: " + triple.object,
					  culprit );
		    }
		}
	    } else {
		//		progressLogger.debug( "Property Instance: " + triple );
		createPropertyInstance(onto, triple.subject, triple.predicate, triple.object);
		/* If they're bNodes, record the fact that there's an
		 * edge between them */
		if ( isAnonymousNode( triple.subject.toString() ) &&
		     isAnonymousNode( triple.object.toString() ) ) {
		    Set s = (Set) bNodeConnectedness.get( triple.subject );
		    if ( s==null ) {
			s = new HashSet();
			bNodeConnectedness.put( triple.subject, s );
		    }
		    s.add( triple.object );
		}
	    }
	}
	

	/* Check for cycles between blank nodes. Basically involves
	 * starting at a blank node and checking whether a traversal
	 * along any path results in you arriving back where you
	 * started. If so, we're in Full. */
	
	for (Iterator bNodeIt = bNodeConnectedness.keySet().iterator();
	     bNodeIt.hasNext(); ) {
	    URI bNode = (URI) bNodeIt.next();
	    /* Those nodes we can get to from here */
	    Set newReachables = (Set) bNodeConnectedness.get( bNode );
	    Set reachables;

	    do {
		/* Take a copy */
		reachables = new HashSet( newReachables );

		/* Now for any node that's reachable, find any others
		 * that are reacahble, and chuck them into the set */
		for (Iterator reachIt = reachables.iterator();
		     reachIt.hasNext(); ) {
		    Object reach = reachIt.next();
		    newReachables.add( reach );
		    Set reachReach = (Set) bNodeConnectedness.get( reach );
		    if (reachReach!=null) {
			newReachables.addAll( reachReach );
		    }
		} 
		/* Carry on doing this until the sets are the same
		 * size, e.g. we haven't been able to add any more
		 * stuff. */
	    } while ( reachables.size() != newReachables.size() );
	    if (reachables.contains( bNode ) ) {
		owlFullConstruct( CYCLICAL_BNODES,
				  "Cycle in bNodes! " + bNode );
	    }
	}

	remainingTriples = new HashSet();
	for (Iterator it = literalTriples.iterator();
	     it.hasNext(); ) {
	    Object t = it.next();
	    if (!usedTriples.contains( t ) ) {
		remainingTriples.add( t );
	    }
	}

	progressLogger.info( remainingTriples.size() + 
			     " Data Triples left");

	/* Oh dear. This must have been using up a bit of time. */
// 	progressLogger.debug("++++++++++++++++++++++++++++++++++");
// 	for (Iterator it = remainingTriples.iterator();
// 	     it.hasNext(); ) {
// 	    progressLogger.debug( it.next() );
// 	}
// 	progressLogger.debug("++++++++++++++++++++++++++++++++++");
	
	tripleCount = 0;
        for (Iterator iter = remainingTriples.iterator();
	     iter.hasNext();
	     ) {
	    
	    /* Report on the progress of data triples */
	    tripleCount++;
	    if ( tripleLogger.isInfoEnabled() &&
		 (tripleCount % tripleCountChunk) == 0 ) {
		tripleLogger.info( Integer.toString( tripleCount ) );
	    }
	    LiteralTriple triple = (LiteralTriple) iter.next();

	    URI subject = triple.subject;
	    URI predicate = triple.predicate;

	    String object = triple.object;
	    String datatype = triple.dataType;
	    String language = triple.language;

	    if ( language == null ) {
		if (logger.isDebugEnabled()) {
		    logger.debug(subject + "-" + predicate + "-" + object + "^^" + datatype );
		}
	    } else {
		if (logger.isDebugEnabled()) {
		    
		    logger.debug(subject + "-" + predicate + "-" + object + "@" + language);
		}
	    }
	    
	    if (definitelyAnnotationProperties.contains( predicate ) ) {
		/* It's an annotation. */
		try {
		    /* Build a DataValue for the annotation */
		    URI dt = null;
		    String lang = null;
		    /* If there's a language specified use that. */
		    if (language!=null) {
			lang = language;
		    } else {
			/* Otherwise use the datatype. */
			try {
			    /* By default just assume these things are
			     * strings. Might want to change this
			     * later to allow nulls...*/
			    
			    /* Allowing null values in here..... */
			    //dt = xsdString;
			    //newURI("http://www.w3.org/2001/XMLSchema#string");
			    if (datatype !=null) {
				dt = newURI( datatype );
			    }
			} catch (URISyntaxException e) {
			}
		    }
		    if (!ignoreAnnotationContent) {
			OWLDataValue dv = 
			    factory.getOWLConcreteData( dt, 
							lang,
							object );
			/* Create the annotation */
			createAnnotationInstance(onto, subject, predicate, dv);
		    }
		} catch (OWLException ex) {
		    error(ex.getMessage());
		}
	    } else if (owlOntologyProperties.contains( predicate )) {
		/* It's an ontology property */
	    } else {
		createDataPropertyInstance(onto, subject,
					   predicate, datatype, language, 
					   object );
	    }
	}	

	/** STILL TODO: 

	o Annotations
	o Ontology Properties (other than imports) 

	*/

	/* Post-processing checks */

	/* Check that everything is defined when you've finished. This
	 * can be omitted if the consumer is being used for an import
	 * parse. After finishing an import parse, there may be
	 * entities that are undefined because the definitions are in
	 * the enclosing ontologies (which is fine). SKB 18/02/03. */
	
	if ( checkDefinitions ) {
	    checkForUndefinedOntologies();
	    checkForUndefinedObjectProperties();
	    checkForUndefinedDataProperties();
	    checkForUndefinedClasses();
	    checkForUntypedIndividuals();
	    checkForRDFProperties();
	    checkForRDFSClasses();
	    checkForOneOfs();
	    checkBooleans();
	}
	/* Check that lists are done properly. */
	checkForMalformedLists();
	progressLogger.info("Parsing Complete");
	
	/* Make sure that the triple structure can be garbage collected. */
	releaseTripleModel();
    } // endModel
    
    /** Returns an OWLDescription that corresponds to the given URI. */
    protected OWLDescription translateDescription( URI uri ) throws SAXException {
	if (logger.isDebugEnabled()) {
	    logger.debug( "Translating: " + uri );
	}
	
	if (uri.toString().equals(OWLVocabularyAdapter.INSTANCE.getThing())) {
	    try {
		return factory.getOWLThing();
	    } catch (OWLException ex) {
		error( ex.getMessage() );
	    }
	}
	if (uri.toString().equals(OWLVocabularyAdapter.INSTANCE.getNothing())) {
	    try {
		return factory.getOWLNothing();
	    } catch (OWLException ex) {
		error( ex.getMessage() );
	    }
	}
	
	/* If the description has been processed, we're done. This
	 * will be the case if, for instance, the class is a named
	 * one. */
	if (logger.isDebugEnabled()) {
	    logger.debug( "Checking translations..." );
	}
	OWLDescription tryThis = (OWLDescription) translatedDescriptions.get( uri );
	if (tryThis!=null) {
	    /* Just return it */
	    if (logger.isDebugEnabled()) {
		logger.debug( "Found " + tryThis );
	    }

	    return tryThis;
	}
	if (logger.isDebugEnabled()) {
	    logger.debug( "Not Found " );
	}

	if ( pendingTranslations.contains( uri ) ) {
	    owlFullConstruct( STRUCTURE_SHARING,
			      "Cycle in descriptions! " + uri );
	    /* This should perhaps be an error as the thing is going
	     * to end up looping horribly. However, for the time being
	     * we'll just send back some bogus thing to make sure we
	     * get somewhere...*/
	    OWLClass cl = getAndAssumeOWLClass( uri );
	    translatedDescriptions.put( uri, cl );
	    pendingTranslations.remove( uri );
	    return cl;
	}

	pendingTranslations.add( uri );
	
	/* ********* Stop gap!!! ***************
	   SKB */
        URI dataUri = null;
        try {
            dataUri = newURI("http://to.do.com/");
        } catch (URISyntaxException e) {
        }

	/* Ok. So now we know that we've not got a named class. There
	 * are three alternatives here.

	 1) It's a boolean
	 2) It's a restriction
	 3) It's an enumeration
	 
	 In addition, it should be *exactly* one of these if we're in
	 DL. */
	
	/* A quick check to see if there's anything nasty around,
	 * e.g. if there are multiple definitions of an anonymous
	 * class. If so, we're in Full. */
	
	int count = 0 ;
	int selector = UNSET;
	
	Set s = getObjects( uri.toString(), v.getOneOf() );
	if (!s.isEmpty()) {
	    selector = ONEOF;
	    count = count + s.size();
	}
	s = getObjects( uri.toString(), v.getUnionOf() );
	if (!s.isEmpty()) {
	    selector = UNION;
	    count = count + s.size();
	}
	s = getObjects( uri.toString(), v.getIntersectionOf() );
	if (!s.isEmpty()) {
	    selector = INTERSECTION;
	    count = count + s.size();
	}
	s = getObjects( uri.toString(), v.getComplementOf() );
	if (!s.isEmpty()) {
	    selector = COMPLEMENT;
	    count = count + s.size();
	}
	/* Need to check types for restrictions. */
	if ( tripleExists( uri.toString(), v.getInstanceOf(), v.getRestriction() ) ) {
	    selector = RESTRICTION;
	    count = count++;
	}
	
	if (count > 1) {
	    /* There's some multiple definition stuff going on here.*/
	    owlFullConstruct( MULTIPLE_DEFINITIONS,
			      "Multiple definitions of an anonymous class " + 
			      uri + ". Further messages may be misleading...." );
	}

	/* Now for each of the possible types of things, we need to do
	   the necessary, e.g. translate it, stick it in the *
	   collection and then return it. */

	if (logger.isDebugEnabled()) {
	    logger.debug("Translating " + uri + " " + selector);
	}
	switch (selector) {
	case UNSET: 
	    {
		/* We'll sort this out later... */
		//owlFullConstruct( "Unknown resource " + uri );
		break;
	    }
	case ONEOF: 
	    {
		Set oneofs = getObjects( uri.toString(), v.getOneOf() );
		/* Earlier checks guarantee that there's only one. */
		Triple ooTriple = (Triple) getSingletonObject( oneofs );
		usedTriples.add( ooTriple );
		URI listStart = ooTriple.object;	    

		HashSet uriSet = new HashSet();
		rdfListToSet(listStart, uriSet);
		Set individuals = uriToIndividualSet(uriSet);
		
		OWLDescription desc;
		try {
		    desc = factory.getOWLEnumeration(individuals);
		    translatedDescriptions.put(uri, desc);
		    pendingTranslations.remove( uri );
		    definitelyClasses.add( uri );
		    return desc;
		} catch (OWLException e) {
		    error( e.getMessage() );
		}
		break;
	    }
	case UNION: 
	    {
		Set unions = getObjects( uri.toString(), v.getUnionOf() );
		/* Earlier checks guarantee that there's only one. */
		Triple uoTriple = (Triple) getSingletonObject( unions );
		usedTriples.add( uoTriple );
		URI listStart = uoTriple.object;	    

		if (logger.isDebugEnabled()) {
		    logger.debug("Union: " + uri );
		}
		HashSet uriSet = new HashSet();
		rdfListToSet(listStart, uriSet);
		Set classes = uriToClassSet(uriSet);
		OWLDescription desc;
		try {
		    desc = factory.getOWLOr(classes);
		    translatedDescriptions.put(uri, desc);
		    pendingTranslations.remove( uri );
		    definitelyClasses.add( uri );
		    return desc;
		} catch (OWLException e) {
		    error( e.getMessage() );
		}
		break;
	    }
	case INTERSECTION:
	    {
		Set intersections = getObjects( uri.toString(), v.getIntersectionOf() );
		/* Earlier checks guarantee that there's only one. */
		Triple ioTriple = (Triple) getSingletonObject( intersections );
		usedTriples.add( ioTriple );
		URI listStart = ioTriple.object;	    
		
		HashSet uriSet = new HashSet();
		rdfListToSet(listStart, uriSet);
		Set classes = uriToClassSet(uriSet);
		OWLDescription desc;
		try {
		    desc = factory.getOWLAnd(classes);
		    translatedDescriptions.put(uri, desc);
		    pendingTranslations.remove( uri );
		    definitelyClasses.add( uri );
		    return desc;
		} catch (OWLException e) {
		    error( e.getMessage() );
		}
		break;
	    }
	case COMPLEMENT:
	    {
		Set complements = getObjects( uri.toString(), v.getComplementOf() );
		/* It's a complement */
		Triple coTriple = (Triple) getSingletonObject( complements );
		usedTriples.add( coTriple );
		URI argURI = coTriple.object;	   

		OWLDescription arg = translateDescription( argURI );
		checkAndConsumeBNode( argURI );

		OWLDescription desc;
		try {
		    desc = factory.getOWLNot( arg );
		    translatedDescriptions.put( uri, desc );
		    pendingTranslations.remove( uri );
		    definitelyClasses.add( uri );
		    return desc;
		} catch (OWLException e) {
		    error( e.getMessage() );
		}
		break;
	    }
	case RESTRICTION:
	    {
		/* It's a restriction */
		Triple resTriple = getTriple( uri.toString(), v.getInstanceOf(), v.getRestriction() );

		if ( resTriple!=null ) {
		    usedTriples.add( resTriple );
		}

		/* First check that there's a single onProperty */

		Set onProps = getObjects( uri.toString(), v.getOnProperty() );
		
		if (onProps.size() == 0) {
		    owlFullConstruct( MALFORMED_RESTRICTION,
				      "Restriction with no onProperty: " + uri );
		} else if (onProps.size() > 1) {
		    owlFullConstruct( MALFORMED_RESTRICTION,
				      "Restriction with multiple onProperty: " + uri );
		}

		/* There's a problem here. If the thing is malformed
		 * and the error handler doesn't stop us, we end up
		 * throwing an exception here as the set has no
		 * elements.... SKB 14/04/04*/

		/* It's a complement */
		Triple opTriple = (Triple) getSingletonObject( onProps );
		usedTriples.add( opTriple );

		
		/* Get the property URI */
		URI prop = opTriple.object;
		
		/* Now find out what kind of restriction it is. We can do
		 * a similar check here for well-formed ness. It should
		 * appear in one and only one of:
		 
		 * allValues
		 * someValues
		 * hasValue
		 * dataHasValue
		 * minCardinality
		 * maxCardinality
		 * cardinality
		 */
		
		int restrictionCount = 0 ;
		int restrictionSelector = UNSET;
		Set rs = getObjects (uri.toString(), v.getAllValuesFrom() );
		if (!rs.isEmpty()) {
		    restrictionSelector = ALL;
		    restrictionCount = restrictionCount + rs.size();
		}
		rs = getObjects (uri.toString(), v.getSomeValuesFrom() );
		if (!rs.isEmpty()) {
		    restrictionSelector = SOME;
		    restrictionCount = restrictionCount + rs.size();
		}
		rs = getObjects (uri.toString(), v.getHasValue() );
		if (!rs.isEmpty()) {
		    restrictionSelector = HASVALUE;
		    restrictionCount = restrictionCount + rs.size();
		}
		rs = getLiterals (uri.toString(), v.getHasValue() );
		//		rs = (Set) dataHasValue.get( uri );
		if (!rs.isEmpty()) {
		    restrictionSelector = DATAHASVALUE;
		    restrictionCount = restrictionCount + rs.size();
		}
		rs = getLiterals (uri.toString(), v.getMinCardinality() );
		//		rs = (Set) minCardinality.get( uri );
		if (!rs.isEmpty()) {
		    restrictionSelector = MIN;
		    restrictionCount = restrictionCount + rs.size();
		}
		rs = getLiterals (uri.toString(), v.getMaxCardinality() );
		//		rs = (Set) maxCardinality.get( uri );
		if (!rs.isEmpty()) {
		    restrictionSelector = MAX;
		    restrictionCount = restrictionCount + rs.size();
		}
		rs = getLiterals (uri.toString(), v.getCardinality() );
		//		rs = (Set) cardinality.get( uri );
		if (!rs.isEmpty()) {
		    restrictionSelector = CARD;
		    restrictionCount = restrictionCount + rs.size();
		}
		if (restrictionCount > 1) {
		    /* Multiple restrictions being specified. */
		    owlFullConstruct( MALFORMED_RESTRICTION,
				      "Malformed Restriction (multiple types) " + uri );
		}
		
		if (logger.isDebugEnabled()) {
		    logger.debug("Translating Restriction: " + uri + " " + restrictionSelector);
		}

		/* Now switch on the restriction type */
		switch( restrictionSelector ) {
		case UNSET: 
		    {
			owlFullConstruct( MALFORMED_RESTRICTION,
					  "Unknown RestrictionType " + uri );
			break;
		    }

		    /* The following cases are a bit tricky. There may
		       be situations where a property is used without
		       an explicit specification of whether it's data
		       or object. This is OWL Full. The question is
		       then how to recover. Here we've taking the view
		       that if we can't tell, we'll assume it's object
		       and then take it from there. There will be
		       cases (for example if the property is then used
		       with a data type) where we could do a better
		       job, but at this point I don't really care.  */ 
		case SOME:
		case ALL:
		case HASVALUE:
		case DATAHASVALUE:
		    {
			/* Depends on whether the property is an
			 * object one or a data one. If it definitely
			 * is, or is known not to be a data one, then
			 * go for the object.  */

			/* Changed here to use the definitely known
			   ones. */

			/* This may need to change. We should do the following:

			1. If it's definitely object, go for that 

			2. If it's definitely data, go for that.

			3. If not, make a guess, but based on some
			other information, e.g. the context. To get
			this right, we probably want to ensure that we
			do all the things that could give us context
			first, e.g some/all etc. Cardinality
			restrictions don't tell us anything.  */

// 			if (owlObjectProperties.contains( prop ) ||
// 			    !owlDataProperties.contains( prop )) {
			if (definitelyObjectProperties.contains( prop ) ) {
			    /* ObjectProperty restriction. We can
			     * assume it's in the ontology. */

			    /* Translate it as an object property
			     * restriction */
			    OWLDescription desc = translateRestriction(uri, 
								       prop, 
								       restrictionSelector, 
								       true);
			    pendingTranslations.remove( uri );
			    definitelyClasses.add( uri );
			    return desc;

			} else if (definitelyDataProperties.contains( prop ) ) {
			    /* It's a data some. */
			    OWLDescription desc = translateRestriction(uri, 
								       prop, 
								       restrictionSelector,
								       false);
			    pendingTranslations.remove( uri );
			    definitelyClasses.add( uri );
			    return desc;

			} else {
			    /* At this point we know it's broken, but we can try a few things. */
			    /* Possible strategies are: 

			       1. Look at any some/all properties and
			       try and see if they're classes. 

			       2. ermmm.

			    */
			    /* Translate it as an object property
			     * restriction */
			    boolean guessItsAnObject = true;
			    if (restrictionSelector == HASVALUE) {
				/* Should be Object Property. */
				guessItsAnObject = true;
			    } else if (restrictionSelector == DATAHASVALUE) {
				/* Should be Data Property. */
				guessItsAnObject = false;
			    } else if (restrictionSelector == SOME) {
				/* We've got a SOME. Have a peek at
				 * the thing at the end and see if
				 * that's going to help us at all. */
				Set trips = getObjects (uri.toString(), v.getSomeValuesFrom() );
				Triple trip = (Triple) getSingletonObject( trips );
				if ( definitelyClasses.contains( trip.object ) ) {
				    guessItsAnObject = true;
				} else if ( definitelyDatatypes.contains( trip.object ) ) {
				    guessItsAnObject = false;
				}
			    } else if (restrictionSelector == ALL) {
				/* We've got an ALL. Have a peek at
				 * the thing at the end and see if
				 * that's going to help us at all. */
				Set trips = getObjects (uri.toString(), v.getAllValuesFrom() );
				Triple trip = (Triple) getSingletonObject( trips );
				if ( definitelyClasses.contains( trip.object ) ) {
				    guessItsAnObject = true;
				} else if ( definitelyDatatypes.contains( trip.object ) ) {
				    guessItsAnObject = false;
				}
			    }
			    //			    System.out.println( "Guessing: " + guessItsAnObject );
			    OWLDescription desc = translateRestriction(uri, 
								       prop, 
								       restrictionSelector, 
								       guessItsAnObject);
			    pendingTranslations.remove( uri );
			    definitelyClasses.add( uri );
			    return desc;

			}
		    }
		case MIN: 
		case MAX:
		case CARD:
		    {
			if (definitelyObjectProperties.contains( prop ) ) {
			    /* ObjectProperty restriction. We can
			     * assume it's in the ontology. */
			    
			    /* Translate it as an object property
			     * restriction */
			    pendingTranslations.remove( uri );
			    definitelyClasses.add( uri );
			    return translateCardinalityRestriction(uri, prop, restrictionSelector, true);
			} else if ( definitelyDataProperties.contains( prop ) ) {
			    /* It's a data some. */
			    pendingTranslations.remove( uri );
			    definitelyClasses.add( uri );
			    return translateCardinalityRestriction(uri, prop, restrictionSelector, false);
			} else {
			    /* Guessing Object again... */
			    pendingTranslations.remove( uri );
			    definitelyClasses.add( uri );
			    return translateCardinalityRestriction(uri, prop, restrictionSelector, true);
			}
		    }
		default:
		    {
			error(" TODO (Restriction) " + restrictionSelector );
		    }
		}
		break;
	    }
	default: 
	    {
		error(" TODO (Translation) " + selector );
	    }
	}
	/* If we're checking definitions (e.g. we're at the top
	 * level of the parsing, and we haven't yet been able to
	 * determine that the thing is a class, then warn about
	 * this. */
	/* Don't do this here. Worry about it later, once the parse is
	 * complete. */
// 	if (checkDefinitions) {
// 	    /* We don't know for sure that it's a class. */
// 	    owlFullConstruct("Unknown URI type: " + uri);
// 	}
	/* Last gasp attempt to recover something... */
	OWLClass cl = getAndAssumeOWLClass( uri );
	translatedDescriptions.put( uri, cl );
	pendingTranslations.remove( uri );
	return cl;
    } //translateDescription

    /** Returns a translation of the given URI */
    protected OWLDescription translateRestriction( URI uri,
						 URI propURI,
						 int type,
						 boolean obj ) throws SAXException{

	if (logger.isDebugEnabled()) {
	    logger.debug("Translating Restriction: " + uri + " " 
			 + propURI + " " + type + " " + obj );
	}
	OWLDescription desc = null;
	try {
	    if (obj) {
		OWLObjectProperty prop =
		    onto.getObjectProperty( propURI );
		if (prop==null) {
		    /* The ontology doesn't know about it, so we'll
		       create one and record the assumption */ 
		    prop = createObjectProperty(onto, propURI );
		    if (!definitelyObjectProperties.contains( propURI )) {
			assumedObjectProperties.add( propURI );
		    }
		}
		/* Get the uri of the filler */
		URI filler = null;
		if (type == SOME) {
		    Set someTrips = getObjects (uri.toString(), v.getSomeValuesFrom() );
		    Triple someTrip = (Triple) getSingletonObject( someTrips );
		    usedTriples.add( someTrip );
		    filler = someTrip.object;
		    /* Translate it */
		    if (logger.isDebugEnabled()) {
			logger.debug("Filler: " + filler);
		    }
		    OWLDescription fillerDesc = 
			translateDescription( filler );
		    checkAndConsumeBNode( filler );

		    desc =
			factory.getOWLObjectSomeRestriction(prop, 
								fillerDesc);
		} else if (type == ALL) {
		    Set allTrips = getObjects (uri.toString(), v.getAllValuesFrom() );
		    Triple allTrip = (Triple) getSingletonObject( allTrips );
		    usedTriples.add( allTrip );
		    filler = allTrip.object;
		    /* Translate it */
		    if (logger.isDebugEnabled()) {
			logger.debug("Filler: " + filler);
		    }
		    OWLDescription fillerDesc = 
			translateDescription( filler );
		    checkAndConsumeBNode( filler );

		    desc =
			factory.getOWLObjectAllRestriction(prop, 
							       fillerDesc);
		} else if (type == HASVALUE) {
		    Set fillerTrips = getObjects (uri.toString(), v.getHasValue() );
		    Triple fillerTrip = (Triple) getSingletonObject( fillerTrips );
		    usedTriples.add( fillerTrip );
		    filler = fillerTrip.object;
		    /* Translate it */
		    if (logger.isDebugEnabled()) {
			logger.debug("Filler: " + filler);
		    }
		    OWLIndividual ind = 
			translateIndividual( filler );
		    desc =
			factory.getOWLObjectValueRestriction(prop, 
								    ind);
		}
	    } else {
		OWLDataProperty prop =
		    onto.getDataProperty( propURI );
		if (prop == null) {
		    /* The ontology doesn't know about it, so we'll
		       create one and record the assumption */ 
		    prop = createDataProperty(onto, propURI );
		    if (!definitelyDataProperties.contains( propURI )) {
			assumedDataProperties.add( propURI );
		    }
		}
		
		/* Get the uri of the filler */
		URI filler = null;
		if (type == SOME) {
		    Set fillerTrips = getObjects (uri.toString(), v.getSomeValuesFrom() );
		    Triple fillerTrip = (Triple) getSingletonObject( fillerTrips );
		    usedTriples.add( fillerTrip );
		    filler = fillerTrip.object;
		    /* Translate it */
		    /* ***** BUG 25/03/04 ***** 
		       Will *not* spot undefined data
		       types used in restrictions. SKB 25/3/04. */
		    OWLDataRange fillerType =  translateDataRange(filler);
			//factory.getOWLConcreteDataType( filler );
		    desc =
			factory.getOWLDataSomeRestriction(prop, 
							  fillerType);
		} else if (type==ALL) {
		    Set fillerTrips = getObjects (uri.toString(), v.getAllValuesFrom() );
		    Triple fillerTrip = (Triple) getSingletonObject( fillerTrips );
		    usedTriples.add( fillerTrip );
		    filler = fillerTrip.object;
		    /* Translate it */
		    OWLDataRange fillerType = translateDataRange(filler);
			//factory.getOWLConcreteDataType( filler );
		    desc =
			factory.getOWLDataAllRestriction(prop, 
							 fillerType);
		} else if (type==DATAHASVALUE) {
		    Set fillerTrips = getLiterals (uri.toString(), v.getHasValue() );
		    LiteralTriple fillerTrip = (LiteralTriple) getSingletonObject( fillerTrips );
		    usedTriples.add( fillerTrip );
		    if (logger.isDebugEnabled()) {
			logger.debug( "Data HasValue: " + fillerTrip );
		    }
		    
		    URI datatype = null;
		    String lang = null;
		    if (fillerTrip.language!=null) {
			lang = fillerTrip.language;
		    } else {
			try {
			    /* By default just assume these things are strings */
			    /* Allowing null values in here */
			    //datatype = xsdString;
			    //newURI("http://www.w3.org/2001/XMLSchema#string");
			    try {
				//			    datatype = newURI( (String) fillers[0] );
				datatype = newURI( fillerTrip.dataType );
			    } catch (NullPointerException ex) {
			    } catch (ClassCastException ex) {
			    }
			} catch (URISyntaxException e) {
			}
		    }
		    
		    /* Translate it */
		    OWLDataValue fillerType = 
			factory.getOWLConcreteData( datatype, 
						    lang,
						    fillerTrip.object /*fillers[1]*/ );
		    desc =
			factory.getOWLDataValueRestriction(prop, 
							   fillerType);
		}
	    }
	} catch (OWLException ex) {
	    error( ex.getMessage() );
	}
	if (desc!=null) {
	    if (logger.isDebugEnabled()) {
		logger.debug( uri + " ~> " + desc );
	    }
	    translatedDescriptions.put( uri, desc );
	    return desc;
	} else{
	    /* Last gasp attempt to recover something... */
	    OWLClass cl = getAndAssumeOWLClass( uri );
	    translatedDescriptions.put( uri, cl );
	    return cl;
	}
    } // translateRestriction

    protected OWLDescription translateCardinalityRestriction( URI uri,
							    URI propURI,
							    int type,
							    boolean obj ) throws SAXException{
	if (logger.isDebugEnabled()) {
	    logger.debug("Translating Restriction: " + uri + " " 
			 + propURI + " " + type + " " + obj );
	}
	OWLDescription desc = null;
	try {
	    if (obj) {
		OWLObjectProperty prop =
		    onto.getObjectProperty( propURI );
		if (prop==null) {
		    /* The ontology doesn't know about it, so we'll
		       create one and record the assumption */ 
		    prop = createObjectProperty(onto, propURI );
		    if (!definitelyObjectProperties.contains( propURI )) {
			assumedObjectProperties.add( propURI );
		    }
		}
		Integer val = null;
		if (type == MIN) {
		    /* Get the value. */
		    Set valTrips = getLiterals (uri.toString(), v.getMinCardinality() );
		    LiteralTriple valTrip = (LiteralTriple) getSingletonObject( valTrips );
		    usedTriples.add( valTrip );
		    if (logger.isDebugEnabled()) {
			logger.debug( "MIN: " + valTrip );
		    }
		    /* Parse the value */
		    int v = parseInt( valTrip.object, valTrip.language );
		    if ( v < 0 ) {
			owlFullConstruct( MALFORMED_RESTRICTION,
					  "Bad value for mincardinality: " + valTrip );
		    } 

		    /* Translate it */
		    desc =
			factory.getOWLObjectCardinalityAtLeastRestriction(prop, v);
		} else if (type == MAX) {
		    /* Get the value. */
		    Set valTrips = getLiterals (uri.toString(), v.getMaxCardinality() );
		    LiteralTriple valTrip = (LiteralTriple) getSingletonObject( valTrips );
		    usedTriples.add( valTrip );
		    if (logger.isDebugEnabled()) {
			logger.debug( "MAX: " + valTrip );
		    }
		    /* Parse the value */
		    int v = parseInt( valTrip.object, valTrip.language );
		    if ( v < 0 ) {
			owlFullConstruct( MALFORMED_RESTRICTION,
					  "Bad value for maxcardinality: " + valTrip );
		    } 

		    /* Translate it */
		    desc =
			factory.getOWLObjectCardinalityAtMostRestriction(prop, v);
		} else {
		    /* Get the value. */
		    Set valTrips = getLiterals (uri.toString(), v.getCardinality() );
		    LiteralTriple valTrip = (LiteralTriple) getSingletonObject( valTrips );
		    usedTriples.add( valTrip );
		    if (logger.isDebugEnabled()) {
			logger.debug( "CARD: " + valTrip );
		    }
		    /* Parse the value */
		    int v = parseInt( valTrip.object, valTrip.dataType );
		    if ( v < 0 ) {
			owlFullConstruct( MALFORMED_RESTRICTION,
					  "Bad value for cardinality: " + valTrip );
		    } 

		    /* Translate it */
		    desc =
			factory.getOWLObjectCardinalityRestriction(prop, v, v);
		}
	    } else {
		OWLDataProperty prop =
		    onto.getDataProperty( propURI );
		if (prop==null) {
		    /* The ontology doesn't know about it, so we'll
		       create one and record the assumption */ 
		    prop = createDataProperty(onto, propURI );
		    if (!definitelyDataProperties.contains( propURI )) {
			assumedDataProperties.add( propURI );
		    }
		}
		Integer val = null;
		if (type == MIN) {
		    /* Get the value. */
		    Set valTrips = getLiterals (uri.toString(), v.getMinCardinality() );
		    LiteralTriple valTrip = (LiteralTriple) getSingletonObject( valTrips );
		    usedTriples.add( valTrip );
		    if (logger.isDebugEnabled()) {
			logger.debug( "MIN: " + valTrip );
		    }
		    /* Parse the value */
		    int v = parseInt( valTrip.object, valTrip.dataType );
		    if ( v < 0 ) {
			owlFullConstruct( MALFORMED_RESTRICTION,
					  "Bad value for mincardinality: " + valTrip );
		    } 

		    /* Translate it */
		    desc =
			factory.getOWLDataCardinalityAtLeastRestriction(prop, v);
		} else if (type == MAX) {
		    /* Get the value. */
		    Set valTrips = getLiterals (uri.toString(), v.getMaxCardinality() );
		    LiteralTriple valTrip = (LiteralTriple) getSingletonObject( valTrips );
		    usedTriples.add( valTrip );
		    if (logger.isDebugEnabled()) {
			logger.debug( "MAX: " + valTrip );
		    }
		    /* Parse the value */
		    int v = parseInt( valTrip.object, valTrip.language );
		    if ( v < 0 ) {
			owlFullConstruct( MALFORMED_RESTRICTION,
					  "Bad value for maxcardinality: " + valTrip );
		    } 

		    /* Translate it */
		    desc =
			factory.getOWLDataCardinalityAtMostRestriction(prop, v);
		} else {
		    /* Get the value. */
		    Set valTrips = getLiterals (uri.toString(), v.getCardinality() );
		    LiteralTriple valTrip = (LiteralTriple) getSingletonObject( valTrips );
		    usedTriples.add( valTrip );
		    if (logger.isDebugEnabled()) {
			logger.debug( "CARD: " + valTrip );
		    }
		    /* Parse the value */
		    int v = parseInt( valTrip.object, valTrip.dataType );
		    if ( v < 0 ) {
			owlFullConstruct( MALFORMED_RESTRICTION,
					  "Bad value for cardinality: " + valTrip );
		    } 
		    
		    /* Translate it */
		    desc =
			factory.getOWLDataCardinalityRestriction(prop, v, v);
		}
	    }
	} catch (OWLException ex) {
	    error( ex.getMessage() );
	}
	if (desc!=null) {
	    if (logger.isDebugEnabled()) {
		logger.debug( uri + " ~> " + desc );
	    }
	    translatedDescriptions.put( uri, desc );
	    return desc;
	} else{
	    /* Last gasp attempt to recover something... */
	    OWLClass cl = getAndAssumeOWLClass( uri );
	    translatedDescriptions.put( uri, cl );
	    return cl;
	}
    } // translateCardinalityRestriction


    protected OWLIndividual translateIndividual( URI uri ) throws SAXException {

	/* Have we done it already? */
	OWLIndividual ind = (OWLIndividual) createdIndividuals.get( uri );
	if (ind!=null) {
	    return ind;
	}
	try {
	    /* Create it */
	    if ( isAnonymousNode( uri.toString() ) ) {
	    // ANON: use the genid rdfparser gives to create the individual
	    // ind = factory.getOWLIndividual( null );    
		ind = factory.getAnonOWLIndividual( uri );
		// ===
	    } else {
		ind = factory.getOWLIndividual( uri );
	    }
	    /* If we haven't seen a type assertion for this one, then note that. */
	    if (!definitelyTypedIndividuals.contains( uri ) ) {
		untypedIndividuals.add( uri );
	    }
	    createdIndividuals.put( uri, ind );
	    
	    /* Add to ontology */
            AddEntity ae = new AddEntity(onto, ind, null);
	    
	    applyChange( ae );
	    return ind;
	} catch (OWLException ex) {
	    error( ex.getMessage() );
	}
	return null;
    } //translateIndividual 


    protected OWLDataRange translateDataRange( URI range ) throws OWLException, SAXException {
	if (logger.isDebugEnabled()) {
	    logger.debug(" Translating Data Range: " + range );
	    /* Look in the translated collection */
	    logger.debug( "Checking translations (data)..." );
	}
	
	OWLDataRange tryThis = null;
	try { tryThis =
	    (OWLDataRange) translatedDescriptions.get( range );
	} catch (ClassCastException e) {
		logger.error( range + " is not a OWLDataRange");
	}
	if ( tryThis!=null ) {
	    if (logger.isDebugEnabled()) {
		logger.debug( "Found " + tryThis );
	    }
	    return tryThis;
	}
	if (logger.isDebugEnabled()) {
	    logger.debug( "Not Found " );
	}
	/* Check to see if it's a data type */
	Triple datatypeTriple = getTriple( range.toString(), v.getInstanceOf(), v.getDatatype() );
	if (datatypeTriple!=null) {
	    /* It's a datatype */
	    OWLDataType dt =
		factory.getOWLConcreteDataType( range );
	    return dt;
	}

	/* Check to see if it's a data range */
	Triple dataRangeTriple = getTriple( range.toString(), v.getInstanceOf(), v.getDataRange() );
	if (dataRangeTriple==null) {
	    //	if ( !owlDataRanges.contains( range ) ) {
	    /* It's not been declared as a data range or a datatype,
	     * so we've got a problem. */
	    
	    if ( !xsdDatatypes.contains( range.toString() ) &&
		 !range.equals( rdfLiteral ) &&
		 !definitelyDatatypes.contains( range ) ) {
		/* It's not an XSD one or Literal or a defined one... */
		URI culprit = range;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}
		owlFullConstruct( UNTYPED_DATATYPE,
				  "Undefined Datatype: " + range,
				  culprit );
	    }
	    /* Try and recover... */
	    OWLDataType dt =
		factory.getOWLConcreteDataType( range );
	    return dt;
	} else { // DataRange
	    usedTriples.add( dataRangeTriple );

	    /* It's an owl:DataRange, so there must be some oneofs
	     * with data values hanging around. */
	    /* First find the uri that it's a oneof of. */
	    Set oneofs = getObjects( range.toString(), v.getOneOf() );
	    //	    Set oneofs = (Set) owlOneOf.get( range );
	    if (oneofs.size()!=1) {
		owlFullConstruct( MALFORMED_DESCRIPTION,
				  "Data Range with multiple oneOfs!: " + range);
	    } else {
		/* Earlier checks guarantee that there's only one. */
		Triple listStartTriple = (Triple) getSingletonObject( oneofs );
		usedTriples.add( listStartTriple );
		URI listStart = listStartTriple.object;
		//		URI listStart = (URI) getSingletonObject( oneofs );	    
		Set vals = new HashSet();
		Set dataValues = new HashSet();
		rdfListToSet( listStart, vals );
		/* This should now be a list of pairs of strings, the
		 * first being the object, the second the language,
		 * and the third the type. */
		for (Iterator it = vals.iterator();
		     it.hasNext(); ) {
		    
		    Object[] valueInformation = 
			(Object[]) it.next();
		    URI datatype = null;
		    String lang = null;
		    if (valueInformation[1]!=null) {
			lang = (String) valueInformation[1];
		    } else {
			try {
			    /* By default just assume these things are strings */
			    /* Allow null values in here */
			    //datatype = xsdString;
			    //newURI("http://www.w3.org/2001/XMLSchema#string");
			    try {
			    datatype = newURI( (String) valueInformation[2] );
			    } catch (NullPointerException ex) {
			    } catch (ClassCastException ex) {
			    }
			} catch (URISyntaxException e) {
			}
		    }
		    
		    /* create a new datavalue */
		    OWLDataValue fillerType = 
			factory.getOWLConcreteData( datatype, 
						    lang,
						    valueInformation[0] );
		    if (logger.isDebugEnabled()) {
			logger.debug( fillerType );
		    }
		    /* Add to the set */
		    dataValues.add( fillerType );
		}
		/* Now return a datarange with the collected values. */
		OWLDataRange dr = factory.getOWLDataEnumeration( dataValues );
		translatedDescriptions.put( range, dr );
		return dr;
	    }
	    /* XXXXXXXXXXXXXXXX */
	}
	/* Panic recovery.....*/
	OWLDataType dt =
	    factory.getOWLConcreteDataType( range );
	return dt;
    }

    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#statementWithResourceValue(String, String, String)
     */

    public void statementWithResourceValue(
        String subj,
        String pred,
        String obj)
        throws SAXException {

// 	if ( subj.startsWith( "#" ) ) {
// 	    warning( "Resource: " + subj + " has no namespace. This may be due to a missing xml:base." );
// 	} 
// 	if ( pred.startsWith( "#" ) ) {
// 	    warning( "Resource: " + pred + " has no namespace. This may be due to a missing xml:base." );
// 	}
// 	if ( obj.startsWith( "#" ) ) {
// 	    warning( "Resource: " + obj + " has no namespace. This may be due to a missing xml:base." );
// 	}
	/* Short term fix to deal with dodgy resolving. 16-04-04. */
 	if ( subj.endsWith( "#" ) ) {
	    logger.debug( "HASH: " + subj );
	    subj = subj.substring( 0, subj.length()-1 );
 	} 
 	if ( pred.endsWith( "#" ) ) {
	    logger.debug( "HASH: " + subj );
	    pred = pred.substring( 0, subj.length()-1 );
 	}
 	if ( obj.endsWith( "#" ) ) {
	    logger.debug( "HASH: " + subj );
	    obj = obj.substring( 0, obj.length()-1 );
 	}
	if ( tripleLogger.isDebugEnabled() ) {
	    tripleLogger.debug("Triple [" + subj + "," + pred + "," + obj + "]");
	}
	/* ######################## */

	tripleCount++;
	if ( tripleLogger.isInfoEnabled() &&
	     (tripleCount % tripleCountChunk) == 0 ) {
	    tripleLogger.info( Integer.toString( tripleCount ) );
	    //	    tripleLogger.info("Triple [" + subj + "," + pred + "," + obj + "]");
	}
        URI subj_uri = null;
        URI obj_uri = null;
        URI pred_uri = null;
        try {
            subj_uri = newURI(subj);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
	try {
            obj_uri = newURI(obj);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
	try {
            pred_uri = newURI(pred);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }

	addTriple( subj_uri, pred_uri, obj_uri );

	/* If it's thing or nothing, whack in a "virtual" class
	 * triple. This gets round problems with the use of the
	 * builtins.  */
	if ( subj.equals( OWLVocabularyAdapter.INSTANCE.getThing() ) ||
	     subj.equals( OWLVocabularyAdapter.INSTANCE.getThing() ) ) {
	    try {
		URI instanceOf = new URI( v.getInstanceOf() );
		URI owlClass = new URI( v.getClass_() );
		addTriple( subj_uri, instanceOf, owlClass );
	    } catch ( URISyntaxException ex ) {
		error( ex.getMessage() );
		}
	}

	/* Make a note if it's a bnode. */
	if ( isAnonymousNode( subj ) ) {
	    bNodes.add( subj_uri );
	} 
	if ( isAnonymousNode( obj ) ) {
	    bNodes.add( obj_uri );
	} 

    }

    /**
     * Called when a statement with literal value is added to the model.
     *
     * @param subject               URI of the subject resource
     * @param predicate             URI of the predicate resource
     * @param object                literal object value
     * @param parseType             the parse type of the literal
     * @param language              the language
     */
    public void statementWithLiteralValue(
        String subject,
        String predicate,
        String object,
	/* These two were the wrong way round! Raphael, that's a beer
	 * you owe me..... SKB */
        String language,
        String datatype)
        throws SAXException {

	/* Short term fix to deal with dodgy resolving. 16-04-04. */
 	if ( subject.endsWith( "#" ) ) {
	    logger.debug( "HASH: " + subject );
	    subject = subject.substring( 0, subject.length()-1 );
 	} 
 	if ( predicate.endsWith( "#" ) ) {
	    logger.debug( "HASH: " + predicate );
	    predicate = predicate.substring( 0, predicate.length()-1 );
 	}
// 	if ( subject.startsWith( "#" ) ) {
// 	    warning( "Resource: " + subject + " has no namespace. This may be due to a missing xml:base." );
// 	}
// 	if ( predicate.startsWith( "#" ) ) {
// 	    warning( "Resource: " + predicate + " has no namespace. This may be due to a missing xml:base." );
// 	}

	/* Is this right?? */
	//        String datatype = language;
        Integer value = null;
	if ( tripleLogger.isDebugEnabled() ) {
	    tripleLogger.debug("[" + subject + "," + predicate + "," + object + ", lang: " + language + ", dt: " + datatype + "]");
	}
	tripleCount++;
	if ( tripleLogger.isInfoEnabled() &&
	     (tripleCount % tripleCountChunk) == 0 ) {
	    tripleLogger.info( Integer.toString( tripleCount ) );
	    //	    tripleLogger.info("[" + subject + "," + object + "," + parseType + "," + language + "]");
	}

	if ( predicate.equals( rdfNodeID ) ) {
	    warning( "rdf:nodeID being used. Things may go wrong...." );
	}

// 	if ( predicate.equals(rdfV.RDF+ "nodeID") ) {
// 	    warning( "rdf:nodeID being used. Things may go wrong...." );
// 	}

	URI subj_uri = null;
	URI pred_uri = null;
	try {
	    subj_uri = newURI(subject);
	} catch (URISyntaxException e) {
	    warning( e.getMessage() );
	}
	try {
	    pred_uri = newURI(predicate);
	} catch (URISyntaxException e) {
	    warning( e.getMessage() );
	}
	    
	addLiteralTriple( subj_uri, pred_uri, object, language, datatype );
	/* Make a note if it's a bnode. */
	if ( isAnonymousNode( subject ) ) {
	    bNodes.add( subj_uri );
	} 

    }

    protected void createPropertyInstance(
					OWLOntology onto,
					URI subj,
					URI pred,
					URI obj) throws SAXException {
        try {
	    //	    System.out.println( subj + ":" + pred + ":" + obj );
	    if (logger.isDebugEnabled()) {
		logger.debug("PI: " + subj);
	    }
	    /* Don't do this if the subject is known to be an ontology */
	    if (pred.toString().startsWith( v.OWL ) ) {
		/* Something funny has happened -- there are extra
		 * triples lying around that shouldn't be
		 * here. Probably in Full. */
		owlFullConstruct( UNUSED_TRIPLES,
				  "Unused triples using owl properties: [" + 
				  subj + "," 
				  + pred + "," + obj + 
				  ". Further messages may be misleading...." );
	    } 
	    OWLObjectProperty p = getOrCreateObjectProperty(pred);
	    OWLIndividual s = translateIndividual(subj);
	    OWLIndividual o = translateIndividual(obj);
	    AddObjectPropertyInstance ae =
		new AddObjectPropertyInstance(onto, s, p, o, null);
	    applyChange( ae );
        } catch (OWLException e) {
            error( e.getMessage() );
        }
    }

    /**
     * Method createSameIndividualAs.
     * @param onto
     * @param subj
     * @param obj
     */
    protected void createSameIndividualAs(OWLOntology onto, URI subj, URI obj) throws SAXException{
	try {
	    /* Added some more specific error handling. */
	    if ( definitelyClasses.contains( subj ) &&
		 definitelyClasses.contains( obj ) ) {
		URI culprit = subj;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}
		if ( fixSameAs ) {
		    /* Treat this as an equivalent class */
		    warning( "Treating owl:sameAs applied to Classes as owl:equivalentClass" );
		    createEquivalentClasses( onto, subj, obj );
		    return;
		}
		owlFullConstruct( SAME_AS_USED_FOR_CLASS,
				  subj + " owl:sameAs " + obj + ".\n\tShould this be owl:equivalentClass?", null);
	    } else if ( definitelyObjectProperties.contains( subj ) &&
		 definitelyObjectProperties.contains( obj ) ) {
		URI culprit = subj;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}
		owlFullConstruct( SAME_AS_USED_FOR_OBJECT_PROPERTY,
				  subj + " owl:sameAs " + obj + ".\n\tShould this be owl:equivalentProperty?", null);
		
	    } else if ( definitelyDataProperties.contains( subj ) &&
		 definitelyDataProperties.contains( obj ) ) {
		URI culprit = subj;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}
		owlFullConstruct( SAME_AS_USED_FOR_DATA_PROPERTY,
				  subj + " owl:sameAs " + obj + ".\n\tShould this be owl:equivalentProperty?", null);
		
	    }

	    OWLIndividual s = translateIndividual(subj);
	    OWLIndividual o = translateIndividual(obj);
	    HashSet set = new HashSet(2, 1);
	    set.add(s);
	    set.add(o);
	    OWLIndividualAxiom entity =
		factory.getOWLSameIndividualsAxiom(set);
	    AddIndividualAxiom ae =
		new AddIndividualAxiom(onto, entity, null);
	    applyChange( ae );
	} catch (OWLException e) {
	    error( e.getMessage() );
	}
    }

    /** We assume that inverse of must apply to object properties. */
    protected void createInverseOf(OWLOntology onto, URI subj, URI obj) throws SAXException {
	try {
	    OWLObjectProperty s = null; //onto.getObjectProperty(subj);
	    OWLObjectProperty o = null;
	    s = factory.getOWLObjectProperty(subj);
	    if (!definitelyObjectProperties.contains( subj ) ) {
		assumedObjectProperties.add( subj );
	    }
	    o = factory.getOWLObjectProperty(obj);
	    if (!definitelyObjectProperties.contains( obj ) ) {
		assumedObjectProperties.add( obj );
	    }
	    AddInverse ai = new AddInverse(onto, s, o, null);
	    applyChange( ai );
	} catch (OWLException e) {
	    error( e.getMessage() );
	}
    }

    protected void createDifferentFrom(OWLOntology onto, URI subj, URI obj) throws SAXException {
	try {
	    OWLIndividual s = translateIndividual(subj);
	    OWLIndividual o = translateIndividual(obj);

	    OWLIndividualAxiom entity;
	    HashSet set = new HashSet(2, 1);
	    set.add(s);
	    set.add(o);
	    entity = factory.getOWLDifferentIndividualsAxiom(set);
	    AddIndividualAxiom ae =
		new AddIndividualAxiom(onto, entity, null);
	    
	    applyChange( ae );
	} catch (OWLException e) {
	    error( e.getMessage() );
	}
    }

    protected void createEquivalentProperty(
        OWLOntology onto,
        URI subj,
        URI obj) throws SAXException {
	    int OK = 0; // 1 = Object, 2 = Data

	    OWLProperty o = null;
	    OWLProperty s = null;

	    if ( definitelyObjectProperties.contains( subj ) &&
		 definitelyObjectProperties.contains( obj ) ) {
		OK = 1;
		/* Two object props, so add the axiom */
		o = getOrCreateObjectProperty( obj );
		s = getOrCreateObjectProperty( subj );
	    } else if ( definitelyDataProperties.contains( subj ) &&
			definitelyDataProperties.contains( obj ) ) {
		/* Two data props, so add the axiom */
		OK = 2;
		o = getOrCreateDataProperty( obj );
		s = getOrCreateDataProperty( subj );
	    } else {
		/* Bad. */
	    }
	    if (OK > 0) {
		HashSet set = new HashSet(2, 1);
		set.add(s);
		set.add(o);
		try {
		    OWLEquivalentPropertiesAxiom axiom =
			factory.getOWLEquivalentPropertiesAxiom(set);
		    AddPropertyAxiom event =
			new AddPropertyAxiom(onto, axiom, null);
		    
		    applyChange( event );
		} catch (OWLException e) {
		    logger.warn(e);
		}
	    } else {
		URI culprit = subj;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}

		owlFullConstruct( ILLEGAL_SUBPROPERTY,
				  subj + " rdfs:subPropertyOf " + obj + ". Only allowed between two DatatypeProperties or ObjectProperties.", culprit);
		
	    }
    }

    /* Used when doing "lax" parsing */
    protected void createEquivalentClasses(OWLOntology onto,
					 URI subj,
					 URI obj) throws SAXException {
	OWLDescription d1 = null;
	OWLDescription d2 = null;

	if ( definitelyClasses.contains( subj ) &&
	     definitelyClasses.contains( obj ) ) {
	    d1 = translateDescription( subj );
	    d2 = translateDescription( obj );
	    HashSet set = new HashSet(2, 1);
	    set.add(d1);
	    set.add(d2);
	    try {
		OWLEquivalentClassesAxiom axiom =
		    factory.getOWLEquivalentClassesAxiom(set);
		AddClassAxiom ac = new AddClassAxiom(onto, axiom, null);
		applyChange( ac );
	    } catch (OWLException e) {
		logger.warn(e);
	    }
	} else {
	    error( "Problems with sameAs" );
	}
    }

    protected void createSubProperty(OWLOntology onto, URI subj, URI obj) throws SAXException {
	    int OK = 0; // 1 = Object, 2 = Data
	    OWLProperty o = null;
	    OWLProperty s = null;

	    if ( definitelyObjectProperties.contains( subj ) &&
		 definitelyObjectProperties.contains( obj ) ) {
		OK = 1;
		/* Two object props, so add the axiom */
		o = getOrCreateObjectProperty( obj );
		s = getOrCreateObjectProperty( subj );
	    } else if ( definitelyDataProperties.contains( subj ) &&
			definitelyDataProperties.contains( obj ) ) {
		/* Two data props, so add the axiom */
		OK = 2;
		o = getOrCreateDataProperty( obj );
		s = getOrCreateDataProperty( subj );
	    } else {
		/* Bad. */
	    }
	    if (OK > 0) {
		try {
		    OWLSubPropertyAxiom axiom =
			factory.getOWLSubPropertyAxiom(s, o);
		    AddPropertyAxiom event =
			new AddPropertyAxiom(onto, axiom, null);
		    //                 AddSuperProperty event =
		    //                     new AddSuperProperty(onto, s, o, null);
		    applyChange( event );
		} catch (OWLException e) {
		    logger.warn(e);
		}
	    } else {
		URI culprit = subj;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}

		owlFullConstruct( ILLEGAL_SUBPROPERTY,
				  subj + " rdfs:subPropertyOf " + obj + ". Only allowed between two DatatypeProperties or ObjectProperties.", culprit );
// 		logger.warn(
// 			    "["
// 			    + subj
// 			    + "subPropertyOf"
// 			    + obj
// 			    + "] ignored, they are not properties of the same kind (data/object) this is only possible in OWL Full");
	    }
    } 

    /**
     * Method createRange.
     * @param onto
     * @param subj
     * @param obj
     */
    protected void createRange(OWLOntology onto, 
			     URI property, 
			     URI range) throws SAXException {
        try {
	    OWLProperty p = getProperty(property);
            if (p == null) {
		/* Test was wrong here. */
		if ( definitelyDatatypes.contains( range ) ) {
//                 if (XMLSchemaSimpleDatatypeVocabulary
//                     .INSTANCE
//                     .getDatatypes()
//                     .contains(range.toString())) {
                    logger.warn(
				"Need to know type of property "
				+ property
				+ " but it is missing, assuming DataProperty !");
		    
                    p = getOrCreateDataProperty(property);
		    //p = factory.getOWLDataProperty(property);
		    //assumedDataProperties.add( property );
                } else {
                    logger.warn(
				"Need to know type of property "
				+ property
				+ " but it is missing, assuming ObjectProperty !");
		    /* Any unspecified URI that is given a range will
		     * be treated as an ObjectProperty */
                    p = getOrCreateObjectProperty(property);
		    // p = factory.getOWLObjectProperty(property);
                }
	    }
	    //            } else {

	    /* Now add the range */

	    /* Probably need to check whether the data type is an
	     * allowed one here.... */

	    /* The instanceof test here may well create problems. In
	       some cases, two properties may be created, one data,
	       one object. In that case, we'll have the object one
	       here. If there is then a data range applied, it'll all
	       break during the translation of the description. This
	       is the kind of thing that would need addressing if the
	       API is to try and support more of OWL Full, but for
	       now, we'll not worry about it too much. SKB 22/09/05. */
	    OntologyChange change = null;
	    if (p instanceof OWLDataProperty) {
		OWLDataRange dRange = translateDataRange( range );
		//                     OWLDataType _class =
		//                         factory.getOWLConcreteDataType( range );
		change =
		    new AddDataPropertyRange(
					     onto,
					     (OWLDataProperty) p,
					     /*_class*/ dRange,
					     null);
		
	    } else {
		OWLDescription desc = translateDescription( range );
		/* If the range is a bnode, mark it as used. */
		checkAndConsumeBNode( range );
		change =
		    new AddObjectPropertyRange(onto,
					       (OWLObjectProperty) p,
					       desc,
					       null);
	    }
	    applyChange( change );
	    //            }
        } catch (OWLException e) {
            error( e.getMessage() );
        }
    }

    protected void createDomain(OWLOntology onto, URI property, URI domain) throws SAXException {
	try {
	    OWLProperty p = getProperty(property);
	    if (p == null) {
		logger.warn(
			    "Need to know type of property "
			    + property
			    + " but it is missing, assuming ObjectProperty !");
		p = factory.getOWLObjectProperty(property);
		assumedObjectProperties.add( property );
	    } else {
		OWLDescription desc = translateDescription( domain );
		/* If the domain is a bnode, mark it as used. */
		checkAndConsumeBNode( domain );

		if (desc==null) {
		    /* Why has this happened?? */
		    desc = getAndAssumeOWLClass(domain);
		}
		AddDomain ad = new AddDomain(onto, p, desc, null);
		
		applyChange( ad );
	    }
        } catch (OWLException e) {
            error( e.getMessage() );
        }
    }
    
    /**
     * Method importOntology.
     * @param onto
     * @param subj
     * @param obj
     */
    protected void importOntology(OWLOntology onto, URI subj, URI obj) throws SAXException {

	/* 
	   1) create an ontology. If it's already there, goto 3)
	   
	   2) if it's not there, parse it using a new RDF parser and a
	   new consumer that knows my assumptions.
	   
	   3) add import. 
	   
	*/

	/* Ignore schema imports when told to */
	String oString = obj.toString();
	warning( "Importing: " + obj);

	String oString2 = obj.toString() + "#";
	if ( ( (oString.equals( v.OWL )) ||
	       (oString.equals( rdfV.RDF )) ||
	       (oString.equals( rdfsV.RDFS )) ||
	       (oString2.equals( v.OWL )) ||
	       (oString2.equals( rdfV.RDF )) ||
	       (oString2.equals( rdfsV.RDFS )) ) && 
	     ignoreSchemaImports ) {
	    warning( "Ignoring import of: " + obj);
	    return;
	}

	/* First check that it's an import applied to *this* ontology. */

	/* Unfortunately we can't guarantee to do this as we have no
	 * way of determining what the URI of this ontology is. So
	 * we'll just *always* import.... */

	if ( !subj.equals( ontologyLogicalURI ) ) {
	    /* We really don't know what to do here, so we'll just
	     * throw an error. */

	    /* Leaving this check out for now due to underlying RDF
	     * parser problems... 13/10/03 */

	    warning( "An imports statement:\n\t " + subj + " owl:imports " + obj + "\nhas been found that does not seem apply to the ontology being parsed.\nThis may be due to a missing owl:Ontology triple.\nThis parser isn't really able to resolve such situations and will just\nimport anyway." );
	    //owlFullConstruct( MALFORMED_IMPORT, "An imports statement:\n\t " + subj + " owl:imports " + obj + "\nhas been found that does not apply to the ontology being parsed.\nThis may be due to a missing owl:Ontology triple.\nThis parser is unable to resolve such situations." );
	} 
	
	if ( !definitelyOntologies.contains( subj) ) {
	    /* We should only ever apply imports to ontologies */
	    owlFullConstruct( MALFORMED_IMPORT, "An imports statement:\n\t " + subj + " owl:imports " + obj + "\nhas been found with a subject that is not an ontology." );
	} 
	
	progressLogger.info("Importing: " + obj.toString());

	/* Need to check that the object really is an ontology. */

//  	if (!definitelyOntologies.contains( subj ) ) {
//  	    owlFullConstruct( UNTYPED_ONTOLOGY,
//  			      "Imports applied to untyped Ontology: " + subj ); 
//  	}
	if (!definitelyOntologies.contains( obj ) ) {
	    assumedOntologies.add( obj );
	}
	
	OWLOntology importedOntology = null;

	/* Check to see if we've done it yet... */
	//	if (!parsedURIs.contains( obj.toString() ) ) {
	try {
	    /* Try and simply create an empty one. */
	    
	    /* Question: Does this add it to the openOntologies
	     * collection?? */
	    
	    importedOntology = 
		onto.getOWLConnection().createOntology(obj,
							  obj);
	    
	    /* Now parse it. */
	    
	    OWLRDFParser parser = new OWLRDFParser();

	    /* Pass on my parent parser's options to the new parser */
	    parser.setOptions( myParser.getOptions() );

	    /* Create a new consumer, that shares my assumptions */
	    
	    OWLConsumer consumer = new OWLConsumer( importedOntology ); 


	    /* Hook up the parser for callbacks. */
	    consumer.setOWLRDFParser( parser );
	    
	    /* Use the same error handling behaviour */
	    consumer.setOWLRDFErrorHandler( this.handler );
	    
	    /* The consumer should not worry about undefined stuff. */
	    consumer.setCheckDefinitions( false );
	    
	    HashSet[] assumptions = new HashSet[15];
	    assumptions[0] = this.assumedClasses;
	    assumptions[1] = this.assumedDataProperties;
	    assumptions[2] = this.assumedObjectProperties;
	    assumptions[3] = this.definitelyClasses;
	    assumptions[4] = this.definitelyObjectProperties;
	    assumptions[5] = this.definitelyDataProperties;
	    assumptions[6] = this.untypedIndividuals;
	    assumptions[7] = this.definitelyTypedIndividuals;
	    assumptions[8] = this.parsedURIs;
	    assumptions[9] = this.rdfsClasses;
	    assumptions[10] = this.rdfProperties;
	    assumptions[11] = this.definitelyAnnotationProperties;
	    assumptions[12] = this.definitelyDatatypes;
	    assumptions[13] = this.definitelyOntologies;
	    assumptions[14] = this.assumedOntologies;
	    consumer.setAssumptions( assumptions );
	    
	    /* Tell the parser to use the new consumer */
	    parser.setConsumer( consumer );
	    
	    /* Now parse the ontology */
	    try {

		logger.info( "Parsing: " + obj );
		/* Parse it */
		
		/*****************************************************/
		/*  The next section of code is only here for
		 *  performance testing purposes. Make sure this is
		 *  subsequently removed!!! */
		//		System.out.println( "Found: " + obj);
// 		if ( obj.toString().startsWith("http://www.w3.org/2002/03owlt") ) {
// 			try{
// 				String ss = "file:testing/wg"+obj.toString().substring("http://www.w3.org/2002/03owlt".length());
// 				if (!ss.endsWith(".rdf"))
// 				  ss = ss + ".rdf";
// 			obj = new URI(ss);
// 			}
// 			catch (Exception e) {
// 				System.err.println(e.getMessage());
// 			}
// 		}
		//		System.out.println( "Changed to: " + obj);
		/*****************************************************/

		parser.parseOntology(importedOntology, obj);
		logger.info( "Finished import parse: " + obj );
		for ( Iterator it = assumedClasses.iterator();
		      it.hasNext(); ) {
		    logger.info( it.next() );
		}

	    } catch (ParserException e) {
		warning("Import parsing failed: " + e.getMessage());
	    }
	    
	} catch (OWLException e) {
	    /* This will occur if the ontology has already been
	       created. */
	    
	    if (logger.isDebugEnabled()) {
		logger.debug("Ontology: " + obj.toString() + " already exists");
	    }
	    try {
		importedOntology = 
		    onto.getOWLConnection().getOntologyPhysical(obj);
	    } catch (OWLException e2) {
		error( e2.getMessage() );
		importedOntology = null;
	    }
	}
	//	logger.debug( importedOntology.toString() );
	/* SKB 11/06/03 */
	if ( importedOntology!=null ) {
	    /* Create an event for the import. */ 
	    AddImport ai = 
		new AddImport(onto, importedOntology, null); 
	    /* Apply it. */
	    try {
		applyChange( ai );
	    } catch (OWLException e) {
		error( e.getMessage() );
	    }
	}
	progressLogger.info("Done Importing: " + obj.toString());
    }
        
    protected OWLObjectProperty createObjectProperty(OWLOntology onto, URI uri) {
        OWLObjectProperty entity = null;
        try {
            createdObjectProperties.add(uri);
            entity = factory.getOWLObjectProperty(uri);
            AddEntity ae = new AddEntity(onto, entity, null);
	    
	    applyChange( ae );
        } catch (OWLException e) {
            logger.warn(e);
	}
	return entity;
    }

    protected OWLAnnotationProperty createAnnotationProperty(OWLOntology onto, URI uri) {
        OWLAnnotationProperty entity = null;
        try {
            entity = factory.getOWLAnnotationProperty(uri);
            AddEntity ae = new AddEntity(onto, entity, null);
	    
	    applyChange( ae );
        } catch (OWLException e) {
            logger.warn(e);
        }
        return entity;
    }

    protected OWLDataProperty createDataProperty(OWLOntology onto, URI uri) throws SAXException {
        OWLDataProperty entity = null;
        try {
            createdDatatypeProperties.add(uri);
            entity = factory.getOWLDataProperty(uri);
            AddEntity ae = new AddEntity(onto, entity, null);
	    applyChange( ae );
        } catch (OWLException e) {
            logger.warn(e);
	}
	return entity;
    }

    protected OWLObjectProperty getOrCreateObjectProperty(URI uri) throws SAXException {
	try {
	    OWLObjectProperty p = onto.getObjectProperty(uri);
	    if (p == null) {
		p = createObjectProperty(onto, uri);
		if (!definitelyObjectProperties.contains( uri ) ) {
		    assumedObjectProperties.add( uri );
		}
	    }
	    return p;
        } catch (OWLException e) {
            error( e.getMessage() );
        }
	return null;
    }

    protected OWLAnnotationProperty getOrCreateAnnotationProperty(URI uri) throws SAXException {
	try {
	    OWLAnnotationProperty p = onto.getAnnotationProperty(uri);
	    if (p == null) {
		p = createAnnotationProperty(onto, uri);
		if (!definitelyAnnotationProperties.contains( uri ) ) {
		    definitelyAnnotationProperties.add( uri );
		}
	    }
	    return p;
        } catch (OWLException e) {
            error( e.getMessage() );
        }
	return null;
    }

    protected OWLDataProperty getOrCreateDataProperty(URI uri) throws SAXException {
	try {
	    OWLDataProperty p = onto.getDataProperty(uri);
	    if (p == null) {
		p = createDataProperty(onto, uri);
		if (!definitelyDataProperties.contains( uri ) ) {
		    assumedDataProperties.add( uri );
		}
	    }
	    return p;
        } catch (OWLException e) {
            error( e.getMessage() );
        }
	return null;
    }

    HashSet functionalProperties = new HashSet();

    /**
     * Method createOntology.
     * @param onto
     * @param subj
     */
    protected void createOntology(OWLOntology onto, String subj) throws SAXException {
	
        logger.warn("Need OntologyMetaDataInterface");
    }

    HashSet deprecatedClasses = new HashSet();

    /**
     * Method createClass. This should only be called when you're damn
     * sure that the class really is a class.
     * @param onto
     * @param subj
     */
    protected OWLClass createClass(OWLOntology onto, URI uri) throws SAXException {
	/* Get a new class. */
	OWLClass entity = null;
	try {
	    if (logger.isDebugEnabled()) {
		logger.debug( "Creating: " + uri );
	    }
	    //entity = getAndAssumeOWLClass(uri);
	    entity = factory.getOWLClass( uri );

	    AddEntity ae = new AddEntity(onto, entity, null);
	    
	    applyChange( ae );
	} catch (OWLException e) {
	    logger.warn(e);
	}
	return entity;
    }

    /**
     * Method createDatatype.
     * @param onto
     * @param subj
     */
    protected OWLDataType createDatatype(OWLOntology onto, URI uri) throws SAXException {
	/* Get a new class */
	OWLDataType entity = null;
	try {
	    //	    logger.debug( "Creating: " + subj );
	    entity = factory.getOWLConcreteDataType( uri );
	    
	    AddDataType ae = new AddDataType(onto, entity, null);
	    
	    applyChange( ae );

	    definitelyDatatypes.add( uri );
	} catch (OWLException e) {
	    logger.warn(e);
	}
	return entity;
    }
    
    protected void createDataPropertyInstance(
					    OWLOntology onto,
					    URI subject,
					    URI predicate,
					    String datatype,
					    String language, 
					    String object) throws SAXException {
	if (logger.isDebugEnabled()) {
	    logger.debug("DI: " + subject);
	}
	try {
	    URI dt = null;
	    String lang = null;
	    if (language!=null) {
		lang = language;
	    } else {
		try {
		    /* By default just assume these things are strings */
		    /* Allow null values in here */
		    //dt = xsdString;
		    //newURI("http://www.w3.org/2001/XMLSchema#string");
		    if (datatype!=null) {
			dt = newURI( datatype );
		    }
		} catch (URISyntaxException e) {
		}
	    }
	    /* The subject has to be an individual */
	    //cachedIndividuals.add( subject );
	    
	    createDataProperty(onto, predicate);
	    /* If we don't know for sure that it's an OWL
	     * data property, record the assumption */
	    if (!definitelyDataProperties.contains( predicate ) ) {
		assumedDataProperties.add( predicate );
	    }
	    
	    /* Check whether the type given really is a datatype. */
	    if ( dt!=null && !definitelyDatatypes.contains( dt ) ) {
		if ( !xsdDatatypes.contains( dt.toString() ) ) {
		    /* It's not an XSD one... */
		    URI culprit = dt;
		    if ( isAnonymousNode( culprit.toString() ) ) {
			culprit = null;
		    }

		    owlFullConstruct( UNTYPED_DATATYPE,
				      "Undefined Datatype: " + dt,
				      culprit);
		}
	    }
	    
	    OWLDataProperty p = factory.getOWLDataProperty(predicate);
	    OWLIndividual s = translateIndividual(subject);
	    /* Not convinced this is right -- what about language stuff?? */
	    OWLDataValue o = factory.getOWLConcreteData(dt, lang, object);
	    AddDataPropertyInstance ae =
		new AddDataPropertyInstance(onto, s, p, o, null);
	    
	    applyChange( ae );
	} catch (OWLException e) {
	    error( e.getMessage() );
	}
    }

    protected void createAnnotationInstance(OWLOntology onto,
					  URI subject,
					  URI predicate,
					  Object object) throws SAXException {
	OWLObject oo = null;
	try {
	    if (definitelyOntologies.contains( subject ) ) {
		if ( subject.equals( ontologyLogicalURI ) ) {
		    /* We know it's about *this* ontology */
		    oo = onto;
		} else {
		    /* Will need fixed...*/
		    warning("Ignoring Annotation: " + subject + " - " + predicate + " - " + object );
		    return;
		}
	    }
	    if (oo==null) {
		oo = onto.getClass( subject );
	    }
	    if (oo==null) {
		oo = onto.getObjectProperty( subject );
	    };
	    if (oo==null) {
		oo = onto.getDataProperty( subject );
	    } 
	    if (oo==null) {
		oo = onto.getAnnotationProperty( subject );
	    } 
	    if (oo==null) {
		oo = onto.getIndividual( subject );
	    } 
	    if (oo==null) {
		oo = onto.getDatatype( subject );
	    } 
	    if (oo==null) {
		/* We've got no idea what it is, so we'll guess individual */
		if ( isAnonymousNode( subject.toString() ) ) {
		    oo = factory.getAnonOWLIndividual( subject );
		} else {
		    oo = factory.getOWLIndividual( subject );
		}
		/* If we haven't seen a type assertion for this one, then note that. */
		if (!definitelyTypedIndividuals.contains( subject ) ) {
		    untypedIndividuals.add( subject );
		}
		createdIndividuals.put( subject, oo );
	    }
	    /* We can do this as this is only called when the thing is
	       definitely an annotation property. */
	    OWLAnnotationProperty prop = getOrCreateAnnotationProperty( predicate );
	    if (prop == null) {
		/* This is probably because it's a built in. */
		if ( OWLVocabularyAdapter.INSTANCE.getAnnotationProperties().contains( predicate.toString() ) ) {
		    if (logger.isDebugEnabled()) {
			logger.debug( "Use of: " + predicate );
		    }
		    prop = createAnnotationProperty( onto, predicate );
		    definitelyAnnotationProperties.add( predicate );
		}
	    }
	    //System.out.println( "XX" + prop );

	    /* Now we need to check that the object of the annotation
	     * is typed. If not, we're in full... */
	    /* This is no longer necessary. SKB 18/11/03. See
	       http://www.w3.org/2002/03owlt/AnnotationProperty/Manifest003
	    */
// 	    if (object instanceof URI &&
// 		!tripleExists(object.toString(), v.getInstanceOf() ) ) {
// 		owlFullConstruct( UNTYPED_URI,
// 				  "Untyped URI reference used in annotation: " + object );
// 	    }

	    AddAnnotationInstance aai =
		new AddAnnotationInstance( onto, oo, prop, object, null );
	    applyChange( aai );
	} catch (OWLException e) {
	    error( e.getMessage() );
	}
	
    }	    
    
    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#logicalURI(String)
     */
    public void logicalURI(String arg0) throws SAXException {
    } 
    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#includeModel(String, String)
     */
    public void includeModel(String arg0, String arg1) throws SAXException {
    } 
    /**
     * @see edu.unika.aifb.rdf.api.syntax.RDFConsumer#addModelAttribte(String, String)
     */
    public void addModelAttribte(String arg0, String arg1)
        throws SAXException {
    }

    protected void applyChange( OntologyChange oc ) throws OWLException {
	oc.accept( (ChangeVisitor) onto ); 
	
// 	EvolutionStrategy strategy = 
// 	    new AddAllEntitiesStrategy();
// 	List original = new ArrayList();
// 	original.add( oc );
// 	List changes = strategy.computeChanges( original );
// 	for ( Iterator it = changes.iterator();
// 	      it.hasNext(); ) {
// 	    OntologyChange newOc = (OntologyChange) it.next();
// 	    /** Assumes that the ontology implements the change
// 	     * visitor interface, i.e. allows changes. */
// 	    //	    newOc.accept( onto.getChangeVisitor() ); 
// 	    newOc.accept( (ChangeVisitor) onto ); 
// 	}
    }

    /* Get the appropriate class object and record that we've made an
     * assumption. */
    protected OWLClass getAndAssumeOWLClass( URI uri ) throws SAXException {
	/* This shouldn't really happen, and probably indicates
	 * something wrong. */
	if (isAnonymousNode( uri.toString() ) ) {
	    owlFullConstruct( ANONYMOUS_CLASS_CREATION,
			      "Anonymous Class being Created: " + uri );
	}
	try {
	    OWLClass clazz = factory.getOWLClass( uri );
	    if (!definitelyClasses.contains( uri )) {
		//Thread.currentThread().dumpStack();
		if (logger.isDebugEnabled()) {
		    logger.debug( "Assuming Class: " + uri);
		}
		//	    logger.debug( "Is Anon: " + isAnonymousNode( uri.toString() ) );
		assumedClasses.add( uri );
	    }
	    return clazz;
	} catch (OWLException ex) {
	    error( ex.getMessage() );
	    return null;
	}
    }

    
    protected boolean isAnonymousNode( String str ) {
	/* Returns true if the given URI is that of an anonymous
	   node. Currently checks to see whether the node name is of
	   the form "<xxx>#genid<num>". This is not great, and needs instead
	   some callback from the parser that it is creating an
	   anonymous node. */ 
	/* Now calls back to the parser */
	return myParser.isAnonymousNodeURI( str );
// 	try {
// 	    URI uri = newURI( str );
// 	    return ( uri.getFragment().startsWith("genid") );
// 	} catch (Exception ex) {
// 	    return false;
// 	}
    }

    /* Assumes the map maps objects to sets, and will add the given
     * value to the corresponding set. */
    protected void addToMap( Map map, Object key, Object value ) {
	Set s = (Set) map.get( key );
	if (s==null) {
	    s = new HashSet();
	    map.put( key, s );
	}
	s.add( value );
    }

    /* Returns the first object from the set. Assumes there is one. */
    protected Object getSingletonObject(Set s) {
	Iterator it = s.iterator();
	return it.next();
    }

    /* Check that everything that's a class has been defined. really has been given
     * a type. */
    protected void checkForUntypedIndividuals() throws SAXException {
	//	logger.debug( assumedClasses.size() + " assumed classes." );
        for (Iterator iter = untypedIndividuals.iterator();
	     iter.hasNext();
	     ) {
	    URI uri = (URI) iter.next();
	    /* Non-OWL-DL */
	    URI culprit = uri;
	    if ( isAnonymousNode( culprit.toString() ) ) {
		culprit = null;
	    }

	    owlFullConstruct( UNTYPED_INDIVIDUAL,
			      "Untyped Individual: " + uri,
			      culprit );
	}
    }

    /* Check that everything that's an ontology has been defined. */
    protected void checkForUndefinedOntologies() throws SAXException {
        for (Iterator iter = assumedOntologies.iterator();
	     iter.hasNext();
	     ) {
	    URI uri = (URI) iter.next();
	    /* Non-OWL-DL */
	    /* Leaving this out for now due to underlying RDF parser
	     * problems. 13/10/03. */
	    URI culprit = uri;
	    if ( isAnonymousNode( culprit.toString() ) ) {
		culprit = null;
	    }

 	    owlFullConstruct( UNTYPED_ONTOLOGY,
 			      "Imports applied to untyped Ontology: " + uri,
			      culprit ); 
	}
    }

    /* Check that everything that's a class has been defined. really has been given
     * a type. */
    protected void checkForUndefinedClasses() throws SAXException {
	//	logger.debug( assumedClasses.size() + " assumed classes." );
        for (Iterator iter = assumedClasses.iterator();
	     iter.hasNext();
	     ) {
	    URI uri = (URI) iter.next();
	    /* Non-OWL-DL */
	    URI culprit = uri;
	    if ( isAnonymousNode( culprit.toString() ) ) {
		culprit = null;
	    }

	    owlFullConstruct( UNTYPED_CLASS,
			      "Untyped Class: " + uri,
			      culprit );
	}
    }
    
    /* Check that everything that's an object property really has been
     * given a type. */
    protected void checkForUndefinedObjectProperties() throws SAXException {
        for (Iterator iter = assumedObjectProperties.iterator();
	     iter.hasNext();
	     ) {
	    URI uri = (URI) iter.next();
	    /* Non-OWL-DL */
	    URI culprit = uri;
	    if ( isAnonymousNode( culprit.toString() ) ) {
		culprit = null;
	    }

	    owlFullConstruct( UNTYPED_PROPERTY_OBJECT,
			      "Untyped Object Property: " + uri,
			      culprit );
	}
    }
    
    /* Check that everything that's an object property really has been
     * given a type. */
    protected void checkForUndefinedDataProperties() throws SAXException {
        for (Iterator iter = assumedDataProperties.iterator();
	     iter.hasNext();
	     ) {
	    URI uri = (URI) iter.next();
	    /* Non-OWL-DL */
	    URI culprit = uri;
	    if ( isAnonymousNode( culprit.toString() ) ) {
		culprit = null;
	    }

	    owlFullConstruct( UNTYPED_PROPERTY_DATA,
			      "Untyped Data Property: " + uri,
			      culprit );
	}
    }

    /** Check that any properties declared using rdf:Property have
     * also been dealt with properly. */
    protected void checkForRDFProperties() throws SAXException {
	/* This may not work with imports. SKB */
        for (Iterator iter = rdfProperties.iterator();
	     iter.hasNext();
	     ) {
	    URI uri = (URI) iter.next();

	    //	    URI uri = (URI) iter.next();
	    if ( !definitelyObjectProperties.contains( uri ) &&
		 !definitelyDataProperties.contains( uri ) && 
		 /* May need to fix this for imports */
		 !definitelyAnnotationProperties.contains( uri ) ) {
		/* Non-OWL-DL */
		URI culprit = uri;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}

		owlFullConstruct( RDF_PROPERTY,
				  "rdf:Property used for: " + uri,
				  culprit );
	    }
	}
    }

    /** Check that anything declared as an rdfs:Class has been dealt
     * with properly. */
    protected void checkForRDFSClasses() throws SAXException {
	/* This may not work with imports. SKB */
        for (Iterator iter = rdfsClasses.iterator();
	     iter.hasNext();
	     ) {
	    URI uri = (URI) iter.next();
	    //URI uri = (URI) iter.next();
	    if ( !definitelyClasses.contains( uri ) ) {
		/* Check something here */
		URI culprit = uri;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}

		owlFullConstruct( RDF_CLASS,
				  "rdfs:Class used for: " + uri,
				  culprit );
	    }
	}
    }
    
    /** Check that any oneOfs are declared properly. */
    protected void checkForOneOfs() throws SAXException {
	/* This may not work with imports. SKB */
	Set workingTriples = null;

	workingTriples = getByPredicate( v.getOneOf() );
        for (Iterator iter = workingTriples.iterator();
	     iter.hasNext();
	     ) {
	    Triple triple = (Triple) iter.next();
 	    if ( !tripleExists( triple.subject.toString(),
 				v.getInstanceOf(),
 				v.getClass_() ) &&
		 !tripleExists( triple.subject.toString(),
				v.getInstanceOf(),
				v.getDataRange() ) &&
		 !builtInClasses.contains( triple.subject ) ) {
		/* Check something here */
		URI culprit = triple.subject;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}

		owlFullConstruct( MALFORMED_RESTRICTION,
				  "oneOf without Class or DataRange: " + triple.subject,
				  culprit);
	    }
	}
    }

    /** Check that any booleans are declared properly. */
    protected void checkBooleans() throws SAXException {
	/* This may not work with imports. SKB */
	/* Originally checked for an appropriate class triple. This is
	 * not enough -- for instance stuff involving the built in
	 * classes is valid here. So... we'll just check in the
	 * definite classes set. This is probably wrong in some cases,
	 * for example where we have an owl:Restriction rather than an
	 * owl:Class, in which case it'll have been added to the
	 * definite classes set. Whatever. */
	Set workingTriples = null;

	workingTriples = getByPredicate( v.getComplementOf() );
        for (Iterator iter = workingTriples.iterator();
	     iter.hasNext();
	     ) {
	    Triple triple = (Triple) iter.next();
	    //	    if ( !definitelyClasses.contains( triple.subject ) ) {
 	    if ( !tripleExists( triple.subject.toString(),
 				v.getInstanceOf(),
 				v.getClass_() ) &&
		 !builtInClasses.contains( triple.subject ) ) {
		/* Check something here */
		URI culprit = triple.subject;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}

		owlFullConstruct( MALFORMED_DESCRIPTION,
				  "complementOf without Class: " + triple.subject,
				  culprit );
	    }
	}
	workingTriples = getByPredicate( v.getIntersectionOf() );
        for (Iterator iter = workingTriples.iterator();
	     iter.hasNext();
	     ) {
	    Triple triple = (Triple) iter.next();
	    //	    if ( !definitelyClasses.contains( triple.subject ) ) {
 	    if ( !tripleExists( triple.subject.toString(),
 				v.getInstanceOf(),
 				v.getClass_() ) &&
		 !builtInClasses.contains( triple.subject ) ) {
		/* Check something here */
		URI culprit = triple.subject;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}

		owlFullConstruct( MALFORMED_DESCRIPTION,
				  "intersectionOf without Class: " + triple.subject,
				  culprit );
	    }
	}
	workingTriples = getByPredicate( v.getUnionOf() );
        for (Iterator iter = workingTriples.iterator();
	     iter.hasNext();
	     ) {
	    Triple triple = (Triple) iter.next();
	    //	    if ( !definitelyClasses.contains( triple.subject ) ) {
 	    if ( !tripleExists( triple.subject.toString(),
 				v.getInstanceOf(),
 				v.getClass_() ) &&
		 !builtInClasses.contains( triple.subject ) ) {
		/* Check something here */
		URI culprit = triple.subject;
		if ( isAnonymousNode( culprit.toString() ) ) {
		    culprit = null;
		}

		owlFullConstruct( MALFORMED_DESCRIPTION,
				  "unionOf without Class: " + triple.subject,
				  culprit );
	    }
	}
    }

    /* Check wellformedness of lists. Anything that's the subject of a
     * first or rest must be either the object of a rest, or the
     * object of an intersection, union or oneof. */
    protected void checkForMalformedLists() throws SAXException {
	HashSet allValidListSubjects = new HashSet();
	Set workingTriples = null;
	workingTriples = getByPredicate( RDFConstants.RDF_REST );
	for (Iterator iter = workingTriples.iterator();
	     iter.hasNext(); ) {
	    Triple triple = (Triple) iter.next();
	    allValidListSubjects.add( triple.object  );
	}
	workingTriples = getByPredicate( v.getIntersectionOf() );
	for (Iterator iter = workingTriples.iterator();
	     iter.hasNext(); ) {
	    Triple triple = (Triple) iter.next();
	    allValidListSubjects.add( triple.object  );
	}
	workingTriples = getByPredicate( v.getUnionOf() );
	for (Iterator iter = workingTriples.iterator();
	     iter.hasNext(); ) {
	    Triple triple = (Triple) iter.next();
	    allValidListSubjects.add( triple.object  );
	}
	workingTriples = getByPredicate( v.getOneOf() );
	for (Iterator iter = workingTriples.iterator();
	     iter.hasNext(); ) {
	    Triple triple = (Triple) iter.next();
	    allValidListSubjects.add( triple.object  );
	}
	workingTriples = getByPredicate( v.getDistinctMembers() );
	for (Iterator iter = workingTriples.iterator();
	     iter.hasNext(); ) {
	    Triple triple = (Triple) iter.next();
	    allValidListSubjects.add( triple.object  );
	}
					 
	workingTriples = getByPredicate( RDFConstants.RDF_FIRST );
	for (Iterator iter = workingTriples.iterator();
	     iter.hasNext(); ) {
	    Triple triple = (Triple) iter.next();
	    URI uri = triple.subject;
	    /* This uri must be the object of one of:
	       intersectionOf
	       unionOf
	       oneOf
	       rest
	    */
	    if (!allValidListSubjects.contains( uri ) ) {
		owlFullConstruct( MALFORMED_LIST,
				  "Bad list first/rest subject: " + uri +
				  "\n\tNot an intersection/union/oneof" );
	    }
	}
	workingTriples = getByPredicate( RDFConstants.RDF_REST );
	for (Iterator iter = workingTriples.iterator();
	     iter.hasNext(); ) {
	    Triple triple = (Triple) iter.next();
	    URI uri = triple.subject;
	    /* This uri must be the object of one of:
	       intersectionOf
	       unionOf
	       oneOf
	       rest
	    */
	    if (!allValidListSubjects.contains( uri ) ) {
		owlFullConstruct( MALFORMED_LIST,
				  "Bad list first/rest subject: " + uri +
				  "\n\tNot an intersection/union/oneof" );
	    }
	}
    }


    /************************** Simple Triple stuff ****************/

    /* The following variables and methods provide access to a
    somewhat, ahem, naive triple model. It is likely that this will
    need to be replaced in the future by something better, perhaps
    based on Jena. This is unlikely to scale. */

    /* Data structures that hold all the triples */

    /* All the triples. */
    HashSet triples = new HashSet();
    /* Those that have been used in translation. */
    HashSet usedTriples = new HashSet();
    HashSet literalTriples = new HashSet();;

    /* Some maps to improve access */
    HashMap triplesBySubject = new HashMap();;
    HashMap triplesByObject = new HashMap();
    HashMap triplesByPredicateSubject = new HashMap();
    HashMap triplesByPredicateObject = new HashMap();
    HashMap triplesByPredicate = new HashMap();
    HashMap literalTriplesBySubject = new HashMap();

    /* Release the triples. */
    protected void releaseTripleModel() {
	triples = null;
	usedTriples = null;
	literalTriples = null;
	triplesBySubject = null;
	triplesByObject = null;
	triplesByPredicateObject = null;
	triplesByPredicateSubject = null;
	triplesByPredicate = null;
	literalTriplesBySubject = null;
    }
    
    protected void addTriple(URI s, URI p, URI o) throws SAXException {
	if (!tripleExists( s, p, o ) ) {
	    Triple triple = new Triple(s, p, o);
	    triples.add( triple );
	    /* Add to the triples by subject */
	    Map map = (Map) triplesBySubject.get( s );
	    if (map==null) {
		map = new HashMap();
		triplesBySubject.put( s, map );
	    }
	    Set set = (Set) map.get( p );
	    if (set==null) {
		set = new HashSet();
		map.put( p, set );
	    }
	    set.add( triple );

	    /* Add to the triples by object */
	    map = (Map) triplesByObject.get( o );
	    if (map==null) {
		map = new HashMap();
		triplesByObject.put( o, map );
	    }
	    set = (Set) map.get( p );
	    if (set==null) {
		set = new HashSet();
		map.put( p, set );
	    }
	    set.add( triple );

	    /* Add to the triples by predicate/subject */
	    map = (Map) triplesByPredicateSubject.get( p );
	    if (map==null) {
		map = new HashMap();
		triplesByPredicateSubject.put( p, map );
	    }
	    set = (Set) map.get( s );
	    if (set==null) {
		set = new HashSet();
		map.put( s, set );
	    }
	    set.add( triple );

	    /* Add to the triples by predicate/object */
	    map = (Map) triplesByPredicateObject.get( p );
	    if (map==null) {
		map = new HashMap();
		triplesByPredicateObject.put( p, map );
	    }
	    set = (Set) map.get( o );
	    if (set==null) {
		set = new HashSet();
		map.put( o, set );
	    }
	    set.add( triple );

	    /* Add to the triples by predicate */
	    set = (Set) triplesByPredicate.get( p );
	    if (set==null) {
		set = new HashSet();
		triplesByPredicate.put( p, set );
	    }
	    set.add( triple );
	}
    }

    protected void addLiteralTriple(URI s, URI p, String o, String l, String dt) {
	LiteralTriple triple = new LiteralTriple(s, p, o, l, dt);
	literalTriples.add( triple );
	/* Add to the triples by subject */
	Map map = (Map) literalTriplesBySubject.get( s );
	if (map==null) {
	    map = new HashMap();
	    literalTriplesBySubject.put( s, map );
	}
	Set set = (Set) map.get( p );
	if (set==null) {
	    set = new HashSet();
	    map.put( p, set );
	}
	set.add( triple );
    }

    protected Set getSubjects(String predicate, String object) throws SAXException {
	URI predicate_uri = null;
	URI object_uri = null;
        try {
            predicate_uri = newURI(predicate);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
        try {
            object_uri = newURI(object);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
	return getSubjects( predicate_uri, object_uri );
    }
    
    protected Set getSubjects( URI predicate, URI object ) {
	Map map = (Map) triplesByPredicateObject.get( predicate );
	if (map==null) {
	    return new HashSet();
	} else {
	    Set set = (Set) map.get( object );
	    if (set==null) {
		return new HashSet();
	    } else {
		return set;
	    }
	}
    }

    protected Set getObjects(String subject, String predicate ) throws SAXException {
	URI predicate_uri = null;
	URI subject_uri = null;
        try {
            predicate_uri = newURI(predicate);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
        try {
            subject_uri = newURI(subject);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
	return getObjects( subject_uri, predicate_uri );
    }
    
    protected Set getObjects( URI subject, URI predicate ) {
	Map map = (Map) triplesByPredicateSubject.get( predicate );
	if (map==null) {
	    return new HashSet();
	} else {
	    Set set = (Set) map.get( subject );
	    if (set==null) {
		return new HashSet();
	    } else {
		return set;
	    }
	}
    }

    protected Set getLiterals(String subject, String predicate ) throws SAXException {
	URI predicate_uri = null;
	URI subject_uri = null;
        try {
            predicate_uri = newURI(predicate);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
        try {
            subject_uri = newURI(subject);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
	return getLiterals( subject_uri, predicate_uri );
    }
    
    protected Set getLiterals( URI subject, URI predicate ) {
	Map map = (Map) literalTriplesBySubject.get( subject );
	if (map==null) {
	    return new HashSet();
	} else {
	    Set set = (Set) map.get( predicate );
	    if (set==null) {
		return new HashSet();
	    } else {
		return set;
	    }
	}
    }

    protected Set getByPredicate( URI predicate ) {
	Set set = (Set) triplesByPredicate.get( predicate );
	if (set==null) {
	    return new HashSet();
	} else {
	    return set;
	}
    }

    protected Set getByPredicate( String predicate ) throws SAXException {
	URI predicate_uri = null;
        try {
            predicate_uri = newURI(predicate);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
	return getByPredicate( predicate_uri );
    }

    /* Returns true if the given URI is the object of (any) triple */
    protected boolean isObject( URI object ) throws SAXException {
	Map map = (Map) triplesByObject.get( object );
	if (map!=null) {
	    for (Iterator it = map.values().iterator();
		 it.hasNext();) {
		Set vals = (Set) it.next();
		if (!vals.isEmpty()) {
		    return true;
		}
	    }
	}
	return false;
    }

    protected boolean tripleExists(String subject, String predicate) throws SAXException {
	return (!getObjects(subject, predicate).isEmpty() ||
		!getLiterals(subject, predicate).isEmpty() );
    }

    protected boolean tripleExists(URI subject, URI predicate) throws SAXException {
	return (!getObjects(subject, predicate).isEmpty() ||
		!getLiterals(subject, predicate).isEmpty() );
    }

    protected boolean tripleExists(URI subject, URI predicate, URI object) throws SAXException {
	return getTriple(subject, predicate, object) != null;
    }
    
    protected boolean tripleExists(String subject, String predicate, String object) throws SAXException {
	return getTriple(subject, predicate, object) != null;
    }

    protected Triple getTriple(URI subject, URI predicate, URI object) throws SAXException {
	Map map = (Map) triplesBySubject.get( subject );
	if (map==null) {
	    /* No triples with this subject. */
	    return null;
	} else {
	    Set set = (Set) map.get( predicate );
	    if (set==null) {
		/* No triples with given subject and predicate */
		return null;
	    } else {
		for (Iterator it = set.iterator();
		     it.hasNext(); ) {
		    Triple t = (Triple) it.next();
		    if ( object.equals( t.object ) ) {
			return t;
		    }
		}
		/* Didn't find it. */
		return null;
	    }
	}	
    }
    
    protected Triple getTriple(String subject, String predicate, String object) throws SAXException {
	URI subject_uri = null;
	URI predicate_uri = null;
	URI object_uri = null;
        try {
            subject_uri = newURI(subject);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
        try {
            predicate_uri = newURI(predicate);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
        try {
            object_uri = newURI(object);
        } catch (URISyntaxException e) {
	    warning( e.getMessage() );
        }
	return getTriple( subject_uri, predicate_uri, object_uri );
    }




    /* A very simple data structure representing a triple. */

    protected class Triple {
	Triple(URI s, URI p, URI o) {
	    subject = s;
	    predicate = p;
	    object = o;
	}
	
	URI subject;
	URI predicate;
	URI object;
	public String toString() {
	    return "[" + subject + "," + predicate + "," + object + "]";
	}
    }

    /* A very simple data structure representing a literal triple. */

    protected class LiteralTriple {
	LiteralTriple(URI s, URI p, String o, String l, String dt) {
	    subject = s;
	    predicate = p;
	    object = o;
	    language = l;
	    dataType = dt;
	}
	
	URI subject;
	URI predicate;
	String object;
	String language;
	String dataType;
	public String toString() {
	    return "[" + subject + "," + predicate + "," + object + "," + language + "," + dataType + "]";
	}
    }

    protected String dumpTriples() {
	StringWriter sw = new StringWriter();
	PrintWriter pw = new PrintWriter( sw );
	pw.println();
	pw.println("=======================================================");
	for (Iterator it = triples.iterator();
	     it.hasNext(); ) {
	    pw.println( it.next() );
	}
	for (Iterator it = literalTriples.iterator();
	     it.hasNext(); ) {
	    pw.println( it.next() );
	}
	pw.println("=======================================================");
	return sw.toString();
    }
    
    /***************** End of crappy triple stuff ********************/

    /** Returns true if it's the uri of one of a number of language
	things */
    protected boolean languageMachineryType( URI uri ) {
	String str = uri.toString();
	return ( str.equals( v.getClass_() ) ||
		 str.equals( RDFConstants.RDFS_CLASS ) ||
		 str.equals( rdfsV.getProperty() ) ||
		 str.equals( rdfsV.getDatatype() ) ||
		 str.equals( v.getAllDifferent() ) ||
		 str.equals( v.getRestriction() ) ||
		 str.equals( v.getObjectProperty() ) ||
		 str.equals( v.getAnnotationProperty() ) ||
		 str.equals( RDFConstants.RDF_LIST) ||
		 str.equals( v.getDatatypeProperty() ) ||
		 str.equals( v.getDataRange() ) ||
		 str.equals( v.getTransitive() ) ||
		 str.equals( v.getSymmetricProperty() ) ||
		 str.equals( v.getFunctionalProperty() ) ||
		 str.equals( v.getInverseFunctionalProperty() ) ||
		 str.equals( v.getOntology() ) ||
		 str.equals( v.getDeprecatedClass() ) ||
		 str.equals( v.getDeprecatedProperty() ) );
    }

    protected int parseInt( String str, String type ) throws SAXException {
	/* Simply ignoring the type for now. */
	try {
	    BigInteger bigValue = new BigInteger(str.trim());
	    if ( bigValue.compareTo( MAXCARDASBIGINTEGER ) > 0 ) {
		/* If it's too big, just take MAXINT */
		warning( "Cardinality of " + bigValue + 
			 " is too big. Using " + Integer.MAX_VALUE + 
			 " instead!" );
		return Integer.MAX_VALUE;
	    } else {
		return bigValue.intValue();
	    }
	} catch (NumberFormatException ex) {
	}
	return -1;
    }

    /** Check to see if the given node is a bnode, and if so, whether
     * it's been used before. If so, we're in Full. If not, mark as
     * used and carry on. */
    protected void checkAndConsumeBNode( URI bnode ) throws SAXException {
	if ( isAnonymousNode( bnode.toString() ) ) {
	    if ( usedBNodes.contains( bnode ) ) {
		owlFullConstruct( STRUCTURE_SHARING,
				  "Structure Sharing of: " + bnode );
	    }
	    usedBNodes.add( bnode );
	}
    }

    /* Simple cache of strings to URIs in an attempt to reduce the
     * memory overhead. */
    protected URI newURI( String u ) throws URISyntaxException {
	if ( stringsToURIs.containsKey( u ) ) {
	    return (URI) stringsToURIs.get( u );
	} else {
	    URI uri = new URI( u );
	    stringsToURIs.put( u, uri );
	    return uri;
	}
    }

    /** This function provides a hook for parser extensions. It gets
     * called after the ontology has been parsed, but before the
     * "extra" stuff that hasn't been used is handled. This will allow
     * us to extend the RDF parser and deal with extensions to OWL,
     * such as rules. */ 
    protected void additionalParsingHook() throws SAXException {
        logger.info("Doing Extra Stuff");
    }
    
    /**
     * Aditya: Added this feature to the OWLConsumer to toggle the
     * preference of loading/parsing imported ontologies as well
     * @param importing
     */
    protected void setImporting(boolean importing) {
    	this.importing = importing;
    }
}

/*
 * ChangeLog
 * $Log: OWLConsumer.java,v $
 * Revision 1.21  2006/03/28 16:14:46  ronwalf
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
 * Revision 1.20  2005/09/22 09:59:46  sean_bechhofer
 * Fixing bug in parser relating to assertions of inverseFunctionality
 * of DatatypeProperties. This still isn't *quite* right, but addresses
 * a problem with the validator/parser crashing.
 *
 * Revision 1.19  2005/06/10 12:20:33  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 *
 */
