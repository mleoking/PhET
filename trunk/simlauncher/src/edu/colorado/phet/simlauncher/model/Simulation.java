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

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.simlauncher.model.resources.DescriptionResource;
import edu.colorado.phet.simlauncher.model.resources.SimResource;
import edu.colorado.phet.simlauncher.model.resources.SimResourceException;
import edu.colorado.phet.simlauncher.model.resources.ThumbnailResource;
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

    private DescriptionResource descriptionResource;
    private ThumbnailResource thumbnailResource;
    private List resources = new ArrayList();
    private File lastLaunchedTimestampFile;
    private File localRoot;
    private boolean updateAvailable;

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------

    EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();
    private String localPath;

    public Simulation( String name, DescriptionResource descriptionResource, ThumbnailResource thumbnail, URL launchResourceUrl, File localRoot ) {
        this.name = name;
        this.localRoot = localRoot;

//        this.descriptionResource = descriptionResource;
//        addResource( descriptionResource );

        thumbnailResource = thumbnail;
        addResource( thumbnailResource );

        // Create the file that will store the timestamp of the last time we were launched
        String subPath = launchResourceUrl.getPath().substring( 0, launchResourceUrl.getPath().lastIndexOf( '/' ) );
        String relativePath = launchResourceUrl.getHost() + FileUtil.getPathSeparator() + subPath;
        localPath = localRoot.getAbsolutePath()
                    + FileUtil.getPathSeparator()
                    + relativePath;
        lastLaunchedTimestampFile = new File( localPath
                                              + FileUtil.getPathSeparator()
                                              + "lastLaunchTimeStamp.txt" );

        // Register ourselves with the class
        namesToSims.put( name, this );
    }

    protected void addResource( SimResource simResource ) {
        resources.add( simResource );
    }
    
    public List getResources(){
        return resources;
    }

    /**
     * Returns the Simulation instance for the simulation with a specified name.
     *
     * @param name
     * @return the simulation with the specified name
     */
    public static Simulation getSimulationForName( String name ) {
        Simulation sim = (Simulation)namesToSims.get( name );
        if( sim == null ) {
            throw new IllegalArgumentException( "name not recognized: " + name );
        }
        return sim;
    }

    //--------------------------------------------------------------------------------------------------
    // Abstract methods
    //--------------------------------------------------------------------------------------------------

    /**
     * Tells if the simulation is installed locally
     *
     * @return true if the simulation is installed
     */
    public abstract boolean isInstalled();

    /**
     * Subclasses must override this method to handle stopping the download of their resources
     */
    protected SimResource resourceCurrentlyDowloading;
    protected boolean installationStopped;

    public void stopInstallation() {
        installationStopped = true;
        System.out.println( "Simulation.stopInstallation" );
        if( resourceCurrentlyDowloading != null ) {
            resourceCurrentlyDowloading.setStopDownload( true );
        }

        for( int i = 0; i < resources.size(); i++ ) {
            SimResource simResource = (SimResource)resources.get( i );
            simResource.setStopDownload( true );
        }
        uninstall();
    }

    //--------------------------------------------------------------------------------------------------
    // Concrete methods
    //--------------------------------------------------------------------------------------------------

    protected void finalize() throws Throwable {
        namesToSims.remove( getName() );
        super.finalize();
    }

    public String toString() {
        return name;
    }

    /**
     * Sets the update-available flag to false and notifies listeners of a change event.
     * <p/>
     * Subclasses should override this method, and downloading their specific resources
     * in those override BEFORE they call super.install().
     */
    public void install() throws SimResourceException {

        // Install the thumbnail resource
        // Don't think we need this.
        thumbnailResource.download();

//        descriptionResource.download();

        setUpdateAvailable( false );
        changeListenerProxy.installed( new ChangeEvent( this ) );
    }

    /**
     * Notifies listeners that the simulation has been uninstalled.
     * <p/>
     * Subclasses should override this method, and remove whatever resources are appropriate
     * BEFORE calling super.uninstall()
     */
    public void uninstall() {
        // Delete the resources other than the thumbnail, which is needed for display purposes
//        for( int i = resources.size() - 1; i >= 0; i-- ) {
//            SimResource simResource = (SimResource)resources.get( i );
//            if( !( simResource instanceof ThumbnailResource ) ) {
//                simResource.uninstall();
//            }
//        }

        // Delete the rest of the contents of the directory for the simulation, and
        // the directory itself.
//        FileUtil.deleteDir( new File( localPath ) );

        changeListenerProxy.uninstalled( new ChangeEvent( this ) );
    }

    /**
     * Checks to see that all the required resources are available and logs the time as the last time this
     * simulation was launched.
     * 
     * @throws LaunchException
     */
    public void launch() throws LaunchException {
        // Check to see that all the resources are available
        for( int i = 0; i < resources.size(); i++ ) {
            SimResource simResource = (SimResource)resources.get( i );
            if( !simResource.getLocalFile().exists() ) {
                throw new LaunchException( "Local resource file not found: " + simResource.getLocalFile().getAbsolutePath() );
            }
        }

        // Record the time the simulation is launched
        try {
            recordLastLaunchTime();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
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
    public void update() throws SimResourceException {

        // Note that calling uninstall() and then install() doesn't work!!!!
        for( int i = 0; i < resources.size(); i++ ) {
            SimResource resource = (SimResource)resources.get( i );
            resource.update();
        }
        setUpdateAvailable( false );
        changeListenerProxy.updated( new ChangeEvent( this ) );
    }

    public String getName() {
        return name;
    }

    public DescriptionResource getDescriptionResource() {
        return descriptionResource;
    }

    public String getDescription() {
        return descriptionResource.getDescription();
    }

    public ImageIcon getThumbnail() {
        return thumbnailResource.getImageIcon();
    }

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    /**
     * Enables the UPDATE_ENABLED flag, and notifies listeners that the simulation has an update available.
     * The flag is set so that the SimTable will show an icon. This is only a stopgap way of doing it.
     */
    private void setUpdateAvailable( boolean isAvailable ) {
        if( isAvailable ) {
            System.out.println( "Simulation.setUpdateAvailable" );
        }
        updateAvailable = isAvailable;
        boolean orgFlag = SimResource.isUpdateEnabled();
        SimResource.setUpdateEnabled( true );
        this.changeListenerProxy.updateAvailable( new ChangeEvent( this ) );
        SimResource.setUpdateEnabled( orgFlag );
    }

    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    public void checkForUpdate() {
        if( isInstalled() ) {
            for( int i = 0; i < resources.size(); i++ ) {
                SimResource simResource = (SimResource)resources.get( i );
                simResource.checkForUpdate();
            }
            setUpdateAvailable( !isCurrent() );
        }
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Simulation source ) {
            super( source );
        }

        public Simulation getSimulation() {
            return (Simulation)getSource();
        }
    }

//--------------------------------------------------------------------------------------------------
// Implementation of SimContainer
//--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return this;
    }

    public Simulation[] getSimulations() {
        return new Simulation[]{this};
    }

//--------------------------------------------------------------------------------------------------
// ChangeListener interface
//--------------------------------------------------------------------------------------------------

    public interface ChangeListener extends EventListener {
        void installed( ChangeEvent event );

        void uninstalled( ChangeEvent event );

        void updated( ChangeEvent event );

        void updateAvailable( ChangeEvent event );
    }

    //--------------------------------------------------------------------------------------------------
    // Inner Classes
    //--------------------------------------------------------------------------------------------------

    public class LaunchException extends Exception {
        public LaunchException() {
            super();
        }

        public LaunchException( String message ) {
            super( message );
        }

        public LaunchException( Throwable cause ) {
            super( cause );
        }

        public LaunchException( String message, Throwable cause ) {
            super( message, cause );
        }
    }

}
