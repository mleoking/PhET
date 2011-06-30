// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.torque.teetertotter.model.weights.Weight;

/**
 * This is the plank upon which weights can be placed.
 *
 * @author John Blanco
 */
public class Plank extends ShapeModelElement {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    public static final double LENGTH = 4;// meters
    public static final double THICKNESS = 0.05; // meters
    public static final int NUM_SNAP_TO_MARKERS = 19;
    public static final double MASS = 200; // kg

    // Moment of inertia.
    // TODO: I'm not certain that this is the correct formula, should check with Mike Dubson.
    private static final double MOMENT_OF_INERTIA = MASS * ( ( LENGTH * LENGTH ) + ( THICKNESS * THICKNESS ) ) / 12;

    // The x position (i.e. the position along the horizontal axis) where the
    // pivot exists.  If and when the fulcrum becomes movable, this will need
    // to become a variable.
    private static final double PIVOT_PT_POS_X = LENGTH / 2;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    public double torqueFromWeights = 0;
    public double tiltAngle = 0;
    public double angularVelocity = 0; // radians/sec
    public final double maxTiltAngle;

    // List of the weights that are resting on the surface of this plank.
    private final List<Weight> weightsOnSurface = new ArrayList<Weight>();

    // Map of weights to distance from the center of the plank.
    private final Map<Weight, Double> mapWeightToDistFromCenter = new HashMap<Weight, Double>();

