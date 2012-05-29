package edu.colorado.phet.fractionsintro.equalitylab.view;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class RadioButton extends PNode {

    public RadioButton( final IUserComponent component, final String text, final SettableProperty<Boolean> property, final boolean value ) {
        addChild( new PSwing( new PropertyRadioButton<Boolean>( component, text, property, value ) {{
            setOpaque( false );
            setFont( new PhetFont( 18, true ) );
        }} ) );

    }
}