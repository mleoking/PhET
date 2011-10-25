// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.modules;

import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.AbstractDilutionsModule;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;

/**
 * The "Dilution Equation" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawLabModule extends AbstractDilutionsModule {

    public BeersLawLabModule() {
        super( Strings.BEERS_LAW_LAB );
        setSimulationPanel( new AbstractDilutionsCanvas() {
        } );
    }
}
