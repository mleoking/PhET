// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;

import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * @author Sam Reid
 */
public class SpinnerButtonPanelVBox extends VBox {
    public SpinnerButtonPanelVBox( VoidFunction0 up, ObservableProperty<Boolean> upEnabled, VoidFunction0 down, ObservableProperty<Boolean> downEnabled ) {
        this( 50, up, upEnabled, down, downEnabled );
    }

    public SpinnerButtonPanelVBox( int size, VoidFunction0 up, ObservableProperty<Boolean> upEnabled, VoidFunction0 down, ObservableProperty<Boolean> downEnabled ) {
        super( 2,
               new SpinnerButtonNode( multiscale( ROUND_BUTTON_UP, size ), multiscale( ROUND_BUTTON_UP_PRESSED, size ), multiscale( ROUND_BUTTON_UP_GRAY, size ), up, upEnabled ),
               new SpinnerButtonNode( multiscale( ROUND_BUTTON_DOWN, size ), multiscale( ROUND_BUTTON_DOWN_PRESSED, size ), multiscale( ROUND_BUTTON_DOWN_GRAY, size ), down, downEnabled ) );
    }

    private static BufferedImage multiscale( BufferedImage im, int size ) {
        return BufferedImageUtils.multiScaleToHeight( im, size );
    }
}
