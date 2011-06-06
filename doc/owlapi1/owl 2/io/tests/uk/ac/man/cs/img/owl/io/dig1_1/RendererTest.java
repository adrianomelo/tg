/*
 * Copyright (C) 2003 The University of Manchester
 * 
 * Modifications to the initial code base are copyright of their respective
 * authors, or their employers as appropriate. Authorship of the modifications
 * may be determined from the ChangeLog placed at the end of this file.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 * ***************************************************************************
 * Source code information
 * Filename         $RCSfile: RendererTest.java,v $ 
 * Revision         $Revision: 1.4 $
 * Release status   $State: Exp $ 
 * Last modified on $Date: 2006/02/08 14:28:26 $ 
 *               by $Author: dturi $
 ******************************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import java.net.URI;
import java.net.URL;

import junit.framework.TestCase;

import org.kr.dl.dig.v1_1.TellsDocument;
import org.semanticweb.owl.io.ParserException;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.owl_rdf.OWLRDFParser;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;

/**
 * @author dturi $Id: RendererTest.java,v 1.4 2006/02/08 14:28:26 dturi Exp $
 */
public class RendererTest extends TestCase {

	protected OWLOntology ontology;
	protected OWLConnection connection;
	protected OWLRDFParser parser;

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for RendererTest.
	 * 
	 * @param arg0
	 */
	public RendererTest(String arg0) throws Exception {
		super(arg0);
		//PropertyConfigurator.configure(new URL("file:io/src/log4j.properties"));
		connection = null;
		try {
			connection = OWLManager.getOWLConnection();
		} catch (OWLException e) {
			System.err.println("Could not obtain connection");
			System.exit(-1);
		}
        parser = new OWLRDFParser();
        parser.setConnection(connection);
	}

	public void testRenderOntology() throws Exception {
        render("full1");
        render("full2");
        render("full4");
        render("full5");
        render("full6");
        render("full7");
        render("functionalProperty1");
        render("inverseProperties3");
        render("domain1");
        render("range1");
        render("anonymousIndividuals1");
        render("anonymousIndividuals2");
        render("anonymousIndividuals3");
        render("anonymousIndividuals4");
        render("some1");
        render("some2");
        render("some3");
        render("all1");
        render("cardinality1");
        render("cardinality2");
        render("data2");
        render("equivalent1");
        render("equivalent2");
        render("equivalent3");
        render("equivalent4");
        render("equivalent5");
        render("equivalent6");
        render("disjoint1");
        render("subclassunion1");
        render("equivalentProperty1");
        render("equivalentProperty2");
        render("subProperty1");
	}
    
    public void testRenderEnumeration() throws Exception {
        render("enumeration1");
    }
    
    public void testRenderURL() throws Exception {
        render(new URL("http://www.cs.man.ac.uk/~horrocks/OWL/Ontologies/mad_cows.owl"));
    }

	/**
	 * @param parser
	 * @throws ParserException
	 * @throws RendererException
	 */
	private void render(String test) throws Exception {
        URL testURL = ClassLoader.getSystemResource(test +".owl");
		render(testURL);
	}

    private void render(URL testURL) throws Exception {
        ontology = parser.parseOntology(new URI(testURL.toString()));
		Renderer renderer = new Renderer();
		TellsDocument tells = renderer.renderOntology(ontology);
		System.out.println(tells.toString());
        tells.getTells().setUri("urn:test");
        assertTrue(tells.validate());
    }
}