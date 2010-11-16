package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class PoolHeightReadoutNode extends PNode {
    public PoolHeightReadoutNode( ModelViewTransform2D transform, Pool pool ) {
        final double dw = 5;
        PNode heightReadout = new PNode() {{
            PText text = new PText( "10 feet" ) {{
                setFont( new PhetFont( 16, true ) );
            }};
            addChild( new PhetPPath( new RoundRectangle2D.Double( -dw, -dw, text.getFullBounds().getWidth() + 2 * dw, text.getFullBounds().getHeight() + 2 * dw, 10, 10 ), Color.lightGray) );
            addChild( text );
        }};
        Rectangle2D bounds = transform.createTransformedShape( pool.getShape() ).getBounds2D();
        heightReadout.setOffset( bounds.getCenterX() - heightReadout.getFullBounds().getWidth() / 2, bounds.getY() + dw + 5 );
        addChild( heightReadout );
    }
}
