// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.simsharing.SimSharingPropertyRadioButton;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentChain.chain;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents.offRadioButton;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents.onRadioButton;
import static edu.colorado.phet.energyskatepark.basics.EnergySkateParkBasicsModule.CONTROL_FONT;

/**
 * Panel that lets the user choose between on and off.
 *
 * @author Sam Reid
 */
public class OnOffPanel extends PSwing {
    public OnOffPanel( final IUserComponent userComponent, final SettableProperty<Boolean> property ) {
        super( new JPanel() {{
            add( new SimSharingPropertyRadioButton<Boolean>( chain( userComponent, offRadioButton ), EnergySkateParkResources.getString( "off" ), property, false ) {{setFont( CONTROL_FONT );}} );
            add( new SimSharingPropertyRadioButton<Boolean>( chain( userComponent, onRadioButton ), EnergySkateParkResources.getString( "on" ), property, true ) {{setFont( CONTROL_FONT );}} );
        }} );
    }
}