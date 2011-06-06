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
 * Filename           $RCSfile: TestUtils.java,v $
 * Revision           $Revision: 1.1.1.1 $
 * Release status     $State: Exp $
 * Last modified on   $Date: 2003/10/14 17:10:20 $
 *               by   $Author: sean_bechhofer $
 ****************************************************************/

package uk.ac.man.cs.img.owl.validation; 
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.change.ChangeVisitor;
import java.net.URI;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.change.OntologyChange;
import org.semanticweb.owl.model.change.AddEntity;
import org.semanticweb.owl.model.OWLEntity;

// Generated package name


/**
 * TestUtils.java
 *
 *
 * Created: Thu Feb 06 16:09:32 2003
 *
 * @author Sean Bechhofer
 * @version $Id: TestUtils.java,v 1.1.1.1 2003/10/14 17:10:20 sean_bechhofer Exp $
 */

public class TestUtils 
{

    /** Add a class with the given URI to the ontology. */
    public static OWLClass addClass( OWLOntology o,
				     OWLDataFactory f,
				     ChangeVisitor v,
				     URI uri ) throws OWLException {
	OWLClass clazz = f.getOWLClass( uri );
	OntologyChange evt = new AddEntity( o,
					    clazz,
					    null );
	evt.accept( v );
	return clazz;
    }

    /** Add the class to the ontology. */
    public static void addEntity( OWLOntology o,
			     OWLDataFactory f,
			     ChangeVisitor v,
			     OWLEntity e ) throws OWLException {
	OntologyChange evt = new AddEntity( o,
					    e,
					    null );
	evt.accept( v );
    }
    
    /** Add an individual with the given URI to the ontology. */
    public static OWLIndividual addIndividual( OWLOntology o,
					       OWLDataFactory f,
					       ChangeVisitor v,
					       URI uri ) throws OWLException {
	OWLIndividual ind = f.getOWLIndividual( uri );
	OntologyChange evt = new AddEntity( o,
					    ind,
					    null );
	evt.accept( v );
	return ind;
    }
    
    
} // TestUtils



/*
 * ChangeLog
 * $Log: TestUtils.java,v $
 * Revision 1.1.1.1  2003/10/14 17:10:20  sean_bechhofer
 * Initial Import
 *
 * Revision 1.1  2003/02/12 09:58:26  seanb
 * Moving Tests.
 *
 * Revision 1.2  2003/02/06 18:39:17  seanb
 * Further Validation Tests.
 *
 * Revision 1.1  2003/02/06 16:44:26  seanb
 * More validation tests
 *
 */
