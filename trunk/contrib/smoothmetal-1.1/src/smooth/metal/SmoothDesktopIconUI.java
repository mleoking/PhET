package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalDesktopIconUI;
import java.awt.*;

public class SmoothDesktopIconUI extends MetalDesktopIconUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothDesktopIconUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
