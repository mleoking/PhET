// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;

import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * @author Sam Reid
 */
public class SpinnerButtonPanelHBox extends HBox {
    public SpinnerButtonPanelHBox( VoidFunction0 up, ObservableProperty<Boolean> upEnabled, VoidFunction0 down, ObservableProperty<Boolean> downEnabled ) {
        this( 50, up, upEnabled, down, downEnabled );
    }

    public SpinnerButtonPanelHBox( int size, VoidFunction0 up, ObservableProperty<Boolean> upEnabled, VoidFunction0 down, ObservableProperty<Boolean> downEnabled ) {
        super( 2,
               new SpinnerButtonNode( multiscale( ROUND_BUTTON_DOWN, size ), multiscale( ROUND_BUTTON_DOWN_PRESSED, size ), multiscale( ROUND_BUTTON_DOWN_GRAY, size ), down, downEnabled ),
               new SpinnerButtonNode( multiscale( ROUND_BUTTON_UP, size ), multiscale( ROUND_BUTTON_UP_PRESSED, size ), multiscale( ROUND_BUTTON_UP_GRAY, size ), up, upEnabled ) );
    }

    private static BufferedImage multiscale( BufferedImage im, int size ) {
        return BufferedImageUtils.multiScaleToHeight( im, size );
    }
}
