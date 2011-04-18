// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.interactiveisotope.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.*;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This is the primary model class for the Build an Isotope module.  This
 * class acts as the main interface for model actions, and contains the
 * constituent model elements.  It watches all neutrons and, based on where
 * they are placed by the user, moves them between the neutron bucket and the
 * atom.
 *
 * In this model, units are picometers (1E-12).
 */
public class InteractiveIsotopeModel implements Resettable, IConfigurableAtomModel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Constant that defines the default number of neutrons in the bucket.
    private static final int DEFAULT_NUM_NEUTRONS_IN_BUCKET = 4;

    // Constants that define the size, position, and appearance of the neutron bucket.
    private static final Dimension2D BUCKET_SIZE = new PDimension( 65, 30 );
    private static final Point2D NEUTRON_BUCKET_POSITION = new Point2D.Double( -120, -140 );

    // Distance at which nucleons are captured by the nucleus.
    protected static final double NUCLEUS_CAPTURE_DISTANCE = Atom.ELECTRON_SHELL_1_RADIUS;

    // Default atom configuration.
    private static final ImmutableAtom DEFAULT_ATOM_CONFIG = new ImmutableAtom( 1, 0, 1 ); // Hydrogen.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    private final Atom atom;

    // Arrays that contain the subatomic particles, whether they are in the
    // bucket or in the atom.  This is part of a basic assumption about how
    // the model works, which is that the model contains all the particles,
    // and the particles move back and forth from being in the bucket or in
    // in the atom.
    private final ArrayList<Neutron> neutrons = new ArrayList<Neutron>();
    private final ArrayList<Proton> protons = new ArrayList<Proton>();
    private final ArrayList<Electron> electrons = new ArrayList<Electron>();

    // The buckets that holds the neutrons that are not in the atom.
    private final ParticleBucket neutronBucket = new ParticleBucket( NEUTRON_BUCKET_POSITION,
            BUCKET_SIZE, Color.gray, BuildAnAtomStrings.NEUTRONS_NAME, Neutron.RADIUS );

    // Listener support
    private final ArrayList<Listener> listeners = new ArrayList<Listener>();

    // An event listener that watches for when the user releases a neutron and
    // decides whether it should go in the bucket or the atom's nucleus.
    private final SphericalParticle.Adapter neutronDropListener =  new SphericalParticle.Adapter() {
        @Override
        public void droppedByUser( SphericalParticle particle ) {
            assert particle instanceof Neutron; // Should always be a neutron.
            assert neutrons.contains( particle ); // Particle should always be contained by model.
            // The user just released this neutron.  If it is close
            // enough to the nucleus, send it there, otherwise
            // send it to its bucket.
            if ( particle.getPosition().distance( atom.getPosition() ) < NUCLEUS_CAPTURE_DISTANCE ) {
                atom.addNeutron( (Neutron) particle, false );
            }
            else {
                neutronBucket.addParticle( particle, false );
            }
        }
    };

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    /**
     * Construct the model with the atoms initially in the bucket.
     */
    public InteractiveIsotopeModel( BuildAnAtomClock clock ) {
        this.clock = clock;

        // Create the atom.
        atom = new Atom( new Point2D.Double( 0, 0 ), clock );
        atom.setPosition( 50, 50 );

        for ( int i = 0; i < DEFAULT_NUM_NEUTRONS_IN_BUCKET; i++ ) {
            final Neutron neutron = new Neutron( clock );
            neutrons.add( neutron );
            neutron.addListener( new SphericalParticle.Adapter() {
                @Override
                public void droppedByUser( SphericalParticle particle ) {
                    // The user just released this neutron.  If it is close
                    // enough to the nucleus, send it there, otherwise
                    // send it to its bucket.
                    if ( neutron.getPosition().distance( atom.getPosition() ) < NUCLEUS_CAPTURE_DISTANCE ) {
                        atom.addNeutron( neutron, false );
                    }
                    else {
                        neutronBucket.addParticle( neutron, false );
                    }
                }
            } );
        }
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    public void addListener( Listener listener ){
        listeners.add( listener );
    }

    public void removeListener( Listener listener ){
        listeners.remove( listener );
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public Atom getAtom() {
        return atom;
    }

    public void setAtomConfiguration( IAtom atomConfig ){
        if ( !atom.configurationEquals( atomConfig ) ){
            // Clear the atom.
            clearAtom();

            // Add the particles.
            for ( int i = 0; i < atomConfig.getNumElectrons(); i++ ){
                Electron electron = new Electron( clock );
                atom.addElectron( electron, true );
                electrons.add( electron );
                notifyParticleAdded( electron );
            }
            for ( int i = 0; i < atomConfig.getNumProtons(); i++ ){
                Proton proton = new Proton( clock );
                atom.addProton( proton, true );
                protons.add( proton );
                notifyParticleAdded( proton );
            }
            for ( int i = 0; i < atomConfig.getNumNeutrons(); i++ ){
                Neutron neutron = new Neutron( clock );
                neutron.addListener( neutronDropListener );
                atom.addNeutron( neutron, true );
                neutrons.add( neutron );
                notifyParticleAdded( neutron );
            }
        }

        // Whenever the atom configuration is set, the neutron bucket is
        // set to contain its default number of neturons.
        setNeutronBucketCount( DEFAULT_NUM_NEUTRONS_IN_BUCKET );
    }

    /**
     * Configure the neutron bucket to have the specified number of particles
     * in it.
     *
     * @param targetNumNeutrons
     */
    private void setNeutronBucketCount( int targetNumNeutrons ) {

        if ( targetNumNeutrons != neutronBucket.getParticleList().size() ) {
            clearBucket();

            // Add the target number of neutrons, sending notifications of
            // the additions.
            for ( int i = 0; i < targetNumNeutrons; i++ ) {
                Neutron newNeutron = new Neutron( clock );
                newNeutron.addListener( neutronDropListener );
                neutronBucket.addParticle( newNeutron, true );
                neutrons.add( newNeutron );
                notifyParticleAdded( newNeutron );
            }
        }
    }

    /**
     * Remove all particles that are currently contained in the atom from
     * both the atom and from the model.  Note that there may still be
     * particles left in the model after doing this, since they could be in
     * the bucket.
     */
    private void clearAtom(){
        // Remove all particles associated with the atom from the model.
        ArrayList<Proton> copyOfProtons = new ArrayList<Proton>( protons );
        for ( Proton proton : copyOfProtons ) {
            if ( atom.containsProton( proton )){
                atom.removeProton( proton );
                protons.remove( proton );
                proton.removedFromModel();
            }
        }
        ArrayList<Neutron> copyOfNeutrons = new ArrayList<Neutron>( neutrons );
        for ( Neutron neutron : copyOfNeutrons ) {
            if ( atom.containsNeutron( neutron )){
                atom.removeNeutron( neutron );
                neutrons.remove( neutron );
                neutron.removedFromModel();
            }
        }
        ArrayList<Electron> copyOfElectrons = new ArrayList<Electron>( electrons );
        for ( Electron electron : copyOfElectrons ) {
            if ( atom.containsElectron( electron )){
                atom.removeElectron( electron );
                electrons.remove( electron );
                electron.removedFromModel();
            }
        }

        // Now clear the atom itself.
        atom.reset();
    }

    /**
     * Remove all particles that are currently contained in the bucket from
     * both the bucket and from the model.  Note that this does NOT remove
     * the particles from the atom.
     */
    private void clearBucket(){
        // Remove neutrons from this model.
        ArrayList<Neutron> copyOfNeutrons = new ArrayList<Neutron>( neutrons );
        for ( Neutron neutron : copyOfNeutrons ) {
            if ( neutronBucket.containsParticle( neutron )){
                neutrons.remove( neutron );
                neutron.removedFromModel();
            }
        }
        // Remove neutrons from bucket.
        neutronBucket.reset();
    }

    /**
     * Reset the model.  The sets the atom and the neutron bucket into their
     * default initial states.
     */
    public void reset(){
        // Reset the atom.
        setAtomConfiguration( DEFAULT_ATOM_CONFIG );

        // Reset the neutron bucket.
        setNeutronBucketCount( DEFAULT_NUM_NEUTRONS_IN_BUCKET );
    }

    /**
     * @return
     */
    public ParticleBucket getNeutronBucket() {
        return neutronBucket;
    }

    private void notifyParticleAdded( SphericalParticle particle ){
        for ( Listener listener : listeners ){
            listener.particleAdded( particle );
        }
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public interface Listener {
        /**
         * Indicates that a particle was added to the model.  Note that
         * notifications about particle removal are sent by the individual
         * particles.
         *
         * @param subatomicParticle
         */
        void particleAdded( SphericalParticle subatomicParticle );
    }

    public static class Adapter implements Listener {
        public void particleAdded( SphericalParticle subatomicParticle ) { }
    }
}
