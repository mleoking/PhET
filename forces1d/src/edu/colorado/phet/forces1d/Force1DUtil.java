/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.view.util.FrameSequence;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:27:37 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DUtil {
    private static Font borderFont = new Font( "Lucida Sans", Font.PLAIN, 12 );

    public static void main( String[] args ) throws IOException {
        FrameSequence animation = new FrameSequence( "animations/pusher/pusher-3", 19 );
        System.out.println( "animation = " + animation );
        for( int i = 0; i < animation.getNumFrames(); i++ ) {
            BufferedImage image = animation.getFrame( i );
        }
    }

    public static Border createTitledBorder( String s ) {
        return new TitledBorder( BorderFactory.createLineBorder( Color.black ), s, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, borderFont ) {
            public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
                GraphicsUtil.setAntiAliasingOn( (Graphics2D)g );
                super.paintBorder( c, g, x, y, width, height );
            }
        };
    }
}
