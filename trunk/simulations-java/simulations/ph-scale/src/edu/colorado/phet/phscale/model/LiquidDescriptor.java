/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.text.DecimalFormat;

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
    
    private static final int ALPHA = 127;
    
    private static final DecimalFormat PH_FORMAT = new DecimalFormat( "0.0" );
    
    private static final Color UNASSIGNED_ACID_COLOR = ColorUtils.createColor( PHScaleConstants.H3O_COLOR, ALPHA );
    private static final Color UNASSIGNED_BASE_COLOR = ColorUtils.createColor( PHScaleConstants.OH_COLOR, ALPHA );
    
    // instances for each liquid type
    private static final LiquidDescriptor BATTERY_ACID = new LiquidDescriptor( PHScaleStrings.CHOICE_BATTERY_ACID, 1, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor VOMIT = new LiquidDescriptor( PHScaleStrings.CHOICE_VOMIT, 2, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor COLA = new LiquidDescriptor( PHScaleStrings.CHOICE_COLA, 2.5, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor BEER = new LiquidDescriptor( PHScaleStrings.CHOICE_BEER, 4.5, new Color( 185, 79, 5, ALPHA ) );
    private static final LiquidDescriptor COFFEE = new LiquidDescriptor( PHScaleStrings.CHOICE_COFFEE, 5.0, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor MILK = new LiquidDescriptor( PHScaleStrings.CHOICE_MILK, 6.5, new Color( 255, 255, 255, ALPHA ) );
    private static final LiquidDescriptor WATER = new LiquidDescriptor( PHScaleStrings.CHOICE_WATER, 7, ColorUtils.createColor( PHScaleConstants.H2O_COLOR, ALPHA ) );
    private static final LiquidDescriptor SPIT = new LiquidDescriptor( PHScaleStrings.CHOICE_SPIT, 7.4, UNASSIGNED_BASE_COLOR );
    private static final LiquidDescriptor BLOOD = new LiquidDescriptor( PHScaleStrings.CHOICE_BLOOD, 7.4, new Color( 255, 0, 0, ALPHA ) );
    private static final LiquidDescriptor HAND_SOAP = new LiquidDescriptor( PHScaleStrings.CHOICE_HAND_SOAP, 10, UNASSIGNED_BASE_COLOR );
    private static final LiquidDescriptor DRAIN_CLEANER = new LiquidDescriptor( PHScaleStrings.CHOICE_DRAIN_CLEANER, 13, UNASSIGNED_BASE_COLOR );
    
    // all instances
    private static final LiquidDescriptor[] ALL_INSTANCES = new LiquidDescriptor[] {
        BATTERY_ACID, COLA, BEER, COFFEE, MILK, WATER, BLOOD, HAND_SOAP, DRAIN_CLEANER
    };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _name;
    private final double _pH;
    private Color _color;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /* not intended for instantiation, all instance are defined herein as static constants */
    protected LiquidDescriptor( String name, double pH, Color color ) {
        _name = name;
        _pH = pH;
        _color = color;
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
    
    public static LiquidDescriptor getDefaultLiquid() {
        return BATTERY_ACID;
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
    }
    
    public String toString() { 
        // this format is displayed by LiquidComboBox
        return _name + " (" + PH_FORMAT.format( _pH ) + ")";
    }
}
