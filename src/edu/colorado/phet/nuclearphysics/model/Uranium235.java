/**
 * Class: Uranium235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.view.Cesium;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

public class Uranium235 extends Nucleus {
    private static Random random = new Random();
    // Regulates how fast the profile rises when fission occurs
    private static final int morphSpeedFactor = 20;

    private ArrayList decayListeners = new ArrayList();
    private AlphaParticle[] alphaParticles = new AlphaParticle[4];
    private int morphTargetNeutrons;
    private int morphTargetProtons;
    private Neutron fissionInstigatingNeutron;
    private BaseModel model;


    public Uranium235( Point2D.Double position, BaseModel model ) {
        super( position, 92, 143 );
        this.model = model;
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

    public void fission( Neutron neutron ) {
        morph( getNumNeutrons() - 200, getNumProtons() );
        fissionInstigatingNeutron = neutron;
        // Move the neutron way, way away so it doesn't show and doesn't
        // cause another fission event. It will be destroyed later.
        neutron.setLocation( 100E3, 100E3 );
        neutron.setVelocity( 0, 0 );
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

        // Check to see if we are being hit by a neutron
        for( int i = 0; i < model.numModelElements(); i++ ) {
            ModelElement me = model.modelElementAt( i );
            if( me instanceof Neutron ) {
                Neutron neutron = (Neutron)me;
                if( neutron.getLocation().distanceSq( this.getLocation() )
                    < this.getRadius() * this.getRadius() ) {
                    this.fission( neutron );
                }
            }
        }


        // Handle fission morphing
        if( morphTargetNeutrons != 0 ) {
            // The morphSpeedFactor regulates how fast the profile rises
            int incr = morphSpeedFactor * Math.abs( morphTargetNeutrons ) / morphTargetNeutrons;
            setNumNeutrons( getNumNeutrons() + incr );
            int temp = morphTargetNeutrons;
            morphTargetNeutrons -= incr;
            if( temp * morphTargetNeutrons <= 0 ) {
                super.fission( fissionInstigatingNeutron );
            }
        }

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

    public FissionProducts getFissionProducts( Neutron neutron ) {

        Nucleus daughter1 = new Rubidium( this.getLocation() );
        double theta = random.nextDouble() * Math.PI;
        double vx = Config.fissionDisplacementVelocity * Math.cos( theta );
        double vy = Config.fissionDisplacementVelocity * Math.sin( theta );
        daughter1.setVelocity( (float)( -vx ), (float)( -vy ) );

        Nucleus daughter2 = new Cesium( this.getLocation() );
        daughter2.setVelocity( (float)( vx ), (float)( vy ) );

        Neutron[] neutronProducts = new Neutron[3];
        for( int i = 0; i < 3; i++ ) {
            theta = random.nextDouble() * Math.PI;
            neutronProducts[i] = new Neutron( this.getLocation(), theta );
        }

        FissionProducts fp = new FissionProducts( this, neutron,
                                                  daughter1,
                                                  daughter2,
                                                  neutronProducts );
        return fp;
    }


    /**
     * Changes the composition of the nucleus. The changes are made
     * incrementally, one time step at a time
     *
     * @param numNeutrons
     * @param numProtons
     */
    public void morph( int numNeutrons, int numProtons ) {
        this.morphTargetNeutrons = numNeutrons - getNumNeutrons();
        this.morphTargetProtons = numProtons - getNumProtons();
    }
}
