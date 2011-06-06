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
 * Revision           $Revision: 1.9 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/05/14 10:37:11 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.tptp; 


/**
 * Produces TPTP from OWL ontologies.
 *
 *
 * Created: Fri Feb 21 09:12:03 2003
 *
 * @author Sean Bechhofer
 * @version $Id: Renderer.java,v 1.9 2004/05/14 10:37:11 sean_bechhofer Exp $
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
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLNamedObject;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLProperty;
import java.net.URLEncoder;
import org.semanticweb.owl.model.OWLDataRange;

public class Renderer implements org.semanticweb.owl.io.Renderer,
				 org.semanticweb.owl.io.ShortFormProvider {

    /* NOTE: This renderer isn't very careful about where assertions
     * come from, so *all* information about classes, properties
     * etc. is rendered. Thus it could all go pear-shaped if there are
     * multiple ontologies lurking around...*/

    /* There are some brutal hacks in here to try and deal with data
     * types. Note that if the ontology uses classes or properties
     * with names STRING_n or INTEGER_n, this __will__ break....*/ 
    
    private PrintWriter pw;
    private Set allURIs;
    private List shortNames;
    private Map known;
    private int reservedNames;
    private String[] names = {"", "a_", "b_", "c_", "d_", "e_", "f_", "g_", "h_", "i_", 
		      "j_", "k_", "l_", "m_", "n_", "o_", "p_", "q_", "r_", 
		      "s_", "t_", "u_", "v_", "w_", "x_", "y_", "z_", };
    
    
    private Random random;
    private SimpleDateFormat sdf;
    private java.util.TimeZone gmt;

    private int axiomCount = 0; 

    private Map anonymousIndividuals;

    private List axioms; 

    private boolean singleAxiom = false;

    private boolean negate = false;

    public static int STRING = 0;
    public static int INTEGER = 1;
    public static int OTHER = 2;
    public static int TYPES = 3;

    private List stringDataValues;
    private List integerDataValues;
    private List otherDataValues;
    private List unsupportedDataTypes;

    public String[] typeNames = { "xsd_string", "xsd_integer", "unsupported_type" };
    
    public Renderer()
    {
// 	stringDataValues = new ArrayList();
// 	integerDataValues = new ArrayList();
// 	otherDataValues = new ArrayList();
// 	unsupportedDataTypes = new ArrayList();
    }

    public void setOptions( Map map ) {
    }

    public Map getOptions() {
	return null;
    }

    public void setNegate( boolean b ) {
	negate = b;
    }

    private void addAxiom( String axiom, String comment ) {
	String[] strs = new String[2];
	strs[0] = axiom;
	strs[1] = comment;
	axioms.add( strs );
    }

    public void renderOntology( OWLOntology ontology,
				Writer writer ) throws RendererException {
	renderOntology( ontology, writer, new ArrayList(), new ArrayList(), new ArrayList(),
			new ArrayList(), new ArrayList() );
    }
    
    public void renderOntology( OWLOntology ontology,
				Writer writer,
				List names,
				List strings,
				List integers,
				List others,
				List types) throws RendererException {
	try {

	    this.pw = new PrintWriter( writer );
	    this.allURIs = OntologyHelper.allURIs( ontology );

// 	    pw.println( "%-----------------------------------------------");
// 	    pw.println( "%--- TPTP Translation of OWL Ontology ----------");
// 	    pw.println( "%--- This is a translation of an ontology    ---");
// 	    pw.println( "%--- into a format suitable for FO theorem   ---");
// 	    pw.println( "%--- provers. It employs some very simple    ---");
// 	    pw.println( "%--- rules in order to ensure that names are ---");
// 	    pw.println( "%--- of a suitable format. If the concept    ---");
// 	    pw.println( "%--- URIs in the ontology have non-alpha-    ---");
// 	    pw.println( "%--- numeric characters in them, it may well ---");
// 	    pw.println( "%--- break. No guarantees of quality or      ---");
// 	    pw.println( "%--- accuracy....                            ---");
// 	    pw.println( "%--- Comments to: seanb@cs.man.ac.uk         ---");
	    
 	    axiomCount = 0;
	    axioms = new ArrayList();
	    shortNames = names;

	    /* Needs to know about types and values. */
	    this.stringDataValues = strings;
	    this.integerDataValues = integers;
	    this.otherDataValues = others;
	    this.unsupportedDataTypes = types;

	    known = new HashMap();
	    known.put( OWLVocabularyAdapter.INSTANCE.OWL, "owl" );
	    known.put( RDFVocabularyAdapter.INSTANCE.RDF, "rdf" );
	    known.put( RDFSVocabularyAdapter.INSTANCE.RDFS, "rdfs" );
	    known.put( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.XS, "xsd" );

	    // 	    generateShortNames();
	    sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	    gmt = java.util.TimeZone.getTimeZone("GMT");
	    sdf.setTimeZone(gmt);

	    random = new Random();
	    anonymousIndividuals = new HashMap();
	    

	    /* This is a bit of a hack... */

	    /* Cleaned up slightly in order to introduce distinct data
	     * and abstract domains. We introduce a number of axioms:
	     *
	     * o everything is in the abstract or the data domain
	     * o abstract and data domains are disjoint
	     * o everything in the abstract domain is thing and not nothing
	     * o everything in thing is abstract
	     * o everything in nothing is abstract
	     * 
	     * For any class C, C(X) => Abstract(X)
	     * In addition, for any class C with an equivalence, 
	     * C(X) <=> Abstract(X) and Definition(X)
	     * For any datatype D, D(X) => Data(X)
	     * 
	     * For any object property P,
	     * P(X,Y) => Abstract(X) and Abstract(Y)
	     *
	     * For any data property R,
	     * R(X,Y) => Abstract(X) and Data(Y)

	     * We also have to be careful about translating negation,
	     * as this has to be w.r.t. the abstract domain. So for
	     * any uses of complement, we add that the things have to
	     * be abstract.
	     */

	    addAxiom( "![X]: (abstractDomain(X) | dataDomain(X))", "Everything is in the Abstract or Data domain" );

	    addAxiom( "?[X]: (abstractDomain(X))", "The abstract domain must be non-empty." );

	    addAxiom( "?[X]: (dataDomain(X))", "The data domain must be non-empty (we know that 1 exists)" );
	     
	    addAxiom( "![X]: (~(abstractDomain(X) & dataDomain(X)))", "The Abstract and Data domains are distinct" );
	    
	    addAxiom( "![X]: (iowlThing(X) => abstractDomain(X))", "Everything in Thing is Abstract" );

	    addAxiom( "![X]: (iowlNothing(X) => abstractDomain(X))", "Anything in Nothing is abstract" );

	    addAxiom( "![X]: (abstractDomain(X) => iowlThing(X))", "All Abstract things are in Thing" );

	    addAxiom( "![X]: (~iowlNothing(X))", "Nothing is empty" );

	    addAxiom( "![X]: (" + typeNames[STRING] + "(X) => dataDomain(X))", "Strings are Data objects " );
	    addAxiom( "![X]: (" + typeNames[INTEGER] + "(X) => dataDomain(X))", "Integers are Data objects " );
	    addAxiom( "![X]: (dataDomain(X) => ~(" + typeNames[STRING] + "(X) & " + typeNames[INTEGER] + "(X)))", "String and Integer are disjoint " );

	    /* Need to pull in everything from imported
	     * ontologies. All rather nasty, and should be dealt with
	     * in a better manner. */

	    Set allOntologies = OntologyHelper.importClosure( ontology );

// 	    ontology.getIncludedOntologies();
// 	    allOntologies.add( ontology );
	    
	    for ( Iterator allIt = allOntologies.iterator();
		  allIt.hasNext(); ) {
		OWLOntology ontologyToProcess = (OWLOntology) allIt.next();
		
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getClasses() ).iterator();
		      it.hasNext(); ) {
		    renderClass( ontologyToProcess, ( OWLClass ) it.next() );
		}
		
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getObjectProperties() ).iterator();
		      it.hasNext(); ) {
		    renderObjectProperty( ontologyToProcess, ( OWLObjectProperty ) it.next() );
		}
		
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getDataProperties() ).iterator();
		      it.hasNext(); ) {
		    renderDataProperty( ontologyToProcess, ( OWLDataProperty ) it.next() );
		}
		
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getIndividuals() ).iterator();
		      it.hasNext(); ) {
		    renderIndividual( ontologyToProcess, ( OWLIndividual ) it.next() );
		}
		
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getDatatypes() ).iterator();
		      it.hasNext(); ) {
		    renderDataType( ontologyToProcess, ( OWLDataType ) it.next() );
		}
		
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getClassAxioms() ).iterator();
		      it.hasNext(); ) {
		    renderClassAxiom( ( OWLClassAxiom ) it.next() );
		} 
		
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getPropertyAxioms() ).iterator();
		      it.hasNext(); ) {
		    renderPropertyAxiom( ( OWLPropertyAxiom ) it.next() );
		} 
		
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getIndividualAxioms() ).iterator();
		      it.hasNext(); ) {
		    renderIndividualAxiom( ( OWLIndividualAxiom ) it.next() );
		} 
		
	    }

	    // 	    writeShortNames();

	    writeUnsupportedDataTypes();

	    /* Now add axioms that assert the disjointness of all the
	     * string data values */
	    for (int outer=0; outer<stringDataValues.size(); outer++) {
		for (int inner=outer+1; inner<stringDataValues.size(); inner++) {
		    addAxiom( "~equal(" + typeNames[STRING] + "_" + outer + 
			      "," +
			      typeNames[STRING] + "_" + inner + ")", 
			      "String value disjoint" );
		}
	    }

	    /* Now add axioms that assert the disjointness of all the
	     * integer data values */
	    for (int outer=0; outer<integerDataValues.size(); outer++) {
		for (int inner=outer+1; inner<integerDataValues.size(); inner++) {
		    addAxiom( "~equal(" + typeNames[INTEGER] + "_" + integerDataValues.get( outer ) + 
			      "," +
			      typeNames[INTEGER] + "_" + integerDataValues.get( inner ) + ")", 
			      "Integer value disjoint" );
		}
	    }
	    
	/* Now print out all the axioms. */
	    if (negate) {
		    /* Just print out a single big axiom which is the
		     * conjunction of everything and negate it.*/
		    pw.println( "input_formula(the_axiom,axiom,(");
		    pw.println( "~(" );
		    for (Iterator it = axioms.iterator(); it.hasNext() ; ) {
			String[] axiomAndComment = (String[]) it.next() ;
			if (axiomAndComment[1]!=null) {
			    pw.println( "%-- " + axiomAndComment[1] );
			}
			pw.println( "( " + axiomAndComment[0] + ")" );
			if (it.hasNext()) { 
			    pw.println(" &");
			}
		    }		
		    pw.println( ")" );
		    pw.println( "))." );    
	    } else {
		if (singleAxiom) {
		    /* Just print out a single big axiom which is the conjunction of everything */
		    pw.println( "input_formula(the_axiom,axiom,(");
		    for (Iterator it = axioms.iterator(); it.hasNext() ; ) {
			String[] axiomAndComment = (String[]) it.next() ;
			if (axiomAndComment[1]!=null) {
			    pw.println( "%-- " + axiomAndComment[1] );
			}
			pw.println( axiomAndComment[0] );
			if (it.hasNext()) { 
			    pw.println(" &");
			}
		    }		
		    pw.println( "))." );    
		} else {
		    for (Iterator it = axioms.iterator(); it.hasNext() ; ) {
			String[] axiomAndComment = (String[]) it.next() ;
			if (axiomAndComment[1]!=null) {
			    pw.println( "%-- " + axiomAndComment[1] );
			}
			pw.println( "input_formula(axiom_" + (axiomCount++) + ",axiom,(");
			pw.println( " " + axiomAndComment[0] );
			pw.println( "))." );    
		    }
		}
	    }
	    
	} catch (Renderer.BigCardinalityException ex) {
	    /* Rethrow it. */
	    throw ex;
	} catch (OWLException ex) {
	    throw new RendererException( ex.getMessage() );
	}
    } // renderOntology 

    private void renderClass( OWLOntology ontology,
			      OWLClass clazz ) throws OWLException {
	/* No need to state any introductions. */
	/* Need to state that classes are subsets of the abstract
	 * domain. */

	addAxiom( "![X]: (" + shortForm( clazz ) + "(X) => abstractDomain(X))", "Class " + shortForm( clazz) + " is subset of Abstract domain");
	
	/* Simply takes all information known about the class */
	
	for ( Iterator it = clazz.getEquivalentClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    equality( clazz, eq, "Equality " + shortForm( clazz ) );
	}
	
	for ( Iterator it = clazz.getSuperClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    implication( clazz, eq, "Super " + shortForm( clazz ) );
	}

	for ( Iterator it = clazz.getEnumerations( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    equality( clazz, eq, "Enumeration " + shortForm( clazz ) );
	}
    }

    private void renderIndividual( OWLOntology ontology,
				   OWLIndividual ind ) throws OWLException {
	/* No need to do any definition or introduction */

	if (ind.getTypes( ontology ).isEmpty() && 
	    ind.getObjectPropertyValues( ontology ).keySet().isEmpty() &&
	    ind.getDataPropertyValues( ontology ).keySet().isEmpty()) {
	    addAxiom( "abstractDomain(" + shortForm( ind ) + ")",
		      shortForm( ind ) );
	} else {
	    for ( Iterator it = ind.getTypes( ontology ).iterator();
		  it.hasNext(); ) {
		RenderingVisitor rv1 = new RenderingVisitor( this, shortForm( ind ), new HashSet() );
		OWLDescription eq = (OWLDescription) it.next();
		eq.accept( rv1 );
		addAxiom( rv1.result() , shortForm( ind ) );
	    }
	    
	    Map propertyValues = ind.getObjectPropertyValues( ontology );
	    for ( Iterator it = propertyValues.keySet().iterator();
		  it.hasNext(); ) {
		OWLObjectProperty prop = (OWLObjectProperty) it.next();
		Set vals = (Set) propertyValues.get(prop);
		for (Iterator valIt = vals.iterator(); valIt.hasNext();
		     ) {
 		    OWLIndividual oi = (OWLIndividual) valIt.next();
		    addAxiom( shortForm( prop ) + "(" + shortForm( ind ) + "," + shortForm( oi ) + ")", 
			      shortForm( ind ) + ":" + shortForm( prop ) + ":" + shortForm( oi ) );
		}
	    }

	    Map dataValues = ind.getDataPropertyValues( ontology );
	    for ( Iterator it = dataValues.keySet().iterator();
		  it.hasNext(); ) {
		OWLDataProperty prop = (OWLDataProperty) it.next();
		Set vals = (Set) dataValues.get(prop);
		for (Iterator valIt = vals.iterator(); valIt.hasNext();
		     ) {
 		    OWLDataValue dv = (OWLDataValue) valIt.next();
		    addAxiom( shortForm( prop ) + "(" + shortForm( ind ) + "," + canonicalize( dv ) + ")", 			      
			      shortForm( ind ) + ":" + shortForm( prop ) + ":" + canonicalize( dv ) );
		}
	    }

	}
	//	pw.println();
    }

    private void renderObjectProperty( OWLOntology ontology,
				       OWLObjectProperty prop ) throws OWLException {

	/* Need to say that the property applies to the abstract domain */
	
	StringWriter sw = new StringWriter();
	
	sw.write( "![X,Y]: " );
	sw.write( "(" + shortForm( prop ) + "(X,Y) => (abstractDomain(X) & abstractDomain(Y)))");
	addAxiom( sw.toString(), "ObjectProperty: " + shortForm( prop )  );

	if (!prop.getSuperProperties( ontology ).isEmpty()) {
	    for ( Iterator it = prop.getSuperProperties( ontology ).iterator();
		  it.hasNext(); ) {
		OWLObjectProperty sup = (OWLObjectProperty) it.next();
		addAxiom( "![X,Y]: (" + shortForm( prop ) + "(X,Y) => " + shortForm( sup ) + "(X,Y)) ", 
			  "Super: " + shortForm( prop ) );
	    }	    
	}

	if ( prop.isFunctional( ontology ) ) {
	    //	    pw.println( "%-- Functional " + shortForm( prop ) );
	    addAxiom( "![X,Y,Z]: (" + shortForm( prop ) + "(X,Y) & " + shortForm( prop ) + "(X,Z) => equal(Y,Z))",
		      "Functional: " + shortForm( prop ) );
	}

	if ( prop.isInverseFunctional( ontology ) ) {
	    //	    pw.println( "%-- InverseFunctional " + shortForm( prop ) );
	    addAxiom( "![X,Y,Z]: (" + shortForm( prop ) + "(Y,X) & " + shortForm( prop ) + "(Z,X) => equal(Y,Z))",
		      "InverseFunctional: " + shortForm( prop ) );
	}

	if ( prop.isTransitive( ontology ) ) { 
	    //	    pw.println( "%-- Transitive " + shortForm( prop ) );
	    addAxiom( "![X,Y,Z]: (" + shortForm( prop ) + "(X,Y) & " + 
			shortForm( prop ) + "(Y,Z) => " + 
			shortForm( prop ) + "(X,Z))",
		      "Transitive: " + shortForm( prop ) );
	}

	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    //	    pw.println( "%-- Domain " + shortForm( prop ) );
	    sw = new StringWriter();
	    
	    sw.write( "![X,Y]: " );
	    sw.write( "(" + shortForm( prop ) + "(X,Y) => ");

	    OWLDescription dom = (OWLDescription) it.next();

	    RenderingVisitor rv = new RenderingVisitor( this, "X", new HashSet());
	    dom.accept( rv );
	    sw.write( rv.result() + ")" );
	    addAxiom( sw.toString(), "Domain: " + shortForm( prop )  );
	}


	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    //	    pw.println( "%-- Range " + shortForm( prop ) );
	    sw = new StringWriter();

	    sw.write( "![X,Y]: (" + shortForm( prop ) + "(X,Y) => ");

	    OWLDescription ran = (OWLDescription) it.next();

	    Set s = new HashSet();
	    s.add( "X" );
	    RenderingVisitor rv = new RenderingVisitor( this, "Y", s);
	    ran.accept( rv );
	    sw.write( rv.result() + ")" );
	    addAxiom( sw.toString(), "Range: " + shortForm( prop )  );
	}

	if ( prop.isSymmetric( ontology ) ) {
// 	    pw.println( "%-- Symmetry " + shortForm( prop ) );
	    addAxiom( "![X,Y]: (" + shortForm( prop ) + "(X,Y) => " + shortForm( prop ) + "(Y,X))",
		      "Symmetric: " + shortForm( prop ) );
	}

	for ( Iterator it = prop.getInverses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLObjectProperty inv  = (OWLObjectProperty) it.next();
	    addAxiom("![X,Y]: (" + shortForm( prop ) + "(X,Y) <=> " + shortForm( inv ) + "(Y,X))",
		     "Inverse: " + shortForm( prop ) );
	}	    

    }
    
    private void renderDataProperty( OWLOntology ontology,
				     OWLDataProperty prop ) throws OWLException {

	/* Need to say that the property applies to the abstract
	 * domain and has range data domain  */

	StringWriter sw = new StringWriter();
	
	sw.write( "![X,Y]: " );
	sw.write( "(" + shortForm( prop ) + "(X,Y) => (abstractDomain(X) & dataDomain(Y)))");
	addAxiom( sw.toString(), "DataProperty: " + shortForm( prop )  );

	if (!prop.getSuperProperties( ontology ).isEmpty()) {
	    for ( Iterator it = prop.getSuperProperties( ontology  ).iterator();
		  it.hasNext(); ) {
		
		OWLObjectProperty sup = (OWLObjectProperty) it.next();
		addAxiom( "![X,Y]: (" + shortForm( prop ) + "(X,Y) => " + shortForm( sup ) + "(X,Y))",
			  "Super: " + shortForm( prop ) );
	    }	    
	}

	if ( prop.isFunctional( ontology ) ) {
	    //	    pw.println( "%-- Functional " + shortForm( prop ) );
	    addAxiom( "![X,Y,Z]: (" + shortForm( prop ) + "(X,Y) & " + shortForm( prop ) + "(X,Z) => equal(Y,Z))",
		      "Functional: " + shortForm( prop ) );
	}

	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    sw = new StringWriter();
	    
	    sw.write( "![X,Y]: (" + shortForm( prop ) + "(X,Y) => ");
	    
	    OWLDescription dom = (OWLDescription) it.next();

	    RenderingVisitor rv = new RenderingVisitor( this, "X", new HashSet());
	    dom.accept( rv );
	    sw.write( rv.result() + ")" );
	    addAxiom( sw.toString(), "Domain: " + shortForm( prop )  );
	}


	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    //	    pw.println( "%-- Range " + shortForm( prop ) );
	    sw = new StringWriter();
	    
	    sw.write( "![X,Y]: (" + shortForm( prop ) + "(X,Y) => ");
	    
	    OWLDataRange ran = (OWLDataRange) it.next();
	    
	    Set s = new HashSet();
	    s.add( "X" );
	    RenderingVisitor rv = new RenderingVisitor( this, "Y", s);
	    ran.accept( rv );
	    sw.write( rv.result() + ")" );
	    addAxiom( sw.toString(), "Range: " + shortForm( prop )  );
	}

