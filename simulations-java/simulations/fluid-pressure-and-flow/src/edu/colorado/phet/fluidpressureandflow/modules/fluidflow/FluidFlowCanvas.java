// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.fluidpressureandflow.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.FluidPressureControlPanel;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.Pool;
import edu.colorado.phet.fluidpressureandflow.view.*;
import edu.umd.cs.piccolo.PNode;

//import edu.colorado.phet.fluidpressureandflow.model.VelocitySensor;

/**
 * @author Sam Reid
 */
public class FluidFlowCanvas extends FluidPressureAndFlowCanvas {
    private PNode particleLayer;
    private PNode foodColoringLayer;

    private static final double modelHeight = Pool.DEFAULT_HEIGHT * 3.2;
    private static final double pipeCenterY = -2;
    private static final double modelWidth = modelHeight / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();

    public FluidFlowCanvas( final FluidFlowModule module ) {
        super( ModelViewTransform.createRectangleInvertedYMapping( new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2 + pipeCenterY + 0.75, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ) ) );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );

        addChild( new PipeBackNode( transform, module.getFluidFlowModel().getPipe(), module.getFluidFlowModel().liquidDensity ) );
        particleLayer = new PNode();
        foodColoringLayer = new PNode();
        addChild( foodColoringLayer );
        addChild( particleLayer );
        addChild( new PipeFrontNode( transform, module.getFluidFlowModel().getPipe() ) );
        for ( final Particle p : module.getFluidFlowModel().getParticles() ) {
            addParticleNode( p );
        }
        module.getFluidFlowModel().addParticleAddedObserver( new VoidFunction1<Particle>() {
            public void apply( Particle particle ) {
                addParticleNode( particle );
            }
        } );

        for ( PressureSensor sensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, sensor, module.getFluidPressureAndFlowModel().pressureUnit ) );
        }

        addVelocitySensorNodes( module.getFluidPressureAndFlowModel() );

        final FluidFlowModel model = module.getFluidFlowModel();

        addChild( new ParticleInjectorNode( transform, 3 * Math.PI / 2, model.getPipe(), new SimpleObserver() {
            public void update() {
                model.pourFoodColoring();
            }
        } ) );

        model.addFoodColoringObserver( new VoidFunction1<FoodColoring>() {
            public void apply( FoodColoring foodColoring ) {
                addFoodColoringNode( foodColoring );
            }
        } );

        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new FluidFlowControlPanel<FluidFlowModel>( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, 2 );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, (int) ( FluidPressureControlPanel.CONTROL_FONT.getSize() * 1.3 ), FluidPressureControlPanel.FOREGROUND, FluidPressureControlPanel.BACKGROUND ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() - 2 );
        }} );

        addChild( new FluidDensityControl<FluidFlowModel>( module ) {{
            setOffset( 0, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );

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
                    setValue( true );
                }

                public void deactivated() {
                    setValue( false );
                }
            } );
        }};

        Property<Boolean> clockRunning = new Property<Boolean>( true );
        //wire up the clock to be running if the module is active and if the clock control button has been pressed
        new And( clockRunning, moduleActive ) {{
            addObserver( new SimpleObserver() {
                public void update() {
                    model.getClock().setRunning( getValue() );
                }
            } );
        }};
        //No time readout
        addChild( new FloatingClockControlNode( clockRunning, null, module.getClock(), FPAFStrings.RESET, new Property<Color>( Color.white ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }} );
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