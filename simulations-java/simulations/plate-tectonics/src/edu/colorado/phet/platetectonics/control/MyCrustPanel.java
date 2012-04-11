// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJSlider;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.CrustModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.*;

/**
 * Displays three sliders (temperature, composition and thickness) to control a piece of crust
 */
public class MyCrustPanel extends PNode {
    private static final PhetFont sliderTitleFont = new PhetFont( 14 );
    private static final PhetFont limitFont = new PhetFont( 10 );

    public MyCrustPanel( CrustModel model ) {
        PText titleNode = new PText( MY_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }};

        SliderNode temperatureSlider = new SliderNode( UserComponents.temperatureSlider, TEMPERATURE, COOL, WARM, model.temperatureRatio, 0, 1 );
        SliderNode compositionSlider = new SliderNode( UserComponents.compositionSlider, COMPOSITION, MORE_IRON, MORE_SILICA, model.compositionRatio, 0, 1 );
        SliderNode thicknessSlider = new SliderNode( UserComponents.thicknessSlider, THICKNESS, THIN, THICK, model.thickness, 4000, 70000 );

        // center the title node (based on the slider itself, not the other labels!)
        titleNode.setOffset( ( temperatureSlider.getSlider().getWidth() - titleNode.getFullBounds().getWidth() ) / 2, 0 );

        // position sliders below
        temperatureSlider.setOffset( 0, titleNode.getFullBounds().getMaxY() + 10 );
        compositionSlider.setOffset( 0, temperatureSlider.getFullBounds().getMaxY() + 10 );
        thicknessSlider.setOffset( 0, compositionSlider.getFullBounds().getMaxY() + 10 );

        addChild( titleNode );
        addChild( temperatureSlider );
        addChild( compositionSlider );
        addChild( thicknessSlider );
    }

    private static class SliderNode extends PNode {
        private JSlider slider;
        private final Property<Double> property;
        private final double min;
        private final double max;

        private static final int sliderMax = 4096;

        private SliderNode( IUserComponent userComponent, String title, String lowText, String highText, final Property<Double> property, double min, double max ) {
            this.property = property;
            this.min = min;
            this.max = max;
            PText titleNode = new PText( title ) {{ setFont( sliderTitleFont ); }};
            PText lowNode = new PText( lowText ) {{ setFont( limitFont ); }};
            PText highNode = new PText( highText ) {{ setFont( limitFont ); }};
            slider = new SimSharingJSlider( userComponent, 0, sliderMax, getInitialValue() ) {{
                setPaintTicks( true );
            }};
            PSwing sliderNode = new PSwing( slider );

            // center the title node
            titleNode.setOffset( ( sliderNode.getFullBounds().getWidth() - titleNode.getFullBounds().getWidth() ) / 2,
                                 0 );

            // put the slider below the title
            sliderNode.setOffset( 0,
                                  titleNode.getFullBounds().getMaxY() );

            // put the low text centered under the left boundary
            lowNode.setOffset( -lowNode.getFullBounds().getWidth() / 2,
                               sliderNode.getFullBounds().getMaxY() - 5 );

            // put the high text centered under the right boundary
            highNode.setOffset( sliderNode.getFullBounds().getWidth() - highNode.getFullBounds().getWidth() / 2,
                                sliderNode.getFullBounds().getMaxY() - 5 );

            addChild( titleNode );
            addChild( lowNode );
            addChild( highNode );
            addChild( sliderNode );

            // listen to model changes
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    final double value = getValue();

                    // invoke the model change in the JME thread
                    LWJGLUtils.invoke( new Runnable() {
                        public void run() {
                            property.set( value );
                        }
                    } );
                }
            } );
        }

        private int getInitialValue() {
            double ratio = ( property.get() - min ) / ( max - min );
            return (int) ( ratio * sliderMax );
        }

        private double getValue() {
            double ratio = ( (double) slider.getValue() ) / ( (double) sliderMax );
            return min + ratio * ( max - min );
        }

        public JSlider getSlider() {
            return slider;
        }
    }
}
