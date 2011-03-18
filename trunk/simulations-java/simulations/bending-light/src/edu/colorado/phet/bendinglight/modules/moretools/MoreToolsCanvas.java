// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.model.VelocitySensor;
import edu.colorado.phet.bendinglight.modules.intro.*;
import edu.colorado.phet.bendinglight.view.BendingLightWavelengthControl;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MoreToolsCanvas extends IntroCanvas<MoreToolsModel> {
    public WaveSensorNode myWaveSensorNode;

    public MoreToolsCanvas( MoreToolsModel model, BooleanProperty moduleActive, Resettable resetAll ) {
        super( model, moduleActive, resetAll, new Function3<IntroModel, Double, Double, PNode>() {
            public PNode apply( IntroModel introModel, final Double x, final Double y ) {
                return new BendingLightWavelengthControl( introModel.wavelengthProperty, introModel.getLaser().color ) {{
                    setOffset( x, y );
                }};
            }
        }, 0, new Or( new ValueEquals<LaserView>( model.laserView, LaserView.WAVE ), model.waveSensor.visible ) );
    }

    @Override protected PNode[] getMoreTools( ResetModel resetModel ) {

        final VelocitySensorNode velocitySensorNode = new VelocitySensorNode( transform, new VelocitySensor() );
        final Property<Boolean> showVelocitySensor = new Property<Boolean>( false );
        resetModel.addResetListener( new VoidFunction0() {
            public void apply() {
                showVelocitySensor.reset();
            }
        } );
        Function1<Rectangle2D[], Boolean> container = new Function1<Rectangle2D[], Boolean>() {
            public Boolean apply( Rectangle2D[] bounds ) {
                //TODO: refactor this into Tool, and pass in container lazily (VoidFunction1)
                boolean t = false;
                for ( Rectangle2D bound : bounds ) {
                    if ( toolboxNode.getGlobalFullBounds().contains( bound ) ) {
                        t = true;
                    }
                }
                return t;
            }
        };
        final Tool velocityTool = new Tool( velocitySensorNode.toImage( ToolboxNode.ICON_WIDTH, (int) ( velocitySensorNode.getFullBounds().getHeight() / velocitySensorNode.getFullBounds().getWidth() * ToolboxNode.ICON_WIDTH ), new Color( 0, 0, 0, 0 ) ),
                                            showVelocitySensor,
                                            transform, this, new Tool.NodeFactory() {
                    public VelocitySensorNode createNode( ModelViewTransform transform, final Property<Boolean> showTool, final Point2D modelPt ) {
                        model.velocitySensor.position.setValue( new ImmutableVector2D( modelPt ) );
                        return new VelocitySensorNode( transform, model.velocitySensor ) {{
                            showTool.addObserver( new VoidFunction1<Boolean>() {
                                public void apply( Boolean visible ) {
                                    setVisible( visible );
                                }
                            } );
                        }};
                    }
                }, resetModel, new Function0<Rectangle2D>() {
                    public Rectangle2D apply() {
                        return toolboxNode.getGlobalFullBounds();
                    }
                } );


        final Function1.Constant<ImmutableVector2D, Option<Double>> value = new Function1.Constant<ImmutableVector2D, Option<Double>>( new Option.None<Double>() );
        final WaveSensorNode waveSensorNode = new WaveSensorNode( transform, new WaveSensor( new ConstantDtClock(), value, value ) );
        resetModel.addResetListener( new VoidFunction0() {
            public void apply() {
                model.waveSensor.visible.reset();
            }
        } );
        final Tool waveTool = new Tool( waveSensorNode.toImage( ToolboxNode.ICON_WIDTH, (int) ( waveSensorNode.getFullBounds().getHeight() / waveSensorNode.getFullBounds().getWidth() * ToolboxNode.ICON_WIDTH ), new Color( 0, 0, 0, 0 ) ),
                                        model.waveSensor.visible,
                                        transform, this, new Tool.NodeFactory() {
                    public WaveSensorNode createNode( ModelViewTransform transform, final Property<Boolean> showTool, final Point2D modelPt ) {
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
                }, resetModel, new Function0<Rectangle2D>() {
                    public Rectangle2D apply() {
                        return toolboxNode.getGlobalFullBounds();
                    }
                } );

        return new PNode[] { velocityTool, waveTool };
    }
}