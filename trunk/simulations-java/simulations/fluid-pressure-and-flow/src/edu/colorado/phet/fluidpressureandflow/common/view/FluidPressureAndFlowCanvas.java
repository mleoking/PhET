// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.bendinglight.model.ImmutableRectangle2D;
import edu.colorado.phet.bendinglight.model.ModelBounds;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.SimSpeedControlPNode;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensor;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensorNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.fluidpressureandflow.common.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowCanvas<T extends FluidPressureAndFlowModel> extends PhetPCanvas {
    protected final ModelViewTransform transform;
    private final PNode rootNode;

    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    public static final Font CONTROL_FONT = new PhetFont( 15, false );//Font to use for the majority of controls in this sim

    public FluidPressureAndFlowCanvas( final ModelViewTransform transform, final ModelBounds bounds ) {
        this.transform = transform;
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );

        //Notify model elements about the canvas area so they can't be dragged outside it.
        final VoidFunction0 updateDragBounds = new VoidFunction0() {
            public void apply() {
                //identify the bounds that objects will be constrained to be dragged within
                int insetPixels = 10;
                final Rectangle2D.Double viewBounds = new Rectangle2D.Double( insetPixels, insetPixels, getWidth() - insetPixels * 2, getHeight() - insetPixels * 2 );

                //Convert to model bounds and store in the model
                final ImmutableRectangle2D modelBounds = new ImmutableRectangle2D( transform.viewToModel( rootNode.globalToLocal( viewBounds ) ) );
                bounds.setValue( new Option.Some<ImmutableRectangle2D>( modelBounds ) );
            }
        };
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
        final Property<Double> dt = module.getFluidPressureAndFlowModel().simulationTimeStep;
        return new HBox( 10,
                         //Set the time speed slider to go between 1/2 and 2x the default dt
                         new SimSpeedControlPNode( dt.getValue() / 2, dt, dt.getValue() * 2, 0.0, new Property<Color>( Color.black ) ),
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
            model.units.addObserver( new SimpleObserver() {
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
                final Unit unit = model.units.getValue().velocity;
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
}