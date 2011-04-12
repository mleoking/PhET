// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model.buckets;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Class that defines the shape and common functionality for a "bucket", which
 * is container into which some sort of model objects may be placed.  This is
 * a model object in the Model-View-Controller paradigm, and requires a
 * counterpart in the view in order to be presented to the user.
 * <p/>
 * In general, this is intended to be a base class, and subclasses should be
 * used to add specific functionality, such as how other model objects are
 * added to and removed from the bucket.
 * <p/>
 * One other important note: The position of the bucket in model space is
 * based on the center of the bucket's opening.
 *
 * @author John Blanco
 */
public class Bucket {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // Proportion of the total height which the ellipse that represents
    // the hole occupies.  It is assumed that the width of the hole
    // is the same as the width specified at construction.
    private static final double HOLE_ELLIPSE_HEIGHT_PROPORTION = 0.25;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // The position is defined to be where the center of the hole is.
    private Point2D position = new Point2D.Double();

    // The two shapes that define the overall shape of the bucket.
    protected final Shape holeShape;
    private final Shape containerShape;

    // Base color of the bucket.
    private final Color baseColor;

    // Caption to be shown on the bucket.
    private final String captionText;

    // The following boolean property indicates whether this bucket is
    // currently a part of the larger model.  It is intended to be used as a
    // notification for when the bucket goes away, so that the corresponding
    // view element can also be removed.
    private final BooleanProperty partOfModelProperty = new BooleanProperty( true );

    // Particles that are in this bucket.
    private final List<AtomModel> containedAtoms = new LinkedList<AtomModel>();

    private final Atom atomType;

    /*---------------------------------------------------------------------------*
    * positioning instance data
    *----------------------------------------------------------------------------*/

    // Radius of particles that will be going into this bucket.  This is
    // used for placing particles.
    private final double particleRadius;

    // Proportion of the width of the bucket to use for particle placement.
    // A value of 1 means that the entire bucket should be used.
    private final double usableWidthProportion;

    // Offset, in picometers, of the particles in the y direction.  This helps
    // to avoid the appearance of particles floating in the bucket.
    private final double yOffset;

    // Listener for events where the user grabs the particle, which is interpreted as
    // removal from the bucket.
    private final AtomModel.Adapter atomRemovalListener = new AtomModel.Adapter() {
        @Override
        public void grabbedByUser( final AtomModel particle ) {
            // The user has picked up this particle, so we assume
            // that they want to remove it.
            assert containedAtoms.contains( particle );
            containedAtoms.remove( particle );
            particle.removeListener( this );

            final Point2D initialPosition = particle.getDestination();
            particle.addPositionListener( new SimpleObserver() {
                public void update() {
                    if ( initialPosition.distance( particle.getDestination() ) > particle.getRadius() * 10 ) {
                        relayoutBucketParticles();
                        particle.removePositionListener( this );
                    }
                }
            } );
        }
    };

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public Bucket( Atom atomType, Dimension2D size, String caption ) {
        this( atomType, size, caption, 1, 0 );
    }

