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

import edu.colorado.phet.boundstates.control.BSWellComboBox.WellChoice;
import edu.colorado.phet.boundstates.enum.WellType;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSDoubleModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSDoubleModule extends BSManyModule {

    public BSDoubleModule() {
        super( SimStrings.get( "BSDoubleModule.title" ) );
        setNumberOfWells( 2 );
        setNumberOfWellsControlVisible( false );
        WellType[] wellTypeChoices = { WellType.COULOMB, WellType.SQUARE };
        setWellTypeChoices( wellTypeChoices );
    }
}
