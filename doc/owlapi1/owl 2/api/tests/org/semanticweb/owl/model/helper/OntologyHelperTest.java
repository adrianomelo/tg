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

package org.semanticweb.owl.model.helper; 
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.TestUtils;
import org.semanticweb.owl.model.helper.OntologyHelper;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.change.AddSuperClass;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.AddEquivalentClass;

// Generated package name


/**
 * Test that entites are added in a sensible fashion.
 *
 * <br/>
 * Created: Wed Jan 22 15:50:41 2003
 *
 * @author Sean Bechhofer
 * @version $Id: OntologyHelperTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class OntologyHelperTest extends TestCase
{
    private OWLDataFactory factory;
    private OWLOntology ontology;
    private ChangeVisitor visitor;

    public static Test suite( OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv ) {

	TestSuite suite = new TestSuite ("OntologyHelper");
	/* Entity tests */
	suite.addTest( new OntologyHelperTest( "testClassAxioms",
					       fact,
					       o,
					       cv ) );
	return suite;
    }
    
    public OntologyHelperTest( String name, 
			  OWLDataFactory fact,
			  OWLOntology o, 
			  ChangeVisitor cv )
    {
        super( name );
	this.factory = fact;
	this.ontology = o;
	this.visitor = cv;
    }
    
    public void testClassAxioms() throws Throwable {
	assertTrue( OntologyHelper.allDefinitionsAsAxioms( ontology ).size() 
		    == 0 );
	/* Get a new class */
	OWLClass clazz1 = factory.getOWLClass( TestUtils.newURI() );
	/* Get a new change object */
	AddEntity ae = new AddEntity( ontology,
				      clazz1,
				      null );
	/* Add it */
	ae.accept( visitor );
	/* Add a super class */
	OWLClass clazz2 = factory.getOWLClass( TestUtils.newURI() );
	AddSuperClass asc = new AddSuperClass( ontology,
					       clazz1,
					       clazz2,
					       null);
	asc.accept( visitor); 
	/* Should now be one axiom in there */
	assertTrue( OntologyHelper.allDefinitionsAsAxioms( ontology ).size()
		    == 1);
	
	/* Add an equivalent class */
	OWLClass clazz3 = factory.getOWLClass( TestUtils.newURI() );
	AddEquivalentClass aec = new AddEquivalentClass( ontology,
						    clazz1,
						    clazz2,
						    null);
	aec.accept( visitor);
	/* Should now be two axioms in there */
	assertTrue( OntologyHelper.allDefinitionsAsAxioms( ontology ).size()
		    == 2);
    }
    
} // OntologyHelperTest



/*
 * ChangeLog
 * $Log: OntologyHelperTest.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/04/10 11:20:48  seanb
 * no message
 *
 *
 */
