/*, 2003.*/
package edu.colorado.phet.greenhouse.phetcommon.view.util.framesetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 8:00:39 AM
 *
 */
public class AbsoluteFrameSetup implements FrameSetup {
    private int width;
    private int height;

    public AbsoluteFrameSetup(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void initialize(JFrame frame) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int x = (d.width - width) / 2;
        int y = (d.height - height) / 2;
        frame.setLocation(x, y);
        frame.setSize(width, height);
    }
}
