// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
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
        subtractButton = new SingleButton( multiScaleToWidth( MINUS_BUTTON, 50 ), multiScaleToWidth( MINUS_BUTTON_PRESSED, 50 ), subtract );
        addButton = new SingleButton( multiScaleToWidth( PLUS_BUTTON, 50 ), multiScaleToWidth( PLUS_BUTTON_PRESSED, 50 ), add );
        addChild( new VBox( addButton, subtractButton ) );
        subtractButton.setTransparency( 0 );
        subtractButton.setAllPickable( false );
    }

    //REVIEW: IntelliJ flagged the following three methods as returning values
    //that are never used.  Suggest either converting to void or documenting
    //why they should return the activity.
    public PInterpolatingActivity hideIncreaseButton() {
        addButton.setAllPickable( false );
        return addButton.animateToTransparency( 0, 200 );
    }

    public PInterpolatingActivity hideDecreaseButton() {
        subtractButton.setAllPickable( false );
        return subtractButton.animateToTransparency( 0, 200 );
    }

    public PInterpolatingActivity showIncreaseButton() {
        addButton.setAllPickable( true );
        return addButton.animateToTransparency( 1, 200 );
    }

    public PInterpolatingActivity showDecreaseButton() {
        subtractButton.setAllPickable( true );
        return subtractButton.animateToTransparency( 1, 200 );
    }

}