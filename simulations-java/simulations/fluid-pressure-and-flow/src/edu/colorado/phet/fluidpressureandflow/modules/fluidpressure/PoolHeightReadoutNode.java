package edu.colorado.phet.fluidpressureandflow.modules.fluidpressure;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.Units;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class PoolHeightReadoutNode extends PNode {
    public PoolHeightReadoutNode( ModelViewTransform transform, final Pool pool, final Property<Units.Unit> distanceUnitProperty ) {
        final double dw = 5;
        final PhetPPath background = new PhetPPath( Color.white );
        PNode heightReadout = new PNode() {{
            PText text = new PText() {{
                setFont( new PhetFont( 16, true ) );
                distanceUnitProperty.addObserver( new SimpleObserver() {
                    public void update() {
                        DecimalFormat format = new DecimalFormat( "0.000" );
                        if ( distanceUnitProperty.getValue() == Units.FEET ) {
                            format = new DecimalFormat( "0" );
                        }
                        setText( format.format( distanceUnitProperty.getValue().siToUnit( pool.getHeight() ) ) + " " + distanceUnitProperty.getValue().getAbbreviation() );
                        background.setPathTo( new RoundRectangle2D.Double( -dw, -dw, getFullBounds().getWidth() + ( 2 * dw ), getFullBounds().getHeight() + 2 * dw, 10, 10 ) );
                    }
                } );
            }};
            addChild( background );
            addChild( text );
        }};
        Rectangle2D bounds = transform.createTransformedShape( pool.getShape() ).getBounds2D();
        heightReadout.setOffset( bounds.getCenterX() - heightReadout.getFullBounds().getWidth() / 2, bounds.getY() + dw + 5 );
        addChild( heightReadout );
    }
}
