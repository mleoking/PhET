package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsMenuItemUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothMenuItemUI extends WindowsMenuItemUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothMenuItemUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
