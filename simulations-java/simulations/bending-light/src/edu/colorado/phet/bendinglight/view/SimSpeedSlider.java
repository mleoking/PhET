// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class should be used when adding a speed slider to the floating
 * clock control.  It provides the wrapper around a swing slider, hooks
 * up the clock, and sets the initial position.
 */
public class SimSpeedSlider extends PNode {

    /*
     * This assumes a default range of clock speeds based on the current (which is presumably the default) clock dt setting.
     *
     * @param maxPosX - The maximum x value within the floating clock control node, which may be the left edge of the rewind button (if present) or the left edge of the play button.
     */
    public SimSpeedSlider( double min, Property<Double> value, double max, final double maxPosX, ObservableProperty<Color> labelColors ) {
        addChild( new PSwing( new InnerSlider( min, max, "0", value, PhetCommonResources.getString( "Common.sim.speed" ), labelColors ) ) {{
            setOffset( maxPosX - getFullBoundsReference().width, 0 );
        }} );
    }

    /**
     * This is the Swing part of the GAOTimeSlider
     */
    public static class InnerSlider extends VerticalLayoutPanel {
        private final LinearValueControl linearSlider;

        public InnerSlider( final double min, final double max, String textFieldPattern, final Property<Double> valueProperty,
                            final String title, final ObservableProperty<Color> labelColor ) {
            linearSlider = new LinearValueControl( min, max, "", textFieldPattern, "" );
            linearSlider.setTextFieldVisible( false );

            final Function1<String, JLabel> labelMaker = new Function1<String, JLabel>() {
                public JLabel apply( String s ) {
                    return new JLabel( s ) {{
                        labelColor.addObserver( new SimpleObserver() {
                            public void update() {
                                setForeground( labelColor.getValue() );
                            }
                        } );
                    }};
                }
            };

            //Create the Hash table of labels (Double => JComponent)
            Hashtable<Object, Object> table = new Hashtable<Object, Object>() {{
                put( new Double( min ), labelMaker.apply( PhetCommonResources.getString( "Common.time.slow" ) ) );
                put( new Double( max ), labelMaker.apply( PhetCommonResources.getString( "Common.time.fast" ) ) );

                final JLabel value = labelMaker.apply( title );
                value.setFont( new PhetFont( Font.ITALIC, PhetFont.getDefaultFontSize() ) );
                put( new Double( ( max + min ) / 2 ), value );
            }};

            linearSlider.setTickLabels( table );
            valueProperty.addObserver( new SimpleObserver() {
                public void update() {
                    linearSlider.setValue( valueProperty.getValue() );
                }
            } );
            linearSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    valueProperty.setValue( linearSlider.getValue() );
                }
            } );
            add( linearSlider );
            SwingUtils.setBackgroundDeep( this, new Color( 0, 0, 0, 0 ) );
        }
    }
}