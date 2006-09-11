/** Sam Reid*/
package edu.colorado.phet.common_cck.view.basicgraphics.repaint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 13, 2004
 * Time: 7:38:34 PM
 * Copyright (c) Sep 13, 2004 by Sam Reid
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
