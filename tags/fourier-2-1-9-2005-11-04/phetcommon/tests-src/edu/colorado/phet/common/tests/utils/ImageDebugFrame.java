/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.tests.utils;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 11, 2005
 * Time: 6:56:12 PM
 * Copyright (c) Apr 11, 2005 by Sam Reid
 */

public class ImageDebugFrame extends JFrame {
    public ImageDebugFrame( Image im ) {
        setContentPane( new JLabel( new ImageIcon( im ) ) );
        pack();
    }
}
