/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.tests;

import edu.colorado.phet.qm.phetcommon.RulerGraphic;
import edu.umd.cs.piccolo.PCanvas;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Jan 21, 2006
 * Time: 5:23:02 PM
 * Copyright (c) Jan 21, 2006 by Sam Reid
 */

public class TestRulerGraphic {
    public static void main( String[] args ) {
        PCanvas pCanvas = new PCanvas();
        JFrame frame = new JFrame();
        frame.setContentPane( pCanvas );
        frame.setSize( 800, 400 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        String[] digits = new String[11];
        for( int i = 0; i < digits.length; i++ ) {
            digits[i] = new String( i + "" );
        }
        pCanvas.getLayer().addChild( new RulerGraphic( digits, "nm", 650, 40 ) );
        frame.show();
    }
}
