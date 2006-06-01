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

import edu.colorado.phet.simlauncher.util.LauncherUtil;
import edu.colorado.phet.simlauncher.resources.JnlpResource;
import edu.colorado.phet.simlauncher.resources.ThumbnailResource;
import edu.colorado.phet.simlauncher.resources.JarResource;
import edu.colorado.phet.simlauncher.resources.SimResource;
import edu.colorado.phet.common.util.EventChannel;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Simulation
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Simulation {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------
    private static boolean DEBUG = false;

//    private static List simulations;
//    private static List uninstalledSims;
//    private static List installedSims;
//    private static ArrayList listeners = new ArrayList();
//    private static HashMap namesToSims;
//
//    static {
//        namesToSims = new HashMap();
//        simulations = Catalog.instance().getAllSimulations();
//        uninstalledSims = new ArrayList( simulations );
//        installedSims = new ArrayList();
//    }
//
//    public static List getAllInstances() {
//        return simulations;
//    }
//
//    public static List getUninstalledSims() {
//        return uninstalledSims;
//    }
//
//    public static List getInstalledSims() {
//        return installedSims;
//    }
//
//    public static Simulation getSimulationForName( String name ) {
//        Simulation sim = (Simulation)namesToSims.get( name );
//        if( sim == null ) {
//            throw new IllegalArgumentException( "name not recognized" );
//        }
//        return sim;
//    }

    //--------------------------------------------------------------------------------------------------
    // Event/Listener mechanism for class-level state
    //--------------------------------------------------------------------------------------------------

//    public static void addListener( ClassChangeListener listener ) {
//        listeners.add( listener );
//    }
//
//    public static void removeListener( ClassChangeListener listener ) {
//        listeners.remove( listener );
//    }
//
//    public static interface ClassChangeListener extends EventListener {
//        void instancesChanged();
//    }
//
//    private static void notifyListeners() {
//        for( int i = 0; i < listeners.size(); i++ ) {
//            ClassChangeListener changeListener = (ClassChangeListener)listeners.get( i );
//            changeListener.instancesChanged();
//        }
//    }


    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------
    private String name;
    private String description;
    private URL jnlpUrl;
    private JnlpResource jnlpResource;
    private SimResource descriptionResource;
    private ThumbnailResource thumbnailResource;
    private JarResource[] jarResources;
    private List resources = new ArrayList();

    /**
     * Constructor
     *
     * @param name
     * @param description
     * @param thumbnail
     * @param jnlpUrl
     */
    public Simulation( String name, String description, ThumbnailResource thumbnail, URL jnlpUrl, File localRoot ) {
        this.name = name;
        this.description = description;
        this.jnlpUrl = jnlpUrl;

        jnlpResource = new JnlpResource( jnlpUrl, localRoot );
        resources.add( jnlpResource );
        jarResources = jnlpResource.getJarResources();
        for( int i = 0; i < jarResources.length; i++ ) {
            JarResource jarResource = jarResources[i];
            resources.add( jarResource );
        }
        thumbnailResource = thumbnail;
        resources.add( thumbnailResource );

//        namesToSims.put( name, this );
    }

    public String toString() {
        return name;
    }

    /**
     *
     */
    public void install() {
        jnlpResource.download();
        for( int i = 0; i < jarResources.length; i++ ) {
            JarResource jarResource = jarResources[i];
            System.out.println( "jarResource = " + jarResource.getLocalFile() );
            jarResource.download();
        }
        thumbnailResource.download();

        changeListenerProxy.isInstalled( new ChangeEvent( this ) );

//        uninstalledSims.remove( this );
//        installedSims.add( this );
//        notifyListeners();
    }


    public void uninstall() {
        // Delete the local files

        changeListenerProxy.isUninstalled( new ChangeEvent( this ) );

//        installedSims.remove( this );
//        uninstalledSims.add( this );
//        notifyListeners();
    }

    /**
     * Launches the simulation
     * todo: put more smarts in here
     */
    public void launch() {
        String[]commands = new String[]{"javaws", jnlpResource.getLocalFile().getAbsolutePath()};
        if( DEBUG ) {
            for( int i = 0; i < commands.length; i++ ) {
                System.out.println( "commands[i] = " + commands[i] );
            }
        }
        final Process process;
        try {
            process = Runtime.getRuntime().exec( commands );
            // Get the input stream and read from it
            new Thread( new LauncherUtil.OutputRedirection( process.getInputStream() ) ).start();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Tells if the simulation is installed locally
     *
     * @return true if the simulation is installed
     */
    public boolean isInstalled() {
        return this.jnlpResource.isInstalled();
    }

    /**
     * Tells if this instance is current with the version on the PhET web site
     *
     * @return true if the local version is current
     */
    public boolean isCurrent() {
        boolean isCurrent = true;
        for( int i = 0; i < resources.size(); i++ ) {
            SimResource simResource = (SimResource)resources.get( i );
            isCurrent &= simResource.isCurrent();
        }
        return isCurrent;
    }

    /**
     * Updates the local version of the simulation with the one on the PhET web site
     */
    public void update() {
        for( int i = 0; i < resources.size(); i++ ) {
            SimResource simResource = (SimResource)resources.get( i );
            simResource.update();
        }
        changeListenerProxy.isUninstalled( new ChangeEvent( this ) );
    }

    //--------------------------------------------------------------------------------------------------
    // Setters and getters
    //--------------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ImageIcon getThumbnail() {
        return thumbnailResource.getImageIcon();
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
        public ChangeEvent( Simulation source ) {
            super( source );
        }

        public Simulation getSimulation() {
            return (Simulation)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void isInstalled( ChangeEvent event );
        void isUninstalled( ChangeEvent event );
        void isUpdated( ChangeEvent event );
    }

}
