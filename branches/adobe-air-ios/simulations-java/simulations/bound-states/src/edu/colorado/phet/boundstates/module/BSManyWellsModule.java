// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.boundstates.module;

import edu.colorado.phet.boundstates.BSResources;


/**
 * BSManyWellsModule is the "Many Wells" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BSManyWellsModule extends BSAbstractModule {

    public BSManyWellsModule() {
        super( BSResources.getString( "BSManyWellsModule.title" ), new BSManyWellsSpec() );
    }
}
