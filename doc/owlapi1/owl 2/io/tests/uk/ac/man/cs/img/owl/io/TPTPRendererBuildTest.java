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

package uk.ac.man.cs.img.owl.io; // Generated package name
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import java.net.URI;
import org.semanticweb.owl.model.OWLOntology;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import uk.ac.man.cs.img.owl.test.OWLTestParser;
import uk.ac.man.cs.img.owl.test.OWLTest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.semanticweb.owl.io.Renderer;
import org.semanticweb.owl.io.Parser;
import org.semanticweb.owl.io.RendererTest;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import java.util.Iterator;

/**
 * Regression test harness for Species Validation.
 *
 * @author Sean Bechhofer
 * @version $Id: TPTPRendererBuildTest.java,v 1.3 2005/06/10 12:20:33 sean_bechhofer Exp $
 */

public class TPTPRendererBuildTest extends TestCase 
{

    public TPTPRendererBuildTest (String name){
        super(name);
    }

    public static TestSuite suite(){
	BasicConfigurator.configure();
        TestSuite suite = new TestSuite ("TPTPRendererBuild");

	/** App should work on arbitrary OWLConnections */

// 	OWLConnection connection = null;
// 	try {
// 	    connection = OWLManager.getOWLConnection();
// 	} catch ( OWLException e ) {
// 	    System.err.println("Could not obtain connection:");
// 	    System.err.println( e.getMessage());
// 	    System.exit(-1);
// 	}

	/* Renderer to test. */

        Renderer renderer = 
	    new uk.ac.man.cs.img.owl.io.tptp.Renderer();

	/* Return the generic test suite */
	return RendererTest.suite( "TPTPRenderer", renderer );
    }
    
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }
    
} // TPTPRendererBuildTest



/*
 * ChangeLog
 * $Log: TPTPRendererBuildTest.java,v $
 * Revision 1.3  2005/06/10 12:20:33  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.2  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:16  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/09/25 13:34:38  bechhofers
 * Removing references to impl classes. Main methods in parsers and
 * renderers now use system property to determine the implementation classes
 * to use.
 *
 * Revision 1.1  2003/08/29 08:12:53  bechhofers
 * Further rendering tests.
 *
 *
 */
