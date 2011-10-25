// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.modules;

import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.AbstractDilutionsModule;
import edu.colorado.phet.dilutions.common.view.AbstractDilutionsCanvas;

/**
 * The "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionModule extends AbstractDilutionsModule {

    public DilutionModule() {
        super( Strings.DILUTION );
        setSimulationPanel( new AbstractDilutionsCanvas() {
        } );
    }
}
