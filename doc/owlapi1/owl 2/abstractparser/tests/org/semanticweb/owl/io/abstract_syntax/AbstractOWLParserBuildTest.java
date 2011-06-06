/*
 * Copyright (C) 2003 The University of Manchester 
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
 * Filename           $RCSfile: AbstractOWLParserBuildTest.java,v $
 * Revision           $Revision: 1.4 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2006/03/28 16:14:45 $
 *               by   $Author: ronwalf $
 ****************************************************************/
package org.semanticweb.owl.io.abstract_syntax;

import java.io.*;
import java.io.FileReader;
import java.net.URI;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import junit.framework.TestCase;
import org.semanticweb.owl.model.OWLAnd;
import org.semanticweb.owl.model.OWLAnnotationInstance;
import org.semanticweb.owl.model.OWLAnnotationProperty;
import org.semanticweb.owl.model.OWLCardinalityRestriction;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataAllRestriction;
import org.semanticweb.owl.model.OWLDataCardinalityRestriction;
import org.semanticweb.owl.model.OWLDataEnumeration;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDataValueRestriction;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEnumeration;
import org.semanticweb.owl.model.OWLEquivalentPropertiesAxiom;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectCardinalityRestriction;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectSomeRestriction;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOr;
import org.semanticweb.owl.model.OWLProperty;
import org.semanticweb.owl.model.OWLPropertyAxiom;
import org.semanticweb.owl.model.OWLSubPropertyAxiom;
import org.semanticweb.owl.model.helper.OWLDescriptionVisitorAdapter;
import org.semanticweb.owl.model.helper.OWLPropertyAxiomVisitorAdapter;

/**
 * OWLBuilderTest.java.
 * 
 * 
 * Created: Mon May 12 12:03:11 2003.
 * 
 * @author <a href="mailto:dturi at cs.man.ac.uk">Daniele Turi </a>
 * @version $Id: AbstractOWLParserBuildTest.java,v 1.1 2003/12/01 17:06:10
 *          dturi Exp $
 */
public class AbstractOWLParserBuildTest extends TestCase {

    static Logger logger = Logger.getLogger(AbstractOWLParserBuildTest.class
            .getName());

    final static public String TEST_FILE = "general.absowl";

    final static public String DESCR_TEST_FILE = "description.absowl";

    AbstractOWLParser parser;
    OWLOntology ontology;
    String nameSpace;

    public AbstractOWLParserBuildTest(String name) {
        super(name);
    }

    URI findTestFile(String suffix) throws Exception {
	java.net.URL url = ClassLoader.getSystemResource ( suffix );
	return new URI( url.toString() );
    }

    public void setUp() throws Exception {
        parser = new AbstractOWLParser();
        URI testFileURI = findTestFile(TEST_FILE);
        ontology = parser.parseOntology(testFileURI);
        nameSpace = "http://cohse.semanticweb.org/ontologies/people#";
    }

    public void testAll() throws Exception {
        ontology();
    }

