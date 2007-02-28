package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsInternalFrameUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothInternalFrameUI extends WindowsInternalFrameUI {
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
