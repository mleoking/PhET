/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.basicgraphics.repaint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * ImmediateDisjointPaint
 *
 * @author Sam Reid
 * @version $Revision$
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
