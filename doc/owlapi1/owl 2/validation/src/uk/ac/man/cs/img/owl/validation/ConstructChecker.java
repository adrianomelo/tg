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
 * Filename           $RCSfile: ConstructChecker.java,v $
 * Revision           $Revision: 1.5 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:46 $
 *               by   $Author: ronwalf $
 ****************************************************************/

package uk.ac.man.cs.img.owl.validation; 
import org.semanticweb.owl.model.helper.OWLClassAxiomVisitorAdapter;
import org.semanticweb.owl.model.helper.OWLPropertyAxiomVisitorAdapter;
import org.semanticweb.owl.model.helper.OWLObjectVisitorAdapter;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.apache.log4j.Logger;
import java.util.Iterator;
import org.semanticweb.owl.model.OWLException;
import org.apache.log4j.BasicConfigurator;
import org.semanticweb.owl.util.OWLConnection;
import java.util.Map;
import java.util.HashMap;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import java.net.URI;

import org.semanticweb.owl.model.OWLDataPropertyInstance;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLIndividualTypeAssertion;
import org.semanticweb.owl.model.OWLInverseFunctionalPropertyAxiom;
import org.semanticweb.owl.model.OWLInversePropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyInstance;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLNot;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLSymmetricPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitivePropertyAxiom;

import java.util.List;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.semanticweb.owl.model.OWLDataSomeRestriction;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataRange;


// Generated package name

/**
 * An implementation of a construct checker. 
 *
 * @author Sean Bechhofer
 * @version $Id: ConstructChecker.java,v 1.5 2006/03/28 16:14:46 ronwalf Exp $
 */

public class ConstructChecker implements org.semanticweb.owl.validation.ConstructChecker, org.semanticweb.owl.validation.OWLValidationConstants
{
    static Logger logger = Logger.getLogger(SpeciesValidator.class);

    //    protected int constructs;
    protected Set constructs;

    ClassAxiomVisitor cav;
    PropertyAxiomVisitor pav;
    OWLObjectVisitor oov;

    public ConstructChecker()
    {
	constructs = new HashSet();
	oov = new OWLObjectVisitor( this );
	cav = new ClassAxiomVisitor( this, oov );
	pav = new PropertyAxiomVisitor( this );
    }

    public void reset() {
	constructs = new HashSet() ;
    }

