package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsSpinnerUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothSpinnerUI extends WindowsSpinnerUI {

    public static ComponentUI createUI( final JComponent jcomponent ) {
        return new SmoothSpinnerUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
