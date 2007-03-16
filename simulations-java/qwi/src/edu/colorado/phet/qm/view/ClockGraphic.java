/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jan 21, 2006
 * Time: 7:03:02 PM
 * Copyright (c) Jan 21, 2006 by Sam Reid
 */

public class ClockGraphic extends PNode {
    private PPath bigHandNode;
    private PImage clockImage;
    private HandGraphic minuteHand;
    private HandGraphic hourHand;
//    double bigAngle;

    class HandGraphic extends PNode {
        double angle;
        double length;
        //        double width;
        private PPath shape;
        private double headHeight = 6;
        private double headWidth = 8;
        private double tailWidth = 3;

        public HandGraphic( double length, double width ) {
            this.length = length;
            this.tailWidth = width;

            shape = new PPath();
            addChild( shape );
            shape.setPaint( Color.black );
            shape.setStroke( new BasicStroke( 0.2f ) );
            shape.setStrokePaint( Color.darkGray );
        }

        public void setAngle( double angle ) {
            this.angle = angle;
            update();
        }

        private void update() {
            Arrow arrow = new Arrow( getTailLocation(), Vector2D.Double.parseAngleAndMagnitude( length, angle ).getDestination( getTailLocation() ),
                                     headHeight, headWidth, tailWidth );
            shape.setPathTo( arrow.getShape() );
        }
    }

    public ClockGraphic() {
        clockImage = PImageFactory.create( "images/clock3.png" );
        addChild( clockImage );

        minuteHand = new HandGraphic( 20, 2 );
        minuteHand.setAngle( 0.0 );
        addChild( minuteHand );
        hourHand = new HandGraphic( 13, 3 );
        addChild( hourHand );
        setTime( 0.0 );
    }

    protected Point2D getTailLocation() {
        return new Point2D.Double( clockImage.getWidth() / 2, clockImage.getHeight() / 2 );
    }

    public void setTime( double t ) {
        setMinuteHandAngle( t - Math.PI / 2 );
        setHourHandAngle( t / 12 - Math.PI / 2 );
    }

    public void setMinuteHandAngle( double angle ) {
        minuteHand.setAngle( angle );
    }

    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setSize( 400, 400 );
        PCanvas pCanvas = new PCanvas();
        frame.setContentPane( pCanvas );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );

        final ClockGraphic child = new ClockGraphic();

        Timer timer = new Timer( 30, new ActionListener() {
            double dt = 4.0;
            double t = 0;
            double ddt = 0.04;

            public void actionPerformed( ActionEvent e ) {
                t += dt;
                dt -= ddt;
                child.setTime( t / 10.0 );
            }
        } );
        timer.start();

        pCanvas.getLayer().addChild( child );
    }

    private void setHourHandAngle( double v ) {
        hourHand.setAngle( v );
    }
}
