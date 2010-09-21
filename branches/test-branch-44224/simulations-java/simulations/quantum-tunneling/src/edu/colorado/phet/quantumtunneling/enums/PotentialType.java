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
 * PotentialType is an enumeration of potential energy types.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class PotentialType extends AbstractEnum {

    /* This class is not intended for instantiation. */
    private PotentialType( String name ) {
        super( name );
    }
    
    // Potential Type values
    public static final PotentialType CONSTANT = new PotentialType( "constant" );
    public static final PotentialType STEP = new PotentialType( "step" );
    public static final PotentialType SINGLE_BARRIER = new PotentialType( "singleBarrier" );
    public static final PotentialType DOUBLE_BARRIER = new PotentialType( "doubleBarrier" );
    
    /**
     * Retrieves a wave type by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static PotentialType getByName( String name ) {
        PotentialType potentialType = null;
        if ( CONSTANT.isNamed( name ) ) {
            potentialType = CONSTANT;
        }
        else if ( STEP.isNamed( name ) ) {
            potentialType = STEP;
        }
        else if ( SINGLE_BARRIER.isNamed( name ) ) {
            potentialType = SINGLE_BARRIER;
        }
        else if ( DOUBLE_BARRIER.isNamed( name ) ) {
            potentialType = DOUBLE_BARRIER;
        }
        return potentialType;
    }
}
