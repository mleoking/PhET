package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsRadioButtonUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothRadioButtonUI extends WindowsRadioButtonUI {
    private static final ComponentUI ui = new SmoothRadioButtonUI();

    public static ComponentUI createUI( JComponent jcomponent ) {
        return ui;
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
