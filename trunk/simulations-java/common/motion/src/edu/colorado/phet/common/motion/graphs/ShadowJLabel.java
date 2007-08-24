package edu.colorado.phet.common.motion.graphs;

import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * Author: Sam Reid
 * Jul 19, 2007, 4:46:07 PM
 */
//todo: cache for performance reasons?
public class ShadowJLabel extends PCanvas {
    public ShadowJLabel( String text, Color foreground, Font font ) {
        ShadowPText shadowPText = new ShadowPText( text );
        shadowPText.setTextPaint( foreground );
        shadowPText.setFont( font );
        getLayer().addChild( shadowPText );
        setPreferredSize( new Dimension( (int)shadowPText.getFullBounds().getWidth() + 1, (int)shadowPText.getFullBounds().getHeight() + 1 ) );
        setOpaque( false );
        setBackground( null );
        setBorder( null );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        ShadowJLabel contentPane = new ShadowJLabel( "" + '\u03B8', Color.blue, new Font( "Lucida Sans", Font.BOLD, 24 ) );
        frame.setContentPane( contentPane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();
    }
}
