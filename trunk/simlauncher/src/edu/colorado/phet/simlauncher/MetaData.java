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

import java.util.Properties;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

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

    public static MetaData initMetaData( URL url ) throws IOException {
        MetaData m = new MetaData();
        m.setProperty( LAST_MODIFIED, "" + url.openConnection().getLastModified() );
        m.setProperty( REMOTE_PROTOCOL, url.getProtocol() );
        m.setProperty( REMOTE_HOST, url.getHost() );
        m.setProperty( REMOTE_PATH, url.getPath() );
        return m;
    }

    public static MetaData loadMetaData( File f ) throws IOException {
        MetaData metaData = new MetaData();
        metaData.load( new FileInputStream( toMetaFile( f ) ) );
        return metaData;
    }

    public static File toMetaFile( File f ) {
        return new File( f.getAbsolutePath() + "-meta" );
    }

    public long getLastModified() {
        return Long.parseLong( getProperty( LAST_MODIFIED ) );
    }

    public void saveForFile( File local ) throws IOException {
        store( new FileOutputStream( toMetaFile( local ) ), "Automatically generated. Do not modify." );
    }

    public static boolean getMetaDataExists( File local ) {
        return toMetaFile( local ).exists();
    }
}

