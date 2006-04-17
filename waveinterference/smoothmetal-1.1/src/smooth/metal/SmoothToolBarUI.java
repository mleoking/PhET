package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToolBarUI;
import java.awt.*;

public class SmoothToolBarUI extends MetalToolBarUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothToolBarUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
