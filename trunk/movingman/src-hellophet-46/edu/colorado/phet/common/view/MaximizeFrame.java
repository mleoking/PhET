package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.common.view.util.framesetup.FullScreen;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 6, 2003
 * Time: 11:22:47 PM
 * To change this template use Options | File Templates.
 */
public class MaximizeFrame implements FrameSetup {
    public MaximizeFrame() {
    }

    public void initialize(JFrame jFrame) {
        jFrame.setVisible(true);
        //Just in case extended state not supported.
        new FullScreen().initialize(jFrame);
        jFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
