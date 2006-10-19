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

import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.model.InvalidJnlpException;
import edu.colorado.phet.simlauncher.model.JnlpFile;
import edu.colorado.phet.simlauncher.util.DebugStringFile;

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

    private static String FILE_SEPARATOR = System.getProperty( "file.separator" );
    private static String LOCAL_CODEBASE_PREFIX = "file:" + FILE_SEPARATOR + FILE_SEPARATOR;

    private String remoteCodebase;

    public JnlpResource( URL url, File localRoot ) {
        super( url, localRoot );
        this.url = url;
    }

    public void download() throws SimResourceException {
        super.download();

        // Get the reomote codebase, and modify codebase of the local jnlp file
        JnlpFile jnlpFile = null;
        try {
            jnlpFile = new JnlpFile( getLocalFile() );
        }
        catch( InvalidJnlpException e ) {
            e.printStackTrace();
        }
        remoteCodebase = jnlpFile.getCodebase();
        String localPath = getLocalFileName();
        if( System.getProperty( "os.name" ).toLowerCase().indexOf( "windows" ) >= 0 && localPath.indexOf( ":" ) >= 0 ) {
            localPath = localPath.substring( localPath.indexOf( ':' ) + 1 );
        }

        // Set the codebase to the local location and write the local jnlp file
        try {
            String relativeCodebase = null;
//            URL tempURL = new URL( Configuration.instance().);
            URL tempURL = new URL( remoteCodebase );
            relativeCodebase = tempURL.getHost() + tempURL.getPath();
            String lastChar = relativeCodebase.endsWith( "/" ) || relativeCodebase.endsWith( "\\" ) ? "" : FILE_SEPARATOR;
            String newCodebase = LOCAL_CODEBASE_PREFIX + getLocalRoot() + FILE_SEPARATOR + relativeCodebase + lastChar;
            jnlpFile.setCodebase( newCodebase );
            BufferedWriter out = new BufferedWriter( new FileWriter( getLocalFileName() ) );
            out.write( jnlpFile.toString() );
            out.close();

            // Write a copy of the JNLP file to the local codebase. This may or may not be the same as
            // what we get from getLocalFileName(), because of possible redirections on the server.
            String localCodebaseFilename = getLocalRoot() + FILE_SEPARATOR + relativeCodebase + lastChar + jnlpFile.getjnlpHref();
//            String localCodebaseFilename = newCodebase + getLocalFile().getName();
            BufferedWriter out2 = new BufferedWriter( new FileWriter( localCodebaseFilename ) );
            out2.write( jnlpFile.toString() );
            out2.close();

            System.out.println( "localCodebaseFilename = " + localCodebaseFilename );
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
     * todo: The way I handle not being connected to the PhET site is not very good here.
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
        else { // todo: take this out? it's never called
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
            String codebase = Configuration.instance().getSimulationsUrl().toString();
            if( remoteCodebase != null ) {
                codebase = remoteCodebase;
            }
            String s = codebase.endsWith( "/" ) || codebase.endsWith( "\\" ) ? "" : "/";
            String urlString = new String( codebase ).concat( s ).concat( urlStrings[i] );
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
        DebugStringFile jnlpFile = new DebugStringFile( getLocalFile().getAbsolutePath() );
        String s = jnlpFile.getContents();
        return s;
    }
}
