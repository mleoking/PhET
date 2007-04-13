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
 * BSTwoWellsModule is the "Two Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSTwoWellsModule extends BSAbstractModule {

    public BSTwoWellsModule() {
        super( BSResources.getString( "BSTwoWellsModule.title" ), new BSTwoWellsSpec() );
        getClock().start();
    }
}
