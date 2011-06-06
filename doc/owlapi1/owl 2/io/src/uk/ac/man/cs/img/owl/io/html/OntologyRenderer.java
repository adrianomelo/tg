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
 * Filename           $RCSfile: OntologyRenderer.java,v $
 * Revision           $Revision: 1.8 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/10/19 17:32:36 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.html;



/**
 * Produces abstract syntax from OWL ontologies. 
 *
 *
 * Created: Fri Feb 21 09:12:03 2003
 *
 * @author Sean Bechhofer
 * @version $Id: OntologyRenderer.java,v 1.8 2004/10/19 17:32:36 sean_bechhofer Exp $
 */
import java.io.PrintWriter;
import java.util.Set;
import java.util.List;
import java.util.Map;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLObject;
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
import org.semanticweb.owl.model.OWLNamedObject;
import org.semanticweb.owl.model.OWLIndividualAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.io.vocabulary.XMLSchemaSimpleDatatypeVocabulary;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.io.vocabulary.RDFSVocabularyAdapter;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLOntologyObject;
import java.util.HashSet;
import org.semanticweb.owl.io.ShortFormProvider;

public class OntologyRenderer implements ShortFormProvider
{

    /* NOTE: This renderer isn't very careful about where assertions
     * come from, so *all* information about classes, properties
     * etc. is rendered. */

    private List shortNames;
    private Map known;
    private int reservedNames;
    private String[] names = {"a", "b", "c", "d", "e", "f", "g", "h", "i", 
		      "j", "k", "l", "m", "n", "o", "p", "q", "r", 
		      "s", "t", "u", "v", "w", "x", "y", "z", };

    //    private Linker linker;

    private OWLOntology ontology;
    
    public OntologyRenderer( OWLOntology ontology ) {
	this.ontology = ontology;
	generateShortNames();
    }

    public void listClasses( PrintWriter pw,
			     Linker linker ) throws OWLException {
	objectCollection( orderedEntities( ontology.getClasses() ).iterator(),
			  pw,
			  linker );
    }

    public void listObjectProperties( PrintWriter pw,
			     Linker linker ) throws OWLException {
	objectCollection( orderedEntities( ontology.getObjectProperties() ).iterator(),
			  pw,
			  linker );
    }

    public void listAnnotationProperties( PrintWriter pw,
			     Linker linker ) throws OWLException {
	objectCollection( orderedEntities( ontology.getAnnotationProperties() ).iterator(),
			  pw,
			  linker );
    }

    public void listDataProperties( PrintWriter pw,
			     Linker linker ) throws OWLException {
	objectCollection( orderedEntities( ontology.getDataProperties() ).iterator(),
			  pw,
			  linker );
    }

    public void listIndividuals( PrintWriter pw,
			     Linker linker ) throws OWLException {
	objectCollection( orderedEntities( ontology.getIndividuals() ).iterator(),
			  pw,
			  linker );
    }


    public void renderURI( URI uri,
			   PrintWriter pw,
			   Linker linker ) throws OWLException {
	OWLClass clazz = ontology.getClass( uri );
	if (clazz!=null) {
	    renderClass( clazz, pw, linker );
	    return;
	}
	OWLObjectProperty oprop = ontology.getObjectProperty( uri );
	if (oprop!=null) {
	    renderObjectProperty( oprop, pw, linker );
	    return;
	}
	OWLDataProperty dprop = ontology.getDataProperty( uri );
	if (dprop!=null) {
	    renderDataProperty( dprop, pw, linker );
	    return;
	}
	OWLAnnotationProperty aprop = ontology.getAnnotationProperty( uri );
	if (aprop!=null) {
	    renderAnnotationProperty( aprop, pw, linker );
	    return;
	}
	OWLIndividual ind = ontology.getIndividual( uri );
	if (ind!=null) {
	    renderIndividual( ind, pw, linker );
	    return;
	}
    }