    public Set constructsUsed( OWLOntology ontology ) throws OWLException {
	for (Iterator it = ontology.getClasses().iterator();
	     it.hasNext(); ) {
	    OWLClass clazz = (OWLClass) it.next();
	    if ( !clazz.getSuperClasses( ontology ).isEmpty() ) {
		/* There's a partial definition*/
		constructs.add( new Integer( PARTIAL ) );
		for ( Iterator dit = 
			  clazz.getSuperClasses( ontology ).iterator();
		      dit.hasNext(); ) {
		    OWLDescription description = 
			(OWLDescription) dit.next();
		    description.accept( oov );
		}
	    }
	    if ( !clazz.getEquivalentClasses( ontology ).isEmpty() ) {
		/* There's a complete definition*/
		constructs.add( new Integer( COMPLETE ) );
		for ( Iterator dit = 
			  clazz.getEquivalentClasses( ontology ).iterator();
		      dit.hasNext(); ) {
		    OWLDescription description = 
			(OWLDescription) dit.next();
		    description.accept( oov );
		}
	    }
	    if ( !clazz.getEnumerations( ontology ).isEmpty() ) {
		/* There's an enumeration*/
		constructs.add( new Integer( COMPLETE ) );
		constructs.add( new Integer( ONEOF ) );
	    }
	}

	if (!ontology.getDatatypes().isEmpty() ) {
	    constructs.add( new Integer( DATATYPE ) );
	}

	for (Iterator it = ontology.getObjectProperties().iterator();
	     it.hasNext(); ) {
	    OWLObjectProperty prop = (OWLObjectProperty) it.next();
	    if ( !prop.getInverses( ontology ).isEmpty() ) {
		constructs.add( new Integer(INVERSE) );
	    }
	    if ( prop.isFunctional( ontology ) ) {
		constructs.add( new Integer(FUNCTIONAL) );
	    }
	    if ( prop.isTransitive( ontology ) ) {
		constructs.add( new Integer(TRANSITIVE) );
	    }	
	    if ( prop.isSymmetric( ontology ) ) {
		constructs.add( new Integer(SYMMETRIC) );
	    }
	    if ( prop.isInverseFunctional( ontology ) ) {
		constructs.add( new Integer(INVERSEFUNCTIONAL) );
	    }

	    for ( Iterator dit = 
		      prop.getDomains( ontology ).iterator();
		  dit.hasNext(); ) {
		OWLDescription description = 
		    (OWLDescription) dit.next();
		description.accept( oov );
	    }
	    for ( Iterator dit = 
		      prop.getRanges( ontology ).iterator();
		  dit.hasNext(); ) {
		OWLDescription description = 
		    (OWLDescription) dit.next();
		description.accept( oov );
	    }
	}
	for (Iterator it = ontology.getDataProperties().iterator();
	     it.hasNext(); ) {
	    OWLDataProperty prop = (OWLDataProperty) it.next();
	    if ( prop.isFunctional( ontology ) ) {
		constructs.add( new Integer(FUNCTIONAL) );
	    }
	    for ( Iterator dit = 
		      prop.getDomains( ontology ).iterator();
		  dit.hasNext(); ) {
		OWLDescription description = 
		    (OWLDescription) dit.next();
		description.accept( oov );
	    }
	    for ( Iterator rit = 
		      prop.getRanges( ontology ).iterator();
		  rit.hasNext(); ) {
		OWLObject t = (OWLObject) rit.next();
		t.accept( oov );
		//		constructs = constructs | DATATYPE;
	    }
	}

	for ( Iterator axit = ontology.getClassAxioms().iterator();
	      axit.hasNext(); ) {
	    OWLClassAxiom axiom =
		(OWLClassAxiom) axit.next();
	    axiom.accept( cav );
	}

	for ( Iterator axit = ontology.getPropertyAxioms().iterator();
	      axit.hasNext(); ) {
	    OWLPropertyAxiom axiom =
		(OWLPropertyAxiom) axit.next();
	    axiom.accept( pav );
	}
	if ( !ontology.getIndividuals().isEmpty() ) {
	    constructs.add( new Integer(INDIVIDUALS) );
	}

	for ( Iterator indit = ontology.getIndividuals().iterator();
	      indit.hasNext(); ) {
	    OWLIndividual ind =
		(OWLIndividual) indit.next();
	    if ( !ind.getDataPropertyValues( ontology ).isEmpty() ) {
	    }
	    /* Look at the individual types */
	    for ( Iterator dit = 
		      ind.getTypes( ontology ).iterator();
		  dit.hasNext(); ) {
		OWLDescription description = 
		    (OWLDescription) dit.next();
		description.accept( oov );
	    }
	    
	    Map propertyValues = ind.getObjectPropertyValues( ontology );
	    for ( Iterator it = propertyValues.keySet().iterator();
		  it.hasNext(); ) {
		OWLObjectProperty prop = (OWLObjectProperty) it.next();
		Set vals = (Set) propertyValues.get(prop);
		if ( !vals.isEmpty() ) {
		    constructs.add( new Integer(RELATEDINDIVIDUALS) );
		}
	    }
	    Map dataValues = ind.getDataPropertyValues( ontology );
	    for ( Iterator it = dataValues.keySet().iterator();
		  it.hasNext(); ) {
		OWLDataProperty prop = (OWLDataProperty) it.next();
		Set vals = (Set) dataValues.get(prop);
		if ( !vals.isEmpty() ) {
			constructs.add( new Integer( INDIVIDUALDATA ) );
		}
	    }
	}
	return constructs;
    }

    private class ClassAxiomVisitor extends OWLClassAxiomVisitorAdapter {
	ConstructChecker parent;
	OWLObjectVisitor oov;
	ClassAxiomVisitor( ConstructChecker cc,
			   OWLObjectVisitor oov ) {
	    parent = cc;
	    this.oov = oov;
	}

	public void visit( OWLEquivalentClassesAxiom axiom ) throws OWLException {
	    constructs.add( new Integer(EQUIVALENCE) );
	    for ( Iterator it = axiom.getEquivalentClasses().iterator();
		  it.hasNext(); ) {
		OWLDescription desc = (OWLDescription) it.next();
		desc.accept( oov );
	    }
	}
	