    public void ontology() throws Exception {
        assertNotNull(ontology);
        processAnnotationProperties(ontology.getAnnotationProperties());
        processClasses(ontology.getClasses());
        processProperties(ontology.getObjectProperties());
        processPropertyAxioms(ontology.getPropertyAxioms());
        processIndividuals(ontology.getIndividuals());
        processDatatypes(ontology.getDatatypes());
        assertNotNull(ontology.getDatatype(new URI(nameSpace + "salary")));
        assertNotNull(ontology.getDataProperty(new URI(nameSpace
                + "service_number")));
        OWLClass animal = ontology.getClass(new URI(nameSpace + "animal"));
        assertEquals(1, animal.getEquivalentClasses(ontology).size());
        OWLClass bicycle = ontology.getClass(new URI(nameSpace + "bicycle"));
        assertEquals(1, bicycle.getSuperClasses(ontology).size());
        OWLClass testEnumerated = ontology.getClass(new URI(nameSpace
                + "testEnumerated"));
        assertTrue(testEnumerated.isDeprecated(ontology));
        OWLEnumeration enum_ = (OWLEnumeration) testEnumerated.getEnumerations(
                ontology).iterator().next();
        assertEquals(2, enum_.getIndividuals().size());
        OWLClass animalLover = ontology.getClass(new URI(nameSpace
                + "animal_lover"));
        DescriptionTestVisitor animalLoverVisitor = new DescriptionTestVisitor();
        processDescriptions(animalLover.getSuperClasses(ontology),
                animalLoverVisitor);
        assertNotNull(animalLoverVisitor.restriction);
        assertEquals(nameSpace + "has_pet", animalLoverVisitor.restriction
                .getProperty().getURI().toString());
        OWLIndividual fred = ontology
                .getIndividual(new URI(nameSpace + "fred"));
        processIndividual(fred);
        Map valuesMap = fred.getObjectPropertyValues(ontology);
        if (!valuesMap.isEmpty()) {
            System.out.println("fred's values:");
            Iterator values = valuesMap.values().iterator();
            Iterator targets = ((Set) values.next()).iterator();
            processIndividual((OWLIndividual) targets.next());
            System.out.println("end fred's values");
        } else {
            logger.warning("Did not recognise object property values");
        }
        OWLObjectProperty eats = ontology.getObjectProperty(new URI(nameSpace
                + "eats"));
        assertEquals(nameSpace + "eaten_by", ((OWLObjectProperty) eats
                .getInverses(ontology).iterator().next()).getURI().toString());
        assertEquals(nameSpace + "animal", ((OWLClass) eats
                .getDomains(ontology).iterator().next()).getURI().toString());
        assertTrue(eats.isDeprecated(ontology));

        OWLObjectProperty haMadre = ontology.getObjectProperty(new URI(
                nameSpace + "ha_madre"));
        assertNotNull(haMadre);

        OWLObjectProperty hasFather = ontology.getObjectProperty(new URI(
                nameSpace + "has_father"));
        OWLObjectProperty hasParent = (OWLObjectProperty) hasFather
                .getSuperProperties(ontology).iterator().next();
        assertEquals(nameSpace + "has_parent", hasParent.getURI().toString());

        OWLDataProperty serviceNumber = ontology.getDataProperty(new URI(
                nameSpace + "service_number"));
        assertTrue(serviceNumber.isFunctional(ontology));
        OWLDataProperty code = (OWLDataProperty) serviceNumber
                .getSuperProperties(ontology).iterator().next();
        assertEquals(nameSpace + "code", code.getURI().toString());
        OWLDataType integerType = (OWLDataType) serviceNumber.getRanges(
                ontology).iterator().next();
        assertEquals("http://www.w3.org/2001/XMLSchema#integer", integerType
                .getURI().toString());

        OWLDataProperty serviceNumberItalian = ontology
                .getDataProperty(new URI(nameSpace + "numero_di_servizio"));
        assertNotNull(serviceNumberItalian);

        OWLIndividual ben = ontology.getIndividual(new URI(nameSpace + "ben"));
        processDescriptions(ben.getTypes(ontology),
                new DescriptionTestVisitor() {

                    public void visit(OWLDataValueRestriction node)
                            throws OWLException {
                        assertEquals(nameSpace + "service_number", node
                                .getProperty().getURI().toString());
                        assertEquals(
                                "http://www.w3.org/2001/XMLSchema#integer",
                                node.getValue().getURI().toString());
                        assertEquals("3", node.getValue().getValue().toString());
                    }
                });

        OWLIndividual john = ontology
                .getIndividual(new URI(nameSpace + "john"));
        processDescriptions(john.getTypes(ontology),
                new DescriptionTestVisitor() {

                    public void visit(OWLDataAllRestriction node)
                            throws OWLException {
                        OWLDataType dataType = (OWLDataType) node.getDataType();
                        assertEquals(nameSpace + "test", dataType.getURI()
                                .toString());
                        // 		     OWLDataEnumeration enum =
                        // 			 (OWLDataEnumeration)node.getDataType();
                        // 		     Iterator i = enum.getValues().iterator();
                        // 		     assertEquals("http://www.w3.org/2001/XMLSchema#integer",
                        // 				  ((OWLDataValue)i.next())
                        // 				  .getURI().toString());
                    }
                });

        OWLIndividual another = ontology.getIndividual(new URI(nameSpace
                + "another"));
        Set values = (Set) another.getDataPropertyValues(ontology).values()
                .iterator().next();
        OWLDataValue dValue = (OWLDataValue) values.iterator().next();
        assertEquals("http://www.w3.org/2001/XMLSchema#integer", dValue
                .getURI().toString());

        OWLIndividual testLang = ontology.getIndividual(new URI(nameSpace
                + "testLang"));
        assertEquals("en", ((OWLDataValue) ((Set) testLang
                .getDataPropertyValues(ontology).values().iterator().next())
                .iterator().next()).getLang());

        OWLDescription rabbitOwner = parser.parseDescription(new FileReader(
                new File(findTestFile(DESCR_TEST_FILE).toURL().getFile())));

        assertNotNull(rabbitOwner);
        ExternalDescriptionTestVisitor v = new ExternalDescriptionTestVisitor();
        rabbitOwner.accept(v);
        assertEquals(nameSpace + "person", v.firstOperandId);
    }