    public void renderClass( OWLClass clazz, 
			     PrintWriter pw,
			     Linker linker ) throws OWLException {
	pw.println( "<h2>Class: " + linker.linkFor( clazz ) + "</h2>" );
	if ( !clazz.getEquivalentClasses( ontology ).isEmpty() ) {
	    pw.println( "<h3>Complete Descriptions</h3>" );
	    
	    descriptionCollection( clazz.getEquivalentClasses( ontology ).iterator(), 
				   pw,
				   linker );
	}
	
	if (!clazz.getSuperClasses( ontology ).isEmpty()) {
	    pw.println( "<h3>Super Classes</h3>" );
	    descriptionCollection( clazz.getSuperClasses( ontology ).iterator(), 
				   pw,
				   linker );
	}
	if (!clazz.getEnumerations( ontology ).isEmpty()) {
	    pw.println( "<h3>Enumerations</h3>" );
	    descriptionCollection( clazz.getEnumerations( ontology ).iterator(),
				   pw,
				   linker);
	}
	if ( !clazz.getAnnotations( ontology ).isEmpty() ) {
	    pw.println( "<h3>Annotations</h3>" );
	    List l = new ArrayList();
	    for ( Iterator it = clazz.getAnnotations( ontology ).iterator();
		  it.hasNext(); ) {
		OWLAnnotationInstance oai = (OWLAnnotationInstance) it.next();
		RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		oai.accept( visitor );
		l.add( visitor.result() );
	    }
	    collection ( l, pw );
	}
	displayUsage( clazz, pw, linker );
    }

    public void renderIndividual( OWLIndividual ind,
				  PrintWriter pw,
				  Linker linker ) throws OWLException {
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

	pw.print( "<h2>Individual: " + linker.linkFor( ind ) + "</h2>" );
	
	if (!ind.getTypes( ontology ).isEmpty()) {
	    pw.println( "<h3>Types</h3>");
	    descriptionCollection( ind.getTypes( ontology ).iterator(),
				   pw,
				   linker);
	}
	
	if (!ind.getObjectPropertyValues( ontology ).keySet().isEmpty()) {
	    pw.println( "<h3>Values</h3>");
	    
	    Map propertyValues = ind.getObjectPropertyValues( ontology );
	    
	    for ( Iterator it = propertyValues.keySet().iterator();
		  it.hasNext(); ) {
		pw.println();
		OWLObjectProperty prop = (OWLObjectProperty) it.next();
		Set vals = (Set) propertyValues.get(prop);
		List l = new ArrayList();
		for (Iterator valIt = vals.iterator(); valIt.hasNext();
		     ) {
		    OWLIndividual oi = (OWLIndividual) valIt.next();
		    RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		    oi.accept( visitor );
		    l.add( "  <span class='keyword'>value</span>(" + 
			      linker.linkFor( prop ) + " " + 
			      visitor.result() + ")" );
		}
		collection( l, pw );
	    }
	}
	
	if (!ind.getDataPropertyValues( ontology ).keySet().isEmpty()) {
	    pw.println( "<h3>Data Values</h3>");
	    
	    Map dataValues = ind.getDataPropertyValues( ontology );
	    
	    for ( Iterator it = dataValues.keySet().iterator();
		  it.hasNext(); ) {
		pw.println();
		OWLDataProperty prop = (OWLDataProperty) it.next();
		Set vals = (Set) dataValues.get(prop);
		List l = new ArrayList();
		for (Iterator valIt = vals.iterator(); valIt.hasNext();
		     ) {
		    OWLDataValue dtv = (OWLDataValue) valIt.next();
		    RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		    dtv.accept( visitor );
		    l.add( "  <span class='keyword'>value</span>(" + 
			      linker.linkFor( prop ) + " " + 
			      visitor.result() + ")" );
		}
		collection( l, pw );
	    }
	}
	
	if ( !ind.getAnnotations( ontology ).isEmpty() ) {
	    pw.println( "<h3>Annotations</h3>");
	    List l = new ArrayList();
	    for ( Iterator it = ind.getAnnotations( ontology ).iterator();
		  it.hasNext(); ) {
		OWLAnnotationInstance oai = (OWLAnnotationInstance) it.next();
		RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		oai.accept( visitor );
		l.add( visitor.result() );
	    }
	    collection( l, pw );
	}
	displayUsage( ind, pw, linker );
    }

    public void renderAnnotationProperty( OWLAnnotationProperty prop,
					  PrintWriter pw,
					  Linker linker ) throws OWLException {
	pw.println( "<h2>Annotation Property: " + linker.linkFor( prop ) + "</h2>" );
	displayUsage( prop, pw, linker );
    }
    
