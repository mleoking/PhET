// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyskatepark.basics.EnergySkateParkBasicsModule.CONTROL_FONT;

/**
 * Panel that lets the user choose between on and off.
 *
 * @author Sam Reid
 */
class OnOffPanel extends PSwing {
    public OnOffPanel( final SettableProperty<Boolean> property ) {
        super( new JPanel() {{
            add( new PropertyRadioButton<Boolean>( "Off", property, false ) {{setFont( CONTROL_FONT );}} );
            add( new PropertyRadioButton<Boolean>( "On", property, true ) {{setFont( CONTROL_FONT );}} );
        }} );
    }
}