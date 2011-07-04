// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.model;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.sugarandsaltsolutions.common.model.BeakerDimension;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

/**
 * Introductory (macro) model that keeps track of moles of solute dissolved in the liquid.
 *
 * @author Sam Reid
 */
public class MacroModel extends SugarAndSaltSolutionModel {
    public MacroModel() {
        super( new ConstantDtClock( 30 ), new BeakerDimension( 0.2 ) );
    }
}