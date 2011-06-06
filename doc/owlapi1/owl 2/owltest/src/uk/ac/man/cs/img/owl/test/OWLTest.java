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
 * Filename           $RCSfile: OWLTest.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:17 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.test;

import java.util.HashMap;


import java.util.Map;
import java.net.URI;
import org.semanticweb.owl.io.ParserException;
import java.net.MalformedURLException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.unika.aifb.rdf.api.syntax.RDFParser;
import edu.unika.aifb.rdf.api.util.RDFManager;

import org.apache.log4j.BasicConfigurator;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * Simple data structure that represents some of the information in an
 * OWL Test. This isn't intended to capture everything, but should be
 * enough to automate some simple unit testing of parsers and
 * validators.

 * @author <a href="mailto:seanb@cs.man.ac.uk">Sean Bechhofer</a>
 */

public class OWLTest {

    /* The URI of the test. */

    private String uri;

    /* The type of test */

    private String type;

    /* The status of the test */

    private String status;

    /* The documents referred to in the test */
       
    private Set documents;

    /* Datatypes used */

    private Set datatypes;

    /* The species levels of the documents */

    private Map documentLevels;

    /* The roles of the documents */

    private Map documentRoles;

    /* Descriptions applied to things */

    private Map descriptions;

    public OWLTest() {
	uri = "??";
	type = "??";
	status = "??";
	documents = new HashSet();
	datatypes = new HashSet();
	documentLevels = new HashMap();
	documentRoles = new HashMap();
	descriptions = new HashMap();
    }

    public String getURI() {
	return uri;
    }
    public void setURI(String uri) {
	this.uri = uri;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }
    
    public Set getDocuments() {
	return documents;
    }

    public Set getDatatypes() {
	return datatypes;
    }

    public Map getDocumentRoles() {
	return documentRoles;
    }

    public Map getDocumentLevels() {
	return documentLevels;
    }

    public Map getDescriptions() {
	return descriptions;
    }
}// OWLTest
