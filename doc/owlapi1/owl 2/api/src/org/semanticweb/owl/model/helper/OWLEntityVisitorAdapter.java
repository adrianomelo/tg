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
 * Provides an empty implementation of the {@link OWLEntityVisitor OWLEntityVisitor} interface. Those wishing to implement a visitor over subsets of the data structure can extend this class and provide the methods they need. Default behaviour of this adapter is to do nothing except log the visit to an appropriate logger. 
 *
 * @author Sean Bechhofer
 * @version $Id: OWLEntityVisitorAdapter.java,v 1.2 2005/06/10 12:20:29 sean_bechhofer Exp $
 */

public class OWLEntityVisitorAdapter implements OWLEntityVisitor
{

    static Logger logger = Logger.getLogger(OWLEntityVisitorAdapter.class);
    
    public void visit( OWLDataProperty entity ) throws OWLException
    {
	logger.debug("Default DataProperty");
	return;
    }

    public void visit( OWLObjectProperty entity ) throws OWLException
    {
	logger.debug("Default ObjectProperty");
	return;
    }

    public void visit( OWLAnnotationProperty entity ) throws OWLException
    {
	logger.debug("Default AnnotationProperty");
	return;
    }

    public void visit( OWLIndividual entity ) throws OWLException
    {
	logger.debug("Default Individual");
	return;
    }

    public void visit( OWLClass entity ) throws OWLException
    {
	logger.debug("Default Class");
	return;
    }

    
    
} // OWLEntityVisitorAdapter



/*
 * ChangeLog
 * $Log: OWLEntityVisitorAdapter.java,v $
 * Revision 1.2  2005/06/10 12:20:29  sean_bechhofer
 * Housekeeping license information to consistent LGPL.
 *
 * Revision 1.1  2004/07/09 14:04:58  sean_bechhofer
 * More usage related functionality. Updates to HTML Presentation Servlet
 *
 *
 *
 */
