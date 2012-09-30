package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.data.List;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode.DEFAULT_TRACK_THICKNESS;
import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.CONTROL_FONT;
import static edu.colorado.phet.forcesandmotionbasics.motion.SpeedValue.*;

/**
 * @author Sam Reid
 */
public class SliderControl extends PNode {

    private final HSliderNode sliderNode;

    public SliderControl( final ObservableProperty<SpeedValue> speedValue, final DoubleProperty appliedForce, final Property<List<StackableNode>> stack, final boolean friction ) {

        final Not enabled = Not.not( stack.valueEquals( List.<StackableNode>nil() ) );
        final String unitsString = friction ? "Newtons (N)" : "Newtons";

        final SettableProperty<Double> appliedForceSliderModel = new SettableProperty<Double>( appliedForce.get() ) {
            @Override public void set( final Double value ) {
                if ( speedValue.get() == WITHIN_ALLOWED_RANGE ) { appliedForce.set( value ); }
                else if ( speedValue.get() == RIGHT_SPEED_EXCEEDED ) { appliedForce.set( MathUtil.clamp( -100, value, 0 ) ); }
                else { appliedForce.set( MathUtil.clamp( 0, value, 100 ) ); }
                notifyIfChanged();
            }

            @Override public Double get() { return appliedForce.get(); }

            {
                speedValue.addObserver( new VoidFunction1<SpeedValue>() {
                    public void apply( final SpeedValue speedValue ) {

                        //Pass the current value through the filter in case it now needs to be clamped.
                        set( get() );
                    }
                } );
            }
        };

        sliderNode = new HSliderNode( null, -100, 100, DEFAULT_TRACK_THICKNESS, 200 * 1.75, appliedForceSliderModel, enabled ) {{
            PhetPPath tick1 = addLabel( -100, dummyLabel() );
            PhetPPath tick2 = addLabel( -50, dummyLabel() );
            PhetPPath tick3 = addLabel( 0, new EnablePhetPText( "0", CONTROL_FONT, enabled ) );
            PhetPPath tick4 = addLabel( 50, dummyLabel() );
            PhetPPath tick5 = addLabel( 100, dummyLabel() );

            //Gray out the ticks if the speed is exceeded.
            speedValue.addObserver( grayIf( tick1, LEFT_SPEED_EXCEEDED ) );
            speedValue.addObserver( grayIf( tick2, LEFT_SPEED_EXCEEDED ) );
            speedValue.addObserver( grayIf( tick4, RIGHT_SPEED_EXCEEDED ) );
            speedValue.addObserver( grayIf( tick5, RIGHT_SPEED_EXCEEDED ) );

            setTrackFillPaint( Color.white );

            //Gray out the half of the track that is unavailable
            //Note the metrics are rotated since it is an HSliderNode wrapping a VSliderNode.
            getTrackNode().addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, getTrackNode().getFullBounds().getWidth(), getTrackNode().getFullBounds().getHeight() / 2 ), Color.lightGray ) {{
                speedValue.addObserver( new VoidFunction1<SpeedValue>() {
                    public void apply( final SpeedValue speedValue ) {
                        setVisible( speedValue == RIGHT_SPEED_EXCEEDED );
                    }
                } );
            }} );
            getTrackNode().addChild( new PhetPPath( new Rectangle2D.Double( 0, getTrackNode().getFullBounds().getHeight() / 2, getTrackNode().getFullBounds().getWidth(), getTrackNode().getFullBounds().getHeight() / 2 ), Color.lightGray ) {{
                speedValue.addObserver( new VoidFunction1<SpeedValue>() {
                    public void apply( final SpeedValue speedValue ) {
                        setVisible( speedValue == LEFT_SPEED_EXCEEDED );
                    }
                } );
            }} );

            //When dropping the slider thumb, the value should go back to 0.  The user has to hold the thumb to keep applying the force
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( final PInputEvent event ) {
                    appliedForce.set( 0.0 );
                    appliedForceSliderModel.set( 0.0 );
                }
            } );
        }};
        VBox box = new VBox( 5, new EnablePhetPText( "Applied Force", CONTROL_FONT, enabled ),
                             sliderNode,
                             new HBox( new PhetPText( unitsString, CONTROL_FONT ) {{setTransparency( 0.0f );}},
                                       new PSwing( new JTextField( 3 ) {
                                           {
                                               setFont( CONTROL_FONT );
                                               setText( "0" );
                                               final DecimalFormat format = new DecimalFormat( "0" );
                                               setHorizontalAlignment( JTextField.RIGHT );
                                               appliedForce.addObserver( new VoidFunction1<Double>() {
                                                   public void apply( final Double value ) {
                                                       setText( format.format( value ) );
                                                   }
                                               } );
                                               enabled.addObserver( new VoidFunction1<Boolean>() {
                                                   public void apply( final Boolean enabled ) {
                                                       setEnabled( enabled );
                                                   }
                                               } );
                                               addActionListener( new ActionListener() {
                                                   public void actionPerformed( final ActionEvent e ) {
                                                       updateValueFromText( format );
                                                   }
                                               } );
                                               addFocusListener( new FocusListener() {
                                                   public void focusGained( final FocusEvent e ) {
                                                   }

                                                   public void focusLost( final FocusEvent e ) {
                                                       updateValueFromText( format );
                                                   }
                                               } );
                                           }

                                           private void updateValueFromText( final DecimalFormat format ) {
                                               try {
                                                   appliedForce.set( MathUtil.clamp( -100, format.parse( getText() ).doubleValue(), 100 ) );
                                                   appliedForceSliderModel.set( appliedForce.get() );
                                               }
                                               catch ( ParseException e1 ) {
                                                   setText( format.format( appliedForce.get() ) );
                                               }
                                           }
                                       } ),
                                       new EnablePhetPText( unitsString, CONTROL_FONT, enabled )
                             )
        );

        addChild( box );
    }

    private PhetPText dummyLabel() {return new PhetPText( "a", CONTROL_FONT ) {{ setTransparency( 0.0f ); }};}

    private VoidFunction1<SpeedValue> grayIf( final PhetPPath path, final SpeedValue value ) {
        return new VoidFunction1<SpeedValue>() {
            public void apply( final SpeedValue speedValue ) {
                path.setStrokePaint( speedValue == value ? Color.gray : Color.black );
            }
        };
    }

    private class EnablePhetPText extends PhetPText {
        public EnablePhetPText( final String text, final Font font, ObservableProperty<Boolean> enabled ) {
            super( text, font );

            enabled.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean enabled ) {
                    setTextPaint( enabled ? Color.black : Color.gray );
                }
            } );
        }
    }
}