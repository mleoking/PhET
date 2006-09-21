package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicCheckBoxMenuItemUI;
import java.awt.*;

public class SmoothCheckBoxMenuItemUI extends BasicCheckBoxMenuItemUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothCheckBoxMenuItemUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
