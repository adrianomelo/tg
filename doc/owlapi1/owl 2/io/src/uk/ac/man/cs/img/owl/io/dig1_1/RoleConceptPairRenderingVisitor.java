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
 * Filename         $RCSfile: RoleConceptPairRenderingVisitor.java,v $ 
 * Revision         $Revision: 1.3 $
 * Release status   $State: Exp $ 
 * Last modified on $Date: 2004/06/03 14:43:16 $ 
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
 * to the concept in a {@link org.kr.dl.dig.v1_1.RoleConceptPair}.
 * @author dturi
 * $Id: RoleConceptPairRenderingVisitor.java,v 1.3 2004/06/03 14:43:16 dturi Exp $
 */
public class RoleConceptPairRenderingVisitor extends AbstractDescriptionRenderingVisitor {

    RoleConceptPair roleConceptPair;
    
	/**
     * Sets the {@link RoleConceptPair} to be populated 
     * while visiting the description 
     * to <code>roleConceptPair</code>.
     * @param roleConceptPair a {@link RoleConceptPair} 
     */
	public RoleConceptPairRenderingVisitor(RoleConceptPair roleConceptPair) {
		this.roleConceptPair = roleConceptPair;
    }
    
    /**
     * Implementing specification.
     * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewTop()
     */
    public XmlObject addNewTop() {
        return roleConceptPair.addNewTop();
    }
   
    /**
     * Implementing specification.
     * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewBottom()
     */
    public XmlObject addNewBottom() {
        return roleConceptPair.addNewBottom();
    }

	
	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewCatom()
	 */
	public Named addNewCatom() {
		return roleConceptPair.addNewCatom();
	}

	
	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewAnd()
	 */
	public Concepts addNewAnd() {
		return roleConceptPair.addNewAnd();
	}

	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewOr()
	 */
	public Concepts addNewOr() {
		return roleConceptPair.addNewOr();
	}

	
	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewNot()
	 */
	public Concept addNewNot() {
		return roleConceptPair.addNewNot();
	}

	
	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewIset()
	 */
	public Individuals addNewIset() {
		return roleConceptPair.addNewIset();
	}

	
	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewSome()
	 */
	public RoleConceptPair addNewSome() {
		return roleConceptPair.addNewSome();
	}

	
	/**
	 * Implementing specification.
	 * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewAll()
	 */
	public RoleConceptPair addNewAll() {
		return roleConceptPair.addNewAll();
	}
    
    /**
     * Adds a new <code>atmost</code> to <code>roleConceptPair</code>.
     * @return the {@link NumRoleConceptPair} resulting from adding
     * a new <code>atmost</code> to <code>roleConceptPair</code>.
     */
    public NumRoleConceptPair addNewAtmost() {
        return roleConceptPair.addNewAtmost();
    }
    
    /**
     * Adds a new <code>atleast</code> to <code>roleConceptPair</code>.
     * @return the {@link NumRoleConceptPair} resulting from adding
     * a new <code>atleast</code> to <code>roleConceptPair</code>.
     */
    public NumRoleConceptPair addNewAtleast() {
        return roleConceptPair.addNewAtleast();
    }

    /**
     * Implementing specification.
     * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewStringequals()
     */
    public StringequalsDocument.Stringequals addNewStringequals() {
        return roleConceptPair.addNewStringequals();
    }
    
    /**
     * Implementing specification.
     * @see uk.ac.man.cs.img.owl.io.dig1_1.AbstractDescriptionRenderingVisitor#addNewIntequals()
     */
    public IntequalsDocument.Intequals addNewIntequals() {
        return roleConceptPair.addNewIntequals();
    }
}
