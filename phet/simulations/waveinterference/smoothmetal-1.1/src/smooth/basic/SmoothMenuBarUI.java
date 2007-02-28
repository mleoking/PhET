package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;
import java.awt.*;

public class SmoothMenuBarUI extends BasicMenuBarUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothMenuBarUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
