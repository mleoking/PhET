package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalProgressBarUI;
import java.awt.*;

public class SmoothProgressBarUI extends MetalProgressBarUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothProgressBarUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
