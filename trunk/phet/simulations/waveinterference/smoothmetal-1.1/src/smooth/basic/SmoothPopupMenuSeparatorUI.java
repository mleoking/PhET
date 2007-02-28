package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPopupMenuSeparatorUI;
import java.awt.*;

public class SmoothPopupMenuSeparatorUI extends BasicPopupMenuSeparatorUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothPopupMenuSeparatorUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
