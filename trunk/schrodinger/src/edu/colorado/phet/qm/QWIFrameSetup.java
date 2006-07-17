package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 17, 2006
 * Time: 9:30:18 AM
 * Copyright (c) Jul 17, 2006 by Sam Reid
 */

public class QWIFrameSetup implements FrameSetup {

    public void initialize( JFrame frame ) {
        int width = 912;
        int height = 732;
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int x = ( d.width - width ) / 2;
        int y = 0;
        frame.setLocation( x, y );
        frame.setSize( width, height );
    }
}
