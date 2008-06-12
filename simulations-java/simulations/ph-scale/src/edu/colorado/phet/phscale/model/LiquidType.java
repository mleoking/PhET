/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import java.awt.Color;
import java.text.DecimalFormat;

import edu.colorado.phet.phscale.PHScaleStrings;


public class LiquidType {
    
    // Liquid types
    public static final LiquidType MILK = new LiquidType( PHScaleStrings.CHOICE_MILK, 6.5, Color.WHITE );
    public static final LiquidType BEER = new LiquidType( PHScaleStrings.CHOICE_BEER, 4.5, new Color( 185, 79, 5 ) );
    public static final LiquidType COLA = new LiquidType( PHScaleStrings.CHOICE_COLA, 2.5, new Color( 122, 60, 35 ) );
    public static final LiquidType LEMON_JUICE = new LiquidType( PHScaleStrings.CHOICE_LEMON_JUICE, 2.4, Color.YELLOW );
    
    // Array of all liquid types
    public static final LiquidType[] ALL_TYPES = new LiquidType[] { MILK, BEER, COLA, LEMON_JUICE };
    
    // pH format
    private static final DecimalFormat PH_FORMAT = new DecimalFormat( "0.0" );
    
    // Gets all liquid types
    public static LiquidType[] getAll() {
        return ALL_TYPES;
    }
    
    /**
     * Retrieves a liquid type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return the well type that corresponds to name, possibly null
     */
    public static LiquidType getByName( String name ) {
        LiquidType liquidType = null;
        if ( MILK.isNamed( name ) ) {
            liquidType = MILK;
        }
        else if ( BEER.isNamed( name ) ) {
            liquidType = BEER;
        }
        else if ( COLA.isNamed( name ) ) {
            liquidType = COLA;
        }
        return liquidType;
    }

    private final String _name;
    private final double _pH;
    private final Color _color;
    
    private LiquidType( String name, double pH, Color color ) {
        _name = name;
        _pH = pH;
        _color = color;
    }
    
    public String getName() {
        return _name;
    }
    
    public boolean isNamed( String name ) {
        return _name.equals( name );
    }
    
    public double getPH() {
        return _pH;
    }
    
    public Color getColor() {
        return _color;
    }
    
    public String toString() { 
        return _name + " (" + PH_FORMAT.format( _pH ) + ")";
    }
}
