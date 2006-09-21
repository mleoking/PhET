package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSeparatorUI;
import java.awt.*;

public class SmoothSeparatorUI extends MetalSeparatorUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothSeparatorUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
