package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsToolBarUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothToolBarUI extends WindowsToolBarUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothToolBarUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
