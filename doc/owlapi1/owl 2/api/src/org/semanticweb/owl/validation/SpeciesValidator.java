/*
 * Copyright (C) 2005, University of Manchester
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

package org.semanticweb.owl.validation; // Generated package name

import java.io.Writer;
import java.io.Reader;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OWLConnection;
import java.util.Map;
import java.net.URI;

/**
 * Provides functionality for identifying the particular species that
 * an ontology belongs to.
 *
 *
 * @author Sean Bechhofer
 * @version $Id: SpeciesValidator.java,v 1.4 2005/06/10 12:20:29 sean_bechhofer Exp $ 
 */

public interface SpeciesValidator 
{
    /**
     * Set options for this validator
     * @param options a <code>Map</code> value. Should contain a map from {@link String String}s to {@link String String}s.
     */
    public void setOptions( Map options );

    /**
     * 
     * Get options for this validator
     * @return a <code>Map</code> value. Contains a map from {@link String String}s to {@link String String}s.
     */
    public Map getOptions();

    /** Provides a writer which will be used to record explanations
     * for the verdict arrived at. */
    public void setReporter ( SpeciesValidatorReporter reporter );

    
    public boolean isOWLLite( OWLOntology ontology ) throws OWLException;
    public boolean isOWLDL( OWLOntology ontology ) throws OWLException;
    public boolean isOWLFull( OWLOntology ontology ) throws OWLException;


    /** Set the connection (e.g. the implementation that the validator
    will choose to use when constructing ontologies. */ 

    public void setConnection( OWLConnection connection );

    /* Validate from a URL. Assumes OWL-RDF */
    public boolean isOWLLite( URI uri ) throws OWLException;
    public boolean isOWLDL( URI uri ) throws OWLException;
    public boolean isOWLFull( URI uri) throws OWLException;

    /* Validate from a Reader. Assumes OWL-RDF */
    public boolean isOWLLite( Reader r, URI physicalURI ) throws OWLException;
    public boolean isOWLDL( Reader r, URI physicalURI ) throws OWLException;
    public boolean isOWLFull( Reader r, URI physicalURI) throws OWLException;

}// SpeciesValidator


/*
 * ChangeLog
 * $Log: SpeciesValidator.java,v $
 * Revision 1.4  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.3  2004/07/09 12:07:47  sean_bechhofer
 * Addition of functionality to access usage, e.g. where classes, properties etc
 * are used within the ontology.
 *
 * Revision 1.2  2003/12/19 12:04:16  sean_bechhofer
 * Refactoring of the Connection/Manager stuff. Providing a single access
 * point to connection creation through OWLManager. Some test code has
 * also been amended to cope with the fact that successive test runs may use
 * the same logical/physical URIs as previous test, so the maps used by the
 * connection may need to be reset.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:10  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/03/20 10:28:34  seanb
 * Number of changes and updates to support RDF parsing and further
 * improvements to default implementation.
 *
 * Revision 1.2  2003/02/13 18:45:49  seanb
 * Improved validation and parsing.
 *
 * Revision 1.1  2003/01/29 14:30:20  seanb
 * Initial Checkin
 *
 *
 */

