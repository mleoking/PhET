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

import edu.colorado.phet.simlauncher.resources.SimResourceException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * MetaData
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MetaData extends Properties {
    private static final String LAST_MODIFIED = "lastmodified";
    private static final String REMOTE_PROTOCOL = "remote.protocol";
    private static final String REMOTE_HOST = "remote.host";
    private static final String REMOTE_PATH = "remote.path";

//    public static MetaData initMetaData( URL url ) throws IOException {
//        MetaData m = new MetaData();
//        m.setProperty( LAST_MODIFIED, "" + url.openConnection().getLastModified() );
//        m.setProperty( REMOTE_PROTOCOL, url.getProtocol() );
//        m.setProperty( REMOTE_HOST, url.getHost() );
//        m.setProperty( REMOTE_PATH, url.getPath() );
//        return m;
//    }

//    public static MetaData loadMetaData( File f ) throws IOException {
//        MetaData metaData = new MetaData();
//        metaData.load( new FileInputStream( toMetaFile( f ) ) );
//        return metaData;
//    }
//

//    public static boolean getMetaDataExists( File local ) {
//        return toMetaFile( local ).exists();
//    }
//

    private static File toMetaFile( File f ) {
        return new File( f.getAbsolutePath() + "-meta" );
    }

    public MetaData( URL url ) throws IOException {
        setProperty( LAST_MODIFIED, "" + url.openConnection().getLastModified() );
        setProperty( REMOTE_PROTOCOL, url.getProtocol() );
        setProperty( REMOTE_HOST, url.getHost() );
        setProperty( REMOTE_PATH, url.getPath() );
    }

    public MetaData( File localResourceFile ) throws IOException, SimResourceException {
        File  metaFile = toMetaFile( localResourceFile );
        if( !metaFile.exists() ) {
            throw new SimResourceException( "metafile missing for local resource = " + localResourceFile.getName() );
        }
        load( new FileInputStream( toMetaFile( localResourceFile ) ) );
    }

    public static boolean getMetaDataExists( File local ) {
        return toMetaFile( local ).exists();
    }

    public long getLastModified() {
        return Long.parseLong( getProperty( LAST_MODIFIED ) );
    }

    public void saveForFile( File local ) throws IOException {
        store( new FileOutputStream( toMetaFile( local ) ), "Automatically generated. Do not modify." );
    }

    public void deleteForFile( File local ) {
        toMetaFile( local ).delete();
    }
}
