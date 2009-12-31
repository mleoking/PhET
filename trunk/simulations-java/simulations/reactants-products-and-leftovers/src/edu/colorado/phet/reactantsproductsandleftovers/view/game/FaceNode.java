package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
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
    
    // head properties
    private static final float HEAD_DIAMETER = 150f;
    private static final Stroke HEAD_STROKE = new BasicStroke( 4f );
    private static final Color HEAD_STROKE_COLOR = Color.YELLOW;
    private static final Color HEAD_FILL_COLOR = new Color( 255, 255, 0, 180 ); // translucent yellow
    
    // eye properties
    private static final float EYE_DIAMETER = 0.15f * HEAD_DIAMETER;
    private static final Stroke EYE_STROKE = null;
    private static final Color EYE_STROKE_COLOR = null;
    private static final Color EYE_FILL_COLOR = Color.BLACK;
    
    // mouth properties
    private static final float MOUTH_RADIUS = HEAD_DIAMETER/4;
    private static final Stroke MOUTH_STROKE = new BasicStroke( 6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    private static final Color MOUTH_STROKE_COLOR = Color.BLACK;
    
    private final PPath smileNode;
    private final PPath frownNode;
    
    public FaceNode() {
        super();
        
        PNode headNode = new HeadNode();
        PNode leftEyeNode = new EyeNode();
        PNode rightEyeNode = new EyeNode();
        smileNode = new SmileNode();
        frownNode = new FrownNode();
        
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
        x = 0.3 * HEAD_DIAMETER;
        y = 0.4 * HEAD_DIAMETER;
        leftEyeNode.setOffset( x, y );
        x = HEAD_DIAMETER - leftEyeNode.getXOffset();
        y = leftEyeNode.getYOffset();
        rightEyeNode.setOffset( x, y );
        x = 0.5 * HEAD_DIAMETER;
        y = 0.55 * HEAD_DIAMETER;
        smileNode.setOffset( x, y );
        x = smileNode.getXOffset();
        y = 0.65 * HEAD_DIAMETER;
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
    private static class HeadNode extends PPath {
        public HeadNode() {
            super();
            setPathTo( new Ellipse2D.Double( 0, 0, HEAD_DIAMETER, HEAD_DIAMETER ) );
            setStroke( HEAD_STROKE );
            setStrokePaint( HEAD_STROKE_COLOR );
            setPaint( HEAD_FILL_COLOR );
        }
    }
    
    // origin at geometric center
    private static class EyeNode extends PPath {
        public EyeNode() {
            super();
            setPathTo( new Ellipse2D.Double( -EYE_DIAMETER/2, -EYE_DIAMETER/2, EYE_DIAMETER, EYE_DIAMETER ) );
            setStroke( EYE_STROKE );
            setStrokePaint( EYE_STROKE_COLOR );
            setPaint( EYE_FILL_COLOR );
        }
    }
    
    private static class SmileNode extends PPath {
        public SmileNode() {
            super();
            setPathTo( new Arc2D.Double( new Rectangle2D.Double( -MOUTH_RADIUS, -MOUTH_RADIUS, 2*MOUTH_RADIUS, 2*MOUTH_RADIUS ), -30, -120, Arc2D.OPEN ) );//XXX
            setStroke( MOUTH_STROKE );
            setStrokePaint( MOUTH_STROKE_COLOR );
        }
    }
    
    private static class FrownNode extends PPath {
        public FrownNode() {
            super();
            setPathTo( new Arc2D.Double( new Rectangle2D.Double( -MOUTH_RADIUS, 0, 2*MOUTH_RADIUS, 2*MOUTH_RADIUS ), 40, 100, Arc2D.OPEN ) );//XXX
            setStroke( MOUTH_STROKE );
            setStrokePaint( MOUTH_STROKE_COLOR );
        }
    }

}
