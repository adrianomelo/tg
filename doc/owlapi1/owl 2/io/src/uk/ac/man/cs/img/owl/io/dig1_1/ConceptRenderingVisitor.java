/*
 * Copyright (C) 2003 The University of Manchester 
 * 
 * Modifications to the initial code base are copyright of their respective
 * authors, or their employers as appropriate. Authorship of the modifications
 * may be determined from the ChangeLog placed at the end of this file.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 ******************************************************************************
 * Source code information
 * Filename         $RCSfile: ConceptRenderingVisitor.java,v $ 
 * Revision         $Revision: 1.4 $
 * Release status   $State: Exp $ 
 * Last modified on $Date: 2004/06/18 10:55:50 $ 
 *               by $Author: dturi $
 ******************************************************************************/
package uk.ac.man.cs.img.owl.io.dig1_1;

import org.apache.xmlbeans.XmlObject;
import org.kr.dl.dig.v1_1.Concept;
import org.kr.dl.dig.v1_1.Concepts;
import org.kr.dl.dig.v1_1.Individuals;
import org.kr.dl.dig.v1_1.IntequalsDocument;
import org.kr.dl.dig.v1_1.Named;
import org.kr.dl.dig.v1_1.NumRoleConceptPair;
import org.kr.dl.dig.v1_1.RoleConceptPair;
import org.kr.dl.dig.v1_1.StringequalsDocument;
import org.semanticweb.owl.model.OWLDescription;

/**
 * Visits an {@link OWLDescription} and adds its content
 * to {@link org.kr.dl.dig.v1_1.Concept}.
 * @author Daniele Turi
 * @version $Id: ConceptRenderingVisitor.java,v 1.4 2004/06/18 10:55:50 dturi Exp $
 */
public class ConceptRenderingVisitor extends AbstractDescriptionRenderingVisitor {

	Concept concept;
    
    /**
     * Returns the concept.
     * @return the concept.
     */
    public Concept getConcept() {
        return concept;
    }
    
    /**
     * Sets the concept.
     * @param concept The concept to set.
     */
    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    /**
     * Sets the {@link Concept} to be populated while visiting the description to
     * <code>concept</code>.
     * @param concept a {@link Concept} 
     */
	public ConceptRenderingVisitor(Concept concept) {
		this.concept = concept;
	}
    
    public ConceptRenderingVisitor() {
        this.concept = Concept.Factory.newInstance();
        
    }
    
    /**
     * Implementing specification.
     * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewTop()
     */
    public XmlObject addNewTop() {
        return concept.addNewTop();
    }
   
    /**
     * Implementing specification.
     * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewBottom()
     */
    public XmlObject addNewBottom() {
        return concept.addNewBottom();
    }

	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewCatom()
	 */
	public Named addNewCatom() {
		return concept.addNewCatom();
	}

	
	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewAnd()
	 */
	public Concepts addNewAnd() {
		return concept.addNewAnd();
	}

	
	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewOr()
	 */
	public Concepts addNewOr() {
		return concept.addNewOr();
	}

	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewNot()
	 */
	public Concept addNewNot() {
		return concept.addNewNot();
	}

	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewIset()
	 */
	public Individuals addNewIset() {
		return concept.addNewIset();
	}

	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewSome()
	 */
	/**
	 * @return
	 */
	public RoleConceptPair addNewSome() {
		return concept.addNewSome();
	}

	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewAll()
	 */
	public RoleConceptPair addNewAll() {
		return concept.addNewAll();
	}
    
    /**
     * Adds a new <code>atmost</code> to <code>concept</code>.
     * @return the {@link NumRoleConceptPair} resulting from adding
     * a new <code>atmost</code> to <code>concept</code>.
     */
    public NumRoleConceptPair addNewAtmost() {
    	return concept.addNewAtmost();
    }
    
    /**
     * Adds a new <code>atleast</code> to <code>concept</code>.
     * @return the {@link NumRoleConceptPair} resulting from adding
     * a new <code>atleast</code> to <code>concept</code>.
     */
    public NumRoleConceptPair addNewAtleast() {
        return concept.addNewAtleast();
    }

    /**
     * Implementing specification.
     * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewStringequals()
     */
    public StringequalsDocument.Stringequals addNewStringequals() {
        return concept.addNewStringequals();
    }
    
    /**
     * Implementing specification.
     * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewIntequals()
     */
    public IntequalsDocument.Intequals addNewIntequals() {
        return concept.addNewIntequals();
    }
    
}