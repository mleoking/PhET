
package edu.colorado.phet.common_1200.view.basicgraphics.repaint;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Sam Reid
 * Date: Sep 10, 2004
 * Time: 9:15:25 AM
 *
 */
public class InvokeAndWaitImmediatePaint extends StoredRectRepainter {
    public InvokeAndWaitImmediatePaint( JComponent component ) {
        super( component );
    }

    public void finishedUpdateCycle() {
        Rectangle union = super.getUnion();
        super.clearList();
        if( union != null ) {
            paintInSwingThread( union );
        }
    }

    private void paintInSwingThread( final Rectangle union ) {
        final JComponent component = super.getComponent();
        if( !SwingUtilities.isEventDispatchThread() ) {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        component.paintImmediately( union );
                    }
                } );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            catch( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
        else {
            component.paintImmediately( union );
        }
    }
}
