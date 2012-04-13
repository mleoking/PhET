// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.watertower.view;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Radio button used on the faucet for manual/automatic flow.
 *
 * @author Sam Reid
 */
public class FaucetRadioButton extends PropertyRadioButton<Boolean> {
    public FaucetRadioButton( IUserComponent component, String name, SettableProperty<Boolean> selected ) {
        super( component, name, selected, true );

        //Background is transparent since these buttons should be placed directly on the pipe itself without any background
        setBackground( WaterTowerCanvas.TRANSPARENT );

        //Increase the font since this is scaled down to fit on the pipe, so that the font will look somewhat like fonts elsewhere in control panels in this sim
        setFont( new PhetFont( 17 ) );
    }
}
