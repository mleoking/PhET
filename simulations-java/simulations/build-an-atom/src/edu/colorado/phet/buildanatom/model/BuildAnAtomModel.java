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
    private static final Point2D PROTON_BUCKET_POSITION = new Point2D.Double( -100, -120 );
    private static final Point2D NEUTRON_BUCKET_POSITION = new Point2D.Double( 0, -120 );
    private static final Point2D ELECTRON_BUCKET_POSITION = new Point2D.Double( 100, -120 );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    private final Atom atom;

    // The humor in the name "bucketList" is not lost on me.  Just in case
    // you were wondering.
    private final ArrayList<Bucket> bucketList = new ArrayList<Bucket>();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomModel( BuildAnAtomClock clock ) {
        super();

        this.clock = clock;

        // Create the atom.
        atom = new Atom( new Point2D.Double( 0, 0 ) );

        // Create the buckets that hold the sub-atomic particles.
        // TODO: i18n
        bucketList.add( new Bucket( PROTON_BUCKET_POSITION, BUCKET_SIZE, Color.red, "Protons" ) );
        // TODO: i18n
        bucketList.add( new Bucket( NEUTRON_BUCKET_POSITION, BUCKET_SIZE, Color.gray, "Neutrons" ) );
        // TODO: i18n
        bucketList.add( new Bucket( ELECTRON_BUCKET_POSITION, BUCKET_SIZE, Color.blue, "Electrons" ) );
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

    public ArrayList<Bucket> getBuckets() {
        return new ArrayList<Bucket>( bucketList );
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
        private static final double NUCLEUS_RADIUS = 10;

        private final Point2D position = new Point2D.Double();

        // Radii of the electron shells.  The values used here are based on
        // the covalent radius values found in Wikipedia.
        private final ArrayList<Double> electronShellRadii = new ArrayList<Double>() {
            {
                add( new Double( 34 ) );
                add( new Double( 110 ) );
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
    }
}
