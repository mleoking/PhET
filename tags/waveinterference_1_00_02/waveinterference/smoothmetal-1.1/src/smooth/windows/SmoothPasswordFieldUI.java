package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsPasswordFieldUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothPasswordFieldUI extends WindowsPasswordFieldUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothPasswordFieldUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
