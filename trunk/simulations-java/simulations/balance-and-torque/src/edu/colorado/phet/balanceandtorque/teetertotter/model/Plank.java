// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
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
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * This is the plank upon which masses can be placed.
 *
 * @author John Blanco
 */
public class Plank extends ShapeModelElement {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    public static final double LENGTH = 4.5;// meters
    public static final double THICKNESS = 0.05; // meters
    private static final double MASS = 100; // kg

    // The number of locations where masses may be placed on the plank.  Only
    // the locations defined be this are valid.
    private static final double INTER_SNAP_TO_MARKER_DISTANCE = 0.25; // meters
    public static final int NUM_SNAP_TO_LOCATIONS = (int) Math.floor( LENGTH / INTER_SNAP_TO_MARKER_DISTANCE - 1 );

    // Moment of inertia.
    // TODO: I'm not certain that this is the correct formula, should check with Mike Dubson.
    private static final double MOMENT_OF_INERTIA = MASS * ( ( LENGTH * LENGTH ) + ( THICKNESS * THICKNESS ) ) / 12;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // The pivot point around which the plank rotates.  This can be changed by
    // the user if the fulcrum is moved.
    private final Point2D pivotPoint = new Point2D.Double();

    // Point where the bottom center of the plank is currently located. When
    // the plank is sitting on top of the fulcrum, this point will be the same
    // as the pivot point.  When the pivot point is above the plank, this will
    // be different.  This is a public property so that it can be monitored
    // externally, since it changes as the plank tilts.
    public final Property<Point2D> bottomCenterPoint = new Property<Point2D>( new Point2D.Double() );

    // Angle of the plank with respect to the ground.  A value of 0 indicates
    // a level plank.  Value is in radians.
    private double tiltAngle = 0;

    // Various variables that need to be retained between time steps in order
    // to calculate the position at the next time step.
    private double currentTorque = 0;
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

    // The original, unrotated shape, which is needed for a number of operations.
    private final Shape unrotatedShape;

    // Shape of the tick marks.  These are calculated here in the model, since
    // their positions correspond to the "snap to" locations, but they are not
    // added to the overall shape so that the view has more freedom to vary
    // their appearance.
    private final List<Shape> tickMarks = new ArrayList<Shape>();

