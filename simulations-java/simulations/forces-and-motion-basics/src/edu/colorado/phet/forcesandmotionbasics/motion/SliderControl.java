package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.data.List;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Not;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
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

/**
 * @author Sam Reid
 */
public class SliderControl extends PNode {

    public SliderControl( final DoubleProperty appliedForce, final Property<List<StackableNode>> stack, final boolean friction ) {

        final Not enabled = Not.not( stack.valueEquals( List.<StackableNode>nil() ) );
        final String unitsString = friction ? "Newtons (N)" : "Newtons";
        VBox box = new VBox( 5, new EnablePhetPText( "Applied Force", CONTROL_FONT, enabled ),
                             new HSliderNode( null, -100, 100, DEFAULT_TRACK_THICKNESS, 200 * 1.75, appliedForce, enabled ) {{
                                 addLabel( 0, new EnablePhetPText( "0", CONTROL_FONT, enabled ) );
                                 addLabel( -50, new PhetPText( "a", CONTROL_FONT ) {{setTransparency( 0.0f );}} );
                                 addLabel( -100, new PhetPText( "a", CONTROL_FONT ) {{setTransparency( 0.0f );}} );
                                 addLabel( 50, new PhetPText( "a", CONTROL_FONT ) {{setTransparency( 0.0f );}} );
                                 addLabel( 100, new PhetPText( "100", CONTROL_FONT ) {{setTransparency( 0.0f );}} );

                                 setTrackFillPaint( Color.white );

                                 //When dropping the slider thumb, the value should go back to 0.  The user has to hold the thumb to keep applying the force
                                 addInputEventListener( new PBasicInputEventHandler() {
                                     @Override public void mouseReleased( final PInputEvent event ) {
                                         appliedForce.set( 0.0 );
                                     }
                                 } );
                             }},
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