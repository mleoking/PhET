/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.enums;


public class LightType extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private LightType( String name ) {
        super( name );
    }
    
    // Well type values
    public static final LightType WHITE = new LightType( "white" );
    public static final LightType MONOCHROMATIC = new LightType( "monochromatic" );
    
    /**
     * Retrieves a well type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return the well type that corresponds to name, possibly null
     */
    public static LightType getByName( String name ) {
        LightType lightType = null;
        if ( WHITE.isNamed( name ) ) {
            lightType = WHITE;
        }
        else if ( MONOCHROMATIC.isNamed( name ) ) {
            lightType = MONOCHROMATIC;
        }
        return lightType;
    }
}
