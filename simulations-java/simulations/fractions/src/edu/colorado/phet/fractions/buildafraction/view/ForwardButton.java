// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.common.view.SpinnerButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class ForwardButton extends PNode {
    public ForwardButton( final VoidFunction0 pressed ) {
        addChild( new SpinnerButtonNode( BackButton.scale( Images.RIGHT_BUTTON_UP ), BackButton.scale( Images.RIGHT_BUTTON_PRESSED ), BackButton.scale( Images.RIGHT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean spinning ) {
                pressed.apply();
            }
        } ) );
    }
}