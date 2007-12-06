package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Created by: Sam
 * Dec 5, 2007 at 2:44:47 PM
 */
public class MotionVectorNode extends PNode {
    private PhetPPath shapeNode;
    private ShadowPText textNode;
    private double textOffsetY;

    public MotionVectorNode( Font font, String text, Paint fill, Stroke stroke, Paint strokePaint ) {
        this( font, text, fill, stroke, strokePaint, 0.0 );
    }

    public MotionVectorNode( Font font, String text, Paint fill, Stroke stroke, Paint strokePaint, double textOffsetY ) {
        this.textOffsetY = textOffsetY;
        shapeNode = new PhetPPath( fill, stroke, strokePaint );
        addChild( shapeNode );
        textNode = new ShadowPText( text );
        textNode.setTextPaint( fill );
        textNode.setShadowColor( Color.black );
        textNode.setFont( font );
        textNode.scale( 1.0 / 40.0 );
        textNode.setOffset( 0, textOffsetY );
        addChild( textNode );
        setVector( 0, 0, 0, 0 );
    }

    public void setVector( double x, int y, double dx, double dy ) {
        if ( Math.abs( dx ) < 1E-4 && Math.abs( dy ) < 1E-4 ) {
            shapeNode.getPathReference().reset();
            firePropertyChange( PPath.PROPERTY_CODE_PATH, PPath.PROPERTY_PATH, null, shapeNode.getPathReference() );
            shapeNode.updateBoundsFromPath();
            invalidatePaint();
            textNode.setVisible( false );
        }
        else {
            Arrow arrow = new Arrow( new Point2D.Double( x, y ), new Point2D.Double( x + dx, y + dy ), 1, 1, 0.5, 0.5, false );
            final GeneralPath shape = arrow.getShape();
            shapeNode.setPathTo( shape );
            textNode.setVisible( true );
        }
        textNode.setOffset( x + dx, y + textOffsetY );
    }
}
