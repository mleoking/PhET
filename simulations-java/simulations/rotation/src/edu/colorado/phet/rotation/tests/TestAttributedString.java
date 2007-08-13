package edu.colorado.phet.rotation.tests;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.AttributedString;

public class TestAttributedString {
    private JFrame frame = new JFrame( getClass().getName().substring( getClass().getName().lastIndexOf( '.' ) + 1 ) );

    public TestAttributedString() {
        JPanel contentPane = new JPanel() {

            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                g.setColor( Color.blue );
                AttributedString attributedString = new AttributedString( "this text is my text" );
                attributedString.addAttribute( TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 2, 5 );
                attributedString.addAttribute( TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, 5, 10 );
//                attributedString.addAttribute( TextAttribute.FONT, new Font( "Lucida Sans", Font.BOLD, 20 ) );
                TextLayout textLayout = new TextLayout( attributedString.getIterator(), new FontRenderContext( new AffineTransform(), true, false ) );
                textLayout.draw( (Graphics2D)g, 50, 50 );

                g.fillRect( 100, 100, 5, 5 );
            }
        };

        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 800, 600 );
    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) {
        new TestAttributedString().start();
    }
}

