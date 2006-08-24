package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsRadioButtonMenuItemUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothRadioButtonMenuItemUI extends WindowsRadioButtonMenuItemUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothRadioButtonMenuItemUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
