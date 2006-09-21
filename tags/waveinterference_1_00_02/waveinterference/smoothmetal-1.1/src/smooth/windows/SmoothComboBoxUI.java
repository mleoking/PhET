package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsComboBoxUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothComboBoxUI extends WindowsComboBoxUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothComboBoxUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
