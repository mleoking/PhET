/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.util;

import java.awt.*;

import javax.swing.*;

/**
 * FrameSetup
 *
 * @author ?
 * @version $Revision$
 */
public interface FrameSetup {
    public void initialize( JFrame frame );

    public static class CenteredWithInsets implements FrameSetup {
        private int insetX;
        private int insetY;

        public CenteredWithInsets( int insetX, int insetY ) {
            this.insetX = insetX;
            this.insetY = insetY;
        }

        public static void setup( Window w, int insetX, int insetY ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int width = d.width - insetX * 2;
            int height = d.height - insetY * 2;
            w.setSize( width, height );
            w.setLocation( insetX, insetY );
        }

        public void initialize( JFrame frame ) {
            setup( frame, insetX, insetY );
        }
    }

    public static class CenteredWithSize implements FrameSetup {
        private int width;
        private int height;

        public CenteredWithSize( int width, int height ) {
            this.width = width;
            this.height = height;
        }

        // todo: add test to see that the requested dimensions aren't
        // bigger than the screen
        public void initialize( JFrame frame ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int x = ( d.width - width ) / 2;
            int y = ( d.height - height ) / 2;
            frame.setLocation( x, y );
            frame.setSize( width, height );
        }
    }

    public static class Full {
        public Full() {
        }

        public void initialize( JFrame frame ) {
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation( 0, 0 );
            frame.setSize( d );
        }
    }

    public static class MaxExtent implements FrameSetup {
        FrameSetup pre = null;

        public MaxExtent() {
        }

        public MaxExtent( FrameSetup pre ) {
            this.pre = pre;
        }

        public void initialize( JFrame frame ) {
            if ( pre != null ) {
                pre.initialize( frame );
            }
            int state = frame.getExtendedState();
            // Set the maximized bits
            state |= Frame.MAXIMIZED_BOTH;
            // Maximize the frame
            frame.setExtendedState( state );
        }
    }
}
