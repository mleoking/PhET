// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp.common;

import java.net.URL;

import javax.sound.sampled.*;

/**
 * User: Sam Reid
 * Date: Dec 31, 2005
 * Time: 11:15:34 AM
 */

public class PhetAudioClip {
    private URL url;
    private Clip line;

    public PhetAudioClip( String resourceName ) {
        this( toURL( resourceName ) );
    }

    private static URL toURL( String resourceName ) {
        URL url = Thread.currentThread().getContextClassLoader().getResource( resourceName );
        if ( url == null ) {
            throw new RuntimeException( "Null URL for resource name: " + resourceName );
        }
        return url;
    }

    public PhetAudioClip( URL url ) throws PhetAudioException {
        this.url = url;
        AudioFileFormat f = null;
        try {
            f = AudioSystem.getAudioFileFormat( url );
            AudioFormat audioFormat = f.getFormat();

            DataLine.Info info = new DataLine.Info( Clip.class, audioFormat ); // format is an AudioFormat object
            // Obtain and open the line.
            line = (Clip) AudioSystem.getLine( info );
            line.open( AudioSystem.getAudioInputStream( url.openStream() ) );
        }
        catch ( Exception e ) {
            e.printStackTrace();
            throw new PhetAudioException( e, this );
        }
    }

    public void playAndWait() {
//        System.out.println( "Started playing: " + url );
        line.setFramePosition( 0 );//prepare for next run
        line.start();
//        System.out.println( "Finished playing: " + url );
    }

    public void play() {
        Thread thread = new Thread( new Runnable() {
            public void run() {
                playAndWait();
            }
        } );
        thread.start();
    }

    public URL getURL() {
        return url;
    }
}
