/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 17, 2004
 * Time: 3:15:06 PM
 * Copyright (c) Jun 17, 2004 by Sam Reid
 */
public class RectangleUtils {
    public static Rectangle expand( Rectangle r, int dx, int dy ) {
        return new Rectangle( r.x - dx, r.y - dy, r.width + dx * 2, r.height + dy * 2 );
    }
}
