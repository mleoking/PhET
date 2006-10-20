/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model.resources;

import edu.colorado.phet.simlauncher.Options;
import edu.colorado.phet.simlauncher.model.MetaData;
import edu.colorado.phet.simlauncher.model.PhetSiteConnection;
import edu.colorado.phet.simlauncher.util.FileUtil;
import edu.colorado.phet.simlauncher.util.LauncherUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * SimResource
 * <p/>
 * A SimResource is *updatable* if there is a connection to the Phet site, the resource is not current,
 * and the global UPDATE_ENABLED flag is true.
 * <p/>
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
    private boolean isCurrent = true;

    public static boolean isUpdateEnabled() {
        return UPDATE_ENABLED;
    }

    public static void setUpdateEnabled( boolean UPDATE_ENABLED ) {
        SimResource.UPDATE_ENABLED = UPDATE_ENABLED;
    }

    static {
        Options.instance().addListener( new Options.ChangeListener() {
            public void optionsChanged( Options.ChangeEvent event ) {
                setUpdateEnabled( event.getOptions().isCheckForUpdatesOnStartup() );
            }
        } );
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    URL url;
    private MetaData metaData;
    private File localFile;
    private File localRoot;
    private boolean stopDownload;

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
            // Signlas a problem with the resource metafile. Delete the resource local file so that the
            // resource will need to be reinstalled
            catch( SimResourceException e ) {
                localFile.delete();
                uninstall();
            }
        }
    }

    /**
     * Says if the resource can be considered for updating
     *
     * @return true if the resouce can be considered for updating
     */
    protected boolean isUpdatable() {
        return UPDATE_ENABLED && isInstalled() && !isCurrent();
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
     * The SimResource checks to see if its local copy is current with the remote
     * resource.
     */
    public void checkForUpdate() {
        if( PhetSiteConnection.instance().isConnected() ) {
            // Get timestamp for remote
            long remoteTimestamp = 0;
            try {
                remoteTimestamp = url.openConnection().getLastModified();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }

            if( metaData == null ) {
                isCurrent = false;
            }
            else {
                // get timestamp of metadata
                long localTimestamp = metaData.getLastModified();

                // compare and return result
                if( localTimestamp > remoteTimestamp ) {
                    throw new RuntimeException( "local timestamp newer than remote timestamp" );
                }
                isCurrent = localTimestamp == remoteTimestamp;
            }
        }
    }

    /**
     * Tells if the local version of the resource is current with the remote version
     *
     * @return true if the local version of the resource is current
     */
    public boolean isCurrent() {
        return isCurrent;
    }

    /**
     * Downloands the resource to a local file
     *
     * @throws SimResourceException
     */
    public void download() throws SimResourceException {
        try {
            stopDownload = false;
            if( !localFile.getParentFile().exists() ) {
                localFile.getParentFile().mkdirs();
            }
            if( !localFile.exists() ) {
                localFile.createNewFile();
            }

            // download the bytes
            try {
                InputStream in = url.openStream();
                FileOutputStream out = new FileOutputStream( localFile );

                // Transfer bytes from in to out, from almanac
                byte[] buf = new byte[1024];
                int len;
                while( !getStopDownload() && ( len = in.read( buf ) ) > 0 ) {
                    out.write( buf, 0, len );
                }
                out.flush();
                in.close();
                out.close();
            }
            catch( java.net.UnknownHostException uhe ) {
                System.getProperty( "user.dir");
                System.out.println( "System.getProperty( \"user.dir\") = " + System.getProperty( "user.dir") );
                throw new SimResourceException( "Bad URL for resource: "
                                                + url.getHost() + url.getPath() );
            }

            // If the download was stopped in the middle, delete what we stored
            // otherwise, save the metadata for the resource.
            if( getStopDownload() ) {
                localFile.delete();
            }
            else {
                saveMetaData();
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param stopDownload
     */
    public synchronized void setStopDownload( boolean stopDownload ) {
        this.stopDownload = stopDownload;
    }

    private synchronized boolean getStopDownload() {
        return stopDownload;
    }

    private void saveMetaData() throws IOException {
        metaData = null;
        while( metaData == null ) {
            this.metaData = new MetaData( url );
        }
        metaData.saveForFile( localFile );
    }

    /**
     * Removes the resource's local file, downloads a new copy, and sets the isCurrent flag
     * to true
     *
     * @throws SimResourceException
     */
    public void update() throws SimResourceException {
        if( isUpdatable() ) {
            uninstall();
            download();
            isCurrent = true;
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
        metaData = null;
    }
}