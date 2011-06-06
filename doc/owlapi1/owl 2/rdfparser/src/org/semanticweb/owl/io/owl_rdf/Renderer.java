package org.semanticweb.owl.io.owl_rdf;

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
 * Last modified on   $Date: 2006/03/28 16:14:46 $
 *               by   $Author: ronwalf $
 ****************************************************************/

import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.owl_rdf.RenderingConstants;
import org.semanticweb.owl.io.owl_rdf.RenderingVisitor;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataRange;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNamedObject;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.helper.OntologyHelper;

//import org.semanticweb.owl.io.abstract_syntax.*;
/** Produces OWL/RDF Syntax from a given ontology
 *  @author Raphael Volz, volz@fzi.de
 */
public class Renderer implements org.semanticweb.owl.io.Renderer, RenderingConstants {

    protected OWLOntology ontology;
    protected PrintWriter pw;
    protected Set allURIs;
    protected int reservedNames;
    protected RenderingVisitor visitor;
    protected QNameShortFormProvider qnameProvider;

    public Renderer() {

    }

    public void setOptions(Map map) {
    }

    public Map getOptions() {
        return null;
    }

    public void renderOntology(OWLOntology ontology, Writer writer)
        throws RendererException {
        this.ontology = ontology;
        try {
            this.pw = new PrintWriter(writer);
            this.allURIs = OntologyHelper.allURIs(ontology);
            this.visitor = new RenderingVisitor(this);
	    this.qnameProvider = new QNameShortFormProvider();

            generateShortNames();
            writeHeader();

            for (Iterator it =
                orderedEntities(ontology.getClasses()).iterator();
                it.hasNext();
                ) {
                renderClass(ontology, (OWLClass) it.next());
            }

            for (Iterator it =
                orderedEntities(ontology.getObjectProperties()).iterator();
                it.hasNext();
                ) {
                renderObjectProperty(ontology, (OWLObjectProperty) it.next());
            }
            pw.println();
            for (Iterator it =
                orderedEntities(ontology.getDataProperties()).iterator();
                it.hasNext();
                ) {
                renderDataProperty(ontology, (OWLDataProperty) it.next());
            }
            pw.println();
            for (Iterator it =
                orderedEntities(ontology.getIndividuals()).iterator();
                it.hasNext();
                ) {
                renderIndividual(ontology, (OWLIndividual) it.next());
            }
            pw.println();
            for (Iterator it =
                orderedEntities(ontology.getClassAxioms()).iterator();
                it.hasNext();
                ) {
                renderClassAxiom((OWLClassAxiom) it.next());
            }
            
            for (Iterator it =
                orderedEntities(ontology.getPropertyAxioms()).iterator();
                it.hasNext();
                ) {
                renderPropertyAxiom((OWLPropertyAxiom) it.next());
            }
	    writeExtensions();
	    writeFooter();
	    pw.flush();
        } catch (OWLException ex) {
            throw new RendererException(ex.getMessage());
        }
	
    }

    /** This method allows us to extend the renderer and add extra
     * information. */
    protected void writeExtensions() throws OWLException {
    }

    /**
     * Method renderPropertyAxiom.
     * @param oWLPropertyAxiom
     */
    protected void renderPropertyAxiom(OWLPropertyAxiom axiom) throws OWLException{
        visitor.reset();
	visitor.setLevel( 0 );
        axiom.accept(visitor);
        pw.println(visitor.result());
    }


    /**
     * Method writeFooter.
     */
    protected void writeFooter() {
        pw.println("</rdf:RDF>");
    }

