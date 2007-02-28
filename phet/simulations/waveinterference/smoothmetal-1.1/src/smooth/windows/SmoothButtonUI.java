package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsButtonUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Smooth Button UI delegate. Responsible for drawing a
 * button. Most UI delegates are similar to this one, the
 * <code>paint()</code> method turns on anti-aliasing and
 * the other methods are merely to install this delegate.
 */
public class SmoothButtonUI extends WindowsButtonUI {
    private static final ComponentUI ui = new SmoothButtonUI();

    public static ComponentUI createUI( JComponent jcomponent ) {
        return ui;
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}