/* Copyright 2008, University of Colorado */

package edu.colorado.phet.phscale.model;

import edu.colorado.phet.phscale.PHScaleStrings;


public class LiquidType {
    
    // Well type values
    public static final LiquidType LEMON_JUICE = new LiquidType( PHScaleStrings.CHOICE_LEMON_JUICE );
    public static final LiquidType MILK = new LiquidType( PHScaleStrings.CHOICE_MILK );
    public static final LiquidType BEER = new LiquidType( PHScaleStrings.CHOICE_BEER );
    public static final LiquidType COLA = new LiquidType( PHScaleStrings.CHOICE_COLA );
    
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

    private String _name;
    
    private LiquidType( String name ) {
        _name = name;
    }
    
    public String getName() {
        return _name;
    }
    
    public boolean isNamed( String name ) {
        return _name.equals( name );
    }
    
    public String toString() { 
        return _name;
    }
}
