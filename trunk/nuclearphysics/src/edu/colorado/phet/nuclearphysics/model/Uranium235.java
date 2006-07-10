/**
 * Class: Uranium235
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.view.Cesium;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Uranium235
 * <p>
 * These are special nuclei that can fission and decay. The stepInTime() method is
 * pretty complicated, and could benefit from refactoring it into a state machine, or pluggable
 * behaviors (perhaps a stepInTimeStrategy).
 * <p>
 * A Uranium235 notifies certain listeners when it is about to decay on its next time step,
 * and then notifies other listeners in that next time step.
 */
public class Uranium235 extends ProfileableNucleus {

    //----------------------------------------------------------------
    // Class data and methods
    //----------------------------------------------------------------
    public final static int NUM_PROTONS = 92;
    public final static int NUM_NEUTRONS = 143;

    private static Random random = new Random();
    // The likelihood that a neutron striking a U235 nucleus will be absorbed, causing fission
    private static double ABSORPTION_PROBABILITY = 1;
    // Location to put neutrons until they can be removed from the model
    public static final Point2D HoldingAreaCoord = new Point2D.Double( Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY );


    public static void setAbsoptionProbability( double probability ) {
        ABSORPTION_PROBABILITY = probability;
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private ArrayList decayListeners = new ArrayList();
    // List of DecayListeners that want to be alerted the time step before general DecayListeners are notified. This
    // is provided so that an agent can pause the clock so users can single-step through the decay process
    private ArrayList preDecayListeners = new ArrayList();
    // Flag to tell if we have taken the pre-decay time step
    private boolean preDecayStep = false;

    private AlphaParticle[] alphaParticles = new AlphaParticle[4];
    private int morphTargetNeutrons;
    private int morphTargetProtons;
    private Neutron fissionInstigatingNeutron;
    private NuclearPhysicsModel model;
    private boolean doMorph = false;
    private double jiggleOrgX;

    public Uranium235( Point2D position, NuclearPhysicsModel model ) {
        super( position, NUM_PROTONS, NUM_NEUTRONS );
        this.model = model;
//        for( int i = 0; i < alphaParticles.length; i++ ) {
//            alphaParticles[i] = new AlphaParticle( position,
//                                                   getEnergyProfile().getAlphaDecayX() * Config.AlphaLocationUncertaintySigmaFactor );
//            alphaParticles[i].setNucleus( this );
//        }
    }

    public double getMinPotentialEnergy() {
        return 0;
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
        if( random.nextDouble() <= ABSORPTION_PROBABILITY ) {
            morph( getNumNeutrons() - 100, getNumProtons() );
            fissionInstigatingNeutron = neutron;
            // Move the neutron way, way away so it doesn't show and doesn't
            // cause another fission event. It will be destroyed later. Do it
            // twice so the neutron's previous position gets set to the same thing
            neutron.setPosition( HoldingAreaCoord.getX(), HoldingAreaCoord.getY() );
            neutron.setPosition( HoldingAreaCoord.getX(), HoldingAreaCoord.getY() );
            neutron.setVelocity( 0, 0 );

            neutron.leaveSystem();
            model.removeModelElement( neutron );

            // Make note of the x coordinate of the nucleus, so we can keep the jiggling
            // centered
            jiggleOrgX = this.getPosition().getX();
        }
    }

    /**
     * Detects if alpha particles have escaped.
     *
     * @param dt
     */
    public void stepInTime( double dt ) {

        super.stepInTime( dt );

        // Handle fission morphing
        if( morphTargetNeutrons > 0 ) {
            setPotential( getPotential() + Config.U235MorphSpeedFactor );
            if( getPotential() > getPotentialProfile().getMaxEnergy()
                || !doMorph ) {
                // Before we morph, make sure the parent nucleus is centered. That is, don't
                // leave it where itr jittered to.
                this.setPosition( jiggleOrgX, this.getPosition().getY() );
                doNecking();
                super.fission( fissionInstigatingNeutron );
            }

            // The code here morphs the profile
            // The U235MorphSpeedFactor regulates how fast the profile rises
            int incr = Config.U235MorphSpeedFactor * Math.abs( morphTargetNeutrons ) / morphTargetNeutrons;
            morphTargetNeutrons -= incr;

            // Jiggle the nucleus
            double d = 1.8;
            double dx = random.nextGaussian() * d * ( random.nextBoolean() ? 1 : -1 );
            double dy = random.nextGaussian() * d * ( random.nextBoolean() ? 1 : -1 );
            this.setPosition( jiggleOrgX + dx, getPosition().getY() + dy );
            //            this.setPosition( getPosition().getX() + dx, getPosition().getY() + dy );
        }
    }

    public FissionProducts getFissionProducts( Neutron neutron ) {

        Nucleus daughter1 = new Rubidium( this.getPosition() );
        double theta = random.nextDouble() * Math.PI;
        double vx = Config.fissionDisplacementVelocity * Math.cos( theta );
        double vy = Config.fissionDisplacementVelocity * Math.sin( theta );
        daughter1.setVelocity( (float)( -vx ), (float)( -vy ) );
        daughter1.setPotential( this.getPotential() );

        Nucleus daughter2 = new Cesium( this.getPosition() );
        daughter2.setVelocity( (float)( vx ), (float)( vy ) );
        daughter2.setPotential( this.getPotential() );

        Neutron[] neutronProducts = new Neutron[3];
        for( int i = 0; i < 3; i++ ) {
            theta = random.nextDouble() * Math.PI * 2;
            neutronProducts[i] = new Neutron( this.getPosition(), theta );
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
            this.morphTargetNeutrons = getNumNeutrons() - numNeutrons;
            this.morphTargetProtons = getNumProtons() - numProtons;
        }
        else {
            this.morphTargetNeutrons = 1;
            this.morphTargetProtons = 1;
        }
    }

    /**
     * Do something to make the morphing nucleus neck down before it finally spits
     */
    private void doNecking() {
        // ?????
    }
}
