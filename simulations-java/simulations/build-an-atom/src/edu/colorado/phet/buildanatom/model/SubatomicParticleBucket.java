package edu.colorado.phet.buildanatom.model;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

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
public class SubatomicParticleBucket {

    // Proportion of the total height which the ellipse that represents
    // the hole occupies.  It is assumed that the width of the hole
    // is the same as the width specified at construction.
    private static final double HOLE_ELLIPSE_HEIGHT_PROPORTION = 0.25;

    // The position is defined to be where the center of the hole is.
    private final Point2D position = new Point2D.Double();

    // The two shapes that define the overall shape of the bucket.
    private final Shape holeShape;
    private final Shape containerShape;

    // Base color of the bucket.
    private final Color baseColor;

    // Caption to be shown on the bucket.
    private final String captionText;

    // Particles that are in this bucket.
    private final ArrayList<SubatomicParticle> containedParticles = new ArrayList<SubatomicParticle>();

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
    private final SubatomicParticle.Adapter particleRemovalListener = new SubatomicParticle.Adapter() {
        @Override
        public void grabbedByUser( final SubatomicParticle particle ) {
            // The user has picked up this particle, so we assume
            // that they want to remove it.
            assert containedParticles.contains( particle );
            containedParticles.remove( particle );
            particle.removeListener( this );

            final Point2D initialPosition = particle.getDestination();
            particle.addPositionListener( new SimpleObserver() {
                public void update() {
                    if (initialPosition.distance( particle.getDestination() )>particle.getRadius()*10){
                        relayoutBucketParticles();
                        particle.removePositionListener( this );
                    }
                }
            } );
        }
    };

    /**
     * Constructor.
     */
    public SubatomicParticleBucket( Point2D position, Dimension2D size, Color baseColor, String caption, double particleRadius, double usableWidthProportion, double yOffset ) {
        this.position.setLocation( position );
        this.baseColor = baseColor;
        this.captionText = caption;
        this.particleRadius = particleRadius;
        this.usableWidthProportion = usableWidthProportion;
        this.yOffset = yOffset;

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

    /**
     * Constructor that assumes that the entire width of the bucket should be
     * used for particle placement.
     */
    public SubatomicParticleBucket( Point2D position, Dimension2D size, Color baseColor, String caption, double particleRadius ) {
        this(position, size, baseColor, caption, particleRadius, 1, 0);
    }

    public void reset() {
        containedParticles.clear();
    }

    public Point2D getPosition() {
        return position;
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

    public void removeParticle( SubatomicParticle particle ) {
        if (!containedParticles.contains( particle )){
           System.err.println( getClass().getName() + " - Error: Particle not here, can't remove." );
        }
        assert containedParticles.contains( particle );
        containedParticles.remove( particle );
    }

    public void addParticle( final SubatomicParticle particle, boolean moveImmediately ) {
        // Determine an open location in the bucket.
        Point2D freeParticleLocation = getFirstOpenLocation();

        // Move the particle.
        if ( moveImmediately ) {
            // Move the particle instantaneously to the destination.
            particle.setPositionAndDestination( freeParticleLocation );
        }
        else {
            // Set the destination and let the particle find its own way.
            particle.setDestination( freeParticleLocation );
        }

        // Listen for when the user removes this particle from the bucket.
        particle.addListener( particleRemovalListener );

        containedParticles.add( particle );
    }

    public boolean containsParticle( SubatomicParticle particle ){
        return containedParticles.contains( particle );
    }

    public ArrayList<SubatomicParticle> getParticleList(){
        return containedParticles;
    }

    //DOC
    private Point2D getFirstOpenLocation() {
        Point2D openLocation = new Point2D.Double();
        double placeableWidth = holeShape.getBounds2D().getWidth() * usableWidthProportion - 2 * particleRadius;
        double offsetFromBucketEdge = (holeShape.getBounds2D().getWidth() - placeableWidth) / 2 + particleRadius;
        int numParticlesInLayer = (int) Math.floor( placeableWidth / ( particleRadius * 2 ) );
        int layer = 0;
        int positionInLayer = 0;
        boolean found = false;
        while ( !found ) {
            double yPos = getYPositionForLayer( layer );
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
                    layer++;
                    positionInLayer = 0;
                    numParticlesInLayer--;
                    offsetFromBucketEdge += particleRadius;
                    if ( numParticlesInLayer == 0 ) {
                        // This algorithm doesn't handle the situation
                        // where more particles are added than can be
                        // stacked into a pyramid of the needed size, but
                        // so far it hasn't needed to.  If this
                        // requirement changes, the algorithm will need to
                        // change too.
                        //                            assert false;
                        numParticlesInLayer = 1;
                        offsetFromBucketEdge -= particleRadius;
                    }
                }
            }
        }
        return openLocation;
    }

    //DOC what is a layer?
    private double getYPositionForLayer( int layer ) {
        double yPos = getPosition().getY() + layer * particleRadius * 2 * 0.866 + yOffset;
        return yPos;
    }

    private void relayoutBucketParticles() {
        ArrayList<SubatomicParticle> p = new ArrayList<SubatomicParticle>( containedParticles );
        for ( SubatomicParticle containedParticle : p) {
            if (isDangling(containedParticle)){
                removeParticle( containedParticle );
                addParticle( containedParticle, false);
                relayoutBucketParticles();
            }
        }
    }

    //DOC
    private boolean isDangling( SubatomicParticle containedParticle ) {
        boolean upperLayer = containedParticle.getDestination().getY() > getYPositionForLayer( 0 );
        return upperLayer && countSupportingParticles(containedParticle)<2;
    }

    private int countSupportingParticles( SubatomicParticle p ) {
        int count =0 ;
        for ( SubatomicParticle particle : containedParticles ) {
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
        for ( SubatomicParticle particle : containedParticles ) {
            Point2D position = particle.getDestination();
            if ( position.getX() == x && position.getY() == y ) {
                positionOpen = false;
                break;
            }
        }
        return positionOpen;
    }
}