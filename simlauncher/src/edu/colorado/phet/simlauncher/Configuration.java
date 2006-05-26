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
 * <p>
 * A singleton class that contains configuration information
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Configuration implements ChangeEventChannel.ChangeEventSource {

    //--------------------------------------------------------------------------------------------------
    // Implementation of singleton
    //--------------------------------------------------------------------------------------------------
    private static File DEFAULT_CACHE = new File( "/phet/temp/simlauncher/cache" );
    private static String DEFAULT_PHET_URL_STRING = "http://www.colorado.edu/physics/phet";
    private static URL DEFAULT_PHET_URL;
    static {
        try {
            DEFAULT_PHET_URL = new URL( DEFAULT_PHET_URL_STRING );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }

    private static URL DEFAULT_CATALOG_URL;
    static{
        try {
            DEFAULT_CATALOG_URL = new URL( DEFAULT_PHET_URL_STRING + "/simulations/catalog/simulations.xml" );
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
    }


    private static Configuration instance = new Configuration();

    public static Configuration instance() {
        return instance;
    }

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

    // Location of local cache
    private File localRoot = DEFAULT_CACHE;
    public File getLocalRoot() {
        return localRoot;
    }
    public void setLocalRoot( File localRoot ) {
        this.localRoot = localRoot;
        notifyListeners();
    }

    // URL to PhET web site
    private URL phetUrl = DEFAULT_PHET_URL;
    public URL getPhetUrl() {
        return phetUrl;
    }
    public void setPhetUrl( URL phetUrl ) {
        this.phetUrl = phetUrl;
    }

    // Location of catalog relative to phetUrl and local root
    private URL catalogUrl = DEFAULT_CATALOG_URL;
    public URL getCatalogUrl() {
        return catalogUrl;
    }
    public void setCatalogUrl( URL catalogUrl ) {
        this.catalogUrl = catalogUrl;
    }
}
