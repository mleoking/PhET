// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.BendingLightStrings;
import edu.colorado.phet.bendinglight.model.ProtractorModel;
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
import edu.colorado.phet.common.piccolophet.nodes.SimSpeedControlPNode;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static edu.colorado.phet.bendinglight.model.BendingLightModel.MAX_DT;
import static edu.colorado.phet.bendinglight.model.BendingLightModel.MIN_DT;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static java.awt.Color.black;

//REVIEW: The name of this class and the header comment are misleading, since
//this class is used in the "More Tools" module as well as the "Intro"
//module.
/**
 * Canvas for the "intro" tab.
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
                       if ( angle < -Math.PI / 2 ) { angle = Math.PI; }
                       if ( angle < Math.PI / 2 && angle > 0 ) { angle = Math.PI / 2; }
                       return angle;
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
               //rotation if the user clicks anywhere on the object.
               new Function2<Shape, Shape, Shape>() {
                   public Shape apply( Shape full, Shape back ) {
                       //REVIEW: I looked at this for a few minutes but don't get it.  What does
                       // this function do and why does this instantiation always return full?
                       return full;
                   }
               },
               "laser.png", centerOffsetLeft );

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
        //REVIEW: The following is difficult to read due to usage of double brace initialization
        //twice inside of one "addChild" parameter list.  At the very least, the formatting should
        //be cleaned up, but I would prefer separation of the creation of the interior PNode and
        //then embedding it in the ControlPanelNode.
        afterLightLayer2.addChild( new ControlPanelNode( new PNode() {{
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
            final PNode additionalControl = additionalLaserControls.apply( model, 30.0, radioButtonPanel.getFullBounds().getMaxY() + 5 );
            addChild( additionalControl );
        }} ) {
            {
                setOffset( 5, 5 );
            }

            private PBounds layoutSize = null;//Store so only the original value is used

            //In the laser view box, putting the wavelength slider all the way to red makes the box size change--this override ensures the control panel wide enough to hold the laser wavelength control, but not resize as controls move around.
            @Override protected PBounds getControlPanelBounds( PNode content ) {
                if ( layoutSize == null ) {
                    final PBounds bounds = content.getFullBounds();
                    layoutSize = new PBounds( bounds.getX(), bounds.getY(), bounds.getWidth() + 18, bounds.getHeight() );
                }
                return layoutSize;
            }
        } );

        //Create a tool for dragging out the protractor
        final NodeFactory protractorNodeFactory = new NodeFactory() {
            public ProtractorNode createNode( ModelViewTransform transform, Property<Boolean> showTool, Point2D model ) {
                return newProtractorNode( transform, showTool, model );
            }
        };
        final ToolIconNode<T> protractor = new ToolIconNode<T>( multiScaleToWidth( RESOURCES.getImage( "protractor.png" ), ToolboxNode.ICON_WIDTH ), showProtractor,
                                                          transform, this, protractorNodeFactory, model, new Function0<Rectangle2D>() {
                    public Rectangle2D apply() {
                        return toolboxNode.getGlobalFullBounds();
                    }
                } ) {
            //Move the protractor behind the light node so that it also goes behind other controls (such as wavelength controls), since otherwise it obscures them from interaction
            @Override protected void addChild( BendingLightCanvas<T> canvas, ToolNode node ) {
                //REVIEW: Can commented-out code be removed?
//                canvas.addChildBehindLight( node );
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
                        model.getClock().setDt( getValue() );
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