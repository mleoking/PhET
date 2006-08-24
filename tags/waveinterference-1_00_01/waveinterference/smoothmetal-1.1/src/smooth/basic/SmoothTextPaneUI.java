package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextPaneUI;
import java.awt.*;

public class SmoothTextPaneUI extends BasicTextPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTextPaneUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
