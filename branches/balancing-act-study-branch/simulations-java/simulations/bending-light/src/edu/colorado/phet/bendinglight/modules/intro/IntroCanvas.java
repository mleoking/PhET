// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.model.ProtractorModel;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.BendingLightResetAllButtonNode;
import edu.colorado.phet.bendinglight.view.LaserView;
import edu.colorado.phet.bendinglight.view.MediumControlPanel;
import edu.colorado.phet.bendinglight.view.MediumNode;
import edu.colorado.phet.bendinglight.view.ProtractorNode;
import edu.colorado.phet.bendinglight.view.ToolboxNode;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.ResetModel;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.Function3;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.SimSpeedControlPNode;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.NodeFactory;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.ToolIconNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static edu.colorado.phet.bendinglight.model.BendingLightModel.MAX_DT;
import static edu.colorado.phet.bendinglight.model.BendingLightModel.MIN_DT;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static java.awt.Color.black;

/**
 * Canvas for the "intro" tab, which is also subclassed for use in the MoreTools tab
 *
 * @author Sam Reid
 */
public class IntroCanvas<T extends IntroModel> extends BendingLightCanvas<T> {
    public final ToolboxNode toolboxNode;

    public IntroCanvas( final T model,
                        final BooleanProperty moduleActive,
                        final Resettable resetAll,
                        final Function3<IntroModel, Double, Double, PNode> additionalLaserControls,//Function to create any additional laser controls necessary to show in the laser control panel.
                        final double centerOffsetLeft,
                        final ObservableProperty<Boolean> clockControlVisible,
                        final ResetModel resetModel,
                        final String indexOfRefractionFormatPattern,//decimal format pattern to use in the medium control panel
                        final int columns ) {//number of columns to show in the MediumControlPanel readout
        super( model,
               moduleActive,
               //Specify how the drag angle should be clamped, in this case the laser must remain in the top left quadrant
               new Function1<Double, Double>() {
                   public Double apply( Double angle ) {
                       while ( angle < 0 ) { angle += Math.PI * 2; }
                       return MathUtil.clamp( Math.PI / 2, angle, Math.PI );
                   }
               },
               //Indicate if the laser is not at its max angle, and therefore can be dragged to larger angles
               new Function1<Double, Boolean>() {
                   public Boolean apply( Double laserAngle ) {
                       return laserAngle < Math.PI;
                   }
               },
               //Indicate if the laser is not at its min angle, and can therefore be dragged to smaller angles.
               new Function1<Double, Boolean>() {
                   public Boolean apply( Double laserAngle ) {
                       return laserAngle > Math.PI / 2;
                   }
               },
               true,
               getProtractorRotationRegion(),
               //rotation if the user clicks anywhere on the object
               new Function2<Shape, Shape, Shape>() {
                   public Shape apply( Shape full, Shape back ) {
                       // In this tab, clicking anywhere on the laser (i.e. on its 'full' bounds) translates it, so always return the 'full' region
                       return full;
                   }
               },
               "laser.png", centerOffsetLeft
        );

        //Add MediumNodes for top and bottom
        mediumNode.addChild( new MediumNode( transform, model.topMedium ) );
        mediumNode.addChild( new MediumNode( transform, model.bottomMedium ) );

        //Add control panels for setting the index of refraction for each medium
        afterLightLayer2.addChild( new ControlPanelNode( new MediumControlPanel( this, model.topMedium, BendingLightStrings.MATERIAL, true, model.wavelengthProperty, indexOfRefractionFormatPattern, columns ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) - 10 - getFullBounds().getHeight() );
        }} );
        afterLightLayer2.addChild( new ControlPanelNode( new MediumControlPanel( this, model.bottomMedium, BendingLightStrings.MATERIAL, true, model.wavelengthProperty, indexOfRefractionFormatPattern, columns ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) + 10 );
        }} );

        //add a line that will show the border between the mediums even when both n's are the same... Just a thin line will be fine.
        beforeLightLayer.addChild( new PhetPPath( transform.modelToView( new Line2D.Double( -1, 0, 1, 0 ) ), new BasicStroke( 0.5f ), Color.gray ) {{
            setPickable( false );
        }} );

        //Show the normal line where the laser strikes the interface between mediums
        afterLightLayer2.addChild( new NormalLine( transform, model.getHeight() ) {{
            showNormal.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean value ) {
                    setVisible( value );
                }
            } );
        }} );

        //Laser control panel
        //First create the content pane which shows the radio buttons and title
        final PNode laserViewContentPane = new PNode() {{
            //Title readout
            final PText title = new PText( BendingLightStrings.LASER_VIEW ) {{setFont( labelFont );}};
            addChild( title );

            //Radio buttons to select "ray" or "wave"
            final PSwing radioButtonPanel = new PSwing( new VerticalLayoutPanel() {{
                add( new PropertyRadioButton<LaserView>( BendingLightStrings.RAY, model.laserView, LaserView.RAY ) {{setFont( labelFont );}} );
                add( new PropertyRadioButton<LaserView>( BendingLightStrings.WAVE, model.laserView, LaserView.WAVE ) {{setFont( labelFont );}} );
            }} ) {{
                setOffset( 0, title.getFullBounds().getMaxY() );
            }};
            addChild( radioButtonPanel );

            //Add any additional controls (used in more tools tab)
            final PNode additionalControl = additionalLaserControls.apply( model, 0d, radioButtonPanel.getFullBounds().getMaxY() + 5 );
            addChild( additionalControl );
        }};

        //Embed in the a control panel node to get a border and background
        final ControlPanelNode laserViewControlPanelNode = new ControlPanelNode( laserViewContentPane );

        //Set the location and add to the scene
        laserViewControlPanelNode.setOffset( 5, 5 );
        afterLightLayer2.addChild( laserViewControlPanelNode );

        //Create a tool for dragging out the protractor
        final NodeFactory protractorNodeFactory = new NodeFactory() {
            public ProtractorNode createNode( ModelViewTransform transform, Property<Boolean> showTool, Point2D model ) {
                return newProtractorNode( transform, showTool, model );
            }
        };
        final ToolIconNode<BendingLightCanvas<T>> protractor = new ToolIconNode<BendingLightCanvas<T>>( multiScaleToWidth( RESOURCES.getImage( "protractor.png" ), ToolboxNode.ICON_WIDTH ), showProtractor,
                                                                                                        transform, this, protractorNodeFactory, model, new Function0<Rectangle2D>() {
            public Rectangle2D apply() {
                return toolboxNode.getGlobalFullBounds();
            }
        } ) {
            //Move the protractor behind the light node so that it also goes behind other controls (such as wavelength controls), since otherwise it obscures them from interaction
            @Override protected void addChild( BendingLightCanvas<T> canvas, ToolNode node ) {
                canvas.addChildAfterLight( node );
            }

            @Override protected void removeChild( BendingLightCanvas<T> canvas, ToolNode node ) {
                canvas.removeChildAfterLight( node );
            }
        };

        //Create the toolbox
        toolboxNode = new ToolboxNode( this, transform, protractor, getMoreTools( model ), model.getIntensityMeter(), showNormal );
        final ControlPanelNode toolbox = new ControlPanelNode( toolboxNode ) {{
            setOffset( 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }};
        beforeLightLayer.addChild( toolbox );

        //Add the reset all button, floating in the play area
        afterLightLayer2.addChild( new BendingLightResetAllButtonNode( resetAll, this, stageSize ) );

        //Add the clock control node, which is only visible when in wave mode or when the user has selected the wave sensor
        afterLightLayer2.addChild( new FloatingClockControlNode( playing, null, model.getClock(), BendingLightStrings.RESET, new Property<Color>( Color.white ) ) {{
            clockControlVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
            final double dt = model.getClock().getDt();
            final Property<Double> simSpeedProperty = new Property<Double>( dt ) {{
                addObserver( new SimpleObserver() {
                    public void update() {
                        model.getClock().setDt( get() );
                    }
                } );
                resetModel.addResetListener( new VoidFunction0() {
                    public void apply() {
                        reset();
                    }
                } );
            }};

            //Add a slider to change the sim speed
            final SimSpeedControlPNode speedSlider = new SimSpeedControlPNode( MIN_DT, simSpeedProperty, MAX_DT, 0, new Property<Color>( black ) );
            addChild( speedSlider );

            //sim speed slider is not at (0,0) in this node, so need to account for its size
            setOffset( toolbox.getFullBounds().getMaxX() + speedSlider.getFullBounds().getWidth() + 10, stageSize.getHeight() - getFullBounds().getHeight() );
        }} );
    }

    //No more tools available in IntroCanvas, but this is overriden in MoreToolsCanvas to provide additional tools
    protected PNode[] getMoreTools( ResetModel resetModel ) {
        return new PNode[0];
    }

    //Create a protractor node, used by the protractor Tool
    protected ProtractorNode newProtractorNode( ModelViewTransform transform, Property<Boolean> showTool, Point2D model ) {
        return new ProtractorNode( transform, showTool, new ProtractorModel( model.getX(), model.getY() ), getProtractorDragRegion(), getProtractorRotationRegion(), ProtractorNode.DEFAULT_SCALE, 1 );
    }

    //Get the function that chooses which region of the protractor can be used for rotation--none in this tab.
    protected static Function2<Shape, Shape, Shape> getProtractorRotationRegion() {
        return new Function2<Shape, Shape, Shape>() {
            public Shape apply( Shape innerBar, Shape outerCircle ) {
                return new Rectangle2D.Double( 0, 0, 0, 0 );//empty shape since shouldn't be rotatable in this tab
            }
        };
    }

    //Get the function that chooses which region of the protractor can be used for translation--both the inner bar and outer circle in this tab
    protected Function2<Shape, Shape, Shape> getProtractorDragRegion() {
        return new Function2<Shape, Shape, Shape>() {
            public Shape apply( Shape innerBar, final Shape outerCircle ) {
                return new Area( innerBar ) {{add( new Area( outerCircle ) );}};
            }
        };
    }
}