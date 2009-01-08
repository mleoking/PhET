package edu.colorado.phet.common.phetcommon.updates;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * XMLVersionChecker gets the most up-to-date version information from the PhET website's XML service.
 */
public class XMLVersionChecker extends AbstractVersionChecker {

    public XMLVersionChecker() {}

    public PhetVersion getVersion( String project, String simulation ) throws IOException {
        String read = readURL( HTMLUtils.getSimVersionXML_PHPQueryURL( project, simulation ) );
//        System.out.println( "read = " + read );
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( new ByteArrayInputStream( read.getBytes() ) );
            String version = document.getDocumentElement().getAttribute( "version" );
            String revision = document.getDocumentElement().getAttribute( "revision" );
            StringTokenizer t = new StringTokenizer( version, "." );
            return new PhetVersion( t.nextToken(), t.nextToken(), t.nextToken(), revision );
        }
        catch( Exception e ) {
            //bundle this exception as IOException to use same error handling on the client side
            throw new IOException("Failed to obtain version information from XML" );
        }
    }

    public static void main( String[] args ) throws IOException {
        System.out.println( "DefaultVersionChecker.main" );
        PhetVersion phetVersionInfo = new XMLVersionChecker().getVersion( "balloons", "balloons" );
        System.out.println( "phetVersionInfo = " + phetVersionInfo );
    }
}