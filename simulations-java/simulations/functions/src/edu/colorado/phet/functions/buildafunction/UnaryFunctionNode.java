package edu.colorado.phet.functions.buildafunction;

import fj.F;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.functions.FunctionsResources.Images;
import edu.colorado.phet.functions.model.Type;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.functions.buildafunction.Constants.functionColor;

/**
 * @author Sam Reid
 */
public class UnaryFunctionNode extends PNode {
    public static final RoundRectangle2D.Double bodyRect = new RoundRectangle2D.Double( 0, 0, Constants.bodyDimension.width, Constants.bodyDimension.height, 20, 20 );
    private static boolean dragForLayout = false;
    public final F<Object, Object> function;

    public UnaryFunctionNode( String text, boolean draggable, F<Object, Object> function, Type input, Type output ) {
        this( text, draggable, function, input, output, 0, 0 );
    }

    public UnaryFunctionNode( String text, boolean draggable, F<Object, Object> function, Type input, Type output, double x, double y ) {
        this( new PhetPText( text, new PhetFont( 46, true ) ), draggable, function, input, output, x, y );
    }

    public UnaryFunctionNode( PNode icon, boolean draggable, F<Object, Object> function, Type input, Type output ) {
        this( icon, draggable, function, input, output, 0, 0 );
    }

    public UnaryFunctionNode( PNode icon, boolean draggable, F<Object, Object> function, Type input, Type output, double x, double y ) {
        this.function = function;
        //use CAG for prototype, may need to speed up later on
        Area a = new Area( bodyRect );
        a.subtract( InputOutputShapes.getRightSide( input, new Vector2D( a.getBounds2D().getMinX(), a.getBounds2D().getCenterY() ) ) );
        a.add( InputOutputShapes.getRightSide( output, new Vector2D( a.getBounds2D().getMaxX(), a.getBounds2D().getCenterY() ) ) );
        addChild( new PhetPPath( a, functionColor.get(), new BasicStroke( 1 ), Color.black ) {{
            functionColor.addObserver( new VoidFunction1<Color>() {
                public void apply( final Color color ) {
                    setPaint( color );
                }
            } );
        }} );
        addChild( icon );
        icon.setOffset( bodyRect.getCenterX() - icon.getFullBounds().getWidth() / 2 + 10, bodyRect.getCenterY() - icon.getFullBounds().getHeight() / 2 );

        if ( draggable ) {
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( final PInputEvent event ) {
                    UnaryFunctionNode.this.moveToFront();
                }

                @Override public void mouseDragged( final PInputEvent event ) {
                    PDimension delta = event.getDeltaRelativeTo( UnaryFunctionNode.this.getParent() );
                    translate( delta.width, delta.height );
                }
            } );
            addInputEventListener( new CursorHandler() );
        }
        else if ( dragForLayout ) {
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseDragged( final PInputEvent event ) {
                    super.mouseDragged( event );
                    translate( event.getDeltaRelativeTo( getParent() ).width, event.getDeltaRelativeTo( getParent() ).height );
                    System.out.println( "getOffset() = " + getOffset() );
                }
            } );
        }

        final HTMLImageButtonNode htmlImageButtonNode = new HTMLImageButtonNode( Images.GRID_ICON ) {{
            setBackground( Color.orange );
            scale( 0.5 );
            setOffset( bodyRect.getWidth() - getFullBounds().getWidth() - 6, 4 );
        }};
        addChild( htmlImageButtonNode );

        setOffset( x, y );
    }
}