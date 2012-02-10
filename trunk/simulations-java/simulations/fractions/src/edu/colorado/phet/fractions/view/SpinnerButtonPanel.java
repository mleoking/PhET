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
public class SpinnerButtonPanel extends VBox {
    public SpinnerButtonPanel( VoidFunction0 up, ObservableProperty<Boolean> upEnabled, VoidFunction0 down, ObservableProperty<Boolean> downEnabled ) {
        super( 2,
               new SpinnerButtonNode( multiscale( ROUND_BUTTON_UP ), multiscale( ROUND_BUTTON_UP_PRESSED ), multiscale( ROUND_BUTTON_UP_GRAY ), up, upEnabled ),
               new SpinnerButtonNode( multiscale( ROUND_BUTTON_DOWN ), multiscale( ROUND_BUTTON_DOWN_PRESSED ), multiscale( ROUND_BUTTON_DOWN_GRAY ), down, downEnabled ) );
    }

    private static BufferedImage multiscale( BufferedImage im ) {
        return BufferedImageUtils.multiScaleToHeight( im, 50 );
    }
}
