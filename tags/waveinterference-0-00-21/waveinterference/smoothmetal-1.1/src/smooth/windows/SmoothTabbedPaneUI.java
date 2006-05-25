package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothTabbedPaneUI extends WindowsTabbedPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTabbedPaneUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
