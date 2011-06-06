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
 * Filename           $RCSfile: NonInferencingTaxonomyReasoner.java,v $
 * Revision           $Revision: 1.2 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package uk.ac.man.cs.img.owl.inference; 
import java.util.Set;
import java.util.HashSet;

import org.semanticweb.owl.model.OWLDataPropertyInstance;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividualTypeAssertion;
import org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;
import org.semanticweb.owl.inference.OWLTaxonomyReasoner;
import org.semanticweb.owl.model.change.OntologyChangeListener;

import uk.ac.man.cs.img.dig.reasoner.Reasoner;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.OntologyChangeSource;
import java.util.Map;
import java.util.HashMap;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.io.vocabulary.OWLVocabularyAdapter;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.PrintWriter;
import java.util.Iterator;
import uk.ac.man.cs.img.owl.io.dig1_0.RenderingVisitor;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.validation.SpeciesValidatorReporter;
import org.semanticweb.owl.validation.SpeciesValidator;
import org.semanticweb.owl.io.Renderer;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.List;
//import uk.ac.man.cs.img.owl.Utils;
import org.semanticweb.owl.model.helper.OntologyHelper;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.helper.OWLObjectVisitorAdapter;
// Generated package name


/**
 * A simple taxonomy reasoner. This implementation doesn't actually
 * make use of a reasoner, but instead tries to determine the
 * hierarchy through a structural examination of the class
 * definitions. As a result it will be <em>incomplete</em>.
 *
 * @author Sean Bechhofer
 * @version $Id: NonInferencingTaxonomyReasoner.java,v 1.2 2006/03/28 16:14:45 ronwalf Exp $
 */

public class NonInferencingTaxonomyReasoner implements OWLTaxonomyReasoner, OntologyChangeListener
{

    /* A simple OWL Taxonomy Reasoner. Doesn't actually do any
     * reasoning, but instead analyses the structure of the classes in
     * order to work out what the hierarchy is. Doesn't know about
     * equivalences.*/

    /* The ontology being reasoned over */
    private OWLOntology ontology;

    private Map supers;
    private Map subs;

    public NonInferencingTaxonomyReasoner() {
	supers = new HashMap();
	subs = new HashMap();
    }

    /** Set the ontology that the reasoner knows about. The reasoner
     * will then transmit the converted ontology to the DIG
     * reasoner. */
    public void setOntology( OWLOntology onto ) throws OWLException {
	/* Stop listening to any ontologies. */
	if (ontology!=null && ontology instanceof OntologyChangeSource) {
	    ((OntologyChangeSource) ontology).removeOntologyChangeListener( this );
	    ontology = null;
	}
	if ( onto!=null ) {
	    /* Check the species. */ 
	    this.ontology = onto;
	    if ( ontology instanceof OntologyChangeSource ) {
		((OntologyChangeSource) ontology).addOntologyChangeListener( this );
	    }
	    initialise();
	}
    }
    
    private void initialise() throws OWLException {
	/* Examine the ontology */
	supers = new HashMap();
	subs = new HashMap();
	SuperExtracter extracter = new SuperExtracter();
	
	for (Iterator it = ontology.getClasses().iterator();
	     it.hasNext(); ) {
	    OWLClass clazz = (OWLClass) it.next();
	    for (Iterator superIt = clazz.getSuperClasses( ontology ).iterator();
		 superIt.hasNext(); ) {
		OWLDescription desc = (OWLDescription) superIt.next();
		extracter.reset();
		desc.accept( extracter );
		for (Iterator classIt = extracter.getSupers().iterator();
		     classIt.hasNext(); ) {
		    OWLClass superClass = (OWLClass) classIt.next();
		    addSuper( clazz, superClass );
		    addSub( superClass, clazz );
		}
	    }
	    for (Iterator superIt = clazz.getEquivalentClasses( ontology ).iterator();
		 superIt.hasNext(); ) {
		OWLDescription desc = (OWLDescription) superIt.next();
		extracter.reset();
		desc.accept( extracter );
		for (Iterator classIt = extracter.getSupers().iterator();
		     classIt.hasNext(); ) {
		    OWLClass superClass = (OWLClass) classIt.next();
		    addSuper( clazz, superClass );
		    addSub( superClass, clazz );
		}
	    }
	}

	/* Add thing as a super to any orphans */
	for (Iterator it = ontology.getClasses().iterator();
	     it.hasNext(); ) {
	    OWLClass clazz = (OWLClass) it.next();
	    if ( superClassesOf( clazz ).isEmpty() ) {
		try {
		    OWLClass thing = ontology.getClass( new URI( OWLVocabularyAdapter.INSTANCE.getThing() ) );
		    if (clazz!=thing) {
			addSuper( clazz, thing );
			addSub( thing, clazz );
		    }
		} catch (URISyntaxException ex) {
		}
	    }
	}
    }

