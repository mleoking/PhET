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

import edu.colorado.phet.simlauncher.resources.JarResource;
import edu.colorado.phet.simlauncher.resources.JnlpResource;
import edu.colorado.phet.simlauncher.resources.SimResourceException;
import edu.colorado.phet.simlauncher.resources.ThumbnailResource;
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
     * @param description
     * @param thumbnail
     * @param jnlpUrl
     */
    public JavaSimulation( String name, String description, ThumbnailResource thumbnail, URL jnlpUrl, File localRoot ) {
        super( name, description, thumbnail, jnlpUrl, localRoot );
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
     * @throws SimResourceException
     */
    public void install() throws SimResourceException {
        super.install();
        // Install the JNLP resource
//        jnlpResource = new JnlpResource( jnlpUrl, localRoot );
        jnlpResource.download();
        addResource( jnlpResource );

        // Install all the jar resoureces mentioned in the JNLP
        if( jnlpResource.isRemoteAvailable() ) {
            JarResource[] jarResources = jnlpResource.getJarResources();
            for( int i = 0; i < jarResources.length; i++ ) {
                JarResource jarResource = jarResources[i];
                addResource( jarResource );
                jarResource.download();
            }
        }
    }

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
            recordLastLaunchTime();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Setters and getters
    //--------------------------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimContainer
    //--------------------------------------------------------------------------------------------------

}