package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Strings;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode.DEFAULT_TRACK_THICKNESS;
import static edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas.DEFAULT_FONT;

/**
 * Control shown in the control panel of the 3rb tab "friction" for changing the coefficient of friction.
 *
 * @author Sam Reid
 */
class FrictionSliderControl extends PNode {
    public static final double MAX = 0.5;

    public FrictionSliderControl( final SettableProperty<Double> friction ) {
        addChild( new VBox( 0, new PhetPText( Strings.FRICTION_TITLE, DEFAULT_FONT ),
                            new HSliderNode( UserComponents.frictionSliderKnob, 0, MAX, DEFAULT_TRACK_THICKNESS, 150, friction, new BooleanProperty( true ) ) {{
                                addLabel( 0, new PhetPText( Strings.NONE ) );
                                addLabel( MAX, new PhetPText( Strings.LOTS ) );
                            }} ) );
    }
}