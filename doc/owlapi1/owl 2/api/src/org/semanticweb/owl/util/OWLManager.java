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

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A manager for factories of OWL Ontologies.
 * This is the central class for getting access
 * to OWLOntology implementations and thereby
 * to OWLOntologies.
 *
 * @author Raphael Volz (volz@aifb.uni-karlsruhe.de)
 * @author Boris Motik (boris.motik@fzi.de)
 */
public class OWLManager {
    /** Load basic data of a concept. */
    final int LOAD_CONCEPT_BASICS = 0x00000001;
    /** Load superconcepts. */
    final int LOAD_SUPER_CONCEPTS = 0x00000002;
    /** Load subconcepts. */
    final int LOAD_SUB_CONCEPTS = 0x00000004;
    /** Load properties from the concept. */
    final int LOAD_PROPERTIES_FROM = 0x00000008;
    /** Load properties to the concept. */
    final int LOAD_PROPERTIES_TO = 0x00000010;
    /** Load concept instances. */
    final int LOAD_CONCEPT_INSTANCES = 0x00000020;
    /** Load all concept properties. */
    final int LOAD_CONCEPT_ALL=LOAD_CONCEPT_BASICS | LOAD_SUPER_CONCEPTS | LOAD_SUB_CONCEPTS | LOAD_PROPERTIES_FROM | LOAD_PROPERTIES_TO | LOAD_CONCEPT_INSTANCES;

    /** Load basic data of a property. */
    final int LOAD_PROPERTY_BASICS = 0x00000040;
    /** Load superproperties. */
    final int LOAD_SUPER_PROPERTIES = 0x00000080;
    /** Load subproperties. */
    final int LOAD_SUB_PROPERTIES = 0x00000100;
    /** Load property domains. */
    final int LOAD_PROPERTY_DOMAINS = 0x00000200;
    /** Load property ranges. */
    final int LOAD_PROPERTY_RANGES = 0x00000400;
    /** Load property instances. */
    final int LOAD_PROPERTY_INSTANCES = 0x00000800;
    /** Load all property information. */
    final int LOAD_PROPERTY_ALL=LOAD_PROPERTY_BASICS | LOAD_SUPER_PROPERTIES | LOAD_SUB_PROPERTIES | LOAD_PROPERTY_DOMAINS | LOAD_PROPERTY_RANGES | LOAD_PROPERTY_INSTANCES;

    /** Load basic data of an instance. */
    final int LOAD_INSTANCE_BASICS = 0x00001000;
    /** Load instance parent concepts. */
    final int LOAD_INSTANCE_PARENT_CONCEPTS = 0x00002000;
    /** Load property values from instance. */
    final int LOAD_INSTANCE_FROM_PROPERTY_VALUES = 0x00004000;
    /** Load property values to instance. */
    final int LOAD_INSTANCE_TO_PROPERTY_VALUES = 0x00008000;
    /** Load all instance information. */
    final int LOAD_INSTANCE_ALL=LOAD_INSTANCE_BASICS | LOAD_INSTANCE_PARENT_CONCEPTS | LOAD_INSTANCE_FROM_PROPERTY_VALUES | LOAD_INSTANCE_TO_PROPERTY_VALUES;

    /** Load the lexicon of an entity. */
    final int LOAD_LEXICON = 0x00010000;

    /** Load all entity information. */
    final int LOAD_ENTITY_ALL=LOAD_CONCEPT_ALL | LOAD_PROPERTY_ALL | LOAD_INSTANCE_ALL | LOAD_LEXICON;

    /** Capability specifying that model is always saved. */
    public final static int CAPABILITY_ALWAYS_SAVED = 0x00000001;
    /** Capability specifying that model supports optimized loading. */
    public final static int CAPABILITY_SUPPORTS_OPTIMIZED_LOADING = 0x00000002;
    /** Capability specifying that model supports notifications. */
    public final static int CAPABILITY_SUPPORTS_NOTIFICATIONS = 0x00000004;

    /** Parameter specifying the OI-model connection class. */
    public static final String OWL_CONNECTION = "OWL_CONNECTION";

