package edu.colorado.phet.acidbasesolutions.model.acids;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;


public class CustomWeakAcid implements IWeakAcid {
    
    public double _strength;
    public final ArrayList _listeners;
    
    public CustomWeakAcid() {
        this( ABSConstants.DEFAULT_WEAK_STRENGTH );
    }
    
    public CustomWeakAcid( double strength ) {
        _strength = strength;
        _listeners = new ArrayList();
    }
    
    public String getName() {
        return ABSStrings.CUSTOM_WEAK_ACID;
    }
    
    public String getSymbol() {
        return "HA";
    }
    
    public String getConjugateBaseSymbol() {
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
