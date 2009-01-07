/*  */
package edu.colorado.phet.movingman.model;

import edu.colorado.phet.movingman.MovingManModule;
import edu.colorado.phet.movingman.common.JSAudioPlayer;

/**
 * User: Sam Reid
 * Date: Apr 12, 2005
 * Time: 10:41:02 PM
 */

public class CollisionAudioEffects extends Man.Adapter {


    String LEFT = "left";
    String RIGHT = "right";
    String FREE = "free";
    String lastCollisionLocation = FREE;
    private MovingManModule module;
    private Man man;
    private boolean soundEnabled = true;

    public CollisionAudioEffects( MovingManModule module, Man man ) {
        this.module = module;
        this.man = man;
        module.addListener( new MovingManModule.Listener() {
            public void reset() {
            }

            public void soundOptionChanged( boolean soundEnabled ) {
                CollisionAudioEffects.this.soundEnabled = soundEnabled;
            }
        } );
        soundEnabled = module.isSoundEnabled();
    }

    public void positionChanged( double x ) {

        if ( !man.isMaximum() && !man.isMinimum() ) {
            lastCollisionLocation = FREE;
        }
    }

    public void collided( Man man ) {
        if ( lastCollisionLocation.equals( FREE ) ) {
            if ( soundEnabled ) {
                JSAudioPlayer.playNoBlock( Thread.currentThread().getContextClassLoader().getResource( "moving-man/audio/smash0.wav" ) );
                lastCollisionLocation = man.isMaximum() ? RIGHT : LEFT;
            }
        }

    }
}
