// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.modules;

import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.view.AbstractDilutionsCanvas;

/**
 * The "Dilution Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionIntroModule extends AbstractDilutionsModule {

    public DilutionIntroModule() {
        super( Strings.DILUTION_INTRO );
        setSimulationPanel( new AbstractDilutionsCanvas() {
        } );
    }
}