	public void visit( OWLDisjointClassesAxiom axiom ) throws OWLException {
	    constructs.add( new Integer(DISJOINT) );
	    for ( Iterator it = axiom.getDisjointClasses().iterator();
		  it.hasNext(); ) {
		OWLDescription desc = (OWLDescription) it.next();
		desc.accept( oov );
	    }
	}
	
	public void visit( OWLSubClassAxiom axiom ) throws OWLException {
	    constructs.add( new Integer(SUBCLASS) );
	    axiom.getSubClass().accept( oov );
	    axiom.getSuperClass().accept( oov );
	}
	
    }

    private class PropertyAxiomVisitor extends OWLPropertyAxiomVisitorAdapter {
	ConstructChecker parent;
	PropertyAxiomVisitor( ConstructChecker cc ) {
	    parent = cc;

	}
    }
    private class OWLObjectVisitor extends OWLObjectVisitorAdapter {
	ConstructChecker parent;
	OWLObjectVisitor( ConstructChecker cc ) {
	    parent = cc;
	}
	
	public void visit( OWLOr node ) throws OWLException {
	    parent.constructs.add( new Integer(UNION) );
	    for (Iterator dit = node.getOperands().iterator();
		 dit.hasNext(); ) {
		OWLDescription description = 
		    (OWLDescription) dit.next();
		description.accept( this );
	    }
	}

	public void visit ( OWLAnd node ) throws OWLException {
	    parent.constructs.add( new Integer(INTERSECTION) );
	    for (Iterator dit = node.getOperands().iterator();
		 dit.hasNext(); ) {
		OWLDescription description = 
		    (OWLDescription) dit.next();
		description.accept( this );
	    }
	}

	public void visit( OWLNot node ) throws OWLException {
	    parent.constructs.add( new Integer(COMPLEMENT) );
	    node.getOperand().accept( this );
	}

	public void visit( OWLEnumeration node ) {
	    parent.constructs.add( new Integer(ONEOF) );
	}
	public void visit( OWLObjectValueRestriction node ) {
	    /* It's a some and a oneof */
	    parent.constructs.add( new Integer(ONEOF) );
	    parent.constructs.add( new Integer(SOME) );
	}
	public void visit( OWLObjectSomeRestriction node ) throws OWLException {
	    parent.constructs.add( new Integer(SOME) );
	    node.getDescription().accept( this );
	}
	public void visit( OWLObjectAllRestriction node ) throws OWLException {
	    parent.constructs.add( new Integer(ALL) );
	    node.getDescription().accept( this );
	}
	public void visit( OWLObjectCardinalityRestriction node ) throws OWLException {
	    parent.constructs.add( new Integer(CARDINALITY) );
	}
	
	public void visit( OWLDataValueRestriction node ) {
	    parent.constructs.add( new Integer(DATATYPE) );
	}
	public void visit( OWLDataSomeRestriction node ) throws OWLException {
	    parent.constructs.add( new Integer(SOME) );
	    //	    parent.constructs = parent.constructs | DATATYPE;
	    node.getDataType().accept( this );
	}
	public void visit( OWLDataAllRestriction node ) throws OWLException {
	    parent.constructs.add( new Integer(ALL) );
	    //	    parent.constructs = parent.constructs | DATATYPE;
	    node.getDataType().accept( this );
	}
	public void visit( OWLDataType node ) {
	    parent.constructs.add( new Integer(DATATYPE) );
	}
	public void visit( OWLDataRange node ) {
	    parent.constructs.add( new Integer(DATARANGE) );
	}
	
