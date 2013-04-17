// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration;

import java.awt.Frame;

import edu.colorado.phet.beerslawlab.common.BLLModule;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.beerslawlab.concentration.model.ConcentrationModel;
import edu.colorado.phet.beerslawlab.concentration.view.ConcentrationCanvas;

/**
 * The "Concentration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationModule extends BLLModule {

    public ConcentrationModule( Frame parentFrame ) {
        super( UserComponents.concentrationTab, Strings.TAB_CONCENTRATION );
        setSimulationPanel( new ConcentrationCanvas( new ConcentrationModel( getClock() ), parentFrame ) );
    }
}
