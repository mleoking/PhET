// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;

import static edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerAndShakerCanvas.*;

/**
 * Reset all button used in all tabs, shown in the bottom right corner
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsResetAllButtonNode extends ResetAllButtonNode {
    public SugarAndSaltSolutionsResetAllButtonNode( final double stageWidth, final double stageHeight, final VoidFunction0 reset ) {
        super( new Resettable() {
            public void reset() {
                reset.apply();
            }
        }, null, CONTROL_FONT, Color.black, BUTTON_COLOR );

        //Have to set the offset after changing the font since it changes the size of the node
        setOffset( stageWidth - getFullBounds().getWidth() - INSET, stageHeight - getFullBounds().getHeight() - INSET );

        //Easy to get back to many states, so don't confirm reset
        setConfirmationEnabled( false );
    }
}