// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This is the plank upon which masses can be placed.
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
    public static final double INTER_MARKER_DISTANCE = LENGTH / ( NUM_SNAP_TO_MARKERS + 1 ); // meters
    public static final double MASS = 200; // kg

    // Moment of inertia.
    // TODO: I'm not certain that this is the correct formula, should check with Mike Dubson.
    private static final double MOMENT_OF_INERTIA = MASS * ( ( LENGTH * LENGTH ) + ( THICKNESS * THICKNESS ) ) / 12;

    // The x position (i.e. the position along the horizontal axis) where the
    // pivot point exists.  If and when the fulcrum becomes movable, this will need
    // to become a variable.
    private static final double PIVOT_PT_POS_X = LENGTH / 2;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // The pivot point around which the plank rotates.  This can be changed by
    // the user if the fulcrum is moved.
    private final Point2D pivotPoint = new Point2D.Double();

    // Point at which the bar from the pivot point attaches to the plank. When
    // the plank is sitting on top of the fulcrum, this point will be the same
    // as the pivot point.  When the pivot point is above the plank, this will
    // be different.  This is a public property so that it can be monitored
    // externally, since it changes as the plank tilts.
    public final Property<Point2D> attachmentPointProperty = new Property<Point2D>( new Point2D.Double() );

    // Angle of the plank with respect to the ground.  A value of 0 indicates
    // a level plank.  Value is in radians.
    private double tiltAngle = 0;

    // Various variables that need to be retained between time steps in order
    // to calculate the position at the next time step.
    private double torqueFromMasses = 0;
    private double angularVelocity = 0;    // In radians/sec
    private double maxTiltAngle;

    // List of the masses that are resting on the surface of this plank.
    private final List<Mass> massesOnSurface = new ArrayList<Mass>();

    // Map of masses to distance from the center of the plank.
    private final Map<Mass, Double> mapMassToDistFromCenter = new HashMap<Mass, Double>();

    // Property that indicates whether the support columns are currently
    // active.  When the columns are active, the plank is forced into a level
    // position regardless of any masses on its surface.
    private final BooleanProperty supportColumnsActive;

    private final Shape unrotatedShape;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    /**
     * Constructor.  Creates the initial shape of the plank.  This assumes
     * that the plank is initially flat and that the pivot point is under the
     * center of the plank.
     *
     * @param clock                - The model clock used to drive time-dependent behavior.
     * @param initialLocation      - Initial location of the plank.  This is the
     *                             location of the horizontal center, vertical bottom of the plank.
     * @param initialPivotPoint
     * @param supportColumnsActive - Boolean property that can be monitored
     */
    public Plank( final ConstantDtClock clock, Point2D initialLocation, Point2D initialPivotPoint, BooleanProperty supportColumnsActive ) {
        super( generateOriginalShape( initialLocation ) );
        pivotPoint.setLocation( initialPivotPoint );
        this.supportColumnsActive = supportColumnsActive;
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
        // Keep a copy of the initial, unrotated shape.  This is rotated and
        // translated based on the masses on the plank's surface.
        unrotatedShape = generateOriginalShape( initialLocation );

        // TODO: This will need to work differently once the pivot point is made movable.
        // Formula for this determined with lots of help from Mathematica.
        double d = initialPivotPoint.distance( initialLocation );
        double py = initialPivotPoint.getY();
        double h = LENGTH;
        maxTiltAngle = Math.PI - Math.acos( ( d * py - Math.sqrt( d * d * h * h + Math.pow( h, 4 ) - h * h * py * py ) ) / ( d * d + h * h ) );

        attachmentPointProperty.set( new Point2D.Double( initialLocation.getX(), initialLocation.getY() ) );

        // Listen to the support column property.  The plank goes back to the
        // level position whenever the supports become active.
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
     * Add a mass (e.g. a brick) to the surface of the plank.  This will
     * set the position and orientation of the mass.  The plank will then
     * continue to control the position and orientation of the mass until the
     * mass is removed from the surface of the plank.
     *
     * @param mass
     */
    public void addMassToSurface( final Mass mass ) {
        massesOnSurface.add( mass );
        mass.setOnPlank( true );
        mass.setPosition( getClosestOpenLocation( mass.getPosition() ) );
        double distanceFromCenter = getPlankSurfaceCenter().toPoint2D().distance( mass.getPosition() ) * ( mass.getPosition().getX() > getPlankSurfaceCenter().getX() ? 1 : -1 );
        mapMassToDistFromCenter.put( mass, distanceFromCenter );
        mass.userControlled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean userControlled ) {
                if ( userControlled ) {
                    // The user has picked up this mass, so it is no longer
                    // on the surface.
                    removeMassFromSurface( mass );
                }
            }
        } );
        updateMassPositions();
        updateTorqueDueToMasses();
    }

    private void removeMassFromSurface( Mass mass ) {
        massesOnSurface.remove( mass );
        mass.setRotationAngle( 0 );
        mass.setOnPlank( false );
        updateTorqueDueToMasses();
    }

    public void removeAllMasses() {
        for ( Mass mass : new ArrayList<Mass>( massesOnSurface ) ) {
            removeMassFromSurface( mass );
        }
    }

    public Point2D getPivotPoint() {
        return new Point2D.Double( pivotPoint.getX(), pivotPoint.getY() );
    }

    // Generate the original shape, which is assumed to be level.  This also
    // creates and adds the "tick marks" to the plank.
    private static Shape generateOriginalShape( Point2D position ) {
        // Create the outline shape of the plank.
        GeneralPath path = new GeneralPath();
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
        // Translate to the initial position.
        return AffineTransform.getTranslateInstance( position.getX(), position.getY() ).createTransformedShape( path );
    }

    // Generate the shape of the plank based on the current state of the
    // internal variables.
    private void updatePlankPosition() {
        // Rotate the base shape to the appropriate angle using the pivot
        // point as the rotational anchor point.
        getShapeProperty().set( AffineTransform.getRotateInstance( tiltAngle, pivotPoint.getX(), pivotPoint.getY() ).createTransformedShape( unrotatedShape ) );
        // Update the attachment point.
        assert ( pivotPoint.getY() >= unrotatedShape.getBounds2D().getMinY() ); // Doesn't handle pivot point below plank.
        Vector2D pivotPointVector = new Vector2D( pivotPoint );
        Vector2D attachmentBarVector = new Vector2D( 0, unrotatedShape.getBounds2D().getY() - pivotPoint.getY() );
        attachmentBarVector.rotate( tiltAngle );
        attachmentPointProperty.set( pivotPointVector.add( attachmentBarVector ).toPoint2D() );
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
        updatePlankPosition();
        updateMassPositions();
    }

    private void stepInTime( double dt ) {
        if ( supportColumnsActive.get() ) {
            tiltAngle = 0;
            angularVelocity = 0;
        }
        else {
            updateTorqueDueToMasses();
            // Update the angular velocity based on the current torque.
            angularVelocity += torqueFromMasses / MOMENT_OF_INERTIA;
        }
        // Update the angle of the plank's tilt based on the angular velocity.
        if ( angularVelocity != 0 ) {
            tiltAngle += angularVelocity * dt;
            if ( Math.abs( tiltAngle ) > maxTiltAngle ) {
                // Limit the angle when once end of the plank is touching the ground.
                tiltAngle = maxTiltAngle * ( tiltAngle < 0 ? -1 : 1 );
                angularVelocity = 0;
            }
            updatePlankPosition();
            updateMassPositions();
        }
        // Simulate friction by slowing down the rotation a little.
        angularVelocity *= 0.98;
    }

    private void updateMassPositions() {
        for ( Mass mass : massesOnSurface ) {
            // Compute the vector from the center of the plank's surface to
            // the center of the mass, in meters.
            ImmutableVector2D vectorFromCenterToMass = new Vector2D( mapMassToDistFromCenter.get( mass ), 0 ).getRotatedInstance( tiltAngle );

            // Now add the location of the center of the plank's surface to
            // find the absolute location of the mass.
            ImmutableVector2D massPosition = getPlankSurfaceCenter().plus( vectorFromCenterToMass );

            // Set the position and rotation of the mass.
            mass.setPosition( massPosition.toPoint2D() );
            mass.setRotationAngle( tiltAngle );
        }
    }

    //Determine the absolute position (in meters) of the surface (top) of the plank
    private ImmutableVector2D getPlankSurfaceCenter() {

        //Start at the absolute location of the attachment point, and add the relative location of the top of the plank, accounting for its rotation angle
        return new ImmutableVector2D( attachmentPointProperty.get() ).plus( new ImmutableVector2D( 0, THICKNESS ).getRotatedInstance( tiltAngle ) );
    }

    private ImmutableVector2D getPivotPointVector() {
        return new ImmutableVector2D( pivotPoint );
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

    private void updateTorqueDueToMasses() {
        double netTorqueFromMasses = 0;
        for ( Mass mass : massesOnSurface ) {
            netTorqueFromMasses += -mass.getPosition().getX() * mass.getMass();
        }
        torqueFromMasses = netTorqueFromMasses;
    }

    /**
     * Get a list of the "snap to" locations on the surface of the plank.
     * These locations are the only locations where the masses may ba placed,
     * and locations between these points are not considered valid.  This is
     * done to make it easier to balance things.
     *
     * @return
     */
    private List<Point2D> getSnapToLocations() {
        List<Point2D> snapToLocations = new ArrayList<Point2D>( NUM_SNAP_TO_MARKERS );
        AffineTransform rotationTransform = AffineTransform.getRotateInstance( tiltAngle, pivotPoint.getX(), pivotPoint.getY() );
        double unrotatedY = unrotatedShape.getBounds2D().getMaxY();
        double unrotatedMinX = unrotatedShape.getBounds2D().getMinX();
        for ( int i = 0; i < NUM_SNAP_TO_MARKERS; i++ ) {
            Point2D unrotatedPoint = new Point2D.Double( unrotatedMinX + ( i + 1 ) * INTER_MARKER_DISTANCE, unrotatedY );
            snapToLocations.add( rotationTransform.transform( unrotatedPoint, null ) );
        }

        return snapToLocations;
    }
}
