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

import edu.colorado.phet.boundstates.enums.BSWellType;
import edu.colorado.phet.boundstates.model.BSDoubleRange;
import edu.colorado.phet.boundstates.model.BSIntegerRange;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSTwoWellsModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSTwoWellsModule extends BSAbstractModule {

    private static final BSWellType[] WELL_TYPES = { BSWellType.SQUARE, BSWellType.COULOMB_1D };

    private static final boolean SUPPORTS_SUPERPOSITION = true;
    
    // Ranges (min, max, default)
    private static final BSIntegerRange NUMBER_OF_WELLS_RANGE = new BSIntegerRange( 2, 2, 2 );
    private static final BSDoubleRange OFFSET_RANGE = new BSDoubleRange( -15, 5, 0 ); // eV
    private static final BSDoubleRange DEPTH_RANGE = new BSDoubleRange( 0, 20, 10 ); // eV
    private static final BSDoubleRange WIDTH_RANGE = new BSDoubleRange( 0.1, 3, 1.5 ); // nm
    private static final BSDoubleRange SPACING_RANGE = new BSDoubleRange( 0.3, 3, 1 ); // nm
    private static final BSDoubleRange SEPARATION_RANGE = new BSDoubleRange( 0.05, 0.7, 0.3 ); // nm
    private static final BSDoubleRange ANGULAR_FREQUENCY_RANGE = new BSDoubleRange( 1, 2.5, 1 ); // fs^-1

    public BSTwoWellsModule() {
        super( SimStrings.get( "BSTwoWellsModule.title" ), 
                WELL_TYPES,
                SUPPORTS_SUPERPOSITION,
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
