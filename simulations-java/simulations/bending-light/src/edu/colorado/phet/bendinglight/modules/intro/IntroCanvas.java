// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.view.*;
import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.*;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;

/**
 * @author Sam Reid
 */
public class IntroCanvas<T extends IntroModel> extends BendingLightCanvas<T> {
    public final ToolboxNode toolboxNode;

    public IntroCanvas( final T model,
                        final BooleanProperty moduleActive,
                        final Resettable resetAll,
                        final Function3<IntroModel, Double, Double, PNode> additionalLaserControls,
                        final double centerOffsetLeft,
                        final ObservableProperty<Boolean> clockControlVisible,
                        final ResetModel resetModel,
                        final String format,//decimal format pattern to use in the medium control panel
                        final int columns ) {//number of columns to show in the MediumControlPanel readout
        super( model, moduleActive, new Function1<Double, Double>() {
            public Double apply( Double angle ) {
                if ( angle < -Math.PI / 2 ) { angle = Math.PI; }
                if ( angle < Math.PI / 2 && angle > 0 ) { angle = Math.PI / 2; }
                return angle;
            }
        }, new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble < Math.PI;
            }
        }, new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble > Math.PI / 2;
            }
        }, true,
               new Function2<Shape, Shape, Shape>() {
                   public Shape apply( Shape full, Shape front ) {
                       return new Rectangle2D.Double( 0, 0, 0, 0 );//no region can be translated
                   }
               }, new Function2<Shape, Shape, Shape>() {
                    public Shape apply( Shape full, Shape back ) {
                        return full; //rotation if the user clicks anywhere on the object.
                    }
                }, "laser.png", centerOffsetLeft );
        mediumNode.addChild( new MediumNode( transform, model.topMedium ) );
        mediumNode.addChild( new MediumNode( transform, model.bottomMedium ) );

        afterLightLayer.addChild( new ControlPanelNode( new MediumControlPanel( this, model.topMedium, "Material:", true, model.wavelengthProperty, format, columns ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) - 10 - getFullBounds().getHeight() );
        }} );
        afterLightLayer.addChild( new ControlPanelNode( new MediumControlPanel( this, model.bottomMedium, "Material:", true, model.wavelengthProperty, format, columns ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) + 10 );
        }} );

        //add a line that will show the border between the mediums even when both n's are the same... Just a thin line will be fine.
        beforeLightLayer.addChild( new PhetPPath( transform.modelToView( new Line2D.Double( -1, 0, 1, 0 ) ), new BasicStroke( 0.5f ), Color.gray ) {{
            setPickable( false );
        }} );

        afterLightLayer.addChild( new NormalLine( transform, model.getHeight() ) {{
            showNormal.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean value ) {
                    setVisible( value );
                }
            } );
        }} );

        afterLightLayer.addChild( new ControlPanelNode( new PNode() {{
            final PText title = new PText( "Laser View" ) {{setFont( labelFont );}};
            addChild( title );
            final PSwing radioButtonPanel = new PSwing( new VerticalLayoutPanel() {{
                add( new PropertyRadioButton<LaserView>( "Ray", model.laserView, LaserView.RAY ) {{setFont( labelFont );}} );
                add( new PropertyRadioButton<LaserView>( "Wave", model.laserView, LaserView.WAVE ) {{setFont( labelFont );}} );
            }} ) {{
                setOffset( 0, title.getFullBounds().getMaxY() );
            }};
            addChild( radioButtonPanel );
            final PNode additionalControl = additionalLaserControls.apply( model, 30.0, radioButtonPanel.getFullBounds().getMaxY() + 5 );
            addChild( additionalControl );
        }} ) {{
            setOffset( 5, 5 );
        }} );

        final Tool protractor = new Tool( multiScaleToWidth( RESOURCES.getImage( "protractor.png" ), ToolboxNode.ICON_WIDTH ), showProtractor,
                                          transform, this, new Tool.NodeFactory() {
                    public ProtractorNode createNode( ModelViewTransform transform, Property<Boolean> showTool, Point2D model ) {
                        return new ProtractorNode( transform, showTool, new ProtractorModel( model.getX(), model.getY() ), new Function2<Shape, Shape, Shape>() {
                            public Shape apply( Shape innerBar, final Shape outerCircle ) {
                                return new Area( innerBar ) {{add( new Area( outerCircle ) );}};
                            }
                        }, new Function2<Shape, Shape, Shape>() {
                            public Shape apply( Shape innerBar, Shape outerCircle ) {
                                return new Rectangle2D.Double( 0, 0, 0, 0 );//empty shape since shouldn't be rotatable in this tab
                            }
                        }, 1 );
                    }
                }, model, new Function0<Rectangle2D>() {
                    public Rectangle2D apply() {
                        return toolboxNode.getGlobalFullBounds();
                    }
                } );
        toolboxNode = new ToolboxNode( this, transform, protractor, getMoreTools( model ), model.getIntensityMeter(), showNormal );
        final ControlPanelNode toolbox = new ControlPanelNode( toolboxNode ) {{
            setOffset( 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        beforeLightLayer.addChild( toolbox );

        afterLightLayer.addChild( new BendingLightResetAllButtonNode( resetAll, this ) {{
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - 10, stageSize.getHeight() - getFullBounds().getHeight() - 10 );
        }} );

        afterLightLayer.addChild( new FloatingClockControlNode( clockRunningPressed, null, model.getClock(), "Reset", new Property<Color>( Color.white ) ) {{
            clockControlVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
            final double dt = model.getClock().getDt();
            final Property<Double> simSpeedProperty = new Property<Double>( dt ) {{
                addObserver( new SimpleObserver() {
                    public void update() {
                        model.getClock().setDt( getValue() );
                    }
                } );
                resetModel.addResetListener( new VoidFunction0() {
                    public void apply() {
                        reset();
                    }
                } );
            }};

            final SimSpeedSlider speedSlider = new SimSpeedSlider( dt / 4, simSpeedProperty, dt, 0, new Property<Color>( Color.black ) );
            addChild( speedSlider );

            //sim speed slider is not at (0,0) in this node, so need to account for its size
            setOffset( toolbox.getFullBounds().getMaxX() + speedSlider.getFullBounds().getWidth() + 10, stageSize.getHeight() - getFullBounds().getHeight() );
        }} );
    }

    protected PNode[] getMoreTools( ResetModel resetModel ) {
        return new PNode[0];
    }
}