package edu.colorado.phet.functions.buildafunction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.functions.model.Type;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.functions.buildafunction.Constants.*;

/**
 * @author Sam Reid
 */
public class CopyFunctionNode extends PNode {
    public CopyFunctionNode( String text, Type type, double x, double y ) {
        //use CAG for prototype, may need to speed up later on
        final RoundRectangle2D.Double bodyRect = new RoundRectangle2D.Double( 0, 0, bodyDimension.width, bodyDimension.height * 2 + inset, 20, 20 );
        Area a = new Area( bodyRect );
        final double maxX = a.getBounds2D().getMaxX();
        a.add( InputOutputShapes.getRightSide( type, Vector2D.v( maxX, a.getBounds2D().getCenterY() + Constants.bodyDimension.height * 0.5 ) ) );
        a.add( InputOutputShapes.getRightSide( type, Vector2D.v( maxX, a.getBounds2D().getCenterY() - Constants.bodyDimension.height * 0.5 ) ) );
        a.subtract( InputOutputShapes.getRightSide( type, Vector2D.v( a.getBounds2D().getX(), a.getBounds2D().getCenterY() ) ) );

        addChild( new PhetPPath( a, functionColor.get(), new BasicStroke( 1 ), Color.black ) {{
            functionColor.addObserver( new VoidFunction1<Color>() {
                public void apply( final Color color ) {
                    setPaint( color );
                }
            } );
        }} );
        addChild( new PhetPText( text, new PhetFont( 24, true ) ) {{
            setOffset( bodyRect.getCenterX() - getFullBounds().getWidth() / 2 + 10, bodyRect.getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                CopyFunctionNode.this.moveToFront();
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( CopyFunctionNode.this.getParent() );
                translate( delta.width / getScale(), delta.height / getScale() );
            }
        } );
        addInputEventListener( new CursorHandler() );
        setOffset( x, y );
    }
}