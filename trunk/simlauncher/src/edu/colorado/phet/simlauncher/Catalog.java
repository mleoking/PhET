/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.simlauncher.resources.CatalogResource;

import java.util.*;

/**
 * Catalog
 * <p/>
 * A catalog of all the available simulations
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Catalog implements Simulation.ChangeListener {

    private static Catalog instance = new Catalog();

    public static Catalog instance() {
        return instance;
    }

    private CatalogResource catalogResource = new CatalogResource( Configuration.instance().getCatalogUrl(),
                                                                   Configuration.instance().getLocalRoot() );
    private List simulations;
    private List installedSimulations;
    private HashMap namesToSims;

    /**
     * Private constructor
     */
    private Catalog() {
        namesToSims = new HashMap();
        simulations = new SimulationFactory().getSimulations( catalogResource.getLocalFile() );
        installedSimulations = new ArrayList();
        for( int i = 0; i < simulations.size(); i++ ) {
            Simulation simulation = (Simulation)simulations.get( i );
            simulation.addChangeListener( this );

            namesToSims.put( simulation.getName(), simulation );

            if( simulation.isInstalled() ) {
                installedSimulations.add( simulation );
            }
        }
    }

    /**
     * Returns the Simulation instance for the simulation with a specified name.
     * @param name
     * @return the simulation with the specified name
     */
    public Simulation getSimulationForName( String name ) {
        Simulation sim = (Simulation)namesToSims.get( name );
        if( sim == null ) {
            throw new IllegalArgumentException( "name not recognized" );
        }
        return sim;
    }

    /**
     * Returns all the simulations in the catalog
     * @return All simulations
     */
    public List getAllSimulations() {
        return simulations;
    }

    /**
     * Returns all the installed simulations
     * @return all installed simulations
     */
    public List getInstalledSimulations() {
        return installedSimulations;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Simulation.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void isInstalled( Simulation.ChangeEvent event ) {
        installedSimulations.add( event.getSimulation() );
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public void isUninstalled( Simulation.ChangeEvent event ) {
        installedSimulations.remove( event.getSimulation() );
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public void isUpdated( Simulation.ChangeEvent event ) {
        // noop
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Catalog source ) {
            super( source );
        }

        public Catalog getCatalog() {
            return (Catalog)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

}
