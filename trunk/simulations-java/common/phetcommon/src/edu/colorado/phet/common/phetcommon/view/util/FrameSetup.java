// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;

import javax.swing.*;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import static java.awt.Toolkit.getDefaultToolkit;

/*
 * Strategies for setting up the size and location of a frame
 * @author Sam Reid
 */
public interface FrameSetup {
    public void initialize( JFrame frame );

    public static class CenteredWithSize implements FrameSetup {
        private int frameWidth;
        private int frameHeight;

        /**
         * Creates a new CenteredWithSize strategy that will center the window with the specified size (or the available window size, whichever is smaller).
         * This prevents the window from being initialized to be larger (in width or height) than the available screen size, accounting for insets of toolbars, etc.
         *
         * @param frameWidth  the desired frame width
         * @param frameHeight the desired frame height
         */
        public CenteredWithSize( int frameWidth, int frameHeight ) {
            this.frameWidth = Math.min( getAvailableWidth(), frameWidth );
            this.frameHeight = Math.min( getAvailableHeight(), frameHeight );
        }

        //Determines the width of the screen that is available for windowing, that is, the size without the insets.
        private int getAvailableWidth() {
            return getDefaultToolkit().getScreenSize().width - getInsets().left - getInsets().right;
        }

        //Determines the width of the screen that is available for windowing, that is, the size without the insets.
        private int getAvailableHeight() {
            return getDefaultToolkit().getScreenSize().height - getInsets().top - getInsets().bottom;
        }

        private Insets getInsets() {
            return getDefaultToolkit().getScreenInsets( getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration() );
        }

        public void initialize( JFrame frame ) {
            Dimension screenSize = getDefaultToolkit().getScreenSize();
            //If too big, then just fit inside insets, otherwise center
            frame.setLocation( frameWidth >= getAvailableWidth() ? getInsets().left : screenSize.width / 2 - frameWidth / 2,
                               frameHeight >= getAvailableHeight() ? getInsets().top : screenSize.height / 2 - frameHeight / 2 );
            frame.setSize( frameWidth, frameHeight );
        }
    }
}