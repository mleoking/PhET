package edu.colorado.phet.piccolo.help;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.ShadowHTMLGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 1:42:44 PM
 * Copyright (c) Jun 1, 2006 by Sam Reid
 * <p/>
 * Wiggle Me
 * <p/>
 * Features:                                                             <br>
 * - default motion: moves in a straight line from a "start" to "end"<br>
 * location, variable motion speed, optional deceleration, stops moving
 * immediately when it reaches end location
 * - default motion is replaceable<br>
 * - text string: internationalized, supports HTML, variable color and font<br>
 * - optional drop shadow on text, with variable color<br>
 * - optional bubble behind text, with variable fill color, stroke color<br>
 * - optional arrow, with variable fill color, stroke color, length, head
 * size, attachment point, and direction
 * - arrow (if present) is visible while the wiggle me is in motion<br>
 * <p/>
 * Usage Guidelines:
 * - use a wiggle me to get the user started on a task, or to expose a
 * non-obvious feature<br>
 * - make the wiggle me disappear when the user takes some action<br>
 * - shouldn't reappear after it disappears<br>
 * - "start" location is typically off screen<br>
 * - "end" location is in the play area<br>
 * - text is brief and indicates an action the user should take (eg,
 * "Click on A!")<br>
 * - use a bubble behind the text when the background is too busy<br>
 * - use an arrow when it's not clear what the wiggle me refers to
 * - maximum of one wiggle me per panel<br>
 */

public class HintNode extends PhetPNode {
    private ShadowHTMLGraphic shadowHTMLGraphic;
    private PActivity activity;
    private boolean started = false;
    private BackgroundNode backgroundNode;
    private ArrowNode arrowNode;

