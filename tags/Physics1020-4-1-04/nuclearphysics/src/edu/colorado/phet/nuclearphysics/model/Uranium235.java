/**
 * Class: Uranium235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.MathUtil;
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
    private static final int morphSpeedFactor = 5;

    private ArrayList decayListeners = new ArrayList();
    private AlphaParticle[] alphaParticles = new AlphaParticle[4];
    private int morphTargetNeutrons;
    private int morphTargetProtons;
    private Neutron fissionInstigatingNeutron;
    private BaseModel model;
    private boolean doMorph = false;

    public Uranium235( Point2D.Double position, BaseModel model ) {
        super( position, 92, 143 );
        this.model = model;
        for( int i = 0; i < alphaParticles.length; i++ ) {
            alphaParticles[i] = new AlphaParticle( position,
                                                   getPotentialProfile().getAlphaDecayX() * Config.AlphaLocationUncertaintySigmaFactor );
            alphaParticles[i].setNucleus( this );
        }
    }

    /**
     * Set the field that determines whether, in fission, the nucleus will morph through a stage
     * in which the well in it's potential energy profile rises, disappears and goes positive
     * before the nucleus splits.
     *
     * @param doMorph
     */
    public void setDoMorph( boolean doMorph ) {
        this.doMorph = doMorph;
    }

    public AlphaParticle[] getAlphaParticles() {
        return alphaParticles;
    }

    public void addDecayListener( DecayListener listener ) {
        this.decayListeners.add( listener );
    }

    public void fission( Neutron neutron ) {
        morph( getNumNeutrons() - 100, getNumProtons() );
        fissionInstigatingNeutron = neutron;
        // Move the neutron way, way away so it doesn't show and doesn't
        // cause another fission event. It will be destroyed later.
        neutron.setLocation( 100E3, 100E3 );
        neutron.setVelocity( 0, 0 );
    }

    public void stepInTime( double dt ) {

        AlphaParticle ap = null;

        // See if any of the alpha particles has escaped, and initiate alpha decay if it has
        for( int j = 0; j < alphaParticles.length; j++ ) {
            AlphaParticle alphaParticle = alphaParticles[j];
            if( alphaParticle.getLocation().distanceSq( this.getLocation() ) - alphaParticle.getRadius()
                > getPotentialProfile().getAlphaDecayX() * getPotentialProfile().getAlphaDecayX() ) {

                // set the alpha particle directly on the profile
                ap = alphaParticle;
                double d = alphaParticle.getLocation().distance( this.getLocation() );
                double dx = alphaParticle.getLocation().getX() - this.getLocation().getX();
                double dy = alphaParticle.getLocation().getY() - this.getLocation().getY();
                dx *= this.getPotentialProfile().getAlphaDecayX() / d * MathUtil.getSign( dx );
                dy *= this.getPotentialProfile().getAlphaDecayX() / d * MathUtil.getSign( dy );
                alphaParticle.setPotential( getPotentialProfile().getHillY( getPotentialProfile().getAlphaDecayX() ) );
                alphaParticle.setLocation( this.getLocation().getX() + dx, this.getLocation().getY() + dy );

//                try {
//                    Thread.sleep( 1000 );
//                }
//                catch( InterruptedException e ) {
//                    e.printStackTrace();
//                }

                AlphaDecayProducts decayProducts = new AlphaDecayProducts( this, alphaParticle );
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
            setPotential( getPotential() + morphSpeedFactor );
            if( getPotential() > getPotentialProfile().getMaxPotential()
                || !doMorph ) {
                super.fission( fissionInstigatingNeutron );
            }

            // The code here morphs the profile
            // The morphSpeedFactor regulates how fast the profile rises
            int incr = morphSpeedFactor * Math.abs( morphTargetNeutrons ) / morphTargetNeutrons;
//            setNumNeutrons( getNumNeutrons() + incr );
            int temp = morphTargetNeutrons;
            morphTargetNeutrons -= incr;

            // Jiggle the nucleus
//            double d = 0.5;
//            double dx = random.nextGaussian() * d * ( random.nextBoolean() ? 1 : -1 );
//            double dy = random.nextGaussian() * d * ( random.nextBoolean() ? 1 : -1 );
//            this.setLocation( getLocation().getX() + dx, getLocation().getY() + dy );
//            if( temp * morphTargetNeutrons <= 0 ) {
//                super.fission( fissionInstigatingNeutron );
//            }
        }
    }

    public FissionProducts getFissionProducts( Neutron neutron ) {

        Nucleus daughter1 = new Rubidium( this.getLocation() );
        double theta = random.nextDouble() * Math.PI;
        double vx = Config.fissionDisplacementVelocity * Math.cos( theta );
        double vy = Config.fissionDisplacementVelocity * Math.sin( theta );
        daughter1.setVelocity( (float)( -vx ), (float)( -vy ) );
        daughter1.setPotential( this.getPotential() );

        Nucleus daughter2 = new Cesium( this.getLocation() );
        daughter2.setVelocity( (float)( vx ), (float)( vy ) );
        daughter2.setPotential( this.getPotential() );

        Neutron[] neutronProducts = new Neutron[3];
        for( int i = 0; i < 3; i++ ) {
            theta = random.nextDouble() * Math.PI * 2;
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
     * incrementally, one time step at a time.
     *
     * @param numNeutrons
     * @param numProtons
     */
    private void morph( int numNeutrons, int numProtons ) {
        if( doMorph ) {
            this.morphTargetNeutrons = numNeutrons - getNumNeutrons();
            this.morphTargetProtons = numProtons - getNumProtons();
        }
        else {
            this.morphTargetNeutrons = 1;
            this.morphTargetProtons = 1;
        }
    }
}
