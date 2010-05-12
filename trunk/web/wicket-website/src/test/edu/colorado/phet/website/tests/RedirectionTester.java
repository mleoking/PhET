package edu.colorado.phet.website.tests;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class RedirectionTester {

    public static Hit getHit( String addr ) {
        if ( addr.startsWith( "/" ) ) {
            addr = "http://phetsims.colorado.edu" + addr;
        }
        HttpURLConnection conn = null;
        int code = 0;
        String location = null;
        try {
            URL url = new URL( addr );
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod( "GET" );
            conn.setReadTimeout( 10000 );
            conn.setInstanceFollowRedirects( false );
            conn.connect();

            location = conn.getHeaderField( "Location" );
            code = conn.getResponseCode();
        }
        catch( MalformedURLException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        finally {
            if ( conn != null ) {
                conn.disconnect();
            }
        }

        if ( code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_NOT_MODIFIED ) {
            return new Hit( code );
        }
        else if ( code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP ) {
            return new Hit( code, location, getHit( location ) );
        }
        else {
            return new Hit( code );
        }
    }

    private static class Hit {
        private final int code;
        private final String location;
        private final Hit child;

        private Hit( int code ) {
            this.code = code;
            this.location = null;
            this.child = null;
        }

        private Hit( int code, String location ) {
            this.code = code;
            this.location = location;
            this.child = null;
        }

        private Hit( int code, String location, Hit child ) {
            this.code = code;
            this.location = location;
            this.child = child;
        }

        public int getCode() {
            return code;
        }

        public String getLocation() {
            return location;
        }

        public Hit getChild() {
            return child;
        }

        public boolean is404() {
            if ( code == 404 ) {
                return true;
            }
            else if ( child == null ) {
                return false;
            }
            else {
                return child.is404();
            }
        }

        public boolean isOk() {
            if ( code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_NOT_MODIFIED ) {
                return true;
            }
            else if ( child == null ) {
                return false;
            }
            else {
                return child.isOk();
            }
        }

        @Override
        public String toString() {
            String ret = code + " ";
            if ( code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP ) {
                ret += "[" + location + "] ";
            }
            if ( child != null ) {
                ret += child.toString();
            }
            return ret;
        }
    }

    public static void runProcessedLog( File logFile ) {
        try {
            BufferedReader in = new BufferedReader( new FileReader( logFile ) );

            String line;
            while ( ( line = in.readLine() ) != null ) {
                StringTokenizer tokenizer = new StringTokenizer( line, " " );
                if ( tokenizer.countTokens() < 4 ) {
                    System.out.println( "Untokenizable line: " + line );
                    continue;
                }
                String method = tokenizer.nextToken();
                if ( !method.equals( "GET" ) ) {
                    System.out.println( "Skipping method: " + method );
                    continue;
                }
                String addr = tokenizer.nextToken();
                int status = Integer.valueOf( tokenizer.nextToken() );
                String redir = tokenizer.nextToken();
                Hit hit = getHit( addr );
                System.out.println( addr + " (" + status + ":" + ( hit.isOk() ? "OK" : "ERR" ) + ") " + hit );
            }
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static void main( String[] args ) {
        //System.out.println( getHit( "/index.php" ) );
        runProcessedLog( new File( "/home/jon/tmp/filtered_log" ) );
    }
}
