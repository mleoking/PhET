package edu.colorado.phet.website.tests;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

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

    private static class Hits {
        private Hit hit;
        private int count = 1;

        private Hits( Hit hit ) {
            this.hit = hit;
        }

        public Hit getHit() {
            return hit;
        }

        public int getCount() {
            return count;
        }

        public void increment() {
            count++;
        }
    }

    private static class Request {
        private final String addr;
        private final int status;

        private Request( String addr, int status ) {
            this.addr = addr;
            this.status = ( status == 304 || status == 200 ) ? 1 : status;
        }

        public String getAddr() {
            return addr;
        }

        public int getStatus() {
            return status;
        }

        @Override
        public int hashCode() {
            return new Integer( status ).hashCode() + addr.hashCode();
        }

        @Override
        public boolean equals( Object o ) {
            return ( o instanceof Request && ( (Request) o ).getAddr().equals( getAddr() ) && ( (Request) o ).getStatus() == getStatus() );
        }

        @Override
        public String toString() {
            if ( status == 1 ) {
                return "OK";
            }
            else {
                return String.valueOf( status );
            }
        }
    }

    private static class Entry implements Comparable {
        private Request request;
        private Hits hits;

        private Entry( Request request, Hits hits ) {
            this.request = request;
            this.hits = hits;
        }

        public Request getRequest() {
            return request;
        }

        public Hits getHits() {
            return hits;
        }

        public int compareTo( Object o ) {
            if ( o instanceof Entry ) {
                return -( new Integer( hits.getCount() ).compareTo( ( (Entry) o ).getHits().getCount() ) );
            }
            else {
                return -1;
            }
        }
    }

    public static Map<Request, Hits> runProcessedLog( File logFile ) {
        Map<Request, Hits> map = new HashMap<Request, Hits>();
        try {
            BufferedReader in = new BufferedReader( new FileReader( logFile ) );

            String line;
            while ( ( line = in.readLine() ) != null ) {
                StringTokenizer tokenizer = new StringTokenizer( line, " " );
                if ( tokenizer.countTokens() < 4 ) {
                    //System.out.println( "Untokenizable line: " + line );
                    continue;
                }
                String method = tokenizer.nextToken();
                if ( !method.equals( "GET" ) ) {
                    //System.out.println( "Skipping method: " + method );
                    continue;
                }
                String addr = tokenizer.nextToken();
                int status = Integer.valueOf( tokenizer.nextToken() );
                String redir = tokenizer.nextToken();

                Request request = new Request( addr, status );
                if ( map.containsKey( request ) ) {
                    map.get( request ).increment();
                }
                else {
                    Hit hit = getHit( addr );
                    map.put( request, new Hits( hit ) );
                    System.err.println( addr + " (" + request.toString() + ":" + ( hit.isOk() ? "OK" : "ERR" ) + ") " + hit );
                }


            }
        }
        catch( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return map;
    }

    public static void main( String[] args ) throws FileNotFoundException {
        Map<Request, Hits> map = runProcessedLog( new File( "/home/jon/tmp/filtered_access_log" ) );

        System.err.println( "\n\n\n\n" );

        int cOK = 0;
        int c404 = 0;
        int cERR = 0;

        List<Entry> ok = new LinkedList<Entry>();
        List<Entry> notfound = new LinkedList<Entry>();
        List<Entry> err = new LinkedList<Entry>();

        for ( Request request : map.keySet() ) {
            Hits hits = map.get( request );

            if ( hits.getHit().isOk() ) {
                cOK += hits.getCount();
                ok.add( new Entry( request, hits ) );
            }
            else if ( hits.getHit().is404() ) {
                c404 += hits.getCount();
                notfound.add( new Entry( request, hits ) );
            }
            else {
                cERR += hits.getCount();
                err.add( new Entry( request, hits ) );
            }
        }

        System.err.flush();


        Collections.sort( ok );
        Collections.sort( notfound );
        Collections.sort( err );

        PrintStream out = new PrintStream( new FileOutputStream( new File( "/home/jon/tmp/output" ) ) );

        out.println( "summary: " + cOK + " OK, " + c404 + " not found, " + cERR + " others" );

        printReport( ok, out );
        printReport( notfound, out );
        printReport( err, out );

        out.close();

    }

    private static void printReport( List<Entry> entries, PrintStream out ) {
        for ( Entry entry : entries ) {
            // TODO: map into toString()s?
            out.println( entry.getHits().getCount() + " " + entry.getRequest().getAddr() + " (" + entry.getRequest().toString() + ":" + ( entry.getHits().getHit().isOk() ? "OK" : "ERR" ) + ") " + entry.getHits().getHit() );
        }
        out.println();
    }
}
