package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import java.awt.*;

public class SmoothPasswordFieldUI extends BasicPasswordFieldUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothPasswordFieldUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
