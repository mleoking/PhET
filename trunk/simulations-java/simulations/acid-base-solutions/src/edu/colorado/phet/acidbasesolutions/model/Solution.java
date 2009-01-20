package edu.colorado.phet.acidbasesolutions.model;

import java.util.ArrayList;
import java.util.Iterator;


public class Solution {
    
    private double _concentration; // mol/L
    private double _strength;
    private final ArrayList _listeners;
    
    public Solution() {
        _concentration = 0;
        _strength = 0;
        _listeners = new ArrayList();
    }
    
    public PHValue getPH() {
        return new PHValue( 0 );//XXX
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
