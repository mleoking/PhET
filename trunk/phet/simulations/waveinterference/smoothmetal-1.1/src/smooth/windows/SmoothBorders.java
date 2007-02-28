package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsBorders;
import smooth.util.SmoothUtilities;

import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalBorders;
import java.awt.*;

/**
 * Border factory. Modelled after the MetalBorders, from
 * which it inherits. The <code>paintBorder()</code> method
 * is overridden to turn on anti-aliasing. This is very
 * similar to what's done in UI delegates.
 */
public class SmoothBorders extends WindowsBorders {
    private static Border buttonBorder;

    public static class ButtonBorder extends MetalBorders.ButtonBorder {
        public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {
            SmoothUtilities.configureGraphics( g );
            super.paintBorder( c, g, x, y, w, h );
        }
    }

    public static Border getButtonBorder() {
        if( buttonBorder == null ) {
            buttonBorder = new BorderUIResource.CompoundBorderUIResource( new SmoothBorders.ButtonBorder(), new BasicBorders.MarginBorder() );
        }
        return buttonBorder;
    }
}