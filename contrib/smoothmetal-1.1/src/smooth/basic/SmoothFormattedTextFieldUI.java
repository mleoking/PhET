package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicFormattedTextFieldUI;
import java.awt.*;

public class SmoothFormattedTextFieldUI extends BasicFormattedTextFieldUI {

    public static ComponentUI createUI( final JComponent jcomponent ) {
        return new SmoothFormattedTextFieldUI();
    }

    protected void paintSafely( Graphics g ) {
        SmoothUtilities.configureGraphics( g );
        super.paintSafely( g );
    }
}
