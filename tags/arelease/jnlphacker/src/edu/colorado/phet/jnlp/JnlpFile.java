/**
 * Class: JnlpFile
 * Package: edu.colorado.phet.jnlp
 * Author: Another Guy
 * Date: May 14, 2003
 */
package edu.colorado.phet.jnlp;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

/**
 * A class for manipulating JNLP files
 */
public class JnlpFile {

    private String jnlpUrl;
    private String jnlpText;
    private JnlpFile[] extensions;
    private String[] jarUrls;
    private String title;
    private Document jdomDoc;
    private String codebase;

    /**
     *
     * @param jnlpUrl
     * @throws InvalidJnlpException
     */
    public JnlpFile( String jnlpUrl ) throws InvalidJnlpException {
        this.jnlpUrl = jnlpUrl;
        jnlpText = getJnlpText( jnlpUrl );
        jarUrls = parseJarURLs();
        title = parseTitle();
        extensions = parseExtensions();
    }

    public JnlpFile( File jnlpFile ) {

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
    }

    /**
     * Provided for decorators
     * @param jnlpFile
     */
    protected JnlpFile( JnlpFile jnlpFile ) {
        jnlpUrl = jnlpFile.jnlpUrl;
        jnlpText = jnlpFile.jnlpText;
        jarUrls = jnlpFile.jarUrls;
        title = jnlpFile.title;
        extensions = jnlpFile.extensions;
    }

    public String toString() {
//        XMLOutputter xmlOutputter = new XMLOutputter();
        XMLOutputter xmlOutputter = new XMLOutputter( "", true );
//        xmlOutputter.setLineSeparator( "\n" );
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

    public String[] getJarUrls() {
        return jarUrls;
    }

    /**
     * Returns the value of the codebase for the JNLP file
     * @return
     */
    public String getCodebase() {
        codebase = getJdomDoc().getRootElement().getAttribute( "codebase" ).getValue();
        return codebase;
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
                StringReader sr = new StringReader( getJnlpText( ));
                if( sr == null ) {
                    throw new RuntimeException( "Error parsing document at: " + this.jnlpUrl );
                }
                jdomDoc = builder.build( new StringReader( getJnlpText() ));
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
     *
     * @param jnlpURL
     * @return
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
            String s = getJnlpText();
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
     *
     * @return
     */
    private String parseTitle() throws InvalidJnlpException {
        Element titleElement = getJdomDoc().getRootElement().
                getChild( "information" ).
                getChild( "title" );
        return titleElement.getText();
    }

    /**
     * Finds the jar resources in the JNLP file and builds an array of URLs for them
     * @return
     */
    private String[] parseJarURLs() {

        ArrayList resultList = new ArrayList();
        Element resourcesElement = getJdomDoc().getRootElement().getChild( s_resourcesTag );
        List jarElementList = resourcesElement.getChildren( s_jarTag );
        for( int i = 0; i < jarElementList.size(); i++ ) {
            Element jarElement = (Element)jarElementList.get( i );
            String jarName = jarElement.getAttributeValue( s_hrefAttrib );
            String jarURL = new String( "jar:" ).concat( getCodebase() ).concat( "/" ).concat( jarName ).concat( "!/");
            resultList.add( jarURL );
        }
        return (String[])resultList.toArray( new String[resultList.size()] );
    }

    /**
     *
     * @return
     */
    private JnlpFile[] parseExtensions() throws InvalidJnlpException {

        ArrayList resultList = new ArrayList();
        Element resourcesElement = getJdomDoc().getRootElement().getChild( s_resourcesTag );
        List extensionElementList = resourcesElement.getChildren( s_extensionTag );
        for( int i = 0; i < extensionElementList.size(); i++ ) {
            Element jarElement = (Element)extensionElementList.get( i );
            String extensionJnlpName = jarElement.getAttributeValue( s_hrefAttrib );
            String jnlpURL = new String( codebase ).concat( "/" ).concat( extensionJnlpName );
            resultList.add( new JnlpFile( jnlpURL ));
        }
        return (JnlpFile[])resultList.toArray( new JnlpFile[resultList.size()] );
    }

    //
    // Static fields and methods
    //
    private static final String s_resourcesTag = "resources";
    private static final String s_jarTag = "jar";
    private static final String s_extensionTag = "extension";
    private static final String s_hrefAttrib = "href";
}
