/*
 * Copyright (C) 2003, University of Karlsruhe
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

package org.semanticweb.owl.impl.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.io.Parser;
import org.semanticweb.owl.io.ParserException;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.semanticweb.owl.util.PhysicalURIToDefaultParametersResolver;

/**
 * OWL Connection
 * 
 * The class gives access to a concrete implementation of the OWL API
 * 
 * @since 05.02.2003
 * @author <a href="mailto:volz@fzi.de">Raphael Volz</a>
 * @version $Id: OWLConnectionImpl.java,v 1.8 2005/06/10 12:20:31 sean_bechhofer Exp $  
 * 
 */
public class OWLConnectionImpl implements OWLConnection {

    Map openOntologies = new HashMap();
    // Tracks all opened ontologies ! Possible reload problems

    static Logger logger = Logger.getLogger(OWLConnectionImpl.class);

    /** Default parameters of this connection */
    protected static final Map s_defaultParameters;

    static {
        Map parameters = new HashMap();
        parameters.put(
            OWLManager.OWL_CONNECTION,
            "org.semanticweb.owl.impl.model");
        s_defaultParameters = Collections.unmodifiableMap(parameters);
        // register the URI resolver
        OWLManager.registerPhysicalURIToDefaultParametersResolver(
            new PhysicalURIResolver());
    }

    //	protected OWLConnectionImpl m_ownerOWLConnection; // Only needed if connections create other connections 

    /** Parameters of this connection */

    protected Map m_parameters;

    /** The map of OWL ontologies indexed by the logical URI opened by this connection. */
    protected Map m_ontologiesByLogicalURI;

    /** The map of OWL ontologies indexed by the physical URI opened by this connection. */
    protected Map m_ontologiesByPhysicalURI;

    /** Data Factory used to create entities */
    protected OWLDataFactory m_owlDataFactory;

    /** Visitor for change events. */
    protected ChangeVisitor m_applyChangeVisitor;

    /**
     * Creates an instance of this class with default parameters.
     *
     * @throws OWLException                    thrown if connection cannot be created
     */
    public OWLConnectionImpl() throws OWLException {
        this(s_defaultParameters, new HashMap(), new HashMap());
    }

    /**
     * Creates an instance of this class using user defined  parameters.
     *
     * @param parameters                        the map of parameters
     * @throws OWLException                    thrown if connection cannot be created
     */
    public OWLConnectionImpl(Map parameters) throws OWLException {
        this(parameters, new HashMap(), new HashMap());
    }

    /**
         * Creates an instance of this class.
         *
         * @param parameters                        the map of connection parameters
         * @param ontologiesByLogicalURI            the map of OWL Ontologies by logical URI
         * @param ontologiesByPhysicalURI           the map of OWL Ontologies by physical URI
         * @throws OWLException                    thrown if connection cannot be created
         */
    public OWLConnectionImpl(
        Map parameters,
        Map ontologiesByLogicalURI,
        Map ontologiesByPhysicalURI)
        throws OWLException {
        m_parameters = new HashMap(parameters);
        m_ontologiesByLogicalURI = ontologiesByLogicalURI;
        m_ontologiesByPhysicalURI = ontologiesByPhysicalURI;
        m_owlDataFactory = OWLDataFactoryImpl.getInstance(this);
        m_applyChangeVisitor = null;

        //logger.warn("No Apply Change visitor here !!!!");
    }

    public OWLDataFactory getDataFactory() {
        return m_owlDataFactory;
    }

    /**
         * Returns the parameters of this connection.
         *
         * @return                                  the parameters of this connection
         */
    public synchronized Map getParameters() {
        return m_parameters;
    }
    /**
     * Closes all opened ontologies. Involves no saving of changes !!!
     */
    public synchronized void close() {
        m_ontologiesByLogicalURI = null;
        m_ontologiesByPhysicalURI = null;
    }
    /**
     * @return <code>true</code> if this connection has one opened ontology.
     */
    public synchronized boolean isOpen() {
        return m_ontologiesByLogicalURI != null;
    }

    /**
     * Returns a new connection using the parameters of this connection. 
     * @throws OWLException  thrown if connection cannot be created
     * @deprecated
     */
    public synchronized OWLConnection getConnection() throws OWLException {
        Map childOIModelsByLogicalURI = new HashMap();
        Map childOIModelsByPhysicalURI = new HashMap();
        return new OWLConnectionImpl(
            m_parameters,
            childOIModelsByLogicalURI,
            childOIModelsByPhysicalURI);
    }

