package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsProgressBarUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothProgressBarUI extends WindowsProgressBarUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothProgressBarUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
