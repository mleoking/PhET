// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Main model class.  Units are picometers (1E-12).
 */
public class BuildAnAtomModel implements Resettable {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Constants that define the number of sub-atomic particles that exist
    // within the sim.
    private static final int DEFAULT_NUM_ELECTRONS = 10;
    private static final int DEFAULT_NUM_PROTONS = 10;
    private static final int DEFAULT_NUM_NEUTRONS = 13;

    // Constants that define the size, position, and appearance of the buckets.
    private static final Dimension2D BUCKET_SIZE = new PDimension( 60, 30 );
    private static final Point2D PROTON_BUCKET_POSITION = new Point2D.Double( -80, -150 );
    private static final Point2D NEUTRON_BUCKET_POSITION = new Point2D.Double( 0, -150 );
    private static final Point2D ELECTRON_BUCKET_POSITION = new Point2D.Double( 80, -150 );

    protected static final double NUCLEUS_CAPTURE_DISTANCE = Atom.ELECTRON_SHELL_1_RADIUS;

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
    private final ParticleBucket electronBucket = new ParticleBucket( ELECTRON_BUCKET_POSITION,
            BUCKET_SIZE, Color.blue, BuildAnAtomStrings.ELECTRONS_NAME, Electron.RADIUS, 0.6, -Electron.RADIUS / 2 );
    private final ParticleBucket protonBucket = new ParticleBucket( PROTON_BUCKET_POSITION,
            BUCKET_SIZE, Color.red, BuildAnAtomStrings.PROTONS_NAME, Proton.RADIUS );
    private final ParticleBucket neutronBucket = new ParticleBucket( NEUTRON_BUCKET_POSITION,
            BUCKET_SIZE, Color.gray, BuildAnAtomStrings.NEUTRONS_NAME, Neutron.RADIUS );

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public BuildAnAtomModel( BuildAnAtomClock clock ) {
        this( clock, new ImmutableAtom( DEFAULT_NUM_PROTONS, DEFAULT_NUM_NEUTRONS, DEFAULT_NUM_ELECTRONS ) );
    }

    /**
     * Construct the model based on the atom description.
     */
    public BuildAnAtomModel( BuildAnAtomClock clock, ImmutableAtom atomValue, boolean moveImmediately ) {
        this( clock, atomValue );
        setState( atomValue, moveImmediately );
    }

    /**
     * Construct the model with the atoms initially in the bucket.
     */
    public BuildAnAtomModel( BuildAnAtomClock clock, ImmutableAtom atomValue ) {
        super();

        this.clock = clock;

        // Create the atom.
        atom = new Atom( new Point2D.Double( 0, 0 ), clock );

        for ( int i = 0; i < atomValue.getNumElectrons(); i++ ) {
            final Electron electron = new Electron( clock );
            electrons.add( electron );
            electron.addListener( new SphericalParticle.Adapter() {
                @Override
                public void droppedByUser( SphericalParticle particle ) {
                    // The user just released this electron.  If it is close
                    // enough to the shell, and there is room, send it there.
                    // Otherwise send it to its bucket.
                    if ( atom.getRemainingElectronCapacity() > 0 && electron.getPosition().distance( atom.getPosition() ) < ELECTRON_CAPTURE_DISTANCE ) {
                        atom.addElectron( electron , false );
                    }
                    else {
                        electronBucket.addParticle( electron, false );
                    }
                }
            } );
        }

        for ( int i = 0; i < atomValue.getNumProtons(); i++ ) {
            final Proton proton = new Proton( clock );
            protons.add( proton );
            proton.addListener( new SphericalParticle.Adapter() {
                @Override
                public void droppedByUser( SphericalParticle particle ) {
                    // The user just released this proton.  If it is close
                    // enough to the nucleus, send it there, otherwise
                    // send it to its bucket.
                    if ( proton.getPosition().distance( atom.getPosition() ) < NUCLEUS_CAPTURE_DISTANCE ) {
                        atom.addProton( proton, false );
                    }
                    else {
                        protonBucket.addParticle( proton, false );
                    }
                }
            } );
        }

        for ( int i = 0; i < atomValue.getNumNeutrons(); i++ ) {
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

        initializeBuckets();
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    public void reset() {

        // Reset the constituent model elements.
        atom.reset();
        electronBucket.reset();
        neutronBucket.reset();
        protonBucket.reset();

        initializeBuckets();
    }

    private void initializeBuckets(){
        // Put all the particles into their respective buckets.
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

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public ParticleBucket getElectronBucket() {
        return electronBucket;
    }

    public ParticleBucket getProtonBucket() {
        return protonBucket;
    }

    public ParticleBucket getNeutronBucket() {
        return neutronBucket;
    }

    public static<T> ArrayList<T> reverse(ArrayList<T>list){
        ArrayList<T> t = new ArrayList<T>( list );
        Collections.reverse( t );
        return t;
    }

    public Proton getFreeProton() {
        for ( Proton proton : reverse(protons )) {
            if (!getAtom().containsProton(proton )){
                return proton;
            }
        }
        return null;
    }
    public Neutron getFreeNeutron() {
        for ( Neutron neutron : reverse(neutrons ) ) {
            if (!getAtom().containsNeutron(neutron )){
                return neutron;
            }
        }
        return null;
    }
    public Electron getFreeElectron() {
        for ( Electron electron : reverse(electrons )) {
            if (!getAtom().containsElectron(electron )){
                return electron;
            }
        }
        return null;
    }

    public void setState( ImmutableAtom answer, boolean moveImmediately ) {
        ArrayList<SphericalParticle> removedParticles = getAtom().setState( answer, this, moveImmediately );
        for ( SphericalParticle particle : removedParticles ) {
            if ( particle instanceof Proton ) {
                protonBucket.addParticle( particle, moveImmediately );
            }
            else if ( particle instanceof Electron ) {
                electronBucket.addParticle( particle, moveImmediately );
            }
            else if ( particle instanceof Neutron ) {
                neutronBucket.addParticle( particle, moveImmediately );
            }
        }
    }
}