    // Observable list of the force vectors due to the masses on the surface.
    public final ObservableList<MassForceVector> forceVectorList =
            new ObservableList<MassForceVector>();

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    /**
     * Constructor.  Creates the initial shape of the plank.  This assumes that
     * the plank is initially flat and that the pivot point is under the center
     * of the plank.
     *
     * @param clock                - The model clock used to drive
     *                             time-dependent behavior.
     * @param initialLocation      - Initial location of the plank.  This is the
     *                             location of the horizontal center, vertical
     *                             bottom of the plank.
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

        // Listen the our own overall shape and update the tick marks whenever
        // the shape changes.
        addShapeObserver( new VoidFunction1<Shape>() {
            public void apply( Shape shape ) {
                updateTickMarks();
            }
        } );

        // Calculate the max angle at which the plank can tilt before hitting
        // the ground.  NOTE: This assumes a small distance between the pivot
        // point and the bottom of the plank.  If this assumption changes, or
        // if the fulcrum becomes movable, the way this is done will need to
        // change.
        maxTiltAngle = Math.asin( initialLocation.getY() / ( LENGTH / 2 ) );

        // Initialize the attachment point (where the attachment bar meets the
        // plank) which is the same as the initial location.
        bottomCenterPoint.set( new Point2D.Double( initialLocation.getX(), initialLocation.getY() ) );

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
     * Add a mass (e.g. a brick) to the surface of the plank.  If successful,
     * this will set the position and orientation of the mass.  The plank will
     * then continue to control the position and orientation of the mass until
     * the mass is removed from the surface of the plank.
     * <p/>
     * If there is no valid location for this mass on the plank, it will not be
     * added and 'false' will be returned.
     *
     * @param mass
     * @return true if mass was successfully added, false if not (which
     *         generally indicates that no open nearby locations were
     *         available).
     */
    public boolean addMassToSurface( final Mass mass ) {
        boolean massAdded = false;
        Point2D closestOpenLocation = getOpenMassDroppedLocation( mass.getPosition() );
        if ( isPointAbovePlank( mass.getMiddlePoint() ) && closestOpenLocation != null ) {
            massesOnSurface.add( mass );
            mass.setOnPlank( true );
            mass.setPosition( closestOpenLocation );
            double distanceFromCenter = getPlankSurfaceCenter().toPoint2D().distance( mass.getPosition() ) *
                                        ( mass.getPosition().getX() > getPlankSurfaceCenter().getX() ? 1 : -1 );
            mapMassToDistFromCenter.put( mass, distanceFromCenter );
            forceVectorList.add( new MassForceVector( mass ) );
            mass.userControlled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean userControlled ) {
                    if ( userControlled ) {
                        // The user has picked up this mass, so it is no longer
                        // on the surface.
                        removeMassFromSurface( mass );
                        mass.userControlled.removeObserver( this );
                    }
                }
            } );
            updateMassPositions();
            updateTorque();
            massAdded = true;
        }

        return massAdded;
    }

    private void removeMassFromSurface( Mass mass ) {
        massesOnSurface.remove( mass );
        mass.setRotationAngle( 0 );
        mass.setOnPlank( false );
        // Remove the force vector associated with this mass.
        for ( MassForceVector massForceVector : new ArrayList<MassForceVector>( forceVectorList ) ) {
            if ( mass == massForceVector.mass ) {
                forceVectorList.remove( massForceVector );
            }
        }
        updateTorque();
    }

    public void removeAllMasses() {
        for ( Mass mass : new ArrayList<Mass>( massesOnSurface ) ) {
            removeMassFromSurface( mass );
        }
    }

    public Point2D getPivotPoint() {
        return new Point2D.Double( pivotPoint.getX(), pivotPoint.getY() );
    }

    public Shape getUnrotatedShape() {
        return unrotatedShape;
    }

    public double getTiltAngle() {
        return tiltAngle;
    }

    public List<Shape> getTickMarks() {
        return tickMarks;
    }

    // Generate the original shape, which is assumed to be level.  This also
    // creates and adds the "tick marks" to the plank.
    private static Shape generateOriginalShape( Point2D position ) {
        // Create the outline shape of the plank.
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( 0, 0 );
        path.lineTo( LENGTH / 2, 0 );
        path.lineTo( LENGTH / 2, THICKNESS );
        path.lineTo( 0, THICKNESS );
        path.lineTo( -LENGTH / 2, THICKNESS );
        path.lineTo( -LENGTH / 2, 0 );
        path.lineTo( 0, 0 );
        // Translate to the initial position.
        return AffineTransform.getTranslateInstance( position.getX(), position.getY() ).createTransformedShape( path.getGeneralPath() );
    }

    // Updates the positions of the tick marks.
    private void updateTickMarks() {
        tickMarks.clear();
        double interTickMarkDistance = LENGTH / (double) ( NUM_SNAP_TO_LOCATIONS + 1 );
        double plankLeftEdgeX = unrotatedShape.getBounds2D().getMinX() + interTickMarkDistance;
        double tickMarkYPos = unrotatedShape.getBounds2D().getMinY();
        DoubleGeneralPath tickMarkPath = new DoubleGeneralPath();
        AffineTransform transform = AffineTransform.getRotateInstance( tiltAngle, pivotPoint.getX(), pivotPoint.getY() );
        for ( int i = 0; i < NUM_SNAP_TO_LOCATIONS; i++ ) {
            double xPos = plankLeftEdgeX + interTickMarkDistance * i;
            tickMarkPath.moveTo( xPos, tickMarkYPos );
            tickMarkPath.lineTo( xPos, tickMarkYPos + THICKNESS );
            tickMarks.add( transform.createTransformedShape( tickMarkPath.getGeneralPath() ) );
            tickMarkPath.reset();
        }
    }

    // Generate the shape of the plank based on the current state of the
    // internal variables.
    private void updatePlankPosition() {
        // Rotate the base shape to the appropriate angle using the pivot
        // point as the rotational anchor point.
        setShape( AffineTransform.getRotateInstance( tiltAngle, pivotPoint.getX(), pivotPoint.getY() ).createTransformedShape( unrotatedShape ) );
        // Update the attachment point.
        assert ( pivotPoint.getY() >= unrotatedShape.getBounds2D().getMinY() ); // Doesn't handle pivot point below plank.
        Vector2D pivotPointVector = new Vector2D( pivotPoint );
        Vector2D attachmentBarVector = new Vector2D( 0, unrotatedShape.getBounds2D().getY() - pivotPoint.getY() );
        attachmentBarVector.rotate( tiltAngle );
        bottomCenterPoint.set( pivotPointVector.add( attachmentBarVector ).toPoint2D() );
    }

    // Find the best open location for a mass that was dropped at the given
    // point.  Returns null if no nearby open location is available.
    private Point2D getOpenMassDroppedLocation( Point2D p ) {
        Point2D closestOpenLocation = null;
        List<Point2D> candidateOpenLocations = getSnapToLocations();
        if ( NUM_SNAP_TO_LOCATIONS % 2 == 1 ) {
            // Remove the location at the center of the plank from the set of
            // candidates, since we don't want to allow users to place things
            // there.
            candidateOpenLocations.remove( NUM_SNAP_TO_LOCATIONS / 2 );
        }
        // Sort through the locations and eliminate those that are already
        // occupied or too far away.
        for ( Point2D candidateOpenLocation : candidateOpenLocations ) {
            // Must be a reasonable distance away in the horizontal direction
            // so that objects don't appear to fall sideways.
            if ( Math.abs( candidateOpenLocation.getX() - p.getX() ) <= INTER_SNAP_TO_MARKER_DISTANCE ) {
                // This location is a potential candidate.  Is it better than
                // what was already found?
                if ( closestOpenLocation == null || candidateOpenLocation.distance( p ) < closestOpenLocation.distance( p ) ) {
                    closestOpenLocation = candidateOpenLocation;
                }
            }
        }
        return closestOpenLocation;
    }

    /**
     * Force the plank back to the level position.  This is generally done when
     * some sort of support column has been put into place.
     */
    private void forceToLevel() {
        tiltAngle = 0;
        updatePlankPosition();
        updateMassPositions();
    }

    private void stepInTime( double dt ) {
        double angularAcceleration = 0;
        if ( supportColumnsActive.get() ) {
            tiltAngle = 0;
            angularVelocity = 0;
        }
        else {
            updateTorque();
            // Update the angular acceleration and velocity.  There is some
            // thresholding here to prevent the plank from oscillating forever
            // with small values, since this can cause odd-looking movements
            // of the planks and masses.  The thresholds were empirically
            // determined.
            angularAcceleration = currentTorque / MOMENT_OF_INERTIA;
            angularAcceleration = Math.abs( angularAcceleration ) > 0.00001 ? angularAcceleration : 0;
            angularVelocity += angularAcceleration;
            angularVelocity = Math.abs( angularVelocity ) > 0.00001 ? angularVelocity : 0;
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
        // Update the force vectors from the masses.  This mostly just moves
        // them to the correct locations.
        for ( MassForceVector massForceVector : forceVectorList ) {
            massForceVector.update();
        }
        // Simulate friction by slowing down the rotation a little.
        angularVelocity *= 0.90;
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
        return new ImmutableVector2D( bottomCenterPoint.get() ).plus( new ImmutableVector2D( 0, THICKNESS ).getRotatedInstance( tiltAngle ) );
    }

    private ImmutableVector2D getPivotPointVector() {
        return new ImmutableVector2D( pivotPoint );
    }

    public Point2D getCenterSurfacePoint() {
        return new Vector2D( bottomCenterPoint.get() ).add( new Vector2D( 0, THICKNESS ).rotate( tiltAngle ) ).toPoint2D();
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
        Point2D surfacePointAboveBalancePoint = getCenterSurfacePoint();
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

    private void updateTorque() {
        currentTorque = 0;
        // Calculate torque due to masses.
        for ( Mass mass : massesOnSurface ) {
            currentTorque += pivotPoint.getX() - mass.getPosition().getX() * mass.getMass();
        }
        // Add in torque due to plank.
        currentTorque += ( pivotPoint.getX() - bottomCenterPoint.get().getX() ) * MASS;
    }

    /**
     * Get a list of the "snap to" locations on the surface of the plank. These
     * locations are the only locations where the masses may be placed, and
     * locations between these points are not considered valid.  This is done to
     * make it easier to balance things.
     *
     * @return
     */
    private List<Point2D> getSnapToLocations() {
        List<Point2D> snapToLocations = new ArrayList<Point2D>( NUM_SNAP_TO_LOCATIONS );
        AffineTransform rotationTransform = AffineTransform.getRotateInstance( tiltAngle, pivotPoint.getX(), pivotPoint.getY() );
        double unrotatedY = unrotatedShape.getBounds2D().getMaxY();
        double unrotatedMinX = unrotatedShape.getBounds2D().getMinX();
        for ( int i = 0; i < NUM_SNAP_TO_LOCATIONS; i++ ) {
            Point2D unrotatedPoint = new Point2D.Double( unrotatedMinX + ( i + 1 ) * INTER_SNAP_TO_MARKER_DISTANCE, unrotatedY );
            snapToLocations.add( rotationTransform.transform( unrotatedPoint, null ) );
        }

        return snapToLocations;
    }

    /**
     * Get a list of the "snap to" locations on the surface of the plank that
     * where there are no masses currently positioned.
     */
    private List<Point2D> getOpenSnapToLocations() {
        List<Point2D> snapToLocations = getSnapToLocations();
        List<Point2D> openSnapToLocations = new ArrayList<Point2D>( snapToLocations );
        for ( Point2D snapToLocation : snapToLocations ) {
            for ( Mass mass : massesOnSurface ) {
                if ( snapToLocation.distance( mass.getPosition() ) < 0.001 ) {
                    openSnapToLocations.remove( snapToLocation );
                }
            }
        }
        return openSnapToLocations;
    }

    /**
     * Convenience class that maintains relationship between a mass and the
     * force vector associated with it.
     */
    public static class MassForceVector {
        public static final double ACCELERATION_DUE_TO_GRAVITY = -9.8; // meters per second squared.
        public final Mass mass;
        public final Property<PositionedVector> forceVectorProperty;

        public MassForceVector( Mass mass ) {
            this.mass = mass;
            this.forceVectorProperty = new Property<PositionedVector>( generateVector( mass ) );
        }

        public void update() {
            forceVectorProperty.set( generateVector( mass ) );
        }

        private PositionedVector generateVector( Mass mass ) {
            return new PositionedVector( new ImmutableVector2D( mass.getPosition() ),
                                         new ImmutableVector2D( 0, mass.getMass() * ACCELERATION_DUE_TO_GRAVITY ) );
        }
    }

    // TODO: In early August 2011 the design team decided that they didn't like
    // the distance (a.k.a. lever arm) vectors, and they were removed.  Keep
    // this class around for a bit in case that decision is reversed and, if it
    // sticks, blow it away at some point.

    /**
     * Convenience class that maintains relationship between a mass and a lever
     * arm vector.  A lever arm vector starts at the pivot point and goes to the
     * location of the center of gravity of the mass on the surface of the
     * plank.
     */
    public static class LeverArmVector {
        private final Mass mass;
        private final Point2D origin = new Point2D.Double();
        public final Property<PositionedVector> leverArmVectorProperty =
                new Property<PositionedVector>( new PositionedVector( new ImmutableVector2D( 0, 0 ), new ImmutableVector2D( 0, 0 ) ) );

        public LeverArmVector( Point2D pivotPoint, Mass mass ) {
            this.mass = mass;
            origin.setLocation( pivotPoint );
            update();
        }

        public void update() {
            leverArmVectorProperty.set( new PositionedVector( new ImmutableVector2D( origin ),
                                                              new ImmutableVector2D( mass.getPosition().getX() - origin.getX(),
                                                                                     mass.getPosition().getY() - origin.getY() ) ) );
        }
    }
}
