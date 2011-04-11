// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;

import javax.swing.*;

/*
 * Strategies for setting up the size and location of a frame
 * @author Sam Reid
 */
public interface FrameSetup {
    public void initialize( JFrame frame );

    public static class CenteredWithSize implements FrameSetup {
        private int width;
        private int height;

        public CenteredWithSize( int width, int height ) {
            this.width = width;
            this.height = height;
        }

        public void initialize( JFrame frame ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            int x = ( d.width - width ) / 2;
            int y = ( d.height - height ) / 2;
            frame.setLocation( x, y );
            frame.setSize( width, height );
        }
    }
}