/**
 * Class: Uranium235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.nuclearphysics.Config;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Uranium235 extends Nucleus {

    private ArrayList decayListeners = new ArrayList();
    private AlphaParticle[] alphaParticles = new AlphaParticle[4];

    public Uranium235( Point2D.Double position ) {
        super( position, 145, 92 /*, potentialProfile */ );
        for( int i = 0; i < alphaParticles.length; i++ ) {
            alphaParticles[i] = new AlphaParticle( position,
                                                   getPotentialProfile().getAlphaDecayX() * Config.AlphaLocationUncertaintySigmaFactor );
        }
    }

    public AlphaParticle[] getAlphaParticles() {
        return alphaParticles;
    }

    public void addDecayListener( DecayListener listener ) {
        this.decayListeners.add( listener );
    }

    public void stepInTime( double dt ) {
        for( int j = 0; j < alphaParticles.length; j++ ) {
            AlphaParticle alphaParticle = alphaParticles[j];
            if( alphaParticle.getLocation().distanceSq( this.getLocation() ) + alphaParticle.getRadius()
                > getPotentialProfile().getAlphaDecayX() * getPotentialProfile().getAlphaDecayX() ) {
//                > potentialProfile.getAlphaDecayX() * potentialProfile.getAlphaDecayX() ) {
//                try {
//                    Thread.sleep( 1000 );
//                }
//                catch( InterruptedException e ) {
//                    e.printStackTrace();
//                }
                // Note: the production of decay products in this way is probably no longer the best
                // way to do things, now that there are alpha particles jumping around and causing the
                // decay in the first place. But this hacked up system still seems to work, so I'm
                // sticking with it for now.
                DecayProducts decayProducts = alphaDecay( alphaParticle );
                for( int i = 0; i < decayListeners.size(); i++ ) {
                    DecayListener decayListener = (DecayListener)decayListeners.get( i );
                    decayListener.alphaDecay( decayProducts );
                }
                return;
            }
        }
        super.stepInTime( dt );
    }

    public DecayProducts alphaDecay( AlphaParticle alphaParticle ) {
        Vector2D sep = new Vector2D( (float)( alphaParticle.getLocation().getX() - this.getLocation().getX() ),
                                     (float)( alphaParticle.getLocation().getY() - this.getLocation().getY() ) );
        sep.normalize();
        // Change the composition of the nucleus. Do this so that the profile will change
        // the way we'd like
        this.setNumProtons( this.getNumProtons() - 2 );
        this.setNumNeutrons( this.getNumNeutrons() + 200 );

        // n2 is the alpha particle
        Nucleus n2 = new DecayNucleus( alphaParticle, sep, this.getPotentialProfile().getWellPotential() );
        DecayProducts products = new DecayProducts( this, this, n2 );
//        DecayProducts products = new DecayProducts( this, n1, n2 );
        return products;
    }
}
