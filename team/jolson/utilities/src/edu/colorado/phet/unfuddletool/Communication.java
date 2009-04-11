package edu.colorado.phet.unfuddletool;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.data.DateTime;

public class Communication {
    private Communication() {
    }

    public static String toString( Node document ) throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // Document source
        DOMSource source = new DOMSource( document );

        // StringWriter result
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult( stringWriter );

        transformer.transform( source, result );
        return stringWriter.toString();
    }

    public static Document toDocument( String string ) throws TransformerException, ParserConfigurationException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // StringReader source
        Source source = new StreamSource( new StringReader( string ) );

        // Document result
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        Result result = new DOMResult( document );

        transformer.transform( source, result );
        return document;
    }

    public static String invoke( String cmd ) throws IOException {
        Process p = Runtime.getRuntime().exec( cmd );
        StringBuffer s = new StringBuffer();
        InputStream in = p.getInputStream();
        int c;
        while ( ( c = in.read() ) != -1 ) {//blocks until data is available
            s.append( (char) c );
            System.out.print( c );
        }
        in.close();
        return s.toString();
    }

    public static String invoke( String[] cmd ) throws IOException {
        Process p = Runtime.getRuntime().exec( cmd );
        StringBuffer s = new StringBuffer();
        InputStream in = p.getInputStream();
        int c;
        while ( ( c = in.read() ) != -1 ) {//blocks until data is available
            s.append( (char) c );
            //System.out.print( c );
        }
        in.close();
        return s.toString();
    }

    public static String execCommand( String cmd ) throws IOException, InterruptedException {
        System.out.println( "Executing: " + cmd );
        String s = invoke( cmd );
        return s.substring( s.indexOf( "<?xml" ) );
        //return s;
    }

    public static String execCommand( String[] cmd ) throws IOException, InterruptedException {
        //System.out.println( "Executing: " + cmd );
        String s = invoke( cmd );
        return s.substring( s.indexOf( "<?xml" ) );
        //return s;
    }

    public static String getXMLResponse( String xmlString, String location, String auth ) {
        String ret = null;
        try {
            ret = execCommand( new String[]{"curl", "-i", "-u", auth, "-X", "GET", "-H", "Accept: application/xml", "-d", xmlString, "http://phet.unfuddle.com/api/v1/" + location} );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        return ret;
    }


    public static String firstChildString( Node node ) {
        if ( node.getNodeType() == Node.TEXT_NODE ) {
            return node.getNodeValue();
        }
        else if ( node.hasChildNodes() ) {
            return firstChildString( node.getFirstChild() );
        }
        else {
            return null;
        }
    }

    public static String getTagString( Element event, String name ) {
        NodeList list = event.getElementsByTagName( name );
        return firstChildString( list.item( 0 ) );
    }

    public static Node getFirstNodeByName( Element element, String name ) {
        return ( element.getElementsByTagName( name ) ).item( 0 );
    }

    public static int getIntField( Element element, String name ) {
        return Integer.parseInt( getTagString( element, name ) );
    }

    public static int getOptionalIntField( Element element, String name ) {
        String str = getTagString( element, name );
        if ( str == null ) { return -1; }
        return Integer.parseInt( str );
    }

    public static String getStringField( Element element, String name ) {
        return getTagString( element, name );
    }

    public static DateTime getDateTimeField( Element element, String name ) {
        return new DateTime( getTagString( element, name ) );
    }

    public static String HTMLPrepare( String str ) {
        String ret = str.replaceAll( "\n", "ABRACADABRAX" );

        ret = HTMLEntityEncode( ret );

        ret = ret.replaceAll( "ABRACADABRAX", "<br/>" );

        return ret;
    }

    public static String HTMLEntityEncode( String s ) {
        StringBuffer buf = new StringBuffer();
        int len = ( s == null ? -1 : s.length() );

        for ( int i = 0; i < len; i++ ) {
            char c = s.charAt( i );
            if ( c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' ) {
                buf.append( c );
            }
            else {
                buf.append( "&#" + (int) c + ";" );
            }
        }
        return buf.toString();
    }
}
