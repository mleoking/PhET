// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.tests.utils;

import edu.colorado.phet.common.phetcommon.audio.PhetAudioClip;

public class TestPhetAudioClip {
    public static void main( String[] args ) throws Exception {
        // Note that this test cannot be run without the data from movingman!
        PhetAudioClip clip = new PhetAudioClip( "audio/smash0.wav" );

        clip.play();

        System.out.println( "Test" );

        Thread.sleep( 200 );

        // Required because JavaSound uses a non-daemon thread
        System.exit( 0 );
    }
}
