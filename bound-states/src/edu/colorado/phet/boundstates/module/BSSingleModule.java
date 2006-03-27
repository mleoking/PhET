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

import edu.colorado.phet.boundstates.enums.WellType;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSSingleModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSingleModule extends BSManyModule {

    public BSSingleModule() {
        super( SimStrings.get( "BSSingleModule.title" ) );
        setNumberOfWells( 1 );
        setNumberOfWellsControlVisible( false );
        WellType[] wellTypeChoices = { WellType.COULOMB, WellType.HARMONIC_OSCILLATOR, WellType.SQUARE, WellType.ASYMMETRIC };
        setWellTypeChoices( wellTypeChoices );
    }
}
