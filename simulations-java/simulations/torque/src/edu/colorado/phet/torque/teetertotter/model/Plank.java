// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.torque.teetertotter.model.weights.Weight;

/**
 * This is the plank upon which weights can be placed.
 *
 * @author John Blanco
 */
public class Plank extends ModelObject {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    public static final double LENGTH = 4;// meters
    public static final double THICKNESS = 0.05; // meters
    public static final int NUM_SNAP_TO_MARKERS = 19;
    public static final double MASS = 200; // kg

    // Moment of inertia.
    // TODO: I'm not certain that this is the correct formula, should check with Mike Dubson.
    public static final double MOMENT_OF_INERTIA = MASS * ( ( LENGTH * LENGTH ) + ( THICKNESS * THICKNESS ) ) / 12;

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
                tiltAngle = 0; // Force the plank to be level.
                updateShape();
                updateWeightPositions();
            }
        }
        );
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public static double getLength() {
        return LENGTH;
    }

    /**
     * Add a weight (e.g. a brick) to the surface of the plank.
     *
     * @param weight
     */
    public void addWeightToSurface( final Weight weight ) {
        weightsOnSurface.add( weight );
        mapWeightToDistFromCenter.put( weight, weight.getPosition().getX() - positionHandle.getX() );
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
        // Rotate the appropriate amount.
        Shape shape = AffineTransform.getRotateInstance( tiltAngle ).createTransformedShape( path );
        // Translate to the appropriate height.
        shape = AffineTransform.getTranslateInstance( 0, centerHeight ).createTransformedShape( shape );

        return shape;
    }

    public Point2D getClosestOpenLocation( Point2D p ) {
        // TODO: Doesn't actually give open locations yet, just valid snap-to ones.
        // TODO: Doesn't handle rotation yet.
        double minX = getShape().getBounds2D().getMinX();
        double maxX = getShape().getBounds2D().getMaxX();
        double increment = getShape().getBounds2D().getWidth() / ( NUM_SNAP_TO_MARKERS + 1 );
        double xPos = 0;
        if ( p.getX() > maxX - increment ) {
            xPos = maxX - increment;
        }
        else if ( xPos < minX + increment ) {
            xPos = minX + increment;
        }
        else {
            int numLengths = (int) Math.round( ( p.getX() - minX ) / increment );
            xPos = minX + numLengths * increment;
        }
        return new Point2D.Double( xPos, getShape().getBounds2D().getMaxY() );
    }

    /**
     * Force the plank back to the level position.  This is generally done when some sort of support column has been
     * put into place.
     */
    public void forceToLevel() {
        tiltAngle = 0;
        updateShape();
        updateWeightPositions();
    }

    public void setTorqueFromWeights( double torque ) {
        torqueFromWeights = torque;
    }

    public void stepInTime( double dt ) {
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
            weight.setPosition( vectorToPivotPoint.add( vectorToCenterAbovePivot.add( vectorToWeight ) ).toPoint2D() );
            weight.setRotationAngle( tiltAngle );
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
        // TODO: This only works when the fulcrum is immobile, and will need
        // to be improved.
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
    public double getSurfaceYValue( double xValue ) {
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

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

}
