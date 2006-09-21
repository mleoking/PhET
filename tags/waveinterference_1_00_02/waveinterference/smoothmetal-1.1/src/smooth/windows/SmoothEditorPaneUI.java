package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsEditorPaneUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothEditorPaneUI extends WindowsEditorPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothEditorPaneUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
