// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidDensityControl;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.GravityControl;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.flow.view.GridNode;
import edu.colorado.phet.fluidpressureandflow.pressure.FluidPressureModule;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

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
        addChild( new OutsideBackgroundNode( transform, 3, 1 ) {{
            module.model.atmosphere.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean atmosphere ) {
                    setVisible( atmosphere );
                }
            } );
        }} );

        addChild( new OutsideBackgroundNode( transform, 3, 1, OutsideBackgroundNode.DEFAULT_MODEL_BOUNDS.toRectangle2D(), Color.black, Color.black ) {{
            module.model.atmosphere.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean atmosphere ) {
                    setVisible( !atmosphere );
                }
            } );
        }} );

        //Variables for convenient access
        final FluidPressureModel model = module.model;
        final IPool pool = model.pool.get();

        //Add a background behind the pool so earth doesn't bleed through transparent pool
        addPoolSpecificNode( model, new Function1<IPool, PNode>() {
            @Override public PNode apply( final IPool p ) {
                return new PhetPPath( transform.modelToView( p.getShape() ), Color.white );
            }
        } );

        //Show the height on the side of the pool in selected right units
        addPoolSpecificNode( model, new Function1<IPool, PNode>() {
            @Override public PNode apply( final IPool p ) {
                if ( p instanceof Pool ) {
                    return new SidePoolHeightReadoutNode( transform, (Pool) p, model.units );
                }
                else { return new PNode(); }
            }
        } );

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

        //Add an image for a sense of scale
        addChild( new PImage( POTTED_PLANT ) {{
            double height = Math.abs( transform.modelToViewDeltaY( 3 / Units.FEET_PER_METER ) );
            double currentHeight = getFullBounds().getHeight();
            scale( height / currentHeight );
            setOffset( 115.50960118168459, 249.0989660265875 );//determined with a draghandler
        }} );

        //Show the ruler
        //Some nodes go behind the pool so that it looks like they submerge
        //Position the meter stick so that its origin is at the top of the pool since the rulers measure down in this tab
        final Point2D.Double rulerModelOrigin = new Point2D.Double( model.squarePool.getMinX(), model.squarePool.getMinY() );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, new Point2D.Double( rulerModelOrigin.getX(), model.squarePool.getMaxY() - MeterStick.LENGTH_SMALL ), model, true );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, model, true );
        synchronizeRulerLocations( meterStick, englishRuler );
        addChild( meterStick );
        addChild( englishRuler );

        //Show the pool itself
        addPoolSpecificNode( model, new Function1<IPool, PNode>() {
            @Override public PNode apply( final IPool p ) {
                return new PoolNode( transform, p, model.liquidDensity );
            }
        } );

        addChild( new GridNode( module.gridVisible, transform, model.units ) {{
            translate( -transform.modelToViewDeltaX( model.squarePool.getWidth() / 2 ), 0 );
        }} );

        //Create and show the fluid density and gravity controls
        //TODO: Layout for i18n long strings

        final GravityControl<FluidPressureModel> gravityControl = new GravityControl<FluidPressureModel>( module );
        final FluidPressureAndFlowControlPanelNode gravityControlPanelNode = new FluidPressureAndFlowControlPanelNode( gravityControl );

        final FluidDensityControl<FluidPressureModel> fluidDensityControl = new FluidDensityControl<FluidPressureModel>( module );
        final FluidPressureAndFlowControlPanelNode fluidDensityControlNode = new FluidPressureAndFlowControlPanelNode( fluidDensityControl );

        double maxControlWidth = Math.max( gravityControl.getMaximumSize().getWidth(), fluidDensityControl.getMaximumSize().getWidth() );

        gravityControlPanelNode.setOffset( STAGE_SIZE.getWidth() - maxControlWidth - INSET, STAGE_SIZE.getHeight() - gravityControl.getMaximumSize().getHeight() - INSET );
        fluidDensityControlNode.setOffset( STAGE_SIZE.getWidth() - maxControlWidth - INSET, gravityControlPanelNode.getFullBounds().getY() - fluidDensityControl.getMaximumSize().getHeight() - INSET );

        addChild( gravityControlPanelNode );
        addChild( fluidDensityControlNode );

        addChild( new FluidPressureFaucetNode( model.trapezoidPool.flowRatePercentage, model.trapezoidPool.faucetEnabled ) {{
            model.pool.valueEquals( model.trapezoidPool ).addObserver( new VoidFunction1<Boolean>() {
                @Override public void apply( final Boolean visible ) {
                    setVisible( visible );
                }
            } );

            //Center the faucet over the left opening, values sampled from a drag listener
            setOffset( new Point2D.Double( 109.2584933530281, 157.19350073855244 ) );
        }} );

        //Add the sensor toolbox node, which also adds the velocity and pressure sensors
        //Doing this later on ensures that the draggable sensors will appear in front of everything else
        addSensorToolboxNode( model, controlPanelNode );

        FluidPressureRadioButtonStrip fluidPressureRadioButtonStrip = new FluidPressureRadioButtonStrip( this, model ) {{
            setOffset( INSET, INSET );
        }};
        addChild( fluidPressureRadioButtonStrip );
    }

    //When the pool changes, replace the existing node with one for the given pool.
    private void addPoolSpecificNode( final FluidPressureModel model, final Function1<IPool, PNode> f ) {
        addChild( new PNode() {{
            model.pool.addObserver( new VoidFunction1<IPool>() {
                @Override public void apply( final IPool p ) {
                    removeAllChildren();
                    addChild( f.apply( p ) );
                }
            } );
        }} );
    }
}