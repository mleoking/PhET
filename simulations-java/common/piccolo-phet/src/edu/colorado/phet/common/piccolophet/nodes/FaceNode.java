// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * A face that can smile or frown, for universally indicating success or failure.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaceNode extends PComposite {

    // default properties
    private static final Color HEAD_COLOR = Color.YELLOW;
    private static final Color EYE_COLOR = Color.BLACK;
    private static final Color MOUTH_COLOR = Color.BLACK;

    private final PPath smileNode;
    private final PPath frownNode;

    public FaceNode( double headDiameter ) {
        this( headDiameter, HEAD_COLOR );
    }

    public FaceNode( double headDiameter, Paint headPaint ) {
        this( headDiameter, headPaint, EYE_COLOR, MOUTH_COLOR );
    }

    public FaceNode( final double headDiameter, Paint headPaint, Paint eyePaint, Paint mouthPaint ) {
        super();

        final double eyeDiameter = 0.15f * headDiameter;
        final double mouthRadius = headDiameter / 4d;
        final Stroke mouthStroke = new BasicStroke( Math.max( 1f, (float) ( headDiameter / 25 ) ), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

        PNode headNode = new CircleNode( headDiameter, headPaint );
        PNode leftEyeNode = new CircleNode( eyeDiameter, eyePaint );
        PNode rightEyeNode = new CircleNode( eyeDiameter, eyePaint );
        smileNode = new SmileNode( mouthRadius, mouthPaint, mouthStroke );
        frownNode = new FrownNode( mouthRadius, mouthPaint, mouthStroke );

        // rendering order
        addChild( headNode );
        addChild( leftEyeNode );
        addChild( rightEyeNode );
        addChild( smileNode );
        addChild( frownNode );

        // layout
        double x = 0;
        double y = 0;
        headNode.setOffset( x, y );
        x = ( 0.3 * headDiameter ) - ( leftEyeNode.getWidth() / 2 );
        y = ( 0.4 * headDiameter ) - ( leftEyeNode.getHeight() / 2 );
        leftEyeNode.setOffset( x, y );
        x = headDiameter - leftEyeNode.getXOffset() - rightEyeNode.getWidth();
        y = leftEyeNode.getYOffset();
        rightEyeNode.setOffset( x, y );
        x = 0.5 * headDiameter;
        y = 0.55 * headDiameter;
        smileNode.setOffset( x, y );
        x = smileNode.getXOffset();
        y = 0.65 * headDiameter;
        frownNode.setOffset( x, y );

        // default state
        smile();
    }

    public void smile() {
        smileNode.setVisible( true );
        frownNode.setVisible( false );
    }

    public void frown() {
        smileNode.setVisible( false );
        frownNode.setVisible( true );
    }

    // origin at upper left of bounding rectangle
    private static class CircleNode extends PPath {

        public CircleNode( double diameter, Paint paint ) {
            super();
            setPathTo( new Ellipse2D.Double( 0, 0, diameter, diameter ) );
            setPaint( paint );
            setStroke( null );
        }
    }

    private static class SmileNode extends PPath {

        public SmileNode( double radius, Paint strokePaint, Stroke stroke ) {
            super();
            setPathTo( new Arc2D.Double( new Rectangle2D.Double( -radius, -radius, 2 * radius, 2 * radius ), -30, -120, Arc2D.OPEN ) );
            setStrokePaint( strokePaint );
            setStroke( stroke );
        }
    }

    private static class FrownNode extends PPath {

        public FrownNode( double radius, Paint strokePaint, Stroke stroke ) {
            super();
            setPathTo( new Arc2D.Double( new Rectangle2D.Double( -radius, 0, 2 * radius, 2 * radius ), 40, 100, Arc2D.OPEN ) );
            setStrokePaint( strokePaint );
            setStroke( stroke );
        }
    }

}
