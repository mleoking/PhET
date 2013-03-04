// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.data.List;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Strings;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.ParameterKeys;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode.DEFAULT_TRACK_THICKNESS;
import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.DEFAULT_FONT;
import static edu.colorado.phet.forcesandmotionbasics.motion.SpeedValue.LEFT_SPEED_EXCEEDED;
import static edu.colorado.phet.forcesandmotionbasics.motion.SpeedValue.RIGHT_SPEED_EXCEEDED;
import static edu.colorado.phet.forcesandmotionbasics.motion.SpeedValue.WITHIN_ALLOWED_RANGE;

/**
 * Set of controls (featuring a slider) used to view and set the applied force.  If the user releases the knob, the force should go back to zero.
 * If the user types in a value, the value should stay set until the maximum speed is exceeded.
 *
 * @author Sam Reid
 */
class AppliedForceSliderControl extends PNode {

    public static final int MAX_APPLIED_FORCE = 500;//Newtons.  Int because it is used in string values for the slider

    public AppliedForceSliderControl( final ObservableProperty<SpeedValue> speedValue, final DoubleProperty appliedForce,
                                      final Property<List<StackableNode>> stack, final boolean friction, final BooleanProperty playing, final MotionModel model ) {

        final Not enabled = Not.not( stack.valueEquals( List.<StackableNode>nil() ) );
        final String unitsString = friction ? Strings.NEWTONS__N : Strings.NEWTONS;

        final SettableProperty<Double> appliedForceSliderModel = new SettableProperty<Double>( appliedForce.get() ) {
            @Override public void set( final Double value ) {
                if ( speedValue.get() == WITHIN_ALLOWED_RANGE ) {
                    appliedForce.set( value );
                }
                else if ( speedValue.get() == RIGHT_SPEED_EXCEEDED ) {
                    appliedForce.set( MathUtil.clamp( -MAX_APPLIED_FORCE, value, 0 ) );
                }
                else { appliedForce.set( MathUtil.clamp( 0, value, MAX_APPLIED_FORCE ) ); }
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

        final HSliderNode sliderNode = new HSliderNode( UserComponents.appliedForceSliderKnob, -MAX_APPLIED_FORCE, MAX_APPLIED_FORCE, DEFAULT_TRACK_THICKNESS, 200 * 1.75, appliedForceSliderModel, enabled ) {{
            final int longTick = 15;
            final int shortTick = 10;
            final int longTickOffset = 8;
            final int shortTickOffset = 15;
            PhetPPath tick1 = addLabel( -MAX_APPLIED_FORCE, new EnablePhetPText( "-" + MAX_APPLIED_FORCE, DEFAULT_FONT, enabled ), longTick, longTickOffset );
            PhetPPath tick1_5 = addLabel( -MAX_APPLIED_FORCE * 0.75, dummyLabel(), shortTick, shortTickOffset );
            PhetPPath tick2 = addLabel( -MAX_APPLIED_FORCE * 0.5, dummyLabel(), longTick, longTickOffset );
            PhetPPath tick2_5 = addLabel( -MAX_APPLIED_FORCE * 0.25, dummyLabel(), shortTick, shortTickOffset );
            addLabel( 0, new EnablePhetPText( "0", DEFAULT_FONT, enabled ), longTick, longTickOffset );
            PhetPPath tick3_5 = addLabel( MAX_APPLIED_FORCE * 0.25, dummyLabel(), shortTick, shortTickOffset );
            PhetPPath tick4 = addLabel( MAX_APPLIED_FORCE * 0.5, dummyLabel(), longTick, longTickOffset );
            PhetPPath tick4_5 = addLabel( MAX_APPLIED_FORCE * 0.75, dummyLabel(), shortTick, shortTickOffset );
            PhetPPath tick5 = addLabel( MAX_APPLIED_FORCE, new EnablePhetPText( "" + MAX_APPLIED_FORCE, DEFAULT_FONT, enabled ), longTick, longTickOffset );

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

            //When playing: When dropping the slider thumb, the value should go back to 0.  The user has to hold the thumb to keep applying the force
            //When paused: The user can set a value and it will stay (just as they can always set a value with the text box)
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseReleased( final PInputEvent event ) {
                    if ( playing.get() ) {
                        appliedForce.set( 0.0 );
                        appliedForceSliderModel.set( 0.0 );
                    }
                }
            } );

            //Force slider should go to zero if the stack is emptied
            stack.addObserver( new VoidFunction1<List<StackableNode>>() {
                public void apply( final List<StackableNode> stackableNodes ) {
                    if ( stackableNodes.isEmpty() ) {
                        appliedForceSliderModel.set( 0.0 );
                        model.speed.set( new Some<Double>( 0.0 ) );
                        model.velocity.set( 0.0 );
                    }
                }
            } );
        }};
        VBox box = new VBox( -3, new EnablePhetPText( Strings.APPLIED_FORCE, DEFAULT_FONT, enabled ),
                             sliderNode,
                             new HBox( new PhetPText( unitsString, DEFAULT_FONT ) {{
                                 setTransparency( 0.0f );
                             }},
                                       new PSwing( new JTextField( 3 ) {
                                           {
                                               setFont( DEFAULT_FONT );
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
                                                   final double value = MathUtil.clamp( -MAX_APPLIED_FORCE, format.parse( getText() ).doubleValue(), MAX_APPLIED_FORCE );
                                                   SimSharingManager.sendUserMessage( UserComponents.appliedForceTextField, UserComponentTypes.textField, UserActions.textFieldCommitted, ParameterSet.parameterSet( ParameterKeys.appliedForce, value ) );
                                                   appliedForce.set( value );
                                                   appliedForceSliderModel.set( appliedForce.get() );
                                               }
                                               catch( ParseException e1 ) {
                                                   setText( format.format( appliedForce.get() ) );
                                               }
                                           }
                                       } ),
                                       new EnablePhetPText( unitsString, DEFAULT_FONT, enabled )
                             )
        );

        addChild( box );
    }

    private PhetPText dummyLabel() {
        return new PhetPText( "a", DEFAULT_FONT ) {{
            setTransparency( 0.0f );
        }};
    }

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