    /**
     * Construct a HintNode with default settings for the specified text string.
     *
     * @param text
     */
    public HintNode( String text ) {
        shadowHTMLGraphic = new ShadowHTMLGraphic( text );
        shadowHTMLGraphic.setShadowColor( Color.lightGray );
        backgroundNode = new BackgroundNode( shadowHTMLGraphic );
        arrowNode = new ArrowNode( shadowHTMLGraphic );

        addChild( arrowNode );
        addChild( backgroundNode );
        addChild( shadowHTMLGraphic );
        addPropertyChangeListener( PROPERTY_PARENT, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                testSchedule();
            }
        } );
        update();
    }

    /**
     * Sets the activity that moves this node.
     *
     * @param pActivity
     */
    public void setActivity( PActivity pActivity ) {
        if( this.activity != null ) {
            this.activity.terminate();
            started = false;
        }
        this.activity = pActivity;
        testSchedule();
    }

    public void setBackgroundInsets( int insetX, int insetY ) {
        backgroundNode.setInsets( insetX, insetY );
    }

    /**
     * This class encompasses the background (if any) for the HintNode.
     */
    public static class BackgroundNode extends PNode {
        private Paint lightGray;
        private Stroke basicStroke;
        private Paint black;
        private PNode textNode;
        private int insetX = 15;
        private int insetY = 15;

        public BackgroundNode( PNode textNode ) {
            this.textNode = textNode;
        }

        public void setInsets( int insetX, int insetY ) {
            this.insetX = insetX;
            this.insetY = insetY;
            update();
        }

        public void update() {
            removeAllChildren();
            Rectangle2D bounds = textNode.getFullBounds();
            bounds = RectangleUtils.expand( bounds, insetX, insetY );
            RoundRectangle2D.Double b = new RoundRectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 15, 15 );
            PPath child = new PPath( b );
            child.setStroke( basicStroke );
            child.setStrokePaint( black );
            child.setPaint( lightGray );
            addChild( child );
        }

        public void setBackgroundPaint( Paint lightGray ) {
            this.lightGray = lightGray;
            update();
        }

        public void setBackgroundStroke( Stroke basicStroke ) {
            this.basicStroke = basicStroke;
            update();
        }

        public void setBackgroundStrokePaint( Paint black ) {
            this.black = black;
            update();
        }
    }

    /**
     * This class represents the arrow part of the HintNode.
     */
    public static class ArrowNode extends PNode {
        private double angle;
        private double arrowLength;
        PNode textNode;

        public ArrowNode( PNode textNode ) {
            this.textNode = textNode;
        }

        public void update() {
            Rectangle2D bounds = textNode.getFullBounds();
            AbstractVector2D vector = Vector2D.Double.parseAngleAndMagnitude( arrowLength, angle );
            Point2D.Double src = new Point2D.Double( bounds.getCenterX(), bounds.getCenterY() );
            Arrow arrow = new Arrow( src, vector.getDestination( src ), 10, 10, 4, 1.0, true );
            removeAllChildren();

            PPath child = new PPath( arrow.getShape() );
            child.setPaint( Color.black );
            addChild( child );
        }

        public void setArrowLength( double arrowLength ) {
            this.arrowLength = arrowLength;
            update();
        }

        public void setArrowAngle( double angle ) {
            this.angle = angle;
            update();
        }
    }

    /**
     * Sets whether the arrow is visible.
     *
     * @param visible
     */
    public void setArrowVisible( boolean visible ) {
        arrowNode.setVisible( visible );
    }

    /**
     * Draws the node; ensures the activity is active.
     *
     * @param paintContext
     */
    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        testSchedule();
    }

    private void testSchedule() {
        if( !started && getRoot() != null ) {
            getRoot().addActivity( activity );
            started = true;
        }
    }

    /**
     * Sets whether this HintNode is visible; use setVisible(false) to stop using the HintNode.
     *
     * @param isVisible
     */
    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
        if( !isVisible && activity != null ) {
            activity.terminate();
        }
    }

    /**
     * The node will start at its starting offset (from setOffset) and move to the specified location.
     *
     * @param x
     * @param y
     */
    public void animateToLocation( int x, int y ) {
        PActivity activity = animateToPositionScaleRotation( x, y, 1, 0, 5500 );
        if( getRoot() != null ) {
            started = true;
        }
        setActivity( activity );
    }

    public void setBackgroundPaint( Paint backgroundPaint ) {
        backgroundNode.setBackgroundPaint( backgroundPaint );
        update();
    }

    public void setBackgroundStroke( Stroke stroke ) {
        backgroundNode.setBackgroundStroke( stroke );
        update();
    }

    public void setBackgroundStrokePaint( Paint paint ) {
        backgroundNode.setBackgroundStrokePaint( paint );
        update();
    }

    /**
     * Sets the backgroundPaint, stroke and strokePaint all at once for convenience.
     *
     * @param backgroundPaint
     * @param stroke
     * @param strokePaint
     */
    public void setBackground( Paint backgroundPaint, Stroke stroke, Paint strokePaint ) {
        backgroundNode.setBackgroundPaint( backgroundPaint );
        backgroundNode.setBackgroundStroke( stroke );
        backgroundNode.setBackgroundStrokePaint( strokePaint );
    }

    public void setArrowLength( double arrowLength ) {
        this.arrowNode.setArrowLength( arrowLength );
        update();
    }

    public void setArrowAngle( double arrowAngle ) {
        this.arrowNode.setArrowAngle( arrowAngle );
        update();
    }

    /**
     * Sets the angle and arrowLength of the arrow.
     *
     * @param angle
     * @param arrowLength
     */
    public void setArrow( double angle, double arrowLength ) {
        arrowNode.setArrowLength( arrowLength );
        arrowNode.setArrowAngle( angle );
        update();
    }

    private void update() {
        shadowHTMLGraphic.setOffset( -shadowHTMLGraphic.getFullBounds().getWidth() / 2, -shadowHTMLGraphic.getFullBounds().getHeight() / 2 );
        backgroundNode.update();
        arrowNode.update();
    }

    public void setFont( Font font ) {
        shadowHTMLGraphic.setFont( font );
        update();
    }

    public void setText( String text ) {
        shadowHTMLGraphic.setHtml( text );
        update();
    }

    public void setColor( Color color ) {
        shadowHTMLGraphic.setColor( color );
        update();
    }

    public void setShadowColor( Color shadowColor ) {
        shadowHTMLGraphic.setShadowColor( shadowColor );
        update();
    }

    public void setShadowOffset( int dx, int dy ) {
        shadowHTMLGraphic.setShadowOffset( dx, dy );
        update();
    }
}
