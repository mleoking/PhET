package edu.colorado.phet.functions.buildafunction;

import fj.F;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.functions.intro.ShapeValue;
import edu.colorado.phet.functions.model.Type;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.functions.buildafunction.Constants.ellipseWidth;
import static edu.colorado.phet.functions.buildafunction.InputOutputShapes.getLeftSide;
import static edu.colorado.phet.functions.buildafunction.InputOutputShapes.getRightSide;

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
        a = new Area( new Rectangle2D.Double( 0, 0, 50, ellipseWidth ) );
        a.add( getLeftSide( getType( originalValue ), new Vector2D( a.getBounds2D().getMinX(), a.getBounds2D().getCenterY() ) ) );
        a.add( getRightSide( getType( originalValue ), new Vector2D( a.getBounds2D().getMaxX(), a.getBounds2D().getCenterY() ) ) );
        boundNode = new PhetPPath( a, paint, stroke, strokePaint );
        addChild( boundNode );
        this.node = toNode( getCurrentValue() );
        centerContent();
        addChild( node );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( ValueNode.this.getParent() );
                valueContext.mouseDragged( ValueNode.this, delta );
                System.out.println( "getOffset() = " + getOffset() );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                valueContext.mouseReleased( ValueNode.this );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }

    private Type getType( final Object value ) {
        return value instanceof ShapeValue ? Type.SHAPE :
               value instanceof String ? Type.TEXT :
               value instanceof Number ? Type.NUMBER :
               null;
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
        if ( currentValue instanceof ShapeValue ) {
            return ( (ShapeValue) currentValue ).toNode();
        }
        else {
            final PhetPText textNode = toTextNode( currentValue.toString(), textPaint );
            if ( textNode.getFullWidth() > ellipseWidth ) {
                textNode.scale( ellipseWidth / textNode.getFullWidth() );
            }
            return textNode;
        }
    }
}