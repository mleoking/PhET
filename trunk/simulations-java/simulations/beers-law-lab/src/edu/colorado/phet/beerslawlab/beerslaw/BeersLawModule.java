// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw;

import java.awt.Frame;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.view.BLLModule;

/**
 * The "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawModule extends BLLModule {

    public BeersLawModule( Frame parentFrame ) {
        super( UserComponents.beersLawTab, Strings.TAB_BEERS_LAW );
        setSimulationPanel( new BeersLawCanvas( new BeersLawModel(), parentFrame ) );
    }
}
