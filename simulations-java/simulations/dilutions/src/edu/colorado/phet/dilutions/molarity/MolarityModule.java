// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.molarity;

import java.awt.Frame;

import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.common.AbstractDilutionsModule;

/**
 * The "Molarity" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityModule extends AbstractDilutionsModule {

    public MolarityModule( Frame frame ) {
        super( Strings.MOLARITY );
        setSimulationPanel( new MolarityCanvas( new MolarityModel(), frame ) );
    }
}