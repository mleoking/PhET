/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.movingman.common.JSAudioPlayer;

import java.net.URL;

/**
 * User: Sam Reid
 * Date: Apr 12, 2005
 * Time: 10:41:02 PM
 * Copyright (c) Apr 12, 2005 by Sam Reid
 */

public class CollisionAudioEffects extends Man.Adapter {


    String LEFT = "left";
    String RIGHT = "right";
    String FREE = "free";
    String lastCollisionLocation = FREE;
    private Man man;

    public CollisionAudioEffects( Man man ) {
        this.man = man;
    }

    public void positionChanged( double x ) {

        if( !man.isMaximum() && !man.isMinimum() ) {
            lastCollisionLocation = FREE;
        }
    }

    public void collided( Man man ) {
        if( lastCollisionLocation.equals( FREE ) ) {
            URL crash = CollisionAudioEffects.class.getClassLoader().getResource( "audio/smash0.wav" );
            JSAudioPlayer.playNoBlock( crash );
            lastCollisionLocation = man.isMaximum() ? RIGHT : LEFT;
        }

    }
}
