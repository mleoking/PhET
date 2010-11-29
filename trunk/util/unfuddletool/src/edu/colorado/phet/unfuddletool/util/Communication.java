package edu.colorado.phet.unfuddletool.util;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.colorado.phet.unfuddletool.Authentication;
import edu.colorado.phet.unfuddletool.Configuration;
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

        //System.out.println( "Requesting " + location );
        String ret = rawHttpsRequest( xmlString, location, auth );
        //System.out.println( "Received " + location );
        return ret;

        //return persistentHTTPRequest( xmlString, location, auth );
    }

    private static String toCommand( String[] cmd ) {
        String s = "";
        for ( int i = 0; i < cmd.length; i++ ) {
            s = s + cmd[i] + " ";
        }
        return s;
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

    public static String base64Data = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";


    public static String base64Encode( String string ) {
        String ret = "";
        int count = ( 3 - ( string.length() % 3 ) ) % 3;
        string += "\0\0".substring( 0, count );
        for ( int i = 0; i < string.length(); i += 3 ) {
            int j = ( string.charAt( i ) << 16 ) + ( string.charAt( i + 1 ) << 8 ) + string.charAt( i + 2 );
            ret = ret + base64Data.charAt( ( j >> 18 ) & 0x3f ) + base64Data.charAt( ( j >> 12 ) & 0x3f ) + base64Data.charAt( ( j >> 6 ) & 0x3f ) + base64Data.charAt( j & 0x3f );
        }
        return ret.substring( 0, ret.length() - count ) + "==".substring( 0, count );
    }


    public static String HTMLPrepare( String str ) {

        if ( str == null ) {
            return str;
        }

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

    public static String encodeAuth( String auth ) {
        return base64Encode( auth );
    }

    public static String HTMLRequestString( String xml, String location, String auth ) {
        String ret = "GET /api/v1/" + location + " HTTP/1.1\n";
        ret += "Authorization: Basic " + encodeAuth( auth ) + "\n";
        ret += "User-Agent: curl/7.16.4 (i486-pc-linux-gnu) libcurl/7.16.4 OpenSSL/0.9.8e zlib/1.2.3.3 libidn/1.0\n";
        ret += "Host: " + Configuration.getAccountName() + ".unfuddle.com\n";
        ret += "Accept: application/xml\n";
        ret += "Content-Length: " + String.valueOf( xml.length() ) + "\n";
        ret += "Content-Type: application/x-www-form-urlencoded\n";
        ret += "Connection: close\n";
        ret += "\n";
        ret += xml;
        ret += "\n"; // should not be necessary
        return ret;
    }

    public static String rawHTTPRequest( String xml, String location, String auth ) {
        StringBuffer buf = new StringBuffer();
        Socket socket = null;
        try {
            InetAddress addr = InetAddress.getByName( Configuration.getAccountName() + ".unfuddle.com" );
            SocketAddress sockAddr = new InetSocketAddress( addr, 80 );

            socket = new Socket();
            socket.connect( sockAddr, 30000 );

            PrintWriter out = new PrintWriter( socket.getOutputStream() );
            BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

            String requestString = HTMLRequestString( xml, location, auth );

            out.println( requestString );
            out.flush();

            String line;

            boolean errorSet = false;

            while ( ( line = in.readLine() ) != null && line.length() > 0 ) {
                //buf.append( "Header: " + line + "\n" );

                if ( errorSet ) {
                    System.err.println( line );
                }

                if ( line.startsWith( "HTTP/1.1 " ) ) {
                    if ( !line.startsWith( "HTTP/1.1 200" ) ) {
                        errorSet = true;
                        System.err.println( "WARNING: HTTP Error code received" );
                        System.err.println( "----====] Request [====----" );
                        System.err.println( requestString );
                        System.err.println( "\n----====] Response [====----" );
                        System.err.println( line );
                    }
                }
            }

            while ( ( line = in.readLine() ) != null ) {
                buf.append( line + "\n" );

                if ( errorSet ) {
                    System.err.println( line );
                }
            }

            if ( errorSet ) {
                System.err.println( "----------------" );
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        finally {
            if ( socket != null && !socket.isClosed() ) {
                try {
                    socket.close();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }

        return buf.toString();
    }

    private static String rawHttpsRequest( String xml, String location, String auth ) {
        try {
            String uri = "https://phet.unfuddle.com/api/v1/" + location;

            org.apache.http.client.HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet( uri );
            httpget.addHeader( "Authorization", "Basic " + encodeAuth( auth ) );
            httpget.addHeader( "Accept", "application/xml" );
            httpget.addHeader( "User-Agent", "curl/7.16.4 (i486-pc-linux-gnu) libcurl/7.16.4 OpenSSL/0.9.8e zlib/1.2.3.3 libidn/1.0" );

            StringBuilder buf = new StringBuilder();

            try {
                System.out.println( "executing: " + httpget.getRequestLine() );
                HttpResponse response = httpclient.execute( httpget );
                System.out.println( response.getStatusLine() );
                System.out.println( response.getEntity().getContentLength() );

                BufferedReader in = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );
                try {
                    String line;
                    while ( ( line = in.readLine() ) != null ) {
                        buf.append( line ).append( "\n" );
                    }
                }
                finally {
                    in.close();
                }
            }
            finally {
                //httpclient.getConnectionManager().shutdown();
            }

            return buf.toString();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
        return "";
    }

    public static Element getTicketElementFromServer( int id ) {
        String xmlString = Communication.getXMLResponse( "<request></request>", "projects/" + Configuration.getProjectIdString() + "/tickets/" + String.valueOf( id ), Authentication.auth );
        try {
            return (Element) Communication.toDocument( xmlString ).getFirstChild();
        }
        catch ( TransformerException e ) {
            e.printStackTrace();
        }
        catch ( ParserConfigurationException e ) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main( String[] args ) {
        Authentication.auth = args[0];

        System.out.println( rawHttpsRequest( "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><start-date>2009/4/9</start-date><end-date>2010/1/1</end-date><limit>50</limit></request>", "projects/9404/activity", Authentication.auth ) );
    }
}
