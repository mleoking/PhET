package smooth.util;

import java.awt.*;

/**
 * A repository of utility code.
 *
 * @author James Shiell
 * @version 1.1
 * @since 0.3
 */
public class SmoothUtilities {

    protected static boolean antialias = true;
    protected static boolean fractionalMetrics = false;

    public static void configureGraphics( Graphics g ) {
        if( antialias ) {
            ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
            ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        }
        else {
            ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
            ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        }

        if( fractionalMetrics ) {
            ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );
        }
        else {
            ( (Graphics2D)g ).setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
        }
    }

    public static boolean isAntialias() {
        return antialias;
    }

    public static void setAntialias( boolean antialias ) {
        SmoothUtilities.antialias = antialias;
    }

    public static boolean isFractionalMetrics() {
        return fractionalMetrics;
    }

    public static void setFractionalMetrics( boolean fractionalMetrics ) {
        SmoothUtilities.fractionalMetrics = fractionalMetrics;
    }
}