    /** Type specifier for our constructors. */
    protected static final Class[] OWL_CONNECTION_PARAMETERS=new Class[] { Map.class };

    /** The parameters for the RDF connections. */
    public static final Map s_parametersAPIOnRDF;
    static {
        Map parameters = new HashMap();
        parameters.put(OWL_CONNECTION,"edu.unika.aifb.owl.apionrdf.OWLConnectionImpl");
        s_parametersAPIOnRDF = Collections.unmodifiableMap(parameters);
    }

    /** The map of registered physical URI resolvers. */
    protected static final Map s_physicalURIToDefaultParametersResolvers=new HashMap();
    /** The list of registered logical URI resolvers. */
    protected static final List s_logicalURIResolvers = new ArrayList();

    static {
        // start well-known OWL implementations
        try {
            startOWLImplementation("edu.unika.aifb.kaon.apionrdf.OWLConnectionImpl");
        }
        catch (OWLException ignored) {
        }

        // More of this here...
    }

    /**
     * Starts the OWL API implementation with given name.
     *
     * @param connectionClassName               the name of the KAON Connection class
     * @throws OWLException                    thrown if the implementation cannot be started
     */
    public static void startOWLImplementation(String connectionClassName) throws OWLException {
        try {
            Class.forName(connectionClassName);
        }
        catch (ClassNotFoundException e) {
            throw new OWLException("Cannot load OWL connection class '"+connectionClassName+"'",e);
    }
    }
    /**
     * Returns a factory to use for given parameters.
     *
     * @param parameters                        parameters specifying the class
     * @return                                  the KAON connection for given parameters
     * @throws OWLException                    thrown if parameters are incorrect
     */
    public static OWLConnection getOWLConnection(Map parameters) throws OWLException {
        String connectionClass = (String)parameters.get(OWL_CONNECTION);
        if (connectionClass == null)
            throw new OWLException("Parameters must specify the connection class through 'KAON_CONNECTION' key.");
        try {
            Class clazz = Class.forName(connectionClass);
            Constructor constructor=clazz.getConstructor(OWL_CONNECTION_PARAMETERS);
            Object object=constructor.newInstance(new Object[] { parameters });
            if (!(object instanceof OWLConnection))
                throw new OWLException("Supplied object is not a OWLConnection.");
            return (OWLConnection)object;
        }
        catch (ClassNotFoundException e) {
            throw new OWLException("Cannot load KAON connection class '"+connectionClass+"'",e);
       }
        catch (NoSuchMethodException e) {
            throw new OWLException("Cannot instantiate KAON connection class '"+connectionClass+"'",e);
        }
        catch (InvocationTargetException e) {
            throw new OWLException("Cannot instantiate KAON connection class '"+connectionClass+"'",e);
        }
        catch (InstantiationException e) {
            throw new OWLException("Cannot instantiate KAON connection class '"+connectionClass+"'",e);
        }
        catch (IllegalAccessException e) {
            throw new OWLException("Cannot instantiate KAON connection class '"+connectionClass+"'",e);
        }
    }

    /** Returns a default implementation of connection which is determined at run time by the following methods:
    
    1. If System Property org.semanticweb.owl.util.OWLConnection is
       set, this determines the class to use
    
    2. If not, search the resource paths for the key
    org.semanticweb.owl.util.OWLConnection in META-INF/services. 
    
        2.1 If the resource is found, the file is read and the class is
            instantiated.
    
        2.2 If an exception is thrown while reading the file then
            the class is instantiated to 
            <code>org.semanticweb.owl.impl.model.OWLConnectionImpl</code>.
    */

