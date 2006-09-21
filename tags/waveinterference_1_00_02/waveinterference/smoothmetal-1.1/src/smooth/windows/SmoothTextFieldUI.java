package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsTextFieldUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothTextFieldUI extends WindowsTextFieldUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTextFieldUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
