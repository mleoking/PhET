// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.modules;

import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.view.AbstractDilutionsCanvas;

/**
 * The "Molarity" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityModule extends AbstractDilutionsModule {

    public MolarityModule() {
        super( Strings.MOLARITY );
        setSimulationPanel( new AbstractDilutionsCanvas() {
        } );
    }
}