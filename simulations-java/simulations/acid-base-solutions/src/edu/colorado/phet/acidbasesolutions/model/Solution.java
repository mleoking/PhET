/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;

//TODO: this should be deleted, it has been replaced but its still wired into view
public class Solution {
    
    private static final double AVOGADROS_NUMBER = 6.023E23;
    private static final double H2O_CONCENTRATION = 55; // moles/L
    private static final double VOLUME = 1; // L
    private static final double PH_H2O = 7;
    
    private PHValue _pH;
    private double _concentration; // mol/L
    private double _strength;
    private final ArrayList _listeners;
    
    public Solution() {
        _pH = new PHValue( PH_H2O );//XXX
        _concentration = 0;
        _strength = 0;
        _listeners = new ArrayList();
    }
    
    public double getVolume() {
        return VOLUME;
    }
    
    public PHValue getPH() {
        return _pH;
    }
    
    public void setConcentration( double concentration ) {
        if ( concentration != _concentration ) {
            _concentration = concentration;
            notifyStateChanged();
        }
    }
    
    public double getConcentration() {
        return _concentration;
    }
    
    public double getConcentrationH2O() {
        return H2O_CONCENTRATION;
    }
    
    public double getConcentrationH3O() {
        return Math.pow( 10, -_pH.getValue() );
    }
    
    public double getConcentrationOH() {
        return Math.pow( 10, -( 14 - _pH.getValue() ) );
    }
    
    public void setStrength( double strength ) {
        if ( strength != _strength ) {
            _strength = strength;
            notifyStateChanged();
        }
    }
    
    public interface SolutionListener {
        public void stateChanged();
    }
    
    public void addSolutionListener( SolutionListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeSolutionListener( SolutionListener listener ) {
        _listeners.remove( listener );
    }

    private void notifyStateChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (SolutionListener) i.next() ).stateChanged();
        }
    }
    
}
