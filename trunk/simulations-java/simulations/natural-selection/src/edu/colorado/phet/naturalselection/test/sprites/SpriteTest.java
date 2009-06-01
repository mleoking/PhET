package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;

import javax.swing.*;

import org.jfree.ui.RefineryUtilities;

public class SpriteTest extends JFrame {

    public SpriteTest() throws HeadlessException {
        super( "SpriteTest" );

        JPanel panel = new JPanel( new GridLayout( 1, 1 ) );
        panel.add( new SpriteCanvas() );
        setContentPane( panel );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        SpriteTest demo = new SpriteTest();
        demo.pack();
        demo.setSize( 400, 300 );
        RefineryUtilities.centerFrameOnScreen( demo );
        demo.setVisible( true );
    }
}
