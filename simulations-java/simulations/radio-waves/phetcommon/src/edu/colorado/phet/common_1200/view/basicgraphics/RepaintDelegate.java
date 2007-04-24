
package edu.colorado.phet.common_1200.view.basicgraphics;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 7:59:13 AM
 *
 */
public interface RepaintDelegate {
    public void repaint( Component component, Rectangle rect );
}
