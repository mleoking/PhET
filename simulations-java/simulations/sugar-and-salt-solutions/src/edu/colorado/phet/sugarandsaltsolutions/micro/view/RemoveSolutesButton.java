// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.RemoveSoluteButtonNode;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;

/**
 * Button that removes all solutes from the beaker.  Reads "remove solute" if one solute type, "remove solutes" if two solute tyes
 *
 * @author Sam Reid
 */
public class RemoveSolutesButton extends RemoveSoluteButtonNode {
    public RemoveSolutesButton( String text, ObservableProperty<Boolean> visible, final MicroModel model ) {
        super( text, visible, new VoidFunction0() {
            public void apply() {
                model.clearSolutes();
            }
        } );
    }
}