    // Property that indicates whether the suppoort columns are currently
    // active.
    BooleanProperty supportColumnsActive;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    /**
     * Constructor.  Creates the initial shape of the plank.  This assumes
     * that the plank is initially sitting with a fulcrum under the center and
     * that it is initially balanced.
     *
     * @param clock
     * @param centerHeight
     * @param supportColumnsActive
     */
    public Plank( final ConstantDtClock clock, double centerHeight, BooleanProperty supportColumnsActive ) {
        super( generateShape( centerHeight, 0 ) );
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
        maxTiltAngle = Math.asin( centerHeight / ( LENGTH / 2 ) );

        this.supportColumnsActive = supportColumnsActive;
        // Listen to the support column property.  The plank goes back to the
        // level position whenever the supports are active.
        supportColumnsActive.addObserver( new SimpleObserver() {
            public void update() {
                forceToLevel();
            }
        } );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public static double getLength() {
        return LENGTH;
    }

    /**
     * Add a weight (e.g. a brick) to the surface of the plank.  This will
     * set the position and orientation of the weight.  The plank will then
     * continue to control the position and orientation of the weight until the
     * weight is removed from the surface of the plank.
     *
     * @param weight
     */
    public void addWeightToSurface( final Weight weight ) {
        weightsOnSurface.add( weight );
        weight.setPosition( getClosestOpenLocation( weight.getPosition() ) );
        mapWeightToDistFromCenter.put( weight, ( weight.getPosition().getX() - positionHandle.getX() ) / Math.cos( tiltAngle ) );
        weight.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( userControlled ) {
                    // The user has picked up this weight, so it is no longer
                    // on the surface.
                    weightsOnSurface.remove( weight );
                    weight.setRotationAngle( 0 );
                    updateTorqueDueToWeights();
                }
            }
        }
        );
        updateWeightPositions();
        updateTorqueDueToWeights();
    }

    // Generate the shape of the plank.  This is static so that it can be used
    // in the constructor.
    private static Shape generateShape( final double centerHeight, double tiltAngle ) {
        // Create the outline shape of the plank.
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( LENGTH / 2, 0 );
        path.lineTo( LENGTH / 2, THICKNESS );
        path.lineTo( 0, THICKNESS );
        path.lineTo( -LENGTH / 2, THICKNESS );
        path.lineTo( -LENGTH / 2, 0 );
        path.lineTo( 0, 0 );
        // Add the "snap to" markers to the plank.
        double interMarkerDistance = LENGTH / (double) ( NUM_SNAP_TO_MARKERS + 1 );
        double markerXPos = -LENGTH / 2 + interMarkerDistance;
        for ( int i = 0; i < NUM_SNAP_TO_MARKERS; i++ ) {
            path.moveTo( markerXPos, 0 );
            path.lineTo( markerXPos, THICKNESS );
            markerXPos += interMarkerDistance;
        }
        // Rotate the appropriate amount.
        Shape shape = AffineTransform.getRotateInstance( tiltAngle ).createTransformedShape( path.getGeneralPath() );
        // Translate to the appropriate height.
        shape = AffineTransform.getTranslateInstance( 0, centerHeight ).createTransformedShape( shape );

        return shape;
    }

    private Point2D getClosestOpenLocation( Point2D p ) {
        // TODO: Doesn't actually give open locations yet, just valid snap-to ones.
        Point2D closestOpenLocation = new Point2D.Double( 0, 0 );
        for ( Point2D location : getSnapToLocations() ) {
            if ( location.distance( p ) < closestOpenLocation.distance( p ) ) {
                closestOpenLocation.setLocation( location );
            }
        }
        return closestOpenLocation;
    }

    /**
     * Force the plank back to the level position.  This is generally done when some sort of support column has been
     * put into place.
     */
    private void forceToLevel() {
        tiltAngle = 0;
        updateShape();
        updateWeightPositions();
    }

    private void stepInTime( double dt ) {
        if ( !supportColumnsActive.get() ) {
            // Update the angular velocity based on the current torque.
            angularVelocity += torqueFromWeights / MOMENT_OF_INERTIA;
        }
        else {
            angularVelocity = 0;
        }
        if ( angularVelocity != 0 ) {
            tiltAngle += angularVelocity * dt;
            if ( Math.abs( tiltAngle ) > maxTiltAngle ) {
                tiltAngle = maxTiltAngle * ( tiltAngle < 0 ? -1 : 1 );
                angularVelocity = 0;
            }
            updateShape();
            updateWeightPositions();
        }
    }

    private void updateShape() {
        if ( !supportColumnsActive.get() ) {
            setShapeProperty( generateShape( positionHandle.getY(), tiltAngle ) );
        }
        else {
            // The support columns are in place, so the plank must be flat.
            setShapeProperty( generateShape( positionHandle.getY(), 0 ) );
        }
    }

    private void updateWeightPositions() {
        for ( Weight weight : weightsOnSurface ) {
            Vector2D vectorToPivotPoint = new Vector2D( positionHandle );
            Vector2D vectorToCenterAbovePivot = new Vector2D( 0, THICKNESS );
            vectorToCenterAbovePivot.rotate( tiltAngle );
            Vector2D vectorToWeight = new Vector2D( mapWeightToDistFromCenter.get( weight ), 0 );
            vectorToWeight.rotate( tiltAngle );
            weight.setRotationAngle( tiltAngle );
            weight.setPosition( vectorToPivotPoint.add( vectorToCenterAbovePivot.add( vectorToWeight ) ).toPoint2D() );
        }
    }

    /**
     * Get the balance point for the plank.  This is the point on which the
     * plank rests and tilts, so it is the underside of the plank, not the
     * top.
     *
     * @return
     */
    public Point2D getBalancePoint() {
        // TODO: This only works when the fulcrum is immobile, and will need to be improved.
        return new Point2D.Double( positionHandle.getX(), positionHandle.getY() );
    }

    private Point2D getSurfacePointAboveBalancePoint() {
        return new Vector2D( getBalancePoint() ).add( new Vector2D( 0, THICKNESS ).rotate( tiltAngle ) ).toPoint2D();
    }

    /**
     * Obtain the Y value for the surface of the plank for the specified X
     * value.
     *
     * @param xValue
     * @return
     */
    private double getSurfaceYValue( double xValue ) {
        // Solve the linear equation for the line that represents the surface
        // of the plank.
        Point2D surfacePointAboveBalancePoint = getSurfacePointAboveBalancePoint();
        double m = Math.atan( tiltAngle );
        double b = surfacePointAboveBalancePoint.getY() - m * surfacePointAboveBalancePoint.getX();
        // Does NOT check if the xValue range is valid.
        return m * xValue + b;
    }

    public boolean isPointAbovePlank( Point2D p ) {
        Rectangle2D plankBounds = getShape().getBounds2D();
        if ( p.getX() >= plankBounds.getMinX() && p.getX() <= plankBounds.getMaxX() && p.getY() > getSurfaceYValue( p.getX() ) ) {
            return true;
        }
        else {
            return false;
        }
    }

    private void updateTorqueDueToWeights() {
        double netTorqueFromWeights = 0;
        for ( Weight weight : weightsOnSurface ) {
            netTorqueFromWeights += -weight.getPosition().getX() * weight.getMass();
        }
        torqueFromWeights = netTorqueFromWeights;
    }

    /**
     * Get a list of the "snap to" locations on the surface of the plank.
     * These locations are the only locations where the weights may ba placed,
     * and locations between these points are not considered valid.  This is
     * done to make it easier to balance things.
     *
     * @return
     */
    private List<Point2D> getSnapToLocations() {
        // Find the point that represents the leftmost edge of the surface.
        ImmutableVector2D vectorFromPivotToCenterSurface = new Vector2D( 0, THICKNESS ).rotate( tiltAngle );
        ImmutableVector2D vectorToLeftSurfaceEdge = new Vector2D( -PIVOT_PT_POS_X, 0 ).rotate( tiltAngle );
        Point2D currentPoint = new Vector2D( getBalancePoint() ).add( vectorFromPivotToCenterSurface ).add( vectorToLeftSurfaceEdge ).toPoint2D();
        // Create a vector for moving one snap-to mark along the plank.
        ImmutableVector2D incrementVector = new Vector2D( LENGTH / ( NUM_SNAP_TO_MARKERS + 1 ), 0 ).rotate( tiltAngle );
        // Create the list of locations by starting at the left edge and
        // stepping to the right edge.
        List<Point2D> snapToLocations = new ArrayList<Point2D>( NUM_SNAP_TO_MARKERS );
        for ( int i = 0; i < NUM_SNAP_TO_MARKERS; i++ ) {
            currentPoint = new Vector2D( currentPoint ).add( incrementVector ).toPoint2D();
            snapToLocations.add( currentPoint );
        }
        return snapToLocations;
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

}
