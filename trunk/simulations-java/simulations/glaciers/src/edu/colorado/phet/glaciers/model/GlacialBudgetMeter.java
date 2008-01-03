/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class GlacialBudgetMeter extends AbstractTool {
    
    private double _accumulation;
    private double _ablation;
    private double _glacialBudget;
    private ArrayList _listeners;

    public GlacialBudgetMeter( Point2D position ) {
        super( position );
        _accumulation = 0;
        _ablation = 0;
        _glacialBudget = 0;
        _listeners = new ArrayList();
    }
    
    public double getAccumulation() {
        return _accumulation;
    }
    
    public double getAblation() {
        return _ablation;
    }
    
    public double getGlacialBudget() {
        return _glacialBudget;
    }
    
    public void stepInTime( double dt ) {
        boolean changed = false;
        // TODO calculate new values and see if any of them changed
        if ( changed ) {
            notifyValuesChanged();
        }
    }
    
    public interface GlacialBudgetMeterListener {
        public void valuesChanged();
    }

    public void addListener( GlacialBudgetMeterListener listener ) {
        _listeners.add( listener );
    }

    public void removeListener( GlacialBudgetMeterListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyValuesChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (GlacialBudgetMeterListener) _listeners.get( i ) ).valuesChanged();
        }
    }
}
