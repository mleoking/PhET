/*, 2003.*/
package edu.colorado.phet.greenhouse.phetcommon.view.util.framesetup;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:45:43 AM
 *
 */
public class FrameCenterer implements FrameSetup {
    private int insetX;
    private int insetY;

    public FrameCenterer(int insetX, int insetY) {
        this.insetX = insetX;
        this.insetY = insetY;
    }

    public static void setup(Window w,int insetX,int insetY)
    {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();
        int width = d.width - insetX * 2;
        int height = d.height - insetY * 2;
        w.setSize(width, height);
        w.setLocation(insetX, insetY);
    }

    public void initialize(JFrame frame) {
        setup(frame, insetX, insetY);
    }
}
