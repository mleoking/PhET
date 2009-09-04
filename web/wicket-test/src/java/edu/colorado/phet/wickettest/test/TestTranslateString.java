package edu.colorado.phet.wickettest.test;

import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestTranslateString {

    public static void main( String[] args ) {
        //System.out.println( rawHTTPRequest() );
//        System.out.println( extractString( "{\"responseData\": {\"translatedText\":\"baron\"}, \"responseDetails\": null, \"responseStatus\": 200}") );
//        System.out.println( extractString( "{\"responseData\": {\"translatedText\":\"\u0645\u0631\u062d\u0628\u0627 \u0627\u0644\u0639\u0627\u0644\u0645\"}, \"responseDetails\": null, \"responseStatus\": 200}") );
        System.out.println( translate( "hello world", "en", "ar" ) );
        System.out.println( translate( "hello world", "en", "es" ) );
        System.out.println( translate( "hello world", "en", "zh_CN" ) );
    }

    public static String translate( String text, String from, String to ) {
        return extractString( rawHTTPRequest( text, from, to ) );
    }

    private static Pattern pattern = Pattern.compile( "\\{\"translatedText\":\"([^\"]+)\"\\}" );

    public static String extractString( String result ) {
        Matcher matcher = pattern.matcher( result );
        if ( matcher.find() ) {
            return matcher.group( 1 );
        }
        else {
            System.out.println( "Translation error in: " + result );
            return null;
        }
    }

    public static String HTMLRequestString( String text, String from, String to ) {
        String encodedText = null;
        try {
            encodedText = URLEncoder.encode( text, "UTF-8" );
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }

        String ret = "GET http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q=" + encodedText + "&langpair=" + from + "%7C" + to + " HTTP/1.1\n";
//        ret += "User-Agent: curl/7.16.4 (i486-pc-linux-gnu) libcurl/7.16.4 OpenSSL/0.9.8e zlib/1.2.3.3 libidn/1.0\n";
        ret += "Host: ajax.googleapis.com\n";
        ret += "Referer: http://phet.colorado.edu\n";
//        ret += "Accept: application/xml\n";
//        ret += "Content-Length: " + String.valueOf( xml.length() ) + "\n";
//        ret += "Content-Type: application/x-www-form-urlencoded\n";
        ret += "Connection: close\n";
        ret += "\n";
//        ret += xml;
//        ret += "\n"; // should not be necessary
        return ret;
    }

    public static String rawHTTPRequest( String text, String from, String to ) {
        StringBuffer buf = new StringBuffer();

        try {
            InetAddress addr = InetAddress.getByName( "ajax.googleapis.com" );
            SocketAddress sockAddr = new InetSocketAddress( addr, 80 );

            Socket socket = new Socket();
            socket.connect( sockAddr, 30000 );

            PrintWriter out = new PrintWriter( socket.getOutputStream() );
            BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );

            String requestString = HTMLRequestString( text, from, to );

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
        catch( IOException e ) {
            e.printStackTrace();
        }

        return buf.toString();
    }
}
