/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.phscale.PHScaleStrings;


public class Liquid {
    
    // water is a special singleton
    public static class Water extends Liquid {
        private Water() {
            super(  PHScaleStrings.CHOICE_WATER, 7, new Color( 255, 255, 255, 100 ) );
        }
    }
    public static final Water WATER = new Water();
    
    // singletons for each liquid type
    public static final Liquid MILK = new Liquid( PHScaleStrings.CHOICE_MILK, 6.5, Color.WHITE );
    public static final Liquid BEER = new Liquid( PHScaleStrings.CHOICE_BEER, 4.5, new Color( 185, 79, 5 ) );
    public static final Liquid COLA = new Liquid( PHScaleStrings.CHOICE_COLA, 2.5, new Color( 122, 60, 35 ) );
    public static final Liquid LEMON_JUICE = new Liquid( PHScaleStrings.CHOICE_LEMON_JUICE, 2.4, Color.YELLOW );
    
    // all choices except water
    private static final Liquid[] CHOICES = new Liquid[] { MILK, BEER, COLA, LEMON_JUICE };
    
    public static Liquid[] getChoices() {
        return CHOICES;
    }
    
    private static final DecimalFormat PH_FORMAT = new DecimalFormat( "0.0" );
    
    private final String _name;
    private final double _pH;
    private double _volume; // liters
    private final Color _color;
    private final java.util.ArrayList _listeners;

    protected Liquid( String name, double pH, Color color ) {
        _name = name;
        _pH = pH;
        _color = color;
        _volume = 0;
        _listeners = new ArrayList();
    }
    
    public String getName() {
        return _name;
    }
    
    public double getPH() {
        return _pH;
    }
    
    public Color getColor() {
        return _color;
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
    
    public String toString() { 
        return _name + " (" + PH_FORMAT.format( _pH ) + ")";
    }
    
    public interface LiquidListener {
        public void volumeChanged( double volume );
    }
    
    public void addLiquidListener( LiquidListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeLiquidListener( LiquidListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyVolumeChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidListener) i.next() ).volumeChanged( _volume );
        }
    }
}
