// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Thermometer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Piccolo node that represents a thermometer in the view.
 *
 * @author John Blanco
 */
public class ThermometerNode extends PNode {


    public ThermometerNode( final Thermometer thermometer, final ModelViewTransform mvt ) {

        // Extract the scale transform from the MVT so that we can separate the
        // shape from the position of the block.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getTransform().getScaleX(), mvt.getTransform().getScaleY() );

        // Add the body of the thermometer.
        LiquidExpansionThermometerNode thermometerBody = new LiquidExpansionThermometerNode( new PDimension( mvt.modelToViewDeltaX( thermometer.getRect().getWidth() ),
                                                                                                             -mvt.modelToViewDeltaY( thermometer.getRect().getHeight() ) ) ) {{
            setOutlineStrokeWidth( 2 );
            setTicks( -mvt.modelToViewDeltaY( thermometer.getRect().getHeight() ) / 10, Color.BLACK, 2 );

        }};
        addChild( thermometerBody );

        // Add the triangle that shows where the thermometer is touching.
        double triangleEdgeSize = thermometerBody.getBulbDiameter() * 0.3;
        ImmutableVector2D apex1 = new ImmutableVector2D( -thermometerBody.getBulbDiameter() * 0.3, thermometerBody.getFullBoundsReference().getMaxY() - thermometerBody.getBulbDiameter() / 2 );
        ImmutableVector2D apex2 = apex1.getAddedInstance( new ImmutableVector2D( triangleEdgeSize, 0 ).getRotatedInstance( Math.PI / 4 ) );
        ImmutableVector2D apex3 = apex1.getAddedInstance( new ImmutableVector2D( triangleEdgeSize, 0 ).getRotatedInstance( -Math.PI / 4 ) );
        DoubleGeneralPath trianglePath = new DoubleGeneralPath( apex1 );
        trianglePath.lineTo( apex2 );
        trianglePath.lineTo( apex3 );
        trianglePath.closePath();
        addChild( new PhetPPath( trianglePath.getGeneralPath(), PhetColorScheme.RED_COLORBLIND, new BasicStroke( 2 ), Color.BLACK ) );

        // Add the cursor handler.
        addInputEventListener( new CursorHandler( CursorHandler.HAND ) );

        // Add the drag handler.
        addInputEventListener( new MovableElementDragHandler( thermometer, this, mvt ) );

        // Update the offset if and when the model position changes.
        thermometer.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D position ) {
                setOffset( mvt.modelToView( position ).toPoint2D() );
            }
        } );
    }
}
