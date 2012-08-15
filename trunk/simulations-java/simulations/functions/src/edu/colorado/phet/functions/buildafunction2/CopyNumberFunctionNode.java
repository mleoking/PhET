package edu.colorado.phet.functions.buildafunction2;

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

import static edu.colorado.phet.functions.buildafunction2.Constants.*;

/**
 * @author Sam Reid
 */
public class CopyNumberFunctionNode extends PNode {
    public CopyNumberFunctionNode( String text ) {
        //use CAG for prototype, may need to speed up later on
        final RoundRectangle2D.Double bodyRect = new RoundRectangle2D.Double( 0, 0, bodyDimension.width, bodyDimension.height * 2 + inset, 20, 20 );
        Area a = new Area( bodyRect );
        double ellipseWidth = 50;
        a.subtract( new Area( new Ellipse2D.Double( bodyRect.getX() - ellipseWidth / 2, bodyRect.height / 2 - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        a.add( new Area( new Ellipse2D.Double( bodyRect.getMaxX() - ellipseWidth / 2, bodyDimension.height / 2 - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
        a.add( new Area( new Ellipse2D.Double( bodyRect.getMaxX() - ellipseWidth / 2, bodyDimension.height * 3.0 / 2 + inset - ellipseWidth / 2, ellipseWidth, ellipseWidth ) ) );
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
                CopyNumberFunctionNode.this.moveToFront();
            }

            @Override public void mouseDragged( final PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( CopyNumberFunctionNode.this.getParent() );
                translate( delta.width, delta.height );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }
}