    /**
     * Opens an OWL Ontology from a given network-reachable URI, i.e. an URL.
     * Throws an OWLException if the ontology has already be opened. 
     * @deprecated Use getOntology instead.
     * @param physicalURI                       the physical URI of the OWLOntology
     * @return                                  the OWLOntology
     * @throws OWLException                    thrown if OWLOntology cannot be opened
     */
    public synchronized OWLOntology openOntologyPhysical(URI physicalURI)
        throws OWLException {
        logger.info("TO DO: Use default Parser instead of hard-coded one");

        Parser parser = null;
        try {
            /* Removing compile time dependency....*/
            parser =
                (Parser) Class
                    .forName("org.semanticweb.owl.io.owl_rdf.OWLRDFParser")
                    .newInstance();
        } catch (Exception ex) {
            throw new OWLException(ex.getMessage());
        }

        /* Changed openOntologies => m_ontologiesByPhysicalURI */
        if (m_ontologiesByPhysicalURI.containsKey(physicalURI))
            return (OWLOntology) m_ontologiesByPhysicalURI.get(physicalURI);
        OWLOntology onto = null;
        logger.info("TO DO: logicalURI should be derived from Code");
        try {
            onto = parser.parseOntology(physicalURI);
        } catch (ParserException e) {
            throw new OWLException("Parsing failed", e);
        }
        /* Changed openOntologies => m_ontologiesByPhysicalURI */
        m_ontologiesByPhysicalURI.put(physicalURI, onto);
        return onto;
    }

    /**
     * Opens an OWLOntology with given logical URI. Can only return an ontology that
     * is already physically opened.
     * @deprecated
     * @param logicalURI                        the logical URI of the OWLOntology
     * @return                                  the OWLOntology
     * @throws OWLException                    thrown if OWLOntology cannot be opened
     */
    public synchronized OWLOntology openOntologyLogical(URI logicalURI)
        throws OWLException {
        if (m_ontologiesByLogicalURI.containsKey(logicalURI)) return  (OWLOntology) m_ontologiesByLogicalURI.get(logicalURI);
        // Try it phyiscally
        try {
        	return loadOntologyPhysical(logicalURI);
        } catch (OWLException e) {
        	throw new OWLException("Cannot find ontology with logical URI" + logicalURI);
        }
    }
    /**
     * Creates an OWLOntology with given physical and logical URIs. Use createOntology instead.
     * @deprecated Use createOntology instead.
     * @param physicalURI                       the physical URI of the OWLOntology
     * @param logicalURI                        the logical URI of the OWLOntology
     * @return                                  the OWLOntology
     * @throws OWLException                    thrown if OWLOntology cannot be opened
     */
    public synchronized OWLOntology createOWLOntology(
        URI physicalURI,
        URI logicalURI)
        throws OWLException {

        if (m_ontologiesByPhysicalURI.containsKey(physicalURI)
            || m_ontologiesByLogicalURI.containsKey(logicalURI))
            throw new OWLException(
                "Cannot create ontology with this physical or logical URI, since it is already open:\n  "
                    + physicalURI
                    + "\n  "
                    + logicalURI);

        logicalURI = physicalURI.resolve(logicalURI);
        if (m_ontologiesByPhysicalURI.containsKey(physicalURI))
            throw new OWLException(
                "Model with physical URI '"
                    + physicalURI
                    + "' has already been opened.");
        if (m_ontologiesByLogicalURI.containsKey(logicalURI))
            throw new OWLException(
                "Model with logical URI '"
                    + logicalURI
                    + "' has already been opened.");
        OWLOntology onto =
            m_owlDataFactory.getOWLOntology(logicalURI, physicalURI);
        if (logicalURI != null)
            m_ontologiesByLogicalURI.put(logicalURI, onto);
        if (physicalURI != null)
            m_ontologiesByPhysicalURI.put(physicalURI, onto);
        return onto;
    }