    private void addSuper( OWLClass class1, 
			   OWLClass class2 ) {
	try {
	    Set s = (Set) supers.get( class1 );
	    if ( s==null ) {
		s = new HashSet();
		supers.put( class1, s );
	    }
	    for (Iterator it = s.iterator();
		 it.hasNext(); ) {
		Set innerSet = (Set) it.next();
		if ( innerSet.contains( class2 ) ) {
		    return;
		}
	    }
	    Set newSet = new HashSet();
	    newSet.add( class2 );
	    s.add( newSet );
	} catch (ClassCastException ex) {
	}
    }

    private void addSub( OWLClass class1, 
			 OWLClass class2 ) {
	try {
	    Set s = (Set) subs.get( class1 );
	    if ( s==null ) {
		s = new HashSet();
		subs.put( class1, s );
	    }
	    for (Iterator it = s.iterator();
		 it.hasNext(); ) {
		Set innerSet = (Set) it.next();
		if ( innerSet.contains( class2 ) ) {
		    return;
		}
	    }
	    Set newSet = new HashSet();
	    newSet.add( class2 );
	    s.add( newSet );
	} catch (ClassCastException ex) {
	}
    }
    
    public OWLOntology getOntology() {
	return ontology;
    }

    public Set superClassesOf( OWLClass clazz ) throws OWLException {
	try {
	    Set s = (Set) supers.get( clazz );
	    if ( s==null ) { 
		s = new HashSet();
		supers.put( clazz, s );
	    }
	    return s;
	} catch (ClassCastException ex) {
	}
 	return new HashSet();
    }

    public Set subClassesOf( OWLClass clazz ) throws OWLException {
	try {
	    Set s = (Set) subs.get( clazz );
	    if ( s==null ) {
		s = new HashSet();
		subs.put( clazz, s );
	    }
	    return s;
	} catch (ClassCastException ex) {
	} 
 	return new HashSet();
    }
    
    public Set ancestorClassesOf( OWLClass d ) throws OWLException {
	return new HashSet();
    }
    
    public Set descendantClassesOf( OWLClass d ) throws OWLException {
	return new HashSet();
    }
    
    /* Returns the collection of (named) classes which are equivalent
     * to the given description. */

    public Set equivalentClassesOf( OWLClass d) throws OWLException {
	return new HashSet();
    }

    public void ontologyChanged( OntologyChange event ) throws OWLException {
	/* A change has occurred. For this simple implementation, we
	 * simple flag that it's changed and then worry about that
	 * later. */
	/* The ontology has changed so we're no longer sure of its status. */
    }

    public void dumpHierarchy( PrintWriter pw, OWLClass clazz, int level ) throws OWLException {
	for (int i=0; i<level; i++) {
	    pw.print(" ");
	    //	    System.out.print(" ");
	}
	pw.println( clazz.getURI() );
	//	System.out.println( clazz.getURI() );
	Set subs = subClassesOf( clazz );

	for (Iterator sit = subs.iterator(); sit.hasNext(); ) {
	    for (Iterator innerIt = ((Set) sit.next()).iterator();
		 innerIt.hasNext(); ) { 
		OWLClass cl = (OWLClass) innerIt.next();
		dumpHierarchy( pw, cl, level+1 );
	    }			
	}
    }

