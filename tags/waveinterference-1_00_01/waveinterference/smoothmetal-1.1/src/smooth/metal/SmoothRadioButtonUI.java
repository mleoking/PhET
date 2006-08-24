package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalRadioButtonUI;
import java.awt.*;

public class SmoothRadioButtonUI extends MetalRadioButtonUI {
    private static final ComponentUI ui = new SmoothRadioButtonUI();

    public static ComponentUI createUI( JComponent jcomponent ) {
        return ui;
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