    protected void renderClass(OWLOntology ontology, OWLClass clazz)
        throws OWLException {
        boolean done = false;
        pw.println("<owl:Class rdf:about=\"" + clazz.getURI().toString() + "\">");
        for (Iterator it = clazz.getEquivalentClasses( ontology ).iterator();
	     it.hasNext();
	     ) {
            OWLDescription eq = (OWLDescription) it.next();
            visitor.reset();
	    visitor.setLevel( 2 );
            eq.accept(visitor);
            pw.println( INDENT + "<owl:equivalentClass>");
	    pw.print(visitor.result());
	    pw.println( INDENT + "</owl:equivalentClass>");
            done = true;
        }

        if (!clazz.getSuperClasses( ontology ).isEmpty()) {

            for (Iterator it = clazz.getSuperClasses( ontology ).iterator();
                it.hasNext();
                ) {
                OWLDescription eq = (OWLDescription) it.next();
                visitor.reset();
		visitor.setLevel( 2 );
                eq.accept(visitor);
                pw.println( INDENT + "<rdfs:subClassOf>");
		pw.print(visitor.result());
		pw.println( INDENT + "</rdfs:subClassOf>");

                done = true;
            }
        }
        for (Iterator it = clazz.getEnumerations( ontology ).iterator(); it.hasNext();) {
	    /* Should be an enumeration! */
            OWLEnumeration enum_ = (OWLEnumeration) it.next();
	    pw.println( INDENT + "<owl:oneOf rdf:parseType=\"Collection\">");
	    for (Iterator indIt = enum_.getIndividuals().iterator();indIt.hasNext();) {
		OWLIndividual desc = (OWLIndividual) indIt.next();
		visitor.reset();
		visitor.setLevel( 2 );
		desc.accept(visitor);
		pw.print(visitor.result());
	    }
	    pw.println( INDENT + "</owl:oneOf>");
        }
        pw.println("</owl:Class>");
	renderAnnotations( ontology, clazz );
    }

    protected void renderIndividual(OWLOntology ontology, OWLIndividual ind)
			throws OWLException {
		if (ind.getTypes(ontology).isEmpty()
				&& ind.getObjectPropertyValues(ontology).keySet().isEmpty()
				&& ind.getDataPropertyValues(ontology).keySet().isEmpty()) {

			pw.println("<owl:Thing rdf:about=\"" + ind.getURI() + "\" />");

		} else {
			pw.println("<rdf:Description rdf:about=\"" + ind.getURI() + "\">");
			// Instantiation
			for (Iterator it = ind.getTypes(ontology).iterator(); it.hasNext();) {

				OWLDescription eq = (OWLDescription) it.next();
				//    pw.println(" Class(" + shortForm(clazz.getURI()) + " complete
				// ");
				visitor.reset();
				visitor.setLevel(2);
				eq.accept(visitor);
				pw.println(INDENT + "<rdf:type>");
				pw.print(visitor.result());
				pw.println(INDENT + "</rdf:type>");
			}

			// Object Property Values
			Map propertyValues = ind.getObjectPropertyValues(ontology);
			for (Iterator it = propertyValues.keySet().iterator(); it.hasNext();) {
				OWLObjectProperty prop = (OWLObjectProperty) it.next();
				Set vals = (Set) propertyValues.get(prop);
				for (Iterator valIt = vals.iterator(); valIt.hasNext();) {
					OWLIndividual oi = (OWLIndividual) valIt.next();
					pw.println(INDENT + "<" + shortForm(prop.getURI())
							+ " rdf:resource=\""
							//			       + shortForm(oi.getURI())
							+ oi.getURI() + "\"/>");
				}
				// 		if (it.hasNext()) {
				// 		    pw.println();
				// 		}
			}
			// Datatype Properties
			Map dataValues = ind.getDataPropertyValues(ontology);
			for (Iterator it = dataValues.keySet().iterator(); it.hasNext();) {
				OWLDataProperty prop = (OWLDataProperty) it.next();
				Set vals = (Set) dataValues.get(prop);
				for (Iterator valIt = vals.iterator(); valIt.hasNext();) {
					OWLDataValue dtv = (OWLDataValue) valIt.next();
					pw.print(INDENT + "<" + shortForm(prop.getURI()));
					if (dtv.getURI() != null) {
						pw.print(" rdf:datatype=\"" + dtv.getURI() + "\"");
					}
					if (dtv.getLang() != null) {
						pw.print(" xml:lang=\"" + dtv.getLang() + "\"");
					}
					pw.print(">");

					pw.print(sanitize(dtv.getValue().toString()));
					pw.println("</" + shortForm(prop.getURI()) + ">");
				}
			}
			pw.println("</rdf:Description>");
		}
		renderAnnotations(ontology, ind);
	}

