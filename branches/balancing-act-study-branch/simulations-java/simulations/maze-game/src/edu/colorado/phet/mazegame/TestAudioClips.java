// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.mazegame;

/**
 * @author Sam Reid
 */
public class TestAudioClips {
    public static void main( String[] args ) throws InterruptedException {
        MazeGameResources.getAudioClip( "twiddle.WAV" ).play();
        Thread.sleep(10000);
    }
}
