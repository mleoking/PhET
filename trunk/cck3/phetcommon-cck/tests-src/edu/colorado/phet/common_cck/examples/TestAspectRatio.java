/**
 * Class: TestAspectRatio
 * Package: edu.colorado.phet.common.examples
 * Author: Another Guy
 * Date: May 12, 2004
 */
package edu.colorado.phet.common_cck.examples;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.components.AspectRatioPanel;

import javax.swing.*;
import java.awt.*;

public class TestAspectRatio {
    public static void main( String[] args ) {
//        final JPanel container = new JPanel();
        final ApparatusPanel apparatusPanel = new ApparatusPanel();
        final int insetY = 30;
        final int insetX = 30;
        apparatusPanel.setBackground( Color.green );

//        container.setBackground( Color.yellow );
        double aspectRatio = 0.5;

//        AspectRatioLayout cl = new AspectRatioLayout( apparatusPanel, insetX, insetY, aspectRatio );
//        container.setLayout( cl );
//        container.add( apparatusPanel );
        AspectRatioPanel arp = new AspectRatioPanel( apparatusPanel, insetX, insetY, aspectRatio );
        arp.setBackground( Color.yellow );
//        arp.setBorder( new );
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( arp );
        jf.pack();
        jf.setSize( 600, 600 );
        jf.setVisible( true );
    }
}
