package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsTextPaneUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothTextPaneUI extends WindowsTextPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTextPaneUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
