/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * JnlpFile
 * A class for manipulating JNLP files
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class JnlpFile {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static final String s_resourcesTag = "resources";
    private static final String s_jarTag = "jar";
    private static final String s_extensionTag = "extension";
    private static final String s_hrefAttrib = "href";

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private String jnlpUrl;
    private String jnlpText;
    private JnlpFile[] extensions;
    private URL[] jarUrls;
    private String[] jarPaths;
    private String title;
    private Document jdomDoc;

    /**
     * @param jnlpURL URL to the actual JNLP file
     */
    public JnlpFile( URL jnlpURL ) throws InvalidJnlpException {
        this( jnlpURL.toString() );
    }

    /**
     * Constructor that takes a reference to a URL
     *
     * @param jnlpUrl String representation of the URL to the actual jnlp file
     * @throws InvalidJnlpException
     */
    public JnlpFile( String jnlpUrl ) throws InvalidJnlpException {
        this.jnlpUrl = jnlpUrl;
        jnlpText = getJnlpText( jnlpUrl );
        jarUrls = parseJarURLs();
        jarPaths = parseJarRelativePaths();
        title = parseTitle();
        extensions = parseExtensions();
    }

    /**
     * Constructor that takes a reference to a File
     *
     * @param jnlpFile
     * @throws InvalidJnlpException
     */
    public JnlpFile( File jnlpFile ) throws InvalidJnlpException {

        // Build the document with SAX and Xerces, no validation
        try {
            SAXBuilder builder = new SAXBuilder();
            jdomDoc = builder.build( jnlpFile );
        }
        catch( JDOMException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Error parsing jnlp file: " + jnlpFile.getName() );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( "Error parsing jnlp file: " + jnlpFile.getName() );
        }

        jarPaths = parseJarRelativePaths();
        jarUrls = parseJarURLs();
        title = parseTitle();
        extensions = parseExtensions();
    }

    /**
     * Provided for decorators
     *
     * @param jnlpFile
     */
    protected JnlpFile( JnlpFile jnlpFile ) {
        jnlpUrl = jnlpFile.jnlpUrl;
        jnlpText = jnlpFile.jnlpText;
        jarPaths = parseJarRelativePaths();
        jarUrls = jnlpFile.jarUrls;
        title = jnlpFile.title;
        extensions = jnlpFile.extensions;
    }

    public String toString() {
        XMLOutputter xmlOutputter = new XMLOutputter();
        return xmlOutputter.outputString( jdomDoc );
    }

    public String getTitle() {
        return title;
    }

    public JnlpFile[] getExtensions() {
        return extensions;
    }

    protected String getJnlpText() {
        return jnlpText;
    }

    public URL[] getJarUrls() {
        return jarUrls;
    }

    public String[] getRelativeJarPaths() {
        return jarPaths;
    }

    /**
     * Returns the value of the codebase for the JNLP file
     *
     * @return the codebase
     */
    public String getCodebase() {
        return getJdomDoc().getRootElement().getAttribute( "codebase" ).getValue();
    }

    /**
     * Returns the href to the jnlp file in the jnlp element
     *
     * @return the jnlp href
     */
    public String getjnlpHref() {
        return getJdomDoc().getRootElement().getAttribute( "href" ).getValue();
    }

    public void setCodebase( String codebase ) {
        getJdomDoc().getRootElement().getAttribute( "codebase" ).setValue( codebase );
    }

    public void setJnlpUrl( String url ) {
        this.jnlpUrl = url;
    }

    private Document getJdomDoc() {
        if( jdomDoc == null ) {
            // Build the document with SAX and Xerces, no validation
            try {
                SAXBuilder builder = new SAXBuilder();
                StringReader sr = new StringReader( getJnlpText() );
                if( sr == null ) {
                    throw new RuntimeException( "Error parsing document at: " + this.jnlpUrl );
                }
                jdomDoc = builder.build( new StringReader( getJnlpText() ) );
            }
            catch( JDOMException e ) {
                e.printStackTrace();
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        return jdomDoc;
    }

    /**
     * @param jnlpURL
     * @return the ASCII text of the jnlp file
     */
    private String getJnlpText( String jnlpURL ) throws InvalidJnlpException {
        StringBuffer jnlpTextBuff = new StringBuffer( 1028 );
        try {
            URL url = new URL( jnlpURL );
            BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ) );
            String str;
            while( ( str = in.readLine() ) != null ) {
                jnlpTextBuff.append( str ).append( '\n' );
            }
            in.close();
        }
        catch( FileNotFoundException e ) {
            // There is a bad reference to a jnlp file in the index page.
            throw new InvalidJnlpException();
        }
        catch( java.io.IOException e ) {
            e.printStackTrace();
        }
        return jnlpTextBuff.toString();
    }

    public void write() {
        try {
            URL url = new URL( this.jnlpUrl );
            URLConnection urlc = url.openConnection();
            urlc.setDoOutput( true );
            OutputStream os = urlc.getOutputStream();
            os.write( getJnlpText().getBytes() );
            os.close();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * @return the title
     */
    private String parseTitle() {
        Element titleElement = getJdomDoc().getRootElement().
                getChild( "information" ).
                getChild( "title" );
        return titleElement.getText();
    }

    private String[] parseJarRelativePaths() {
        ArrayList resultList = new ArrayList();
        Element resourcesElement = getJdomDoc().getRootElement().getChild( s_resourcesTag );
        List jarElementList = resourcesElement.getChildren( s_jarTag );
        for( int i = 0; i < jarElementList.size(); i++ ) {
            Element jarElement = (Element)jarElementList.get( i );
            String jarName = jarElement.getAttributeValue( s_hrefAttrib );
            resultList.add( jarName );
        }
        return (String[])resultList.toArray( new String[resultList.size()] );
    }

    /**
     * Finds the jar resources in the JNLP file and builds an array of URLs for them
     *
     * @return URLs for the jar resources
     */
    private URL[] parseJarURLs() {

        ArrayList resultList = new ArrayList();
        Element resourcesElement = getJdomDoc().getRootElement().getChild( s_resourcesTag );
        List jarElementList = resourcesElement.getChildren( s_jarTag );
        try {
            for( int i = 0; i < jarElementList.size(); i++ ) {
                Element jarElement = (Element)jarElementList.get( i );
                String jarName = jarElement.getAttributeValue( s_hrefAttrib );
                String jarURLStr = new String( "jar:" ).concat( getCodebase() ).concat( "/" ).concat( jarName ).concat( "!/" );
                URL jarURL = new URL( jarURLStr );
                resultList.add( jarURL );
            }
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        return (URL[])resultList.toArray( new URL[resultList.size()] );
    }

    /**
     * @return JnlpFile extensions
     */
    private JnlpFile[] parseExtensions() throws InvalidJnlpException {

        ArrayList resultList = new ArrayList();
        Element resourcesElement = getJdomDoc().getRootElement().getChild( s_resourcesTag );
        List extensionElementList = resourcesElement.getChildren( s_extensionTag );
        for( int i = 0; i < extensionElementList.size(); i++ ) {
            Element jarElement = (Element)extensionElementList.get( i );
            String extensionJnlpName = jarElement.getAttributeValue( s_hrefAttrib );
            String jnlpURL = new String( getCodebase() ).concat( "/" ).concat( extensionJnlpName );
            resultList.add( new JnlpFile( jnlpURL ) );
        }
        return (JnlpFile[])resultList.toArray( new JnlpFile[resultList.size()] );
    }
}

