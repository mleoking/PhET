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

    private PNode node;
    private Area a;
    private PhetPPath boundNode;
    private int numRotations = 0;

    public ValueNode( ValueContext valueContext, final int number, Stroke stroke, Color paint, Color strokePaint, final Color textPaint ) {
        this( valueContext, new PhetPText( number + "", new PhetFont( 42, true ) ) {{setTextPaint( textPaint );}}, stroke, paint, strokePaint );
    }

    public ValueNode( final ValueContext valueContext, PNode node, Stroke stroke, Color paint, Color strokePaint ) {
        a = new Area( new Rectangle2D.Double( 0, 0, 50, Constants.ellipseWidth ) );
        double ellipseWidth = 50;
        a.add( new Area( new Ellipse2D.Double( a.getBounds2D().getMaxX() - ellipseWidth / 2, a.getBounds2D().getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        a.add( new Area( new Ellipse2D.Double( a.getBounds2D().getMinX() - ellipseWidth / 2, a.getBounds2D().getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        boundNode = new PhetPPath( a, paint, stroke, strokePaint );
        addChild( boundNode );
        this.node = node;
        centerContent();
        addChild( node );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
//                ValueNode.this.moveToFront();
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( ValueNode.this.getParent() );
                valueContext.mouseDragged( ValueNode.this, delta );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                valueContext.mouseReleased( ValueNode.this );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }

    public void centerContent() {
        this.node.setOffset( a.getBounds2D().getCenterX() - node.getFullBounds().getWidth() / 2, a.getBounds2D().getCenterY() - node.getFullBounds().getHeight() / 2 );
    }

    public void setStrokePaint( final Color color ) {
        boundNode.setStrokePaint( color );
    }

    public void rotateRight() {
        numRotations++;
    }

    public int getNumberRotations() {
        return numRotations;
    }
}