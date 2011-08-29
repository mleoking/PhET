// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.common.view.PoolNode;
import edu.colorado.phet.fluidpressureandflow.common.view.PressureSensorNode;
import edu.colorado.phet.fluidpressureandflow.pressure.FluidPressureModule;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;

/**
 * @author Sam Reid
 */
public class FluidPressureCanvas extends FluidPressureAndFlowCanvas {

    private static final double modelHeight = Pool.DEFAULT_HEIGHT * 2.2;

    public FluidPressureCanvas( final FluidPressureModule module ) {
        super( ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 ), STAGE_SIZE.height / modelHeight ), module.model.visibleModelBounds );

        addChild( new OutsideBackgroundNode( transform, 3, 1 ) );
        addChild( new PhetPPath( transform.modelToView( module.model.getPool().getShape() ), Color.white ) );//so earth doesn't bleed through transparent pool
        addChild( new SidePoolHeightReadoutNode( transform, module.model.getPool(), module.model.units ) );

        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new FluidPressureControlPanel<FluidPressureModel>( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, (int) ( FluidPressureCanvas.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + 20 );
        }} );

        //Add the draggable sensors in front of the control panels so they can't get lost behind the control panel
        for ( PressureSensor pressureSensor : module.model.getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, module.model.units, module.model.getPool() ) );
        }

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( module.model.getPool().getMinX(), module.model.getPool().getMinY() );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, module.model );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, module.model );
        synchronizeRulerLocations( meterStick, englishRuler );
        addChild( meterStick );
        addChild( englishRuler );

        final PoolNode poolNode = new PoolNode( transform, module.model.getPool(), module.model.liquidDensity );
        addChild( poolNode );

        //Create and show the fluid density controls
        addFluidDensityControl( module );
    }
}
