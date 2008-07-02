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
    private static final LiquidDescriptor BILE = new LiquidDescriptor( PHScaleStrings.CHOICE_BILE, 2, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor LEMON_JUICE = new LiquidDescriptor( PHScaleStrings.CHOICE_LEMON_JUICE, 2.4, new Color( 255, 255, 0, ALPHA ) );
    private static final LiquidDescriptor COLA = new LiquidDescriptor( PHScaleStrings.CHOICE_COLA, 2.5, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor VINEGAR = new LiquidDescriptor( PHScaleStrings.CHOICE_VINEGAR, 2.9, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor ORANGE_JUICE = new LiquidDescriptor( PHScaleStrings.CHOICE_ORANGE_JUICE, 3.5, new Color( 255, 186, 0, ALPHA ) );
    private static final LiquidDescriptor BEER = new LiquidDescriptor( PHScaleStrings.CHOICE_BEER, 4.5, new Color( 185, 79, 5, ALPHA ) );
    private static final LiquidDescriptor COFFEE = new LiquidDescriptor( PHScaleStrings.CHOICE_COFFEE, 5.0, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor TEA = new LiquidDescriptor( PHScaleStrings.CHOICE_TEA, 5.5, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor ACID_RAIN = new LiquidDescriptor( PHScaleStrings.CHOICE_ACID_RAIN, 5.6, UNASSIGNED_ACID_COLOR );
    private static final LiquidDescriptor MILK = new LiquidDescriptor( PHScaleStrings.CHOICE_MILK, 6.5, new Color( 255, 255, 255, ALPHA ) );
    private static final LiquidDescriptor WATER = new LiquidDescriptor( PHScaleStrings.CHOICE_WATER, 7, ColorUtils.createColor( PHScaleConstants.H2O_COLOR, ALPHA ) );
    private static final LiquidDescriptor SALIVA = new LiquidDescriptor( PHScaleStrings.CHOICE_SALIVA, 7.4, UNASSIGNED_BASE_COLOR );
    private static final LiquidDescriptor BLOOD = new LiquidDescriptor( PHScaleStrings.CHOICE_BLOOD, 7.4, new Color( 255, 0, 0, ALPHA ) );
    private static final LiquidDescriptor SEA_WATER = new LiquidDescriptor( PHScaleStrings.CHOICE_SEA_WATER, 7.4, UNASSIGNED_BASE_COLOR );
    private static final LiquidDescriptor HAND_SOAP = new LiquidDescriptor( PHScaleStrings.CHOICE_HAND_SOAP, 10, UNASSIGNED_BASE_COLOR );
    private static final LiquidDescriptor AMMONIA = new LiquidDescriptor( PHScaleStrings.CHOICE_AMMONIA, 11.5, UNASSIGNED_BASE_COLOR );
    private static final LiquidDescriptor BLEACH = new LiquidDescriptor( PHScaleStrings.CHOICE_BLEACH, 12.5, UNASSIGNED_BASE_COLOR );
    private static final LiquidDescriptor DRAIN_CLEANER = new LiquidDescriptor( PHScaleStrings.CHOICE_DRAIN_CLEANER, 13, UNASSIGNED_BASE_COLOR );
    private static final LiquidDescriptor LYE = new LiquidDescriptor( PHScaleStrings.CHOICE_LYE, 13.5, UNASSIGNED_BASE_COLOR );
    
    // all instances
    private static final LiquidDescriptor[] ALL_INSTANCES = new LiquidDescriptor[] {
        BATTERY_ACID, BILE, LEMON_JUICE, COLA, VINEGAR, ORANGE_JUICE, BEER, COFFEE,
        TEA, ACID_RAIN, MILK, WATER, SALIVA, BLOOD, SEA_WATER, HAND_SOAP, AMMONIA,
        BLEACH, DRAIN_CLEANER, LYE
    };
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final String _name;
    private final double _pH;
    private final Color _color;
    
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
        return LEMON_JUICE;
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
    
    public String toString() { 
        // this format is displayed by LiquidComboBox
        return _name + " (" + PH_FORMAT.format( _pH ) + ")";
    }
}
