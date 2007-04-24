
package edu.colorado.phet.common_1200.view.basicgraphics.repaint;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 9:15:25 AM
 *
 */
public class UnionRepaint extends StoredRectRepainter {

    public UnionRepaint( JComponent component ) {
        super( component );
    }

    public void finishedUpdateCycle() {
        Rectangle union = super.getUnion();
        super.clearList();
        super.getComponent().repaint( union );
    }
}
