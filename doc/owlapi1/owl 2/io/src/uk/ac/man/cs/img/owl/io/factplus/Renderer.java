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
 * Revision           $Revision: 1.5 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:46 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.factplus; 


/**
 * Produces abstract syntax from OWL ontologies. 
 *
 *
 * Created: Fri Feb 21 09:12:03 2003
 *
 * @author Sean Bechhofer
 * @version $Id: Renderer.java,v 1.5 2006/03/28 16:14:46 ronwalf Exp $
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

public class Renderer implements org.semanticweb.owl.io.Renderer, ShortFormProvider
{

    /* NOTE: This renderer isn't very careful about where assertions
     * come from, so *all* information about classes, properties
     * etc. is rendered. */

    private PrintWriter pw;
    private Set allURIs;
    private Set allOntologies;
    private List shortNames;
    private Map known;
    private int reservedNames;
    private RenderingVisitor visitor;
    private String[] names = {"a", "b", "c", "d", "e", "f", "g", "h", "i", 
		      "j", "k", "l", "m", "n", "o", "p", "q", "r", 
		      "s", "t", "u", "v", "w", "x", "y", "z", };
    
    
    private Random random;
    private SimpleDateFormat sdf;
    private java.util.TimeZone gmt;

    private Set definedURIs;

    private Map anonymousIndividuals;

    private boolean doneThing;

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
	    this.allOntologies = OntologyHelper.importClosure( ontology );
	    this.visitor = new RenderingVisitor( this );
	    this.definedURIs = new HashSet();
	    generateShortNames();
	    writeShortNames();
	    sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	    gmt = java.util.TimeZone.getTimeZone("GMT");
	    sdf.setTimeZone(gmt);

	    doneThing = false;

	    random = new Random();
	    anonymousIndividuals = new HashMap();
	    for (Iterator ontoIt = allOntologies.iterator();
		 ontoIt.hasNext(); ) {
		OWLOntology onto = (OWLOntology) ontoIt.next();
		
		pw.println(" ;; Ontology: " + onto.getURI());
		
		pw.println();
		pw.println(" ;; Classes ");
		pw.println();
		for ( Iterator it = 
			  orderedEntities( onto.getClasses() ).iterator();
		      it.hasNext(); ) {
		    renderClass( onto, ( OWLClass ) it.next() );
		}
		pw.println(" ;; Object Properties ");
		
		for ( Iterator it = 
			  orderedEntities( onto.getObjectProperties() ).iterator();
		      it.hasNext(); ) {
		    renderObjectProperty( onto, ( OWLObjectProperty ) it.next() );
		}
		pw.println(" ;; Data Properties ");
		
		for ( Iterator it = 
			  orderedEntities( onto.getDataProperties() ).iterator();
		      it.hasNext(); ) {
		    renderDataProperty( onto, ( OWLDataProperty ) it.next() );
		}
		pw.println(" ;; Individuals ");
		
		for ( Iterator it = 
			  orderedEntities( onto.getIndividuals() ).iterator();
		      it.hasNext(); ) {
		    renderIndividual( onto, ( OWLIndividual ) it.next() );
		}
		pw.println(" ;; Class Axioms ");
		
		for ( Iterator it = 
			  orderedEntities( onto.getDatatypes() ).iterator();
		      it.hasNext(); ) {
		    renderDataType( onto, ( OWLDataType ) it.next() );
		}
		pw.println(" ;;_Datatypes ");
		
		for ( Iterator it = 
			  orderedEntities( onto.getClassAxioms() ).iterator();
		      it.hasNext(); ) {
		    renderClassAxiom( ( OWLClassAxiom ) it.next() );
		} 
		pw.println(" ;; Property Axioms ");
		
		for ( Iterator it = 
			  orderedEntities( onto.getPropertyAxioms() ).iterator();
		      it.hasNext(); ) {
		    renderPropertyAxiom( ( OWLPropertyAxiom ) it.next() );
		} 
		
		pw.println(" ;; Individual Axioms ");
		for ( Iterator it = 
			  orderedEntities( onto.getIndividualAxioms() ).iterator();
		      it.hasNext(); ) {
		renderIndividualAxiom( ( OWLIndividualAxiom ) it.next() );
		} 
	    }

	} catch (OWLException ex) {
	    throw new RendererException( ex.getMessage() );
	}
    }

    private void renderClass( OWLOntology ontology,
			      OWLClass clazz ) throws OWLException {
	if (!definedURIs.contains( clazz.getURI() ) ) {
	    pw.println(" (defprimconcept " + 
		       shortForm( clazz ) + ")" );
	    definedURIs.add( clazz.getURI() );
	}
	/* This is a bit of a hack... */
	if ( shortForm( clazz ).equals("|owl:Thing|") && 
	     !doneThing) {
	    pw.println(" ;; Thing == TOP ");
	    pw.println(" (equal_c " + shortForm( clazz ) + " :top)" );
	    doneThing = true;
	}

	if ( shortForm( clazz ).equals("|owl:Nothing|") ) {
	    pw.println(" ;; Nothing == BOTTOM ");
	    pw.println(" (equal_c " + shortForm( clazz ) + " :bottom)" );
	}
	
	for ( Iterator it = clazz.getEquivalentClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.print(" (equal_c " + shortForm( clazz ) );
	    visitor.reset();
	    eq.accept( visitor );
	    pw.print( "  " + visitor.result() );
	    pw.println(" )");
	}
	
	for ( Iterator it = clazz.getSuperClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.print(" (implies_c " + shortForm( clazz ) );
	    visitor.reset();
	    eq.accept( visitor );
	    pw.print( "  " + visitor.result());
	    pw.println(" )");
	}

	for ( Iterator it = clazz.getEnumerations( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.print(" (equal_c " + shortForm( clazz ) );
	    visitor.reset();
	    eq.accept( visitor );
	    pw.print( "  " + visitor.result() );
	    pw.println(" )");
	}
	pw.println();
    }

    private void renderIndividual( OWLOntology ontology,
				   OWLIndividual ind ) throws OWLException {
	/* If the individual is anonymous and has any incoming
	 * properties, then we do not wish to show it here -- it will
	 * be rendered during the rendering of the thing that points
	 * to it. */
// 	if (ind.isAnonymous()) {
// 	    Map m = ind.getIncomingObjectPropertyValues(); 
// 	    if ( !m.isEmpty() ) {
// 		return;
// 	    }
// 	} 
	// 	if ( ind.isAnonymous() ) {
	// 	    pw.print(" Individual(" );
	// 	} else {

	if (!definedURIs.contains( ind.getURI() ) ) {
	    pw.println(" (defindividual " + shortForm( ind ) + ")" );
	    definedURIs.add( ind.getURI() );
	}

	//	}
	
	if (ind.getTypes( ontology ).isEmpty() && 
	    ind.getObjectPropertyValues( ontology ).keySet().isEmpty() &&
	    ind.getDataPropertyValues( ontology ).keySet().isEmpty()) {
	    //	    pw.println( ")");
	} else {
	    for ( Iterator it = ind.getTypes( ontology ).iterator();
		  it.hasNext(); ) {
		pw.print(" (instance " + shortForm( ind ) );
		OWLDescription eq = (OWLDescription) it.next();
		visitor.reset();
		eq.accept( visitor );
		pw.print( " " + visitor.result() );
		pw.println( ")" );
	    }
	    /* Worry about this later.... */
	    
	    Map propertyValues = ind.getObjectPropertyValues( ontology );
	    //	    System.out.println("ZZ: " + ind);
	    for ( Iterator it = propertyValues.keySet().iterator();
		  it.hasNext(); ) {
		OWLObjectProperty prop = (OWLObjectProperty) it.next();
		Set vals = (Set) propertyValues.get(prop);
		for (Iterator valIt = vals.iterator(); valIt.hasNext();
		     ) {
 		    OWLIndividual oi = (OWLIndividual) valIt.next();
		    visitor.reset();
		    oi.accept( visitor );
 		    pw.println( " (related " + 
			      shortForm( ind ) + " " +
			      shortForm( prop ) + " " + 
			      visitor.result() + ")" );
		}
	    }
	    Map dataValues = ind.getDataPropertyValues( ontology );
	    //	    System.out.println("ZZ: " + ind);
	    for ( Iterator it = dataValues.keySet().iterator();
		  it.hasNext(); ) {
		pw.println();
		OWLDataProperty prop = (OWLDataProperty) it.next();
		Set vals = (Set) dataValues.get(prop);
		for (Iterator valIt = vals.iterator(); valIt.hasNext();
		     ) {
		    //		    System.out.println("QQ: " + ((OWLIndividual) valIt.next()));
 		    OWLDataValue dtv = (OWLDataValue) valIt.next();
		    visitor.reset();
		    dtv.accept( visitor );
 		    pw.print( ";;_DataTypeValue  value(" + 
			      shortForm( prop ) + " " + 
			      visitor.result() + ")" );
		    if (valIt.hasNext()) {
			pw.println();
		    }
		}
// 		if (it.hasNext()) {
// 		    pw.println();
// 		}
	    }
	}
	pw.println();
    }

    private void renderObjectProperty( OWLOntology ontology,
				       OWLObjectProperty prop ) throws OWLException {
	//	String key = "defprimrole";

	if (!definedURIs.contains( prop.getURI() ) ) {
	    pw.println(" (defprimrole " + shortForm( prop ) + ")" );
	    definedURIs.add( prop.getURI() );
	}
	
// 	if ( prop.isTransitive( ontology ) ) {
// 	    pw.print(" :transitive T");
// 	}
	
	/* ?? */
	if ( prop.isInverseFunctional( ontology ) ) {
	    pw.print(" (functional");
	    pw.print(" (inv " + shortForm( prop ) + ")" );	    
	    pw.println(" )");
	}
	
	if (!prop.getSuperProperties( ontology ).isEmpty()) {
	    //	    pw.print(" :supers ( ");
	    for ( Iterator it = prop.getSuperProperties( ontology ).iterator();
		  it.hasNext(); ) {
		OWLObjectProperty sup = (OWLObjectProperty) it.next();
//		pw.print(" ;; Super");

		pw.print(" (implies_r ");
		pw.print(shortForm( prop ));
		pw.print(" ");
		pw.print(shortForm( sup ));
		pw.println(")");
	    }	    
		//	    pw.print(")");
	}
	
	
	//	pw.println(")");

	if ( prop.isFunctional( ontology ) ) {
	    pw.print(" (functional");
	    pw.print(" " + shortForm( prop ) );	    
	    pw.println(" )");
	}

	if ( prop.isTransitive( ontology ) ) {
	    pw.print(" (transitive");
	    pw.print(" " + shortForm( prop ) );	    
	    pw.println(" )");
	}

	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println(" ;; Domain " + shortForm( prop ) );
	    pw.print(" (domain " + shortForm( prop ) + " " );
	    OWLDescription dom = (OWLDescription) it.next();
	    visitor.reset();
	    dom.accept( visitor );
	    pw.println( visitor.result() + " )");
	}
	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println(" ;; Range " + shortForm( prop ) );
	    pw.print(" (range " + shortForm( prop ) + " " );
	    OWLDescription dom = (OWLDescription) it.next();
	    visitor.reset();
	    dom.accept( visitor );
	    pw.println( visitor.result() + " )");
	}

	if ( prop.isSymmetric( ontology ) ) {
	    pw.println(" ;; Symmetry ");
	    pw.println( " (equal_r " + " " + shortForm( prop ) + " " +
		      "(inv " + shortForm( prop ) + " ))"  );
	}

	for ( Iterator it = prop.getInverses( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println(" ;;Inverse " + shortForm( prop ) );

	    pw.print( " (equal_r " + " " + shortForm( prop ) + " " +
		      "(inv " );
	    OWLObjectProperty inv = (OWLObjectProperty) it.next();
	    visitor.reset();
	    inv.accept( visitor );
	    pw.println( visitor.result() + " ))");
	}	    
	pw.println();
    }
    
    private void renderDataProperty( OWLOntology ontology,
				     OWLDataProperty prop ) throws OWLException {
	/* Not sure quite what  to do with these -- ignore for now... */
	pw.println(" ;; DataProperty " + shortForm( prop ) );
    }

    private void renderDataType( OWLOntology ontology,
				 OWLDataType datatype ) throws OWLException {
	pw.println(" ;; Datatype(" + datatype.getURI() + ")" );
    }

    private void renderClassAxiom( OWLClassAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(visitor.result() );
	pw.println();
    }

    private void renderPropertyAxiom( OWLPropertyAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(visitor.result() );
	pw.println();
    }

    private void renderIndividualAxiom( OWLIndividualAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(visitor.result() );
	pw.println();
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
	pw.println(";; ============= Namespaces ============= ");
	for ( Iterator it=known.keySet().iterator();
	      it.hasNext(); ) {
	    String ns = (String) it.next();
	    String shrt = (String) known.get( ns );
	    pw.println(";;  " + shrt + "\t= " + ns);
	}
	for ( int i=0; i<shortNames.size(); i++ ) {
	    if (i < names.length) {
		String ns = (String) shortNames.get( i );
		pw.println(";;  " + names[i] + "\t= " + ns);
	    }
	}
	pw.println(";; ====================================== ");
    }

    public String shortForm( OWLNamedObject ono ) throws OWLException {
	/* If it's an anonymous one, create a new name for it. */
	if (ono instanceof OWLIndividual) {
	    OWLIndividual oi = (OWLIndividual) ono;
	    if (oi.isAnonymous()) {
		if (!anonymousIndividuals.containsKey( oi ) ) {

		    String newName = "|_" + sdf.format(new Date()) + "-" + random.nextInt(1000) + "|";
		    anonymousIndividuals.put( oi, newName );
		}
		return (String) anonymousIndividuals.get( oi );
	    }
	}	
	URI uri = ono.getURI();
	if (uri==null) {
	    return "|_|";
	}
	try {
	    if ( uri.getFragment()==null ) {
		/* It's not of the form http://xyz/path#frag */
		return "|" + uri.toString() + "|";
	    }
	    /* It's of the form http://xyz/path#frag */
	    String ssp = new URI( uri.getScheme(),
				  uri.getSchemeSpecificPart(),
				  null ).toString();
	    if ( !ssp.endsWith("#") ) {
		ssp = ssp + "#";
	    }
	    if ( known.keySet().contains( ssp ) ) {
		return "|" + (String) known.get( ssp ) + ":" + 
		    uri.getFragment() + "|";
	    }
	    if ( shortNames.contains( ssp ) ) {
		if ((shortNames.indexOf( ssp )) < names.length) {
		    return "|" + (names[ shortNames.indexOf( ssp ) ]) + 
			":" + 
			uri.getFragment() + "|";
		}
		/* We can't shorten it -- there are too many...*/
		return "|" + uri.toString() + "|" ;	
	    }
 	} catch ( URISyntaxException ex ) {
	}
	return "|" + uri.toString() + "|";	
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

	    OWLOntology onto = OntologyHelper.getOntology( uri );
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
 * Revision 1.4  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.3  2003/11/18 13:54:25  sean_bechhofer
 * Fix to abstract syntax rendering.
 *
 * Revision 1.2  2003/11/12 11:07:55  sean_bechhofer
 * Fixing problems relating to imports.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.7  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.6  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.5  2003/10/01 16:51:09  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.4  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.3  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.2  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
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
