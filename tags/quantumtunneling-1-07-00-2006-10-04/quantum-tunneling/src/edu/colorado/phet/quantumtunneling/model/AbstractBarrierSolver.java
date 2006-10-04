/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import edu.colorado.phet.quantumtunneling.enums.Direction;
import edu.colorado.phet.quantumtunneling.util.Complex;

/**
 * AbstractBarrierSolver is the base class for all classes that implement
 * closed-form solutions to the wave function equation for a plane wave
 * with barriers.
 * <p>
 * This base class was created to address some quirks that occur
 * in the calculation of coefficients for barriers.
 * See getK() for details.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractBarrierSolver extends AbstractPlaneSolver {

    // See getK() for details on this value
    private static final Complex SMALL_K = new Complex( 0.00001, 0 );
    
    /**
     * Constructor.
     * @param te
     * @param pe
     * @param direction
     */
    public AbstractBarrierSolver( TotalEnergy te, AbstractPotential pe, Direction direction ) {
        super( te, pe, direction );
    }
    
    /**
     * Gets the k value for a specified region, adjusted to address quirks with barriers.
     * 
     * For a single barrier if k2=0, and for a double barrier if k2=0 or k4=0,
     * the denominators in the coefficients go to zero so that the coefficients
     * themselves are undefined, so there is no plane wave. This is a quirk, as
     * everything is well-defined around these values.  
     * 
     * The workaround (prescribed by Sam McKagan) is a follows:
     * Whenever k2 or k4 is zero for barriers, set them to a small
     * positive value. Because k is used in an expotential, this small value
     * must be chosen to work properly with the precision used in this
     * simulation; a value that is too small or too large will result in
     * incorrect behavior.
     *  
     * The constant SMALL_K was chosen through a process of experimentation.
     * 
     * Note that k2 is in region 1, and k4 is in region 3.
     * 
     * @param regionIndex region index, which starts at zero
     * @return k value, possibly adjusted
     */
    protected Complex getK( final int regionIndex ) {
        Complex k = super.getK( regionIndex );
        if ( regionIndex == 1 || regionIndex == 3 ) {
            // Adjust k2 or k4
            if ( k.equals( Complex.ZERO ) ) {
                k = SMALL_K;
            }
        }
        return k;
    }
}
