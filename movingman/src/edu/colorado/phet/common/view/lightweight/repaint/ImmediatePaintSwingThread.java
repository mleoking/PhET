/** Sam Reid*/
package edu.colorado.phet.common.view.lightweight.repaint;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 9:15:25 AM
 * Copyright (c) Sep 10, 2004 by Sam Reid
 */
public class ImmediatePaintSwingThread implements SynchronizedRepaintDelegate {
    ArrayList list = new ArrayList();
    private JComponent component;

    public ImmediatePaintSwingThread( JComponent component ) {
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
            final Rectangle union1 = union;
            try {
                if( !SwingUtilities.isEventDispatchThread() ) {

                    SwingUtilities.invokeAndWait( new Runnable() {
                        public void run() {
                            component.paintImmediately( union1 );
                        }
                    } );

                }
                else {
                    component.paintImmediately( union1 );
                }
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            catch( InvocationTargetException e ) {
                e.printStackTrace();
            }
//            component.paintImmediately( union );
        }
    }
}
