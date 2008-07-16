/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.phscale.PHScaleConstants;
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
    
    private static final DecimalFormat PH_FORMAT = new DecimalFormat( "0.0" );
    
    // instances for each liquid type
    private static final LiquidDescriptor DRAIN_CLEANER = new LiquidDescriptor( PHScaleStrings.CHOICE_DRAIN_CLEANER, 13, new Color( 255, 255, 0, 150 ) );
    private static final LiquidDescriptor HAND_SOAP = new LiquidDescriptor( PHScaleStrings.CHOICE_HAND_SOAP, 10, new Color( 204, 0, 204, 90 ) );
    private static final LiquidDescriptor BLOOD = new LiquidDescriptor( PHScaleStrings.CHOICE_BLOOD, 7.4, new Color( 185, 12, 0, 150 ) );
    private static final LiquidDescriptor SPIT = new LiquidDescriptor( PHScaleStrings.CHOICE_SPIT, 7.4, new Color( 204, 204, 198, 73 ) );
    private static final LiquidDescriptor WATER = new LiquidDescriptor( PHScaleStrings.CHOICE_WATER, 7, ColorUtils.createColor( PHScaleConstants.H2O_COLOR, 127 ) );
    private static final LiquidDescriptor MILK = new LiquidDescriptor( PHScaleStrings.CHOICE_MILK, 6.5, new Color( 255, 255, 255, 180 ) );
    private static final LiquidDescriptor COFFEE = new LiquidDescriptor( PHScaleStrings.CHOICE_COFFEE, 5.0, new Color( 164, 99, 7, 127 ) );
    private static final LiquidDescriptor BEER = new LiquidDescriptor( PHScaleStrings.CHOICE_BEER, 4.5, new Color( 255, 200, 0, 127 ) );
    private static final LiquidDescriptor LIME_SODA = new LiquidDescriptor( PHScaleStrings.SODA, 2.5, new Color( 204, 255, 102, 162 ) );
    private static final LiquidDescriptor VOMIT = new LiquidDescriptor( PHScaleStrings.CHOICE_VOMIT, 2, new Color( 255, 171, 120, 183 ) );
    private static final LiquidDescriptor BATTERY_ACID = new LiquidDescriptor( PHScaleStrings.CHOICE_BATTERY_ACID, 1, new Color( 255, 255, 0, 127 ) );
    
    public static class CustomLiquidDescriptor extends LiquidDescriptor {
        
        private final double _defaultPH;
        
        private CustomLiquidDescriptor( ) {
            super( PHScaleStrings.CHOICE_CUSTOM, 7, new Color( 255, 255, 156, 127 )  );
            _defaultPH = getPH();
        }
        
        // make this interface public
        public void setPH( double pH ) {
            super.setPH( pH );
        }
        
        public void resetPH() {
            setPH( _defaultPH );
        }
        
        // no pH value shown for Custom liquid
        public String toString() {
            return getName();
        }
    }
    
    private static final CustomLiquidDescriptor CUSTOM = new CustomLiquidDescriptor();
    
    // all instances
    private static final LiquidDescriptor[] ALL_INSTANCES = new LiquidDescriptor[] {
        DRAIN_CLEANER, HAND_SOAP, BLOOD, SPIT, WATER, MILK, COFFEE, BEER, LIME_SODA, VOMIT, BATTERY_ACID, CUSTOM /* put Custom last! */
    };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _name;
    private double _pH;
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
    
    public static CustomLiquidDescriptor getCustom() {
        return CUSTOM;
    }
    
    public static LiquidDescriptor getDefaultLiquid() {
        return MILK;
    }
    
    public String getName() {
        return _name;
    }
    
    protected void setPH( double pH ) {
        if ( pH != _pH ) {
            _pH = pH;
            notifyPHChanged( pH );
        }
    }
    
    public double getPH() {
        return _pH;
    }
    
    public Color getColor() {
        return _color;
    }
    
    // For use by developer controls only!
    public void dev_setColor( Color color ) {
        if ( !color.equals( _color ) ) {
            _color = color;
            notifyColorChanged( color );
        }
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
    
    public interface LiquidDescriptorListener {
        public void pHChanged( double newPH );
        public void colorChanged( Color newColor );
    }
    
    public static class LiquidDescriptorAdapter implements LiquidDescriptorListener {
        public void pHChanged( double newPH ) {}
        public void colorChanged( Color newColor ) {}
    }
    
    public void addLiquidDescriptorListener( LiquidDescriptorListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeLiquidDescriptorListener( LiquidDescriptorListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyPHChanged( double newPH ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidDescriptorListener) i.next() ).pHChanged( newPH );
        }
    }
    
    private void notifyColorChanged( Color newColor ) {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (LiquidDescriptorListener) i.next() ).colorChanged( newColor );
        }
    }
}
