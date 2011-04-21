// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.model.ProtractorModel;
import edu.colorado.phet.bendinglight.modules.intro.IntroCanvas;
import edu.colorado.phet.bendinglight.modules.intro.IntroModel;
import edu.colorado.phet.bendinglight.modules.intro.Tool;
import edu.colorado.phet.bendinglight.view.BendingLightWavelengthControl;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.bendinglight.view.ProtractorNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Or;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.*;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensor;
import edu.colorado.phet.common.piccolophet.nodes.VelocitySensorNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.bendinglight.modules.intro.ToolboxNode.ICON_WIDTH;

/**
 * Canvas for the "more tools" tab, which adds more tools to the toolbox, and a few more controls for the laser.
 *
 * @author Sam Reid
 */
public class MoreToolsCanvas extends IntroCanvas<MoreToolsModel> {
    public WaveSensorNode myWaveSensorNode;
    public double arrowScale = 1.5E-14;

    public MoreToolsCanvas( MoreToolsModel model, BooleanProperty moduleActive, Resettable resetAll ) {
        super( model, moduleActive, resetAll, new Function3<IntroModel, Double, Double, PNode>() {
            public PNode apply( IntroModel introModel, final Double x, final Double y ) {
                //Create the new wavelength control which is an additional laser control in the more tools tab
                return new BendingLightWavelengthControl( introModel.wavelengthProperty, introModel.getLaser().color ) {{
                    setOffset( x, y );
                }};
            }
        }, 0,
               //Only show the clock control if the laser view is WAVE or if the wave sensor is showing
               new Or( new ValueEquals<LaserView>( model.laserView, LaserView.WAVE ), model.waveSensor.visible ),
               model, "0.000", 6 );
    }

    //Provide the additional tools for this tab
    @Override protected PNode[] getMoreTools( ResetModel resetModel ) {
        //Create the Velocity Sensor tool and wave sensor tool to add to the toolbox
        return new PNode[] { createVelocitySensorTool( resetModel ), createWaveSensorTool( resetModel ) };
    }

    private Tool createWaveSensorTool( ResetModel resetModel ) {
        //Create a model for depicting with the WaveSensorNode
        final Function1.Constant<ImmutableVector2D, Option<Double>> value = new Function1.Constant<ImmutableVector2D, Option<Double>>( new Option.None<Double>() );//Dummy function that always returns None
        final WaveSensor waveSensor = new WaveSensor( new ConstantDtClock(), value, value );

        //Create the WaveSensorNode
        final WaveSensorNode waveSensorNode = new WaveSensorNode( transform, waveSensor );
        resetModel.addResetListener( new VoidFunction0() {
            public void apply() {
                model.waveSensor.visible.reset();
            }
        } );
        final int waveToolHeight = (int) ( waveSensorNode.getFullBounds().getHeight() / waveSensorNode.getFullBounds().getWidth() * ICON_WIDTH );

        //Provide a way of generating the WaveSensorNode when dragged out of the toolbox
        final Tool.NodeFactory waveNodeFactory = new Tool.NodeFactory() {
            public WaveSensorNode createNode( ModelViewTransform transform, final Property<Boolean> showTool, final Point2D modelPt ) {
                //Reset wave sensor positions so that they come out in the right relative location after resetting previous instance
                model.waveSensor.probe1.position.reset();
                model.waveSensor.probe2.position.reset();
                model.waveSensor.bodyPosition.reset();

                //Move so the hot spot (center of first probe) is where the mouse is
                model.waveSensor.translateToHotSpot( modelPt );

                //lazily create and reuse because there are performance problems if you create a new one each time
                if ( myWaveSensorNode == null ) {
                    myWaveSensorNode = new WaveSensorNode( transform, model.waveSensor ) {{
                        showTool.addObserver( new SimpleObserver() {
                            public void update() {
                                setVisible( showTool.getValue() );
                            }
                        } );
                    }};
                }
                return myWaveSensorNode;
            }
        };

        //Create the tool itself for dragging out of the toolbox
        return new Tool( waveSensorNode.toImage( ICON_WIDTH, waveToolHeight, new Color( 0, 0, 0, 0 ) ), model.waveSensor.visible, transform, this, waveNodeFactory, resetModel, getToolboxBounds() );
    }

    private Tool createVelocitySensorTool( ResetModel resetModel ) {
        //Create the VelocitySensorNode to depict in the toolbox
        final Function1<Double, String> formatter = new Function1<Double, String>() {
            public String apply( Double magnitude ) {
                final String value = new DecimalFormat( "0.00" ).format( magnitude / 2.99792458E8 );
                return MessageFormat.format( BendingLightStrings.PATTERN_SPEED_OF_LIGHT_READOUT_VALUE_C, value );
            }
        };

        //Make sure the existence in the toolbox resets when the sim resets
        final Property<Boolean> showVelocitySensor = new Property<Boolean>( false );
        resetModel.addResetListener( new VoidFunction0() {
            public void apply() {
                showVelocitySensor.reset();
            }
        } );

        //Create the NodeFactory which creates the VelocitySensorNode when dragged out of the toolbox
        final Tool.NodeFactory velocityNodeFactory = new Tool.NodeFactory() {
            public VelocitySensorNode createNode( final ModelViewTransform transform, final Property<Boolean> showTool, final Point2D modelPt ) {
                model.velocitySensor.position.setValue( new ImmutableVector2D( modelPt ) );
                return new VelocitySensorNode( transform, model.velocitySensor, arrowScale, new Property<Function1<Double, String>>( formatter ), getBoundedConstraint() ) {{
                    showTool.addObserver( new VoidFunction1<Boolean>() {
                        public void apply( Boolean visible ) {
                            setVisible( visible );
                        }
                    } );
                }};
            }
        };

        //Create and return the tool for dragging out of the toolbox
        final VelocitySensorNode thumbnailSensorNode = new VelocitySensorNode( transform, new VelocitySensor(), arrowScale, new Property<Function1<Double, String>>( formatter ) );
        final int velocityToolHeight = (int) ( thumbnailSensorNode.getFullBounds().getHeight() / thumbnailSensorNode.getFullBounds().getWidth() * ICON_WIDTH );
        return new Tool( thumbnailSensorNode.toImage( ICON_WIDTH, velocityToolHeight, new Color( 0, 0, 0, 0 ) ), showVelocitySensor, transform, this, velocityNodeFactory, resetModel, getToolboxBounds() );
    }

    //Gets the bounds in which tools can be dropped back in the toolbox
    private Function0<Rectangle2D> getToolboxBounds() {
        return new Function0<Rectangle2D>() {
            public Rectangle2D apply() {
                return toolboxNode.getGlobalFullBounds();
            }
        };
    }

    //ProtractorNode is expandable in the "more tools" tab
    protected ProtractorNode newProtractorNode( ModelViewTransform transform, Property<Boolean> showTool, Point2D model ) {
        return new ExpandableProtractorNode( transform, showTool, new ProtractorModel( model.getX(), model.getY() ), getProtractorDragRegion(), getProtractorRotationRegion(), ProtractorNode.DEFAULT_SCALE );
    }
}