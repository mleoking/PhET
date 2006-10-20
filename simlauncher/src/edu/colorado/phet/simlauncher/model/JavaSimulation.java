/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model;

import edu.colorado.phet.simlauncher.model.resources.*;
import edu.colorado.phet.simlauncher.util.LauncherUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Simulation
 * <p/>
 * A Simulation has a collection of SimResources. They include
 * <ul>
 * <li>a JnlpResource
 * <li>one or more JarResources
 * <li>a ThumbnailResource
 * <li>a DescriptionResource
 * </ul>
 * A Simulation also has an assiciated file that keeps track of the time when the simulation
 * was last launched (the lastLaunchedTimestampFile). This is used to sort installed installed
 * simulations by most-recently-used status.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class JavaSimulation extends Simulation {

    private static boolean DEBUG = false;
    protected JnlpResource jnlpResource;

    /**
     * Constructor
     *
     * @param name
     * @param descriptionResource
     * @param thumbnail
     * @param jnlpUrl
     */
    public JavaSimulation( String name, DescriptionResource descriptionResource, ThumbnailResource thumbnail, URL jnlpUrl, File localRoot ) {
        super( name, descriptionResource, thumbnail, jnlpUrl, localRoot );
        this.jnlpResource = new JnlpResource( jnlpUrl, localRoot );
        // If the simulation is installed, create its resource objects
        if( isInstalled() ) {
            addResource( jnlpResource );
            JarResource[] jarResources = jnlpResource.getJarResources();
            for( int i = 0; i < jarResources.length; i++ ) {
                addResource( jarResources[i] );
            }
        }
    }

    /**
     * Extends parent behavior by installing resources specific to JavaSimulations
     *
     * @throws SimResourceException
     */
    public void install() throws SimResourceException {

        // Clear the stopped flag
        installationStopped = false;

        // Clear the resource list, in case the simulation is being re-installed without
        // the application having been closed and re-opened
        getResources().clear();

        // Install the JNLP resource
        resourceCurrentlyDowloading = jnlpResource;
        jnlpResource.download();
        addResource( jnlpResource );

        // Install all the jar resoureces mentioned in the JNLP
        if( !installationStopped && jnlpResource.isRemoteAvailable() ) {
            JarResource[] jarResources = jnlpResource.getJarResources();
            for( int i = 0; !installationStopped && i < jarResources.length; i++ ) {
                JarResource jarResource = jarResources[i];
                resourceCurrentlyDowloading = jarResource;
                if( !jarResource.getLocalFile().exists() || !jarResource.isCurrent() ) {
                    jarResource.download();
                }
                else {
//                    System.out.println( "JavaSimulation.install:  jar resource already in place" );
                }
                addResource( jarResource );
            }
        }

        // This shouldn't happen until all the components are downloaded
        if( !installationStopped ) {
            super.install();
        }
    }

//    public void uninstall() {
//        jnlpResource.uninstall();
//        super.uninstall();
//    }

    /**
     * Tells if the simulation is installed locally
     *
     * @return true if the simulation is installed
     */
    public boolean isInstalled() {
        return jnlpResource != null && jnlpResource.isInstalled();
    }

    /**
     * Launches the simulation
     * todo: put more smarts in here
     */
    public void launch() throws LaunchException {

        // Parent behavior
        super.launch();

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
}