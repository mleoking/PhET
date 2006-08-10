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
 * BSOneWellModule is the "One Well" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSOneWellModule extends BSAbstractModule {

    public BSOneWellModule() {
        super( SimStrings.get( "BSOneWellModule.title" ), new BSOneWellSpec() );
        getClock().start();
    }
}
