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
 * Filename           $RCSfile: SimpleOWLTaxonomyReasoner.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/12/19 12:04:16 $
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
import uk.ac.man.cs.img.dig.reasoner.ReasonerFactory;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.OntologyChangeSource;
import java.util.Map;
import java.util.HashMap;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Iterator;
import uk.ac.man.cs.img.owl.io.dig1_0.RenderingVisitor;
import uk.ac.man.cs.img.owl.inference.InferenceHelper;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import org.semanticweb.owl.validation.SpeciesValidator;
import org.semanticweb.owl.io.Renderer;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.List;
import org.semanticweb.owl.inference.OWLTaxonomyReasoner;
import java.io.Writer;
import org.semanticweb.owl.model.helper.OntologyHelper;
//import uk.ac.man.cs.img.owl.Utils;
// Generated package name


/**
 * A Taxonomy reasoner that relies on a DIG reasoner to do its reasoning.
 *
 * @author Sean Bechhofer
 * @version $Id: SimpleOWLTaxonomyReasoner.java,v 1.2 2003/12/19 12:04:16 sean_bechhofer Exp $
 */

public class SimpleOWLTaxonomyReasoner implements OWLTaxonomyReasoner 
{

    /* A simple taxonomy reasoner. Basically a wrapper around a
     * SimpleOWLReasoner. */

    private SimpleOWLReasoner simpleReasoner;

    /**
     * Create a new reasoning object. Expects a DIG reasoner that will
     * do the actual work of reasoning about the class structure.
     *
     * @param digReasoner a <code>Reasoner</code> value
     */
    public SimpleOWLTaxonomyReasoner( Reasoner digReasoner ) throws OWLException {
	this.simpleReasoner = new SimpleOWLReasoner( digReasoner );
    }

    /** Set the ontology that the reasoner knows about. The reasoner
     * will then transmit the converted ontology to the DIG
     * reasoner. */
    public void setOntology( OWLOntology onto ) throws OWLException {
	simpleReasoner.setOntology( onto );
    }
    
    public OWLOntology getOntology() {
	return simpleReasoner.getOntology();
    }

    /* Reasoning tasks relating to classification */
    
    /** Returns the collection of (named) most specific superclasses
	of the given description. The result of this will be a set of
	sets, where each set in the collection represents an
	equivalence class. */

    public Set superClassesOf( OWLClass cl ) throws OWLException {
	return simpleReasoner.superClassesOf( cl );
    }

    /** Returns the collection of (named) most general subclasses
	of the given description. The result of this will be a set of
	sets, where each set in the collection represents an
	equivalence class. */

    public Set subClassesOf( OWLClass cl ) throws OWLException {
	return simpleReasoner.subClassesOf( cl );
    }

    public Set ancestorClassesOf( OWLClass cl ) throws OWLException {
	return simpleReasoner.ancestorClassesOf( cl );
    }

    public Set descendantClassesOf( OWLClass cl ) throws OWLException {
	return simpleReasoner.descendantClassesOf( cl );
    }

    /* Returns the collection of (named) classes which are equivalent
     * to the given description. */

    public Set equivalentClassesOf( OWLClass cl ) throws OWLException {
	return simpleReasoner.equivalentClassesOf( cl );
    }

    public static void main( String[] args ) {
	try {
	    org.apache.log4j.BasicConfigurator.configure();

	    /* Get hold of a reasoner. Here we're using the HTTP
	     * interface which makes it relatively easy to swap
	     * between RACER/FaCT. Assumes that there is a reasoner
	     * available at the given URL. */
	    
	    Reasoner digReasoner = 
 		new uk.ac.man.cs.img.dig.reasoner.impl.HTTPReasoner(args[0]);
	    
	    SimpleOWLTaxonomyReasoner reasoner = new SimpleOWLTaxonomyReasoner( digReasoner );

	    /* Get hold of an ontology object */
	    java.net.URI uri = new java.net.URI( args[1] );
	    OWLOntology onto = OntologyHelper.getOntology( uri );

	    /* Pass it to the reasoner */
	    reasoner.setOntology( onto );

	    /* Now request the hierarchy (as a new ontology) */
	    OWLOntology hierarchy = 
		InferenceHelper.hierarchyAsNewOntology( reasoner );

	    Renderer renderer = new org.semanticweb.owl.io.abstract_syntax.Renderer();
	    Writer writer = new StringWriter();
	    renderer.renderOntology( hierarchy, writer );
	    System.out.println( writer.toString() );
	    
	} catch ( SimpleOWLReasoner.ExpressivenessOutOfScopeException ex ) {
	    System.out.println( ex.getMessage() );
	} catch ( Exception ex ) {
	    //System.out.println( ex.getMessage() );
	    ex.printStackTrace();
	}
    }

    private void notImplemented(String message) throws OWLException {
	throw new OWLException( message + ": Not yet implemented" );
    }
    
} // SimpleOWLTaxonomyReasoner



/*
 * ChangeLog
 * $Log: SimpleOWLTaxonomyReasoner.java,v $
 * Revision 1.2  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.3  2003/07/09 13:58:30  bechhofers
 * docs update
 *
 * Revision 1.2  2003/07/08 18:01:07  bechhofers
 * Presentation Servlet and additional documentation regarding inference.
 *
 * Revision 1.1  2003/07/08 13:23:50  bechhofers
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
