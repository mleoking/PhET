package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothTreeUI extends WindowsTreeUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTreeUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
