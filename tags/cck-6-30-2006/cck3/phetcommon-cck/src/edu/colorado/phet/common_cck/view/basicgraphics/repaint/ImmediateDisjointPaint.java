/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics.repaint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 9:15:25 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class ImmediateDisjointPaint extends StoredRectRepainter {

    public ImmediateDisjointPaint( JComponent component ) {
        super( component );
    }

    public void finishedUpdateCycle() {
        ArrayList list = super.getRectList();
        for( int i = 0; i < list.size(); i++ ) {
            Rectangle rect = (Rectangle)list.get( i );
            super.getComponent().paintImmediately( rect );
        }
        super.clearList();
    }
}