    protected void renderObjectProperty (
        OWLOntology ontology,
        OWLObjectProperty prop) throws OWLException {
        pw.println(
            "<owl:ObjectProperty rdf:about=\""
                + prop.getURI().toString()
                + "\">");
        if (prop.isTransitive(ontology)) {
            pw.println( INDENT + 
                "<rdf:type rdf:resource=\"&owl;TransitiveProperty\" />");
        }
        if (prop.isFunctional(ontology)) {
            pw.println( INDENT + 
                "<rdf:type rdf:resource=\"&owl;FunctionalProperty\" />");
        }
        if (prop.isInverseFunctional(ontology)) {
            pw.println( INDENT + 
                "<rdf:type rdf:resource=\"&owl;InverseFunctionalProperty\" />");
        }
        if (prop.isSymmetric(ontology)) {
            pw.println( INDENT + 
                "<rdf:type rdf:resource=\"&owl;SymmetricProperty\" />");
        }
        for (Iterator iter = prop.getDomains(ontology).iterator();
            iter.hasNext();
            ) {
            OWLDescription eq = (OWLDescription) iter.next();
            visitor.reset();
	    visitor.setLevel( 2 );
            eq.accept(visitor);
            pw.println( INDENT + "<rdfs:domain>");
	    pw.print( visitor.result() ); 
	    pw.println( INDENT + "</rdfs:domain>");
        }
        for (Iterator iter = prop.getRanges(ontology).iterator();
            iter.hasNext();
            ) {
            OWLDescription eq = (OWLDescription) iter.next();
            visitor.reset();
	    visitor.setLevel( 2 );
            eq.accept(visitor);

            pw.println( INDENT + "<rdfs:range>");
	    pw.print( visitor.result() ); 
	    pw.println( INDENT + "</rdfs:range>");
        }
        for (Iterator iter = prop.getInverses(ontology).iterator();
            iter.hasNext();
            ) {
            OWLNamedObject element = (OWLNamedObject) iter.next();
            pw.println( INDENT + 
			"<owl:inverseOf rdf:resource=\""
			+ element.getURI().toString()
			+ "\" />");

        }
        for (Iterator iter = prop.getSuperProperties(ontology).iterator();
            iter.hasNext();
            ) {
            OWLNamedObject element = (OWLNamedObject) iter.next();
            pw.println( INDENT + 
			"<rdfs:subPropertyOf rdf:resource=\""
			+ element.getURI().toString()
			+ "\" />");
	    
        }
	/* No equivalent properties on prop....*/
//         for (Iterator iter = prop.getEquivalentProperties(ontology).iterator();
//             iter.hasNext();
//             ) {
//             OWLNamedObject element = (OWLNamedObject) iter.next();
//             pw.println(
//                 "<owl:equivalentProperty rdf:resource=\""
//                     + element.getURI().toString()
//                     + "\" />");

//         }
        pw.println("</owl:ObjectProperty>");
	renderAnnotations( ontology, prop );
    }

    protected void renderDataProperty(
        OWLOntology ontology,
        OWLDataProperty prop) throws OWLException {
        pw.println("<owl:DatatypeProperty rdf:about=\"" + prop.getURI() + "\">");
        if (prop.isFunctional(ontology)) {
            pw.println( INDENT + "<rdf:type rdf:resource=\"&owl;FunctionalProperty\" />");
        }
        for (Iterator iter = prop.getDomains(ontology).iterator();
            iter.hasNext();
            ) {
            OWLDescription eq = (OWLDescription) iter.next();
            visitor.reset();
	    visitor.setLevel( 2 );
            eq.accept(visitor);

            pw.println( INDENT + "<rdfs:domain>" + 
		       visitor.result() + 
		       "</rdfs:domain>");
        }
        for (Iterator iter = prop.getRanges(ontology).iterator();
            iter.hasNext();
            ) {
            OWLDataRange eq = (OWLDataRange) iter.next();
            visitor.reset();
	    visitor.setLevel( 2 );
            eq.accept(visitor);

            pw.println( INDENT + "<rdfs:range>" + 
		       visitor.result() + 
		       "</rdfs:range>");
        }
        for (Iterator iter = prop.getSuperProperties(ontology).iterator();
            iter.hasNext();
            ) {
            OWLNamedObject element = (OWLNamedObject) iter.next();
            pw.println( INDENT + 
			"<rdfs:subPropertyOf rdf:resource=\""
			+ element.getURI().toString()
			+ "\" />");
	    
        }
        pw.println("</owl:DatatypeProperty>");
	renderAnnotations( ontology, prop );

    }


