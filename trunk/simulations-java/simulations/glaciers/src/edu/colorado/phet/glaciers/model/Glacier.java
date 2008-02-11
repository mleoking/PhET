/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * Glacier is the model of the glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Glacier extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double X0 = 0; // x coordinate where the glacier starts (meters) DO NOT CHANGE!!
    private static final double MAX_LENGTH = 80000; // maximum length (meters)
    
    private static final int NUMBER_OF_X_SAMPLES = 1001;
    private static final double DX = MAX_LENGTH / ( NUMBER_OF_X_SAMPLES - 1 ); // distance between x-axis sample points (meters)
    
    private static final double MIN_ICE_VOLUME_FLUX = 0; // minimum ice volume flux (meters^2/year)
    private static final double ALMOST_ZERO_ICE_VOLUME_FLUX = 1E-5; // anything <= this value is considered zero
    private static final double MAX_ICE_THICKNESS = 500; // maximum ice thickness (meters)
    private static final double U_SLIDE = 20; // downvalley ice speed (meters/year)
    private static final double U_DEFORM = 20; // contribution of vertical deformation to ice speed (meters/year)
    private static final double MIN_TIMESCALE = 20; //XXX ?
    private static final double MAX_TIMESCALE = 50; //XXX ?
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Valley _valley;
    private final Climate _climate;
    private final ClimateListener _climateListener;
    private final EquilibriumLine _equilibriumLine;
    private final ArrayList _listeners; // list of GlacierListener
    private double _length;
    private double[] _iceThicknessSamples;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Glacier( Valley valley, Climate climate, EquilibriumLine equilibriumLine ) {
        super();
        
        _valley = valley;
        
        _climate = climate;
        _climateListener = new ClimateListener() {

            //XXX temporary, to demonstrate immediate changes
            public void snowfallChanged() {
                updateIceThickness();
            }

            //XXX temporary, to demonstrate immediate changes
            public void temperatureChanged() {
                updateIceThickness();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        _equilibriumLine = equilibriumLine;
        
        _listeners = new ArrayList();
        
        _length = 0;
        _iceThicknessSamples = null;
        
        updateIceThickness();
    }
    
    public void cleanup() {
        _equilibriumLine.cleanup();
        _climate.removeClimateListener( _climateListener );
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
     * Gets the equilibrium line.
     * 
     * @return
     */
    public EquilibriumLine getEquilibriumLine() {
        return _equilibriumLine;
    }
    
    /**
     * Gets the x coordinate where the glacier starts.
     * 
     * @return meters
     */
    public static double getX0() {
        return X0;
    }
    
    /**
     * Gets the length of the glacier.
     * 
     * @return meters
     */
    public double getLength() {
        return _length;
    }
    
    /**
     * Gets the maximum length of the glacier.
     * 
     * @return meters
     */
    public double getMaxLength() {
        return MAX_LENGTH;
    }
    
    /**
     * Gets the x coordinate of the glacier's terminus.
     * The terminus is the downvalley end of the glacier.
     * 
     * @return meters
     */
    public double getTerminusX() {
        return X0 + getLength();
    }
    
    /**
     * Gets the maximum x coordinate of the glacier's terminus.
     * 
     * @return meters
     */
    public static double getMaxTerminusX() {
        return X0 + MAX_LENGTH;
    }
    
    /**
     * Gets the spacing used between x-axis sample points.
     * 
     * @return meters
     */
    public static double getDx() {
        return DX;
    }
    
    /**
     * Gets the ice thickness samples.
     * Samples were made from X0 to terminusX, at intervals of DX.
     * Use getX0, getTerminusX, and getDX to interpret these samples.
     * 
     * @return
     */
    public double[] getIceThicknessSamples() {
        return _iceThicknessSamples;
    }
    
    /**
     * Gets the approximate ice thickness at an x coordinate.
     * The x value specified will fall between 2 ice thickness samples.
     * Locate the 2 samples, and do a linear interpolation between them 
     * to determine the approximate ice thickness.
     * 
     * @param x
     * @return meters
     */
    public double getIceThickness( final double x ) {
        double iceThickness = 0;
        if ( x >= X0 && x <= getTerminusX() ) {
            if ( x == getTerminusX() ) {
                iceThickness = _iceThicknessSamples[_iceThicknessSamples.length - 1];
            }
            else {
                double x1 = X0;
                boolean found = false;
                for ( int i = 0; found == false && i < _iceThicknessSamples.length - 1; i++ ) {
                    if ( x >= x1 && x <= x1 + DX ) {
                        double t1 = _iceThicknessSamples[i];
                        double t2 = _iceThicknessSamples[i + 1];
                        iceThickness = t1 + ( ( ( x - x1 ) / DX ) * ( t2 - t1 ) ); // linear interpolation
                        found = true;
                    }
                    else {
                        x1 += DX;
                    }
                }
                assert ( found == true );
            }
        }
        assert ( iceThickness >= 0 );
        return iceThickness;
    }
    
    //----------------------------------------------------------------------------
    // Ice Thickness model
    //----------------------------------------------------------------------------
    
    private void updateIceThickness() {
        
        final double oldLength = _length;
        
        // Compute ice volume flux at x sample points (flux is an integral).
        ArrayList iceVolumeFluxSamples = new ArrayList();
        double x = X0;
        double flux = 0; // integral is accumulated here
        double maxFlux = 0;
        double valleyWidth = 0;
        double elevation = 0;
        double glacialBudget = 0;
        boolean done = false;
        while ( !done ) {

            // compute flux
            valleyWidth = _valley.getWidth( x );
            elevation = _valley.getElevation( x );
            glacialBudget = _climate.getGlacialBudget( elevation );
            flux += ( valleyWidth * glacialBudget );
            if ( flux < MIN_ICE_VOLUME_FLUX ) {
                flux = MIN_ICE_VOLUME_FLUX;
            }
            iceVolumeFluxSamples.add( new Double( flux ) );

            // keep track of max flux
            if ( flux > maxFlux ) {
                maxFlux = flux;
            }

            // we're done when flux is zero
            if ( flux <= ALMOST_ZERO_ICE_VOLUME_FLUX ) {
                // glacier length
                _length = x - X0;
                done = true;
            }
            else {
                // next x value
                x += DX;
            }
        }

        // Compute ice thickness at x sample points.
        _iceThicknessSamples = new double[iceVolumeFluxSamples.size()];
        x = X0;
        for ( int i = 0; i < _iceThicknessSamples.length; i++ ) {

            // steady state thickness
            flux = ( (Double) iceVolumeFluxSamples.get( i ) ).doubleValue();
            _iceThicknessSamples[i] = MAX_ICE_THICKNESS * ( flux / maxFlux ) * ( _length / MAX_LENGTH );

            // next x value
            x += DX;
        }

        // notification
        if ( _length != oldLength ) {
            notifyLengthChanged();
        }
        notifyIceThicknessChanged();
    }
    
    //----------------------------------------------------------------------------
    // Ice Velocity model
    //----------------------------------------------------------------------------
    
//    /*
//     * Gets the steady-state ice velocity at a point in the ice.
//     * If the point is outside the ice, a zero vector is returned.
//     * <p>
//     * Magnitude of the velocity vector is determined by the ice speed model herein.
//     * Direction corresponds to the slope of the valley floor.
//     * 
//     * @param x meters
//     * @param elevation meters
//     * @return Vector2D, components in meters/year
//     */
//    private Vector2D getSteadyStateIceVelocity( final double x, final double elevation ) {
//        final double magnitude = getSteadyStateIceSpeed( x, elevation );
//        final double direction = _valley.getDirection( x, x + DX );
//        final double xComponent = PolarCartesianConverter.getX( magnitude, direction );
//        final double yComponent = PolarCartesianConverter.getY( magnitude, direction );
//        return new Vector2D.Double( xComponent, yComponent );
//    }
//    
//    /*
//     * Gets the steady-state ice speed at a point in the ice.
//     * If the point is outside the ice, zero is returned.
//     * 
//     * @param x meters
//     * @param elevation meters
//     * @return meters/year
//     */
//    private double getSteadyStateIceSpeed( final double x, final double elevation ) {
//        double iceSpeed = 0;
//        final double iceThickness = getSteadyStateIceThickness( x );
//        if ( iceThickness > 0 ) {
//            final double valleyElevation = _valley.getElevation( x );
//            final double iceSurfaceElevation = valleyElevation + iceThickness;
//            // if the elevation is in the ice...
//            if ( elevation >= valleyElevation && elevation <= iceSurfaceElevation ) {
//                // zz varies linearly from 0 at the valley floor (rock-ice interface) to 1 at the ice surface (air-ice interface)
//                final double zz = ( elevation - valleyElevation ) / iceThickness;
//                final double u_deform_ave = getVerticallyAveragedDeformationIceSpeed( x );
//                iceSpeed = U_SLIDE + ( u_deform_ave * 5. * ( zz - ( 1.5 * zz * 2 ) + ( zz * 3 ) - ( 0.25 * zz * 4 ) ) );
//            }
//        }
//        return iceSpeed;
//    }
//    
//    /*
//     * Gets the vertically-averaged deformation ice speed.
//     * Speed of ice moving downvalley is affected by vertical deformation,
//     * and this method calculation the deformation contribution.
//     * (symbol: u_deform_ave)
//     * 
//     * @param x meters
//     * @return meters/year
//     */
//    private double getVerticallyAveragedDeformationIceSpeed( final double x ) {
//        final double u0ave = getAverageIceThicknessSquared();
//        final double h = getSteadyStateIceThickness( x );
//        return ( ( h * h ) * U_DEFORM / u0ave );
//    }
//    
//    /*
//     * Gets the average of the square of the ice thickness over the complete length of the glaciers.
//     * Only non-zero thickness value are included in the average.
//     * (symbol: u0ave)
//     * 
//     * @return meters^2
//     */
//    private double getAverageIceThicknessSquared() {
//        double sum = 0;
//        double samples = 0;
//        double h = 0;
//        final double xTerminus = getTerminusX();
//        for ( double x = X0; x <= xTerminus; x += DX ) {
//            h = getSteadyStateIceThickness( x );
//            if ( h > 0 ) {
//                sum += ( h * h );
//                samples++;
//            }
//        }
//        return ( sum / samples );
//    }
    
    //----------------------------------------------------------------------------
    //  Timescale model
    //----------------------------------------------------------------------------
    
    /* 
     * Gets the climate change timescale for a specified ELA (equilibrium line altitude).
     * 
     * @param ela equilibrium line altitude (meters)
     * @return timescale (units?)
     */
    private static double getClimateChangeTimescale( double ela ) {
        double timescale = ( ( -37.5 / 300 ) * ela ) + 484.6;
        if ( timescale < MIN_TIMESCALE ) {
            timescale = MIN_TIMESCALE;
        }
        else if ( timescale > MAX_TIMESCALE ) {
            timescale = MAX_TIMESCALE;
        }
        return timescale;
    }
    
    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent event ) {
        //XXX this is where we'll interpolate between current and future states of the glacier
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface GlacierListener {
        public void lengthChanged();
        public void iceThicknessChanged();
        public void iceVelocityChanged();
    }
    
    public static class GlacierAdapter implements GlacierListener {
        public void lengthChanged() {};
        public void iceThicknessChanged() {};
        public void iceVelocityChanged() {};
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
    
    private void notifyLengthChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( ( GlacierListener ) i.next() ).lengthChanged();
        }
    }
    
    private void notifyIceThicknessChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( ( GlacierListener ) i.next() ).iceThicknessChanged();
        }
    }
    
    private void notifyIceVelocityChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( ( GlacierListener ) i.next() ).iceVelocityChanged();
        }
    }
}
