/*
 * Copyright (C) 2005, University of Karlsruhe
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

package org.semanticweb.owl.util;

import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.List;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.model.OWLOntology;

/**
 * Represents a single connection to some implementation provider. 
 * An OWLOntology is unique with respect to its
 * logical URI within a connection.
 *
 * @author Raphael Volz (volz@aifb.uni-karlsruhe.de)
 * @author Boris Motik (boris.motik@fzi.de)
 */
public interface OWLConnection {

	OWLDataFactory getDataFactory();
	/**
	 * Returns the parameters of this connection.
	 *
	 * @return                                  the parameters of this connection
	 */
	Map getParameters() throws OWLException;
	/**
	 * Closes this connection, i.e. closes all open ontologies.
	 */
	void close() throws OWLException;
	
	/**
	 * Returns <code>true</code> if this connection is open, i.e. atleast one ontology is open.
	 *
	 * @return                                  <code>true</code> if the connection is open
	 */
	boolean isOpen() throws OWLException;
	

	/**
	 * Opens an OWLOntology with given physical URI (in fact an
	 * URL), somewhere on the web or in the file system.
	 * In order to learn how to open a local file, just try to open a file with your favorite web browser.
	 * On Windows machines the syntax follows the pattern: file:///<b>drive</b>:/<b>directory</b>/<b>file</b>
	 * @deprecated
	 * @param physicalURI  the physical URI of the OWLOntology
	 */
	OWLOntology openOntologyPhysical(URI physicalURI) throws OWLException;
	
	/**
	 * @return Ontology with the given phyiscal URI. Will try to load the ontology, if the ontology has not been opened.
	 */
	OWLOntology getOntologyPhysical(URI physicalURI) throws OWLException;
	
	/**
	 * Loads an OWLOntology with given physical URI (in fact an
	 * URL), somewhere on the web or in the file system.
	 * In order to learn how to open a local file, just try to open a file with your favorite web browser.
	 * On Windows machines the syntax follows the pattern: file:///<b>drive</b>:/<b>directory</b>/<b>file</b>
	 *
	 * @param physicalURI  the physical URI of the OWLOntology
	 */OWLOntology loadOntologyPhysical(URI physicalURI) throws OWLException;
	
	/**
	 * Reloads an OWLOntology with given physical URI (in fact an
	 * URL), somewhere on the web or in the file system. Will not safe any changes to the ontology before the
	 * reload occurs.
	 * In order to learn how to open a local file, just try to open a file with your favorite web browser.
	 * On Windows machines the syntax follows the pattern: file:///<b>drive</b>:/<b>directory</b>/<b>file</b>
	 *
	 * @param physicalURI  the physical URI of the OWLOntology
	 */
	OWLOntology reloadOntologyPhysical(URI physicalURI) throws OWLException;

	/**
	 * Opens an OWLOntology with given logical URI. The logical
	 * URI is the default namespace of an ontology and also used
	 * to locate an ontology, e.g. in a database of ontologies
	 * @deprecated
	 * @param logicalURI the logical URI of the OWLOntology
	 */
	OWLOntology openOntologyLogical(URI logicalURI) throws OWLException;
	
	/**
	 * Returns an OWLOntology with given logical URI. The logical
	 * URI is the default namespace of an ontology and also used
	 * to locate an ontology, e.g. in a database of ontologies
	 *
	 * @param logicalURI the logical URI of the OWLOntology
	 */
	OWLOntology getOntologyLogical(URI logicalURI) throws OWLException;
	
	/**
	 * Creates an OWLOntology with a given physical and logical URI.
	 * Use createOntology instead.
	 * @deprecated
	 * @param physicalURI                       the physical URI of the OWLOntology
	 * @param logicalURI                        the logical URI of the OWLOntology
	 */
	OWLOntology createOWLOntology(URI physicalURI, URI logicalURI)
		throws OWLException;
		
	/**
	 * Creates an OWLOntology with a given physical and logical URI.
	 *
	 * @param physicalURI                       the physical URI of the OWLOntology
	 * @param logicalURI                        the logical URI of the OWLOntology
	 */
	OWLOntology createOntology(URI physicalURI, URI logicalURI)
		throws OWLException;




	/**
	 * Notifies the connection that the OWLOntology has been deleted.
	 *
	 * @param OWLOntology OWLOntology that is removed
	 */
	void notifyOntologyDeleted(OWLOntology ontology) throws OWLException;

         /** Forget about any ontologies that may have been created
	 * and reinitialise any datafactories.
	 *
	 * 
	 */
	void dropAllOntologies() throws OWLException;

	/**
	 * Returns the set of OWL Ontologies that are loaded by this
	 * OWLConnection. 
	 *
	 * @return the set of OWLOntology objects 
	 *         represented by this OWLConnection
	 */
	Set getAllOntologies() throws OWLException;
	/**
	 * Returns the set of logical URIs used as identifiers of the ontologies loaded by this connection.
	 *
	 * @return  Set(URI)
	 */
	Set getAllLogicalURIs() throws OWLException;
	/**
	 * Returns the set of all open OWLOntology objects.
	 * @deprecated
	 * @return                                  the set of all open OWLOntology objects
	 */
	Set getOpenOWLOntologies();
	/**
	 * Applies the list of changes to the ontologies loaded by this connection.
	 *
	 * @param changeList                list of changes to the models in the connection
	 */
	void applyChanges(List changeList) throws OWLException;

    /** Given an ontology, returns a change visitor that will enact
     * changes over that ontology. May throw an exception if unable to
     * provide such an implementation. */
    public ChangeVisitor getChangeVisitor( OWLOntology o ) throws OWLException;
}
