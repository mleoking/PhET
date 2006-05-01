/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import org.jfree.data.Range;

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.boundstates.model.BSIntegerRange;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSOneWellModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSOneWellModule extends BSAbstractModule {

    private static final BSWellType[] WELL_TYPES = 
        { BSWellType.SQUARE, BSWellType.ASYMMETRIC, BSWellType.COULOMB, BSWellType.HARMONIC_OSCILLATOR };
   
    // Ranges (min, max, default)
    private static final BSIntegerRange NUMBER_OF_WELLS_RANGE = new BSIntegerRange( 1, 1, 1 );
    private static final BSDoubleRange OFFSET_RANGE = new BSDoubleRange( -15, 5, 0 ); // eV
    private static final BSDoubleRange DEPTH_RANGE = new BSDoubleRange( 0, 20, 10 ); // eV
    private static final BSDoubleRange WIDTH_RANGE = new BSDoubleRange( 0.1, 8, 1 ); // nm
    private static final BSDoubleRange SPACING_RANGE = new BSDoubleRange( 0, 0, 0 ); // nm (don't care for one well)
    private static final BSDoubleRange SEPARATION_RANGE = new BSDoubleRange( 0, 0, 0 ); // nm (don't care for one well)
    private static final BSDoubleRange ANGULAR_FREQUENCY_RANGE = new BSDoubleRange( 1, 2.5, 1 ); // fs^-1
    
    public BSOneWellModule() {
        super( SimStrings.get( "BSOneWellModule.title" ),
                WELL_TYPES,
                NUMBER_OF_WELLS_RANGE,
                OFFSET_RANGE,
                DEPTH_RANGE,
                WIDTH_RANGE,
                SPACING_RANGE,
                SEPARATION_RANGE,
                ANGULAR_FREQUENCY_RANGE );
        
        setWellType( BSWellType.SQUARE );
        getClock().start();
    }
}
