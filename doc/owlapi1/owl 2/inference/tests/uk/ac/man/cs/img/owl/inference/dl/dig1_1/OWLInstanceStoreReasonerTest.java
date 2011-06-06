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

/****************************************************************
 * Source code information
 * -----------------------
 * Filename           $RCSfile: OWLInstanceStoreReasonerTest.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2005/06/10 12:20:32 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/
package uk.ac.man.cs.img.owl.inference.dl.dig1_1;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;

import uk.ac.man.cs.img.instancestore.InstanceStoreCreationException;
import uk.ac.man.cs.img.instancestore.InstanceStoreDatabase;
import uk.ac.man.cs.img.instancestore.InstanceStoreFactory;
import uk.ac.man.cs.img.instancestore.OWLInstanceStore;

/**
 * @author dturi $Id: OWLInstanceStoreReasonerTest.java,v 1.3 2005/06/10 12:20:32 sean_bechhofer Exp $
 */
public class OWLInstanceStoreReasonerTest extends TestCase {

    static final String ns = "http://instancestore.man.ac.uk/countries#";

    OWLInstanceStoreReasoner owlReasoner;
    OWLInstanceStore iS;

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
        owlReasoner.release();
    }

    /**
     * Constructor for OWLInstanceStoreReasonerTest.
     * 
     * @param arg0
     */
    public OWLInstanceStoreReasonerTest(String arg0)
            throws InstanceStoreCreationException, Exception {
        super(arg0);
        log4j();
        iS = InstanceStoreFactory
                .getOWLInstance(ClassLoader.getSystemResourceAsStream("iS.properties"));
        if (((InstanceStoreDatabase) iS.getBackingStore()).tables().isEmpty())
            ((InstanceStoreDatabase) iS.getBackingStore()).createTables();
//        URI ontologyUri = URI.create(ClassLoader.getSystemResource(
//                "countries.owl").toString());
//        iS.load(ontologyUri);
        owlReasoner = new OWLInstanceStoreReasoner(iS);
    }

    protected void log4j() {
        Properties props = new Properties();
        InputStream in;
        try {
            in = ClassLoader
                    .getSystemResourceAsStream("properties/inference/log4j.properties");
            props.load(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(props);
    }

//    public OWLInstanceStoreProperties getProperties() throws Exception {
//        OWLInstanceStoreProperties properties = new OWLInstanceStoreProperties();
//        InputStream in = ClassLoader.getSystemResourceAsStream("iS.properties");
//        properties.load(in);
//        in.close();
//        return properties;
//    }

    public void testAll() throws Exception {
        String query = "<http://instancestore.man.ac.uk/countries#Country>";
        OWLDescription description = owlReasoner
                .parseAbstractOWLDescription(query);
        Set retrieved = owlReasoner.instancesOf(description);
        printIndividuals(retrieved);
        assertEquals(6, retrieved.size());

        retrieved = owlReasoner.subClassesOf(description);
        printClasses(retrieved);
        assertEquals(ns + "EUCountry", ((OWLClass) retrieved.iterator().next())
                .getURI().toString());
        
        retrieved = owlReasoner.equivalentClassesOf(description);
////        System.out.println("Equivs");
////        printClasses(retrieved);
//        assertTrue((ns + "Nation").equals(((OWLClass) retrieved.iterator().next())
//                .getURI().toString()) || (ns + "Nation").equals(((OWLClass) retrieved.iterator().next())
//                .getURI().toString()));

        query = "restriction(<http://instancestore.man.ac.uk/countries#people> value(\"50\"^^xsd:integer))";
        description = owlReasoner.parseAbstractOWLDescription(query);
        retrieved = owlReasoner.instancesOf(description);
        assertEquals("http://instancestore.man.ac.uk/countries#britain",
                ((OWLIndividual) retrieved.iterator().next()).getURI()
                        .toString());

        query = "restriction(<http://instancestore.man.ac.uk/countries#remark> value(\"fair\"^^xsd:string))";
        description = owlReasoner.parseAbstractOWLDescription(query);
        retrieved = owlReasoner.instancesOf(description);
        assertEquals("http://instancestore.man.ac.uk/countries#britain",
                ((OWLIndividual) retrieved.iterator().next()).getURI()
                        .toString());

        query = "<http://instancestore.man.ac.uk/countries#EUCountry>";
        description = owlReasoner.parseAbstractOWLDescription(query);
        retrieved = owlReasoner.instancesOf(description);
        assertEquals(4, retrieved.size());
        
        retrieved = owlReasoner.superClassesOf(description);
        assertEquals(2, retrieved.size());
    }

    private void printIndividuals(Set individuals) throws OWLException {
        for (Iterator iter = individuals.iterator(); iter.hasNext();) {
            OWLIndividual element = (OWLIndividual) iter.next();
            System.out.println(element.getURI().toString());
        }
    }

    private void printClasses(Set classes) throws OWLException {
        for (Iterator iter = classes.iterator(); iter.hasNext();) {
            OWLClass element = (OWLClass) iter.next();
            System.out.println(element.getURI().toString());
        }
    }

}
