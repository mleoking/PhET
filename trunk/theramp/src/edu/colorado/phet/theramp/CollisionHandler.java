/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.theramp.model.Block;
import edu.colorado.phet.theramp.model.Collision;
import edu.colorado.phet.theramp.view.JSAudioPlayer;

import java.net.URL;

/**
 * User: Sam Reid
 * Date: Aug 4, 2005
 * Time: 10:05:37 PM
 * Copyright (c) Aug 4, 2005 by Sam Reid
 */

public class CollisionHandler extends Block.Adapter {
    private RampModule rampModule;
    private URL url0;
    private URL url1;
    private URL url2;

    public CollisionHandler( RampModule rampModule ) {
        this.rampModule = rampModule;
        url0 = RampModule.class.getClassLoader().getResource( "audio/smash0.wav" );
        url1 = RampModule.class.getClassLoader().getResource( "audio/smash1.wav" );
        url2 = RampModule.class.getClassLoader().getResource( "audio/smash2.wav" );
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
