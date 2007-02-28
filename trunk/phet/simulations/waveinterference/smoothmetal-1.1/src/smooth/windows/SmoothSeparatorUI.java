package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsSeparatorUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothSeparatorUI extends WindowsSeparatorUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothSeparatorUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
