package edu.colorado.phet.cck;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

/**
 * Author: Sam Reid
 * Aug 5, 2007, 11:35:11 PM
 */
public class TestJA {
    public static void main( String[] args ) {
        Locale.setDefault( new Locale( "ja" ) );
        String name = CCKResources.getString( "CCK3Module.GrabAWire" );
        System.out.println( "name = " + name );
        JFrame frame = new JFrame();

        JButton contentPane = new JButton( name );
        contentPane.setFont( new Font( "MS PGothic", Font.PLAIN, 10 ) );

        frame.setContentPane( contentPane );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width / 2 - frame.getWidth() / 2,
                           Toolkit.getDefaultToolkit().getScreenSize().height / 2 - frame.getHeight() / 2 );
        frame.show();
    }
}
