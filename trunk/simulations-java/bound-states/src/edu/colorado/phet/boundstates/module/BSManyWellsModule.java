/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.module;

import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSManyWellsModule is the "Many Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSManyWellsModule extends BSAbstractModule {

    public BSManyWellsModule() {
        super( SimStrings.getInstance().getString( "BSManyWellsModule.title" ), new BSManyWellsSpec() );
        getClock().start();
    }
}
