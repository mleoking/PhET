/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.circuit.battery;

import edu.colorado.phet.common.view.util.graphics.ImageLoader;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 12:48:18 AM
 * Copyright (c) Mar 15, 2004 by Sam Reid
 */
public class MySpinnerUI2 extends BasicSpinnerUI {
    protected Component createPreviousButton() {
//        Component s = super.createPreviousButton();
//        JButton jb = (JButton) s;
//        jb.setIcon(new ImageIcon("images/pusher/pusher-3_0001.gif"));
//        jb.setText("TEXT");
//        return jb;
        try {
            BufferedImage image = new ImageLoader().loadImage( "images/light.gif" );
            return new JButton( new ImageIcon( image ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    protected Component createNextButton() {
//        Component s = super.createNextButton();
//        JButton jb = (JButton) s;
//        jb.setIcon(new ImageIcon("images/pusher/pusher-3_0001.gif"));
//        jb.setText("TEXT");
//        return new JButton(new ImageIcon("images/light.gif"));
//        return jb;
        try {
            BufferedImage image = new ImageLoader().loadImage( "images/light.gif" );
            return new JButton( new ImageIcon( image ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
