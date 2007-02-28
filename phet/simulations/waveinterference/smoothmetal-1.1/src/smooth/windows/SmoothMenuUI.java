package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsMenuUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothMenuUI extends WindowsMenuUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothMenuUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
