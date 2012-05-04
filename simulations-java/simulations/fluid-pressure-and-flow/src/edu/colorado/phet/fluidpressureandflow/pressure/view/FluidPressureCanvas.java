// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.TexturePaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.GroundNode;
import edu.colorado.phet.common.piccolophet.nodes.background.SkyNode;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidDensityControl;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.GrassNode;
import edu.colorado.phet.fluidpressureandflow.common.view.GravityControl;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.flow.view.NumberedGridNode;
import edu.colorado.phet.fluidpressureandflow.flow.view.UnLabeledGridNode;
import edu.colorado.phet.fluidpressureandflow.pressure.FluidPressureModule;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;

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

    //How much to translate up the camera so that the bottom of the pool isn't too offscreen
    private static final int VIEW_OFFSET_Y = 10;

    public FluidPressureCanvas( final FluidPressureModule module ) {
        super( ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width / 2, STAGE_SIZE.height / 2 - VIEW_OFFSET_Y ), STAGE_SIZE.height / MODEL_HEIGHT ), new ImmutableVector2D( 0, 0 ) );

        //Show the sky, ground and grass
        addChild( new PNode() {{
            addChild( new PNode() {{
                module.model.atmosphere.addObserver( new VoidFunction1<Boolean>() {
                    public void apply( Boolean atmosphere ) {
                        removeAllChildren();
                        addChild( atmosphere ? new SkyNode( transform, new Rectangle2D.Double( -1000, 0, 2000, 2000 ), 20 )
                                             : new SkyNode( transform, new Rectangle2D.Double( -1000, 0, 2000, 2000 ), 20, Color.black, Color.black ) );
                    }
                } );
            }} );

            addChild( new PNode() {{
                new RichSimpleObserver() {
                    @Override public void update() {
                        removeAllChildren();
                        addChild( new GroundNode( transform, new Rectangle2D.Double( -1000, -2000, 2000, 2000 ), 5, FluidPressureAndFlowApplication.dirtTopColor.get(), FluidPressureAndFlowApplication.dirtBottomColor.get() ) );
                    }
                }.observe( FluidPressureAndFlowApplication.dirtTopColor, FluidPressureAndFlowApplication.dirtBottomColor );
            }} );

            //Add grass and pool concrete
            addChild( new PNode() {{
                module.model.pool.addObserver( new VoidFunction1<IPool>() {
                    public void apply( final IPool pool ) {
                        removeAllChildren();
                        ArrayList<Pair<Double, Double>> segments = pool.getGrassSegments();
                        for ( Pair<Double, Double> segment : segments ) {
                            addChild( GrassNode.GrassNode( transform, segment._1, segment._2 ) );
                        }

                        ArrayList<ArrayList<ImmutableVector2D>> lineSets = pool.getEdges();
                        for ( ArrayList<ImmutableVector2D> lines : lineSets ) {
                            DoubleGeneralPath path = new DoubleGeneralPath( lines.get( 0 ) );
                            for ( int i = 1; i < lines.size(); i++ ) {
                                path.lineTo( lines.get( i ) );
                            }
                            addChild( new PhetPPath( transform.modelToView( new BasicStroke( 0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND ).createStrokedShape( path.getGeneralPath() ) ),
                                                     new TexturePaint( Images.CEMENT_TEXTURE_DARK, new Rectangle2D.Double( 0, 0, Images.CEMENT_TEXTURE_DARK.getWidth(), Images.CEMENT_TEXTURE_DARK.getHeight() ) ) ) );
                        }
                    }
                } );
            }} );
        }} );

        //Variables for convenient access
        final FluidPressureModel model = module.model;

        //Create the faucet for the square pool scene.  Shown before pool background node so it will give us leeway in vertical pixels
        addChild( new DrainFaucetNode( model.pool, model.squarePool, 314.1624815361891 - 3 - 30 - 20 - 20 - 11, VIEW_OFFSET_Y, transform, model.liquidDensity ) );

        //Create the faucet for the trapezoidal mode.  Shown before pool background node so it will give us leeway in vertical pixels
        addChild( new DrainFaucetNode( model.pool, model.trapezoidPool, 314.1624815361891 - 3, VIEW_OFFSET_Y, transform, model.liquidDensity ) );

        //Add a background behind the pool so earth doesn't bleed through transparent pool
        addPoolSpecificNode( model, new Function1<IPool, PNode>() {
            public PNode apply( final IPool p ) {
                return new PhetPPath( transform.modelToView( p.getContainerShape() ), new Color( 240, 240, 240 ) );
            }
        } );

        //Show the height on the side of the pool in selected right units
        //Disabled on 5/4/2012 based on AP recommendation
//        addPoolSpecificNode( model, new Function1<IPool, PNode>() {
//            public PNode apply( final IPool p ) {
//                if ( p instanceof SquarePool ) {
//                    return new SidePoolHeightReadoutNode( transform, (SquarePool) p, model.units );
//                }
//                else { return new PNode(); }
//            }
//        } );

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

        final ValueEquals<IPool> squarePool = model.pool.valueEquals( model.squarePool );

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
            public PNode apply( final IPool p ) {
                return new PoolNode( transform, p, model.liquidDensity );
            }
        } );

        //Grid for scene 1
        addChild( new NumberedGridNode( module.gridVisible.and( squarePool ), transform, model.units ) {{
            translate( transform.modelToViewDeltaX( model.squarePool.getMinX() ), 0 );
        }} );

        //Grid for scene 1-2
        addChild( new UnLabeledGridNode( module.gridVisible.and( not( squarePool ) ), transform, model.units ) );

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

        //Create the input faucets
        addChild( new InputFaucetNode( model, model.squarePool, VIEW_OFFSET_Y, transform, 120 ) );
        addChild( new InputFaucetNode( model, model.trapezoidPool, VIEW_OFFSET_Y, transform, 109.2584933530281 ) );

        final PNode massesNode = new PNode() {{
            model.pool.valueEquals( model.chamberPool ).addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean visible ) {
                    setVisible( visible );
                }
            } );

            //On 4/9/2012 we decided to remove this label because it is unnecessary, but I'll leave the code here in case others object
//            addChild( new PhetPText( "Masses", new PhetFont( 18 ) ) {{ setOffset( new Point2D.Double( 54.01477104874449, 343.01329394387 ) );//Sampled from a drag listener }} );

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
                public void apply( final IPool p ) {
                    removeAllChildren();
                    addChild( f.apply( p ) );
                }
            } );
        }} );
    }
}