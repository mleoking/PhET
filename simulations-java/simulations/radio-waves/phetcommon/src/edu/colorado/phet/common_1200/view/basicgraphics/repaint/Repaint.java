
package edu.colorado.phet.common_1200.view.basicgraphics.repaint;

import edu.colorado.phet.common_1200.view.basicgraphics.RepaintDelegate;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 13, 2004
 * Time: 7:35:16 PM
 *
 */
public class Repaint implements RepaintDelegate {
    public void repaint( Component component, Rectangle rect ) {
        component.repaint();
    }
}
