// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildtools.html5;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * For web development: Scan a filesystem and automatically send a web socket
 * message that can be used to trigger a reload of the page.
 *
 * @author Sam Reid
 */
public class Watcher {
    static long lastModified = 0;
    private static WebSocket open;

    public static void main( String[] args ) throws UnknownHostException {
        final WebSocketServer server = new WebSocketServer( new InetSocketAddress( 8887 ) ) {
            @Override public void onOpen( final WebSocket webSocket, final ClientHandshake clientHandshake ) {
                System.out.println( "Watcher.onOpen" );
                open = webSocket;
            }

            @Override public void onClose( final WebSocket webSocket, final int i, final String s, final boolean b ) {
                System.out.println( "Watcher.onClose" );
            }

            @Override public void onMessage( final WebSocket webSocket, final String s ) {
                System.out.println( "Watcher.onMessage" );
            }

            @Override public void onError( final WebSocket webSocket, final Exception e ) {
                System.out.println( "Watcher.onError" );
                e.printStackTrace();
            }
        };
        server.start();
//        WebSocket socket = new WebSocketImpl( server, new Draft_75(), new Socket() );
//        socket.start();

        final File root = new File( args[0] );
        System.out.println( "root = " + root );

        new Thread( new Runnable() {
            public void run() {
                long count = 0;
                while ( true ) {
                    long modifiedTime = lastModified( root );
                    if ( modifiedTime > lastModified ) {
                        lastModified = modifiedTime;
                        try {
                            runTask( root );
                        }
                        catch( Exception e1 ) {
                            e1.printStackTrace();
                            lastModified = 0;
                        }
                        System.out.println();
                        System.out.println( "Finished task" );
                    }
                    else {
                        if ( count % 30 == 0 ) {
                            System.out.print( "." );
                        }
                    }
                    count++;
                    try {
                        Thread.sleep( 100 );
                    }
                    catch( InterruptedException e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    private static void runTask( final File sourceRoot ) throws IOException {
        if ( open != null ) {
            open.send( "refresh" );
            System.out.println( "Sent refresh command." );

            //If there is a version file, update it.  The version file helps us be reassured that the browser is running the latest version instead of running a previous cached version.
            File f = new File( "C:\\workingcopy\\phet\\svn-1.7\\trunk\\simulations-html\\simulations\\energy-skate-park-easeljs-jquerymobile\\src\\app\\util\\version.js" );
            if ( f.exists() ) {
                String file = FileUtils.loadFileAsString( f );
                String tail = file.substring( file.lastIndexOf( ':' ) + 1 ).trim();
                StringTokenizer st = new StringTokenizer( tail, "}" );
                int value = Integer.parseInt( st.nextToken() );
                String s = "define( [ ], function () { return {version: " + ( value + 1 ) + "} } );";
                FileUtils.writeString( f, s );
                System.out.println( s );
            }
        }
    }

    private static long lastModified( final File root ) {
        List<File> files = getAllFiles( root );
        List<Long> modifiedTimes = files.filter( new F<File, Boolean>() {
            @Override public Boolean f( final File file ) {
                return !file.getName().equals( "version.js" );
            }
        } ).map( new F<File, Long>() {
            @Override public Long f( final File file ) {
                return file.lastModified();
            }
        } );
        return modifiedTimes.maximum( Ord.longOrd );
    }

    private static List<File> getAllFiles( final File root ) {
        ArrayList<File> f = new ArrayList<File>();
        if ( root.isDirectory() ) {
            FilenameFilter skipNLS = new FilenameFilter() {
                public boolean accept( File dir, String name ) {
                    return !name.equals( "nls" ) && !name.equals( ".git" ) && !name.equals( "common" );
                }
            };
            for ( File child : root.listFiles( skipNLS ) ) {
                f.addAll( getAllFiles( child ).toCollection() );
            }
        }
        else {
            f.add( root );
        }
        return List.iterableList( f );
    }
}