    void processClasses(Set classes) throws OWLException {
        for (Iterator i = classes.iterator(); i.hasNext();)
            processClass((OWLClass) i.next());
    }

    void processDatatypes(Set datatypes) throws OWLException {
        System.out.println("Datatypes:");
        for (Iterator i = datatypes.iterator(); i.hasNext();)
            processDatatype((OWLDataType) i.next());
        System.out.println("end datatypes.");
    }

    void processDatatype(OWLDataType datatype) throws OWLException {
        System.out.println(datatype.getURI());
    }

    void processIndividuals(Set individuals) throws OWLException {
        for (Iterator i = individuals.iterator(); i.hasNext();)
            processIndividual((OWLIndividual) i.next());
    }

    void processIndividual(OWLIndividual individual) throws OWLException {
        if (individual.isAnonymous())
            System.out.println("Anonymous individual");
        else
            System.out.println("Individual URI: " + individual.getURI());
        Set types = individual.getTypes(ontology);
        if (!types.isEmpty()) {
            System.out.println("Individual types:");
            processDescriptions(types);
            System.out.println("end individual types");
        }
        Map valuesMap = individual.getObjectPropertyValues(ontology);
        // 	if (!valuesMap.isEmpty()) {
        // 	    System.out.println("Individual values:");
        //  	    processIndividuals((Set)valuesMap.values());
        // 	    System.out.println("end individual values");
        // 	}
    }

    void processClass(OWLClass oClass) throws OWLException {
        System.out.println("Class URI: " + oClass.getURI().toString());
        if (oClass.isDeprecated(ontology))
            System.out.println("(deprecated)");
        Set equivs = oClass.getEquivalentClasses(ontology);
        if (!equivs.isEmpty()) {
            System.out.println("Equivalent classes:");
            processDescriptions(equivs);
            System.out.println("end equivalent classes");
        }
        Set sups = oClass.getSuperClasses(ontology);
        if (!sups.isEmpty()) {
            System.out.println("Super classes:");
            processDescriptions(sups);
            System.out.println("end super classes");
        }
        Set enums = oClass.getEnumerations(ontology);
        if (!enums.isEmpty()) {
            System.out.println("Enumerations:");
            for (Iterator i = enums.iterator(); i.hasNext();)
                processIndividuals(((OWLEnumeration) i.next()).getIndividuals());
            System.out.println("end enumerations");
        }
        Set annotations = oClass.getAnnotations();
        if (!annotations.isEmpty()) {
            System.out.println("Annotations:");
            for (Iterator i = annotations.iterator(); i.hasNext();)
                processAnnotation((OWLAnnotationInstance) i.next());
            System.out.println("end annotation");
        }
    }

    void processAnnotationProperties(Set props) throws OWLException {
        if (props.isEmpty())
            return;
        System.out.println("Annotation Properties:");
        for (Iterator i = props.iterator(); i.hasNext();)
            processAnnotationProperty((OWLAnnotationProperty) i.next());
        System.out.println("end annotation properties");
    }

