package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsSliderUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothSliderUI extends WindowsSliderUI {
    public SmoothSliderUI( JSlider b ) {
        super( b );
    }

    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothSliderUI( (JSlider)jcomponent );
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