// 	for ( Iterator it = prop.getRanges( ontology ).iterator();
// 	      it.hasNext(); ) {
// 	    throw new OWLException( "TPTP Renderer does not handle data ranges" );
// 	}
    }

    private void renderDataType( OWLOntology ontology,
				 OWLDataType datatype ) throws OWLException {
	/* Don't have to do anything here. It makes no difference to
	 * the semantic translation. */
	/* Yes we do. We need to say that this is a subset of the data
	 * domain. */
	addAxiom( "![X]: (" + typeFor( datatype ) + "(X) => dataDomain(X))",
		  typeFor( datatype) + " is a datatype" );
    }
    
    private void renderClassAxiom( OWLClassAxiom axiom ) throws OWLException {
	AxiomRenderer ar = new AxiomRenderer( this );
 	axiom.accept( ar );
	String str = ar.result();
	if (!str.equals("")) {
	    addAxiom( ar.result(), null );
	}
    }

    private void renderPropertyAxiom( OWLPropertyAxiom axiom ) throws OWLException {
	AxiomRenderer ar = new AxiomRenderer( this );
	axiom.accept( ar );
	String str = ar.result();
	if (!str.equals("")) {
	    addAxiom( ar.result(), null );
	}
    }

    private void renderIndividualAxiom( OWLIndividualAxiom axiom ) throws OWLException {
	AxiomRenderer ar = new AxiomRenderer( this );
 	axiom.accept( ar );
	addAxiom( ar.result(), null );
    }

    private void implication( OWLDescription desc1,
			      OWLDescription desc2,
			      String comment ) throws OWLException {
	/* Add an axiom stating that desc1 => desc2 */

	RenderingVisitor rv1 = new RenderingVisitor( this, "X", new HashSet() );
	desc1.accept( rv1 );
	RenderingVisitor rv2 = new RenderingVisitor( this, "X", new HashSet() );
	desc2.accept( rv2 );
	addAxiom( "![X]: (" + rv1.result() + " => " + rv2.result() + ")", comment );
    }

    private void equality( OWLDescription desc1,
			   OWLDescription desc2,
			   String comment ) throws OWLException {
	/* Add an axiom stating that desc1 <=> desc2 */
	/* Need to extend to change:

	desc1 <=> Abstract and desc2

	Note that this is only ever called with classes on the left
	hand side, so we don't need to include the Abstract there.

	No, don't need this as the translation will guarantee everything. 
	*/
	
	RenderingVisitor rv1 = new RenderingVisitor( this, "X", new HashSet() );
	desc1.accept( rv1 );
	RenderingVisitor rv2 = new RenderingVisitor( this, "X", new HashSet() );
	desc2.accept( rv2 );
	//	addAxiom( "![X]: (" + rv1.result() + " <=> (abstractDomain(X) & " + rv2.result() + "))", comment);
	addAxiom( "![X]: (" + rv1.result() + " <=> " + rv2.result() + ")", comment);
    }

    private void generateShortNames() {
	/* Generates a list of namespaces. */
	shortNames = new ArrayList();

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
	pw.println("% ============= Namespaces ============= ");
	for ( Iterator it=known.keySet().iterator();
	      it.hasNext(); ) {
	    String ns = (String) it.next();
	    String shrt = (String) known.get( ns );
	    pw.println("%  " + shrt + "\t= " + ns);
	}
	for ( int i=0; i<shortNames.size(); i++ ) {
	    if (i < names.length) {
		String ns = (String) shortNames.get( i );
		pw.println("%  " + names[i] + "\t= " + ns);
	    }
	}
	pw.println("% ====================================== ");
    }

    /* Write out any unsupported types occurring in the kbs. */
    private void writeUnsupportedDataTypes() {
	if ( !unsupportedDataTypes.isEmpty() ) {
	    pw.print("%_Unsupported_Types: " );
	    for (Iterator it = unsupportedDataTypes.iterator();
		 it.hasNext(); ) {
		pw.print( it.next() );
		if ( it.hasNext() ) {
		    pw.print( " " );
		}
	    }
	    pw.println();
	}
	/* Add an axiom stating that the data type is a subset of the
	 * data domain */
	for (int i=0; i<unsupportedDataTypes.size(); i++) {
	    addAxiom( "![X]: (" + typeNames[OTHER] + "_" + i + "(X) => dataDomain(X))", "Data types are subsets of the data domain" );
	}
    }

    public String mangle( String str ) {
	String result = str;
	try {
	    result = URLEncoder.encode(str, "utf-8");
	} catch (Exception ex) {
	}
// 	result = result.replace('1','a');
// 	result = result.replace('2','b');
// 	result = result.replace('3','c');
// 	result = result.replace('4','d');
// 	result = result.replace('5','e');
// 	result = result.replace('6','f');
// 	result = result.replace('7','g');
// 	result = result.replace('8','h');
// 	result = result.replace('9','i');
// 	result = result.replace('0','j');
	result = result.replace('-','_');
	result = result.replace('%','_');
	result = result.replace('.','x');
	return result;
    }

    /* Returns a value identifying whether it's a type that we can
     * handle. */
    public int determineType( URI uri ) throws OWLException {
	if ( uri==null || uri.toString().equals("http://www.w3.org/2001/XMLSchema#string") ) {
	    return STRING;
	} else if ( uri.toString().equals("http://www.w3.org/2001/XMLSchema#integer") ) {
	    return INTEGER;
	} else {
	    return OTHER;
	}
    }

    public String typeFor( OWLDataType odt ) throws OWLException {
	URI uri = odt.getURI();
	if ( determineType( odt.getURI() ) == STRING ) {
	    return typeNames[STRING];
	} else if ( determineType( odt.getURI() ) == Renderer.INTEGER ) {
	    return typeNames[INTEGER];
	} else {
	    return typeNames[OTHER] + "_" + indexForUnsupported( odt.getURI().toString() );
	}
    }

    /* Returns a unique index for an unsupported datatype */
    public int indexForUnsupported( String str ) {
	if (!unsupportedDataTypes.contains( str ) ) {
	    unsupportedDataTypes.add( str );
// 	    System.out.println( str );
// 	    System.out.println( unsupportedDataTypes.indexOf( str ) );
// 	    System.out.println( unsupportedDataTypes.size() );
	}
	return unsupportedDataTypes.indexOf( str );
    }
    
    public int determineType( OWLDataValue dv ) throws OWLException {
	return determineType( dv.getURI() );
    }
    
    public String canonicalize( OWLDataValue dv ) throws OWLException {
	/* Not actually doing any canonicalization...*/
	int t = determineType( dv );
	if (t==STRING) {
	    Object v = dv.getValue();
	    if (!stringDataValues.contains( v ) ) {
		stringDataValues.add( v );
		addAxiom( typeNames[t]+ "(" + typeNames[t] + "_" + stringDataValues.indexOf( v ) + ")", "Data value" );
	    }
	    return typeNames[t] + "_" + stringDataValues.indexOf( v );
	}
	if (t==INTEGER) {
	    try {
		int i = Integer.parseInt( (String) dv.getValue() );
		Integer integer = new Integer( i );
		if (!integerDataValues.contains( integer ) ) {
		    integerDataValues.add( integer );
		    addAxiom( typeNames[t] + "(" + typeNames[t] + "_" + i + ")", "Data value" );
		}
		return typeNames[t] + "_" + i;
	    } catch (NumberFormatException ex) {
		throw new RendererException( ex.getMessage() );
	    }
	}
	if (t==OTHER) {
	    Object v = dv.getURI().toString() + "@" + dv.getValue().toString();
	    if (!otherDataValues.contains( v ) ) {
		otherDataValues.add( v );
		
		addAxiom( typeNames[t] + "_" + indexForUnsupported( dv.getURI().toString() ) +
			  "(" + typeNames[t] + "_value_" + otherDataValues.indexOf( v ) + ")", "Data value" );
	    }
	    return typeNames[t] + "_value_" + otherDataValues.indexOf( v );
	}
	throw new RendererException( "Problem with Datatype canonicalization" );
    }
	
    public String shortForm( OWLNamedObject ono ) throws OWLException {
	/* If it's an anonymous one, create a new name for it. */
	String prefix = "i";
// 	if (ono instanceof OWLIndividual) { prefix = "i"; }
// 	if (ono instanceof OWLProperty) { prefix = "r"; }
// 	if (ono instanceof OWLClass) { prefix = "c"; }

	if (ono instanceof OWLIndividual) {
	    OWLIndividual oi = (OWLIndividual) ono;
	    if (oi.isAnonymous()) {
		if (!anonymousIndividuals.containsKey( oi ) ) {
		    
		    String newName = mangle(prefix + sdf.format(new Date()) + "" + random.nextInt(1000) + "");
		    anonymousIndividuals.put( oi, newName );
		}
		return (String) anonymousIndividuals.get( oi );
	    }
	}	
	URI uri = ono.getURI();
	if (uri==null) {
	    return "xxx";
	}
	return shortForm( uri );
    }


    public String shortForm( URI uri ) {
	/* This is needed because Vampire insists on identifiers
	 * starting with lower case. */
	String prefix = "i";

	if (uri==null) {
	    return "xxx";
	}
	try {
	    if ( uri.getFragment()==null ) {
		/* It's not of the form http://xyz/path#frag */
		return mangle( prefix + uri.toString() );
	    }
	    /* It's of the form http://xyz/path#frag */
	    String ssp = new URI( uri.getScheme(),
				  uri.getSchemeSpecificPart(),
				  null ).toString();
	    if ( !ssp.endsWith("#") ) {
		ssp = ssp + "#";
	    }
	    if ( known.keySet().contains( ssp ) ) {
		return mangle( prefix + (String) known.get( ssp ) + 
			       uri.getFragment() );
	    }
	    if ( !shortNames.contains( ssp ) ) {
		/* Add it to the set if necessary */
		shortNames.add( ssp );
	    }
	    
	    if ((shortNames.indexOf( ssp )) < names.length) {
		return mangle( prefix + (names[ shortNames.indexOf( ssp ) ]) + 
			       uri.getFragment());
	    }
	    /* We can't shorten it -- there are too many...*/
	    return mangle( prefix +  uri.toString() );	
	    
 	} catch ( URISyntaxException ex ) {
	}
	return mangle( prefix + uri.toString() );	
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

	    URI uri;
	    boolean neg = false;

	    if (args.length > 1) {
		neg = args[0].equals("-n");
		uri = new URI( args[1] );
	    } else {
		uri = new URI( args[0] );
	    }		
	    
	    OWLOntology onto = 
		OntologyHelper.getOntology( uri );
	    Renderer renderer = new Renderer();
	    renderer.setNegate( neg );
	    Writer writer = new StringWriter();
	    renderer.renderOntology( onto, writer );
	    System.out.println( writer.toString() );
	} catch ( Exception ex ) {
	    ex.printStackTrace();
	}
    }

    public class BigCardinalityException extends RendererException {
	public BigCardinalityException( String message ) {
	    super( message );
	}
    }
    
    protected void cardinalityException( String message ) throws OWLException {
	throw new BigCardinalityException( message );
    }
    
} // Renderer



