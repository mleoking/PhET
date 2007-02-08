/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:11:47 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class LeftSideOfScreen implements FrameSetup {
    private int insetX;
    private int insetY;

    public LeftSideOfScreen( int insetX, int insetY ) {
        this.insetX = insetX;
        this.insetY = insetY;
    }

    public void initialize( JFrame frame ) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize( dim.width - insetX, dim.height - insetY * 2 );
        frame.setLocation( 0, insetY );
    }
}
