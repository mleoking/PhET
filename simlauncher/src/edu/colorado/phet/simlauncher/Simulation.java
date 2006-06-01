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
     * Downloads all the resources for the simulation
     */
    public void install() {
        jnlpResource.download();
        for( int i = 0; i < jarResources.length; i++ ) {
            JarResource jarResource = jarResources[i];
            jarResource.download();
        }
        thumbnailResource.download();

        changeListenerProxy.isInstalled( new ChangeEvent( this ) );
    }

    /**
     * Uninstalls all of the simulation's resources except for the thumbnail, which is needed
     * for display purposes
     */
    public void uninstall() {
        // Delete the resources other than the thumbnail, which is needed for display purposes
        for( int i = 0; i < resources.size(); i++ ) {
            SimResource simResource = (SimResource)resources.get( i );
            if( !(simResource instanceof ThumbnailResource )) {
                simResource.uninstall();
            }
        }

        changeListenerProxy.isUninstalled( new ChangeEvent( this ) );
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
