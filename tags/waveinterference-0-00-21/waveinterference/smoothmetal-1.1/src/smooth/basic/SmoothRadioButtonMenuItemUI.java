package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;
import java.awt.*;

public class SmoothRadioButtonMenuItemUI extends BasicRadioButtonMenuItemUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothRadioButtonMenuItemUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
