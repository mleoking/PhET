package smooth.basic;

import smooth.util.SmoothUtilities;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Titled border with anti-aliasing capibility.
 *
 * @author James Shiell
 * @version 1.0
 */
public class SmoothTitledBorder extends TitledBorder {

    public SmoothTitledBorder( Border border ) {
        super( border );
    }

    public SmoothTitledBorder( Border border, String title ) {
        super( border, title );
    }

    public SmoothTitledBorder( Border border, String title, int titleJustification, int titlePosition ) {
        super( border, title, titleJustification, titlePosition );
    }

    public SmoothTitledBorder( Border border, String title, int titleJustification, int titlePosition, Font titleFont ) {
        super( border, title, titleJustification, titlePosition, titleFont );
    }

    public SmoothTitledBorder( Border border, String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor ) {
        super( border, title, titleJustification, titlePosition, titleFont, titleColor );
    }

    public SmoothTitledBorder( String title ) {
        super( title );
    }

    public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
        SmoothUtilities.configureGraphics( g );
        super.paintBorder( c, g, x, y, width, height );
    }

}
