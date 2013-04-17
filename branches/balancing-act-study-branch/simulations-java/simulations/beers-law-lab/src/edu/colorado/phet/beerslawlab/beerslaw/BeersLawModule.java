// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw;

import java.awt.Frame;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawModel;
import edu.colorado.phet.beerslawlab.beerslaw.view.BeersLawCanvas;
import edu.colorado.phet.beerslawlab.common.BLLModule;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;

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
