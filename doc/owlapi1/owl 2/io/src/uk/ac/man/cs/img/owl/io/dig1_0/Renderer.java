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
 * Revision           $Revision: 1.7 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/10/19 17:31:43 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.io.dig1_0; // Generated package name

/**
 * Produces FaCT Lisp from OWL ontologies. 
 *
 *
 * Created: Fri Feb 21 09:12:03 2003
 *
 * @author Sean Bechhofer
 * @version $Id: Renderer.java,v 1.7 2004/10/19 17:31:43 sean_bechhofer Exp $
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
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import org.semanticweb.owl.model.OWLPropertyAxiom;

public class Renderer implements org.semanticweb.owl.io.Renderer
{

    /* NOTE: This renderer doesn't do anything with imports.....*/
    /* It does now! */

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
	    pw.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
	    pw.println("<tells xmlns=\"http://dl.kr.org/dig/lang\"");
	    pw.println("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
	    pw.println("       xsi:schemaLocation=\"http://dl.kr.org/dig/lang dig.xsd\">");

	    /* Thing and Nothing are top and bottom */
	    pw.println("<defconcept name=\"" + OWLVocabularyAdapter.INSTANCE.getThing() + "\"/>");
	    pw.println( "<equalc>");
	    pw.println( "  <catom name=\"" + OWLVocabularyAdapter.INSTANCE.getThing() + "\"/>");
	    pw.println( "  <top/>" );
	    pw.println( "</equalc>");
	    
	    pw.println("<defconcept name=\"" + OWLVocabularyAdapter.INSTANCE.getNothing() + "\"/>");
	    pw.println( "<equalc>");
	    pw.println( "  <catom name=\"" + OWLVocabularyAdapter.INSTANCE.getNothing() + "\"/>");
	    pw.println( "  <bottom/>" );
	    pw.println( "</equalc>");


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

	    pw.println("</tells>");

	} catch (OWLException ex) {
	    throw new RendererException( ex.getMessage() );
	}
    }

    private void renderClass( OWLOntology ontology,
			      OWLClass clazz ) throws OWLException {
	pw.println("<defconcept name=\"" + clazz.getURI() + "\"/>");
	
	for ( Iterator it = clazz.getEquivalentClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println( "<equalc>");
	    pw.println( "  <catom name=\"" + clazz.getURI() + "\"/>");
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "  " + visitor.result() );
	    pw.println( "</equalc>");
	}
	
	for ( Iterator it = clazz.getSuperClasses( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println( "<impliesc>");
	    pw.println( "  <catom name=\"" + clazz.getURI() + "\"/>");
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "  " + visitor.result() );
	    pw.println( "</impliesc>");
	}

	for ( Iterator it = clazz.getEnumerations( ontology ).iterator();
	      it.hasNext(); ) {
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println( "<equalc>");
	    pw.println( "  <catom name=\"" + clazz.getURI() + "\"/>");
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "  " + visitor.result() );
	    pw.println( "</equalc>");
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
	    indReference = ind.getURI().toString();
	}

	pw.println("<defindividual name=\"" + indReference + "\"/>" );
	
	for ( Iterator it = ind.getTypes( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    OWLDescription eq = (OWLDescription) it.next();
	    pw.println("<instanceof>");
	    pw.println("  <individual name=\"" + indReference + "\"/>" );
	    visitor.reset();
	    eq.accept( visitor );
	    pw.println( "  " + visitor.result() );
	    pw.println("</instanceof>");

	}
	Map propertyValues = ind.getObjectPropertyValues( ontology );
	//	    System.out.println("ZZ: " + ind.getURI());
	for ( Iterator it = propertyValues.keySet().iterator();
	      it.hasNext(); ) {
	    OWLObjectProperty prop = (OWLObjectProperty) it.next();
	    Set vals = (Set) propertyValues.get(prop);
	    for (Iterator valIt = vals.iterator(); valIt.hasNext();
		 ) {
		pw.println("<related>");
		pw.println("  <individual name=\"" + indReference + "\"/>" );

		pw.println( "   <ratom name=\"" + prop.getURI() + "\"/>" );
		
		OWLIndividual oi = (OWLIndividual) valIt.next();
		if (oi.isAnonymous()) {
		    pw.println("  <individual name=\"anon-" + oi.hashCode() + "\"/>" );
		} else {
		    pw.println("  <individual name=\"" + oi.getURI() + "\"/>" );
		}
		pw.println("</related>");

	    }
	}

	Map dataValues = ind.getDataPropertyValues( ontology );
	//	    System.out.println("ZZ: " + ind.getURI());
	for ( Iterator it = dataValues.keySet().iterator();
	      it.hasNext(); ) {
	    OWLDataProperty prop = (OWLDataProperty) it.next();
	    Set vals = (Set) dataValues.get(prop);
	    for (Iterator valIt = vals.iterator(); valIt.hasNext();
		 ) {
		boolean isString = false;
		boolean isInteger = false;
		OWLDataValue dv = (OWLDataValue) valIt.next();

		/* If there's no URI given, assume it's a string */
		isString = (dv.getURI()==null);

		if ( !isString ) {
		    /* There is a uri given. */
		    if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getString() ) ) {
			isString = true;
		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInteger() ) ) {
			isInteger = true;
		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getPositiveInteger() ) ) {
			isInteger = true;
		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getNegativeInteger() ) ) {
			isInteger = true;
		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getNonNegativeInteger() ) ) {
			isInteger = true;
		    } else if ( dv.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getNonPositiveInteger() ) ) {
			isInteger = true;
		    }
		}

		/* At this point if neither is set, we give up as
		 * we've got a type we can't deal with. */
		if (!(isString || isInteger) ) {
		    throw new RendererException( "Unknown Datatype: " + dv.getURI() );
		}
		
		pw.println("<value>");
		pw.println("  <individual name=\"" + indReference + "\"/>" );
		/* There is a problem here -- by default attributes
		 * have integer range, but we're not necessarily
		 * handling that properly here. */
		pw.println( "   <attribute name=\"" + prop.getURI() + "\"/>" );
		if (isInteger) {
		    pw.println( "   <ival>" + dv.getValue() + "</ival>" );
		}
		if (isString) {
		    pw.println( "   <sval>" + dv.getValue() + "</sval>" );
		}
		pw.println("</value>");

	    }
	}

	/* Don't do these for now! */
