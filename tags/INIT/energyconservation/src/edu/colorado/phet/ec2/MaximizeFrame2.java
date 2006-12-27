/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2;

import edu.colorado.phet.common.view.util.framesetup.FrameSetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 31, 2003
 * Time: 4:23:14 PM
 * Copyright (c) Jul 31, 2003 by Sam Reid
 */
public class MaximizeFrame2 implements FrameSetup {
    public MaximizeFrame2() {
    }

    public void initialize( JFrame jFrame ) {
        jFrame.setSize( Toolkit.getDefaultToolkit().getScreenSize() );
        jFrame.setExtendedState( JFrame.MAXIMIZED_BOTH );
//        jFrame.setVisible(true);
    }
}
