/** Sam Reid*/
package edu.colorado.phet.common.view.lightweight.repaint;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 9:15:25 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class ImmediatePaint implements SynchronizedRepaintDelegate {
    ArrayList list = new ArrayList();
    private JComponent component;

    public ImmediatePaint( JComponent component ) {
        if( component == null ) {
            throw new RuntimeException( "Null component." );
        }
        this.component = component;
    }

    public void repaint( Component component, Rectangle rect ) {
        list.add( rect );
    }

    public void finishedUpdateCycle() {
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
        list.clear();
        if( union != null ) {
            component.paintImmediately( union );
        }
    }
}
