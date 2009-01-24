package edu.colorado.phet.acidbasesolutions.model.acids;

import java.util.ArrayList;
import java.util.Iterator;


public class CustomWeakAcid implements IWeakAcid {
    
    public double _strength;
    public final ArrayList _listeners;
    
    public CustomWeakAcid( double strength ) {
        _strength = strength;
        _listeners = new ArrayList();
    }
    
    public String getAcidName() {
        return "HA";
    }
    
    public String getConjugateBaseName() {
        return "<html>A<sup>-</sup>";
    }
    
    public void setStrength( double strength ) {
        if ( strength != _strength ) {
            _strength = strength;
            notifyStrengthChanged();
        }
    }
    
    public double getStrength() {
        return _strength;
    }
    
    public interface CustomWeakAcidListener {
        public void strengthChanged();
    }
    
    public void addCustomWeakAcidListener( CustomWeakAcidListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeCustomWeakAcidListener( CustomWeakAcidListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyStrengthChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (CustomWeakAcidListener) i.next() ).strengthChanged();
        }
    }
}
