package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSplitPaneUI;
import java.awt.*;

public class SmoothSplitPaneUI extends MetalSplitPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothSplitPaneUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
