package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTreeUI;
import java.awt.*;

public class SmoothTreeUI extends MetalTreeUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothTreeUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
