/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d;

import edu.colorado.phet.forces1d.common.JSAudioPlayer;
import edu.colorado.phet.forces1d.model.Force1DModel;

import java.net.URL;

/**
 * User: Sam Reid
 * Date: Mar 6, 2005
 * Time: 3:30:56 PM
 * Copyright (c) Mar 6, 2005 by Sam Reid
 */

public class CrashAudioPlayer implements Force1DModel.CollisionListener {
    private URL url0;
    private URL url1;
    private URL url2;

    public CrashAudioPlayer() {
        url0 = Force1DModule.class.getClassLoader().getResource( "audio/smash0.wav" );
        url1 = Force1DModule.class.getClassLoader().getResource( "audio/smash1.wav" );
        url2 = Force1DModule.class.getClassLoader().getResource( "audio/smash2.wav" );
    }

    public void collisionOccurred( Force1DModel.CollisionEvent ce ) {
        //                System.out.println( "ce = " + ce );
        double mom = ce.getMomentum();
//                URL url1 = Force1DModule.class.getResource( "audio/smash1.wav" );

//                System.out.println( "url2 = " + url2 );
//                System.out.println( "url1 = " + url1 );
//                System.out.println( "mom = " + mom );
        if( mom < 50 ) {
            //no audio for soft touches.
        }
        else if( mom < 2000 ) {
            JSAudioPlayer.playNoBlock( url0 );
        }
        else if( mom < 4000 ) {
            JSAudioPlayer.playNoBlock( url1 );
        }
        else {
            JSAudioPlayer.playNoBlock( url2 );
        }
    }
}
