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
 * Filename           $RCSfile: AbstractOWLParser.java,v $
 * Revision           $Revision: 1.3 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2004/01/08 16:36:27 $
 *               by   $Author: dturi $
 * Created: Thu Nov 27 16:46:47 2003.
 ****************************************************************/
package org.semanticweb.owl.io.abstract_syntax;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import org.semanticweb.owl.io.Parser;
import org.semanticweb.owl.io.ParserException;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.helper.OWLBuilder;
import org.semanticweb.owl.util.OWLConnection;

/**  
 * <a href="http://owl.man.ac.uk/2003/concrete/latest/">OWL abstract syntax</a>
 * implementation of {@link Parser}.
 * Delegates the actual parsing to the 
 * <a href="http://www.antlr.org/">ANTLR</a>
 * generated {@link ANTLRParser} and the construction of 
 * the OWLOntology to an {@link OWLBuilder}.
 * <p>
 * <strong>Warning:</strong>
 * <code>ObjectProperty</code> and 
 * <code>DatatypeProperty</code> entities have to be defined 
 * before they can be used.
 * Eg: </p>
 * <ul>
 * <li><em>VALID</em>
 * <pre>
 *    Namespace(people=&lt;http://cohse.semanticweb.org/ontologies/people#&gt;)
 *    Ontology( 
 *      ObjectProperty(people:has_pet)
 *      Individual(people:fred value(people:has_pet people:felix))
 *    )
 * </pre></li>
 * <li><em>NOT VALID</em>
 * <pre>
 *    Namespace(people=&lt;http://cohse.semanticweb.org/ontologies/people#&gt;)
 *    Ontology( 
 *      Individual(people:fred value(people:has_pet people:felix))
 *      ObjectProperty(people:has_pet)
 *    )
 * </pre></li>
 * </ul>
 *
 * @author <a href="mailto:dturi at cs.man.ac.uk">Daniele Turi</a>
 * @version $Id: AbstractOWLParser.java,v 1.3 2004/01/08 16:36:27 dturi Exp $
 */
public class AbstractOWLParser implements Parser {

    /**
     * Value: {@value}
     */
    final public static String RDF =
        "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    /**
     * Value: {@value}
     */
    final public static String XSD = "http://www.w3.org/2001/XMLSchema#";

    /**
     * Value: {@value}
     */
    final public static String RDFS = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Value: {@value}
     */
    final public static String OWL = "http://www.w3.org/2002/07/owl#";

    /**
     * Creates a new <code>AbstractOWLParser</code> 
     * and sets the builder property to a new <code>OWLBuilder</code> 
     * with default OWLConnection.
     *
     */
    public AbstractOWLParser() {
        builder = new OWLBuilder();
    }

    /**
     * Creates a new <code>AbstractOWLParser</code> and
     * sets the builder property to a new <code>OWLBuilder</code> with 
     * <code>connection</code> as OWLConnection.
     *
     * @param connection the <code>OWLConnection</code> for the OWLBuilder.
     */
    public AbstractOWLParser(OWLConnection connection) {
        builder = new OWLBuilder(connection);
    }

    ANTLRParser antlrParser;

    /**
     * Get the ANTLR generated parser that performs the
     * actual parsing.
     * @return the ANTLR generated parser that performs the
     * actual parsing.
     */
    public ANTLRParser getANTLRParser() {
        return antlrParser;
    }

    /**
     * Set the ANTLRParser value.
     * @param newAntlrParser The new ANTLRParser value.
     */
    public void setANTLRParser(ANTLRParser newAntlrParser) {
        this.antlrParser = newAntlrParser;
    }

    OWLBuilder builder;

    /**
     * Get the OWLBuilder.
     * @return the OWLBuilder.
     */
    public OWLBuilder getBuilder() {
        return builder;
    }

    /**
     * Set the OWLBuilder.
     * @param newBuilder The new OWLBuilder value.
     */
    public void setBuilder(OWLBuilder newBuilder) {
        builder = newBuilder;
    }

    /**    
     * Gets the current <code>OWLConnection</code>.
     * This is held in the OWLBuilder.
     * @return the current <code>OWLConnection</code>.
     */
    public OWLConnection getConnection() {
        return builder.getConnection();
    }

    /**
     * Set the value of the OWLConnection value.
     * This is held in the OWLBuilder.
     * @param newConnection The new OWLConnection value.
     */
    public void setConnection(OWLConnection newConnection) {
        builder.setConnection(newConnection);
    }

    /**
      * Parses the ontology at <code>uri</code> and 
      * returns a new ontology object based 
      * on the connection that the parser knows about. 
      * Also sets the physical URI of the resulting 
      * OWLOntology to <code>uri</code>
      * and initialises
      * the <code>namespaceTable</code> of the ANTLRParser with 
      * the predefined neamespaces.
      * @param uri a <code>URI</code> corresponding
      * to an ontology.
      * @return the <code>OWLOntology</code> corresponding to parsed ontology.
      * @exception ParserException if an error occurs
      */
    public OWLOntology parseOntology(URI uri) throws ParserException {
        try {
            return parseOntology(
                new InputStreamReader(uri.toURL().openStream()),
                uri);
        } catch (MalformedURLException e) {
            throw new ParserException(
                uri.toString() + " is not well formed",
                e);
        } catch (IOException e) {
            throw new ParserException(
                "IOException while parsing " + uri.toString(),
                e);
        }
    }

