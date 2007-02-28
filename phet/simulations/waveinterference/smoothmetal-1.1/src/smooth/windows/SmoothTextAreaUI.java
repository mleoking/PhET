package smooth.windows;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import java.awt.*;

public class SmoothTextAreaUI extends BasicTextAreaUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTextAreaUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
