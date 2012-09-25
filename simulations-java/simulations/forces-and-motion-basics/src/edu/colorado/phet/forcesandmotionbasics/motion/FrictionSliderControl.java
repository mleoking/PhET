package edu.colorado.phet.forcesandmotionbasics.motion;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FrictionSliderControl extends PNode {
    public FrictionSliderControl( final SettableProperty<Double> friction ) {
        addChild( new VBox( 0, new PhetPText( "Friction", AbstractForcesAndMotionBasicsCanvas.CONTROL_FONT ), new HSliderNode( null, 0, 1.5, VSliderNode.DEFAULT_TRACK_THICKNESS, 150, friction, new BooleanProperty( true ) ) {{
            addLabel( 0, new PhetPText( "None" ) );
            addLabel( 1.5, new PhetPText( "Lots" ) );
        }} ) );
    }
}