    void processAnnotationProperty(OWLAnnotationProperty annotation)
            throws OWLException {
        System.out.println(annotation.getURI());

    }

    void processAnnotation(OWLAnnotationInstance annotation)
            throws OWLException {
        System.out.println(annotation.getContent());

    }

    void processProperties(Set props) throws OWLException {
        for (Iterator i = props.iterator(); i.hasNext();)
            processProperty((OWLProperty) i.next());
    }

    void processProperty(OWLProperty prop) throws OWLException {
        System.out.println("Property URI: " + prop.getURI().toString());
        Set sups = prop.getSuperProperties(ontology);
        if (!sups.isEmpty()) {
            System.out.println("Super properties:");
            processProperties(sups);
            System.out.println("end super properties.");
        }
        Set domains = prop.getDomains(ontology);
        if (!domains.isEmpty()) {
            System.out.println("Domains:");
            processDescriptions(domains);
            System.out.println("end domains.");
        }
    }

    void processDescriptions(Set descriptions) throws OWLException {
        processDescriptions(descriptions, new DescriptionTestVisitor());
    }

    void processDescriptions(Set descriptions, DescriptionTestVisitor visitor)
            throws OWLException {
        for (Iterator i = descriptions.iterator(); i.hasNext();)
            ((OWLDescription) i.next()).accept(visitor);
    }

    void processPropertyAxioms(Set props) throws OWLException {
        for (Iterator i = props.iterator(); i.hasNext();)
            ((OWLPropertyAxiom) i.next())
                    .accept(new PropertyAxiomTestVisitor());
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(AbstractOWLParserBuildTest.class);
    }

    class PropertyAxiomTestVisitor extends OWLPropertyAxiomVisitorAdapter {

        public void visit(OWLEquivalentPropertiesAxiom node)
                throws OWLException {
            System.out.println("Equivalent properties:");
            processProperties(node.getProperties());
            System.out.println("end equivalent properties.");
        }

        public void visit(OWLSubPropertyAxiom node) throws OWLException {
            System.out.println(node.getSubProperty().getURI());
            System.out.println("is subproperty of");
            System.out.println(node.getSuperProperty().getURI());
            System.out.println("");
        }
    }

    class DescriptionTestVisitor extends OWLDescriptionVisitorAdapter {

        OWLCardinalityRestriction restriction;

        public void visit(OWLClass node) throws OWLException {
            System.out.println(node.getURI());
        }

        public void visit(OWLAnd node) throws OWLException {
            processDescriptions(node.getOperands());
        }

        public void visit(OWLOr node) throws OWLException {
            processDescriptions(node.getOperands());
        }

        public void visit(OWLEnumeration node) throws OWLException {
            processIndividuals(node.getIndividuals());
        }

        public void visit(OWLObjectCardinalityRestriction node)
                throws OWLException {
            if (node != null)
                restriction = node;
            System.out.println("Object cardinality restriction for property "
                    + node.getProperty().getURI());
        }

        public void visit(OWLDataCardinalityRestriction node)
                throws OWLException {
            if (node != null)
                restriction = node;
            System.out.println("Data cardinality restriction for property "
                    + node.getProperty().getURI());
        }

        public void visit(OWLDataValueRestriction node) throws OWLException {
            System.out.println("Data value restriction for property "
                    + node.getProperty().getURI());
            System.out.println("Data value URI: " + node.getValue().getURI());
            System.out.println("Data value value: "
                    + node.getValue().getValue());
        }

        public void visit(OWLObjectSomeRestriction node) throws OWLException {
            System.out.println("Object some restriction for property "
                    + node.getProperty().getURI());
        }
    }

    public class ExternalDescriptionTestVisitor extends DescriptionTestVisitor {

        String firstOperandId;

        public void visit(OWLAnd node) throws OWLException {
            Iterator i = node.getOperands().iterator();
            Object o = i.next();
            if (o instanceof OWLClass)
                firstOperandId = ((OWLClass) o).getURI().toString();
            else
                firstOperandId = ((OWLClass) i.next()).getURI().toString();
        }

    }

}
