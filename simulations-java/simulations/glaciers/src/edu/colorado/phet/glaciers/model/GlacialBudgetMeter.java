/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public class GlacialBudgetMeter extends AbstractTool {
    
    private Glacier _glacier;
    private double _accumulation;
    private double _ablation;
    private double _glacialBudget;
    private ArrayList _listeners;

    public GlacialBudgetMeter( Point2D position, Glacier glacier ) {
        super( position );
        _glacier = glacier;
        _accumulation = 0;
        _ablation = 0;
        _glacialBudget = 0;
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        super.cleanup();
    }
    
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
    
    protected void handlePositionChanged() {
        recalculateValues();
    }
    
    protected void handleClockTimeChanged() {
        recalculateValues();
    }
    
    private void recalculateValues() {
        final double x = getX();
        setAccumulation( _glacier.getAccumulation( x ) );
        setAblation( _glacier.getAblation( x ) );
        setGlacialBudget( _glacier.getGlacialBudget( x ) );
    }
    
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

    public void addListener( GlacialBudgetMeterListener listener ) {
        _listeners.add( listener );
    }

    public void removeListener( GlacialBudgetMeterListener listener ) {
        _listeners.remove( listener );
    }

    private void notifyAccumulationChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (GlacialBudgetMeterListener) _listeners.get( i ) ).accumulationChanged();
        }
    }
    
    private void notifyAblationChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (GlacialBudgetMeterListener) _listeners.get( i ) ).ablationChanged();
        }
    }
    
    private void notifyGlacialBudgetChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (GlacialBudgetMeterListener) _listeners.get( i ) ).glacialBudgetChanged();
        }
    }
}
