// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.SlowMotionNormalTimeControlPanel;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensorNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.ComponentTypes;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.colorado.phet.fluidpressureandflow.watertower.model.FPAFVelocitySensor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.ParameterKeys.velocityX;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.ParameterKeys.velocityY;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents.normalSpeedRadioButton;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents.slowMotionRadioButton;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;
import static edu.umd.cs.piccolo.PNode.PROPERTY_FULL_BOUNDS;

/**
 * Canvas used in all tabs of "Fluid Pressure and Flow"
 *
 * @author Sam Reid
 */
public class FluidPressureAndFlowCanvas<T extends FluidPressureAndFlowModel> extends PhetPCanvas {

    //Standard inset used in many layouts
    public static final double INSET = 5;

    //Transform from model to view
    protected final ModelViewTransform transform;

    //Stage where nodes are added and scaled up and down
    private final PNode rootNode;

    //Size for the stage, should have the right aspect ratio since it will always be visible
    //The dimension was determined by running on Windows and inspecting the dimension of the canvas after menubar and tabs are added
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );

    //Font to use for the majority of controls in this sim
    public static final Font CONTROL_FONT = new PhetFont( 15, false );

    //Compute the bounds in the model (meters) that are visible in this canvas, for purposes of constraining draggable sensors to remain onscreen
    public final Function0<ImmutableRectangle2D> visibleModelBounds = new Function0<ImmutableRectangle2D>() {
        public ImmutableRectangle2D apply() {
            //identify the bounds that objects will be constrained to be dragged within
            int insetPixels = 10;
            final Rectangle2D.Double viewBounds = new Rectangle2D.Double( insetPixels, insetPixels, getWidth() - insetPixels * 2, getHeight() - insetPixels * 2 );

            //Convert to model bounds and return
            return new ImmutableRectangle2D( transform.viewToModel( rootNode.globalToLocal( viewBounds ) ) );
        }
    };

    public FluidPressureAndFlowCanvas( final ModelViewTransform transform ) {
        this.transform = transform;
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );
    }

    //Add the floating clock controls and sim speed slider at the bottom of the screen
    protected void addClockControls( final FluidPressureAndFlowModule<?> module ) {

        //Make sure the clock is only running when the associated module is active
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

        //wire up the clock to be running if the module is active and if the clock control button has been pressed
        module.model.clockRunning.and( moduleActive ).addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean run ) {
                module.model.getClock().setRunning( run );
            }
        } );

        //Add clock controls (play/pause), including a time speed slider (no time readout)
        addChild( createClockControls( module ) );
    }

    //Create clock controls (play/pause), including a time speed slider (no time readout)
    protected PNode createClockControls( final FluidPressureAndFlowModule<?> module ) {
        final double SIMULATION_TIME_DT = module.getConstantDtClock().getDt();
        final Property<Boolean> normalSpeed = new Property<Boolean>( true );
        SlowMotionNormalTimeControlPanel controlPanel = new SlowMotionNormalTimeControlPanel( slowMotionRadioButton, SLOW_MOTION, NORMAL, normalSpeedRadioButton, normalSpeed, module.getClock() ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};

        normalSpeed.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean normalSpeed ) {
                module.getConstantDtClock().setDt( normalSpeed ? SIMULATION_TIME_DT : SIMULATION_TIME_DT / 4.0 );
            }
        } );

        return controlPanel;
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    protected void synchronizeRulerLocations( final MeterStick meterStick, final EnglishRuler englishRuler ) {
        //Make sure they remain at the same location so that toggling units won't move the ruler
        englishRuler.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                meterStick.setOffset( englishRuler.getOffset() );
            }
        } );
        meterStick.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                englishRuler.setOffset( meterStick.getOffset() );
            }
        } );
    }

    //Add the velocity sensor node, and constrain to remain on the screen
    protected void addVelocitySensorNodes( final FluidPressureAndFlowModel model, final EmptyNode velocitySensorNodeArea, final FluidPressureAndFlowControlPanelNode sensorToolBoxNode ) {
        for ( final FPAFVelocitySensor velocitySensor : model.getVelocitySensors() ) {

            //Move so it has the right physical location so it will look like it is in the toolbox
            //Do so initially and on resets
            final VoidFunction0 setPosition = new VoidFunction0() {
                public void apply() {
                    velocitySensor.position.set( getModelLocationForVelocitySensor( velocitySensorNodeArea ) );
                }
            };
            model.addResetListener( setPosition );
            setPosition.apply();

            addChild( new VelocitySensorNode( transform, velocitySensor, 1, getVelocityFormatter( model ), new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D point2D ) {
                    return visibleModelBounds.apply().getClosestPoint( point2D );
                }
            }, UNKNOWN_VELOCITY ) {
                {

                    //Make sure the node moves to front when dragged, and that it snaps back to the control panel when dropped
                    addInputEventListener( new MoveToFront( this ) );
                    addInputEventListener( new SnapToToolbox( sensorToolBoxNode, velocitySensor.position,
                                                              getModelLocationForVelocitySensor( velocitySensorNodeArea ), new Function0<PBounds>() {
                        public PBounds apply() {
                            return getBodyNode().getGlobalFullBounds();
                        }
                    } ) );
                }

                @Override protected void sendMessage( final Point2D modelPoint ) {
                    super.sendMessage( modelPoint );
                    final Option<ImmutableVector2D> velocity = velocitySensor.context.getVelocity( modelPoint.getX(), modelPoint.getY() );
                    SimSharingManager.sendUserMessage( velocitySensor.component, ComponentTypes.velocitySensor, UserActions.drag,
                                                       parameterSet( ParameterKeys.x, modelPoint.getX() ).
                                                               with( ParameterKeys.y, modelPoint.getY() ).
                                                               with( velocityX, velocity.isSome() ? velocity.get().getX() : Double.NaN ).
                                                               with( velocityY, velocity.isSome() ? velocity.get().getY() : Double.NaN )
                    );
                }
            } );
        }
    }

    public Property<Function1<ImmutableVector2D, String>> getVelocityFormatter( final FluidPressureAndFlowModel model ) {
        return new Property<Function1<ImmutableVector2D, String>>( createVelocityFormatter( model ) ) {{
            model.units.addObserver( new SimpleObserver() {
                public void update() {
                    set( createVelocityFormatter( model ) );
                }
            } );
        }};
    }

    private Function1<ImmutableVector2D, String> createVelocityFormatter( final FluidPressureAndFlowModel model ) {
        return new Function1<ImmutableVector2D, String>() {
            public String apply( ImmutableVector2D aDouble ) {
                final Unit unit = model.units.get().velocity;
                return MessageFormat.format( VALUE_WITH_UNITS_PATTERN, unit.getDecimalFormat().format( unit.siToUnit( aDouble.getMagnitude() ) ), unit.getAbbreviation() );
            }
        };
    }

    //Adds the fluid density control in the bottom right of the play area
    protected void addFluidDensityControl( FluidPressureAndFlowModule<T> module ) {
        final FluidDensityControl<T> fluidDensityControl = new FluidDensityControl<T>( module );
        addChild( new FluidPressureAndFlowControlPanelNode( fluidDensityControl ) {{
            setOffset( STAGE_SIZE.getWidth() - fluidDensityControl.getMaximumSize().getWidth() - INSET * 2, STAGE_SIZE.getHeight() - fluidDensityControl.getMaximumSize().getHeight() - INSET * 2 );
        }} );
    }

    //Create and add a toolbox that contains the sensors.
    //This is done by creating dummy nodes based on actual model sensors to get the dimensions for the toolbox panel right.
    //This is necessary since we want the pressure sensor to have the right readout within the control panel.
    //This also has the advantages of sizing the control panel so it will fit internationalized versions of the components
    public void addSensorToolboxNode( final FluidPressureAndFlowModel model, final FluidPressureAndFlowControlPanelNode controlPanelNode, Property<IPool> poolProperty ) {
        final EmptyNode velocitySensorArea = new EmptyNode( model.getVelocitySensors().length > 0 ? new VelocitySensorNode( transform, model.getVelocitySensors()[0], 1, getVelocityFormatter( model ) ) : new PNode() );
        final EmptyNode pressureSensorArea = new EmptyNode( new PressureSensorNode( transform, model.getPressureSensors()[0], new Property<UnitSet>( UnitSet.METRIC ), poolProperty, visibleModelBounds ) );
        final FluidPressureAndFlowControlPanelNode sensorToolBoxNode = new FluidPressureAndFlowControlPanelNode( new HBox( velocitySensorArea, pressureSensorArea ) ) {{

            //Position it to the left of the control panel, but leave extra space in case the pressure sensor node needs to jut off to the right;
            //this could happen if the units translation in Metric is significantly longer than the units translation for English (which is used to set the default size)
            setOffset( controlPanelNode.getFullBounds().getX() - getFullBounds().getWidth() - INSET * 3, controlPanelNode.getFullBounds().getY() );
        }};
        addChild( sensorToolBoxNode );

        //Add the velocity sensor nodes
        addVelocitySensorNodes( model, velocitySensorArea, sensorToolBoxNode );

        //Add the draggable sensors in front of the control panels so they can't get lost behind the control panel
        for ( final PressureSensor pressureSensor : model.getPressureSensors() ) {

            //Move so it has the right physical location so it will look like it is in the toolbox
            //Do so initially and on resets
            final VoidFunction0 updatePosition = new VoidFunction0() {
                public void apply() {
                    pressureSensor.location.set( getModelLocationForPressureSensor( pressureSensorArea ) );
                }
            };
            model.addResetListener( updatePosition );
            updatePosition.apply();

            addChild( new PressureSensorNode( transform, pressureSensor, model.units, poolProperty, visibleModelBounds ) {{

                //Make sure the node moves to front when dragged, and that it snaps back to the control panel when dropped
                addInputEventListener( new MoveToFront( this ) );
                addInputEventListener( new SnapToToolbox( sensorToolBoxNode, pressureSensor.location, getModelLocationForPressureSensor( pressureSensorArea ), new Function0<PBounds>() {
                    public PBounds apply() {
                        return getGlobalFullBounds();
                    }
                } ) );
            }} );
        }
    }

    //Determine where to place the velocity sensor in the model (meters) so it will show up in the right place in the toolbox
    private ImmutableVector2D getModelLocationForVelocitySensor( EmptyNode velocitySensorNodeArea ) {
        Point2D globalCenter = new Point2D.Double( velocitySensorNodeArea.getGlobalFullBounds().getCenter2D().getX(), velocitySensorNodeArea.getGlobalFullBounds().getMaxY() );
        Point2D sceneCenter = rootNode.globalToLocal( globalCenter );
        return transform.viewToModel( new ImmutableVector2D( sceneCenter ) );
    }

    //Determine where to place the pressure sensor in the model (meters) so it will show up in the right place in the toolbox
    private ImmutableVector2D getModelLocationForPressureSensor( EmptyNode pressureSensorArea ) {
        Point2D globalCenter = new Point2D.Double( pressureSensorArea.getGlobalFullBounds().getCenterX(), pressureSensorArea.getGlobalFullBounds().getMaxY() );
        Point2D sceneCenter = rootNode.globalToLocal( globalCenter );
        return transform.viewToModel( new ImmutableVector2D( sceneCenter ) );
    }

    public PNode getRootNode() {
        return rootNode;
    }
}