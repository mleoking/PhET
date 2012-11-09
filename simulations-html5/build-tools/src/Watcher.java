// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildtools.html5;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

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
                        catch ( Exception e1 ) {
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
                    catch ( InterruptedException e ) {
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
        }
    }

    private static long lastModified( final File root ) {
        List<File> files = getAllFiles( root );
        List<Long> modifiedTimes = files.map( new F<File, Long>() {
            @Override public Long f( final File file ) {
                return file.lastModified();
            }
        } );
        return modifiedTimes.maximum( Ord.longOrd );
    }

    private static List<File> getAllFiles( final File root ) {
        ArrayList<File> f = new ArrayList<File>();
        if ( root.isDirectory() ) {
            for ( File child : root.listFiles() ) {
                f.addAll( getAllFiles( child ).toCollection() );
            }
        }
        else {
            f.add( root );
        }
        return List.iterableList( f );
    }
}