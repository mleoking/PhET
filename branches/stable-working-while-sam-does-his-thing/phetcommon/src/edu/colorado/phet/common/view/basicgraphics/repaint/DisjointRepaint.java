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
 * DisjointRepaint
 *
 * @author Sam Reid
 * @version $Revision$
 */
public class DisjointRepaint extends StoredRectRepainter {

    public DisjointRepaint( JComponent component ) {
        super( component );
    }

    public void finishedUpdateCycle() {
        ArrayList list = super.getRectList();
        for( int i = 0; i < list.size(); i++ ) {
            Rectangle rectangle = (Rectangle)list.get( i );
            super.getComponent().repaint( rectangle );
        }
    }
}
