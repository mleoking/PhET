// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.common.model.masses.PositionedVector;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.ModelActions.*;
import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.ModelComponents.plank;
import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.ParameterKeys.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes.modelElement;

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
    private static final double MASS = 75; // kg

    // The number of locations where masses may be placed on the plank.  Only
    // the locations defined be this are valid.
    public static final double INTER_SNAP_TO_MARKER_DISTANCE = 0.25; // meters
    public static final int NUM_SNAP_TO_LOCATIONS = (int) Math.floor( LENGTH / INTER_SNAP_TO_MARKER_DISTANCE - 1 );

    // Moment of inertia.
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
    private double currentNetTorque = 0;
    private double angularVelocity = 0;    // In radians/sec
    private double maxTiltAngle;

    // List of the masses that are resting on the surface of this plank.
    public final ObservableList<Mass> massesOnSurface = new ObservableList<Mass>();

    // Map of masses to distance from the center of the plank.
    private final Map<Mass, Double> mapMassToDistFromCenter = new HashMap<Mass, Double>();

    //Property indicating whether there are 2, 1 or zero support pillars holding up the plank
    //When two columns are active, the plank is forced into a level position regardless of any masses on its surface.
    //When only one column is active, the plank is forced to its maximum tilt
    private final Property<ColumnState> columnState;

    // The original, unrotated shape, which is needed for a number of operations.
    private final Shape unrotatedShape;

    // Shape of the tick marks.  These are calculated here in the model, since
    // their positions correspond to the "snap to" locations, but they are not
    // added to the overall shape so that the view has more freedom to vary
    // their appearance.
    private final List<Shape> tickMarks = new ArrayList<Shape>();

    // Observable list of the force vectors due to the masses on the surface.
    public final ObservableList<MassForceVector> forceVectorList = new ObservableList<MassForceVector>();

    // Property that indicates whether the plank is being manually moved by
    // the user.
    public final BooleanProperty userControlled = new BooleanProperty( false );

    // Boolean that tracks whether the plank is still (i.e. perfectly balanced
    // or prevented from tipping by supports or the ground).
    private boolean isStill = true;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    /**
     * Constructor.  Creates the initial shape of the plank.  This assumes that
     * the plank is initially flat and that the pivot point is under the center
     * of the plank.
     *
     * @param clock             - The model clock used to drive
     *                          time-dependent behavior.
     * @param initialLocation   - Initial location of the plank.  This is the
     *                          location of the horizontal center, vertical
     *                          bottom of the plank.
     * @param initialPivotPoint
     * @param columnState       - Property indicating whether there are 2, 1 or zero support pillars holding up the plank
     */
    public Plank( final ConstantDtClock clock, Point2D initialLocation, Point2D initialPivotPoint, Property<ColumnState> columnState ) {
        super( generateOriginalShape( initialLocation ) );

        this.columnState = columnState;

        pivotPoint.setLocation( initialPivotPoint );
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

        // Listen to the support column property.  The plank goes to the level
        // position whenever there are two columns present, and into a tilted
        // position when only one is present.
        columnState.addObserver( new VoidFunction1<ColumnState>() {
            public void apply( ColumnState columnState ) {
                if ( columnState == ColumnState.SINGLE_COLUMN ) {
                    forceToMaxAndStill();
                }
                else if ( columnState == ColumnState.DOUBLE_COLUMNS ) {
                    forceToLevelAndStill();
                }
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
            mass.setPosition( closestOpenLocation );
            mass.setOnPlank( true );
            final double distanceFromCenter = getPlankSurfaceCenter().toPoint2D().distance( mass.getPosition() ) *
                                              ( mass.getPosition().getX() > getPlankSurfaceCenter().getX() ? 1 : -1 );
            mapMassToDistFromCenter.put( mass, distanceFromCenter );

            // Add the force vector for this mass.
            forceVectorList.add( new MassForceVector( mass ) );

            // Add an observer that will remove this mass when the user picks
            // it up.
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
            massesOnSurface.add( mass );
            updateMassPositions();
            updateNetTorque();
            massAdded = true;

            // Send the sim sharing event indicating that this mass was added
            // to the plank.
            SimSharingManager.sendModelMessage( plank,
                                                modelElement,
                                                massAddedToPlank,
                                                new ParameterSet() {{
                                                    with( new Parameter( massUserComponent, mass.getUserComponent().toString() ) );
                                                    with( new Parameter( massValue, mass.getMass() ) );
                                                    with( new Parameter( distanceFromPlankCenter, distanceFromCenter ) );
                                                }} );
        }

        return massAdded;
    }

    /**
     * Add the given mass to the plank at the given distance from the center
     * of the plank, regardless of the current location of the mass.
     *
     * @param mass
     * @param distanceFromCenter - Distance from the center, positive indicates
     *                           right of center, negative indicates left of center.
     */
    public void addMassToSurface( Mass mass, double distanceFromCenter ) {
        assert distanceFromCenter <= LENGTH / 2;
        if ( distanceFromCenter > LENGTH / 2 ) {
            System.out.println( getClass().getName() + " - Warning: Attempt to add mass at invalid distance from center, ignoring." );
            return;
        }
        ImmutableVector2D vectorToLocation = getPlankSurfaceCenter().getAddedInstance( new ImmutableVector2D( distanceFromCenter, 0 ).getRotatedInstance( tiltAngle ) );
        // Set the position of the mass to be just above the plank at the
        // appropriate distance so that it will drop to the correct place.
        mass.setPosition( vectorToLocation.getX(), vectorToLocation.getY() + 0.01 );
        assert isPointAbovePlank( mass.getPosition() );  // Need to fix this if mass isn't above the surface.
        addMassToSurface( mass );
    }

    private void removeMassFromSurface( final Mass mass ) {
        mapMassToDistFromCenter.remove( mass );
        massesOnSurface.remove( mass );
        mass.setRotationAngle( 0 );
        mass.setOnPlank( false );
        // Remove the force vector associated with this mass.
        for ( MassForceVector massForceVector : new ArrayList<MassForceVector>( forceVectorList ) ) {
            if ( mass == massForceVector.mass ) {
                forceVectorList.remove( massForceVector );
            }
        }
        updateNetTorque();

        // Send the sim sharing event indicating that this mass was added to
        // the plank.
        SimSharingManager.sendModelMessage( plank,
                                            modelElement,
                                            massRemovedFromPlank,
                                            new ParameterSet() {{
                                                with( new Parameter( massUserComponent, mass.getUserComponent().toString() ) );
                                            }} );

    }

    public void removeAllMasses() {
        for ( Mass mass : new ArrayList<Mass>( massesOnSurface ) ) {
            removeMassFromSurface( mass );
        }
    }

    public Point2D getPivotPoint() {
        return new Point2D.Double( pivotPoint.getX(), pivotPoint.getY() );
    }

    public double getTiltAngle() {
        return tiltAngle;
    }

    public void setTiltAngle( double tiltAngle ) {
        // Clamp the tilt angle.
        double clampedTiltAngle = MathUtil.clamp( -maxTiltAngle, tiltAngle, maxTiltAngle );
        // Only allow the tilt angle to be set when columns are not present.
        if ( columnState.get() == ColumnState.NONE && Math.abs( clampedTiltAngle ) <= maxTiltAngle ) {
            this.tiltAngle = clampedTiltAngle;
            updatePlankPosition();
            updateMassPositions();
        }
        else {
            System.out.println( getClass().getName() + " - Warning: Attempt to set tilt angle of plank while columns present, ignoring." );
        }
    }

    public List<Shape> getTickMarks() {
        return tickMarks;
    }

    /**
     * Get a boolean value that indicates whether the specified tick mark is
     * occupied by a mass.
     *
     * @param tickMark
     * @return
     */
    public boolean isTickMarkOccupied( Shape tickMark ) {
        Point2D tickMarkCenter = new Point2D.Double( tickMark.getBounds2D().getCenterX(), tickMark.getBounds2D().getCenterY() );
        double tickMarkDistanceFromCenter = getPlankSurfaceCenter().toPoint2D().distance( tickMarkCenter );
        if ( tickMarkCenter.getX() < getPlankSurfaceCenter().getX() ) {
            tickMarkDistanceFromCenter = -tickMarkDistanceFromCenter;
        }
        // Since the distance is from the center of the plank to the center of
        // the tick mark, there needs to be some tolerance built in to
        // recognizing whether masses are at the same distance.
        double detectionTolerance = THICKNESS;
        boolean massAtThisTickMark = false;
        for ( Mass mass : mapMassToDistFromCenter.keySet() ) {
            double massDistanceFromCenter = mapMassToDistFromCenter.get( mass );
            if ( massDistanceFromCenter > tickMarkDistanceFromCenter - detectionTolerance && massDistanceFromCenter < tickMarkDistanceFromCenter + detectionTolerance ) {
                massAtThisTickMark = true;
                break;
            }
        }
        return massAtThisTickMark;
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
        for ( Point2D candidateOpenLocation : new ArrayList<Point2D>( candidateOpenLocations ) ) {
            for ( Mass mass : massesOnSurface ) {
                if ( mass.getPosition().distance( candidateOpenLocation ) < INTER_SNAP_TO_MARKER_DISTANCE / 10 ) {
                    // This position is already occupied.
                    candidateOpenLocations.remove( candidateOpenLocation );
                }
            }
        }
        // Find the closest of the open locations.
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
    private void forceToLevelAndStill() {
        forceAngle( 0.0 );
    }

    /**
     * Force the plank back to the level position.  This is generally done when
     * some sort of support column has been put into place.
     */
    private void forceToMaxAndStill() {
        forceAngle( getMaxTiltAngle() );
    }

    private void forceAngle( double angle ) {
        angularVelocity = 0;
        tiltAngle = angle;
        updatePlankPosition();
        updateMassPositions();
    }

    private void stepInTime( double dt ) {
        if ( !userControlled.get() ) {
            double angularAcceleration;
            updateNetTorque();

            // Update the angular acceleration and velocity.  There is some
            // thresholding here to prevent the plank from oscillating forever
            // with small values, since this can cause odd-looking movements
            // of the planks and masses.  The thresholds were empirically
            // determined.
            angularAcceleration = currentNetTorque / MOMENT_OF_INERTIA;
            angularAcceleration = Math.abs( angularAcceleration ) > 0.00001 ? angularAcceleration : 0;
            angularVelocity += angularAcceleration;
            angularVelocity = Math.abs( angularVelocity ) > 0.00001 ? angularVelocity : 0;

            // Update the angle of the plank's tilt based on the angular velocity.
            double previousTiltAngle = tiltAngle;
            tiltAngle += angularVelocity * dt;
            if ( Math.abs( tiltAngle ) > maxTiltAngle ) {
                // Limit the angle when one end is touching the ground.
                tiltAngle = maxTiltAngle * ( tiltAngle < 0 ? -1 : 1 );
                angularVelocity = 0;
            }
            else if ( Math.abs( tiltAngle ) < 0.0001 ) {
                // Below a certain threshold just force the tilt angle to be
                // zero so that it appears perfectly level.
                tiltAngle = 0;
            }

            // If the plank just started or stopped tipping, send a message.
            if ( angularVelocity != 0 && isStill ) {
                SimSharingManager.sendModelMessage( plank,
                                                    modelElement,
                                                    startedTilting,
                                                    new ParameterSet( new Parameter( BalanceAndTorqueSimSharing.ParameterKeys.plankTiltAngle, previousTiltAngle ) ) );
                isStill = false;
            }
            else if ( angularVelocity == 0 && !isStill ) {
                SimSharingManager.sendModelMessage( plank,
                                                    modelElement,
                                                    stoppedTilting,
                                                    new ParameterSet( new Parameter( BalanceAndTorqueSimSharing.ParameterKeys.plankTiltAngle, previousTiltAngle ) ) );
                isStill = true;
            }

            // Update the shape of the plank and the positions of the masses on
            // the surface, but only if the tilt angle has changed.
            if ( tiltAngle != previousTiltAngle ) {
                updatePlankPosition();
                updateMassPositions();
            }

            // Simulate friction by slowing down the rotation a little.
            angularVelocity *= 0.91;
        }
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

        // Update the force vectors from the masses.  This mostly just moves
        // them to the correct locations.
        for ( MassForceVector massForceVector : forceVectorList ) {
            massForceVector.update();
        }
    }

    //Determine the absolute position (in meters) of the center surface (top)
    // of the plank
    public ImmutableVector2D getPlankSurfaceCenter() {
        //Start at the absolute location of the attachment point, and add the relative location of the top of the plank, accounting for its rotation angle
        return new ImmutableVector2D( bottomCenterPoint.get() ).plus( new ImmutableVector2D( 0, THICKNESS ).getRotatedInstance( tiltAngle ) );
    }

    private ImmutableVector2D getPivotPointVector() {
        return new ImmutableVector2D( pivotPoint );
    }

    /**
     * Obtain the Y value for the surface of the plank for the specified X
     * value.  Does not check for value x value.
     *
     * @param xValue
     * @return
     */
    public double getSurfaceYValue( double xValue ) {
        // Solve the linear equation for the line that represents the surface
        // of the plank.
        double m = Math.tan( tiltAngle );
        double b = getPlankSurfaceCenter().getY() - m * getPlankSurfaceCenter().getX();
        // Does NOT check if the xValue range is valid.
        return m * xValue + b;
    }

    public boolean isPointAbovePlank( Point2D p ) {
        Rectangle2D plankBounds = getShape().getBounds2D();
        return p.getX() >= plankBounds.getMinX() && p.getX() <= plankBounds.getMaxX() && p.getY() > getSurfaceYValue( p.getX() );
    }

    /**
     * Returns true if the masses and distances on the plank work out such
     * that the plank is balanced, even if it is not yet in the level position.
     * This does NOT pay attention to support columns.
     *
     * @return
     */
    public boolean isBalanced() {
        double unCompensatedTorque = 0;
        for ( Mass mass : massesOnSurface ) {
            assert mapMassToDistFromCenter.containsKey( mass ); // Should never have a mass on the surface with no corresponding distance.
            unCompensatedTorque += mass.getMass() * mapMassToDistFromCenter.get( mass );
        }

        // Account for floating point error, just make sure it is close enough.
        return Math.abs( unCompensatedTorque ) < 1E-6;
    }

    private void updateNetTorque() {
        currentNetTorque = 0;
        if ( columnState.get() == ColumnState.NONE ) {

            // Add the torque due to the masses on the surface of the plank.
            currentNetTorque += getTorqueDueToMasses();

            // Add in torque due to plank.
            currentNetTorque += ( pivotPoint.getX() - bottomCenterPoint.get().getX() ) * MASS;
        }
    }

    public double getTorqueDueToMasses() {
        double torque = 0;
        for ( Mass mass : massesOnSurface ) {
            torque += pivotPoint.getX() - mass.getPosition().getX() * mass.getMass();
        }
        return torque;
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

        public boolean isObfuscated() {
            return mass.isMystery();
        }

        private PositionedVector generateVector( Mass mass ) {
            return new PositionedVector( new ImmutableVector2D( mass.getPosition() ),
                                         new ImmutableVector2D( 0, mass.getMass() * ACCELERATION_DUE_TO_GRAVITY ) );
        }
    }

    public double getMaxTiltAngle() {
        return maxTiltAngle;
    }
}
