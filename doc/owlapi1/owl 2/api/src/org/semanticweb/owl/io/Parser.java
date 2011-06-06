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

package org.semanticweb.owl.io; 
import org.semanticweb.owl.model.OWLOntology;
import java.io.Reader;
import java.net.URI;
import org.semanticweb.owl.util.OWLConnection;

// Generated package name


/**
 * Parse some concrete representation into an OWL data structure.
 *
 *
 * Created: Thu Dec 19 15:55:21 2002
 *
 * @author Sean Bechhofer
 * @version $Id: Parser.java,v 1.2 2005/06/10 12:20:28 sean_bechhofer Exp $
 */

public interface Parser extends Options
{
    /** Set the connection used to create new
     * ontologies. Implementations of this interface may choose to
     * provide access to a default implementation. If required,
     * however, the implementation used can be directly set using this
     * method. */
    public void setConnection( OWLConnection connection );

    /** Get the connection used to create new ontologies. */
    public OWLConnection getConnection();

    /** Parse from the given reader and return a new ontology object
     * based on the connection that the parser knows about. We
     * also require that a physical URI is provided for the resulting
     * ontology. */
    public OWLOntology parseOntology( Reader reader,
				      URI physicalURI ) throws ParserException;

    /** Parse from the given URI and return a new ontology object
     * based on the connection that the parser knows about. */
    public OWLOntology parseOntology( URI uri ) throws ParserException;

//     /* Parse from the given reader, storing the relevant facts in the
//      * given ontology. */
//     public void parseOntology( OWLOntology ontology,
// 			       Reader reader,
// 			       URI physicalURI) throws ParserException;

//     /* Parse from the given URI, storing the relevant facts in the
//      * given ontology. */
//     public void parseOntology( OWLOntology ontology,
// 			       URI uri ) throws ParserException;

} // Parser



/*
 * ChangeLog
 * $Log: Parser.java,v $
 * Revision 1.2  2005/06/10 12:20:28  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:08  sean_bechhofer
 * Initial Import
 *
 * Revision 1.4  2003/10/02 14:33:05  bechhofers
 * Removal of blind methods from Ontology objects.
 *
 * Revision 1.3  2003/10/02 09:37:23  bechhofers
 * Cleaning up access to Connection. Addition of Servlet Test validator.
 *
 * Revision 1.2  2003/10/01 16:37:32  bechhofers
 * Refactoring of Parser to return new ontology objects.
 *
 * Revision 1.1  2003/01/29 14:30:18  seanb
 * Initial Checkin
 *
 */
