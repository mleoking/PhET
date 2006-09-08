/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 7:59:13 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public interface RepaintDelegate {
    public void repaint( Component component, Rectangle rect );
}
