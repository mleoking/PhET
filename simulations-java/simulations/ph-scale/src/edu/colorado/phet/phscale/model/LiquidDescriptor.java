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
    
    // instances for each liquid type
    public static final LiquidDescriptor WATER = new LiquidDescriptor( PHScaleStrings.CHOICE_WATER, 7, ColorUtils.createColor( PHScaleConstants.H2O_COLOR, ALPHA ) );
    public static final LiquidDescriptor MILK = new LiquidDescriptor( PHScaleStrings.CHOICE_MILK, 6.5, new Color( 255, 255, 255, ALPHA ) );
    public static final LiquidDescriptor BEER = new LiquidDescriptor( PHScaleStrings.CHOICE_BEER, 4.5, new Color( 185, 79, 5, ALPHA ) );
    public static final LiquidDescriptor COLA = new LiquidDescriptor( PHScaleStrings.CHOICE_COLA, 2.5, new Color( 122, 60, 35, ALPHA ) );
    public static final LiquidDescriptor LEMON_JUICE = new LiquidDescriptor( PHScaleStrings.CHOICE_LEMON_JUICE, 2.4, new Color(255, 255, 0, ALPHA ) );
    
    // all instances except water
    private static final LiquidDescriptor[] ALL_INSTANCES_EXCEPT_WATER = new LiquidDescriptor[] { MILK, BEER, COLA, LEMON_JUICE };
    
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
    
    public static LiquidDescriptor[] getAllInstancesExceptWater() {
        return ALL_INSTANCES_EXCEPT_WATER;
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
