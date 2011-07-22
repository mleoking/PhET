// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;

/**
 * Button for moving forward through the kits
 *
 * @author Sam Reid
 */
public class ForwardButton extends ArrowButton {
    public ForwardButton() {
        super( flipX( LEFT_ARROW ) );
    }
}