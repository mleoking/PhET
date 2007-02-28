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

import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Configuration
 * <p/>
 * A singleton class that contains configuration information. This includes:
 * <ul>
 * <li>The URL for the PhET web site
 * <li>The path to the catalog on the PhET site
 * <li>The path to the local root for this applications data
 * </ul>
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Configuration implements ChangeEventChannel.ChangeEventSource {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    /**
     * Singleton implementation
     */
    private static Configuration instance = new Configuration();

    public static Configuration instance() {
        return instance;
    }

    // App name
    private static final String PROGRAM_NAME = "PhET SimLauncher";
    public String getProgramName() {
        return PROGRAM_NAME;
    }

    // Location of local files
    private static String DEFAULT_CACHE_PATH = "/phet/sl-local-root/";
    private static File DEFAULT_CACHE = new File( DEFAULT_CACHE_PATH);
//    static {
//        String localRootPath = System.getProperty( "user.dir" );
//        DEFAULT_CACHE = new File( localRootPath + System.getProperty("file.separator") + "localRoot");
//    }

    private static String DEFAULT_PHET_ROOT_DIR_URL_STRING = "http://www.colorado.edu/physics/phet";
    private static URL DEFAULT_PHET_URL;

    static {
        try {
            DEFAULT_PHET_URL = new URL( DEFAULT_PHET_ROOT_DIR_URL_STRING );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    // The parent directory s=of all simulations
    private static String SIMUALATIONS_PATH = "/simulations";
    private static URL DEFAULT_SIMULATIONS_URL;

    static {
        try {
            DEFAULT_SIMULATIONS_URL = new URL( DEFAULT_PHET_ROOT_DIR_URL_STRING + SIMUALATIONS_PATH );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    // The Catalog
    private static String CATALOG_PATH = "/simulations/catalog/simulations.xml";
    private static URL DEFAULT_CATALOG_URL;

    static {
        try {
            DEFAULT_CATALOG_URL = new URL( DEFAULT_PHET_ROOT_DIR_URL_STRING + CATALOG_PATH );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    // The Options store
    private static String DEFAULT_OPTIONS_PATH = "options.properties";
    private static File DEFAULT_OPTIONS_FILE;

    static {
        DEFAULT_OPTIONS_FILE = new File( DEFAULT_CACHE_PATH + "/" + DEFAULT_OPTIONS_PATH );
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    /**
     * Private constructor to enforce singleton
     */
    private Configuration() {
    }

    //--------------------------------------------------------------------------------------------------
    // Change notification
    //--------------------------------------------------------------------------------------------------

    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();

    public void addChangeListener( ChangeEventChannel.ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeEventChannel.ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    private void notifyListeners() {
        changeEventChannel.notifyChangeListeners( this );
    }

    //--------------------------------------------------------------------------------------------------
    // Attributes of the Configuration, and their getters and setters. Note the style of initialization
    // used here, rather than initializing the attribute in their declarations. I ran into
    // java.lang.ExceptionInInitializerError exceptions when I tried initializing them in the
    // declaration.
    //--------------------------------------------------------------------------------------------------

    // Location of local cache
    private File localRoot;

    public File getLocalRoot() {
        if( localRoot == null ) {
            localRoot = DEFAULT_CACHE;

            // Print out the current directory
//            JFrame f = new JFrame( );
//            Properties props = System.getProperties();
//            String propValue = (String)props.get( "user.dir" );
//            f.getContentPane().add(new JLabel( propValue ));
//            f.pack();
//            f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
//            f.show();
//            System.out.println( "propValue = " + propValue );


        }
        return localRoot;
    }

    public void setLocalRoot( File localRoot ) {
        this.localRoot = localRoot;
        notifyListeners();
    }

    // URL to PhET web site
    private URL phetUrl;

    public URL getPhetUrl() {
        if( phetUrl == null ) {
            phetUrl = DEFAULT_PHET_URL;
        }
        return phetUrl;
    }

    public void setPhetUrl( URL phetUrl ) {
        this.phetUrl = phetUrl;
    }

    // Location of the simulations relative to phetUrl and local root
    private URL simulationsUrl;

    public URL getSimulationsUrl() {
        if( simulationsUrl == null ) {
            simulationsUrl = DEFAULT_SIMULATIONS_URL;
        }
        return simulationsUrl;
    }

    public void setSimulationsUrl( URL simulationsUrl ) {
        this.simulationsUrl = simulationsUrl;
    }

    // Location of catalog relative to phetUrl and local root
    private URL catalogUrl;

    public URL getCatalogUrl() {
        if( catalogUrl == null ) {
            catalogUrl = DEFAULT_CATALOG_URL;
        }
        return catalogUrl;
    }

    public void setCatalogUrl( URL catalogUrl ) {
        this.catalogUrl = catalogUrl;
    }

    // Location of the options file
    private File optionsFile;

    public File getOptionsFile() {
        if( optionsFile == null ) {
            optionsFile = DEFAULT_OPTIONS_FILE;
        }
        return optionsFile;
    }

    public void setOptionsFile( File optionsFile ) {
        this.optionsFile = optionsFile;
    }
}