/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * Glacier is the model of the glacier.
 * The model was developed by Archie Paulson.
 * The model is a not physical; it is a "Hollywood" model that approximates published data.
 * This implementation is based on the model written in Python that can be found in glaciers/python/.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Glacier extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DX = 80; // distance between x-axis sample points (meters)
    private static final double ELA_EQUALITY_THRESHOLD = 1; // ELAs are considered equal if they are at least this close (meters)
    private static final double U_SLIDE = 20; // downvalley ice speed (meters/year)
    private static final double U_DEFORM = 20; // contribution of vertical deformation to ice speed (meters/year)
    
    private static final double MIN_TIMESCALE = 50; // min value for evolution timescale
    private static final double MAX_TIMESCALE = 300; // max value for evolution timescale
    
    private static final double ELAX_TERMINUS = 0.6;
    private static final double ELAX_B0 = 138248;
    private static final double MAX_THICKNESS_SCALE = 2.3;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Valley _valley;
    private final Climate _climate;
    
    private final ClimateListener _climateListener;
    private final ArrayList _listeners; // list of GlacierListener
    
    private double _quasiELA; // like the ELA, except is describes the state of the glacier's evolution, not the climate (meters)
    private final double _elax_m0;
    private double _averageIceThicknessSquares; // average of the squares of the non-zero ice thickness samples
    private boolean _steadyState; // is the glacier in the steady state?
    private double[] _iceThicknessSamples; // ice thickness at t=now (meters)
    
    private final Point2D _terminus; /// point at the terminus (downvalley end)
    private Point2D _surfaceAtELA; // point where the steady state ELA intersects the ice surface
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Glacier( Valley valley, Climate climate ) {
        super();
        
        _valley = valley;
        _elax_m0 = -55630 / ( valley.getMaxElevation() - 2.7E3 );
        
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
     * Gets the spacing used between x-axis sample points.
     * This is the optimal dx to use when requesting ice thickness values.
     * 
     * @return meters
     */
    public static double getDx() {
        return DX;
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
            _quasiELA = _climate.getELA();
            updateIce();
            _steadyState = true;
            notifySteadyStateChanged();
        }
    }
    
    /**
     * Gets the length of the glacier.
     * 
     * @return length in meters
     */
    public double getLength() {
        return getTerminusX() - getHeadwallX();
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
    
    //----------------------------------------------------------------------------
    // Ice Thickness model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the ice thickness at an x coordinate.
     * <p>
     * This method provides an approximate result, the accuracy of
     * which is dependent on DX, the spacing between sample points.
     * The x value specified will fall between 2 ice thickness samples.
     * Locate the 2 samples, and do a linear interpolation between them 
     * to determine the approximate ice thickness.
     * 
     * @param x
     * @return meters
     */
    public double getIceThickness( final double x ) {
        double iceThickness = 0;
        if ( _iceThicknessSamples != null ) {
            final double headwallX = _valley.getHeadwallPositionReference().getX();
            final double xTerminus = _terminus.getX();
            if ( x >= headwallX && x <= xTerminus ) {
                if ( x == xTerminus ) {
                    iceThickness = _iceThicknessSamples[_iceThicknessSamples.length - 1];
                }
                else {
                    int index = (int) ( ( x - headwallX ) / DX );
                    double x1 = headwallX + ( index * DX );
                    double t1 = _iceThicknessSamples[index];
                    double t2 = _iceThicknessSamples[index + 1];
                    iceThickness = t1 + ( ( ( x - x1 ) / DX ) * ( t2 - t1 ) ); // linear interpolation
                }
            }
        }
        assert ( iceThickness >= 0 );
        return iceThickness;
    }
    
    /*
     * Updates the ice for the current ELA.
     */
    private void updateIce() {
        
        _surfaceAtELA = null;
        
        // the x-position where the quasi-ELA intersects the ice surface
        final double qelax = Math.max( 0, ELAX_B0 + _elax_m0 * _quasiELA );

        final double glacierLength = qelax / ELAX_TERMINUS;
            
        if ( glacierLength == 0 ) {
            _iceThicknessSamples = null;
            _averageIceThicknessSquares = 0;
            _terminus.setLocation( _valley.getHeadwallPositionReference() );
        }
        else {
            // compute constants
            final double ela = _climate.getELA();
            final double maxThickness = MAX_THICKNESS_SCALE * Math.sqrt( qelax );
            final double headwallX = _valley.getHeadwallPositionReference().getX();
            final double headwallY = _valley.getHeadwallPositionReference().getY();
            final int numberOfSamples = (int) ( glacierLength / DX ) + 1;
            
            final double xPeak = headwallX + ( 0.5 * glacierLength ); // midpoint of the ice
            final double p = Math.max( 42 - ( 0.01 * _quasiELA ), 1.5 );
            final double r = 1.5 * xPeak;
            final double xPeakPow = Math.pow( xPeak, p );

            // initialize variables
            double x = headwallX;
            double surfaceElevation = 0;
            double thickness = 0;
            double sumOfNonZeroSquares = 0;
            double countOfNonZeroSquares = 0;
            
            _iceThicknessSamples = new double[numberOfSamples];
            for ( int i = 0; i < numberOfSamples; i++ ) {

                // compute thickness at sample point
                if ( x < xPeak ) {
                    thickness = Math.sqrt( ( r * r ) - ( ( x - xPeak ) * ( x - xPeak ) ) ) * ( maxThickness / r );
                    thickness *= ( xPeakPow - Math.pow( Math.abs( x - xPeak ), p ) ) / xPeakPow;
                }
                else {
                    thickness = Math.sqrt( ( xPeak * xPeak ) - ( ( x - xPeak ) * ( x - xPeak ) ) ) * ( maxThickness / xPeak );
                }
                assert ( thickness >= 0 );

                // accumulate squares
                if ( thickness > 0 ) {
                    sumOfNonZeroSquares += ( thickness * thickness );
                    countOfNonZeroSquares++;
                }
                
                // look for steady state ELA contour, this is approximate!
                if ( _surfaceAtELA == null ) {
                    surfaceElevation = _valley.getElevation( x ) + thickness;
                    if ( surfaceElevation <= ela ) {
                        _surfaceAtELA = new Point2D.Double( x, surfaceElevation );
                    }
                }

                _iceThicknessSamples[i] = thickness;
                x += DX;
            }
            x -= DX; // last sample was at this x value

            // compute average
            _averageIceThicknessSquares = sumOfNonZeroSquares / countOfNonZeroSquares;
            
            // terminus
            _terminus.setLocation( x, _valley.getElevation( x ) );
            assert( _terminus.getX() >= headwallX );
            assert( _terminus.getY() <= headwallY );
        }

        notifyIceThicknessChanged();
    }
    
    //----------------------------------------------------------------------------
    // Ice Velocity model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the ice velocity at a point in the ice.
     * If the point is outside the ice, a zero vector is returned.
     * <p>
     * Magnitude of the velocity vector is determined by the ice speed model herein.
     * Direction corresponds to the slope of the valley floor.
     * 
     * @param x meters
     * @param elevation meters
     * @param outputVector
     * @return Vector2D, components in meters/year
     */
    public Vector2D getIceVelocity( final double x, final double elevation, final Vector2D outputVector ) {
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
    public Vector2D getIceVelocity( final double x, final double elevation ) { 
        return getIceVelocity( x, elevation, new Vector2D.Double() /* outputVector */ );
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
        return ( ( iceThickness * iceThickness ) * U_DEFORM / averageIceThicknessSquares );
    }
    
    //----------------------------------------------------------------------------
    //  Timescale model
    //----------------------------------------------------------------------------
    
    /*
     * When the clock ticks, evolve the model.
     */
    public void clockTicked( ClockEvent event ) {
        
        if ( !isSteadyState() ) {
            
            // If _quasiELA is above the top of the headwall, immediately jump to the top of the headwall.
            // The main reason for doing this is a bug in q_advance_limit (see below). If the ELA goes above 
            // the top of the headwall, q_advance_limit becomes positive when it should be negative, and it
            // causes _quasiELA to evolves in the wrong direction (to higher elevations) forever.
            // A secondary reason for doing this is so that we don't have to wait for a glacier to start
            // forming when the climate is warmed.
            if ( _quasiELA > _valley.getMaxElevation() ) {
                _quasiELA = _valley.getMaxElevation();
            }
            
            final double dt = event.getSimulationTimeChange();
            final double ela = _climate.getELA();
            final double timescale = getTimescale();
            
            // calculate the delta
            double delta = ( ela - _quasiELA ) * ( 1 - Math.exp( -dt / timescale  ) );
            
            // limit the delta for an advancing glacier
            if ( ela < _quasiELA ) {
                final double q_advance_limit = ( ( -0.06 * _quasiELA ) + 300 ) * ELAX_TERMINUS / _elax_m0;
                delta = Math.max( delta, dt * q_advance_limit );
            }
            
            // evolve
            _quasiELA += delta;
            
            // are we close enough to steady state?
            if ( Math.abs( ela - _quasiELA ) <= ELA_EQUALITY_THRESHOLD ) {
                setSteadyState();
            }
            else {
                updateIce();
            }
        }
    }
    
    /* 
     * Gets the timescale for evolving the ice.
     * 
     * @return timescale (units?)
     */
    private double getTimescale() {
        
        // use a different timescale for receding vs advancing glacier
        double timescale;
        final double ela = _climate.getELA();
        if ( ela > _quasiELA ) {
            // receding
            timescale = ( -0.22 * ela ) + 1026;
        }
        else {
            // advancing
            timescale = ( 0.35 * ela ) - 1139;
        }
        
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
            ( ( GlacierListener ) i.next() ).iceThicknessChanged();
        }
    }
    
    private void notifySteadyStateChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( ( GlacierListener ) i.next() ).steadyStateChanged();
        }
    }
}