    /**
     * Constructor.  The dimensions used are just numbers, i.e. they are not
     * meant to be any specific size (such as meters).  This enabled
     * reusability in any 2D model.
     */
    public Bucket( Atom atomType, Dimension2D size, String caption, double usableWidthProportion, double yOffset ) {
        this.baseColor = atomType.getColor();
        this.captionText = caption;
        this.usableWidthProportion = usableWidthProportion;
        this.yOffset = yOffset;
        this.atomType = atomType;
        this.particleRadius = atomType.getRadius();

        // Create the shape of the bucket's hole.
        holeShape = new Ellipse2D.Double( -size.getWidth() / 2,
                                          -size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION / 2,
                                          size.getWidth(),
                                          size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION );

        // Create the shape of the container.  This code is a bit "tweaky",
        // meaning that there are a lot of fractional multipliers in here
        // to try to achieve the desired pseudo-3D look.  The intent is
        // that the "tilt" of the bucket can be changed without needing to
        // rework this code.
        double containerHeight = size.getHeight() * ( 1 - ( HOLE_ELLIPSE_HEIGHT_PROPORTION / 2 ) );
        DoubleGeneralPath containerPath = new DoubleGeneralPath();
        containerPath.moveTo( -size.getWidth() * 0.5, 0 );
        containerPath.lineTo( -size.getWidth() * 0.4, -containerHeight * 0.8 );
        containerPath.curveTo(
                -size.getWidth() * 0.3,
                -containerHeight * 0.8 - size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION * 0.6,
                size.getWidth() * 0.3,
                -containerHeight * 0.8 - size.getHeight() * HOLE_ELLIPSE_HEIGHT_PROPORTION * 0.6,
                size.getWidth() * 0.4,
                -containerHeight * 0.8 );
        containerPath.lineTo( size.getWidth() * 0.5, 0 );
        containerPath.closePath();
        Area containerArea = new Area( containerPath.getGeneralPath() );
        containerArea.subtract( new Area( holeShape ) );
        containerShape = containerArea;
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public Point2D getPosition() {
        return position;
    }

    public void setPosition( Point2D position ) {
        this.position = position;
    }

    public Shape getHoleShape() {
        return holeShape;
    }

    public Shape getContainerShape() {
        return containerShape;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public String getCaptionText() {
        return captionText;
    }

    public BooleanProperty getPartOfModelProperty() {
        return partOfModelProperty;
    }

    public double getWidth() {
        return containerShape.getBounds().getWidth();
    }

    public void reset() {
        containedAtoms.clear();
    }

    public void removeParticle( AtomModel particle ) {
        if ( !containedAtoms.contains( particle ) ) {
            System.err.println( getClass().getName() + " - Error: Particle not here, can't remove." );
        }
        assert containedAtoms.contains( particle );
        containedAtoms.remove( particle );
        particle.removeListener( atomRemovalListener );
    }

    public void addAtom( final AtomModel atom, boolean animate ) {
        // Determine an open location in the bucket.
        Point2D freeParticleLocation = getFirstOpenLocation();

        // Move the atom.
        if ( animate ) {
            // Set the destination and let the atom find its own way.
            atom.setDestination( freeParticleLocation );
        }
        else {
            // Move the atom instantaneously to the destination.
            atom.setPositionAndDestination( freeParticleLocation );
        }

        // Listen for when the user removes this atom from the bucket.
        atom.addListener( atomRemovalListener );

        containedAtoms.add( atom );
    }

    public boolean containsParticle( AtomModel particle ) {
        return containedAtoms.contains( particle );
    }

    public List<AtomModel> getAtoms() {
        return containedAtoms;
    }

    protected double getParticleRadius() {
        return particleRadius;
    }

    /*
     * Returns the first location in a bucket that a particle could be placed without overlapping another particle.
     * Locations may be above (+y) other particles, in order to create a stacking effect.
     */
    private Point2D getFirstOpenLocation() {
        Point2D openLocation = new Point2D.Double();
        double placeableWidth = holeShape.getBounds2D().getWidth() * usableWidthProportion - 2 * particleRadius;
        double offsetFromBucketEdge = ( holeShape.getBounds2D().getWidth() - placeableWidth ) / 2 + particleRadius;
        int numParticlesInLayer = (int) Math.floor( placeableWidth / ( particleRadius * 2 ) );
        int row = 0;
        int positionInLayer = 0;
        boolean found = false;
        while ( !found ) {
            double yPos = getYPositionForRow( row );
            double xPos = getPosition().getX() - holeShape.getBounds2D().getWidth() / 2 + offsetFromBucketEdge + positionInLayer * 2 * particleRadius;
            if ( isPositionOpen( xPos, yPos ) ) {
                // We found a location that is open.
                openLocation.setLocation( xPos, yPos );
                found = true;
                continue;
            }
            else {
                positionInLayer++;
                if ( positionInLayer >= numParticlesInLayer ) {
                    // Move to the next layer.
                    row++;
                    positionInLayer = 0;
                    numParticlesInLayer--;
                    offsetFromBucketEdge += particleRadius;
                    if ( numParticlesInLayer == 0 ) {
                        // This algorithm doesn't handle the situation where
                        // more particles are added than can be stacked into
                        // a pyramid of the needed size, but so far it hasn't
                        // needed to.  If this requirement changes, the
                        // algorithm will need to change too.
                        numParticlesInLayer = 1;
                        offsetFromBucketEdge -= particleRadius;
                    }
                }
            }
        }
        return openLocation;
    }

    /**
     * @param row 0 for the y=0 row, 1 for the next row, etc.
     * @return
     */
    private double getYPositionForRow( int row ) {
        return getPosition().getY() + row * particleRadius * 2 * 0.866 + yOffset;
    }

    private void relayoutBucketParticles() {
        List<AtomModel> copyOfContainedParticles = new LinkedList<AtomModel>( containedAtoms );
        for ( AtomModel containedParticle : copyOfContainedParticles ) {
            if ( isDangling( containedParticle ) ) {
                removeParticle( containedParticle );
                addAtom( containedParticle, false );
                relayoutBucketParticles();
            }
        }
    }

    /**
     * Determine whether a particle is 'dangling', i.e. hanging above an open
     * space in the stack of particles.  Dangling particles should fall.
     */
    private boolean isDangling( AtomModel particle ) {
        boolean onBottomRow = particle.getDestination().getY() == getYPositionForRow( 0 );
        return !onBottomRow && countSupportingParticles( particle ) < 2;
    }

    private int countSupportingParticles( AtomModel p ) {
        int count = 0;
        for ( AtomModel particle : containedAtoms ) {
            if ( particle != p &&//not ourself
                 particle.getDestination().getY() < p.getDestination().getY() && //must be in a lower layer
                 particle.getDestination().distance( p.getDestination() ) < p.getRadius() * 3 ) {
                count++;
            }
        }
        return count;
    }

    /**
     * Determine whether the given particle position is open (i.e.
     * unoccupied) in the bucket.
     *
     * @param x
     * @param y
     * @return
     */
    private boolean isPositionOpen( double x, double y ) {
        boolean positionOpen = true;
        for ( AtomModel particle : containedAtoms ) {
            Point2D position = particle.getDestination();
            if ( position.getX() == x && position.getY() == y ) {
                positionOpen = false;
                break;
            }
        }
        return positionOpen;
    }
}