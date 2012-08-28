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

import static edu.colorado.phet.functions.buildafunction.Constants.functionColor;

/**
 * @author Sam Reid
 */
public class TwoInputFunctionNode extends PNode {
    public TwoInputFunctionNode( String text, Type type ) {
        this( text, type, type, type );
    }

    public TwoInputFunctionNode( String text, Type input1, Type input2, Type output ) {
        //use CAG for prototype, may need to speed up later on
        final RoundRectangle2D.Double bodyRect = new RoundRectangle2D.Double( 0, 0, Constants.bodyDimension.width, Constants.bodyDimension.height * 2 + Constants.inset, 20, 20 );
        Area a = new Area( bodyRect );
        a.subtract( InputOutputShapes.getRightSide( input1, Vector2D.v( a.getBounds2D().getX(), a.getBounds2D().getCenterY() + Constants.bodyDimension.height * 0.5 ) ) );
        a.subtract( InputOutputShapes.getRightSide( input2, Vector2D.v( a.getBounds2D().getX(), a.getBounds2D().getCenterY() - Constants.bodyDimension.height * 0.5 ) ) );
        a.add( InputOutputShapes.getRightSide( output, Vector2D.v( a.getBounds2D().getMaxX(), a.getBounds2D().getCenterY() ) ) );
        addChild( new PhetPPath( a, functionColor.get(), new BasicStroke( 1 ), Color.black ) {{
            functionColor.addObserver( new VoidFunction1<Color>() {
                public void apply( final Color color ) {
                    setPaint( color );
                }
            } );
        }} );
        addChild( new PhetPText( text, new PhetFont( 42, true ) ) {{
            setOffset( bodyRect.getCenterX() - getFullBounds().getWidth() / 2 + 10, bodyRect.getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                TwoInputFunctionNode.this.moveToFront();
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( TwoInputFunctionNode.this.getParent() );
                translate( delta.width, delta.height );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }
}