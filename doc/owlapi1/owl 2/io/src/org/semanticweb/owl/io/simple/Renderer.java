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

package org.semanticweb.owl.io.simple; 
import org.semanticweb.owl.io.RendererException; 
import org.semanticweb.owl.model.OWLOntology;
import java.io.Writer;
import java.util.Iterator;
import org.semanticweb.owl.model.OWLNamedObject;
import java.io.PrintWriter;
import java.util.Map;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLException;
import java.util.Set;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividualAxiom;

// Generated package name


/**
 * A very simple renderer, primarily for test purposes.
 *
 * Created: Thu Dec 19 15:58:42 2002
 *
 * @author Sean Bechhofer
 * @version $Id: Renderer.java,v 1.2 2005/06/10 12:20:32 sean_bechhofer Exp $
 */

public class Renderer implements  org.semanticweb.owl.io.Renderer
{

    private Map options;

    private DescriptionRenderer dr;
    private PrintWriter pw;

    public Renderer() {
	dr = new DescriptionRenderer();
    }
    
    /** Prints a very simple rendering of the ontology to the writer,
     * listing all the classes, properties and individuals defined in
     * the ontology. */
    public void renderOntology( OWLOntology ontology,
				Writer writer ) throws RendererException {
	try {
	    pw = new PrintWriter( writer );
	    
	    pw.println("Ontology: " + ontology.getURI().toString());
	    pw.println("=====================================================");
	    pw.println("Classes:");
	    for (Iterator it = ontology.getClasses().iterator();
		 it.hasNext();) {
		OWLClass clazz = (OWLClass) it.next();
		renderClass( clazz, ontology );
	    }
	    
	    pw.println();
	    pw.println("Object Properties:");
	    for (Iterator it = ontology.getObjectProperties().iterator();
		 it.hasNext();) {
		OWLObjectProperty prop = (OWLObjectProperty) it.next();
		renderObjectProperty( prop, ontology );
	    }
	    
	    pw.println();
	    pw.println("Data Properties:");
	    for (Iterator it = ontology.getDataProperties().iterator();
		 it.hasNext();) {
		OWLDataProperty prop = (OWLDataProperty) it.next();
		renderDataProperty( prop, ontology );
	    }
	    
	    pw.println();
	    pw.println("Individuals:");
	    for (Iterator it = ontology.getIndividuals().iterator();
		 it.hasNext();) {
		OWLIndividual ind = (OWLIndividual) it.next();
		renderIndividual( ind, ontology );
	    }
	    
	    pw.println();
	    pw.println("Axioms:");
	    for (Iterator it = ontology.getClassAxioms().iterator();
		 it.hasNext();) {
		renderClassAxiom( (OWLClassAxiom) it.next() );
	    }
	} catch (OWLException ex) {
	    throw new RendererException( ex.getMessage() );
	}
    }

    /**
     * Set options.
     * @param options a <code>Map</code> value. Should contain a map from {@link String String}s to {@link String String}s.
     */
    public void setOptions( Map options ) {
    };

    /**
     * 
     * Get options.
     * @return a <code>Map</code> value. Contains a map from {@link String String}s to {@link String String}s.
     */
    public Map getOptions() {
	return options;
    }

    private void renderClass( OWLClass clazz, OWLOntology onto ) throws OWLException {
	dr.reset();
	clazz.accept( dr );
	pw.println("\t" +  dr.getContents() );
	for ( Iterator cit = clazz.getSuperClasses( onto ).iterator(); 
	      cit.hasNext(); ) {
	    dr.reset();
	    ((OWLObject) cit.next()).accept( dr );
	    pw.println("\t\t" + dr.getContents() );
	    
	}
	for ( Iterator cit = clazz.getEquivalentClasses( onto ).iterator(); 
	      cit.hasNext(); ) {
	    pw.print("\t\t" );
	    dr.reset();
	    ((OWLObject) cit.next()).accept( dr );
	    pw.println("\t\t" + dr.getContents() );
	    
	}
	for ( Iterator cit = clazz.getEnumerations( onto ).iterator(); 
	      cit.hasNext(); ) {
	    pw.print("\t\t" );
	    dr.reset();
	    ((OWLObject) cit.next()).accept( dr );
	    pw.println("\t\t" + dr.getContents() );
	    
	}
    }
    
    private void renderObjectProperty( OWLObjectProperty oprop, OWLOntology onto ) throws OWLException {
	dr.reset();
	oprop.accept( dr );
	pw.println("\t" +  dr.getContents() );
    }
    private void renderDataProperty( OWLDataProperty dprop, OWLOntology onto ) throws OWLException {
	dr.reset();
	dprop.accept( dr );
	pw.println("\t" +  dr.getContents() );
    }

    private void renderIndividual( OWLIndividual individual, OWLOntology onto ) throws OWLException {
	dr.reset();
	individual.accept( dr );
	pw.println( "\t" + dr.getContents() );
	
	for ( Iterator cit = individual.getTypes( onto ).iterator(); 
	      cit.hasNext(); ) {
	    dr.reset();
	    ((OWLObject) cit.next()).accept( dr );
	    pw.println("\t\t" +  dr.getContents() );
	}
	for ( Iterator cit = individual.getObjectPropertyValues( onto ).keySet().iterator(); 
	      cit.hasNext(); ) {
	    OWLObject k = (OWLObject) cit.next();
	    dr.reset();
	    k.accept( dr );
	    pw.println("\t\t" + dr.getContents());
	    for ( Iterator vit = ((Set) individual.getObjectPropertyValues( onto ).get( k )).iterator();
		  vit.hasNext(); ) {
		OWLObject v = (OWLObject) vit.next();
		dr.reset();
		v.accept( dr );
		pw.println("\t\t\t" + dr.getContents());
	    }
	}
    }
    private void renderClassAxiom( OWLClassAxiom axiom ) throws OWLException {
	dr.reset();
	axiom.accept( dr );
	pw.println("\t" +  dr.getContents() );
    }

    private void renderPropertyAxiom( OWLPropertyAxiom axiom ) throws OWLException {
    }
    private void renderIndiviualAxiom( OWLIndividualAxiom axiom ) throws OWLException {
    }
    

} // Renderer



/*
 * ChangeLog
 * $Log: Renderer.java,v $
 * Revision 1.2  2005/06/10 12:20:32  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.4  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.3  2003/03/27 19:51:54  seanb
 * Various changes.
 *
 * Revision 1.2  2003/02/17 18:23:53  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 */
