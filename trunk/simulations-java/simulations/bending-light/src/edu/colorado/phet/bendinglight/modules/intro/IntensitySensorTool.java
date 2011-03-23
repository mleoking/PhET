// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.model.IntensityMeter;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.IntensityMeterNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class IntensitySensorTool extends Tool {
    public IntensitySensorTool( final BendingLightCanvas canvas,
                                final ModelViewTransform transform,
                                final IntensityMeter intensityMeter,
                                final double modelWidth,
                                final double modelHeight,
                                final ToolboxNode parent,
                                final IntensityMeterNode iconNode,
                                final int sensorIconHeight ) {
        super( iconNode.toImage( ToolboxNode.ICON_WIDTH, sensorIconHeight, new Color( 0, 0, 0, 0 ) ),
               intensityMeter.enabled, transform, canvas, new Tool.NodeFactory() {
                    public IntensityMeterNode createNode( ModelViewTransform transform, Property<Boolean> visible, final Point2D location ) {
                        return new IntensityMeterNode( transform, intensityMeter ) {{
                            intensityMeter.sensorPosition.setValue( new ImmutableVector2D( modelWidth * 0.3, -modelHeight * 0.3 ) );
                            intensityMeter.bodyPosition.setValue( new ImmutableVector2D( modelWidth * 0.4, -modelHeight * 0.3 ) );
                            final ImmutableVector2D delta = new ImmutableVector2D( location ).minus( intensityMeter.sensorPosition.getValue() );
                            intensityMeter.translateAll( new PDimension( delta.getX(), delta.getY() ) );
                        }};
                    }
                }, canvas.getModel(), new Function0<Rectangle2D>() {
                    public Rectangle2D apply() {
                        return parent.getGlobalFullBounds();
                    }
                } );
    }

    @Override protected void addChild( BendingLightCanvas canvas, ToolNode node ) {
        canvas.addChildBehindLight( node );
    }
}
