package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsMenuBarUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothMenuBarUI extends WindowsMenuBarUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothMenuBarUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
