/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.model.SubatomicParticle;
import edu.colorado.phet.buildanatom.model.SubatomicParticleBucket;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
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
public class InteractiveIsotopeModel implements Resettable {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final Rectangle2D MODEL_VIEWPORT =
            new Rectangle2D.Double( -200, -150,
            400,
            400 * BuildAnAtomDefaults.STAGE_SIZE.getHeight() / BuildAnAtomDefaults.STAGE_SIZE.getWidth() );//use the same aspect ratio so circles don't become elliptical

    // Constant that defines the default number of neutrons in the bucket.
    private static final int DEFAULT_NUM_NEUTRONS_IN_BUCKET = 13;

    // Constants that define the size, position, and appearance of the neutron bucket.
    private static final Dimension2D BUCKET_SIZE = new PDimension( 60, 30 );
    private static final Point2D NEUTRON_BUCKET_POSITION = new Point2D.Double( 0, -150 );

    // Distance at which nucleons are captured by the nucleus.
    protected static final double NUCLEUS_CAPTURE_DISTANCE = Atom.ELECTRON_SHELL_1_RADIUS;

    // Default atom configuration.
    private static final AtomValue DEFAULT_ATOM_CONFIG = new AtomValue( 1, 0, 1 ); // Hydrogen.

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    private final Atom atom;

    // Array that tracks the neutrons, whether they are in the bucket or in
    // the atom.
    private final ArrayList<Neutron> neutrons = new ArrayList<Neutron>();

    // The buckets that holds the neutrons that are not in the atom.
    private final SubatomicParticleBucket neutronBucket = new SubatomicParticleBucket( NEUTRON_BUCKET_POSITION,
            BUCKET_SIZE, Color.gray, BuildAnAtomStrings.NEUTRONS_NAME, Neutron.RADIUS );

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    /**
     * Construct the model with the atoms initially in the bucket.
     */
    public InteractiveIsotopeModel( BuildAnAtomClock clock ) {
        super();

        this.clock = clock;

        // Create the atom.
        atom = new Atom( new Point2D.Double( 0, 0 ), clock );


        for ( int i = 0; i < DEFAULT_NUM_NEUTRONS_IN_BUCKET; i++ ) {
            final Neutron neutron = new Neutron( clock );
            neutrons.add( neutron );
            neutron.addListener( new SubatomicParticle.Adapter() {
                @Override
                public void droppedByUser( SubatomicParticle particle ) {
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

    public Rectangle2D getModelViewport() {
        return MODEL_VIEWPORT;
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public Atom getAtom() {
        return atom;
    }

    /**
     * Reset the model.  The sets the atom and the neutron bucket into their
     * default initial states.
     */
    public void reset(){

        // Reset the atom into the default configuration.
        atom.reset();
        for ( int i = 0; i < DEFAULT_ATOM_CONFIG.getNumElectrons(); i++ ){
            atom.addElectron( new Electron( clock ), true );
        }
        for ( int i = 0; i < DEFAULT_ATOM_CONFIG.getNumProtons(); i++ ){
            atom.addProton( new Proton( clock ), true );
        }
        for ( int i = 0; i < DEFAULT_ATOM_CONFIG.getNumNeutrons(); i++ ){
            atom.addNeutron( new Neutron( clock ), true );
        }

        // Reset the neutron bucket.
        neutronBucket.reset();
        for ( Neutron neutron : neutrons ){
            neutronBucket.addParticle( neutron, true );
        }
    }

    /**
     * @return
     */
    public SubatomicParticleBucket getNeutronBucket() {
        return neutronBucket;
    }
}
