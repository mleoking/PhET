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

import javax.swing.*;
import java.util.*;
import java.net.URL;
import java.io.File;
import java.io.IOException;

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

    private static List simulations;
    private static List uninstalledSims;
    private static List installedSims;
    private static ArrayList listeners = new ArrayList();
    private static HashMap namesToSims;

    static {
        namesToSims = new HashMap();
        simulations = new SimulationFactory().getSimulations( "simulations.xml", new File( "/phet/temp" ) );
        uninstalledSims = new ArrayList( simulations );
        installedSims = new ArrayList();
    }

    public static List getAllInstances() {
        return simulations;
    }

    public static List getUninstalledSims() {
        return uninstalledSims;
    }

    public static List getInstalledSims() {
        return installedSims;
    }

    public static Simulation getInstanceForName( String name ) {
        Simulation sim = (Simulation)namesToSims.get( name );
        if( sim == null ) {
            throw new IllegalArgumentException( "name not recognized" );
        }
        return sim;
    }

    //--------------------------------------------------------------------------------------------------
    // Event/Listener mechanism for class-level state
    //--------------------------------------------------------------------------------------------------

    public static void addListener( ChangeListener listener ) {
        listeners.add( listener );
    }

    public static void removeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }

    public static interface ChangeListener extends EventListener {
        void instancesChanged();
    }

    private static void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            ChangeListener changeListener = (ChangeListener)listeners.get( i );
            changeListener.instancesChanged();
        }
    }


    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------
    private String name;
    private String description;
//    private ImageIcon thumbnail;
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

        namesToSims.put( name, this );
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
            jarResource.download();
            thumbnailResource.download();
        }

        uninstalledSims.remove( this );
        installedSims.add( this );
        notifyListeners();
    }

    public void uninstall() {
        installedSims.remove( this );
        uninstalledSims.add( this );
        notifyListeners();
    }

    /**
     * Launches the simulation
     * todo: put more smarts in here
     */
    public void launch() throws IOException {

        String[]commands = new String[]{"javaws", jnlpResource.getLocalFile().getAbsolutePath()};
        for( int i = 0; i < commands.length; i++ ) {
            System.out.println( "commands[i] = " + commands[i] );
        }
        final Process process = Runtime.getRuntime().exec( commands );
        // Get the input stream and read from it
//            new Thread( new OutputRedirection( process.getInputStream() ) ).start();
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
}
