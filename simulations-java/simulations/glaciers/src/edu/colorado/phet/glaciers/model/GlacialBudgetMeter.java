/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * GlacialBudgetMeter is the model of a glacial budget meter.
 * The meter displays the accumulation, ablation and glacial budget at a point on the glacier.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacialBudgetMeter extends AbstractTool {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Climate _climate;
    private final ClimateListener _climateListener;
    private double _accumulation;
    private double _ablation;
    private double _glacialBudget;
    private final ArrayList _listeners; // list of GlacialBudgetMeterListener

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlacialBudgetMeter( Point2D position, Climate climate ) {
        super( position );
        
        _climate = climate;
        _climateListener = new ClimateListener() {
            public void temperatureChanged() {
                updateAllValues();
            }

            public void snowfallChanged() {
                updateAllValues();
            }
            
            public void snowfallReferenceElevationChanged() {
                updateAllValues();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        _accumulation = 0;
        _ablation = 0;
        _glacialBudget = 0;
        _listeners = new ArrayList();
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
    
    protected void handlePositionChanged() {
        updateAllValues();
    }
    
    private void updateAllValues() {
        final double elevation = getY();
        setAccumulation( _climate.getAccumulation( elevation ) );
        setAblation( _climate.getAblation( elevation ) );
        setGlacialBudget( _climate.getGlacialBudget( elevation ) );
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
