package edu.colorado.phet.distanceladder.common.view.util.framesetup;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 7:28:19 PM
 *
 */
public class MaxExtentFrameSetup implements FrameSetup {
    FrameSetup pre = null;

    public MaxExtentFrameSetup() {
    }

    public MaxExtentFrameSetup( FrameSetup pre ) {
        this.pre = pre;
    }

    public void initialize( JFrame frame ) {
        if( pre != null ) {
            pre.initialize( frame );
        }
        frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
    }
}
