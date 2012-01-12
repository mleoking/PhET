// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.boundstates.module;

import edu.colorado.phet.boundstates.BSResources;


/**
 * BSTwoWellsModule is the "Two Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BSTwoWellsModule extends BSAbstractModule {

    public BSTwoWellsModule() {
        super( BSResources.getString( "BSTwoWellsModule.title" ), new BSTwoWellsSpec() );
    }
}
