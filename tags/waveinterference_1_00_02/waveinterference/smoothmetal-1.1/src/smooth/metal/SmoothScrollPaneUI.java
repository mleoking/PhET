package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollPaneUI;
import java.awt.*;

public class SmoothScrollPaneUI extends MetalScrollPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothScrollPaneUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
