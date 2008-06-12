/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;


public class Liquid {
    
    private double _pH;
    private double _volume; // liters
    private Color _color;
    private final java.util.ArrayList _listeners;

    public Liquid( double pH, double volume, Color color ) {
        _pH = pH;
        _volume = volume;
        _listeners = new ArrayList();
    }
    
    public void setPH( double pH ) {
        if ( pH != _pH ) {
            _pH = pH;
            notifyPHChanged();
        }
    }
    
    public double getPH() {
        return _pH;
    }
    
    public void setVolume( double volume ) {
        if ( volume != _volume ) {
            _volume = volume;
            notifyVolumeChanged();
        }
    }
    
    public double getVolume() {
        return _volume;
    }
    
    public void setColor( Color color ) {
        if ( !color.equals( _color ) ) {
            _color = color;
            notifyColorChanged();
        }
    }
    
    public interface LiquidListener {
        public void pHChanged( double pH );
        public void volumeChanged( double volume );
        public void colorChanged( Color color );
    }
    
    public static class LiquidAdapter implements LiquidListener {
        public void pHChanged( double pH ) {}
        public void volumeChanged( double volume ) {}
        public void colorChanged( Color color ) {}
    }
    
    public void addLiquidListener( LiquidListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeLiquidListener( LiquidListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyPHChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidListener) i.next() ).pHChanged( _pH );
        }
    }
    
    private void notifyVolumeChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidListener) i.next() ).volumeChanged( _volume );
        }
    }
    
    private void notifyColorChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidListener) i.next() ).colorChanged( _color );
        }
    }
}
