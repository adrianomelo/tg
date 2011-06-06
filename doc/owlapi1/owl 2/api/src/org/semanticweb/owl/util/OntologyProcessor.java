/*
 * Copyright (C) 2005, University of Karlsruhe
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

package org.semanticweb.owl.util;


import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collections;

import org.semanticweb.owl.model.*;


/**
 * A utility class for processing OWLOntologies in chunks.
 * The progress of the processing can be monitored by attaching
 * a ProgressListener.
 *
 * @author Raphael Volz (volz@aifb.uni-karlsruhe.de)
 * @author Boris Motik (boris.motik@fzi.de)
 */
public abstract class OntologyProcessor {
    /** The size of the chunks for processing. */
    protected static final int CHUNK_SIZE=600;

    /** The progress listener of the processor. */
    protected ProgressListener m_progressListener;

    /**
     * Creates an instance of this class.
     */
    public OntologyProcessor() {
        m_progressListener=NullProgressListener.INSTANCE;
    }
    /**
     * Registers a progress listener.
     *
     * @param progressListener                  the listener for processor progress
     */
    public void setProgressListener(ProgressListener progressListener) {
        m_progressListener=progressListener;
    }
    /**
     * Loads and processes elements from the given set in chunks.
     *
     * @param set                               the original set of objects
     * @param ontology                          the OWLOntology that loads elements (may be <code>null</code> - loading is then not done)
     * @param loadFlag                          the flag specifying which information should be loaded
     * @param objectProcessor                   receives objects to be processed
     * @param progressPhase                     the phase of the replication
     * @throws OWLException                     thrown if there is an error
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void processElements(Set set,OWLOntology ontology,int loadFlag,ObjectProcessor objectProcessor,int progressPhase) throws OWLException,InterruptedException {
        int processed=0;
        m_progressListener.processorProgress(progressPhase,processed,set.size());
        Set objectsToLoad=new HashSet();
        Iterator objects=set.iterator();
        while (objects.hasNext()) {
            OWLObject entity=(OWLObject)objects.next();
            objectsToLoad.add(entity);
            if (objectsToLoad.size()==CHUNK_SIZE) {
                if (ontology!=null)
  //                  ontology.loadObjects(objectsToLoad,loadFlag);
                objectProcessor.processLoadedObjects(objectsToLoad);
                processed+=objectsToLoad.size();
                objectsToLoad.clear();
                m_progressListener.processorProgress(progressPhase,processed,set.size());
            }
            checkInterrupted();
        }
        if (!objectsToLoad.isEmpty()) {
            if (ontology!=null)
 //               ontology.loadObjects(objectsToLoad,loadFlag);
            objectProcessor.processLoadedObjects(objectsToLoad);
            processed+=objectsToLoad.size();
            m_progressListener.processorProgress(progressPhase,processed,set.size());
        }
    }
    /**
     * Checks whether the thread has been interrupted and throws an exception.
     *
     * @throws InterruptedException             thrown if the replicator has been interrupted
     */
    protected void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted())
            throw new InterruptedException();
    }

    /**
     * Processor for loaded objects.
     */
    protected interface ObjectProcessor {
        void processLoadedObjects(Set objects) throws OWLException,InterruptedException;
    }

    /**
     * The interface receiving notification about the progress of the processor.
     */
    public interface ProgressListener {
        void processorProgress(int phase,int done,int steps);
    }

    /**
     * Empty progress listener.
     */
    protected static class NullProgressListener implements ProgressListener {
        public static final ProgressListener INSTANCE=new NullProgressListener();

        public void processorProgress(int phase,int done,int steps) {
        }
    }
}

