// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fluidpressureandflow.FPAFStrings;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.modules.fluidpressure.ButtonExpander;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FluidPressureAndFlowCanvas<T extends FluidPressureAndFlowModel> extends PhetPCanvas {
    protected final ModelViewTransform transform;
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    private final PNode rootNode;
    private final FluidPressureAndFlowModule<T> module;

    public FluidPressureAndFlowCanvas( final FluidPressureAndFlowModule module, ModelViewTransform transform ) {
        this.module = module;
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
            final ButtonExpander fluidDensityExpander = new ButtonExpander( FPAFStrings.FLUID_DENSITY_MORE, FPAFStrings.FLUID_DENSITY_LESS, module.fluidDensityControlVisible ) {{
                setOffset( fluidDensityControl.getFullBounds().getX(), fluidDensityControl.getFullBounds().getY() - getFullBounds().getHeight() );
            }};

            addChild( fluidDensityControl );
            addChild( fluidDensityExpander );
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

//    public static class GravityControl{
    //        final SliderControl gravityControl = new SliderControl( "Gravity", "m/s^2", FluidPressureAndFlowModel.MOON_GRAVITY, FluidPressureAndFlowModel.JUPITER_GRAVITY, module.getFluidPressureAndFlowModel().getGravityProperty(), new HashMap<Double, TickLabel>() {{
//            put( FluidPressureAndFlowModel.EARTH_GRAVITY, new TickLabel( "Earth" ) );
//            put( FluidPressureAndFlowModel.MOON_GRAVITY, new TickLabel( "Moon" ) );
//            put( FluidPressureAndFlowModel.JUPITER_GRAVITY, new TickLabel( "Jupiter" ) );
//        }} ) {{
//            module.getGravityControlVisible().addObserver( new SimpleObserver() {
//                public void update() {
//                    setVisible( module.getGravityControlVisible().getValue() );
//                }
//            } );
//        }};
//
//        gravityControl.setOffset( fluidDensityExpander.getFullBounds().getMinX(), fluidDensityExpander.getFullBounds().getY() - gravityControl.getFullBounds().getHeight() - 20 );
//
//        final ButtonExpander gravityExpander = new ButtonExpander( "Gravity >", "Gravity <", module.getGravityControlVisible() ) {{
//            setOffset( gravityControl.getFullBounds().getX(), gravityControl.getFullBounds().getY() - getFullBounds().getHeight() );
//        }};
//        addChild( gravityExpander );
//        addChild( gravityControl );
//    }

}
