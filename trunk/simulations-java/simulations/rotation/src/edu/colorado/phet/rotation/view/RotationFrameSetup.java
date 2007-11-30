package edu.colorado.phet.rotation.view;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * Author: Sam Reid
 * Jul 9, 2007, 3:26:36 PM
 */
public class RotationFrameSetup implements FrameSetup {
    public void initialize( JFrame frame ) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if ( screenSize.height <= 768 ) {
            new FrameSetup.MaxExtent( new FrameSetup.TopCenter( screenSize.width, 700 ) ).initialize( frame );
        }
        else {
            new FrameSetup.TopCenter( screenSize.width, screenSize.height - 100 ).initialize( frame );
        }
    }
}
