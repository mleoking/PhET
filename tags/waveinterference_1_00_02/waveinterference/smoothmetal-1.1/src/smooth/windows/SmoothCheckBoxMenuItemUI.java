package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsCheckBoxMenuItemUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothCheckBoxMenuItemUI extends WindowsCheckBoxMenuItemUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothCheckBoxMenuItemUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