    /**
     * Parses the ontology in <code>reader</code> and 
     * returns a new ontology object based 
     * on the connection that the parser knows about. 
     * Also sets the physical URI of the resulting OWLOntology to physicalURI
     * and initialises
     * the <code>namespaceTable</code> of the ANTLRParser with 
     * the predefined neamespaces.
     * @param reader a <code>Reader</code> containing an ontology.
     * @param physicalURI the physical <code>URI</code> corresponding
     * to the ontology in the reader.
     * @return the <code>OWLOntology</code> corresponding to parsed ontology.
     * @exception ParserException if an error occurs
     */
    public OWLOntology parseOntology(Reader reader, URI physicalURI)
        throws ParserException {
        try {
            AbstractOWLLexer lexer = new AbstractOWLLexer(reader);
            antlrParser = new ANTLRParser(lexer);
            initialiseNamespaceTable();
            OWLOntology ontology = antlrParser.ontology(physicalURI, builder);
            reader.close();
            return ontology;
        } catch (RecognitionException e) {
            throw new ParserException(
                "RecognitionException while parsing " + physicalURI.toString(),
                e);
        } catch (TokenStreamException e) {
            throw new ParserException(
                "TokenStreamException while parsing " + physicalURI.toString(),
                e);
        } catch (OWLException e) {
            throw new ParserException(
                "OWLException while parsing " + physicalURI.toString(),
                e);
        } catch (IOException e) {
            throw new ParserException(
                "IOException while parsing " + physicalURI.toString(),
                e);
        }
    }

    Map options;

    /**
     * Sets the <code>options</code> Map
     *
     * @param newOptions the new options <code>Map</code>.
     */
    public void setOptions(Map newOptions) {
        options = newOptions;
    }

    /**
     * Gets the <code>options</code> Map.
     *
     * @return the <code>Map</code> with the options.
     */
    public Map getOptions() {
        return options;
    }

    /**
     * Parses the description in <code>descriptionReader</code> 
     * and returns the corresponding OWLDescription.
     *
     * @param descriptionReader a <code>Reader</code> holding a description.
     * @return the <code>OWLDescription</code> corresponding to the parsing
     * of the description in <code>descriptionReader</code>.
     * @exception OWLException if an error occurs
     * @exception RecognitionException if an error occurs
     * @exception TokenStreamException if an error occurs
     * @exception FileNotFoundException if an error occurs
     * @exception IOException if an error occurs
     * @exception ParserException if an error occurs
     */
    public OWLDescription parseDescription(Reader descriptionReader)
        throws
            OWLException,
            RecognitionException,
            TokenStreamException,
            FileNotFoundException,
            IOException,
            ParserException {
        if (antlrParser == null)
            throw new ParserException("ANTLRParser has not been initialised");
        AbstractOWLLexer lexer = new AbstractOWLLexer(descriptionReader);
        ANTLRParser descriptionParser = new ANTLRParser(lexer);
        OWLDescription owlDescription =
            descriptionParser.externalDescription(
                builder,
                antlrParser.getIdsTable(),
                antlrParser.getNamespaceTable());
        descriptionReader.close();
        return owlDescription;
    }

    /**
    	 * Parses <code>descriptionString</code> 
    	 * and returns the corresponding OWLDescription.
    	 *
    	 * @param descriptionString a description String.
    	 * @return the <code>OWLDescription</code> corresponding to the parsing
    	 * of the description in <code>descriptionReader</code>.
    	 * @exception OWLException if an error occurs
    	 * @exception RecognitionException if an error occurs
    	 * @exception TokenStreamException if an error occurs
    	 * @exception FileNotFoundException if an error occurs
    	 * @exception IOException if an error occurs
    	 * @exception ParserException if an error occurs
    	 */
    public OWLDescription parseDescription(String descriptionString)
        throws
            OWLException,
            RecognitionException,
            TokenStreamException,
            FileNotFoundException,
            IOException,
            ParserException {
        return parseDescription(new StringReader(descriptionString));
    }

    /**
     * Initialises the namespaceTable of the ANTLRParser with
     * the following predefined namespaces:
     * <ul>
     * <li>{@link #RDF}</li>
     * <li>{@link #XSD}</li>
     * <li>{@link #RDFS}</li>
     * <li>{@link #OWL}</li>
     * </ul>
     */
    void initialiseNamespaceTable() throws ParserException {
        if (antlrParser == null)
            throw new ParserException("ANTLRParser has not been initialised");
        Map namespaceTable = new HashMap();
        namespaceTable.put("rdf", RDF);
        namespaceTable.put("xsd", XSD);
        namespaceTable.put("rdfs", RDFS);
        namespaceTable.put("owl", OWL);
        antlrParser.setNamespaceTable(namespaceTable);
    }

    /**
     * Parses an ontology (passed as a URI) and builds 
     * a corresponding abstract owl ontology.
     * 
     * @param args one argument - a URI - required.
     */
    public static void main(String[] args) {
        try {
            new AbstractOWLParser().parseOntology(new URI(args[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

} // AbstractOWLParser
