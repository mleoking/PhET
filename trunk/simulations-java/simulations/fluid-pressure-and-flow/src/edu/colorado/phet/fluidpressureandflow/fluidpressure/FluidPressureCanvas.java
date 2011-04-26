// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidpressure;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.view.*;

/**
 * @author Sam Reid
 */
public class FluidPressureCanvas extends FluidPressureAndFlowCanvas {

    private static final double modelHeight = Pool.DEFAULT_HEIGHT * 2.2;

    public FluidPressureCanvas( final FluidPressureModule module ) {
        super( ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 ), STAGE_SIZE.height / modelHeight ), module.getFluidPressureAndFlowModel().visibleModelBounds );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
        addChild( new PhetPPath( transform.modelToView( module.getFluidPressureAndFlowModel().getPool().getShape() ), Color.white ) );//so earth doesn't bleed through transparent pool
        addChild( new SidePoolHeightReadoutNode( transform, module.getFluidPressureAndFlowModel().getPool(), module.getFluidPressureAndFlowModel().units ) );
        for ( PressureSensor pressureSensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.getFluidPressureAndFlowModel().units, module.getFluidPressureAndFlowModel().getPool() ) );
        }

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( module.getFluidPressureAndFlowModel().getPool().getMinX(), module.getFluidPressureAndFlowModel().getPool().getMinY() );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, module.getFluidPressureAndFlowModel() );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, module.getFluidPressureAndFlowModel() );
        synchronizeRulerLocations( meterStick, englishRuler );
        addChild( meterStick );
        addChild( englishRuler );

        final PoolNode poolNode = new PoolNode( transform, module.getFluidPressureAndFlowModel().getPool(), module.getFluidPressureAndFlowModel().liquidDensity );
        addChild( poolNode );

        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new FluidPressureControlPanel<FluidPressureModel>( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, (int) ( FluidPressureCanvas.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
        }} );

        //Create and show the fluid density controls
        addFluidDensityControl( module );
    }
}
