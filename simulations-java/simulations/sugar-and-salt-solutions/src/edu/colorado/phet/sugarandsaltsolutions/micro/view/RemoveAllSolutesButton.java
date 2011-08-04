// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.RemoveSoluteButtonNode;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;

/**
 * Button that removes all solutes from the beaker.  We originally used "remove solute 1" and "remove solute 2" buttons specific to each kit, but
 * KL reported that it seemed awkward e.g., to remove only some of the Cl ions in the case of NaCl and CaCl2, and only some of the Na ions in the case of NaCl and NaNO3.
 *
 * @author Sam Reid
 */
public class RemoveAllSolutesButton extends RemoveSoluteButtonNode {
    public RemoveAllSolutesButton( final MicroModel model ) {
        super( "Remove solutes", model.getAnySolutes(), new VoidFunction0() {
            public void apply() {
                model.clearSolutes();
            }
        } );
    }
}