    /** Render the annotations for an object */
    protected void renderAnnotations( OWLOntology ontology,
				    OWLNamedObject object ) throws OWLException {
	int i = 0;
	if ( !object.getAnnotations( ontology ).isEmpty() ) {
            pw.println("<rdf:Description rdf:about=\"" + object.getURI() + "\">");
	    for ( Iterator it = object.getAnnotations( ontology ).iterator();
		  it.hasNext(); ) {
		OWLAnnotationInstance oai = (OWLAnnotationInstance) it.next();
		OWLAnnotationProperty prop = oai.getProperty();
		Object content = oai.getContent();
		/* Bad, Bad, Bad! */
		if (content instanceof OWLIndividual) {
                    pw.println( INDENT + 
				"<"
				+ shortForm(prop.getURI())
				+ " rdf:resource=\""
				//				+ shortForm(((OWLIndividual) content).getURI())
				+ ((OWLIndividual) content).getURI()
				+ "\"/>");
		} else if (content instanceof URI) {
                    pw.println( INDENT + 
				"<"
				+ shortForm(prop.getURI())
				+ " rdf:resource=\""
				//				+ shortForm(((URI) content))
				+ ((URI) content)
				+ "\"/>");
		} else if (content instanceof OWLDataValue) {
		    OWLDataValue odv = (OWLDataValue) content;
                    pw.print( INDENT + 
			      "<"
			      + shortForm(prop.getURI()));
		    if (odv.getURI()!=null) {
			pw.print(" rdf:datatype=\""
				 + odv.getURI()+ "\"");
		    }
		    if (odv.getLang()!=null) {
			pw.print(" xml:lang=\""
				 + odv.getLang() + "\"");
		    }
		    pw.print(">");
		    /* Needs some kind of XML escaping/CDATA section? */
		    pw.print(odv.getValue().toString());
		    pw.println("</"+ shortForm(prop.getURI()) + ">");
		} else {
		    /* Don't really know what to do here....*/
		    pw.println( INDENT + 
				"<"
				+ shortForm(prop.getURI())
				+ ">" + content.toString() + "</" 
				+ shortForm(prop.getURI())
				+ ">");
		}
	    }
	    pw.println("</rdf:Description>");
	}
    }
    
    protected void renderClassAxiom(OWLClassAxiom axiom) throws OWLException {
        visitor.reset();
	visitor.setLevel( 0 );
        axiom.accept(visitor);
        pw.print(visitor.result());
    }

    protected void generateShortNames() {
        /* Generates a list of namespaces. */
        
        for (Iterator it = allURIs.iterator(); it.hasNext();) {
 	    URI uri = (URI) it.next();
	    String qname = qnameProvider.shortForm(uri, false);
        }
    }

