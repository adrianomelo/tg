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
 * Filename           $RCSfile: ObjectRenderer.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/03/30 17:46:37 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.io.abstract_syntax; // Generated package name


/**
 * Produces abstract syntax from OWL ontologies.
 *
 *
 * Created: Fri Feb 21 09:12:03 2003
 *
 * @author Sean Bechhofer
 * @version $Id: ObjectRenderer.java,v 1.2 2004/03/30 17:46:37 sean_bechhofer Exp $
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
import org.semanticweb.owl.io.ShortFormProvider;
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
import org.semanticweb.owl.model.OWLObject;

public class ObjectRenderer implements ShortFormProvider
{

    private PrintWriter pw;
    private Set allURIs;
    private List shortNames;
    private Map known;
    private int reservedNames;
    private RenderingVisitor visitor;
    /* If you use more that 26 namespaces in the ontology this will break */
    private String[] names = {"a", "b", "c", "d", "e", "f", "g", "h", "i", 
		      "j", "k", "l", "m", "n", "o", "p", "q", "r", 
		      "s", "t", "u", "v", "w", "x", "y", "z", };

    public ObjectRenderer( OWLOntology ontology )
    {
	this.visitor = new RenderingVisitor( this, ontology );
	generateShortNames();
    }

    public String renderObject( OWLObject obj ) throws RendererException {
	try {
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter( sw );
	    
	    visitor.reset();
	    obj.accept( visitor );
	    return visitor.result();
	} catch (OWLException ex) {
	    throw new RendererException( ex.getMessage() );
	}
    }

    private void generateShortNames() {
	/* Generates a list of namespaces. */
	shortNames = new ArrayList();
	known = new HashMap();
	known.put( OWLVocabularyAdapter.INSTANCE.OWL, "owl" );
	known.put( RDFVocabularyAdapter.INSTANCE.RDF, "rdf" );
	known.put( RDFSVocabularyAdapter.INSTANCE.RDFS, "rdfs" );
    }
    
    public String shortForm( URI uri ) {
	try {
	    if ( uri.getFragment()==null ) {
		/* It's not of the form http://xyz/path#frag */
		return uri.toString();
	    }
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
		return (names[ shortNames.indexOf( ssp ) ]) + 
		    ":" + 
		    uri.getFragment();
	    }
	    /* We haven't seen it before */
	    shortNames.add( ssp );
	    if ((shortNames.indexOf( ssp )) >= names.length) {
		/* We can't shorten it -- there are too many...*/
		return uri.toString();	
	    }
	    return (names[ shortNames.indexOf( ssp ) ]) + 
		":" + 
		uri.getFragment();
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
    
} // Renderer



/*
 * ChangeLog
 * $Log: ObjectRenderer.java,v $
 * Revision 1.2  2004/03/30 17:46:37  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.3  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.2  2003/03/31 16:55:12  seanb
 * Various updates and fixes to parser.
 * Adding inverses, domain and range.
 *
 * Revision 1.1  2003/03/20 10:26:34  seanb
 * Adding Abstract Syntax Renderer
 *
 */
