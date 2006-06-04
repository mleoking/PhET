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

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Catalog instance = new Catalog();

    public static Catalog instance() {
        return instance;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private CatalogResource catalogResource = new CatalogResource( Configuration.instance().getCatalogUrl(),
                                                                   Configuration.instance().getLocalRoot() );
    private List simulations;
    private List installedSimulations;
    private List categories;

    /**
     * Private constructor
     */
    private Catalog() {

        // If the catalog isn't installed yet, go get it
        if( !catalogResource.isInstalled() || !catalogResource.isCurrent() ) {
            catalogResource.download();
        }
        simulations = new SimulationFactory().getSimulations( catalogResource.getLocalFile() );
        categories = new CategoryFactory().getCategories( catalogResource.getLocalFile() );

        installedSimulations = new ArrayList();
        for( int i = 0; i < simulations.size(); i++ ) {
            Simulation simulation = (Simulation)simulations.get( i );
            simulation.addChangeListener( this );
            if( simulation.isInstalled() ) {
                installedSimulations.add( simulation );
            }
        }
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

    /**
     * Returns all the categories
     * @return all the categories
     */
    public List getCategories() {
        return categories;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Simulation.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void installed( Simulation.ChangeEvent event ) {
        installedSimulations.add( event.getSimulation() );
        changeListenerProxy.catatlogChanged( new ChangeEvent( this ) );
    }

    public void uninstalled( Simulation.ChangeEvent event ) {
        installedSimulations.remove( event.getSimulation() );
        changeListenerProxy.catatlogChanged( new ChangeEvent( this ) );
    }

    public void updated( Simulation.ChangeEvent event ) {
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
        void catatlogChanged( ChangeEvent event );
    }

}
