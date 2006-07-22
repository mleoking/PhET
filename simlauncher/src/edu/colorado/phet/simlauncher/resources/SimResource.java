/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.resources;

import edu.colorado.phet.simlauncher.MetaData;
import edu.colorado.phet.simlauncher.Options;
import edu.colorado.phet.simlauncher.PhetSiteConnection;
import edu.colorado.phet.simlauncher.util.FileUtil;
import edu.colorado.phet.simlauncher.util.LauncherUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * SimResource
 * <p>
 * A SimResource is *updatable* if there is a connection to the Phet site, the resource is not current,
 * and the global UPDATE_ENABLED flag is true.
 * <p>
 * The SimResource will report that it is current if there is no connection to the Phet site or UPDATE_ENABLED
 * is false.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimResource {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    // Global flag that tells whether SimResources should check to see if they're current when they are
    // asked
    private static boolean UPDATE_ENABLED = Options.instance().isCheckForUpdatesOnStartup();
//    private static boolean UPDATE_ENABLED = false;

    public static boolean isUpdateEnabled() {
        return UPDATE_ENABLED;
    }

    public static void setUpdateEnabled( boolean UPDATE_ENABLED ) {
        SimResource.UPDATE_ENABLED = UPDATE_ENABLED;
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    URL url;
    private MetaData metaData;
    private File localFile;
    private File localRoot;

    /**
     * @param url
     * @param localRoot
     */
    public SimResource( URL url, File localRoot ) {
        this.url = url;
        this.localRoot = localRoot;
        localFile = getLocalFile( localRoot );
        if( isInstalled() ) {
            try {
                metaData = new MetaData( localFile );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Says if the resource can be considered for updating
     * @return
     */
    protected boolean isUpdatable() {
            return  UPDATE_ENABLED && isInstalled() && !isCurrent();
    }

    /**
     * Tells if the resource is installed locally
     *
     * @return true if the resource is installed locally
     */
    public boolean isInstalled() {
        return localFile != null && localFile.exists();
    }

    /**
     * Tells if the remote component of the resource is accessible
     *
     * @return true if the remote component is accessible, false otherwise
     */
    public boolean isRemoteAvailable() {
        if( !PhetSiteConnection.instance().isConnected() ) {
            return false;
        }
        else {
            return LauncherUtil.instance().isRemoteAvailable( url );
        }
    }

    /**
     * Tells if the local version of the resource is current with the remote version
     *
     * @return true if the local version of the resource is current
     */
    public boolean isCurrent() throws SimResourceException /*throws SimResourceException*/ {
        if( !PhetSiteConnection.instance().isConnected() ) {
            return true;
        }

        // Get timestamp for remote
        long remoteTimestamp = 0;
        try {
            remoteTimestamp = url.openConnection().getLastModified();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        if( metaData == null ) {
//            System.out.println( "SimResource.isCurrent: metadata == null" );
            return false;
        }

        // get timestamp of metadata
        long localTimestamp = metaData.getLastModified();

        // compare and return result
        if( localTimestamp > remoteTimestamp ) {
            throw new SimResourceException( "local timestamp newer than remote timestamp" );
        }
        return localTimestamp == remoteTimestamp;
    }

    public void download() throws SimResourceException {
        if( !isCurrent() ) {
            try {
                if( !localFile.getParentFile().exists() ) {
                    localFile.getParentFile().mkdirs();
                }
                if( !localFile.exists() ) {
                    localFile.createNewFile();
                }
                InputStream in = url.openStream();
                FileOutputStream out = new FileOutputStream( localFile );

                // Transfer bytes from in to out, from almanac
                byte[] buf = new byte[1024];
                int len;
                while( ( len = in.read( buf ) ) > 0 ) {
                    out.write( buf, 0, len );
                }
                out.flush();
                in.close();
                out.close();
                saveMetaData();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    private void saveMetaData() throws IOException {
        metaData = null;
        while( metaData == null ) {
            this.metaData = new MetaData( url );
        }
        metaData.saveForFile( localFile );
    }

    public void update() throws SimResourceException {
        if( isUpdatable() ) {
            download();
        }
    }

    protected File getLocalRoot() {
        return localRoot;
    }

    public File getLocalFile() {
        return localFile;
    }

    /**
     * Creates a local file for the resource
     *
     * @param localRoot
     * @return the local file
     */
    private File getLocalFile( File localRoot ) {
        // Parse the URL to get path relative to URL root
        String path = url.getPath();
        String pathSeparator = FileUtil.getPathSeparator();
        path = path.replace( '/', pathSeparator.charAt( 0 ) );
        path = path.replace( '\\', pathSeparator.charAt( 0 ) );
        return new File( localRoot, url.getHost() + pathSeparator + path );
//        return new File( localRoot, path );
    }

    /**
     * Removes the resource's local file and clears its metadata
     */
    public void uninstall() {
        localFile.delete();
        if( metaData != null ) {
            metaData.deleteForFile( localFile );
        }
        else {
            System.out.println( "metaData == null. this = " + this );
        }
        metaData = null;
    }
}