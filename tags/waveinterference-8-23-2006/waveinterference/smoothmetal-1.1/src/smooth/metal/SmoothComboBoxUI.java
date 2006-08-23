package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalComboBoxUI;
import java.awt.*;

public class SmoothComboBoxUI extends MetalComboBoxUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothComboBoxUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
