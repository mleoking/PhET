// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.tests;

import java.awt.*;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Jun 4, 2009
 * Time: 10:42:04 AM
 */
public class TestTextRenderOrder {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
        frame.setContentPane( verticalLayoutPanel );

        testJLabels( verticalLayoutPanel, "", "" );
        verticalLayoutPanel.add( new JLabel( "Separator" ) );
        testJLabels( verticalLayoutPanel, "<html>", "</html>" );
        JPanel jPanel = new JPanel() {
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                Graphics2D g2 = (Graphics2D) g;
                g2.drawString( "\u062D", 50, 50 );
                g2.drawString( "\u0635", 50, 100 );
                g2.drawString( "\u062D\u0635", 50, 150 );
                g2.drawString( "\u062Dhello\u0635", 50, 200 );
                g2.drawString( "\u062D hello \u0635", 50, 250 );
            }
        };
        jPanel.setPreferredSize( new Dimension( 400, 400 ) );
        verticalLayoutPanel.add( jPanel );

        frame.pack();
        frame.setVisible( true );
    }

    private static void testJLabels( VerticalLayoutPanel verticalLayoutPanel, String prefix, String suffix ) {
        verticalLayoutPanel.add( new JLabel( prefix + "\u062D" + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + "\u0635" + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + "\u062D \u0635" + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + "\u062Dhello\u0635" + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + "\u062D hello \u0635" + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + MessageFormat.format( "testing {0} and {1}", "value.0", "value.1" ) + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + MessageFormat.format( "testing {0} and {1}", "\u062D", "\u0635" ) + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + MessageFormat.format( "{0}{1}", "\u062D", "\u0635" ) + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + MessageFormat.format( "{0} {1}", "\u062D", "\u0635" ) + suffix ) );
        verticalLayoutPanel.add( new JLabel( prefix + MessageFormat.format( "{0} hello {1}", "\u062D", "\u0635" ) + suffix ) );
    }
}
