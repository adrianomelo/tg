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

package org.semanticweb.owl.model.change; 
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.TestUtils;
import org.semanticweb.owl.model.change.AddAnnotationInstance;

// Generated package name


/**
 * Test that annotations work.
 *
 * <br/>
 * Created: Wed Jan 22 15:50:41 2003
 *
 * @author Sean Bechhofer
 * @version $Id: AddAnnotationTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class AddAnnotationTest extends TestCase
{

    private OWLDataFactory factory;
    private OWLOntology ontology;
    private ChangeVisitor visitor;

    public static Test suite( OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv ) {

	TestSuite suite = new TestSuite ("AddAnnotation");
	/* Entity tests */
	suite.addTest( new AddAnnotationTest( "testAddAnnotation",
					  fact,
					  o,
					  cv ) );
	return suite;
    }
    
    public AddAnnotationTest( String name, 
			      OWLDataFactory fact,
			      OWLOntology o, 
			      ChangeVisitor cv )
    {
        super( name );
	this.factory = fact;
	this.ontology = o;
	this.visitor = cv;
    }
    
    public void testAddAnnotation() throws Throwable {
	/* Get a new class */
	OWLEntity entity = factory.getOWLClass( TestUtils.newURI() );
	/* Get a new class */
	OWLAnnotationProperty prop = factory.getOWLAnnotationProperty( TestUtils.newURI() );
	/* Get a new change object */
	AddAnnotationInstance aai = new AddAnnotationInstance( ontology,
							       entity,
							       prop,
							       "Annotation",
							       null );
	/* Add it */
	aai.accept( visitor );
	
	assertTrue( !entity.getAnnotations().isEmpty() );
    }

    
} // AddAnnotationTest



/*
 * ChangeLog
 * $Log: AddAnnotationTest.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/05/09 17:34:38  seanb
 * Additional validation. Extending accessors to cover multiple ontologies.
 *
 * Revision 1.2  2003/02/12 09:14:12  seanb
 * Moving Tests.
 *
 * Revision 1.1  2003/02/11 17:20:29  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 */
