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
 * StoredRectRepainter
 *
 * @author Sam Reid
 * @version $Revision$
 */
public abstract class StoredRectRepainter implements SynchronizedRepaintDelegate {
    private ArrayList list = new ArrayList();
    private JComponent component;

    public StoredRectRepainter( JComponent component ) {
        this.component = component;
    }

    public void repaint( Component component, Rectangle rect ) {
        list.add( rect );
    }

    protected ArrayList getRectList() {
        return list;
    }

    public JComponent getComponent() {
        return component;
    }

    public Rectangle getUnion() {
        Rectangle union = null;
        for( int i = 0; i < list.size(); i++ ) {
            Rectangle rect = (Rectangle)list.get( i );
            if( union == null ) {
                union = new Rectangle( rect );
            }
            else {
                union = union.union( rect );
            }
        }
        return union;
    }

    public void clearList() {
        list.clear();
    }
}
