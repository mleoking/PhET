// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * Button that can be used to add or remove additional containers.
 *
 * @author Sam Reid
 */
class IncreaseDecreaseButton extends PNode {

    private final SingleButton subtractButton;
    private final SingleButton addButton;

    public IncreaseDecreaseButton( final VoidFunction0 add, VoidFunction0 subtract ) {
        subtractButton = new SingleButton( multiScaleToWidth( MINUS_BUTTON, 50 ), multiScaleToWidth( MINUS_BUTTON_PRESSED, 50 ), multiScaleToWidth( MINUS_BUTTON_GRAY, 50 ), subtract );
        addButton = new SingleButton( multiScaleToWidth( PLUS_BUTTON, 50 ), multiScaleToWidth( PLUS_BUTTON_PRESSED, 50 ), multiScaleToWidth( PLUS_BUTTON_GRAY, 50 ), add );
        addChild( new VBox( addButton, subtractButton ) );
        subtractButton.setTransparency( 0 );
        subtractButton.setAllPickable( false );
    }

    //Hide the increase button.
    public void hideIncreaseButton() {
        addButton.setAllPickable( false );
        addButton.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
    }

    //Hide the decrease button.
    public void hideDecreaseButton() {
        subtractButton.setAllPickable( false );
        subtractButton.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
    }

    //Show the increase button.
    public void showIncreaseButton() {
        addButton.setAllPickable( true );
        addButton.animateToTransparency( 1, BuildAFractionModule.ANIMATION_TIME );
    }

    //Show the decrease button.  Return the activity in case client needs to attach a delegate e.g., to listen for completion
    public PInterpolatingActivity showDecreaseButton() {
        subtractButton.setAllPickable( true );
        return subtractButton.animateToTransparency( 1, BuildAFractionModule.ANIMATION_TIME );
    }

    public void setEnabled( final boolean pickable ) {
        setPickable( pickable );
        setChildrenPickable( pickable );

        subtractButton.setEnabled( pickable );
        addButton.setEnabled( pickable );
    }
}