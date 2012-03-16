// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.boundstates.module;

import edu.colorado.phet.boundstates.BSResources;


/**
 * BSOneWellModule is the "One Well" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BSOneWellModule extends BSAbstractModule {

    public BSOneWellModule() {
        super( BSResources.getString( "BSOneWellModule.title" ), new BSOneWellSpec() );
    }
}
