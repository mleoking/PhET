// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetSliderNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Units;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidDensityControl;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.GravityControl;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.flow.view.NumberedGridNode;
import edu.colorado.phet.fluidpressureandflow.flow.view.UnLabeledGridNode;
import edu.colorado.phet.fluidpressureandflow.pressure.FluidPressureModule;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.DRAIN_FAUCET_ATTACHED;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.POTTED_PLANT;

/**
 * Canvas for the "pressure" tab in Fluid Pressure and Flow.
 * Parameters tweaked to optimize
 * (a) amount of height the water in the right chamber moves up and
 * (b) the amount of change of the pressure
 * Parameters must account for the entire range of allowed density values, and do not allow masses to protrude completely into the lower left chamber.
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

        //Create the faucet for the trapezoidal mode.  Shown before pool background node so it will give us leeway in vertical pixels
        final PNode outputFaucetAndWater = new PNode() {{
            model.pool.valueEquals( model.trapezoidPool ).addObserver( new VoidFunction1<Boolean>() {
                @Override public void apply( final Boolean visible ) {
                    setVisible( visible );
                }
            } );

            final PImage drainFaucetImage = new PImage( BufferedImageUtils.multiScaleToHeight( DRAIN_FAUCET_ATTACHED, (int) ( DRAIN_FAUCET_ATTACHED.getHeight() * 1.2 ) ) ) {{

                //Center the faucet over the left opening, values sampled from a drag listener
                setOffset( new Point2D.Double( 314.1624815361891 - 3, 644.3426883308715 - 1 ) );

                FaucetSliderNode sliderNode = new FaucetSliderNode( UserComponentChain.chain( FPAFSimSharing.UserComponents.drainFaucet, UserComponents.slider ), model.trapezoidPool.drainFaucetEnabled, 1, model.trapezoidPool.drainFlowRate, true ) {{
                    setOffset( 4, 8 + 5 - 1 ); //TODO #3199, change offsets when the faucet images are revised, make these constants
                    scale( FaucetNode.HANDLE_SIZE.getWidth() / getFullBounds().getWidth() * 1.2 ); //scale to fit into the handle portion of the faucet image
                }};
                addChild( sliderNode );
            }};

            //Show the water coming out of the faucet
            addChild( new OutputFlowingWaterNode( model.trapezoidPool, model.trapezoidPool.drainFlowRate, transform, model.liquidDensity, model.trapezoidPool.drainFaucetEnabled ) );
            addChild( drainFaucetImage );
        }};
        addChild( outputFaucetAndWater );

        //Add a background behind the pool so earth doesn't bleed through transparent pool
        addPoolSpecificNode( model, new Function1<IPool, PNode>() {
            @Override public PNode apply( final IPool p ) {
                return new PhetPPath( transform.modelToView( p.getContainerShape() ), Color.white );
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
        final ValueEquals<IPool> squarePool = model.pool.valueEquals( model.squarePool );
        addChild( new PImage( POTTED_PLANT ) {{
            squarePool.addObserver( new VoidFunction1<Boolean>() {
                @Override public void apply( final Boolean v ) {
                    setVisible( v );
                }
            } );
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

        //Grid for scene 1
        addChild( new NumberedGridNode( module.gridVisible.and( squarePool ), transform, model.units ) {{
            translate( -transform.modelToViewDeltaX( model.squarePool.getWidth() / 2 ), 0 );
        }} );

        //Grid for scene 1
        addChild( new UnLabeledGridNode( module.gridVisible.and( not( squarePool ) ), transform, model.units ) {{
            translate( -transform.modelToViewDeltaX( model.squarePool.getWidth() / 2 ), 0 );
        }} );


        //Create and show the fluid density and gravity controls
        //TODO: Layout for i18n long strings

        final GravityControl<FluidPressureModel> gravityControl = new GravityControl<FluidPressureModel>( module );
        final FluidPressureAndFlowControlPanelNode gravityControlPanelNode = new FluidPressureAndFlowControlPanelNode( gravityControl );

        final FluidDensityControl<FluidPressureModel> fluidDensityControl = new FluidDensityControl<FluidPressureModel>( module );
        final FluidPressureAndFlowControlPanelNode fluidDensityControlNode = new FluidPressureAndFlowControlPanelNode( fluidDensityControl );

        double maxControlWidth = Math.max( gravityControl.getMaximumSize().getWidth(), fluidDensityControl.getMaximumSize().getWidth() );

        //I'm not sure why the factor of 2 is needed in these insets, but without it no insets appear
        gravityControlPanelNode.setOffset( STAGE_SIZE.getWidth() - maxControlWidth - INSET * 2, STAGE_SIZE.getHeight() - gravityControl.getMaximumSize().getHeight() - INSET * 2 );
        fluidDensityControlNode.setOffset( STAGE_SIZE.getWidth() - maxControlWidth - INSET * 2, gravityControlPanelNode.getFullBounds().getY() - fluidDensityControl.getMaximumSize().getHeight() - INSET * 2 );

        addChild( gravityControlPanelNode );
        addChild( fluidDensityControlNode );

        //Create the faucet for the trapezoidal mode
        final PNode inputFaucetAndWater = new PNode() {{
            model.pool.valueEquals( model.trapezoidPool ).addObserver( new VoidFunction1<Boolean>() {
                @Override public void apply( final Boolean visible ) {
                    setVisible( visible );
                }
            } );

            final FluidPressureFaucetNode faucetNode = new FluidPressureFaucetNode( model.trapezoidPool.inputFlowRatePercentage, model.trapezoidPool.inputFaucetEnabled ) {{

                //Center the faucet over the left opening, values sampled from a drag listener
                setOffset( new Point2D.Double( 109.2584933530281, 157.19350073855244 ) );
            }};

            //Show the water coming out of the faucet
            addChild( new InputFlowingWaterNode( model.trapezoidPool, model.trapezoidPool.inputFlowRatePercentage, transform, model.liquidDensity, model.trapezoidPool.inputFaucetEnabled ) );
            addChild( faucetNode );

        }};
        addChild( inputFaucetAndWater );

        final PNode massesNode = new PNode() {{
            model.pool.valueEquals( model.chamberPool ).addObserver( new VoidFunction1<Boolean>() {
                @Override public void apply( final Boolean visible ) {
                    setVisible( visible );
                }
            } );
            addChild( new PhetPText( "Masses", new PhetFont( 18 ) ) {{
                setOffset( new Point2D.Double( 54.01477104874449, 343.01329394387 ) );//Sampled from a drag listener
            }} );
            addChild( new MassesLayer( model.chamberPool, transform ) );
        }};
        addChild( massesNode );

        //Add the sensor toolbox node, which also adds the velocity and pressure sensors
        //Doing this later on ensures that the draggable sensors will appear in front of everything else
        addSensorToolboxNode( model, controlPanelNode, model.pool );

        FluidPressureRadioButtonStripControlPanelNode fluidPressureRadioButtonStrip = new FluidPressureRadioButtonStripControlPanelNode( this, model ) {{
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