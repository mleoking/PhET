package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsScrollBarUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothScrollBarUI extends WindowsScrollBarUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothScrollBarUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
