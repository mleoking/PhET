// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Frame;

import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.view.BLLModule;

/**
 * The "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModule extends BLLModule {

    public ConcentrationModule( Frame parentFrame ) {
        super( UserComponents.concentrationTab, Strings.CONCENTRATION );
        setSimulationPanel( new ConcentrationCanvas( new ConcentrationModel( getClock() ), parentFrame ) );
    }
}
