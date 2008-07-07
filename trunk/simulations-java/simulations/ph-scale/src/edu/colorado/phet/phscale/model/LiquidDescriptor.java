/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.phscale.PHScaleStrings;

/**
 * LiquidDescriptor is a static collection of immutable liquid descriptions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LiquidDescriptor {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int ALPHA = 127;
    
    private static final DecimalFormat PH_FORMAT = new DecimalFormat( "0.0" );
    
    // instances for each liquid type
    private static final LiquidDescriptor DRAIN_CLEANER = new LiquidDescriptor( PHScaleStrings.CHOICE_DRAIN_CLEANER, 13, new Color( 255, 255, 0, ALPHA ) );
    private static final LiquidDescriptor HAND_SOAP = new LiquidDescriptor( PHScaleStrings.CHOICE_HAND_SOAP, 10, new Color( 204, 0, 204, ALPHA ) );
    private static final LiquidDescriptor BLOOD = new LiquidDescriptor( PHScaleStrings.CHOICE_BLOOD, 7.4, new Color( 255, 0, 0, ALPHA ) );
    private static final LiquidDescriptor SPIT = new LiquidDescriptor( PHScaleStrings.CHOICE_SPIT, 7.4, new Color( 255, 255, 255, ALPHA ) );
    private static final LiquidDescriptor WATER = new LiquidDescriptor( PHScaleStrings.CHOICE_WATER, 7, new Color( 193, 222, 227, 127 ) );
    private static final LiquidDescriptor MILK = new LiquidDescriptor( PHScaleStrings.CHOICE_MILK, 6.5, new Color( 255, 255, 255, ALPHA ) );
    private static final LiquidDescriptor COFFEE = new LiquidDescriptor( PHScaleStrings.CHOICE_COFFEE, 5.0, new Color( 74, 44, 30, ALPHA ) );
    private static final LiquidDescriptor BEER = new LiquidDescriptor( PHScaleStrings.CHOICE_BEER, 4.5, new Color( 185, 79, 5, ALPHA ) );
    private static final LiquidDescriptor COLA = new LiquidDescriptor( PHScaleStrings.CHOICE_COLA, 2.5, new Color( 179, 119, 87, ALPHA ) );
    private static final LiquidDescriptor VOMIT = new LiquidDescriptor( PHScaleStrings.CHOICE_VOMIT, 2, new Color( 0, 255, 0, ALPHA ) );
    private static final LiquidDescriptor BATTERY_ACID = new LiquidDescriptor( PHScaleStrings.CHOICE_BATTERY_ACID, 1, new Color( 255, 255, 0, ALPHA ) );
    
    private static final LiquidDescriptor CUSTOM = new LiquidDescriptor( PHScaleStrings.CHOICE_CUSTOM, 7, new Color( 255, 255, 156, ALPHA ) ) {
        public String toString() {
            // no pH value shown for Custom liquid
            return getName();
        }
    };

    // all instances
    private static final LiquidDescriptor[] ALL_INSTANCES = new LiquidDescriptor[] {
        DRAIN_CLEANER, HAND_SOAP, BLOOD, SPIT, WATER, MILK, COFFEE, BEER, COLA, VOMIT, BATTERY_ACID, CUSTOM /* put Custom last! */
    };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _name;
    private final double _pH;
    private Color _color;
    private final ArrayList _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /* not intended for instantiation, all instance are defined herein as static constants */
    protected LiquidDescriptor( String name, double pH, Color color ) {
        _name = name;
        _pH = pH;
        _color = color;
        _listeners = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Getters
    //----------------------------------------------------------------------------
    
    public static LiquidDescriptor[] getAllInstances() {
        return ALL_INSTANCES;
    }
    
    public static LiquidDescriptor getWater() {
        return WATER;
    }
    
    public static LiquidDescriptor getCustom() {
        return CUSTOM;
    }
    
    public static LiquidDescriptor getDefaultLiquid() {
        return BLOOD;
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
    
    // For use by developer controls only!
    public void dev_setColor( Color color ) {
        _color = color;
        notifyColorChanged( color );
    }
    
    public String toString() { 
        // this format is displayed by LiquidComboBox
        return _name + " (" + PH_FORMAT.format( _pH ) + ")";
    }

    /**
     * Two liquids are equal if they have the same name and pH.
     */
    public boolean equals( Object o ) {
        boolean equals = false;
        if ( o instanceof LiquidDescriptor ) {
            LiquidDescriptor d = (LiquidDescriptor) o;
            equals = ( getName().equals( d.getName() ) && getPH() == d.getPH() );
        }
        return equals;
    }

    //----------------------------------------------------------------------------
    // Listeners interface
    //----------------------------------------------------------------------------
    
    /*
     * This interface is for developer controls.
     */
    public interface LiquidDescriptorListener {
        public void colorChanged( Color color );
    }
    
    public void addLiquidDescriptorListener( LiquidDescriptorListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeLiquidDescriptorListener( LiquidDescriptorListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyColorChanged( Color newColor ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidDescriptorListener) i.next() ).colorChanged( newColor );
        }
    }
}
