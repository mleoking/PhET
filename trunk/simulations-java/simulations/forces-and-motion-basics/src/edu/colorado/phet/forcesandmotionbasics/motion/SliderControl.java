package edu.colorado.phet.forcesandmotionbasics.motion;

import java.awt.Color;

import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode.DEFAULT_TRACK_THICKNESS;
import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.CONTROL_FONT;

/**
 * @author Sam Reid
 */
public class SliderControl extends PNode {
    public SliderControl() {
        VBox box = new VBox( 5, new PhetPText( "Applied Force", CONTROL_FONT ),
                             new HSliderNode( null, -100, 100, DEFAULT_TRACK_THICKNESS, 200 * 1.75, new DoubleProperty( 0.0 ), new BooleanProperty( true ) ) {{
                                 addLabel( 0, new PhetPText( "0", CONTROL_FONT ) );
                                 addLabel( -50, new PhetPText( "a", CONTROL_FONT ) {{setTransparency( 0.0f );}} );
                                 addLabel( -100, new PhetPText( "a", CONTROL_FONT ) {{setTransparency( 0.0f );}} );
                                 addLabel( 50, new PhetPText( "a", CONTROL_FONT ) {{setTransparency( 0.0f );}} );
                                 addLabel( 100, new PhetPText( "100", CONTROL_FONT ) {{setTransparency( 0.0f );}} );

                                 setTrackFillPaint( Color.white );
//                                 addLabel( 50, new PNode() );
//                                 addLabel( 100, new PNode() );
                             }},
                             new HBox( new PhetPText( "Newtons", CONTROL_FONT ) {{setTransparency( 0.0f );}}, new PSwing( new JTextField( 4 ) {{
                                 setFont( CONTROL_FONT );
                                 setText( "0" );
                                 setHorizontalAlignment( JTextField.RIGHT );
                             }} ), new PhetPText( "Newtons", CONTROL_FONT ) ) );

        addChild( box );
    }
}