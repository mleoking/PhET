package edu.colorado.phet.rotation.tests;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

/**
 * From:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4156394
 */
public class TestAttStr2 extends JPanel {
    AttributedString as;

    public static void main( String[] args ) {
        JFrame jf = new JFrame( TestAttStr2.class.getName() );
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.getContentPane().setLayout( new GridLayout( 1, 0 ) );
        jf.getContentPane().add( new TestAttStr2() );
        jf.setSize( 300, 300 );
        jf.setVisible( true );
    }

    TestAttStr2() {
        String s = "attributed string of a length long enough to wrap a few times.";
        as = new AttributedString( s );
        as.addAttribute( TextAttribute.SIZE,
                         new Float( 24 ) );
        as.addAttribute( TextAttribute.SUPERSCRIPT,
                         TextAttribute.SUPERSCRIPT_SUB, 0, 3 );
        as.addAttribute( TextAttribute.SUPERSCRIPT,
                         TextAttribute.SUPERSCRIPT_SUPER, 5, 8 );
        as.addAttribute( TextAttribute.SUPERSCRIPT,
                         TextAttribute.SUPERSCRIPT_SUB,
                         s.length() - 5, s.length() - 2 );
    }

    public void paintComponent( Graphics g ) {

        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor( Color.blue );
        AttributedCharacterIterator aci = as.getIterator();
        g2d.drawString( aci, 10, 100 );

        FontRenderContext frc = g2d.getFontRenderContext();
        LineBreakMeasurer lbm = new LineBreakMeasurer( aci, frc );
        final float width = 200;
        float y = 150;
        for( TextLayout tl = lbm.nextLayout( width ); tl != null; tl = lbm.nextLayout( width ) ) {
            y += tl.getAscent();
            tl.draw( g2d, 10, y );
            g2d.drawLine( 0, (int)y, 200, (int)y );
            y += tl.getDescent() + tl.getLeading();
        }
    }
}

