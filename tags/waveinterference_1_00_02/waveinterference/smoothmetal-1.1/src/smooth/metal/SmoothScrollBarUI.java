package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;
import java.awt.*;

public class SmoothScrollBarUI extends MetalScrollBarUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothScrollBarUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
