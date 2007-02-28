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

import edu.colorado.phet.simlauncher.model.resources.DescriptionResource;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;
import edu.colorado.phet.simlauncher.model.resources.SwfResource;
import edu.colorado.phet.simlauncher.model.resources.ThumbnailResource;
import edu.colorado.phet.simlauncher.util.HtmlViewer;

import java.io.File;
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
public class FlashSimulation extends Simulation {

    private static boolean DEBUG = false;

    private SwfResource swfResource;
    private Process process;

    /**
     * Constructor
     *
     * @param name
     * @param descriptionResource
     * @param thumbnail
     * @param swfUrl
     */
    public FlashSimulation( String name, DescriptionResource descriptionResource, ThumbnailResource thumbnail, URL swfUrl, File localRoot ) {
        super( name, descriptionResource, thumbnail, swfUrl, localRoot );
        swfResource = new SwfResource( swfUrl, localRoot );
        addResource( swfResource );
    }

    public void uninstall() {
        swfResource.uninstall();
        super.uninstall();
    }

    /**
     * Extends parent behavior by installing resources specific to FlashSimulations
     *
     * @throws edu.colorado.phet.simlauncher.model.resources.SimResourceException
     *
     */
    public void install() throws SimResourceException {
        installationStopped = false;
//        resourceCurrentlyDowloading = swfResource;
        resourceCurrentlyDowloading = null;
        swfResource.download();
        if( !installationStopped ) {
            super.install();
        }
    }

    /**
     * Tells if the simulation is installed locally
     *
     * @return true if the simulation is installed
     */
    public boolean isInstalled() {
        return swfResource != null && swfResource.isInstalled();
    }

    /**
     * Launches the simulation
     * todo: put more smarts in here
     */
    public void launch() throws LaunchException {

        // Parent behavior
        super.launch();
        new HtmlViewer().view("file://" + swfResource.getLocalFile().getAbsolutePath() );
    }
}
