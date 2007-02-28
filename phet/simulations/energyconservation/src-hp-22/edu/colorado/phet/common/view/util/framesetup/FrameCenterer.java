/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.util.framesetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:45:43 AM
 * Copyright (c) Jun 12, 2003 by Sam Reid
 */
public class FrameCenterer implements FrameSetup {
    private int insetX;
    private int insetY;

    public FrameCenterer( int insetX, int insetY ) {
        this.insetX = insetX;
        this.insetY = insetY;
    }

    public void initialize( JFrame frame ) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int width = d.width - insetX * 2;
        int height = d.height - insetY * 2;
        frame.setSize( width, height );
        frame.setLocation( insetX, insetY );
    }
}