// 	Map dataValues = ind.getDataPropertyValues();
// 	//	    System.out.println("ZZ: " + ind.getURI());
// 	for ( Iterator it = dataValues.keySet().iterator();
// 	      it.hasNext(); ) {
// 	    pw.println();
// 	    OWLDataProperty prop = (OWLDataProperty) it.next();
// 		Set vals = (Set) dataValues.get(prop);
// 		for (Iterator valIt = vals.iterator(); valIt.hasNext();
// 		     ) {
// 		    //		    System.out.println("QQ: " + ((OWLIndividual) valIt.next()).getURI());
//  		    OWLDataValue dtv = (OWLDataValue) valIt.next();
// 		    visitor.reset();
// 		    dtv.accept( visitor );
//  		    pw.print( "  value(" + 
// 			      shortForm( prop.getURI() ) + " " + 
// 			      visitor.result() + ")" );
// 		    if (valIt.hasNext()) {
// 			pw.println();
// 		    }
// 		}
// // 		if (it.hasNext()) {
// // 		    pw.println();
// // 		}
// 	    }
// 	    pw.println(")");
// 	}
	

    }

    private void renderObjectProperty( OWLOntology ontology,
				       OWLObjectProperty prop ) throws OWLException {
	pw.println("<defrole name=\"" + prop.getURI() + "\"/>");
	if ( prop.isTransitive( ontology ) ) {
	    pw.println(" <transitive>");
	    pw.println("  <ratom name=\"" + prop.getURI() + "\"/>");
	    pw.println(" </transitive>");
	}
	if ( prop.isFunctional( ontology ) ) {
	    pw.println(" <functional>");
	    pw.println("  <ratom name=\"" + prop.getURI() + "\"/>");
	    pw.println(" </functional>");
	}
	/* Not sure about these.... */
	if ( prop.isInverseFunctional( ontology ) ) {
	}
	if ( prop.isSymmetric( ontology ) ) {
	}
	/****************************/

	for ( Iterator it = prop.getInverses( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<equalr>");
	    pw.println(" <ratom name=\"" + prop.getURI() + "\"/>");
	    
	    OWLObjectProperty inv = (OWLObjectProperty) it.next();
	    pw.println(" <inverse>" );
	    pw.println("  <ratom name=\"" + inv.getURI() + "\"/>");
	    pw.println(" </inverse>" );
	    pw.println("</equalr>");
	}	    

	for ( Iterator it = prop.getSuperProperties( ontology ).iterator();
	      it.hasNext(); ) {

	    pw.println("<impliesr>");
	    pw.println(" <ratom name=\"" + prop.getURI() + "\"/>");
	    OWLObjectProperty sup = (OWLObjectProperty) it.next();
	    pw.println(" <ratom name=\"" + sup.getURI() + "\"/>");
	    pw.println("</impliesr>");
	}	    

	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<domain>");
	    pw.println(" <ratom name=\"" + prop.getURI() + "\"/>");
	    OWLDescription dom = (OWLDescription) it.next();
	    visitor.reset();
	    dom.accept( visitor );
	    pw.println( "  " + visitor.result() );
	    pw.println("</domain>");
	}

	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<range>");
	    pw.println(" <ratom name=\"" + prop.getURI() + "\"/>");
	    OWLDescription ran = (OWLDescription) it.next();
	    visitor.reset();
	    ran.accept( visitor );
	    pw.println( "  " + visitor.result() );
	    pw.println("</range>");
	}
	
    }
    
    private void renderDataProperty( OWLOntology ontology,
				     OWLDataProperty prop ) throws OWLException {
	pw.println("<defattribute name=\"" + prop.getURI() + "\"/>");
	/* Hmm. These are always functional for DIG ??? */
	if ( prop.isFunctional( ontology ) ) {
	}

	for ( Iterator it = prop.getDomains( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println("<domain>");
	    pw.println(" <attribute name=\"" + prop.getURI() + "\"/>");
	    OWLDescription dom = (OWLDescription) it.next();
	    visitor.reset();
	    dom.accept( visitor );
	    pw.println( "  " + visitor.result() );
	    pw.println("</domain>");
	}
	for ( Iterator it = prop.getRanges( ontology ).iterator();
	      it.hasNext(); ) {
	    pw.println();
	    /* Quick'n'dirty hack for concrete ranges. */
	    try {
		OWLDataType ran = (OWLDataType) it.next();
		
		if ( ran.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInteger() ) 
                || ran.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getInt() ) ) {
		    pw.println("<rangeint>");
		    pw.println(" <attribute name=\"" + prop.getURI() + "\"/>");
		    pw.println("</rangeint>");
		} else if ( ran.getURI().toString().equals( XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getString() ) ) {
		    pw.println("<rangestring>");
		    pw.println(" <attribute name=\"" + prop.getURI() + "\"/>");
		    pw.println("</rangestring>");
		} else {
		    throw new RendererException( "Unsupported range: " + prop.getURI() + "\n\t" + ran.getURI());
		}
	    } catch ( ClassCastException ex ) {
		throw new RendererException( "Unsupported range: " + prop.getURI() );
	    }
	}
    }

    private void renderDataType( OWLOntology ontology,
				 OWLDataType datatype ) throws OWLException {
	//pw.println(" Datatype(" + shortForm( datatype.getURI() ) + ")" );
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
 * Revision 1.7  2004/10/19 17:31:43  sean_bechhofer
 * Fixing problems with import closure.
 *
 * Revision 1.6  2004/05/12 13:30:25  dturi
 * renderOntology() now supports also int.
 *
 * Revision 1.5  2004/05/05 11:42:44  dturi
 * orderedEntities made public and static,
 *
 * Revision 1.4  2004/02/10 15:58:23  sean_bechhofer
 * Added handling of simple data ranges.
 *
 * Revision 1.3  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.2  2003/11/20 12:58:13  sean_bechhofer
 * Addition of language handling in OWLDataValues.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.10  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.9  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.8  2003/10/01 16:51:09  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.7  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.6  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.5  2003/09/15 16:09:03  bechhofers
 * Minor change to DIG renderer to add axioms concerning thing
 * and nothing and to fix anonymous individuals.
 *
 * Revision 1.4  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.3  2003/06/03 17:01:53  seanb
 * Additional inference
 *
 * Revision 1.2  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.1  2003/05/19 11:59:12  seanb
 * DIG Renderer
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
