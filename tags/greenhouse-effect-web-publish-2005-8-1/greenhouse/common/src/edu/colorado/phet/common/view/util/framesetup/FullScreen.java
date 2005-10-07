/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.util.framesetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:42:50 AM
 * Copyright (c) Jun 12, 2003 by Sam Reid
 */
public class FullScreen implements FrameSetup {
    public FullScreen() {
    }

    public void initialize(JFrame frame) {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(0, 0);
        frame.setSize(d);
    }
}
