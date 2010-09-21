package edu.colorado.phet.website.tests.redirections;

import java.io.*;
import java.net.*;
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
            conn.setReadTimeout( 30000 );
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

    public static Map<Request, Hits> runProcessedLog( File logFile ) throws FileNotFoundException {
        Map<Request, Hits> map = new HashMap<Request, Hits>();
        StringDispenser dispenser = new StringDispenser( logFile );

        List<RedirectionThread> threads = new LinkedList<RedirectionThread>();
        for ( int i = 0; i < 50; i++ ) {
            threads.add( new RedirectionThread( map, dispenser ) );
        }

        for ( RedirectionThread thread : threads ) {
            thread.start();
        }

        while ( dispenser.getCounter() != 0 ) {
            try {
                Thread.sleep( 1000 );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }

        for ( RedirectionThread thread : threads ) {
            if ( !thread.isComplete() ) {
                throw new RuntimeException( "Weird thread error!" );
            }
        }

        return map;
    }

    public static void main( String[] args ) throws FileNotFoundException {
        Map<Request, Hits> map = runProcessedLog( new File( "/home/jon/tmp/filtered_access_log" ) );

        System.err.println( "\n\n\n\n" );

        int cOK = 0;
        int c404 = 0;
        int c404OK = 0;
        int cERR = 0;

        List<Entry> ok = new LinkedList<Entry>();
        List<Entry> notfound = new LinkedList<Entry>();
        List<Entry> notfoundok = new LinkedList<Entry>();
        List<Entry> err = new LinkedList<Entry>();

        for ( Request request : map.keySet() ) {
            Hits hits = map.get( request );

            if ( hits.getHit().isOk() ) {
                cOK += hits.getCount();
                ok.add( new Entry( request, hits ) );
            }
            else if ( hits.getHit().is404() ) {
                if ( request.getStatus() != 404 ) {
                    c404 += hits.getCount();
                    notfound.add( new Entry( request, hits ) );
                }
                else {
                    c404OK += hits.getCount();
                    notfoundok.add( new Entry( request, hits ) );
                }
            }
            else {
                cERR += hits.getCount();
                err.add( new Entry( request, hits ) );
            }
        }

        System.err.flush();


        Collections.sort( ok );
        Collections.sort( notfound );
        Collections.sort( notfoundok );
        Collections.sort( err );

        PrintStream out = new PrintStream( new FileOutputStream( new File( "/home/jon/tmp/output" ) ) );

        out.println( "summary: " + cOK + " OK, " + c404 + " new not found, " + c404OK + " not found in any, " + cERR + " others" );

        printReport( ok, out );
        printReport( notfound, out );
        printReport( notfoundok, out );
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
