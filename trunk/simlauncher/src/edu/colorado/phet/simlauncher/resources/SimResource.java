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

import edu.colorado.phet.simlauncher.util.LauncherUtil;
import edu.colorado.phet.simlauncher.MetaData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * SimResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimResource {
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
    }

    public boolean isCurrent() {
        if( !LauncherUtil.getInstance().isRemoteAvailable( url ) ) {
            throw new RuntimeException( "not online" );
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
            return false;
        }

        // get timestamp of metadata
        long localTimestamp = metaData.getLastModified();

        // compare and return result
        if( localTimestamp > remoteTimestamp ) {
            throw new RuntimeException( "local timestamp newer than remote timestamp" );
        }
        return localTimestamp == remoteTimestamp;
    }

    public void download() {
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
        this.metaData = MetaData.initMetaData( url );
        metaData.saveForFile( localFile );
    }

    public void update() {
        if( !isCurrent() ) {
            download();
        }
    }

    protected File getLocalRoot() {
        return localRoot;
    }

    public File getLocalFile() {
        return localFile;
    }

    private File getLocalFile( File localRoot ) {
        // Parse the URL to get path relative to URL root
        String path = url.getPath();
        String pathSeparator = System.getProperty( "file.separator" );
        path = path.replace( '/', pathSeparator.charAt( 0 ) );
        path = path.replace( '\\', pathSeparator.charAt( 0 ) );
        return new File( localRoot, url.getHost() + pathSeparator + path );
    }
}
