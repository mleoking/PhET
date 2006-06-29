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
import edu.colorado.phet.simlauncher.resources.SimResourceException;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * Catalog
 * <p/>
 * A catalog of all the available simulations. It also keeps lists of the simulations that are
 * installed and not installed. 
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Catalog implements JavaSimulation.ChangeListener {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static Catalog instance;

    public static Catalog instance() {
        if( instance == null ) {
            instance = new Catalog();
        }
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
        try {
            if( !catalogResource.isInstalled() || !catalogResource.isCurrent() ) {
                catalogResource.download();
            }
        }
        catch( SimResourceException e ) {
        }
//        simulations = new SimFactory().getSimulations( new PhetWebPage( "http://www.colorado.edu/physics/phet/web-pages/simulation-pages/top-simulations.htm" ) );
        simulations = new SimFactory().getSimulations( catalogResource.getLocalFile() );
        categories = new CategoryFactory().getCategories( catalogResource.getLocalFile() );

        installedSimulations = new ArrayList();
        for( int i = 0; i < simulations.size(); i++ ) {
            JavaSimulation simulation = (JavaSimulation)simulations.get( i );
            simulation.addChangeListener( this );
            if( simulation.isInstalled() ) {
                installedSimulations.add( simulation );
            }
        }
    }

    /**
     * Refreshes the lists of installed and uninstalled simulations, and notifies listeners
     */
    public void refreshInstalledUninstalledLists() {
        simulations = new SimFactory().getSimulations( catalogResource.getLocalFile() );
        categories = new CategoryFactory().getCategories( catalogResource.getLocalFile() );

        installedSimulations = new ArrayList();
        for( int i = 0; i < simulations.size(); i++ ) {
            JavaSimulation simulation = (JavaSimulation)simulations.get( i );
            simulation.addChangeListener( this );
            if( simulation.isInstalled() ) {
                installedSimulations.add( simulation );
            }
        }
        changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
    }

    /**
     * Tells if the remote component of the catalog is available
     *
     * @return true if the remote component of the catalog is available
     */
    public boolean isRemoteAvailable() {
        return catalogResource.isRemoteAvailable();
    }

    /**
     * Returns all the simulations in the catalog
     *
     * @return All simulations
     */
    public List getAllSimulations() {
        return simulations;
    }

    /**
     * Returns all the installed simulations
     *
     * @return all installed simulations
     */
    public List getInstalledSimulations() {
        return installedSimulations;
    }

    /**
     * Returns all the categories
     *
     * @return all the categories
     */
    public List getCategories() {
        return categories;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Simulation.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void installed( JavaSimulation.ChangeEvent event ) {
        installedSimulations.add( event.getSimulation() );
        changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
    }

    public void uninstalled( JavaSimulation.ChangeEvent event ) {
        installedSimulations.remove( event.getSimulation() );
        changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
    }

    public void updated( JavaSimulation.ChangeEvent event ) {
        changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
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
        void catalogChanged( ChangeEvent event );
    }

}