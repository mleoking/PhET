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
import edu.colorado.phet.simlauncher.util.FileUtil;
import edu.colorado.phet.simlauncher.resources.*;
import edu.colorado.phet.common.util.EventChannel;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * Simulation
 * <p>
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
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Simulation {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------
    private static boolean DEBUG = false;
    private static HashMap namesToSims = new HashMap();
    private File localRoot;

    /**
     * Returns the Simulation instance for the simulation with a specified name.
     *
     * @param name
     * @return the simulation with the specified name
     */
    public static Simulation getSimulationForName( String name ) {
        Simulation sim = (Simulation)namesToSims.get( name );
        if( sim == null ) {
            throw new IllegalArgumentException( "name not recognized" );
        }
        return sim;
    }

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
    private File lastLaunchedTimestampFile;

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
        this.localRoot = localRoot;
        this.jnlpResource = new JnlpResource( jnlpUrl, localRoot );
        thumbnailResource = thumbnail;
        resources.add( thumbnailResource );

        // If the simulation is installed, create its resource objects
        if( isInstalled() ) {
            resources.add( jnlpResource );
            jnlpResource.getJarResources();
            JarResource[] jarResources = jnlpResource.getJarResources();
            for( int i = 0; i < jarResources.length; i++ ) {
                JarResource jarResource = jarResources[i];
                resources.add( jarResource );
            }
        }

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

    protected void finalize() throws Throwable {
        namesToSims.remove( getName() );
        super.finalize();
    }

    public String toString() {
        return name;
    }

    /**
     * Downloads all the resources for the simulation
     */
    public void install() throws SimResourceException {
        jnlpResource = new JnlpResource( jnlpUrl, localRoot );
        jnlpResource.download();
        resources.add( jnlpResource );
        if( jnlpResource.isRemoteAvailable() ) {
            jarResources = jnlpResource.getJarResources();
            for( int i = 0; i < jarResources.length; i++ ) {
                JarResource jarResource = jarResources[i];
                resources.add( jarResource );
            }
        }

        for( int i = 0; i < jarResources.length; i++ ) {
            JarResource jarResource = jarResources[i];
            jarResource.download();
        }
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

    /**
     * Records the current time in the last-launch timestamp file
     *
     * @throws IOException
     */
    private void recordLastLaunchTime() throws IOException {
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
     * Tells if the simulation is installed locally
     *
     * @return true if the simulation is installed
     */
    public boolean isInstalled() {
        return jnlpResource != null && jnlpResource.isInstalled();
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
        for( int i = 0; i < resources.size(); i++ ) {
            SimResource simResource = (SimResource)resources.get( i );
            simResource.update();
        }
        changeListenerProxy.uninstalled( new ChangeEvent( this ) );
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

    public void addChangeListener
            ( ChangeListener
                    listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener
            ( ChangeListener
                    listener ) {
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
        void installed( ChangeEvent event );

        void uninstalled( ChangeEvent event );

        void updated( ChangeEvent event );
    }

}