package edu.colorado.phet.rotation.util;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 9:32:26 AM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class GraphicsUtil {
    public static boolean antialias( Graphics g, boolean antialias ) {
        Graphics2D g2 = (Graphics2D)g;
        boolean aa = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING ) == RenderingHints.VALUE_ANTIALIAS_ON;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );
        return aa;
    }
}
