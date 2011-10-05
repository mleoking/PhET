// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.modules;

import edu.colorado.phet.dilutions.DilutionsResources.Strings;
import edu.colorado.phet.dilutions.view.AbstractDilutionsCanvas;

/**
 * The "Dilution Equation" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionEquationModule extends AbstractDilutionsModule {

    public DilutionEquationModule() {
        super( Strings.DILUTION_EQUATION );
        setSimulationPanel( new AbstractDilutionsCanvas() {
        } );
    }
}
