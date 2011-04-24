// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.MinimizeMaximizeNode;
import edu.colorado.phet.common.piccolophet.nodes.SimSpeedControlPNode;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensor;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensorNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.Units;
import edu.colorado.phet.fluidpressureandflow.fluidpressure.FluidPressureControlPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowCanvas<T extends FluidPressureAndFlowModel> extends PhetPCanvas {
    protected final ModelViewTransform transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private final PNode rootNode;

    public FluidPressureAndFlowCanvas( ModelViewTransform transform ) {
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

    //Create clock controls (play/pause), including a time speed slider (no time readout)
    protected HBox createClockControls( final FluidPressureAndFlowModule<T> module, final Property<Boolean> clockRunning ) {
        return new HBox( 10,
                         new SimSpeedControlPNode( 0.1, new Property<Double>( 1.0 ), 2.0, 0.0, new Property<Color>( Color.black ) ),
                         new FloatingClockControlNode( clockRunning, null, module.getClock(), FPAFStrings.RESET, new Property<Color>( Color.white ) ) ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    public static class FluidDensityControl<T extends FluidPressureAndFlowModel> extends PNode {
        public FluidDensityControl( final FluidPressureAndFlowModule<T> module ) {
            final SliderControl fluidDensityControl = new SliderControl( FPAFStrings.FLUID_DENSITY, FPAFStrings.KG_PER_M_3, FluidPressureAndFlowModel.GASOLINE_DENSITY, FluidPressureAndFlowModel.HONEY_DENSITY, module.getFluidPressureAndFlowModel().liquidDensity, new HashMap<Double, TickLabel>() {{
                put( FluidPressureAndFlowModel.GASOLINE_DENSITY, new TickLabel( FPAFStrings.GASOLINE ) );
                put( FluidPressureAndFlowModel.WATER_DENSITY, new TickLabel( FPAFStrings.WATER ) );
                put( FluidPressureAndFlowModel.HONEY_DENSITY, new TickLabel( FPAFStrings.HONEY ) );
            }} ) {{
                module.fluidDensityControlVisible.addObserver( new SimpleObserver() {
                    public void update() {
                        setVisible( module.fluidDensityControlVisible.getValue() );
                    }
                } );
            }};
            MinimizeMaximizeNode minimizeMaximizeNode = new MinimizeMaximizeNode( FPAFStrings.FLUID_DENSITY, MinimizeMaximizeNode.BUTTON_LEFT, FluidPressureControlPanel.CONTROL_FONT, Color.black, 10 ) {{
                addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        module.fluidDensityControlVisible.setValue( isMaximized() );
                    }
                } );
                module.fluidDensityControlVisible.addObserver( new SimpleObserver() {
                    public void update() {
                        setMaximized( module.fluidDensityControlVisible.getValue() );
                    }
                } );
                translate( 0, -getFullBounds().getHeight() );

                //The default green + button is invisible against the green ground, so use a blue one instead
                setMaximizeImage( FluidPressureAndFlowApplication.RESOURCES.getImage( "maximizeButtonBlue.png" ) );
            }};

            addChild( fluidDensityControl );
            addChild( minimizeMaximizeNode );
        }
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

    protected void addVelocitySensorNodes( final FluidPressureAndFlowModel model ) {
        final Property<Function1<Double, String>> formatter = new Property<Function1<Double, String>>( createFormatter( model ) ) {{
            model.velocityUnit.addObserver( new SimpleObserver() {
                public void update() {
                    setValue( createFormatter( model ) );
                }
            } );
        }};
        for ( VelocitySensor velocitySensor : model.getVelocitySensors() ) {
            addChild( new VelocitySensorNode( transform, velocitySensor, 1, formatter ) );
        }
    }

    private Function1<Double, String> createFormatter( final FluidPressureAndFlowModel model ) {
        return new Function1<Double, String>() {
            public String apply( Double aDouble ) {
                final Units.Unit unit = model.velocityUnit.getValue();
                return unit.getDecimalFormat().format( unit.siToUnit( aDouble ) ) + " " + unit.getAbbreviation();//TODO: correct units (from SI) and i18n
            }
        };
    }
}