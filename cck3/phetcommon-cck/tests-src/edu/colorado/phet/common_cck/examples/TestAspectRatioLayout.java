/** Sam Reid*/
package edu.colorado.phet.common_cck.examples;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.components.AspectRatioLayout;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 26, 2004
 * Time: 12:12:25 PM
 * Copyright (c) May 26, 2004 by Sam Reid
 */
public class TestAspectRatioLayout {
    public static void main( String[] args ) {
        final JPanel container = new JPanel();
        final ApparatusPanel apparatusPanel = new ApparatusPanel();
        final int insetY = 30;
        final int insetX = 30;
        apparatusPanel.setBackground( Color.green );

        container.setBackground( Color.yellow );
        double aspectRatio = 1.5;//height/width

        AspectRatioLayout cl = new AspectRatioLayout( apparatusPanel, insetX, insetY, aspectRatio );
        container.setLayout( cl );
        container.add( apparatusPanel );

        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jf.setContentPane( container );
        jf.pack();
        jf.setVisible( true );
    }
}
