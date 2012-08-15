package edu.colorado.phet.functions.buildafunction2;

import fj.F;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.colorado.phet.functions.intro.Key;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class ValueNode extends PNode {

    private PNode node;
    private Area a;
    private PhetPPath boundNode;
    private Object originalValue;

    private ArrayList<F<Object, Object>> functions = new ArrayList<F<Object, Object>>();
    private final Color textPaint;

    public ValueNode( final ValueContext valueContext, Object originalValue, Stroke stroke, Color paint, Color strokePaint, Color textPaint ) {
        this.originalValue = originalValue;
        this.textPaint = textPaint;
        a = new Area( new Rectangle2D.Double( 0, 0, 50, Constants.ellipseWidth ) );
        double ellipseWidth = 50;
        a.add( new Area( new Ellipse2D.Double( a.getBounds2D().getMaxX() - ellipseWidth / 2, a.getBounds2D().getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        a.add( new Area( new Ellipse2D.Double( a.getBounds2D().getMinX() - ellipseWidth / 2, a.getBounds2D().getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        boundNode = new PhetPPath( a, paint, stroke, strokePaint );
        addChild( boundNode );
        this.node = toNode( getCurrentValue() );
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

    public Object getCurrentValue() {
        Object x = originalValue;
        for ( F<Object, Object> function : functions ) {
            x = function.f( x );
        }
        return x;
    }

    private static PhetPText toTextNode( String text, final Color textPaint ) {return new PhetPText( text, new PhetFont( 42, true ) ) {{setTextPaint( textPaint );}};}

    public void centerContent() {
        this.node.setOffset( a.getBounds2D().getCenterX() - node.getFullBounds().getWidth() / 2, a.getBounds2D().getCenterY() - node.getFullBounds().getHeight() / 2 );
    }

    public void setStrokePaint( final Color color ) { boundNode.setStrokePaint( color ); }

    public void applyFunction( F<Object, Object> f ) {
        System.out.println( getCurrentValue() );
        functions.add( f );
        System.out.println( getCurrentValue() );
        removeChild( node );
        node = toNode( getCurrentValue() );
        addChild( node );
        centerContent();
    }

    private PNode toNode( final Object currentValue ) {
        if ( currentValue instanceof Key ) {
            return new PImage( BufferedImageUtils.getRotatedImage( BufferedImageUtils.multiScaleToWidth( Images.KEY, 45 ), -Math.PI / 2 + ( (Key) currentValue ).getNumRotations() * Math.PI / 2 ) );
        }
        else {
            return toTextNode( currentValue.toString(), textPaint );
        }
    }

    public int getNumberRotations() { return ( (Key) getCurrentValue() ).numRotations; }
}