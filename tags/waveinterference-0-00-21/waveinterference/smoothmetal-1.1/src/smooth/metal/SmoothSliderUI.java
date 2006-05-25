package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSliderUI;
import java.awt.*;

public class SmoothSliderUI extends MetalSliderUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothSliderUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
