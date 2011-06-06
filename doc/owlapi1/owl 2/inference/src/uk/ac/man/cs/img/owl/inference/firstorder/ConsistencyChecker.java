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
 * Filename           $RCSfile: ConsistencyChecker.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:15 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.inference.firstorder;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.inference.OWLConsistencyChecker;
import java.util.Set;
import uk.ac.man.cs.img.owl.io.tptp.Renderer;
import java.io.StringWriter;
import java.io.Writer;


// Generated package name


/**
 * An OWL consistency checker. Will report on the consistency of an
 * OWL ontology (e.g. whether it is possible for the knowledge base to
 * be satisfiable). 

 * @author Sean Bechhofer
 * @version $Id: ConsistencyChecker.java,v 1.1.1.1 2003/10/14 17:10:15 sean_bechhofer Exp $
 */

public class ConsistencyChecker implements OWLConsistencyChecker
{
    /** Knowledge base consistency test. Returns {@link #CONSISTENT CONSISTENT} if it is
     * possible to have a model. Returns {@link #INCONSISTENT INCONSISTENT} if it is not
     * possible to have a model. Returns {@link #UNKNOWN UNKNOWN} if it can't tell.
     */

    public int consistency( OWLOntology onto ) throws OWLException {
	/* Just try and convert it */
	try {
	    Renderer renderer = new Renderer();
	    Writer writer = new StringWriter();
	    renderer.renderOntology( onto, writer );
	} catch (Exception ex) {
	    /* If any kind of exception occurs, just return unknown */
	    return UNKNOWN;
	}
	
	/* Try and do something sensible now.... */

	return UNKNOWN;
    }

} // ConsistencyChecker



/*
 * ChangeLog
 * $Log: ConsistencyChecker.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:15  sean_bechhofer
 * Initial Import
 *
 * Revision 1.3  2003/06/16 12:50:41  seanb
 * Small change to reasoning
 *
 * Revision 1.2  2003/06/11 16:48:37  seanb
 * Changes to TPTP renderer.
 *
 * Revision 1.1  2003/06/03 17:01:53  seanb
 * Additional inference
 *
 * Revision 1.3  2003/05/29 09:07:28  seanb
 * Moving RDF error handler
 *
 * Revision 1.2  2003/05/27 08:43:57  seanb
 * Some name changes, introduction of DL/Lite ontologies.
 *
 * Revision 1.1  2003/05/19 11:32:26  seanb
 * Interfaces for reasoning and inference.
 *
 */
