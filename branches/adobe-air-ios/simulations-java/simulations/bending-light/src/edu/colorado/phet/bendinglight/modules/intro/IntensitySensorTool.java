// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.model.IntensityMeter;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.IntensityMeterNode;
import edu.colorado.phet.bendinglight.view.ToolboxNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.NodeFactory;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.ToolIconNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Tool for dragging out the Intensity Sensor
 *
 * @author Sam Reid
 */
public class IntensitySensorTool extends ToolIconNode<BendingLightCanvas> {
    public IntensitySensorTool( final BendingLightCanvas canvas,
                                final ModelViewTransform transform,
                                final IntensityMeter intensityMeter,
                                final double modelWidth,
                                final double modelHeight,
                                final ToolboxNode parent,
                                final IntensityMeterNode iconNode,
                                final int sensorIconHeight ) {
        super( iconNode.toImage( ToolboxNode.ICON_WIDTH, sensorIconHeight, new Color( 0, 0, 0, 0 ) ),
               intensityMeter.enabled,
               transform,
               canvas,
               //Factory that creates new IntensityMeterNodes at the right location
               new NodeFactory() {
                   public IntensityMeterNode createNode( ModelViewTransform transform, Property<Boolean> visible, final Point2D location ) {
                       return new IntensityMeterNode( transform, intensityMeter ) {{
                           //Set the correct relative locations
                           intensityMeter.sensorPosition.set( new ImmutableVector2D( modelWidth * 0.3, -modelHeight * 0.3 ) );
                           intensityMeter.bodyPosition.set( new ImmutableVector2D( modelWidth * 0.4, -modelHeight * 0.3 ) );

                           //Move everything so the mouse is at the middle of the sensor probe
                           final ImmutableVector2D delta = new ImmutableVector2D( location ).minus( intensityMeter.sensorPosition.get() );
                           intensityMeter.translateAll( new PDimension( delta.getX(), delta.getY() ) );
                       }};
                   }
               },
               canvas.getModel(),
               new Function0<Rectangle2D>() {
                   public Rectangle2D apply() {
                       return parent.getGlobalFullBounds();
                   }
               }
        );
    }

    //Intensity sensor must go behind the light so the light goes in front for an interception
    @Override protected void addChild( BendingLightCanvas canvas, ToolNode node ) {
        canvas.addChildBehindLight( node );
    }

    @Override protected void removeChild( BendingLightCanvas canvas, ToolNode node ) {
        canvas.removeChildBehindLight( node );
    }
}
