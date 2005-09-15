/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.application.PhetStartupWindow;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Sep 14, 2005
 * Time: 7:24:26 AM
 * Copyright (c) Sep 14, 2005 by Sam Reid
 */

public class TestSplash {
    public static void main( String[] args ) {

        JLabel logoLabel = new JLabel();
        Image logoImage;
//        try {
//            logoImage = new ImageLoader().loadImage( "images/Phet-logo-anim-120-ii.gif" );
        logoImage = Toolkit.getDefaultToolkit().createImage( PhetStartupWindow.class.getClassLoader().getResource( "images/Phet-logo-anim-120-iv.gif" ) );
        ImageIcon logoIcon = new ImageIcon( logoImage );
        logoLabel.setIcon( logoIcon );

        JFrame frame = new JFrame();
        frame.setContentPane( logoLabel );
        frame.pack();
        frame.show();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
