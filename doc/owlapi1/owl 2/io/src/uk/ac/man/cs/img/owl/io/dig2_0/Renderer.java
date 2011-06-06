/*
 * Copyright (C) 2006 The University of Manchester 
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
 * Revision           $Revision: 1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/20 13:09:27 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.dig2_0; // Generated package name

/**
 * Produces DIG 2.0 XML from OWL ontologies. 
 *
 * @author Sean Bechhofer
 * @version $Id: Renderer.java,v 1.1 2006/03/20 13:09:27 sean_bechhofer Exp $
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
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.model.OWLPropertyAxiom;

public class Renderer implements org.semanticweb.owl.io.Renderer
{

    /* Names for top and bottom */
    public static final String TOP = "http://dl.kr.org/dig/lang/schema#top";
    public static final String BOTTOM = "http://dl.kr.org/dig/lang/schema#bottom";

    private PrintWriter pw;
    private RenderingVisitor visitor;
    
    private List anonymousIndividuals;
    
    public Renderer()
    {
	visitor = new RenderingVisitor();
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

	    pw.println("<?xml version='1.0' encoding='UTF-8'?>");
	    pw.println("<axioms xmlns='http://dl.kr.org/dig/lang/schema'");
	    pw.println("xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'");
	    pw.println("xsi:schemaLocation='http://dl.kr.org/dig/lang/schema http://www.cs.man.ac.uk/~seanb/dig/schema.xsd' uri='urn:whatever'>");

	    Set allOntologies = OntologyHelper.importClosure( ontology );
	    for ( Iterator allIt = allOntologies.iterator();
		  allIt.hasNext(); ) {

		OWLOntology ontologyToProcess = (OWLOntology) allIt.next();
		
 		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getClasses() ).iterator();
		      it.hasNext(); ) {
		    renderClass( ontologyToProcess, ( OWLClass ) it.next() );
		}
		pw.println();
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getObjectProperties() ).iterator();
		      it.hasNext(); ) {
		    renderObjectProperty( ontologyToProcess, ( OWLObjectProperty ) it.next() );
		}
		pw.println();
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getDataProperties() ).iterator();
		      it.hasNext(); ) {
		    renderDataProperty( ontologyToProcess, ( OWLDataProperty ) it.next() );
		}
		pw.println();
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getIndividuals() ).iterator();
		      it.hasNext(); ) {
		    renderIndividual( ontologyToProcess, ( OWLIndividual ) it.next() );
		}
		pw.println();
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getDatatypes() ).iterator();
		      it.hasNext(); ) {
		    renderDataType( ontologyToProcess, ( OWLDataType ) it.next() );
		}
		pw.println();
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getClassAxioms() ).iterator();
		      it.hasNext(); ) {
		    renderClassAxiom( ( OWLClassAxiom ) it.next() );
		} 
		pw.println();
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getPropertyAxioms() ).iterator();
		      it.hasNext(); ) {
		    renderPropertyAxiom( ( OWLPropertyAxiom ) it.next() );
		} 
		pw.println();
		for ( Iterator it = 
			  orderedEntities( ontologyToProcess.getIndividualAxioms() ).iterator();
		      it.hasNext(); ) {
		    renderIndividualAxiom( ( OWLIndividualAxiom ) it.next() );
		} 
	    }

	    pw.println("</axioms>");

	} catch (OWLException ex) {
	    throw new RendererException( ex.getMessage() );
	}
    }

    private void renderClass( OWLOntology ontology,
			      OWLClass clazz ) throws OWLException {
	if ( !isTop( clazz.getURI() ) && !isTop( clazz.getURI() ) ) {
	    pw.println("<defClass URI=\"" + clazz.getURI() + "\"/>");
	}
	
	for ( Iterator it = clazz.getEquivalentClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println( "<equivalentClass>");
	    pw.println( "<class URI=\"" + mapName( clazz.getURI() ) + "\"/>");
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "" + visitor.result() );
	    pw.println( "</equivalentClass>");
	}
	
	for ( Iterator it = clazz.getSuperClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println( "<subClass>");
	    pw.println( "<class URI=\"" + mapName( clazz.getURI() ) + "\"/>");
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "" + visitor.result() );
	    pw.println( "</subClass>");
	}

	for ( Iterator it = clazz.getEnumerations( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println( "<equivalentClass>");
	    pw.println( "<class URI=\"" + mapName( clazz.getURI() ) + "\"/>");
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "" + visitor.result() );
	    pw.println( "</equivalentClass>");
	}
    }
    
    private void renderIndividual( OWLOntology ontology,
				   OWLIndividual ind ) throws OWLException {
	/* If the individual is anonymous and has any incoming
	 * properties, then we do not wish to show it here -- it will
	 * be rendered during the rendering of the thing that points
	 * to it. */

	/* This is all a bit tricky if we're doing DIG.... */
	if (ind.isAnonymous()) {
	    Map m = ind.getIncomingObjectPropertyValues( ontology ); 
	    if ( !m.isEmpty() ) {
		return;
	    }
	} 

	String indReference = "";
	if (ind.isAnonymous()) {
	    /* Almighty hack... Assumes that the hashCodes will always
	     * return different things....*/
	    indReference = "anon-" + ind.hashCode();
	} else {
	    indReference = mapName( ind.getURI() );
	}

	pw.println("<defIndividual URI=\"" + indReference + "\"/>" );
	
	for ( Iterator it = ind.getTypes( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println("<instanceOf>");
	    pw.println("<individual URI=\"" + indReference + "\"/>" );
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "" + visitor.result() );
	    pw.println("</instanceOf>");

	}
	Map propertyValues = ind.getObjectPropertyValues( ontology );
	for ( Iterator it = propertyValues.keySet().iterator();
	      it.hasNext(); ) {
	    OWLObjectProperty prop = (OWLObjectProperty) it.next();
	    Set vals = (Set) propertyValues.get(prop);
	    for (Iterator valIt = vals.iterator(); valIt.hasNext();
		 ) {
		pw.println("<related>");
		pw.println("<individual URI=\"" + indReference + "\"/>" );

		pw.println( "<objectProperty URI=\"" + mapName( prop.getURI() ) + "\"/>" );
		
		OWLIndividual oi = (OWLIndividual) valIt.next();
		if (oi.isAnonymous()) {
		    pw.println("<individual URI=\"anon-" + oi.hashCode() + "\"/>" );
		} else {
		    pw.println("<individual URI=\"" + mapName( oi.getURI() ) + "\"/>" );
		}
		pw.println("</related>");

	    }
	}

	Map dataValues = ind.getDataPropertyValues( ontology );
	for ( Iterator it = dataValues.keySet().iterator();
	      it.hasNext(); ) {
	    OWLDataProperty prop = (OWLDataProperty) it.next();
	    Set vals = (Set) dataValues.get(prop);
	    for (Iterator valIt = vals.iterator(); valIt.hasNext();
		 ) {
		boolean isString = false;
		boolean isInteger = false;
		OWLDataValue dv = (OWLDataValue) valIt.next();

// 		/* If there's no URI given, assume it's a string */
// 		isString = (dv.getURI()==null);

// 		if ( !isString ) {
// 		    /* There is a uri given. */
// 		    if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getString() ) ) {
// 			isString = true;
// 		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInteger() ) ) {
// 			isInteger = true;
// 		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getPositiveInteger() ) ) {
// 			isInteger = true;
// 		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getNegativeInteger() ) ) {
// 			isInteger = true;
// 		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getNonNegativeInteger() ) ) {
// 			isInteger = true;
// 		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getNonPositiveInteger() ) ) {
// 			isInteger = true;
// 		    }
// 		}

// 		/* At this point if neither is set, we give up as
// 		 * we've got a type we can't deal with. */
// 		if (!(isString || isInteger) ) {
// 		    throw new RendererException( "Unknown Datatype: " + dv.getURI() );
// 		}
		
		pw.println("<value>");
		pw.println("<individual URI=\"" + indReference + "\"/>" );
		/* There is a problem here -- by default attributes
		 * have integer range, but we're not necessarily
		 * handling that properly here. */
		pw.println( "<dataProperty URI=\"" + mapName( prop.getURI() ) + "\"/>" );
		pw.println( "<dataLiteral>" + dv.getValue() + "</dataLiteral>" );
		pw.println("</value>");
	    }
	}

    }

    private void renderObjectProperty( OWLOntology ontology,
				       OWLObjectProperty prop ) throws OWLException {
	pw.println("<defObjectProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	if ( prop.isTransitive( ontology ) ) {
	    pw.println("<transitive>");
	    pw.println("<objectProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	    pw.println("</transitive>");
	}
	if ( prop.isFunctional( ontology ) ) {
	    pw.println("<functional>");
	    pw.println("<objectProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	    pw.println("</functional>");
	}
	/* Not sure about these.... */
	if ( prop.isInverseFunctional( ontology ) ) {
	}
	if ( prop.isSymmetric( ontology ) ) {
	}
	/****************************/

	for ( Iterator it = prop.getInverses( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<equivalentProperty>");
	    pw.println("<objectProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	    
	    OWLObjectProperty inv = (OWLObjectProperty) it.next();
	    pw.println("<inverse>" );
	    pw.println("<objectProperty URI=\"" + mapName( inv.getURI() ) + "\"/>");
	    pw.println("</inverse>" );
	    pw.println("</equivalentProperty>");
	}	    

	for ( Iterator it = prop.getSuperProperties( ontology ).iterator();
	      it.hasNext(); ) {

	    pw.println("<subProperty>");
	    pw.println("<objectProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	    OWLObjectProperty sup = (OWLObjectProperty) it.next();
	    pw.println("<objectProperty URI=\"" + mapName( sup.getURI() ) + "\"/>");
	    pw.println("</subProperty>");
	}	    

	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<domain>");
	    pw.println("<objectProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	    OWLDescription dom = (OWLDescription) it.next();
	    visitor.reset();
	    dom.accept( visitor );
	    pw.println( "" + visitor.result() );
	    pw.println("</domain>");
	}

	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<range>");
	    pw.println("<objectProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	    OWLDescription ran = (OWLDescription) it.next();
	    visitor.reset();
	    ran.accept( visitor );
	    pw.println( "" + visitor.result() );
	    pw.println("</range>");
	}
	
    }
    
    private void renderDataProperty( OWLOntology ontology,
				     OWLDataProperty prop ) throws OWLException {
	pw.println("<defDataProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	/* Hmm. These are always functional for DIG ??? */
	if ( prop.isFunctional( ontology ) ) {
	}

	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<domain>");
	    pw.println("<dataProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	    OWLDescription dom = (OWLDescription) it.next();
	    visitor.reset();
	    dom.accept( visitor );
	    pw.println( "" + visitor.result() );
	    pw.println("</domain>");
	}
	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<range>");
	    pw.println("<dataProperty URI=\"" + mapName( prop.getURI() ) + "\"/>");
	    OWLDataType ran = (OWLDataType) it.next();
	    visitor.reset();
	    ran.accept( visitor );
	    pw.println( "" + visitor.result() );
	    pw.println("</range>");

	    pw.println();
// 	    /* Quick'n'dirty hack for concrete ranges. */
// 	    try {
// 		OWLDataType ran = (OWLDataType) it.next();
		
// 		if ( ran.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInteger() ) 
//                 || ran.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInt() ) ) {
// 		    pw.println("<rangeint>");
// 		    pw.println(" <dataProperty URI=\"" + prop.getURI() + "\"/>");
// 		    pw.println("</rangeint>");
// 		} else if ( ran.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getString() ) ) {
// 		    pw.println("<rangestring>");
// 		    pw.println(" <dataProperty URI=\"" + prop.getURI() + "\"/>");
// 		    pw.println("</rangestring>");
// 		} else {
// 		    throw new RendererException( "Unsupported range: " + prop.getURI() + "\n\t" + ran.getURI());
// 		}
// 	    } catch ( ClassCastException ex ) {
// 		throw new RendererException( "Unsupported range: " + prop.getURI() );
// 	    }
	}
    }

    private void renderDataType( OWLOntology ontology,
				 OWLDataType datatype ) throws OWLException {
	pw.println("<datatypeExpression/>");
    }

    private void renderClassAxiom( OWLClassAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(visitor.result() );
    }

    private void renderPropertyAxiom( OWLPropertyAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(visitor.result() );
    }

    private void renderIndividualAxiom( OWLIndividualAxiom axiom ) throws OWLException {
	visitor.reset();
	axiom.accept( visitor );
	pw.println(visitor.result() );
    }


    /**
     * Returns the <code>SortedSet</code> corresponding to <code>entities</code>
     * ordered by the URIs.
     * @param entities a <code>Set</code> of {@link OWLEntity}s.
     */
    public static SortedSet orderedEntities( Set entities ) { 
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

    public static boolean isTop( URI uri ) {
	return uri.toString().equals( OWLVocabularyAdapter.INSTANCE.getThing() );
    }

    public static boolean isBottom( URI uri ) {
	return uri.toString().equals( OWLVocabularyAdapter.INSTANCE.getNothing() );
    }
    
    public static String mapName( URI uri ) {
	if ( isTop( uri ) ) {
	    return TOP;
	}
	if ( isBottom( uri ) ) {
	    return BOTTOM;
	}
	return uri.toString();
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
 * Revision 1.1  2006/03/20 13:09:27  sean_bechhofer
 * Draft renderer for DIG 2.0
 *
 *
 *
 */