    protected void writeHeader() {
        pw.println("<?xml version=\"1.0\"?>");
        pw.println("<!DOCTYPE owl [");
        pw.println("<!ENTITY owl  \"http://www.w3.org/2002/07/owl#\">");
        pw.println("<!ENTITY xsd  \"http://www.w3.org/2001/XMLSchema#\">");
        pw.println(
            "<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">");
        pw.println("<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\">");
        pw.println("]>");
        pw.println("<rdf:RDF");
        // System names
        for (Iterator it = qnameProvider.getPrefixSet().iterator(); it.hasNext();) {
	    String prefix = (String) it.next();
            String ns = qnameProvider.getURI(prefix);
            pw.println(" xmlns:" + prefix + "=\"" + ns + "\"");
        }
        pw.println(">");
        // owl:Ontology Header
//        pw.println(
//            "<!-- Need to support further ontology metadata, import still missing -->");
        
        //*** Added ontology header information: 10/03/04
        // add ontology annotations
        try {
        	pw.println("<owl:Ontology rdf:about=\""+ontology.getLogicalURI().toString()+"\">");
	        if(ontology.getAnnotations() != null) {
	            Set annotSet = ontology.getAnnotations();
	            Iterator iter = annotSet.iterator();
	            while(iter.hasNext()) {
	                OWLAnnotationInstance annot = (OWLAnnotationInstance) iter.next();
	                OWLAnnotationProperty prop = annot.getProperty();
	                String propURI = prop.getURI().toString();
	                String propLbl = "";
	                if (propURI.equals("http://www.w3.org/2000/01/rdf-schema#comment")) propLbl = "rdfs:comment";
	                else if (propURI.equals("http://www.w3.org/2000/01/rdf-schema#label")) propLbl = "rdfs:label";
	                else if (propURI.equals("http://www.w3.org/2002/07/owl#versionInfo")) propLbl = "owl:versionInfo";
	                String value = annot.getContent().toString();
	                if (!propLbl.equals("")) {
	                	pw.println("<"+propLbl+">"+value+"</"+propLbl+">");
	                }
	            }
	        }
	        // add imported ontologies
	        if(ontology.getIncludedOntologies() != null) {
	            Set inclOntSet = ontology.getIncludedOntologies();
	            Iterator iter = inclOntSet.iterator();
	            while (iter.hasNext()) {
	            	OWLOntology impOnt = (OWLOntology) iter.next();
	            	pw.println("<owl:imports rdf:resource=\""+impOnt.getURI().toString()+"\"/>");
	            }
	        }
	        pw.println("</owl:Ontology>");
        }
        catch (OWLException e) {
        	e.printStackTrace();
        }
        //*** 
		
//	String lu = "";
//	try {
//	    lu = ontology.getLogicalURI().toString();
//	} catch (OWLException ex) {
//	}
//        pw.println(
//            "<owl:Ontology rdf:about=\""
//	    + lu
//	    + "\"/>");
    }

    protected String sanitize(String str) {
    		StringBuffer result = new StringBuffer();
    		char str_chars[] = str.toCharArray();
    		for (int index=0; index < str_chars.length; index++) {
    			char character = str_chars[index];
    			if (character=='&') 
    				result.append("&amp;");
    			else if (character=='<')
    				result.append("&lt;");
    			else
    				result.append(character);
    		}
    		return result.toString();
    }
    
    public String shortForm(URI uri) throws RendererException {
        String qname = qnameProvider.shortForm(uri, false);
	if (qname == null) {
	    throw new RendererException("Cannot give a qname to a URI that ends in a non-name character");
	}
	return qname;
    }

    /* Return a collection, ordered by the URIs. */
    protected SortedSet orderedEntities(Set entities) {
        SortedSet ss = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                try {
                    return ((OWLEntity) o1).getURI().toString().compareTo(
                        ((OWLEntity) o2).getURI().toString());
                } catch (Exception ex) {
                    return o1.toString().compareTo(o2.toString());
                }
            }
        });
        ss.addAll(entities);
        return ss;
    }

    public static void main(String args[]) {
        /*if (args.length > 0)
            try {
                URI uri = new URI(args[0]);
		OWLConnection connection = null;
		try {
		    connection = OWLManager.getOWLConnection();
		} catch ( OWLException e ) {
		    System.err.println("Could not obtain connection:");
		    System.err.println( e.getMessage());
		    System.exit(-1);
		}
		
                OWLRDFParser parser = new OWLRDFParser();
                OWLOntology onto = connection.createOntology( uri,uri );
                parser.parseOntology(onto, uri);
                Renderer renderer = new Renderer();
                Writer writer = new StringWriter();
                renderer.renderOntology(onto, writer);
                System.out.println(writer.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Error: " + ex.getMessage());
		}*/
    }
} // Renderer

/*
 * ChangeLog
 * $Log: Renderer.java,v $
 * Revision 1.14  2006/03/28 16:14:46  ronwalf
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
 * Revision 1.13  2004/10/25 17:46:48  aditkal
 * Added files to make new RDF/XML Renderer work properly --- requiring proper generation of QNames for URI's
 *
 *
 * Revision 1.12 2004/10/04 aditya_kalyanpur
 * Added Ontology Header information (especially owl:imports) 
 * 
 * Revision 1.11  2004/06/01 14:31:58  sean_bechhofer
 * Minor Rendering fix (missing quote)
 *
 * Revision 1.10  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.9  2004/03/08 17:27:18  sean_bechhofer
 * Removing references to deprecated methods.
 *
 * Revision 1.8  2004/02/26 09:26:49  sean_bechhofer
 * flushing writer.
 *
 * Revision 1.7  2004/02/18 11:10:26  sean_bechhofer
 * Minor fix to data range.
 *
 * Revision 1.6  2004/02/05 17:55:50  sean_bechhofer
 * Minor fixes.
 *
 * Revision 1.5  2004/02/05 17:36:19  sean_bechhofer
 * RDF/XML Rendering support. Still some minor holes, but almost there.
 *
 * Revision 1.4  2004/02/05 11:48:11  sean_bechhofer
 * Changes to OWLConsumer to improve efficiency in handling large
 * file validation.
 *
 * Revision 1.3  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.2  2003/12/03 16:24:13  sean_bechhofer
 * Minor updates -- this still needs work.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:18  sean_bechhofer
 * Initial Import
 *
 * Revision 1.11  2003/10/03 16:06:41  bechhofers
 * Refactoring of source and tests to break dependencies on implementation
 * classes.
 *
 * Revision 1.10  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.9  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.8  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.7  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.6  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.5  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.4  2003/04/10 12:15:28  rvolz
 * Removed/Added in refactoring process
 *
 * Revision 1.3  2003/03/27 19:51:54  seanb
 * Various changes.
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