/*
 * ChangeLog
 * $Log: Renderer.java,v $
 * Revision 1.9  2004/05/14 10:37:11  sean_bechhofer
 * no message
 *
 * Revision 1.8  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.7  2004/03/05 17:34:49  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.6  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.5  2003/12/12 16:22:49  sean_bechhofer
 * Changes to TPTP renderer to cope with data/abstract domain split.
 *
 * Revision 1.4  2003/11/28 11:04:36  sean_bechhofer
 * Minor rendering fixes
 *
 * Revision 1.3  2003/11/28 10:28:17  sean_bechhofer
 * Separation of data and abstract domains.
 *
 * Revision 1.2  2003/11/21 16:31:11  sean_bechhofer
 * Changes to TPTP rendering -- separation of abstract and data domains.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.13  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.12  2003/10/02 14:33:06  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.11  2003/10/01 16:51:09  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.10  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.9  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.8  2003/09/22 13:21:44  bechhofers
 * Fixes to rendering and some extra helper functions.
 *
 * Revision 1.7  2003/09/10 16:45:08  bechhofers
 * Minor rendering changes.
 *
 * Revision 1.6  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.5  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.4  2003/08/20 08:39:57  bechhofers
 * Alterations to tests.
 *
 * Revision 1.3  2003/06/19 13:33:33  seanb
 * Addition of construct checking.
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
 * Revision 1.3  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
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
