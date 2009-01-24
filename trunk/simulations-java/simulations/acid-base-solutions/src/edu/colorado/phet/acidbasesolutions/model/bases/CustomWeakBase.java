package edu.colorado.phet.acidbasesolutions.model.bases;

import java.util.ArrayList;
import java.util.Iterator;


public class CustomWeakBase implements IWeakBase {
    
    public double _strength;
    public final ArrayList _listeners;
    
    public CustomWeakBase( double strength ) {
        _strength = strength;
        _listeners = new ArrayList();
    }
    
    public String getBaseName() {
        return "B";
    }
    
    public String getConjugateAcidName() {
        return "<html>BH<sup>+</sup>";
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
    
    public interface CustomWeakBaseListener {
        public void strengthChanged();
    }
    
    public void addCustomWeakBaseListener( CustomWeakBaseListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeCustomWeakBaseListener( CustomWeakBaseListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyStrengthChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (CustomWeakBaseListener) i.next() ).strengthChanged();
        }
    }
}