    public void renderObjectProperty( OWLObjectProperty prop,
				      PrintWriter pw,
				      Linker linker ) throws OWLException {
	pw.print( "<h2>ObjectProperty " + linker.linkFor(prop) + "</h2>" );
	List l = new ArrayList();
	
	if ( prop.isTransitive( ontology ) ) {
	    l.add(" <span class='keyword'>Transitive</span>");
	}
	if ( prop.isFunctional( ontology ) ) {
	    l.add(" <span class='keyword'>Functional</span>");
	}
	if ( prop.isInverseFunctional( ontology ) ) {
	    l.add(" <span class='keyword'>InverseFunctional</span>");
	}
	if ( prop.isSymmetric( ontology ) ) {
	    l.add(" <span class='keyword'>Symmetric</span>");
	}
	collection ( l, pw );
	if ( !prop.getInverses( ontology ).isEmpty() ) {
	    pw.println( "<h3>Inverse</h3>" );
	    objectCollection( prop.getInverses( ontology ).iterator(), 
			      pw,
			      linker );
	}
	if ( !prop.getSuperProperties( ontology ).isEmpty() ) {
	    pw.println( "<h3>SuperProperties</h3>" );
	    objectCollection( prop.getSuperProperties( ontology ).iterator(),
			      pw,
			      linker );
	}
	if ( !prop.getDomains( ontology ).isEmpty() ) {
	    pw.println( "<h3>Domain</h3>" );
	    descriptionCollection( prop.getDomains( ontology ).iterator(),
				   pw,
				   linker );
	}
	if ( !prop.getRanges( ontology ).isEmpty() ) {
	    pw.println( "<h3>Range</h3>" );
	    descriptionCollection( prop.getRanges( ontology ).iterator(),
				   pw,
				   linker );
	}

	if ( !prop.getAnnotations( ontology ).isEmpty() ) {
	    pw.println( "<h3>Annotations</h3>");
	    l = new ArrayList();
	    for ( Iterator it = prop.getAnnotations( ontology ).iterator();
		  it.hasNext(); ) {
		OWLAnnotationInstance oai = (OWLAnnotationInstance) it.next();
		RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		oai.accept( visitor );
		l.add( visitor.result());
	    }
	    collection( l, pw );
	}
	displayUsage( prop, pw, linker );
    }
    
    public void renderDataProperty( OWLDataProperty prop,
				    PrintWriter pw,
				    Linker linker ) throws OWLException {
	pw.print( "<h2>DataProperty: " + linker.linkFor( prop ) + "</h2>" );
	List l =  new ArrayList();
	if ( prop.isFunctional( ontology ) ) {
	    l.add(" <span class='keyword'>Functional</span>");
	}
	collection( l, pw );

	if ( !prop.getDomains( ontology ).isEmpty() ) {
	    pw.println( "<h3>Domain</h3>" );
	    descriptionCollection( prop.getDomains( ontology ).iterator(),
				   pw,
				   linker );
	}
	
	if ( !prop.getRanges( ontology ).isEmpty() ) {
	    pw.println( "<h3>Domain</h3>" );
	    l = new ArrayList();
	    for ( Iterator it = prop.getRanges( ontology ).iterator();
		  it.hasNext(); ) {
		OWLDataType ran = (OWLDataType) it.next();
		RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		ran.accept( visitor );
		l.add( visitor.result() );
	    }
	    collection( l, pw );
	}
	if ( !prop.getAnnotations( ontology ).isEmpty() ) {
	    pw.println( "<h3>Annotations</h3>");
	    l = new ArrayList();
	    for ( Iterator it = prop.getAnnotations( ontology ).iterator();
		  it.hasNext(); ) {
		OWLAnnotationInstance oai = (OWLAnnotationInstance) it.next();
		RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		oai.accept( visitor );
		l.add( visitor.result());
	    }
	    collection( l, pw );
	}
	displayUsage( prop, pw, linker );
    }

    public void renderDataType( OWLDataType datatype,
				PrintWriter pw,
				Linker linker) throws OWLException {
	pw.println("<h2>Datatype: " + shortForm( datatype.getURI() ) + "</h2>" );
    }
    
    public void renderClassAxiom( OWLClassAxiom axiom,
				  PrintWriter pw,
				  Linker linker ) throws OWLException {
	RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
	axiom.accept( visitor );
	pw.println(" " + visitor.result() );
    }

    public void renderPropertyAxiom( OWLPropertyAxiom axiom,
				     PrintWriter pw,
				     Linker linker ) throws OWLException {
	RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
	axiom.accept( visitor );
	pw.println(" " + visitor.result() );
    }

