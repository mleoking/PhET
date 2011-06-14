// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.ISugarAndSaltModel;

/**
 * Introductory (macro) model that keeps track of moles of solute dissolved in the liquid.
 *
 * @author Sam Reid
 */
public class MacroModel extends SugarAndSaltSolutionModel implements ISugarAndSaltModel {
    public ObservableProperty<Boolean> isAnySaltToRemove() {
        return salt.moles.greaterThan( 0.0 );
    }

    public ObservableProperty<Boolean> isAnySugarToRemove() {
        return sugar.moles.greaterThan( 0.0 );
    }
}