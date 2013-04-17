// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.common.view.SpinnerButtonNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractions.FractionsResources.Images.*;
import static edu.colorado.phet.fractions.common.view.BackButton.rescale;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.forwardButton;

/**
 * Button that lets the user move forward (to the next set of divisions or the next level).
 *
 * @author Sam Reid
 */
class ForwardButton extends PNode {

    public ForwardButton( final VoidFunction0 pressed, ObservableProperty<Boolean> enabled ) {
        addChild( new SpinnerButtonNode( rescale( RIGHT_BUTTON_UP ), rescale( RIGHT_BUTTON_PRESSED ), rescale( RIGHT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean spinning ) {
                if ( !spinning ) {
                    SimSharingManager.sendButtonPressed( forwardButton );
                    pressed.apply();
                }
            }
        }, enabled ) );
    }
}