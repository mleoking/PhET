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
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.boundstates.util.IntegerRange;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSTwoWellsModule is the "Two Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSTwoWellsModule extends BSAbstractModule {

    public BSTwoWellsModule() {
        super( SimStrings.get( "BSTwoWellsModule.title" ), new BSTwoWellsSpec() );
        getClock().start();
    }
}