    public void renderIndividualAxiom( OWLIndividualAxiom axiom,
				       PrintWriter pw,
				       Linker linker ) throws OWLException {
	RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
	axiom.accept( visitor );
	pw.println(" " + visitor.result() );
	pw.println( "<hr/>" );
    }

    public void generateShortNames() {
	/* Generates a list of namespaces. */
	Set allURIs = new HashSet();
	try {
	    allURIs = OntologyHelper.allURIs( ontology );
	} catch (OWLException ex) {
	}
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
    
    public void writeShortNames( PrintWriter pw ) {
	pw.println( "<h2>Namespaces</h2>" );
	pw.println( "<table>");
	List l = new ArrayList();
	for ( Iterator it=known.keySet().iterator();
	      it.hasNext(); ) {
	    String ns = (String) it.next();
	    String shrt = (String) known.get( ns );
	    pw.println( "<tr><td align='right'><b>" + shrt + "</b></td><td><a href=\" + ns + \">&lt;" + ns + "&gt;</a></td></tr>" );
	    //	    l.add( shrt + "=" + ns );
	}
	for ( int i=0; i<shortNames.size(); i++ ) {
	    if (i < names.length) {
		String ns = (String) shortNames.get( i );
		pw.println( "<tr><td align='right'><b>" + names[i] + "</b></td><td><a href=\" + ns + \">&lt;" + ns + "&gt;</a></td></tr>" );
		//		l.add( names[i] + "=" + ns );
	    }
	}
	//	collection( l, pw );
    }

    public String header () {
	return "<html><body style='font-family:arial'>";
    }

    public String footer () {
	return "</body></html>";
    }


    public Linker defaultLinker() {
	return new Linker() {
		public String linkFor( OWLNamedObject ono ) throws OWLException {
		    return "<a href=\"" + ono.getURI() + "\">" + shortForm( ono.getURI() ) + "</a>";
		}
	    };
    }
    
    public String shortForm( URI uri ) {
	if (uri==null) {
	    return "_";
	}
	try {
	    if ( uri.getFragment()==null ) {
		/* It's not of the form http://xyz/path#frag */
		return "&lt;" + uri.toString() + "&gt;" ;
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
		if ((shortNames.indexOf( ssp )) < names.length) {
		    return (names[ shortNames.indexOf( ssp ) ]) + 
			":" + 
			uri.getFragment();
		}
		/* We can't shorten it -- there are too many...*/
		return "&lt;" + uri.toString() + "&gt;";	
	    }
 	} catch ( URISyntaxException ex ) {
	}
	return uri.toString();	
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

    private void descriptionCollection( Iterator it,
					PrintWriter pw,
					Linker linker ) throws OWLException {
	List l = new ArrayList();
	while ( it.hasNext() ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
	    eq.accept( visitor );
	    l.add( visitor.result() );
	}
	collection ( l, pw );
    }

    private void objectCollection( Iterator it,
				   PrintWriter pw,
				   Linker linker ) throws OWLException {
	List l = new ArrayList();
	while ( it.hasNext() ) {
	    OWLNamedObject eq = (OWLNamedObject) it.next();
	    l.add( linker.linkFor( eq ) );
	}
	collection ( l, pw );
    }

    private void collection( List l, 					
			     PrintWriter pw ) {
	pw.println( "<div class='collection'>" ); 

	for (Iterator it = l.iterator();
	     it.hasNext(); ) {
	    pw.println( "<span class='item'>" ); 
	    pw.println( it.next() );
	    pw.println( "</span>" ); 
	}
	pw.println( "</div>" ); 
    }

    /* Well dodgy coding */
    private void renderAnnotationContent( Object o, PrintWriter pw ) throws OWLException {
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

    private void displayUsage( OWLEntity entity,
			       PrintWriter pw,
			       Linker linker ) throws OWLException {
	if ( !entity.getUsage( ontology ).isEmpty() ) {
	    pw.println( "<h3>Usage</h3>" );
	    pw.println( "<div class='collection'><span class='item'>");
	    for ( Iterator it = entity.getUsage( ontology ).iterator();
		  it.hasNext(); ) {
		/* We actually know these should be OWLOntologyObjects. */
		OWLObject ooo = (OWLObject) it.next();
		RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		ooo.accept( visitor );
		pw.println( visitor.result() );
		if ( it.hasNext() ) {
		    pw.print ( ", ");
		}
	    }
	    pw.println();
	    pw.println( "</span></div>");
	}
	if ( !entity.objectsUsed( ontology ).isEmpty() ) {
	    pw.println( "<h3>Uses</h3>" );
	    pw.println( "<div class='collection'><span>");
	    for ( Iterator it = entity.objectsUsed( ontology ).iterator();
		  it.hasNext(); ) {
		/* We actually know these should be OWLEntities. */
		OWLObject ooo = (OWLObject) it.next();
		RenderingVisitor visitor = new RenderingVisitor( linker, ontology );
		ooo.accept( visitor );
		pw.println( visitor.result() );
		if ( it.hasNext() ) {
		    pw.print ( ", ");
		}
	    }
	    pw.println();
	    pw.println( "</span></div");
	}
    }

    
    public static void main( String[] args ) {
	try {
	    BasicConfigurator.configure();

	    URI uri = new URI( args[0] );

	    OWLOntology onto = OntologyHelper.getOntology( uri );
	    Writer writer = new StringWriter();
	    PrintWriter pw = new PrintWriter( writer );
	    final OntologyRenderer renderer = new OntologyRenderer( onto );
	    pw.println( renderer.header() );
	    Linker linker = new Linker() {
		    public String linkFor( OWLNamedObject ono ) throws OWLException {
			return "<a href=\"" + ono.getURI() + "\">" + renderer.shortForm( ono.getURI() ) + "</a>";
		    }
		};

	    pw.println( "<h1>Ontology: " + onto.getURI() + "</h1>" );
	    pw.println( "<table border=\"1\"><tr>" );
	    pw.println( "<td valign=\"top\">" );
 	    renderer.listClasses( pw, linker );
	    pw.println( "</td>" );
	    pw.println( "<td valign=\"top\">" );
	    renderer.listObjectProperties( pw, linker );
	    pw.println( "</td>" );
	    pw.println( "<td valign=\"top\">" );
	    renderer.listDataProperties( pw, linker );
	    pw.println( "</td>" );
	    pw.println( "<td valign=\"top\">" );
	    renderer.listAnnotationProperties( pw, linker );
	    pw.println( "</td>" );
	    pw.println( "<td valign=\"top\">" );
	    renderer.listIndividuals( pw, linker );
	    pw.println( "</td>" );
	    pw.println( "</tr></table>" );
	    for (Iterator it = onto.getClasses().iterator();
		 it.hasNext();) {
		renderer.renderClass( (OWLClass) it.next(),
				      pw,
				      linker );
	    }
	    for (Iterator it = onto.getObjectProperties().iterator();
		 it.hasNext();) {
		renderer.renderObjectProperty( (OWLObjectProperty) it.next(),
				      pw,
				      linker );
	    }
	    for (Iterator it = onto.getDataProperties().iterator();
		 it.hasNext();) {
		renderer.renderDataProperty( (OWLDataProperty) it.next(),
				      pw,
				      linker );
	    }
	    for (Iterator it = onto.getIndividuals().iterator();
		 it.hasNext();) {
		renderer.renderIndividual( (OWLIndividual) it.next(),
				      pw,
				      linker );
	    }

	    pw.println( renderer.footer() );

	    System.out.println( writer.toString() );
	} catch ( Exception ex ) {
	    ex.printStackTrace();
	}
    }
    
    
} // OntologyRenderer



/*
 * ChangeLog
 * $Log: OntologyRenderer.java,v $
 * Revision 1.8  2004/10/19 17:32:36  sean_bechhofer
 * Cosmetic changes
 *
 * Revision 1.7  2004/07/09 14:04:58  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
 *
 * Revision 1.6  2004/07/09 12:07:48  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.5  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.4  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.3  2003/12/02 10:04:41  sean_bechhofer
 * Minor rendering fixes
 *
 * Revision 1.2  2003/11/28 11:04:36  sean_bechhofer
 * Minor rendering fixes
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.8  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.7  2003/10/02 14:33:06  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.6  2003/10/01 16:51:09  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.5  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.4  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.3  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.2  2003/07/08 18:01:07  bechhofers
 * Presentation Servlet and additional documentation regarding inference.
 *
 * Revision 1.1  2003/07/05 16:58:14  bechhofers
 * Adding an HTML servlet-based renderer and some changes to the
 * inferencing classes.
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
