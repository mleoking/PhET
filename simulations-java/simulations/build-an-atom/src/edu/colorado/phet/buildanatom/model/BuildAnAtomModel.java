/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.buildanatom.module.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Main model class.  Units are picometers (1E-12).
 */
public class BuildAnAtomModel {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Rectangle2D MODEL_VIEWPORT =
            new Rectangle2D.Double( -200, -150,
            400,
            400 * BuildAnAtomDefaults.STAGE_SIZE.getHeight() / BuildAnAtomDefaults.STAGE_SIZE.getWidth() );//use the same aspect ratio so circles don't become elliptical

    // Constants that define the number of sub-atomic particles that exist
    // within the sim.
    private static final int NUM_ELECTRONS = 10;
    private static final int NUM_PROTONS = 10;
    private static final int NUM_NEUTRONS = 11;

    // Constants that define the size, position, and appearance of the buckets.
    private static final Dimension2D BUCKET_SIZE = new PDimension( 60, 30 );
    private static final Point2D PROTON_BUCKET_POSITION = new Point2D.Double( -80, -140 );
    private static final Point2D NEUTRON_BUCKET_POSITION = new Point2D.Double( 0, -140 );
    private static final Point2D ELECTRON_BUCKET_POSITION = new Point2D.Double( 80, -140 );

    protected static final double NUCLEUS_CAPTURE_DISTANCE = 50;

    protected static final double ELECTRON_CAPTURE_DISTANCE = Atom.ELECTRON_SHELL_2_RADIUS + 20;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    private final Atom atom;

    // The subatomic particles.
    private final ArrayList<Electron> electrons = new ArrayList<Electron>();
    private final ArrayList<Proton> protons = new ArrayList<Proton>();
    private final ArrayList<Neutron> neutrons = new ArrayList<Neutron>();

    // The buckets which can hold the subatomic particles.
    // TODO: i18n
    private final SubatomicParticleBucket electronBucket = new SubatomicParticleBucket( ELECTRON_BUCKET_POSITION, BUCKET_SIZE, Color.blue, "Electrons", Electron.RADIUS );
    // TODO: i18n
    private final SubatomicParticleBucket protonBucket = new SubatomicParticleBucket( PROTON_BUCKET_POSITION, BUCKET_SIZE, Color.red, "Protons", Proton.RADIUS );
    // TODO: i18n
    private final SubatomicParticleBucket neutronBucket = new SubatomicParticleBucket( NEUTRON_BUCKET_POSITION, BUCKET_SIZE, Color.gray, "Neutrons", Neutron.RADIUS );

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public BuildAnAtomModel( BuildAnAtomClock clock ) {
        super();

        this.clock = clock;

        // Create the atom.
        atom = new Atom( new Point2D.Double( 0, 0 ) );

        for ( int i = 0; i < NUM_ELECTRONS; i++ ) {
            final Electron electron = new Electron( clock );
            electrons.add( electron );
            electron.addUserControlListener( new SimpleObserver() {
                public void update() {
                    if ( !electron.isUserControlled() ) {
                        // The user just released this electron.  If it is close
                        // enough to the nucleus, send it there, otherwise
                        // send it to its bucket.
                        if ( electron.getPosition().distance( atom.getPosition() ) < ELECTRON_CAPTURE_DISTANCE ) {
                            atom.addElectron( electron );
                        }
                        else {
                            electronBucket.addParticle( electron, false );
                        }
                    }
                }
            } );
        }

        for ( int i = 0; i < NUM_PROTONS; i++ ) {
            final Proton proton = new Proton( clock );
            protons.add( proton );
            proton.addUserControlListener( new SimpleObserver() {
                public void update() {
                    if ( !proton.isUserControlled() ) {
                        // The user just released this proton.  If it is close
                        // enough to the nucleus, send it there, otherwise
                        // send it to its bucket.
                        if ( proton.getPosition().distance( atom.getPosition() ) < NUCLEUS_CAPTURE_DISTANCE ) {
                            atom.addProton( proton );
                        }
                        else {
                            protonBucket.addParticle( proton, false );
                        }
                    }

                }
            } );
        }

        for ( int i = 0; i < NUM_NEUTRONS; i++ ) {
            final Neutron neutron = new Neutron( clock );
            neutrons.add( neutron );
            neutron.addUserControlListener( new SimpleObserver() {
                public void update() {
                    if ( !neutron.isUserControlled() ) {
                        // The user just released this neutron.  If it is close
                        // enough to the nucleus, send it there, otherwise
                        // send it to its bucket.
                        if ( neutron.getPosition().distance( atom.getPosition() ) < NUCLEUS_CAPTURE_DISTANCE ) {
                            atom.addNeutron( neutron );
                        }
                        else {
                            neutronBucket.addParticle( neutron, false );
                        }
                    }

                }
            } );
        }
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    public Electron getElectron( int i ) {
        assert i >= 0 && i < numElectrons();
        return electrons.get( i );
    }

    public int numElectrons() {
        return electrons.size();
    }

    public Proton getProton( int i ) {
        assert i >= 0 && i < numProtons();
        return protons.get( i );
    }

    public int numProtons() {
        return protons.size();
    }

    public Neutron getNeutron( int i ) {
        assert i >= 0 && i < numNeutrons();
        return neutrons.get( i );
    }

    public int numNeutrons() {
        return neutrons.size();
    }

    public void reset() {

        // Reset the constituent model elements.
        atom.reset();
        electronBucket.reset();
        neutronBucket.reset();
        protonBucket.reset();

        // Put all the particles back in the bucket.
        for ( Electron electron : electrons ) {
            electronBucket.addParticle( electron, true );
        }
        for ( Proton proton : protons ) {
            protonBucket.addParticle( proton, true );
        }
        for ( Neutron neutron : neutrons ) {
            neutronBucket.addParticle( neutron, true );
        }
    }

    public Atom getAtom() {
        return atom;
    }

    public Rectangle2D getModelViewport() {
        return MODEL_VIEWPORT;
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public SubatomicParticleBucket getElectronBucket() {
        return electronBucket;
    }

    public SubatomicParticleBucket getProtonBucket() {
        return protonBucket;
    }

    public SubatomicParticleBucket getNeutronBucket() {
        return neutronBucket;
    }
}
