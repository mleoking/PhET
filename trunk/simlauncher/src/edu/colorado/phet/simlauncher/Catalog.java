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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * Catalog
 * <p/>
 * A catalog of all the available simulations. It also keeps lists of the simulations that are
 * installed.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Catalog implements Simulation.ChangeListener {

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
        installedSimulations = new ArrayList();
        createSimulationList();
        categories = new CategoryFactory().getCategories( catalogResource.getLocalFile() );

        for( int i = 0; i < simulations.size(); i++ ) {
            Simulation simulation = (Simulation)simulations.get( i );
            simulation.addChangeListener( this );
            if( simulation.isInstalled() ) {
                installedSimulations.add( simulation );
            }
        }
    }


    private void createSimulationList() {
        // Make a backup of the current catalog file, in case there is a problem
        File catalogFileBackup = new File( catalogResource.getLocalFile().getName() + "-bak" );

        // Create channel on the source
        try {
            if( catalogResource.getLocalFile().exists() ) {
                FileChannel srcChannel = new FileInputStream( catalogResource.getLocalFile() ).getChannel();

                // Create channel on the destination
                FileChannel dstChannel = new FileOutputStream( catalogFileBackup ).getChannel();

                // Copy file contents from source to destination
                dstChannel.transferFrom( srcChannel, 0, srcChannel.size() );

                // Close the channels
                srcChannel.close();
                dstChannel.close();
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
            return;
        }

        // If the catalog isn't installed yet, go get it. If we're not connected to the Phet site, we'll get
        // a SimResourceException, which means we can't get the catalog
        try {
            if( ( Options.instance().isCheckForUpdatesOnStartup() && !isCurrent() ) ) {
                catalogResource.download();
            }
        }
        catch( SimResourceException e ) {
            e.printStackTrace();
            return;
        }

        // Make the simulations in the catalog. If there is a problem, restore the backup and do it again
        try {
            simulations = new SimFactory().getSimulations( catalogResource.getLocalFile() );
        }
        catch( SimFactory.XmlCatalogException xce ) {
            // Create channel on the source
            try {
                System.out.println( "Error parsing the new catalog. The pprevious catalog is being restored." );
                FileChannel srcChannel = new FileInputStream( catalogFileBackup ).getChannel();

                // Create channel on the destination
                FileChannel dstChannel = new FileOutputStream( catalogResource.getLocalFile() ).getChannel();

                // Copy file contents from source to destination
                dstChannel.transferFrom( srcChannel, 0, srcChannel.size() );

                // Close the channels
                srcChannel.close();
                dstChannel.close();

                try {
                    simulations = new SimFactory().getSimulations( catalogResource.getLocalFile() );
                }
                catch( SimFactory.XmlCatalogException e ) {
                    System.out.println( "Problem restoring from backup copy of catalog file" );
                    e.printStackTrace();
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
                return;
            }
        }

    }

    /**
     * Updates the catalog
     */
    public void update() {
        try {
            catalogResource.update();
            categories = new CategoryFactory().getCategories( catalogResource.getLocalFile() );
            changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
        }
        catch( SimResourceException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @return true if the catalog is up to date
     */
    public boolean isCurrent() {
        return catalogResource.isInstalled() && catalogResource.isCurrent();
    }

    /**
     * Refreshes the lists of installed and uninstalled simulations, and notifies listeners
     */
    public void refreshInstalledUninstalledLists() {
        createSimulationList();
//        simulations = new SimFactory().getSimulations( catalogResource.getLocalFile() );
        categories = new CategoryFactory().getCategories( catalogResource.getLocalFile() );

        installedSimulations = new ArrayList();
        for( int i = 0; i < simulations.size(); i++ ) {
            Simulation simulation = (Simulation)simulations.get( i );
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

    public void installed( Simulation.ChangeEvent event ) {
        installedSimulations.add( event.getSimulation() );
        changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
    }

    public void uninstalled( Simulation.ChangeEvent event ) {
        installedSimulations.remove( event.getSimulation() );
        changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
    }

    public void updated( Simulation.ChangeEvent event ) {
//        changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
    }

    public void updateAvailable( Simulation.ChangeEvent event ) {
//        changeListenerProxy.catalogChanged( new ChangeEvent( this ) );
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