/*
 * This file is part of the OWL API.
 *
 * The contents of this file are subject to the LGPL License, Version 3.0.
 *
 * Copyright (C) 2011, The University of Manchester
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0
 * in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 *
 * Copyright 2011, The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.coode.owlapi.examples;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasoner;
import uk.ac.manchester.cs.owlapi.modularity.ModuleType;
import uk.ac.manchester.cs.owlapi.modularity.SyntacticLocalityModuleExtractor;

import java.util.HashSet;
import java.util.Set;


/**
 * Author: Thomas Schneider<br>
 * The University Of Manchester<br>
 * Information Management Group<br>
 * Date: 20-Sep-2010<br><br>
 * <p/>
 * This example shows how to extract modules.
 */
public class Example16 {

    public static final String DOCUMENT_IRI = "http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl";


    public static void main(String[] args) {
        try {
            // Create our manager
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();

            // Load the pizza ontology
            OWLOntology ont = man.loadOntologyFromOntologyDocument(IRI.create(DOCUMENT_IRI));
            System.out.println("Loaded: " + ont.getOntologyID());

            // We want to extract a module for all toppings.
            // We therefore have to generate a seed signature that contains "PizzaTopping" and its subclasses.
            // We start by creating a signature that consists of "PizzaTopping".
            OWLDataFactory df = man.getOWLDataFactory();
            OWLClass toppingCls = df.getOWLClass(IRI.create(ont.getOntologyID().getOntologyIRI().toString() + "#PizzaTopping"));
            Set<OWLEntity> sig = new HashSet<OWLEntity>();
            sig.add(toppingCls);

            // We now add all subclasses (direct and indirect) of the chosen classes.
            // Ideally, it should be done using a DL reasoner, in order to take inferred subclass relations into account.
            // We are using the structural reasoner of the OWL API for simplicity.
            Set<OWLEntity> seedSig = new HashSet<OWLEntity>();
            OWLReasoner reasoner = new StructuralReasoner(ont, new SimpleConfiguration(), BufferingMode.NON_BUFFERING);
            for (OWLEntity ent : sig) {
                seedSig.add(ent);
                if (OWLClass.class.isAssignableFrom(ent.getClass())) {
                    NodeSet<OWLClass> subClasses = reasoner.getSubClasses((OWLClass) ent, false);
                    seedSig.addAll(subClasses.getFlattened());
                }
            }

            // Output for debugging purposes
            System.out.println();
            System.out.println("Extracting the module for the seed signature consisting of the following entities:");
            for (OWLEntity ent : seedSig) {
                System.out.println("  " + ent);
            }
            System.out.println();
            System.out.println("Some statistics of the original ontology:");
            System.out.println("  " + ont.getSignature(true).size() + " entities");
            System.out.println("  " + ont.getLogicalAxiomCount() + " logical axioms");
            System.out.println("  " + (ont.getAxiomCount() - ont.getLogicalAxiomCount()) + " other axioms");
            System.out.println();

            // We now extract a locality-based module.
            // For most reuse purposes, the module type should be STAR -- this yields the smallest possible locality-based module.
            // These modules guarantee that all entailments of the original ontology that can be formulated using only
            // terms from the seed signature or the module will also be entailments of the module.
            // In easier words, the module preserves all knowledge of the ontology about the terms in the seed signature or the module.
            SyntacticLocalityModuleExtractor sme = new SyntacticLocalityModuleExtractor(man, ont, ModuleType.STAR);
            IRI moduleIRI = IRI.create("file:/tmp/PizzaToppingModule.owl");
            OWLOntology mod = sme.extractAsOntology(seedSig, moduleIRI);

            // Output for debugging purposes
            System.out.println("Some statistics of the module:");
            System.out.println("  " + mod.getSignature(true).size() + " entities");
            System.out.println("  " + mod.getLogicalAxiomCount() + " logical axioms");
            System.out.println("  " + (mod.getAxiomCount() - mod.getLogicalAxiomCount()) + " other axioms");
            System.out.println();

            // And we save the module.
            System.out.println("Saving the module as " + mod.getOntologyID().getOntologyIRI() + " .");
            man.saveOntology(mod);
        }
        catch (OWLOntologyCreationException e) {
            System.out.println("Could not create ontology.");
        } catch (OWLOntologyStorageException e) {
            System.out.println("Could not save ontology.");
        }
    }
}