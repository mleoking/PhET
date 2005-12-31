/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.theramp.common.PhETAudioClip;
import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.Collision;

/**
 * User: Sam Reid
 * Date: Aug 4, 2005
 * Time: 10:05:37 PM
 * Copyright (c) Aug 4, 2005 by Sam Reid
 */

public class CollisionHandler extends Block.Adapter {
    private RampModule rampModule;
//    private URL url0;
//    private URL url1;
//    private URL url2;
    private PhETAudioClip smash0;
    private PhETAudioClip smash1;
    private PhETAudioClip smash2;

    public CollisionHandler( RampModule rampModule ) {
        this.rampModule = rampModule;
        smash0 = new PhETAudioClip( "audio/smash0.wav" );
        smash1 = new PhETAudioClip( "audio/smash1.wav" );
        smash2 = new PhETAudioClip( "audio/smash2.wav" );
//        url0 = RampModule.class.getClassLoader().getResource( "audio/smash0.wav" );
//        url1 = RampModule.class.getClassLoader().getResource( "audio/smash1.wav" );
//        url2 = RampModule.class.getClassLoader().getResource( "audio/smash2.wav" );
    }

    public void collisionOccurred( Collision collision ) {
        handleAudio( collision );
        double dp = collision.getMomentumChange();
        rampModule.getRampPhysicalModel().setWallForce( dp / collision.getDt() );
    }

    private void handleAudio( Collision collision ) {
        double mom = collision.getAbsoluteMomentumChange();
        if( mom < 50 ) {
            //no audio for soft touches.
        }
        else if( mom < 2000 ) {
            smash0.play();
//            AudioSourceDataLinePlayer.playNoBlock( url0 );
        }
        else if( mom < 4000 ) {
            smash1.play();
//            AudioSourceDataLinePlayer.playNoBlock( url1 );
        }
        else {
            smash2.play();
//            AudioSourceDataLinePlayer.playNoBlock( url2 );
        }
    }
}
