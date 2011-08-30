// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.common.view.ParticleNode;
import edu.colorado.phet.fluidpressureandflow.common.view.PressureSensorNode;
import edu.colorado.phet.fluidpressureandflow.flow.FluidFlowModule;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluidFlowModel;
import edu.colorado.phet.fluidpressureandflow.flow.model.FoodColoring;
import edu.colorado.phet.fluidpressureandflow.flow.model.Particle;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureCanvas;
import edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class FluidFlowCanvas extends FluidPressureAndFlowCanvas<FluidFlowModel> {
    private PNode particleLayer;
    private PNode foodColoringLayer;

    private static final double modelHeight = Pool.DEFAULT_HEIGHT * 3.2;
    private static final double pipeCenterY = -2;
    private static final double modelWidth = modelHeight / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();

    public FluidFlowCanvas( final FluidFlowModule module ) {
        super( ModelViewTransform.createRectangleInvertedYMapping( new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2 + pipeCenterY + 0.75, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ) ), module.model.visibleModelBounds );

        addChild( new OutsideBackgroundNode( transform, 3, 1 ) );

        addChild( new PipeBackNode( transform, module.model.pipe, module.model.liquidDensity ) );
        particleLayer = new PNode();
        foodColoringLayer = new PNode();
        addChild( foodColoringLayer );
        addChild( particleLayer );

        final FluidFlowModel model = module.model;

        addChild( new PipeFrontNode( transform, module.model.pipe ) );
        for ( final Particle p : module.model.getParticles() ) {
            addParticleNode( p );
        }

        //Add a piccolo graphic for the flux meter
        addChild( new FluxMeterNode( transform, module.model.fluxMeter ) );

        //When particle droplets are added, show graphics for them
        module.model.addParticleAddedObserver( new VoidFunction1<Particle>() {
            public void apply( Particle particle ) {
                addParticleNode( particle );
            }
        } );

        final DropperNode dropperNode = new DropperNode( transform, 3 * Math.PI / 2, model.pipe, new SimpleObserver() {
            public void update() {
                model.pourFoodColoring();
            }
        } );
        addChild( dropperNode );

        //Show a checkbox that enabled/disables adding dots to the fluid
        addChild( new PSwing( new PropertyCheckBox( "Dots", model.dropperEnabled ) {{
            setFont( FluidPressureCanvas.CONTROL_FONT );
            setBackground( new Color( 0, 0, 0, 0 ) );
        }} ) {{
            setOffset( dropperNode.getFullBounds().getMaxX(), dropperNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        //Add the colored fluid graphic when model instances are created
        model.addFoodColoringObserver( new VoidFunction1<FoodColoring>() {
            public void apply( FoodColoring foodColoring ) {
                addFoodColoringNode( foodColoring );
            }
        } );

        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new FluidFlowControlPanel( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, (int) ( FluidPressureCanvas.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - 2 );
        }} );

        //Create and show the fluid density controls
        addFluidDensityControl( module );

        //Some nodes go behind the pool so that it looks like they submerge
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, model );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, model );
        synchronizeRulerLocations( meterStick, englishRuler );
        addChild( meterStick );
        addChild( englishRuler );

        Property<Boolean> moduleActive = new Property<Boolean>( false ) {{
            module.addListener( new Module.Listener() {
                public void activated() {
                    set( true );
                }

                public void deactivated() {
                    set( false );
                }
            } );
        }};

        final Property<Boolean> clockRunning = new Property<Boolean>( true );
        //wire up the clock to be running if the module is active and if the clock control button has been pressed
        new And( clockRunning, moduleActive ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    model.getClock().setRunning( get() );
                }
            } );
        }};

        //Add clock controls (play/pause), including a time speed slider (no time readout)
        addChild( createClockControls( module, clockRunning ) );

        //Add a control for viewing and changing the fluid flow rate
        addChild( new FluidPressureAndFlowControlPanelNode( new FluidFlowControl( module ) ) {{
            setOffset( 10, 10 );
        }} );

        //Add the draggable sensors in front of the control panels so they can't get lost behind the control panel
        for ( PressureSensor sensor : module.model.getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, sensor, module.model.units ) );
        }

        addVelocitySensorNodes( module.model );
    }

    private void addFoodColoringNode( final FoodColoring p ) {
        final FoodColoringNode node = new FoodColoringNode( transform, p );
        foodColoringLayer.addChild( node );
        p.addRemovalListener( new SimpleObserver() {
            public void update() {
                foodColoringLayer.removeChild( node );
                p.removeRemovalListener( this );
            }
        } );
    }

    private void addParticleNode( final Particle p ) {
        final ParticleNode node = new ParticleNode( transform, p );
        particleLayer.addChild( node );
        p.addRemovalListener( new SimpleObserver() {
            public void update() {
                particleLayer.removeChild( node );
                p.removeRemovalListener( this );
            }
        } );
    }
}