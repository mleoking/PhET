package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicEditorPaneUI;
import java.awt.*;

public class SmoothEditorPaneUI extends BasicEditorPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothEditorPaneUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
