// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.common.view.PressureSensorNode;
import edu.colorado.phet.fluidpressureandflow.pressure.FluidPressureModule;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.MAN_STANDING;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.POTTED_PLANT;

/**
 * Canvas for the "pressure" tab in Fluid Pressure and Flow.
 *
 * @author Sam Reid
 */
public class FluidPressureCanvas extends FluidPressureAndFlowCanvas<FluidPressureModel> {

    private static final double MODEL_HEIGHT = Pool.DEFAULT_HEIGHT * 2.2;

    public FluidPressureCanvas( final FluidPressureModule module ) {
        super( ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 ), STAGE_SIZE.height / MODEL_HEIGHT ) );

        //Show the sky
        addChild( new OutsideBackgroundNode( transform, 3, 1 ) );

        //Variables for convenient access
        final FluidPressureModel model = module.model;
        final Pool pool = model.pool;

        //Add a background behind the pool so earth doesn't bleed through transparent pool
        addChild( new PhetPPath( transform.modelToView( pool.getShape() ), Color.white ) );

        //Show the height on the side of the pool in selected right units
        addChild( new SidePoolHeightReadoutNode( transform, pool, model.units ) );

        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new FluidPressureControlPanel( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, INSET );
        }};
        addChild( controlPanelNode );

        //Show the reset button beneath the control panel
        addChild( new ResetAllButtonNode( module, this, (int) ( FluidPressureCanvas.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setConfirmationEnabled( false );
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + INSET * 2 );
        }} );

        //Add the draggable sensors in front of the control panels so they can't get lost behind the control panel
        for ( PressureSensor pressureSensor : model.getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, model.units, pool, visibleModelBounds ) {{

                //Since it is the focus of this tab, make the pressure sensor nodes larger
                scale( 1.2 );
            }} );
        }

        //Show the ruler
        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( pool.getMinX(), pool.getMinY() );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, model );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, model );
        synchronizeRulerLocations( meterStick, englishRuler );
        addChild( meterStick );
        addChild( englishRuler );

        //Show the pool itself
        addChild( new PoolNode( transform, pool, model.liquidDensity ) );

        //Create and show the fluid density controls
        addFluidDensityControl( module );

        //Add an image for a sense of scale
        addChild( new PImage( POTTED_PLANT ) {{
            double height = Math.abs( transform.modelToViewDeltaY( 3 / Units.FEET_PER_METER ) );
            double currentHeight = getFullBounds().getHeight();
            scale( height / currentHeight );

//            addInputEventListener( new PDragEventHandler() {
//                @Override protected void drag( PInputEvent event ) {
//                    super.drag( event );
//                    System.out.println( getOffset() );
//                }
//            } );
            setOffset( 115.50960118168459, 249.0989660265875 );
        }} );

        //Add an image for a sense of scale
        addChild( new PImage( MAN_STANDING ) {{
            double height = Math.abs( transform.modelToViewDeltaY( 6 / Units.FEET_PER_METER ) );
            double currentHeight = getFullBounds().getHeight();
            scale( height / currentHeight );

//            addInputEventListener( new PDragEventHandler() {
//                @Override protected void drag( PInputEvent event ) {
//                    super.drag( event );
//                    System.out.println( getOffset() );
//                }
//            } );
            setOffset( 744.2836041358973, 161.7134416543571 );
        }} );
    }
}