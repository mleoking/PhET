package edu.colorado.phet.functions.buildafunction2;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class ValueNode extends PNode {
    public ValueNode( final int number, Stroke stroke, Color paint, Color strokePaint, final Color textPaint ) {
        this( new PhetPText( number + "", new PhetFont( 42, true ) ) {{setTextPaint( textPaint );}}, stroke, paint, strokePaint );
    }

    public ValueNode( PNode node, Stroke stroke, Color paint, Color strokePaint ) {
        final Area a = new Area( new Rectangle2D.Double( 0, 0, 50, Constants.ellipseWidth ) );
        double ellipseWidth = 50;
        a.add( new Area( new Ellipse2D.Double( a.getBounds2D().getMaxX() - ellipseWidth / 2, a.getBounds2D().getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        a.add( new Area( new Ellipse2D.Double( a.getBounds2D().getMinX() - ellipseWidth / 2, a.getBounds2D().getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        addChild( new PhetPPath( a, paint, stroke, strokePaint ) );
        node.setOffset( a.getBounds2D().getCenterX() - node.getFullBounds().getWidth() / 2, a.getBounds2D().getCenterY() - node.getFullBounds().getHeight() / 2 );
        addChild( node );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
//                ValueNode.this.moveToFront();
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( ValueNode.this.getParent() );
                translate( delta.width, delta.height );
                System.out.println( getOffset() );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }
}