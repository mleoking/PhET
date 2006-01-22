/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.piccolo.PImageFactory;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 21, 2006
 * Time: 7:03:02 PM
 * Copyright (c) Jan 21, 2006 by Sam Reid
 */

public class ClockGraphic extends PNode {
    private PPath bigHandNode;
    private PImage clockImage;

    public ClockGraphic() {
        clockImage = PImageFactory.create( "images/clock.png" );
        addChild( clockImage );

        Shape bigHand = createBigHand();
        bigHandNode = new PPath( bigHand );
    }

    private Shape createBigHand() {
        return null;
    }

    public void setBigHandAngle( double angle ) {

    }

    public void setLittleHandAngle( double angle ) {

    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setSize( 400, 400 );
        PCanvas pCanvas = new PCanvas();
        frame.setContentPane( pCanvas );
        pCanvas.getLayer().addChild( new ClockGraphic() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
