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
import edu.colorado.phet.simlauncher.resources.SimResource;
import edu.colorado.phet.simlauncher.resources.SimResourceException;
import edu.colorado.phet.simlauncher.resources.ThumbnailResource;
import edu.colorado.phet.simlauncher.util.FileUtil;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Simulation
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class Simulation implements SimContainer {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static HashMap namesToSims = new HashMap();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private String name;
    private String description;
    private URL jnlpUrl;

    private SimResource descriptionResource;
    private ThumbnailResource thumbnailResource;
//    private JarResource[] jarResources;
    private List resources = new ArrayList();
    private File lastLaunchedTimestampFile;
    private File localRoot;

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------

    EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public Simulation( String name, String description, ThumbnailResource thumbnail, URL jnlpUrl, File localRoot ) {
        this.name = name;
        this.description = description;
        this.jnlpUrl = jnlpUrl;
        this.localRoot = localRoot;

        thumbnailResource = thumbnail;
        addResource( thumbnailResource );

        // Create the file that will store the timestamp of the last time we were launched
        String subPath = jnlpUrl.getPath().substring( 0, jnlpUrl.getPath().lastIndexOf( '/' ) );
        String relativePath = jnlpUrl.getHost() + FileUtil.getPathSeparator() + subPath;
        lastLaunchedTimestampFile = new File( localRoot.getAbsolutePath()
                                              + FileUtil.getPathSeparator()
                                              + relativePath
                                              + FileUtil.getPathSeparator()
                                              + "lastLaunchTimeStamp.txt" );

        // Register ourselves with the class
        namesToSims.put( name, this );
    }

    protected void addResource( SimResource simResource ) {
        resources.add( simResource );
    }

    /**
     * Returns the Simulation instance for the simulation with a specified name.
     *
     * @param name
     * @return the simulation with the specified name
     */
    public static JavaSimulation getSimulationForName( String name ) {
        JavaSimulation sim = (JavaSimulation)namesToSims.get( name );
        if( sim == null ) {
            throw new IllegalArgumentException( "name not recognized: " + name );
        }
        return sim;
    }

    //--------------------------------------------------------------------------------------------------
    // Abstract methods
    //--------------------------------------------------------------------------------------------------

    public abstract void launch();

    /**
     * Tells if the simulation is installed locally
     *
     * @return true if the simulation is installed
     */
    public abstract boolean isInstalled();



    protected void finalize() throws Throwable {
        namesToSims.remove( getName() );
        super.finalize();
    }

    public String toString() {
        return name;
    }

    /**
     * Downloads all the resources for the simulation. Must be extended by
     * subclasses
     */
    public void install() throws SimResourceException {

        // Install the thumbnail resource
        thumbnailResource.download();
//        descriptionResource.download();

        changeListenerProxy.installed( new ChangeEvent( this ) );
    }

    /**
     * Uninstalls all of the simulation's resources except for the thumbnail, which is needed
     * for display purposes
     */
    public void uninstall() {
        // Delete the resources other than the thumbnail, which is needed for display purposes
        for( int i = 0; i < resources.size(); i++ ) {
            SimResource simResource = (SimResource)resources.get( i );
            if( !( simResource instanceof ThumbnailResource ) ) {
                simResource.uninstall();
            }
        }

        changeListenerProxy.uninstalled( new ChangeEvent( this ) );
    }

    /**
     * Records the current time in the last-launch timestamp file
     *
     * @throws java.io.IOException
     */
    protected void recordLastLaunchTime() throws IOException {
        long time = System.currentTimeMillis();
        if( lastLaunchedTimestampFile.exists() ) {
            lastLaunchedTimestampFile.delete();
        }
        lastLaunchedTimestampFile.createNewFile();
        BufferedWriter out = new BufferedWriter( new FileWriter( lastLaunchedTimestampFile ) );
        out.write( new Long( time ).toString() );
        out.close();
    }

    /**
     * Returns the system time that the simulation was last launched. If it was never launched,
     * returns 0.
     *
     * @return the time the simulation was last launched
     */
    public long getLastLaunchTimestamp() {
        String str = null;
        try {
            BufferedReader in = new BufferedReader( new FileReader( lastLaunchedTimestampFile ) );
            str = in.readLine();
            in.close();
        }
        catch( IOException e ) {
        }
        if( str == null ) {
            str = "0";
        }
        return Long.parseLong( str );
    }

    /**
     * Tells if this instance is current with the version on the PhET web site
     *
     * @return true if the local version is current
     */
    public boolean isCurrent() throws SimResourceException {
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
    public void update() throws SimResourceException {

        // Note that calling uninstall() and then install() doesn't work!!!!
        for( int i = 0; i < resources.size(); i++ ) {
            SimResource resource = (SimResource)resources.get( i );
            resource.uninstall();
            resource.download();
        }
        changeListenerProxy.updated( new ChangeEvent( this ) );
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ImageIcon getThumbnail() {
        return thumbnailResource.getImageIcon();
    }

    public Simulation getSimulation() {
        return this;
    }

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

        public JavaSimulation getSimulation() {
            return (JavaSimulation)getSource();
        }
    }

    //--------------------------------------------------------------------------------------------------
    // ChangeListener interface
    //--------------------------------------------------------------------------------------------------

    public interface ChangeListener extends EventListener {
        void installed( ChangeEvent event );

        void uninstalled( ChangeEvent event );

        void updated( ChangeEvent event );
    }
}