    public void dumpHierarchy( PrintWriter pw ) throws OWLException {
	try {
	    OWLClass thing = ontology.getClass( new URI( OWLVocabularyAdapter.INSTANCE.getThing() ) );
	    dumpHierarchy( pw, thing, 0 );
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
    
    public static void main( String[] args ) {
	try {
	    org.apache.log4j.BasicConfigurator.configure();

	    java.net.URI uri = new java.net.URI( args[0] );
	    OWLOntology onto = OntologyHelper.getOntology( uri );
	    
	    NonInferencingTaxonomyReasoner reasoner = 
		new NonInferencingTaxonomyReasoner();

	    reasoner.setOntology( onto );

	    for (Iterator it = onto.getClasses().iterator(); 
		 it.hasNext();) {
		OWLClass clazz = (OWLClass) it.next();
		System.out.println( clazz.getURI() );
		Set subs = reasoner.subClassesOf( clazz );
		for (Iterator sit = subs.iterator(); sit.hasNext(); ) {
		    for (Iterator innerIt = ((Set) sit.next()).iterator();
			 innerIt.hasNext(); ) { 
			OWLClass cl = (OWLClass) innerIt.next();
			System.out.println("> " + cl.getURI() );
		    }			
		}
		Set supers = reasoner.superClassesOf( clazz );
		for (Iterator sit = supers.iterator(); sit.hasNext(); ) {
		    for (Iterator innerIt = ((Set) sit.next()).iterator();
			 innerIt.hasNext(); ) { 
			OWLClass cl = (OWLClass) innerIt.next();
			System.out.println("< " + cl.getURI() );
		    }			
		}
		Set equivs = reasoner.equivalentClassesOf( clazz );
		for (Iterator eit = equivs.iterator(); eit.hasNext(); ) {
		    for (Iterator innerIt = ((Set) eit.next()).iterator();
			 innerIt.hasNext(); ) { 
			OWLClass cl = (OWLClass) innerIt.next();
			System.out.println("= " + cl.getURI() );
		    }			
		}
	    }
	    StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter( sw );
	    reasoner.dumpHierarchy( pw );
	    System.out.println( sw );
	} catch ( Exception ex ) {
	    //System.out.println( ex.getMessage() );
	    ex.printStackTrace();
	}
    }

    private void notImplemented(String message) throws OWLException {
	throw new OWLException( message + ": Not yet implemented" );
    }

    private class SuperExtracter extends OWLObjectVisitorAdapter {
	private Set supers;
	
	SuperExtracter() {
	}

	void reset() {
	    supers = new HashSet();
	}
	
	Set getSupers() {
	    return supers;
	}
	
	/* If it's a class name, add it */
	public void visit( OWLClass node ) throws OWLException {
	    supers.add( node );
	}

	/* If it's an and, visit each operand */
	public void visit( OWLAnd node ) throws OWLException {
	    for ( Iterator it = node.getOperands().iterator();
		  it.hasNext(); ) {
		OWLDescription description = (OWLDescription) it.next();
		description.accept( this );
	    }
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLFunctionalPropertyAxiom)
	 */
	public void visit(OWLFunctionalPropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom)
	 */
	public void visit(OWLInverseFunctionalPropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLTransitivePropertyAxiom)
	 */
	public void visit(OWLTransitivePropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLSymmetricPropertyAxiom)
	 */
	public void visit(OWLSymmetricPropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLInversePropertyAxiom)
	 */
	public void visit(OWLInversePropertyAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLPropertyDomainAxiom)
	 */
	public void visit(OWLPropertyDomainAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom)
	 */
	public void visit(OWLObjectPropertyRangeAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLDataPropertyRangeAxiom)
	 */
	public void visit(OWLDataPropertyRangeAxiom node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLObjectPropertyInstance)
	 */
	public void visit(OWLObjectPropertyInstance node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLDataPropertyInstance)
	 */
	public void visit(OWLDataPropertyInstance node) throws OWLException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.semanticweb.owl.model.OWLObjectVisitor#visit(org.semanticweb.owl.model.OWLIndividualTypeAssertion)
	 */
	public void visit(OWLIndividualTypeAssertion node) throws OWLException {
		// TODO Auto-generated method stub
		
	}
    }
    
} // SimpleOWLReasoner



/*
 * ChangeLog
 * $Log: NonInferencingTaxonomyReasoner.java,v $
 * Revision 1.2  2006/03/28 16:14:45  ronwalf
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
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.5  2003/10/07 17:42:44  bechhofers
 * Further clean up of compile time dependency.
 *
 * Revision 1.4  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.3  2003/10/02 09:37:24  bechhofers
 * Cleaning up access to Connection. Addition of Servlet Test validator.
 *
 * Revision 1.2  2003/07/08 18:01:07  bechhofers
 * Presentation Servlet and additional documentation regarding inference.
 *
 * Revision 1.1  2003/07/05 16:58:14  bechhofers
 * Adding an HTML servlet-based renderer and some changes to the
 * inferencing classes.
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
