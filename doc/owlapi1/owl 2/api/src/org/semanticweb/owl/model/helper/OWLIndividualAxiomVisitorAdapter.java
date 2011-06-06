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

package org.semanticweb.owl.model.helper; // Generated package name

import org.semanticweb.owl.model.*;
import org.apache.log4j.Logger;

/**
 * Provides an empty implementation of the {@link OWLIndividualAxiomVisitor OWLIndividualAxiomVisitor} interface. Those wishing to implement a visitor over subsets of the data structure can extend this class and provide the methods they need. 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLIndividualAxiomVisitorAdapter.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class OWLIndividualAxiomVisitorAdapter implements OWLIndividualAxiomVisitor
{

    static Logger logger = Logger.getLogger(OWLIndividualAxiomVisitorAdapter.class);
    
    
    public void visit( OWLSameIndividualsAxiom node ) throws OWLException
    {
	logger.debug("Default SameIndividuals");
	return;
    }
    
    public void visit( OWLDifferentIndividualsAxiom node ) throws OWLException
    {
	logger.debug("Default DifferentIndividuals");
	return;
    }
    
    
} // OWLIndividualAxiomVisitorAdapter



/*
 * ChangeLog
 * $Log: OWLIndividualAxiomVisitorAdapter.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/05/08 07:54:34  seanb
 * Some changes to the visitor hierarchies
 * Addition of datatypes
 * Fixing validation errors relating to top level intersections
 * Improved rendering (including subproperties)
 * Adding FaCT rendering
 *
 *
 *
 */
