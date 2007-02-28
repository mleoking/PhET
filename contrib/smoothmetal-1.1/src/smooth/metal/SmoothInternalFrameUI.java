package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalInternalFrameUI;
import java.awt.*;

public class SmoothInternalFrameUI extends MetalInternalFrameUI {
    public SmoothInternalFrameUI( JInternalFrame jinternalframe ) {
        super( jinternalframe );
    }

    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothInternalFrameUI( (JInternalFrame)jcomponent );
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
