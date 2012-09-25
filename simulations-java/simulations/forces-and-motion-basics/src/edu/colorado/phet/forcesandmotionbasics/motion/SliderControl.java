package edu.colorado.phet.forcesandmotionbasics.motion;

import fj.data.List;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JTextField;

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

    public SliderControl( final DoubleProperty appliedForce, final Property<List<StackableNode>> stack ) {

        final Not enabled = Not.not( stack.valueEquals( List.<StackableNode>nil() ) );
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
                             new HBox( new PhetPText( "Newtons", CONTROL_FONT ) {{setTransparency( 0.0f );}},
                                       new PSwing( new JTextField( 3 ) {{
                                           setFont( CONTROL_FONT );
                                           setText( "0" );
                                           setHorizontalAlignment( JTextField.RIGHT );
                                           appliedForce.addObserver( new VoidFunction1<Double>() {
                                               public void apply( final Double value ) {
                                                   setText( new DecimalFormat( "0" ).format( value ) );
                                               }
                                           } );
                                           enabled.addObserver( new VoidFunction1<Boolean>() {
                                               public void apply( final Boolean enabled ) {
                                                   setEnabled( enabled );
                                               }
                                           } );
                                       }} ),
                                       new EnablePhetPText( "Newtons", CONTROL_FONT, enabled )
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