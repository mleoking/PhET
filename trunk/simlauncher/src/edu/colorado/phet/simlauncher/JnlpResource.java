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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ImageResource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class JnlpResource extends SimResource {

    private static String FILE_SEPARATOR = System.getProperty( "file.separator");
    private static String LOCAL_CODEBASE_PREFIX = "file:" + FILE_SEPARATOR + FILE_SEPARATOR;

    private String remoteCodebase;

    public JnlpResource( URL url, File localRoot ) {
        super( url, localRoot );
        this.url = url;
        try {
            remoteCodebase = new JnlpFile( url ).getCodebase();
        }
        catch( InvalidJnlpException e ) {
            e.printStackTrace();
        }
    }

    public void download() {
        super.download();

        // Modify codebase of the file
        JnlpFile jnlpFile = null;
        try {
            jnlpFile = new JnlpFile( getLocalFile() );
        }
        catch( InvalidJnlpException e ) {
            e.printStackTrace();
        }

        remoteCodebase = jnlpFile.getCodebase();
        String localPath = getLocalFileName();
        if( System.getProperty( "os.name" ).toLowerCase().contains( "windows" ) && localPath.contains( ":" ) ) {
            localPath = localPath.substring( localPath.indexOf( ':' ) + 1 );
        }

        // Set the codebase to the local location and write the local jnlp file
        try {
            String relativeCodebase = null;
            URL tempURL = new URL( remoteCodebase );
            relativeCodebase = tempURL.getHost() + tempURL.getPath();
            String newCodebase = LOCAL_CODEBASE_PREFIX + getLocalRoot() + FILE_SEPARATOR + relativeCodebase + FILE_SEPARATOR;
            jnlpFile.setCodebase( newCodebase );
            BufferedWriter out = new BufferedWriter( new FileWriter( getLocalFileName() ) );
            out.write( jnlpFile.toString() );
            out.close();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public String getLocalFileName() {
        return getLocalFile().getAbsolutePath();
    }

    /**
     * Gets the JarResources specified in this jnlp file
     *
     * @return an array of JnlpResources
     */
    public JarResource[] getJarResources() {
        JnlpFile jnlpFile = null;
        if( getLocalFile() != null && getLocalFile().exists() ) {
            try {
                jnlpFile = new JnlpFile( getLocalFile() );
            }
            catch( InvalidJnlpException e ) {
                e.printStackTrace();
            }
        }
        else {
            try {
                jnlpFile = new JnlpFile( url );
            }
            catch( InvalidJnlpException e ) {
                e.printStackTrace();
            }
        }
        String[] urlStrings = jnlpFile.getRelativeJarPaths();
        JarResource[] jarResources = new JarResource[urlStrings.length];
        for( int i = 0; i < urlStrings.length; i++ ) {
            String urlString = new String( getRemoteCodebase() ).concat( "/" ).concat( urlStrings[i] );
            URL url = null;
            try {
                url = new URL( urlString );
            }
            catch( MalformedURLException e ) {
                e.printStackTrace();
            }
            JarResource jarResource = new JarResource( url, getLocalRoot() );
            jarResources[i] = jarResource;
        }
        return jarResources;
    }

    /**
     * For debug
     */
    public String getLocalContents() {
        JnlpFile2 jnlpFile = new JnlpFile2( getLocalFile().getAbsolutePath() );
        String s = jnlpFile.getContents();
        return s;
    }

    private String getRemoteCodebase() {
        return remoteCodebase;
    }
}
