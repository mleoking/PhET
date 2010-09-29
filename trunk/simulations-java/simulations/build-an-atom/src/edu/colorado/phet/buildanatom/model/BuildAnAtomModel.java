/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.buildanatom.module.BuildAnAtomDefaults;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
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

    private static final Dimension2D BUCKET_SIZE = new PDimension( 60, 30 );
    private static final Point2D PROTON_BUCKET_POSITION = new Point2D.Double( -80, -140 );
    private static final Point2D NEUTRON_BUCKET_POSITION = new Point2D.Double( 0, -140 );
    private static final Point2D ELECTRON_BUCKET_POSITION = new Point2D.Double( 80, -140 );

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
    private final Bucket electronBucket = new Bucket( ELECTRON_BUCKET_POSITION, BUCKET_SIZE, Color.blue, "Electrons" );
    // TODO: i18n
    private final Bucket protonBucket = new Bucket( PROTON_BUCKET_POSITION, BUCKET_SIZE, Color.red, "Protons" );
    // TODO: i18n
    private final Bucket neutronBucket = new Bucket( NEUTRON_BUCKET_POSITION, BUCKET_SIZE, Color.gray, "Neutrons" );

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomModel( BuildAnAtomClock clock ) {
        super();

        this.clock = clock;

        // Create the atom.
        atom = new Atom( new Point2D.Double( 0, 0 ) );

        electrons.add( new Electron( 10, 10 ) );
        protons.add( new Proton( -10, 10 ) );
        neutrons.add( new Neutron( -10, -10 ) );
    }

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

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    public Atom getAtom() {
        return atom;
    }

    public Rectangle2D getModelViewport() {
        return MODEL_VIEWPORT;
    }

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public Bucket getElectronBucket() {
        return electronBucket;
    }

    public Bucket getProtonBucket() {
        return protonBucket;
    }

    public Bucket getNeutronBucket() {
        return neutronBucket;
    }

    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------



    /**
     * This class represents that atom in the model.  It supplies static
     * information such as the position of the atom, as well as dynamic
     * information such as the number of protons present.
     */
    public static class Atom {

        // Nuclear radius, in picometers.  This is not to scale - we need it
        // to be larger than real life.
        private static final double NUCLEUS_RADIUS = 5;

        private final Point2D position = new Point2D.Double();

        // Radii of the electron shells.  The values used for these distances
        // are remotely related to reality (based on covalent bond radii of
        // various molecules) but they have been tweaked significantly in
        // order to be at a scale that works visually in the sim.
        private final ArrayList<Double> electronShellRadii = new ArrayList<Double>() {
            {
                add( new Double( 34 ) );
                add( new Double( 102 ) );
            }
        };

        public Atom( Point2D position ) {
            this.position.setLocation( position );
        }

        public ArrayList<Double> getElectronShellRadii() {
            return electronShellRadii;
        }

        public double getNucleusRadius() {
            return NUCLEUS_RADIUS;
        }

        public Point2D getPosition() {
            return position;
        }
    }

    /**
     * Class that defines the shape and functionality of a "bucket", which is
     * (in this sim anyway) a container into which sub-atomic particles can be
     * placed.  It is defined such that it will have somewhat of a 3D look to
     * it, so it has two shapes, one that is the hole, and one that is the
     * outside of the bucket.
     *
     * IMPORTANT NOTE: The shapes that are created and that comprise the
     * bucket are set up such that the point (0,0) is in the center of the
     * bucket's hole.
     *
     * @author John Blanco
     */
    public static class Bucket {

        // Proportion of the total height which the ellipse that represents
        // the hole occupies.  It is assumed that the width of the hole
        // is the same as the width specified at construction.
        private static final double HOLE_ELLIPSE_HEIGHT_PROPORTION = 0.3;

        // The position is defined to be where the center of the hole is.
        private final Point2D position = new Point2D.Double();

        // The two shapes that define the overall shape of the bucket.
        private final Shape holeShape;
        private final DoubleGeneralPath containerShape;

        // Base color of the bucket.
        private final Color baseColor;

        // Caption to be shown on the bucket.
        private final String captionText;

        // Particles that are in this bucket.
        private final ArrayList<SubatomicParticle> containedParticles = new ArrayList<SubatomicParticle>();

        public Bucket( Point2D position, Dimension2D size, Color baseColor, String caption ) {
            this.position.setLocation( position );
            this.baseColor = baseColor;
            this.captionText = caption;

            // Create the shape of the bucket's hole.
            holeShape = new Ellipse2D.Double( -size.getWidth() / 2,
                    -size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION / 2,
                    size.getWidth(),
                    size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION );

            // Create the shape of the container.  This code is a bit "tweaky",
            // meaning that there are a lot of fractional multipliers in here
            // to try to achieve the desired pseudo-3D look.  The intent is
            // that the "tilt" of the bucket can be changed without needing to
            // rework this code.  It may or may not work out that way, so
            // adjust as necessary to get the look you need.
            double containerHeight = size.getHeight() * ( 1 - ( HOLE_ELLIPSE_HEIGHT_PROPORTION / 2 ) );
            containerShape = new DoubleGeneralPath();
            containerShape.moveTo( -size.getWidth() * 0.5, 0 );
            containerShape.lineTo( -size.getWidth() * 0.4, -containerHeight * 0.8 );
            containerShape.curveTo(
                    -size.getWidth() * 0.3,
                    -containerHeight * 0.8 - size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION * 0.6,
                    size.getWidth() * 0.3,
                    -containerHeight * 0.8 - size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION * 0.6,
                    size.getWidth() * 0.4,
                    -containerHeight * 0.8 );
            containerShape.lineTo( size.getWidth() * 0.5, 0 );
            containerShape.closePath();
        }

        public Point2D getPosition() {
            return position;
        }

        public Shape getHoleShape() {
            return holeShape;
        }

        public Shape getContainerShape() {
            return containerShape.getGeneralPath();
        }

        public Color getBaseColor() {
            return baseColor;
        }

        public String getCaptionText() {
            return captionText;
        }

        public void addParticle( final SubatomicParticle particle, boolean moveImmediately ) {
            // Determine an open location in the bucket.
            // TBD: Go to center of hole for now.
            Point2D freeParticleLocation = getPosition();

            // Move the particle.
            if ( moveImmediately ) {
                // Move the particle instantaneously to the destination.
                particle.setPosition( freeParticleLocation );
            }
            else {
                // Set the destination and let the particle find its own way.
                particle.setDestination( freeParticleLocation );
            }

            particle.addUserControlListener( new SimpleObserver() {
                public void update() {
                    if ( particle.isUserControlled() ) {
                        // The user has picked up this particle, so we assume
                        // that it is essentially removed from the bucket.
                        removeParticle( particle );
                    }
                }
            } );

            containedParticles.add( particle );
        }

        public void removeParticle( SubatomicParticle particle ) {
            assert containedParticles.contains( particle );
            containedParticles.remove( particle );
        }


    }
}
