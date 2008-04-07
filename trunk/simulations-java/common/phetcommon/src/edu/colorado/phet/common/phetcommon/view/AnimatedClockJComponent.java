/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

/**
 * Animated clock icon, hands move when the clock ticks.
 * <p>
 * The implementation includes two hands for the clock.
 * But we are hiding one of the hands so that users don't try to use the animation
 * to determine when an hour (for example) has gone by.
 * 
 * @author Sam Reid, Chris Malley
 */
public class AnimatedClockJComponent extends JComponent {

    // Face properties
    private static final double FACE_RADIUS = 11;
    private static final float FACE_STROKE_WIDTH = 2.5f;
    private static final Color FACE_FILL_COLOR = Color.WHITE;
    private static final Color FACE_STROKE_COLOR = Color.BLACK;
    private static final BasicStroke FACE_STROKE = new BasicStroke( FACE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    
    // Hand properties
    private static final BasicStroke HAND_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER );
    private static final Color HAND_STROKE_COLOR = Color.BLACK;
    private static final double FAST_HAND_LENGTH = 0.70 * FACE_RADIUS;
    private static final double SLOW_HAND_LENGTH = 0.55 * FACE_RADIUS;
    private static final double FAST_HAND_START_ANGLE = -Math.PI / 2; // starting angle of the fast hand (radians)
    private static final double SLOW_HAND_START_ANGLE = FAST_HAND_START_ANGLE; // starting angle of the slow hand (radians)
    private static final double FAST_HAND_DELTA_ANGLE = 0.09; // change in angle per clock tick (radians), needs to be fast enough that tick results in a visible move
    private static final double SLOW_HAND_DELTA_ANGLE = FAST_HAND_DELTA_ANGLE / 12; // change in angle per clock tick (radians)
    private static final boolean SLOW_HAND_VISIBLE = false; // hide the slow hand, so users won't ascribe meaning to the animation
    
    // Parts of a clock instance
    private final Face face;
    private final Hand fastHand, slowHand;
    
    public AnimatedClockJComponent() {
        
        // size the component to fit the clock face, account for stroke width
        int componentSize = (int)( ( 2 * FACE_RADIUS ) + ( FACE_STROKE_WIDTH ) ) + 1; // +1 to compensate for cast to int
        setPreferredSize( new Dimension( componentSize, componentSize ) );

        face = new Face( FACE_RADIUS );
        fastHand = new Hand( FAST_HAND_LENGTH, FAST_HAND_START_ANGLE, FAST_HAND_DELTA_ANGLE );
        slowHand = new Hand( SLOW_HAND_LENGTH, SLOW_HAND_START_ANGLE, SLOW_HAND_DELTA_ANGLE );
    }
    
    public void advance() {
        fastHand.update();
        slowHand.update();
        repaint( 0, 0, getWidth(), getHeight() );
    }
    
    public void reset() {
        fastHand.setAngle( FAST_HAND_START_ANGLE );
        slowHand.setAngle( SLOW_HAND_START_ANGLE );
        repaint( 0, 0, getWidth(), getHeight() ); 
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );

        Graphics2D g2 = (Graphics2D) g;
        
        // save graphics state
        RenderingHints origRenderingHints = g2.getRenderingHints();
        Stroke origStroke = g2.getStroke();
        Paint origPaint = g2.getPaint();

        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        // translate origin to center of component
        final double tx = getWidth() / 2;
        final double ty = tx;
        g2.translate( tx, ty );
        
        // draw the parts of the clock
        face.draw( g2 );
        fastHand.draw( g2 );
        if ( SLOW_HAND_VISIBLE ) {
            slowHand.draw( g2 );
        }

        // restore graphics state
        g2.translate( -tx, -ty );
        g2.setRenderingHints( origRenderingHints );
        g2.setStroke( origStroke );
        g2.setPaint( origPaint );
    }

    /**
     * Clock face, origin is at the center of the face, where the hands pivot.
     */
    private static class Face {
        private final Shape shape;
        
        public Face( double radius ) {
            this.shape = new Ellipse2D.Double( -radius, -radius, 2 * radius, 2 * radius ); // origin at center of clock face
        }
        
        /*
         * Draws the clock face, with origin at the center of the face.
         * Assumes the caller is saving and restoring graphics state
         */
        public void draw( Graphics2D g2 ) {
            g2.setStroke( FACE_STROKE );
            g2.setPaint( FACE_FILL_COLOR );
            g2.fill( shape );
            g2.setPaint( FACE_STROKE_COLOR );
            g2.draw( shape );
        }
        
    }
    
    /**
     * Clock hand, origin is at the point where the hand pivots.
     */
    private static class Hand {

        private final double length;
        private double angle;
        private final double deltaAngle;
        private final Line2D.Double line;

        public Hand( double length, double angle, double deltaAngle ) {
            this.length = length;
            this.angle = angle;
            this.deltaAngle = deltaAngle;
            this.line = new Line2D.Double();
        }

        public void setAngle( double angle ) {
            this.angle = angle;
        }
        
        public void update() {
            angle += deltaAngle;
        }

        /*
         * Draws the clock hand, starting at the center of the clock face.
         * Assumes the caller is saving and restoring graphics state
         */
        public void draw( Graphics2D g2 ) {
            g2.setColor( HAND_STROKE_COLOR );
            g2.setStroke( HAND_STROKE );
            line.setLine( 0, 0, length * Math.cos( angle ), length * Math.sin( angle ) ); // origin at center of clock face
            g2.draw( line );
        }
    }

}
