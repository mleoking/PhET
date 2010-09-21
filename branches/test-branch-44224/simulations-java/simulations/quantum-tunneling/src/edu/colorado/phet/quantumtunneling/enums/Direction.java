/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.enums;


/**
 * Direction is an enumeration of wave directions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Direction extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private Direction( String name ) {
        super( name );
    }
    
    // Direction values
    public static final Direction LEFT_TO_RIGHT = new Direction( "leftToRight" );
    public static final Direction RIGHT_TO_LEFT = new Direction( "rightToLeft" );
    
    /**
     * Retrieves a direction by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static Direction getByName( String name ) {
        Direction direction = null;
        if ( LEFT_TO_RIGHT.isNamed( name ) ) {
            direction = LEFT_TO_RIGHT;
        }
        else if ( RIGHT_TO_LEFT.isNamed( name ) ) {
            direction = RIGHT_TO_LEFT;
        }
        return direction;
    }

}
