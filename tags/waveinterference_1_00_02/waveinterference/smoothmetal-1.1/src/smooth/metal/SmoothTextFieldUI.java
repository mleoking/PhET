package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTextFieldUI;
import java.awt.*;

public class SmoothTextFieldUI extends MetalTextFieldUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTextFieldUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
