package smooth.metal;

import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalFileChooserUI;
import java.awt.*;

public class SmoothFileChooserUI extends MetalFileChooserUI {
    public SmoothFileChooserUI( JFileChooser fileChooser ) {
        super( fileChooser );
    }

    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothFileChooserUI( (JFileChooser)jcomponent );
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
