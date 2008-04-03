package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Created by: Sam
 * Apr 3, 2008 at 3:15:34 PM
 */
public class AnimatedClockJComponent extends JComponent {
    private IClock clock;  //the clock we observe

    private int componentWidth = 26;
    private double inset = 2;

    private BasicStroke clockShapeStroke = new BasicStroke( 2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND );
    private Ellipse2D.Double clockShape = new Ellipse2D.Double();

    private Hand fastHand = new Hand( DEFAULT_ANGLE, ( componentWidth - inset * 2 ) / 2 * FAST_HAND_SCALE, FAST_SPEED, Color.black, new BasicStroke( 2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ) );
    private Hand slowHand = new Hand( DEFAULT_ANGLE, ( componentWidth - inset * 2 ) / 2 * SLOW_HAND_SCALE, FAST_SPEED / 60, Color.black, new BasicStroke( 2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ) );

    //Defaults
    private static final double FAST_SPEED = 0.09;//needs to be fast enough that STEP makes a visible move
    private static final double DEFAULT_ANGLE = -Math.PI / 2;
    private static final double FAST_HAND_SCALE = 0.65;
    private static final double SLOW_HAND_SCALE = 0.55;

    public AnimatedClockJComponent( IClock clock ) {
        this.clock = clock;
        setPreferredSize( new Dimension( componentWidth, componentWidth ) );
        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                fastHand.update();
                slowHand.update();
                repaint( 0, 0, getWidth(), getHeight() );
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {
                fastHand.setAngle( DEFAULT_ANGLE );
                slowHand.setAngle( DEFAULT_ANGLE );
                repaint( 0, 0, getWidth(), getHeight() );
            }
        } );
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );

        Graphics2D g2 = (Graphics2D) g;
        RenderingHints renderingHints = g2.getRenderingHints();
        Stroke origStroke = g2.getStroke();
        Paint origPaint = g2.getPaint();

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        g2.setStroke( clockShapeStroke );

        clockShape.setFrame( inset, inset, componentWidth - inset * 2, componentWidth - inset * 2 );

        g2.setPaint( Color.white );
        g2.fill( clockShape );

        g2.setPaint( Color.black );
        g2.draw( clockShape );

        fastHand.draw( g2 );
        slowHand.draw( g2 );

        g2.setRenderingHints( renderingHints );
        g2.setStroke( origStroke );
        g2.setPaint( origPaint );
    }

    private class Hand {
        private double angle;
        private double radius;
        private double speed;
        private Color color;
        private Stroke stroke;
        private Line2D.Double s=new Line2D.Double( );

        Hand( double angle, double radius, double speed, Color color, Stroke stroke ) {
            this.angle = angle;
            this.radius = radius;
            this.speed = speed;
            this.color = color;
            this.stroke = stroke;
        }

        public void update() {
            angle += speed;
        }

        public void draw( Graphics2D g2 ) {
            g2.setColor( color );
            g2.setStroke( stroke );
            double x = componentWidth / 2.0 + inset / 4;//todo: i don't understand why this is off by a pixel or so
            double y = componentWidth / 2.0 + inset / 4;
            s.setLine( x, y, x + radius * Math.cos( angle ), y + radius * Math.sin( angle ) );
            g2.draw( s );
        }

        public void setAngle( double angle ) {
            this.angle = angle;
        }
    }

}
