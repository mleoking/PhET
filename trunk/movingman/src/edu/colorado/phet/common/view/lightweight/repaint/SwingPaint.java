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
public class SwingPaint implements SynchronizedRepaintDelegate {
    ArrayList list = new ArrayList();
    private JComponent component;

    class Unit {
        Component component;
        Rectangle rect;

        public Unit( Component component, Rectangle rect ) {
            this.component = component;
            this.rect = rect;
        }
    }

    public SwingPaint( JComponent component ) {
        this.component = component;
    }

    public void repaint( Component component, Rectangle rect ) {
        list.add( new Unit( component, rect ) );
    }

    public void finishedUpdateCycle() {
        Rectangle union = null;
        for( int i = 0; i < list.size(); i++ ) {
            Unit unit = (Unit)list.get( i );

            if( union == null ) {
                union = unit.rect;
            }
            else {
                union = union.union( unit.rect );
            }
        }
        list.clear();

        final Rectangle union1 = union;
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
//                long time = System.currentTimeMillis();
                component.paintImmediately( union1 );
//                long now = System.currentTimeMillis();
//                long dt = now - time;
//                System.out.println( "dt = " + dt );
            }
        } );
    }
}
