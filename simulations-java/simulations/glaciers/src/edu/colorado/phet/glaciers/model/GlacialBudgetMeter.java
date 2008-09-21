/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * GlacialBudgetMeter is the model of a glacial budget meter.
 * The meter displays the accumulation, ablation and glacial budget at a point on the glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacialBudgetMeter extends AbstractTool {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // when the position is this close or closer to the ice or valley surface, snap to the surface
    private static final double SNAP_TO_SURFACE_THRESHOLD = 150; // meters
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Glacier _glacier;
    private final GlacierListener _glacierListener;
    private final ClimateListener _climateListener;
    private boolean _onSurface;
    private double _accumulation;
    private double _ablation;
    private double _glacialBudget;
    private final ArrayList _listeners; // list of GlacialBudgetMeterListener

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlacialBudgetMeter( Point2D position, Glacier glacier ) {
        super( position );
        
        _glacier = glacier;
        _onSurface = false;
        
        _climateListener = new ClimateListener() {
            public void temperatureChanged() {
                updateAllValues();
            }

            public void snowfallChanged() {
                updateAllValues();
            }
        };
        _glacier.getClimate().addClimateListener( _climateListener );
        
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                // keep meter on glacier surface as the glacier evolves
                if ( !isDragging() && _onSurface ) {
                    double surfaceElevation = _glacier.getSurfaceElevation( getX() );
                    setPosition( getX(), surfaceElevation );
                }
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        _accumulation = 0;
        _ablation = 0;
        _glacialBudget = 0;
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        _glacier.getClimate().removeClimateListener( _climateListener );
        _glacier.removeGlacierListener( _glacierListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getAccumulation() {
        return _accumulation;
    }
    
    private void setAccumulation( double accumulation ) {
        if ( accumulation != _accumulation ) {
            _accumulation = accumulation;
            notifyAccumulationChanged();
        }
    }
    
    public double getAblation() {
        return _ablation;
    }
    
    private void setAblation( double ablation ) {
        if ( ablation != _ablation ) {
            _ablation = ablation;
            notifyAblationChanged();
        }
    }
    
    public double getGlacialBudget() {
        return _glacialBudget;
    }
    
    private void setGlacialBudget( double glacialBudget ) {
        if ( glacialBudget != -glacialBudget ) {
            _glacialBudget = glacialBudget;
            notifyGlacialBudgetChanged();
        }
    }
    
    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------
    
    /**
     * If the position is within some range above/below the ice surface,
     * snap the position to the surface.
     * 
     * @param x
     * @param y
     */
    public void setPosition( double x, double y ) {
        double surfaceElevation = _glacier.getSurfaceElevation( x );
        if ( x >= _glacier.getHeadwallX() && Math.abs( y - surfaceElevation ) < SNAP_TO_SURFACE_THRESHOLD ) {
            _onSurface = true;
            super.setPosition( x, surfaceElevation );
        }
        else {
            _onSurface = false;
            super.setPosition( x, y );
        }
    }
    
    protected void handlePositionChanged() {
        updateAllValues();
    }
    
    private void updateAllValues() {
        final Climate climate = _glacier.getClimate();
        final double elevation = getY();
        setAccumulation( climate.getAccumulation( elevation ) );
        setAblation( climate.getAblation( elevation ) );
        setGlacialBudget( climate.getGlacialBudget( elevation ) );
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface GlacialBudgetMeterListener {
        public void accumulationChanged();
        public void ablationChanged();
        public void glacialBudgetChanged();
    }
    
    public static class GlacialBudgetMeterAdapter implements GlacialBudgetMeterListener {
        public void accumulationChanged() {};
        public void ablationChanged() {};
        public void glacialBudgetChanged() {};
    }

    public void addGlacialBudgetMeterListener( GlacialBudgetMeterListener listener ) {
        _listeners.add( listener );
    }

    public void removeGlacialBudgetMeterListener( GlacialBudgetMeterListener listener ) {
        _listeners.remove( listener );
    }

    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------
    
    private void notifyAccumulationChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GlacialBudgetMeterListener) i.next() ).accumulationChanged();
        }
    }
    
    private void notifyAblationChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GlacialBudgetMeterListener) i.next() ).ablationChanged();
        }
    }
    
    private void notifyGlacialBudgetChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (GlacialBudgetMeterListener) i.next() ).glacialBudgetChanged();
        }
    }
}
