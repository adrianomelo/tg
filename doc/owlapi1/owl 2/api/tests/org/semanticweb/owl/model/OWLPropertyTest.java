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

package org.semanticweb.owl.model; 
import java.net.URISyntaxException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLProperty;
import java.net.URI;
import org.semanticweb.owl.model.OWLEntity;

// Generated package name


/**
 * Test basic methods on {@link OWLProperty OWLProperty}.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: OWLPropertyTest.java,v 1.2 2005/06/10 12:20:30 sean_bechhofer Exp $
 */

public class OWLPropertyTest extends TestCase
{
    private OWLProperty property;
    private OWLOntology ontology;

    public OWLPropertyTest( String name, 
			    OWLOntology ontology,
			    OWLProperty property )
    {
        super( name );
	this.ontology = ontology;
	this.property = property;
    }
    
    public void runTest() throws Throwable
    {
    }

    public void testAccessors() {
	try {
	    assertNotNull( property.getDomains( ontology ) );
	    assertNotNull( property.getRanges( ontology ) );
	    assertNotNull( property.getSuperProperties( ontology ) );
	    assertTrue( !property.isFunctional( ontology ) );
	} catch (OWLException e) {
	    fail( e.getMessage() );
	}
    }
    
} // OWLPropertyTest



/*
 * ChangeLog
 * $Log: OWLPropertyTest.java,v $
 * Revision 1.2  2005/06/10 12:20:30  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/10/02 14:33:06  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.2  2003/05/08 07:54:35  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 * Revision 1.1  2003/02/11 17:20:29  seanb
 * Moving tests to separate directory.
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 * 
 */
