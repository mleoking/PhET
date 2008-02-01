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
    private static final double DX = 10; // distance between x-axis sample points (meters)
    private static final double MIN_ICE_VOLUME_FLUX = 0; // minimum ice volume flux (meters^2/year)
    private static final double ALMOST_ZERO_ICE_VOLUME_FLUX = 1E-5; // anything <= this value is considered zero
    private static final double MAX_ICE_THICKNESS = 500; // maximum ice thickness (meters)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Valley _valley;
    private final Climate _climate;
    private final ClimateListener _climateListener;
    private final EquilibriumLine _equilibriumLine;
    private final ArrayList _listeners; // list of GlacierListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Glacier( Valley valley, Climate climate, EquilibriumLine equilibriumLine ) {
        super();
        
        _valley = valley;
        
        _climate = climate;
        _climateListener = new ClimateListener() {

            public void snowfallChanged() {
                notifyIceThicknessChanged();
            }

            public void temperatureChanged() {
                notifyIceThicknessChanged();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        _equilibriumLine = equilibriumLine;
        
        _listeners = new ArrayList();
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
     * Gets the maximum x coordinate of the glacier's terminus.
     * 
     * @return meters
     */
    public static double getXMax() {
        return getX0() + getMaxLength();
    }
    
    /*
     * Gets the maximum length of the glacier.
     * 
     * @return meters
     */
    private static double getMaxLength() {
        return MAX_LENGTH;
    }
    
    /*
     * Gets the distance between x-axis sample points.
     * 
     * @return meters
     */
    private static double getDx() {
        return DX;
    }
    
    /*
     * Gets the ice volume flux at a specified location.
     * This is the integral from x0 to x of ( valley width * glacial budget ).
     * 
     * @param x meters
     * @return meters^2/year
     */
    private double getIceVolumeFlux( final double x ) {
        double flux = 0;
        double valleyWidth = 0;
        double elevation = 0;
        double glacialBudget = 0;
        for ( double xn = getX0(); xn <= x; xn += getDx() ) {
            valleyWidth = _valley.getWidth( xn );
            elevation = _valley.getElevation( xn );
            glacialBudget = _climate.getGlacialBudget( elevation );
            flux += ( valleyWidth * glacialBudget );
        }
        if ( flux < MIN_ICE_VOLUME_FLUX ) {
            flux = MIN_ICE_VOLUME_FLUX;
        }
        return flux;
    }
    
    /*
     * Gets the length of the glacier.
     * 
     * @return meters
     */
    private double getLength() {
        double x = getX0();
        double iceVolumeFlux = getIceVolumeFlux( x );
        while ( iceVolumeFlux > ALMOST_ZERO_ICE_VOLUME_FLUX ) {
            x += getDx();
            iceVolumeFlux = getIceVolumeFlux( x );
        }
        return x - getX0();
    }
    
    /*
     * Gets the x coordinate of the glacier's terminus.
     * The terminus is the down-valley end of the glacier.
     * 
     * @return meters
     */
    private double getTerminusX() {
        return getX0() + getLength();
    }
    
    /*
     * Gets the ice thickness at steady state at a specified location.
     * 
     * @param x meters
     * @returns meters
     */
    private double getSteadyStateIceThickness( double x ) {
        double length = getLength();
        double iceVolumeFlux = getIceVolumeFlux( x );
        double maxIceVolumeFlux = getIceVolumeFlux( getX0() + ( length / 2 ) );
        return MAX_ICE_THICKNESS * ( iceVolumeFlux / maxIceVolumeFlux ) * ( length / MAX_LENGTH );
    }

    public double getIceThickness( double x ) {
        return 0;//XXX
    }
    
    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    public void simulationTimeChanged( ClockEvent event ) {
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface GlacierListener {
        public void iceThicknessChanged();
    }
    
    public static class GlacierAdapter implements GlacierListener {
        public void iceThicknessChanged() {};
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
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( ( GlacierListener ) i.next() ).iceThicknessChanged();
        }
    }
}
