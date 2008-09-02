/*, 2003.*/
package edu.colorado.phet.greenhouse.common_greenhouse.view.util.framesetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:42:50 AM
 *
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
