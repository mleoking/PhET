// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.SimSpeedControlPNode;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensor;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensorNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.RESET;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowCanvas<T extends FluidPressureAndFlowModel> extends PhetPCanvas {

    public static double INSET = 10;
    protected final ModelViewTransform transform;
    private final PNode rootNode;

    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    public static final Font CONTROL_FONT = new PhetFont( 15, false );//Font to use for the majority of controls in this sim

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

    public static void makeTransparent( JComponent component ) {
        if ( !( component instanceof JTextComponent ) ) {
            component.setBackground( new Color( 0, 0, 0, 0 ) );
            component.setOpaque( false );
        }
        for ( Component component1 : component.getComponents() ) {
            if ( component1 instanceof JComponent ) {
                makeTransparent( (JComponent) component1 );
            }
        }
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
        addChild( createClockControls( module, module.model.clockRunning ) );
    }

    //Create clock controls (play/pause), including a time speed slider (no time readout)
    protected HBox createClockControls( final FluidPressureAndFlowModule<?> module, final Property<Boolean> clockRunning ) {
        final Property<Double> dt = module.model.simulationTimeStep;
        return new HBox( 10,
                         //Set the time speed slider to go between 1/2 and 2x the default dt
                         new SimSpeedControlPNode( dt.get() / 2, dt, dt.get() * 2, 0.0, new Property<Color>( Color.black ) ),
                         new FloatingClockControlNode( clockRunning, null, module.getClock(), RESET, new Property<Color>( Color.white ) ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    protected void synchronizeRulerLocations( final MeterStick meterStick, final EnglishRuler englishRuler ) {
        //Make sure they remain at the same location so that toggling units won't move the ruler
        englishRuler.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                meterStick.setOffset( englishRuler.getOffset() );
            }
        } );
        meterStick.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                englishRuler.setOffset( meterStick.getOffset() );
            }
        } );
    }

    //Add the velocity sensor node, and constrain to remain on the screen
    protected void addVelocitySensorNodes( final FluidPressureAndFlowModel model, EmptyNode velocitySensorNodeArea ) {
        for ( VelocitySensor velocitySensor : model.getVelocitySensors() ) {

            //Move so it has the right physical location so it will look like it is in the toolbox
            velocitySensor.position.set( getModelLocationForVelocitySensor( velocitySensorNodeArea ) );
            addChild( new VelocitySensorNode( transform, velocitySensor, 1, getVelocityFormatter( model ), new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D point2D ) {
                    return visibleModelBounds.apply().getClosestPoint( point2D );
                }
            } ) {{
                addInputEventListener( new MoveToFront( this ) );
            }} );
        }
    }

    public Property<Function1<Double, String>> getVelocityFormatter( final FluidPressureAndFlowModel model ) {
        return new Property<Function1<Double, String>>( createFormatter( model ) ) {{
            model.units.addObserver( new SimpleObserver() {
                public void update() {
                    set( createFormatter( model ) );
                }
            } );
        }};
    }

    private Function1<Double, String> createFormatter( final FluidPressureAndFlowModel model ) {
        return new Function1<Double, String>() {
            public String apply( Double aDouble ) {
                final Unit unit = model.units.get().velocity;
                return unit.getDecimalFormat().format( unit.siToUnit( aDouble ) ) + " " + unit.getAbbreviation();//TODO: correct units (from SI) and i18n
            }
        };
    }

    //Adds the fluid density control in the bottom left of the play area
    protected void addFluidDensityControl( FluidPressureAndFlowModule<T> module ) {
        final FluidDensityControl<T> fluidDensityControl = new FluidDensityControl<T>( module );
        addChild( new FluidPressureAndFlowControlPanelNode( fluidDensityControl ) {{
            final int inset = 10;
            setOffset( inset, STAGE_SIZE.getHeight() - fluidDensityControl.getMaximumHeight() - inset );
        }} );
    }

    //Create and add a toolbox that contains the sensors.
    //This is done by creating dummy nodes based on actual model sensors to get the dimensions for the toolbox panel right.
    //This is necessary since we want the pressure sensor to have the right readout within the control panel.
    //This also has the advantages of sizing the control panel so it will fit internationalized versions of the components
    public void addSensorToolboxNode( final FluidPressureAndFlowModel model, final FluidPressureAndFlowControlPanelNode controlPanelNode ) {
        final EmptyNode velocitySensorArea = new EmptyNode( new VelocitySensorNode( transform, model.getVelocitySensors()[0], 1, getVelocityFormatter( model ) ) );
        final EmptyNode pressureSensorArea = new EmptyNode( new PressureSensorNode( transform, model.getPressureSensors()[0], model.units, visibleModelBounds ) );
        addChild( new SensorToolBoxNode( new HBox( velocitySensorArea, pressureSensorArea ) ) {{
            setOffset( controlPanelNode.getFullBounds().getX() - getFullBounds().getWidth() - INSET, controlPanelNode.getFullBounds().getY() );
        }} );

        //Add the velocity sensor nodes
        addVelocitySensorNodes( model, velocitySensorArea );

        //Add the draggable sensors in front of the control panels so they can't get lost behind the control panel
        for ( PressureSensor pressureSensor : model.getPressureSensors() ) {

            //Move so it has the right physical location so it will look like it is in the toolbox
            pressureSensor.location.set( getModelLocationForPressureSensor( pressureSensorArea ) );

            addChild( new PressureSensorNode( transform, pressureSensor, model.units, visibleModelBounds ) {{
                addInputEventListener( new MoveToFront( this ) );
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
        Point2D globalCenter = new Point2D.Double( pressureSensorArea.getGlobalFullBounds().getX(), pressureSensorArea.getGlobalFullBounds().getCenterY() );
        Point2D sceneCenter = rootNode.globalToLocal( globalCenter );
        return transform.viewToModel( new ImmutableVector2D( sceneCenter ) );
    }
}