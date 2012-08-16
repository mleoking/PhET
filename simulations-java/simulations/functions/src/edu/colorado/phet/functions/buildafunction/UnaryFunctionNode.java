package edu.colorado.phet.functions.buildafunction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
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

    public UnaryFunctionNode( String text, boolean draggable ) {
        this( new PhetPText( text, new PhetFont( 46, true ) ), draggable );
    }

    public UnaryFunctionNode( PNode icon, boolean draggable ) {
        //use CAG for prototype, may need to speed up later on
        Area a = new Area( bodyRect );
        double ellipseWidth = Constants.ellipseWidth;
        a.subtract( new Area( new Ellipse2D.Double( bodyRect.getX() - ellipseWidth / 2, bodyRect.getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        a.add( new Area( new Ellipse2D.Double( bodyRect.getMaxX() - ellipseWidth / 2, bodyRect.getCenterY() - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
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
    }
}