	public void visit( OWLDataCardinalityRestriction node ) throws OWLException {
	    parent.constructs.add( new Integer(CARDINALITY) );
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
    
    
    public static List used( Set s ) {
	List used = new ArrayList();
	for (Iterator it = s.iterator(); it.hasNext(); ) {
	    int i = ((Integer) it.next()).intValue();
	    if ( i==INTERSECTION ) {
		used.add("Intersection");
	    }
	    if ( i == UNION ) {
		used.add("Union");
	    }
	    if ( i == COMPLEMENT ) {
		used.add("Complement");
	    }
	    if ( i == ZEROONECARDINALITY ) {
		used.add("ZeroOneCardinality");
	    }
	    if ( i == CARDINALITY ) {
		used.add("Cardinality");
	    }
	    if ( i == ONEOF ) {
		used.add("Oneof");
	    }
	    if ( i == SOME ) {
		used.add("Some");
	    }
	    if ( i == ALL ) {
		used.add("All");
	    }
	    if ( i == DATATYPE ) {
		used.add("Datatype");
	    }
	    if ( i == DATARANGE ) {
		used.add("DataRange");
	    }
	    if ( i == SUBCLASS ) {
		used.add("SubClass");
	    }
	    if ( i == EQUIVALENCE ) {
		used.add("Equivalence");
	    }
	    if ( i == DISJOINT ) {
		used.add("Disjoint");
	    }
	    if ( i == PARTIAL ) {
		used.add("Partial");
	    }
	    if ( i == COMPLETE ) {
		used.add("Complete");
	    }
	    if ( i == SUBPROPERTY ) {
		used.add("Subproperty");
	    }
	    if ( i == EQUIVALENTPROPERTY ) {
		used.add("Equivalentproperty");
	    }
	    if ( i == INVERSE ) {
		used.add("Inverse");
	    }
	    if ( i == TRANSITIVE ) {
		used.add("Transitive");
	    }
	    if ( i == SYMMETRIC ) {
		used.add("Symmetric");
	    }
	    if ( i == FUNCTIONAL ) {
		used.add("Functional");
	    }
	    if ( i == INVERSEFUNCTIONAL ) {
		used.add("Inversefunctional");
	    }
	    if ( i == INDIVIDUALS ) {
		used.add("Individuals");
	    }
	    if ( i == RELATEDINDIVIDUALS ) {
		used.add("Relatedindividuals");
	    }
	    if ( i == INDIVIDUALDATA ) {
		used.add("IndividualData");
	    }
	    if ( i == SAMEINDIVIDUAL ) {
		used.add("Sameindividual");
	    }
	    if ( i == DIFFERENTINDIVIDUAL ) {
		used.add("Differentindividual");
	    }
	}
	return used;
    }
    
    public static void main(String[] args) {
	    
	try {
	    if (args.length < 1) {
		System.out.println("Usage: ConstructChecker url");
		System.exit(1);
	    }

	    BasicConfigurator.configure();
	    
	    URI uri = new URI(args[0]);

	    OWLRDFParser parser = new OWLRDFParser();
	    parser.setConnection( OWLManager.getOWLConnection() );

	    OWLOntology ontology = 
		parser.parseOntology(uri);
	    
	    ConstructChecker cc = new ConstructChecker();
	    for (Iterator it = ConstructChecker.used( cc.constructsUsed( ontology ) ).iterator(); it.hasNext(); ) {
		String s = (String) it.next();
		System.out.print(s + " ");
	    }
	    System.out.println();
	    
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }

} // ConstructChecker



/*
 * ChangeLog
 * $Log: ConstructChecker.java,v $
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
 * Revision 1.4  2004/06/22 13:57:39  sean_bechhofer
 * Fixing problems with validation/expressivity checking code.
 *
 * Revision 1.3  2004/03/30 17:46:38  sean_bechhofer
 * Changes to parser to support better validation.
 *
 * Revision 1.2  2003/12/19 12:04:17  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:19  sean_bechhofer
 * Initial Import
 *
 * Revision 1.9  2003/10/02 14:33:06  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.8  2003/10/01 16:51:10  bechhofers
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
 * Revision 1.5  2003/09/10 09:01:29  bechhofers
 * Updates to parser to address bnode validation issues, in particular the
 * use of bnodes within equivalence/disjoint axioms and structure sharing.
 *
 * Revision 1.4  2003/08/28 21:10:06  volzr
 * Consistent Usage of URI
 *
 * Revision 1.3  2003/08/21 16:38:31  bechhofers
 * Added DataRange to cope with data oneOf
 * Fixed some problems with validation
 * More test machinery
 *
 * Revision 1.2  2003/06/20 14:07:51  seanb
 * Addition of some documentation. Minor tinkering.
 *
 * Revision 1.1  2003/06/19 13:33:33  seanb
 * Addition of construct checking.
 *
 */
