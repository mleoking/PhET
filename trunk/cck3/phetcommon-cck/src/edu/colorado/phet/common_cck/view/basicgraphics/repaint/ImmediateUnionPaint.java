/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics.repaint;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 9:15:25 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class ImmediateUnionPaint extends StoredRectRepainter {

    public ImmediateUnionPaint( JComponent component ) {
        super( component );
    }

    public void finishedUpdateCycle() {
        Rectangle union = super.getUnion();
        super.clearList();
        if( union != null ) {
            super.getComponent().paintImmediately( union );
        }
    }
}
