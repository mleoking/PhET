// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * Glacier is the model of the glacier.
 * The model was developed by Archie Paulson.
 * The model is a not physical; it is a "Hollywood" model that approximates published data.
 * Documentation is in glaciers/doc/model.txt.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Glacier extends ClockAdapter {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // *_M* constants refer to slopes of data fit
    // *_B* constants refer to y-axis intercept of data fit

    private static final double DX = 80; // distance between x-axis sample points (meters)
    private static final double ELA_EQUALITY_THRESHOLD = 0.1; // ELAs are considered equal if they are at least this close (meters)
    private static final double U_SLIDE = 20; // downvalley ice speed (meters/year)
    private static final double U_DEFORM = 20; // contribution of vertical deformation to ice speed (meters/year)

    private static final double MIN_TIMESCALE = 50; // min value for evolution timescale
    private static final double MAX_TIMESCALE = 200; // max value for evolution timescale

    private static final double ELAX_B0 = 157076;
    private static final double ELAX_M0 = -37.57;
    private static final double ELAX_M2 = -3.12489;
    private static final double ELAX_B2 = 14118;
    private static final double ELAX_X1 = 3800;
    private static final double ELAX_X2 = 4150;
    private static final double ELAX_Y1 = 14300;
    private static final double ELAX_C3 = -0.098411;
    private static final double ELAX_C4 = 0.000281185;
    private static final double ELAX_TERMINUS = 0.6;
    private static final double MAX_THICKNESS_SCALE = 2.3;
    private static final double Q_ADVANCE_LIMIT = -2; // meters/year, a limit on the advance speed of the qela
    private static final double Q_RETREAT_LIMIT = 4; // meters/year, a limit on the retreat speed of the qela
    private static final double DEFAULT_ACCELERATION_M = ( ( ELAX_M0 / ELAX_M2 ) - 1 ) / ( ELAX_X2 - ELAX_X1 );
    private static final double DEFAULT_ACCELERATION_B = 1 - ( DEFAULT_ACCELERATION_M * ELAX_X1 );

    private static final double SURFACE_ELA_SEARCH_DX = 1; // meters
    private static final double SURFACE_ELA_EQUALITY_THRESHOLD = 1; // meters

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final Valley _valley;
    private final Climate _climate;

    private final ClimateListener _climateListener;
    private final ArrayList _listeners; // list of GlacierListener

    private double _qela; // like the ELA, except is describes the state of the glacier's evolution, not the climate, never greater than top of headwall (meters)
    private double _qelax; // the x-position where the quasi-ELA intersects the ice surface, never less than zero (meters)
    private double _glacierLength; // length of the ice (meters)
    private double _maxThickness; // maximum ice thickness (meters)
    private double _averageIceThicknessSquares; // average of the squares of the non-zero ice thickness samples
    private boolean _steadyState; // is the glacier in the steady state?
    private final Point2D _terminus; /// point at the terminus (downvalley end)
    private Point2D _surfaceAtELA; // point where the ELA intersects the ice surface, null if ELA is below the terminus or above the headwall
    private double _accelerationM = DEFAULT_ACCELERATION_M;
    private double _accelerationB = DEFAULT_ACCELERATION_B;

    private final EvolutionState _evolutionState = new EvolutionState(); // for debugging purposes

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public Glacier( Valley valley, Climate climate ) {
        super();

        _valley = valley;

        /*
        * I did my best to make this model independent of the headwall position,
        * by many parts of the model that I was given require x=0.
        */
        assert ( _valley.getHeadwallPositionReference().getX() == 0 );

        _climate = climate;
        _climateListener = new ClimateListener() {

            public void temperatureChanged() {
                handleClimateChange();
            }

            public void snowfallChanged() {
                handleClimateChange();
            }
        };
        _climate.addClimateListener( _climateListener );

        _listeners = new ArrayList();

        _terminus = new Point2D.Double();
        _surfaceAtELA = null; // will be allocated as needed

        setSteadyState();
    }

    public void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }

    //----------------------------------------------------------------------------
    // Climate change handler
    //----------------------------------------------------------------------------

    /*
    * When the climate changes, the glacier is no longer in steady state.
    */
    private void handleClimateChange() {
        updateIceThickness();
        if ( _steadyState ) {
            _steadyState = false;
            notifySteadyStateChanged();
        }
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    /**
     * Gets the valley that the glacier forms in.
     *
     * @return Valley
     */
    public Valley getValley() {
        return _valley;
    }

    /**
     * Gets the climate.
     *
     * @return Climate
     */
    public Climate getClimate() {
        return _climate;
    }

    /**
     * Is the glacier currently in the steady state?
     *
     * @return true or false
     */
    public boolean isSteadyState() {
        return _steadyState;
    }

    /**
     * Sets the glacier to the steady state.
     *
     * @param steadyState
     */
    public void setSteadyState() {
        if ( !_steadyState ) {
            final double maxElevation = _valley.getMaxElevation();
            _qela = Math.min( _climate.getELA(), maxElevation );
            updateIceThickness();
            _steadyState = true;
            notifySteadyStateChanged();
            assert ( _qela <= maxElevation );
        }
    }

    /**
     * Gets the length of the glacier.
     *
     * @return length in meters
     */
    public double getLength() {
        return _glacierLength;
    }

    /**
     * Convenience method for getting the headwall position.
     *
     * @return Point2D
     */
    public Point2D getHeadwallPositionReference() {
        return _valley.getHeadwallPositionReference();
    }

    public double getHeadwallX() {
        return _valley.getHeadwallPositionReference().getX();
    }

    public double getHeadwallY() {
        return _valley.getHeadwallPositionReference().getY();
    }

    /**
     * Gets a reference to the terminus position.
     * If the glacier has zero length, this is the same as the headwall position.
     *
     * @return
     */
    public Point2D getTerminusPositionReference() {
        return _terminus;
    }

    public double getTerminusX() {
        return _terminus.getX();
    }

    public double getTerminusY() {
        return _terminus.getY();
    }

    /**
     * Gets a reference to the point where the ELA intersects the surface of the ice.
     * This will be null if the terminus is above the ELA.
     *
     * @return
     */
    public Point2D getSurfaceAtELAReference() {
        return _surfaceAtELA;
    }

    /**
     * Gets the elevation at some point on the glacier's surface.
     * At points where the glacier thickness is zero, this is the same as
     * the elevation of the valley floor.
     *
     * @param x
     * @return
     */
    public double getSurfaceElevation( double x ) {
        return _valley.getElevation( x ) + getIceThickness( x );
    }

    public void debug_setAccelerationM( double accelerationM ) {
        System.out.println( "Glacier.debug_setAccelerationM " + accelerationM );
        _accelerationM = accelerationM;
    }

    public double debug_getAccelerationM() {
        return _accelerationM;
    }

    public void debug_setAccelerationB( double accelerationB ) {
        System.out.println( "Glacier.debug_setAccelerationB " + accelerationB );
        _accelerationB = accelerationB;
    }

    public double debug_getAccelerationB() {
        return _accelerationB;
    }

    //----------------------------------------------------------------------------
    // Ice Thickness model
    //----------------------------------------------------------------------------

    /**
     * Gets the ice thickness at an x coordinate.
     *
     * @param x
     * @return meters
     */
    public double getIceThickness( final double x ) {

        double thickness = 0;
        final double headwallX = _valley.getHeadwallPositionReference().getX();

        if ( x > headwallX && x < _terminus.getX() ) {

            final double xPeak = headwallX + ( 0.5 * _glacierLength ); // midpoint of the ice
            final double p = Math.max( 42 - ( 0.01 * _qela ), 1.5 );
            final double r = 1.5 * xPeak;
            final double xPeakPow = Math.pow( xPeak, p );

            if ( x < xPeak ) {
                thickness = Math.sqrt( ( r * r ) - ( ( x - xPeak ) * ( x - xPeak ) ) ) * ( _maxThickness / r );
                thickness *= ( xPeakPow - Math.pow( Math.abs( x - xPeak ), p ) ) / xPeakPow;
            }
            else if ( x < _terminus.getX() ) {
                thickness = Math.sqrt( ( xPeak * xPeak ) - ( ( x - xPeak ) * ( x - xPeak ) ) ) * ( _maxThickness / xPeak );
            }
        }
        assert ( thickness >= 0 );

        return thickness;
    }

    /*
    * Updates ice thickness parameters to match the current climate.
    */
    private void updateIceThickness() {

        _surfaceAtELA = null;
        _averageIceThicknessSquares = 0;

        // constants used to compute ice thickness
        _qelax = computeQuasiELAX( _qela );
        _glacierLength = _qelax / ELAX_TERMINUS;
        assert ( _glacierLength >= 0 );
        _maxThickness = MAX_THICKNESS_SCALE * Math.sqrt( _qelax );
        assert ( _maxThickness >= 0 );

        if ( _glacierLength == 0 ) {
            _terminus.setLocation( _valley.getHeadwallPositionReference() );
        }
        else {
            // constants
            final double ela = _climate.getELA();
            final double headwallX = _valley.getHeadwallPositionReference().getX();
            final double headwallY = _valley.getHeadwallPositionReference().getY();
            final double maxElevation = _valley.getMaxElevation();

            // terminus
            final double terminusX = headwallX + _glacierLength;
            final double terminusY = _valley.getElevation( terminusX );
            _terminus.setLocation( terminusX, terminusY );
            assert ( _terminus.getX() >= headwallX );
            assert ( _terminus.getY() <= headwallY );

            // initialize variables
            double x = headwallX + DX;
            double thickness = 0;
            double sumOfNonZeroSquares = 0;
            double countOfNonZeroSquares = 0;
            double surfaceElevation = 0;
            boolean done = false;

            // Compute average ice thickness squares & intersection of ELA with surface.
            // There is some error here if glacier length is not an integer multiple of DX,
            // since we may miss a bit of ice near the terminus.
            while ( !done ) {

                // compute thickness
                thickness = getIceThickness( x );

                if ( thickness == 0 ) {
                    done = true;
                }
                else {
                    // accumulate squares
                    sumOfNonZeroSquares += ( thickness * thickness );
                    countOfNonZeroSquares++;

                    // look for the place where the ELA intersects the ice surface
                    if ( _surfaceAtELA == null && ela < maxElevation ) {
                        surfaceElevation = _valley.getElevation( x ) + thickness;
                        if ( surfaceElevation <= ela ) {
                            // search between previous and current samples
                            _surfaceAtELA = findSurfaceAtELA( ela, x - DX, x );
                        }
                    }

                    x += DX;
                }
            }

            // If the ELA intersect the ice surface close to the terminus, we may miss it above.
            // Look backwards from the terminus to find it.
            if ( _surfaceAtELA == null && ela < maxElevation && terminusY < ela ) {
                _surfaceAtELA = findSurfaceAtELA( ela, terminusX - DX, terminusX );
            }

            // compute average
            if ( countOfNonZeroSquares > 0 ) {
                _averageIceThicknessSquares = sumOfNonZeroSquares / countOfNonZeroSquares;
            }
            assert ( _averageIceThicknessSquares >= 0 );
        }

        notifyIceThicknessChanged();
    }

    /*
    * Computes the x-position of the intersection of the glacier surface and the quasi-ELA.
    */
    private static double computeQuasiELAX( double quasiELA ) {
        double qelax;
        if ( quasiELA < ELAX_X1 ) {
            qelax = ( quasiELA * ELAX_M0 ) + ELAX_B0;
        }
        else if ( quasiELA < ELAX_X2 ) {
            final double x = quasiELA - ELAX_X1;
            qelax = ELAX_Y1 + ( ELAX_M0 * x ) + ( ELAX_C3 * x * x ) + ( ELAX_C4 * x * x * x );
        }
        else {
            qelax = ( quasiELA * ELAX_M2 ) + ELAX_B2;
        }

        if ( qelax < 0 ) {
            qelax = 0;
        }

        assert ( qelax >= 0 );
        return qelax;
    }

    /*
    * Finds the point where the ELA intersects the surface of the glacier.
    * This method is specifically for use by updateIceThickness, and should not be
    * called unless you are certain that the point you're looking for is somewhere
    * between startX and endX.
    *
    * @param ela the ELA we're looking for
    * @param startX start searching here
    * @param endX stop searching here
    */
    private Point2D findSurfaceAtELA( double ela, double startX, double endX ) {
        assert ( startX < endX );

        Point2D p = null;
        double currentSurfaceElevation;
        double currentDiff;
        double previousSurfaceElevation = -1;
        double previousDiff = -1;

        for ( double x = startX; x <= endX && p == null; x += SURFACE_ELA_SEARCH_DX ) {

            currentSurfaceElevation = getSurfaceElevation( x );
            currentDiff = currentSurfaceElevation - ela;

            if ( Math.abs( currentDiff ) <= SURFACE_ELA_EQUALITY_THRESHOLD ) {
                // current sample is close enough
                p = new Point2D.Double( x, currentSurfaceElevation );
            }
            else if ( currentDiff < 0 ) {
                // we went too far, use closest of current and previous samples
                if ( previousSurfaceElevation == -1 ) {
                    // there is no previous, use current
                    p = new Point2D.Double( x, currentSurfaceElevation );
                }
                else if ( currentDiff < previousDiff ) {
                    // current is closer than previous
                    p = new Point2D.Double( x, currentSurfaceElevation );
                }
                else {
                    // previous is closer than current
                    p = new Point2D.Double( x, previousSurfaceElevation );
                }
            }

            previousSurfaceElevation = currentSurfaceElevation;
            previousDiff = currentDiff;
        }

        assert ( p != null );
        return p;
    }

    //----------------------------------------------------------------------------
    // Ice Velocity model
    //----------------------------------------------------------------------------

    /**
     * Gets the ice velocity at a point in the ice.
     * If the point is outside the ice, a zero vector is returned.
     * <p/>
     * Magnitude of the velocity vector is determined by the ice speed model herein.
     * Direction corresponds to the slope of the valley floor.
     *
     * @param x            meters
     * @param elevation    meters
     * @param outputVector
     * @return Vector2D, components in meters/year
     */
    public MutableVector2D getIceVelocity( final double x, final double elevation, final MutableVector2D outputVector ) {
        final double magnitude = getIceSpeed( x, elevation );
        final double direction = _valley.getDirection( x, x + DX );
        final double xComponent = PolarCartesianConverter.getX( magnitude, direction );
        final double yComponent = PolarCartesianConverter.getY( magnitude, direction );
        outputVector.setComponents( xComponent, yComponent );
        return outputVector;
    }

    /**
     * Gets the ice velocity at a point in the ice.
     * See getIceVelocity.
     *
     * @param x
     * @param elevation
     * @return
     */
    public MutableVector2D getIceVelocity( final double x, final double elevation ) {
        return getIceVelocity( x, elevation, new MutableVector2D() /* outputVector */ );
    }

    /*
    * Gets the steady-state ice speed at a point in the ice.
    * If the point is outside the ice, zero is returned.
    *
    * @param x meters
    * @param elevation meters
    * @return meters/year
    */
    private double getIceSpeed( final double x, final double elevation ) {
        double iceSpeed = 0;
        final double iceThickness = getIceThickness( x );
        if ( iceThickness > 0 ) {
            final double valleyElevation = _valley.getElevation( x );
            final double iceSurfaceElevation = valleyElevation + iceThickness;
            // if the elevation is in the ice...
            if ( elevation >= valleyElevation && elevation <= iceSurfaceElevation ) {
                // zz varies linearly from 0 at the valley floor (rock-ice interface) to 1 at the ice surface (air-ice interface)
                final double zz = ( elevation - valleyElevation ) / iceThickness;
                final double u_deform_ave = computeVerticallyAveragedDeformationIceSpeed( iceThickness, _averageIceThicknessSquares );
                iceSpeed = U_SLIDE + ( u_deform_ave * 5. * ( zz - ( 1.5 * zz * zz ) + ( zz * zz * zz ) - ( 0.25 * zz * zz * zz * zz ) ) );
            }
        }
        assert ( iceSpeed >= 0 );
        return iceSpeed;
    }

    /*
    * Gets the vertically-averaged deformation ice speed.
    * Speed of ice moving downvalley is affected by vertical deformation,
    * and this method calculation the deformation contribution.
    * (symbol: u_deform_ave)
    *
    * @param iceThickness ice thickness, meters
    * @param averageIceThicknessSquares average of ice thickness squares, meters^2
    * @return meters/year
    */
    private static double computeVerticallyAveragedDeformationIceSpeed( final double iceThickness, final double averageIceThicknessSquares ) {
        double avg = 0;
        if ( averageIceThicknessSquares != 0 ) {
            avg = ( iceThickness * iceThickness ) * U_DEFORM / averageIceThicknessSquares;
        }
        assert ( avg >= 0 );
        return avg;
    }

    //----------------------------------------------------------------------------
    //  Timescale model
    //----------------------------------------------------------------------------

    public EvolutionState debug_getEvolutionState() {
        return _evolutionState;
    }

    /*
    * When the clock ticks, evolve the model.
    */
    public void clockTicked( ClockEvent event ) {

        if ( !isSteadyState() ) {

            final double dt = event.getSimulationTimeChange();
            final double ela = _climate.getELA();
            final double timescale = getTimescale();
            final double maxElevation = _valley.getMaxElevation();
            final double previousLength = _glacierLength;

            // calculate the delta
            double deltaQela = ( ela - _qela ) * ( 1 - Math.exp( -dt / timescale ) );

            // limit the delta, depending on whether the glacier is advancing or retreating
            if ( ela < _qela ) {
                deltaQela = Math.max( deltaQela, dt * Q_ADVANCE_LIMIT );
            }
            else {
                deltaQela = Math.min( deltaQela, dt * Q_RETREAT_LIMIT );
            }

            // apply acceleration near the headwall if needed
            if ( _qela > ELAX_X2 ) {
                deltaQela = deltaQela * ELAX_M0 / ELAX_M2;
            }
            else if ( _qela > ELAX_X1 ) {
                deltaQela = deltaQela * ( ( _qela * _accelerationM ) + _accelerationB );
            }

            // move the quasi-ELA
            _qela += deltaQela;

            // make adjustments
            if ( _qela > maxElevation ) {
                _qela = maxElevation;
                if ( ela > maxElevation ) {
                    setSteadyState();
                }
            }
            else if ( Math.abs( ela - _qela ) <= ELA_EQUALITY_THRESHOLD ) {
                // are we close enough to steady state?
                setSteadyState();
            }
            else {
                // not in steady state, update the ice thickness
                updateIceThickness();
            }

            // update the state data structure, for debugging
            {
                // values below here are set explicitly in this method
                _evolutionState.ela = ela;
                _evolutionState.timescale = timescale;
                _evolutionState.deltaQela = deltaQela;
                _evolutionState.qela = _qela;
                // values below here are set by updateIceThickness, called from this method
                _evolutionState.qelax = _qelax;
                _evolutionState.glacierLength = _glacierLength;
                _evolutionState.deltaLength = _glacierLength - previousLength;
                _evolutionState.terminus.setLocation( _terminus );
            }

            assert ( _qela <= maxElevation );
        }
    }

    /* 
    * Gets the timescale for evolving the ice.
    *
    * @return timescale (units?)
    */
    private double getTimescale() {

        double timescale = ( -0.22 * _climate.getELA() ) + 1026;

        // limit range
        if ( timescale < MIN_TIMESCALE ) {
            timescale = MIN_TIMESCALE;
        }
        else if ( timescale > MAX_TIMESCALE ) {
            timescale = MAX_TIMESCALE;
        }

        return timescale;
    }

    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------

    public interface GlacierListener {
        public void iceThicknessChanged();

        public void steadyStateChanged();
    }

    public static class GlacierAdapter implements GlacierListener {
        public void iceThicknessChanged() {}

        public void steadyStateChanged() {}
    }

    public void addGlacierListener( GlacierListener listener ) {
        _listeners.add( listener );
    }

    public void removeGlacierListener( GlacierListener listener ) {
        _listeners.remove( listener );
    }

    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------

    private void notifyIceThicknessChanged() {
        ArrayList listenersCopy = new ArrayList( _listeners ); // iterate on a copy to avoid ConcurrentModificationException
        Iterator i = listenersCopy.iterator();
        while ( i.hasNext() ) {
            ( (GlacierListener) i.next() ).iceThicknessChanged();
        }
    }

    private void notifySteadyStateChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GlacierListener) i.next() ).steadyStateChanged();
        }
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    // current values of variables used to evolve the glacier
    public static class EvolutionState {

        public double ela;
        public double timescale;
        public double deltaQela;
        public double qela;
        public double qelax;
        public double glacierLength;
        public double deltaLength;
        public Point2D terminus = new Point2D.Double();

        public EvolutionState() {}

        public void setState( EvolutionState es ) {
            ela = es.ela;
            timescale = es.timescale;
            deltaQela = es.deltaQela;
            qela = es.qela;
            qelax = es.qelax;
            glacierLength = es.glacierLength;
            deltaLength = es.deltaLength;
            terminus.setLocation( es.terminus );
        }
    }
}
