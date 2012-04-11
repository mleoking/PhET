// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.view;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToHeight;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * Shows an up and down spinner in a VBox.
 *
 * @author Sam Reid
 */
public class SpinnerButtonPanelVBox extends VBox {
    public SpinnerButtonPanelVBox( VoidFunction1<Boolean> up, ObservableProperty<Boolean> upEnabled, VoidFunction1<Boolean> down, ObservableProperty<Boolean> downEnabled ) {
        this( 50, up, upEnabled, down, downEnabled );
    }

    public SpinnerButtonPanelVBox( int size, VoidFunction1<Boolean> up, ObservableProperty<Boolean> upEnabled, VoidFunction1<Boolean> down, ObservableProperty<Boolean> downEnabled ) {
        super( 2,
               new SpinnerButtonNode( multiScaleToHeight( ROUND_BUTTON_UP, size ), multiScaleToHeight( ROUND_BUTTON_UP_PRESSED, size ), multiScaleToHeight( ROUND_BUTTON_UP_GRAY, size ), up, upEnabled ),
               new SpinnerButtonNode( multiScaleToHeight( ROUND_BUTTON_DOWN, size ), multiScaleToHeight( ROUND_BUTTON_DOWN_PRESSED, size ), multiScaleToHeight( ROUND_BUTTON_DOWN_GRAY, size ), down, downEnabled ) );
    }
}