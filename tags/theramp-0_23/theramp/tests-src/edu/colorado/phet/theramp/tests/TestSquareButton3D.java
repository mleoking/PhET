/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.tests;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.theramp.common.SquareButton3D;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 10:55:40 AM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */

public class TestSquareButton3D {
    public static void main( String[] args ) {
        ApparatusPanel ap = new ApparatusPanel();
        JFrame jfr = new JFrame();
        jfr.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        jfr.setContentPane( ap );
        jfr.setSize( 600, 600 );

//        Font font = new Font( "Lucida Sans", Font.BOLD, 24 );
        Font font = new Font( "Lucida Sans", Font.PLAIN, 12 );
//        SquareButton3D squareButton3D = new SquareButton3D( ap, font, "Go!" );
        SquareButton3D squareButton3D = new SquareButton3D( ap, font, "<html><center>Go!</center><br>(Start the simulation)</html>" );
//        SquareButton3D squareButton3D = new SquareButton3D( ap, font, "<html>Button<sup><small>3D</small></sup><sub>by PhET&#169;</sub></html>" );
//        SquareButton3D squareButton3D = new SquareButton3D( ap, font, "<html><var>d</var> = <var>b</var><sup><small>2</small></sup> - 4<var>ac</var></html>" );
        squareButton3D.setLocation( 100, 100 );
        ap.addGraphic( squareButton3D );

        jfr.setVisible( true );
    }
}
