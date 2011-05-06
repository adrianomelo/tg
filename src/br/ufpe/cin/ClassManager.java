package br.ufpe.cin;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;

public class ClassManager {
	private Vector<OWLClass> classes = null;
	
	ClassManager(){
		this.classes = new Vector<OWLClass>();
	}
	
	boolean addClass(OWLClass cls){
		return classes.add(cls);
	}
	
	boolean addClass(Set<OWLClass> clss){
		return classes.addAll(clss);
	}

	Iterator<OWLClass> getIterator(){
		return classes.iterator();
	}
	
	/*
	 * métodos para criar o singleton
	 */
	private static ClassManager instance = null;
	public static ClassManager getInstance(){
		if (instance == null)
			instance = new ClassManager();
		return instance;
	}
}
