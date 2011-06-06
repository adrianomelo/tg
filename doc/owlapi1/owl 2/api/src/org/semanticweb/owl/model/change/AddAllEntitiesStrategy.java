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
 * Filename           $RCSfile: AddAllEntitiesStrategy.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:09 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package org.semanticweb.owl.model.change; 
import org.semanticweb.owl.model.OWLException;


import java.util.Iterator;


import org.semanticweb.owl.model.OWLEntity;


import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;

// Generated package name


/**
 * AddAllEntitiesStrategy.java
 *
 *
 * Created: Fri Feb 14 09:25:12 2003
 *
 * @author Sean Bechhofer
 * @version $Id: AddAllEntitiesStrategy.java,v 1.1.1.1 2003/10/14 17:10:09 sean_bechhofer Exp $
 */

public class AddAllEntitiesStrategy implements EvolutionStrategy
{
    static Logger logger = Logger.getLogger(AddAllEntitiesStrategy.class);

    public List computeChanges( List list ) throws OWLException {
	List newList = new ArrayList();
	OntologyChangeEntityCollector collector = new OntologyChangeEntityCollector();
	for ( Iterator it = list.iterator();
	      it.hasNext(); ) {
	    OntologyChange oc = 
		(OntologyChange) it.next();
	    /*  Keep the event in the list */
	    newList.add( oc );
	    logger.debug("Old: " + oc);
	    
	    collector.reset();
	    oc.accept( collector );
	    
	    for ( Iterator entit = collector.getEntities().iterator();
		  entit.hasNext(); ) {
		OWLEntity entity = (OWLEntity) entit.next();
		/* Build a new ontology change event with the same
		 * ontology, the entity to be added, and the original
		 * event as cause. */
		AddEntity ae = new AddEntity( oc.getOntology(), entity, oc );
		newList.add( ae );
		logger.debug("New: " + oc);

	    }
	}
	return newList;
    }
    
} // AddAllEntitiesStrategy



/*
 * ChangeLog
 * $Log: AddAllEntitiesStrategy.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:09  sean_bechhofer
 * Initial Import
 *
 * Revision 1.2  2003/02/17 18:23:54  seanb
 * Further parsing fixes and extensions.
 *
 * Revision 1.1  2003/02/14 17:51:23  seanb
 * Improvements to Validation and updating data structures.
 *
 */
