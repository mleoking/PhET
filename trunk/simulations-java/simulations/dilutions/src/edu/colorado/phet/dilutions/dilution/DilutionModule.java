// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.dilution;

import java.awt.Frame;

import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.AbstractDilutionsModule;

/**
 * The "Dilution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionModule extends AbstractDilutionsModule {

    public DilutionModule( Frame frame ) {
        super( Strings.DILUTION );
        setClockRunningWhenActive( false ); // no animation in this module
        setSimulationPanel( new DilutionCanvas( new DilutionModel(), frame ) );
    }
}