    /**
     * Notifies the connection that the OWLOntology has been deleted.
     *
     * @param oimodel                           OWLOntology that is removed
     * @throws OWLException                    thrown if there is an error
     */
    public synchronized void notifyOntologyDeleted(OWLOntology onto)
        throws OWLException {
        /* Changed to key on strings rather than URIs....  SKB.*/
        //	    m_ontologiesByLogicalURI.remove(oimodel.getLogicalURI().toString());
        //	    m_ontologiesByPhysicalURI.remove(oimodel.getPhysicalURI().toString());
        /* And back to URIs.... */
        m_ontologiesByLogicalURI.remove(onto.getLogicalURI());
        m_ontologiesByPhysicalURI.remove(onto.getPhysicalURI());
	/* Oh dear. */
	((OWLDataFactoryImpl) m_owlDataFactory).forgetOWLOntology( onto );
    }

    /** Forget about any ontologies that may have been created. Also
     * reinitialise the datafactory. Dodgy. */
    public synchronized void dropAllOntologies() throws OWLException {
	m_ontologiesByLogicalURI = new HashMap();
	m_ontologiesByPhysicalURI = new HashMap();
        m_owlDataFactory = OWLDataFactoryImpl.getInstance(this);
    }

    /**
     * Returns the set of all open OWLOntology objects.
     *
     * @return                              the set of all open OWLOntology objects
     */
    public synchronized Set getOpenOWLOntologies() {
        return new HashSet(m_ontologiesByLogicalURI.values());
    }
    /**
     * Returns the set of OI-models available at the node represented by this KAON connection.
     *
     * @return                                  the set of OIModel objects represented by this KAON connection
     */
    public synchronized Set getAllOntologies() {
        return new HashSet(m_ontologiesByLogicalURI.values());
    }
    /**
     * Returns the set of logical URIs of all OI-models available at the node represented by this KAON connection.
     *
     * @return                                  the set of OIModel objects represented by this KAON connection
     */
    public synchronized Set getAllLogicalURIs() {
        return m_ontologiesByLogicalURI.keySet();
    }
    /**
     * Applies the list of changes to the models in this connection.
     * @deprecated
     * @param changes                           list of changes to the models in the connection
     * @throws OWLException                    thrown if there is an error
     */
    public synchronized void applyChanges(List changes) throws OWLException {
        logger.info("Not yet implemented!");
    }
    /**
    * The resolver for RDF physical URIs.
    */
    protected static class PhysicalURIResolver
        implements PhysicalURIToDefaultParametersResolver {
        public Map getDefaultParametersForPhysicalURI(
            String physicalURI,
            Map contextParameters) {
            if (physicalURI.startsWith("jar:")
                || physicalURI.startsWith("file:")
                || physicalURI.startsWith("http:"))
                return new HashMap(s_defaultParameters);
            else
                return null;
        }
    }

    /** Given an ontology, returns a change visitor that will enact
     * changes over that ontology. May throw an exception if unable to
     * provide such an implementation. */
    public ChangeVisitor getChangeVisitor(OWLOntology o) throws OWLException {
        if (o instanceof OWLOntologyImpl) {
            return (ChangeVisitor) o;
        } else {
            /* Might consider new subclass for exception */
            throw new OWLException(
                "Cannot create visitor for: "
                    + o
                    + "\nWrong implementation class");
        }
    }

    public void changeOntologyLogicalURI(OWLOntology o, URI uri)
        throws OWLException {
        /* If it's already set to that then don't worry. */
        if (!o.getURI().equals(uri)) {
            if (m_ontologiesByLogicalURI.containsKey(uri)) {
                throw new OWLException("Logical URI " + uri + " in use");
            } else {
                /* Damn it! removed the new one instead. Doh! */
                m_ontologiesByLogicalURI.remove(o.getLogicalURI());
                m_ontologiesByLogicalURI.put(uri, o);
            }
        }
    }

    /**
     * @see org.semanticweb.owl.util.OWLConnection#createOntology(URI, URI)
     */
    public OWLOntology createOntology(URI physicalURI, URI logicalURI)
        throws OWLException {
        	

        if (m_ontologiesByPhysicalURI.containsKey(physicalURI)
            || m_ontologiesByLogicalURI.containsKey(logicalURI))
            throw new OWLException(
                "Cannot create ontology with this physical or logical URI, since it is already open:\n  "
                    + physicalURI
                    + "\n  "
                    + logicalURI);

        logicalURI = physicalURI.resolve(logicalURI);
        if (m_ontologiesByPhysicalURI.containsKey(physicalURI))
            throw new OWLException(
                "Model with physical URI '"
                    + physicalURI
                    + "' has already been opened.");
        if (m_ontologiesByLogicalURI.containsKey(logicalURI))
            throw new OWLException(
                "Model with logical URI '"
                    + logicalURI
                    + "' has already been opened.");
        OWLOntology onto =
            m_owlDataFactory.getOWLOntology(logicalURI, physicalURI);
        if (logicalURI != null)
            m_ontologiesByLogicalURI.put(logicalURI, onto);
        if (physicalURI != null)
            m_ontologiesByPhysicalURI.put(physicalURI, onto);
        return onto;
    }

