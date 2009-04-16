package edu.colorado.phet.acidbasesolutions.model.acids;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


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
    
    public String getProductCationSymbol() {
        return "<html>A<sup>-</sup>";
    }
    
    public String getProductAnionSymbol() {
        return ABSSymbols.OH_MINUS;
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
    
    public interface CustomAcidListener {
        public void strengthChanged();
    }
    
    public void addCustomAcidListener( CustomAcidListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeCustomAcidListener( CustomAcidListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyStrengthChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (CustomAcidListener) i.next() ).strengthChanged();
        }
    }
}