    public static OWLConnection getOWLConnection() throws OWLException {
        /* Returns a new one every time */
        OWLConnection connection = null;
        try {
            Map parameters = new HashMap();
            String connectionImpl =
                System.getProperty("org.semanticweb.owl.util.OWLConnection");
            if (connectionImpl == null || connectionImpl.equals("")) {
                /* We have to go looking for an appropriate resource */
                ClassLoader cl = ClassLoader.getSystemClassLoader();
                try {
                    BufferedReader br =
                        new BufferedReader(
                            new InputStreamReader(
                                cl.getResourceAsStream(
                                    "META-INF/services/org.semanticweb.owl.util.OWLConnection")));
                    connectionImpl = br.readLine();
                } catch (Exception e) {
                    connectionImpl = "org.semanticweb.owl.impl.model.OWLConnectionImpl";
                }
            }
            if (connectionImpl != null && !connectionImpl.equals("")) {
                parameters.put(OWLManager.OWL_CONNECTION, connectionImpl);

                connection = OWLManager.getOWLConnection(parameters);
            }
        } catch (Exception e) {
	    throw new OWLException( "Cannot create new connection: " + e.getMessage() );
        }
        return connection;
    }

    /**
     * Returns the default parameters for given physical URI. This method is useful for opening models from any URI.
     * If default parameters cannot be created, an exception is thrown.
     *
     * @param physicalURI               the physical URI
     * @param contextParameters         the parameters that are used for missing elements (may be <code>null</code>)
     * @return                          the default parameters for accessing given physical URI (never <code>null</code>)
     * @throws OWLException            thrown if givan physical URI cannot be accessed
     */
    public static synchronized Map getDefaultParametersForPhysicalURI(String physicalURI,Map contextParameters) throws OWLException {
        Iterator iterator=s_physicalURIToDefaultParametersResolvers.values().iterator();
        while (iterator.hasNext()) {
            PhysicalURIToDefaultParametersResolver resolver=(PhysicalURIToDefaultParametersResolver)iterator.next();
            Map parameters=resolver.getDefaultParametersForPhysicalURI(physicalURI,contextParameters);
            if (parameters != null)
                return parameters;
        }
        throw new OWLException("Cannot resolve physical URI '"+physicalURI+"' to default parameters.");
    }
    /**
     * Registers a resolver from physical URI to default parameters. Only one resolver of each class can be registered.
     *
     * @param resolver                  the resolver
     */
    public static synchronized void registerPhysicalURIToDefaultParametersResolver(PhysicalURIToDefaultParametersResolver resolver) {
        String className = resolver.getClass().getName();
        s_physicalURIToDefaultParametersResolvers.put(className, resolver);
    }
    /**
     * Registers a resolver from physical URI to default parameters by its class name.
     *
     * @param resolverClassName         the name of the resolver class
     * @throws OWLException            thrown if resolver cannot be registered
     */
    public static synchronized void registerPhysicalURIToDefaultParametersResolver(String resolverClassName) throws OWLException {
        try {
            Class clazz = Class.forName(resolverClassName);
            Object object = clazz.newInstance();
            if (!(object instanceof PhysicalURIToDefaultParametersResolver))
                throw new OWLException("Supplied object is not a PhysicalURIToDefaultParametersResolver");
            registerPhysicalURIToDefaultParametersResolver((PhysicalURIToDefaultParametersResolver)object);
        }
        catch (ClassNotFoundException e) {
            throw new OWLException("Cannot load resolver class '"+resolverClassName+"'",e);
    }
        catch (InstantiationException e) {
            throw new OWLException("Cannot instantiate resolver class '"+resolverClassName+"'",e);
        }
        catch (IllegalAccessException e) {
            throw new OWLException("Cannot instantiate resolver class '"+resolverClassName+"'",e);
        }
    }
    /**
     * Tries to resolve a logical URI for the connection. This method is called when a connection is unable to obtain a model
     * with given logical URI. It then tries to resolve the logical URI to parameters and the physical URI. If model cannot be
     * opened through the same connection, then model is replicated.
     *
     * @param OWLConnection            the connection
     * @param logicalURI                the logical URI
     * @return                          the OI-model (<code>null</code> if logical URI cannot be resolved)
     * @throws OWLException            thrown if there is an error
     */
    public static OWLOntology resolveLogicalURI(OWLConnection OWLConnection,URI  logicalURI) throws OWLException {
        List resolvers;
        synchronized (OWLManager.class) {
            resolvers = new ArrayList(s_logicalURIResolvers);
        }
        for (int i = 0; i < resolvers.size(); i++) {
            LogicalURIResolver resolver = (LogicalURIResolver)resolvers.get(i);
            LogicalURIResolver.ResultHolder result=resolver.resolveLogicalURI(logicalURI);
            if (result != null) {
                if (canUseConnectionForParameters(OWLConnection,result.m_connectionParameters))
                    return OWLConnection.getOntologyPhysical(result.m_physicalURI);
                else {
                    // if the connection cannot be reused, then try to copy the model with a blank physical URI
                    OWLConnection sourceConnection=getOWLConnection(result.m_connectionParameters);
                    try {
                        OWLOntology sourceOIModel=sourceConnection.getOntologyPhysical(result.m_physicalURI);
                        Map logicalToPhysicalURIsMap = new HashMap();
                        logicalToPhysicalURIsMap.put(logicalURI, "");
                        OntologyReplicator replicator=new OntologyReplicator(sourceOIModel,logicalToPhysicalURIsMap,OWLConnection);
                        try {
                            return replicator.doReplication();
                        }
                        catch (InterruptedException error) {
                            throw new OWLException("Model replication interrupted",error);
                        }
                    }
                    finally {
                        sourceConnection.close();
                    }
                }
            }
        }
        return null;
    }
    /**
     * Registers a logical URI resolver.
     *
     * @param resolver                          the resolver
     */
    public static synchronized void registerLogicalURIResolver(LogicalURIResolver resolver) {
        s_logicalURIResolvers.add(resolver);
    }
    /**
     * Unregisters a logical URI resolver.
     *
     * @param resolver                          the resolver
     */
    public static synchronized void unregisterLogicalURIResolver(LogicalURIResolver resolver) {
        s_logicalURIResolvers.remove(resolver);
    }
    /**
     * Reegisters a well-known RDF OI-model.
     *
     * @param wellKnownOIModelPhysicalURI       the physical URI of the well-known model
     * @throws OWLException                    thrown if there is an error
     */
    public static void registerWellKnownRDFOntology(final URI wellKnownOIModelPhysicalURI) throws OWLException {
        OWLConnection connection = getOWLConnection(s_parametersAPIOnRDF);
        final String wellKnownOIModelLogicalURI;
        try {
            OWLOntology oimodel=connection.getOntologyPhysical(wellKnownOIModelPhysicalURI);
            wellKnownOIModelLogicalURI = oimodel.getLogicalURI().toString();
        }
        finally {
            connection.close();
        }
        registerLogicalURIResolver(new LogicalURIResolver() {
            public ResultHolder resolveLogicalURI(URI logicalURI) {
                if (wellKnownOIModelLogicalURI.equals(logicalURI)) {
                    ResultHolder result = new ResultHolder();
                    result.m_physicalURI = wellKnownOIModelPhysicalURI;
                    result.m_connectionParameters = s_parametersAPIOnRDF;
                    return result;
                }
                else
                    return null;
            }
        });
    }
    /**
     * Checks if supplied connection can be used for opening a model with given parameters.
     *
     * @param connection                        the connection that is checked if it can be reused
     * @param parameters                        the parameters that are checked
     * @return                                  <code>true</code> if the supplied connection can be reused for given parameters
     */
    public static boolean canUseConnectionForParameters(OWLConnection connection,Map parameters) {
        Map connectionParameters;
        try {
            connectionParameters = connection.getParameters();
        }
        catch (OWLException e) {
            return false;
        }
        Iterator keys = parameters.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            if (!connectionParameters.containsKey(key))
                return false;
            Object connectionValue = connectionParameters.get(key);
            Object parametersValue = parameters.get(key);
            if (connectionValue == null) {
                if (parametersValue != null)
                    return false;
            }
            else {
                if (!connectionValue.equals(parametersValue) && !"???".equals(parametersValue))
                    return false;
            }
        }
        return true;
    }
    /**
     * A private version of the method that ignores the exception.
     *
     * @param wellKnownOIModelPhysicalURI       the physical URI of the well-known model
     */
    protected static void registerWellKnownRDFOntologyEx(URI wellKnownOIModelPhysicalURI) {
        try {
            registerWellKnownRDFOntology(wellKnownOIModelPhysicalURI);
        }
        catch (OWLException ignored) {
    }
}
}
