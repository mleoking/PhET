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
 * JnlpResource
 * <p>
 * The SimResource for the JNLP file.
 * <p>
 * There may be two separate copies of the JNLP file for this resource. This happens if the
 * URL specified in the catalog is not the true URL where the server-baased JNLP file lives,
 * and the URL in the catalog is redirected to a different URL. In that case, the jar files
 * and other resources will be in a directory on the local computer that has a different path
 * than what would be seen as the path to the JNLP file in the catalog. In this case, the two
 * local JNLP files will be identical. The one in the path created from the URL in the catalog
 * will href to the one in the path created from the actual location of the JNLP file on the
 * server.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class JnlpResource extends SimResource {

    private static String FILE_SEPARATOR = System.getProperty( "file.separator" );
    private static String LOCAL_CODEBASE_PREFIX = "file:" + FILE_SEPARATOR + FILE_SEPARATOR;

    private String remoteCodebase;
    private String localCodebaseJnlpFilename;

    public JnlpResource( URL url, File localRoot ) {
        super( url, localRoot );
        this.url = url;
    }

    public void download() throws SimResourceException {
        super.download();

        // Get the remoote codebase, and modify codebase of the local jnlp file
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
            URL tempURL = new URL( remoteCodebase );
            relativeCodebase = tempURL.getHost() + tempURL.getPath();

            // Make sure the codebase ends with a file separator character
            String lastChar = relativeCodebase.endsWith( "/" ) || relativeCodebase.endsWith( "\\" ) ? "" : FILE_SEPARATOR;
            String newCodebase = LOCAL_CODEBASE_PREFIX + getLocalRoot() + FILE_SEPARATOR + relativeCodebase + lastChar;
            jnlpFile.setCodebase( newCodebase );
            BufferedWriter out = new BufferedWriter( new FileWriter( getLocalFileName() ) );
            out.write( jnlpFile.toString() );
            out.close();

            // Write a copy of the JNLP file to the local codebase. This may or may not be the same as
            // what we get from getLocalFileName(), because of possible redirections on the server.
            localCodebaseJnlpFilename = getLocalRoot() + FILE_SEPARATOR + relativeCodebase + lastChar + jnlpFile.getjnlpHref();

            BufferedWriter out2 = new BufferedWriter( new FileWriter( localCodebaseJnlpFilename ) );
            out2.write( jnlpFile.toString() );
            out2.close();

            System.out.println( "localCodebaseJnlpFilename = " + localCodebaseJnlpFilename );
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
     * Because there are situations where redirections on the server from the URL in the catalog
     * end up with the actual server resources being at a different URL than the catalog shows,
     * we may have a copy of the JNLP file in a local path that differs from the primary JNLP file
     * created with the URL shown in the catalog. We need to delete that copy, it it exists
     */
    public void uninstall() {
        super.uninstall();
        // Delete the copy of the JNLP file that's in the local codebase, in case that is
        // different than the one specified in the catalog
        try {
            File auxilliaryJnlpFile = new File( localCodebaseJnlpFilename );
            System.out.println( "localCodebaseJnlpFilename = " + localCodebaseJnlpFilename );
            if( auxilliaryJnlpFile.exists() ) {
                auxilliaryJnlpFile.delete();
            }
        }
        catch( Exception e )
        {
            System.out.println( "JnlpResource.uninstall" );

        }
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
