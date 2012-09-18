// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;

/**
 * Button that allows the user to navigate back.
 *
 * @author Sam Reid
 */
public class BackButton extends PNode {

    public BackButton( final VoidFunction0 pressed, ObservableProperty<Boolean> enabled ) {
        addChild( new SpinnerButtonNode( rescale( Images.LEFT_BUTTON_UP ), rescale( Images.LEFT_BUTTON_PRESSED ), rescale( Images.LEFT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean spinning ) {
                if ( !spinning ) {
                    SimSharingManager.sendButtonPressed( Components.backButton );
                    pressed.apply();
                }
            }
        }, enabled ) );
    }

    public static BufferedImage rescale( final BufferedImage image ) { return multiScaleToWidth( image, 50 ); }
}