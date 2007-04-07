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

import edu.colorado.phet.boundstates.BSResources;


/**
 * BSManyWellsModule is the "Many Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSManyWellsModule extends BSAbstractModule {

    public BSManyWellsModule() {
        super( BSResources.getString( "BSManyWellsModule.title" ), new BSManyWellsSpec() );
        getClock().start();
    }
}
