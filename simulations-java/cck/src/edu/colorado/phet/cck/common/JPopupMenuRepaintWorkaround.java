package edu.colorado.phet.cck.common;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 29, 2004
 * Time: 3:57:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class JPopupMenuRepaintWorkaround extends JPopupMenu {
    private Component target;

    public JPopupMenuRepaintWorkaround( Component target ) {
        this.target = target;
        addPopupMenuListener( new PopupMenuListener() {
            public void popupMenuCanceled( PopupMenuEvent e ) {
                waitValidRepaint();
            }

            public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {
                waitValidRepaint();
            }

            public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
                waitValidRepaint();
            }
        } );
    }

    public void waitValidRepaint() {
        Thread thread = new Thread( new Runnable() {
            public void run() {
                try {
                    Thread.sleep( 250 );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
                Window window = SwingUtilities.getWindowAncestor( target );
                if( window instanceof JFrame ) {
                    JFrame jeff = (JFrame)window;
                    Container jp = jeff.getContentPane();
                    jp.invalidate();
                    jp.validate();
                    jp.repaint();
                }
            }
        } );
        thread.setPriority( Thread.MAX_PRIORITY );
        thread.start();
    }

    public void validateRepaint() {
        super.invalidate();
        super.validate();
        super.repaint();
    }

}
