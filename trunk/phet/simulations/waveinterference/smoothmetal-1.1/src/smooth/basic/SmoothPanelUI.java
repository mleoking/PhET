package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;

public class SmoothPanelUI extends BasicPanelUI {

    public static ComponentUI createUI( final JComponent jcomponent ) {
        return new SmoothPanelUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
