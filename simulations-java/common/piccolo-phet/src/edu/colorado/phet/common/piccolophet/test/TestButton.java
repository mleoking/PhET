package edu.colorado.phet.common.piccolophet.test;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * Created by: Sam
 * Sep 12, 2008 at 5:20:11 PM
 */
public class TestButton {
    public static void main( String[] args ) {
        JFrame frame=new JFrame( );
        frame.setSize( 800,600 );
        JButton button = new JButton("test");
        button.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ));
        VerticalLayoutPanel contentPane = new VerticalLayoutPanel();
        frame.setContentPane( contentPane );

        contentPane.add( button );

        frame.show(  );
    }
}
