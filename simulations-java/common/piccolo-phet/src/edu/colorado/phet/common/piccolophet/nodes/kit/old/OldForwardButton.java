// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit.old;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.flipX;

/**
 * Button for moving forward through the kits
 *
 * @author Sam Reid
 */
public class OldForwardButton extends OldArrowButton {
    public OldForwardButton() {
        super( flipX( LEFT_ARROW ) );
    }
}