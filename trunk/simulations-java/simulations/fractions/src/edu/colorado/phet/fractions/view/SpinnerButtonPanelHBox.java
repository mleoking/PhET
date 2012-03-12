// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * Shows two spinners side by side.
 *
 * @author Sam Reid
 */
public class SpinnerButtonPanelHBox extends HBox {
    public SpinnerButtonPanelHBox( VoidFunction0 up, ObservableProperty<Boolean> upEnabled, VoidFunction0 down, ObservableProperty<Boolean> downEnabled ) {
        this( 50, up, upEnabled, down, downEnabled );
    }

    public SpinnerButtonPanelHBox( int size, VoidFunction0 up, ObservableProperty<Boolean> upEnabled, VoidFunction0 down, ObservableProperty<Boolean> downEnabled ) {
        super( 2,
               new SpinnerButtonNode( multiScaleToHeight( LEFT_BUTTON_UP, size ), multiScaleToHeight( LEFT_BUTTON_PRESSED, size ), multiScaleToHeight( LEFT_BUTTON_GRAY, size ), down, downEnabled ),
               new SpinnerButtonNode( multiScaleToHeight( RIGHT_BUTTON_UP, size ), multiScaleToHeight( RIGHT_BUTTON_PRESSED, size ), multiScaleToHeight( RIGHT_BUTTON_GRAY, size ), up, upEnabled ) );
    }
}
