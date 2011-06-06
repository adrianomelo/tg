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
 * Filename           $RCSfile: ValidateTests.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:20 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.validation; // Generated package name

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.owl.model.OWLDataFactory;
import java.net.URI;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLNot;
import java.util.Set;
import java.util.HashSet;
import java.net.URISyntaxException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLObjectValueRestriction;
import org.semanticweb.owl.model.change.AddIndividualClass;
import java.util.zip.ZipInputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import uk.ac.man.cs.img.owl.test.OWLTestParser;
import uk.ac.man.cs.img.owl.test.OWLTest;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
/**
 *  Attempts to validate OWL tests. Expects the URL of a zip
 *  file. This should contain a number of manifests. For each
 *  manifest, attempts to verify that the files listed within that
 *  manifest are really the species that they are claimed to be.
 *
 * @author Sean Bechhofer
 * @version $Id: ValidateTests.java,v 1.1.1.1 2003/10/14 17:10:20 sean_bechhofer Exp $
 */

public class ValidateTests 
{

    public ValidateTests() {
    }

    public static void main( String[] args ) {
	boolean manifests = false;
	boolean validate = false;

	if ( args.length!=3 ) {
	    System.err.println( "Usage: validate-tests (-m|-v) base url");
	    System.exit(1);
	} 
	if ( args[0].equals("-m") ) {
	    manifests = true;
	} else if ( args[0].equals("-v") ) {
	    validate = true;
	}
	try {
	    Map allDocs = new HashMap();
	    String base = args[1];
	    URI baseURI = new URI( base );
	    URL url = new URL( args[2] );
	    ZipInputStream zis = new ZipInputStream( url.openStream() );
	    ZipEntry ze = null;
	    
	    while ( (ze=zis.getNextEntry()) !=null ) {
		String name = ze.getName();
		if ( name.matches( ".*Manifest.*rdf" ) ) {
		    if ( manifests ) {
			System.out.println( base + "/" + name );
		    }
		    OWLTestParser otp = new OWLTestParser();
		    URI manifestURI = new URI( base + "/" + name );
		    OWLTest test = otp.parseTest( manifestURI );
		    allDocs.putAll( test.getDocumentLevels() );
		}
	    }
	    if ( validate ) {
		System.out.println(allDocs.size() + " Files");
		System.out.println("========================");
		for ( Iterator it = allDocs.keySet().iterator();
		      it.hasNext(); ) {
		    Object key = it.next();
		    System.out.println( " " + key + ":" + allDocs.get( key ) );
		}
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	}
    }
} // ValidateTests



/*
 * ChangeLog
 * $Log: ValidateTests.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:20  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/06/06 14:27:34  seanb
 * Utilities for processing tests.
 *
 * Revision 1.1  2003/05/06 14:25:48  seanb
 * Grabbing Manifest files
 *
 *
 */