    /**
     * @see org.semanticweb.owl.util.OWLConnection#getOntologyLogical(URI)
     */
    public OWLOntology getOntologyLogical(URI logicalURI) throws OWLException {
        throw new OWLException("This implementation does not yet track logical URI usage.");
    }

    /**
     * @see org.semanticweb.owl.util.OWLConnection#getOntologyPhysical(URI)
     */
    public OWLOntology getOntologyPhysical(URI physicalURI)
        throws OWLException {
        return (m_ontologiesByPhysicalURI.containsKey(physicalURI))
            ? (OWLOntology) m_ontologiesByPhysicalURI.get(physicalURI)
            : loadOntologyPhysical(physicalURI);
    }
    /**
     * @see org.semanticweb.owl.util.OWLConnection#loadOntologyPhysical(URI)
     */
    public OWLOntology loadOntologyPhysical(URI physicalURI)
        throws OWLException {

        if (m_ontologiesByPhysicalURI.containsKey(physicalURI))
            throw new OWLException(
                "Ontology with physical URI '"
                    + physicalURI
                    + "' has already been loaded.");
        OWLOntology onto = null;
        logger.info("TO DO: logicalURI should be derived from Code");
        Parser parser = null;
        try {
            /* Removing compile time dependency....*/
            parser =
                (Parser) Class
                    .forName("org.semanticweb.owl.io.owl_rdf.OWLRDFParser")
                    .newInstance();
	    parser.setConnection( this );
        } catch (Exception ex) {
            throw new OWLException(ex.getMessage());
        }
        try {
            onto = parser.parseOntology(physicalURI);
        } catch (ParserException e) {
            throw new OWLException("Parsing failed", e);
        }
        /* Changed openOntologies => m_ontologiesByPhysicalURI */
        m_ontologiesByPhysicalURI.put(physicalURI, onto);
        m_ontologiesByLogicalURI.put(onto.getURI(), onto);
        return onto;

    }

    /**
     * @see org.semanticweb.owl.util.OWLConnection#reloadOntologyPhysical(URI)
     */
    public OWLOntology reloadOntologyPhysical(URI physicalURI)
        throws OWLException {
        if (!m_ontologiesByPhysicalURI.containsKey(physicalURI))
            throw new OWLException(
                "Ontology with physical URI '"
                    + physicalURI
                    + "' has not yet been loaded.");
        // Might be too naive to do only this...
        m_ontologiesByLogicalURI.remove(getOntologyPhysical(physicalURI).getURI());
        m_ontologiesByPhysicalURI.remove(physicalURI);
        return loadOntologyPhysical(physicalURI);
    }

}

/*
 * ChangeLog
 * $Log: OWLConnectionImpl.java,v $
 * Revision 1.8  2005/06/10 12:20:31  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.7  2004/11/03 17:36:42  sean_bechhofer
 * Minor addition to connection to support dropping/deletion.
 *
 * Revision 1.6  2004/05/06 15:32:16  sean_bechhofer
 * Ensure that the parser has a connection set.
 *
 * Revision 1.5  2004/03/05 17:34:48  sean_bechhofer
 * Updates to validation to improve reporting capabilities.
 *
 * Revision 1.4  2004/01/26 07:24:01  digitalis
 * Change in connection.
 *
 * Revision 1.3  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.2  2003/12/11 12:59:45  sean_bechhofer
 * Addition of setLogicalURI change event for setting the URI of an
 * Ontology.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:14  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/10/03 16:06:41  bechhofers
 * Refactoring of source and tests to break dependencies on implementation
 * classes.
 *
 * Revision 1.2  2003/10/01 16:51:09  bechhofers
 * Refactoring of Parser interface. Parsing now returns new ontology objects
 * created using a default connection.
 *
 * Revision 1.1  2003/09/25 11:21:48  volzr
 * model.impl -> impl.model rename
 *
 * Revision 1.1  2003/01/29 14:30:19  seanb
 * Initial Checkin
 *
 * 
 */
