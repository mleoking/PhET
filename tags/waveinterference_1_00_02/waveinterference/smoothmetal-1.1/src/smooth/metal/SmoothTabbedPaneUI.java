package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;
import java.awt.*;

public class SmoothTabbedPaneUI extends MetalTabbedPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTabbedPaneUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
